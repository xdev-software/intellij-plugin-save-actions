package software.xdev.saveactions.core.service.impl;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static software.xdev.saveactions.model.StorageFactory.JAVA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.ex.QuickList;
import com.intellij.openapi.actionSystem.ex.QuickListsManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import software.xdev.saveactions.core.ExecutionMode;
import software.xdev.saveactions.core.component.Engine;
import software.xdev.saveactions.core.service.SaveActionsService;
import software.xdev.saveactions.model.Action;
import software.xdev.saveactions.model.Storage;
import software.xdev.saveactions.model.StorageFactory;
import software.xdev.saveactions.processors.Processor;


/**
 * Super class for all ApplicationServices. All actions are routed here. ApplicationServices are Singleton
 * implementations by default.
 * <p>
 * The main method is {@link #guardedProcessPsiFiles(Project, Set, Action, ExecutionMode)} and will delegate to
 * {@link Engine#processPsiFilesIfNecessary(ProgressIndicator, boolean)} ()}.
 * The method will check if the file needs to be processed and uses the processors to apply the modifications.
 * <p>
 * The psi files are ide wide, that means they are shared between projects (and editor windows), so we need to check if
 * the file is physically in that project before reformatting, or else the file is formatted twice and intellij will ask
 * to confirm unlocking of non-project file in the other project, see {@link Engine} for more details.
 *
 * @since 2.4.0
 */
abstract class AbstractSaveActionsService implements SaveActionsService
{
	protected static final Logger LOGGER = Logger.getInstance(SaveActionsService.class);
	
	private final List<Processor> processors;
	private final StorageFactory storageFactory;
	private final boolean javaAvailable;
	private final boolean compilingAvailable;
	
	private final Map<Project, ReentrantLock> guardedProcessPsiFilesLocks =
		Collections.synchronizedMap(new WeakHashMap<>());
	
	protected AbstractSaveActionsService(final StorageFactory storageFactory)
	{
		LOGGER.info("Save Actions Service \"" + this.getClass().getSimpleName() + "\" initialized.");
		this.storageFactory = storageFactory;
		this.processors = new ArrayList<>();
		this.javaAvailable = JAVA.equals(storageFactory);
		this.compilingAvailable = this.initCompilingAvailable();
	}
	
	@Override
	public void guardedProcessPsiFiles(
		final Project project,
		final Set<PsiFile> psiFiles,
		final Action activation,
		final ExecutionMode mode)
	{
		if(ApplicationManager.getApplication().isDisposed())
		{
			LOGGER.info("Application is closing, stopping invocation");
			return;
		}
		
		final Storage storage = this.storageFactory.getStorage(project);
		final Engine engine = new Engine(
			storage,
			this.processors,
			project,
			psiFiles,
			activation,
			mode);
		
		final boolean applyAsync = storage.getActions().contains(Action.processAsync);
		if(applyAsync)
		{
			new Task.Backgroundable(project, "Applying Save Actions", true)
			{
				@Override
				public void run(@NotNull final ProgressIndicator indicator)
				{
					AbstractSaveActionsService.this.processPsiFilesIfNecessaryWithLock(project, engine, indicator);
				}
			}.queue();
			return;
		}
		
		this.processPsiFilesIfNecessaryWithLock(project, engine, null);
	}
	
	private void processPsiFilesIfNecessaryWithLock(
		final Project project,
		final Engine engine,
		final ProgressIndicator indicator)
	{
		if(LOGGER.isTraceEnabled())
		{
			LOGGER.trace("Getting lock - " + project.getName());
		}
		final ReentrantLock lock =
			this.guardedProcessPsiFilesLocks.computeIfAbsent(project, ignored -> new ReentrantLock());
		lock.lock();
		if(LOGGER.isTraceEnabled())
		{
			LOGGER.trace("Got lock - " + project.getName());
		}
		try
		{
			engine.processPsiFilesIfNecessary(
				indicator != null ? indicator : new EmptyProgressIndicator(),
				indicator != null);
		}
		finally
		{
			lock.unlock();
			if(LOGGER.isTraceEnabled())
			{
				LOGGER.trace("Released lock - " + project.getName());
			}
		}
	}
	
	@Override
	public boolean isJavaAvailable()
	{
		return this.javaAvailable;
	}
	
	@Override
	public boolean isCompilingAvailable()
	{
		return this.compilingAvailable;
	}
	
	@Override
	public List<QuickList> getQuickLists(final Project project)
	{
		final Map<Integer, QuickList> quickListsIds =
			Arrays.stream(QuickListsManager.getInstance().getAllQuickLists())
				.collect(toMap(QuickList::hashCode, identity()));
		
		return Optional.ofNullable(this.storageFactory.getStorage(project))
			.map(storage -> storage.getQuickLists().stream()
				.map(Integer::valueOf)
				.map(quickListsIds::get)
				.filter(Objects::nonNull)
				.collect(toList()))
			.orElse(new ArrayList<>());
	}
	
	protected SaveActionsService addProcessors(final Stream<Processor> processors)
	{
		processors.forEach(this.processors::add);
		this.processors.sort(new Processor.OrderComparator());
		return this;
	}
	
	private boolean initCompilingAvailable()
	{
		try
		{
			Class.forName("com.intellij.openapi.compiler.CompilerManager");
			return true;
		}
		catch(final Exception e)
		{
			return false;
		}
	}
}
