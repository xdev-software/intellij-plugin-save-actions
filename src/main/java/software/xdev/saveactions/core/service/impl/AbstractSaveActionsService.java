package software.xdev.saveactions.core.service.impl;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static software.xdev.saveactions.model.StorageFactory.JAVA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.intellij.openapi.actionSystem.ex.QuickList;
import com.intellij.openapi.actionSystem.ex.QuickListsManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import software.xdev.saveactions.core.ExecutionMode;
import software.xdev.saveactions.core.component.Engine;
import software.xdev.saveactions.core.service.SaveActionsService;
import software.xdev.saveactions.model.Action;
import software.xdev.saveactions.model.StorageFactory;
import software.xdev.saveactions.processors.Processor;


/**
 * Super class for all ApplicationServices. All actions are routed here. ApplicationServices are Singleton
 * implementations by default.
 * <p>
 * The main method is {@link #guardedProcessPsiFiles(Project, Set, Action, ExecutionMode)} and will delegate to
 * {@link Engine#processPsiFilesIfNecessary()}. The method will check if the file needs to be processed and uses the
 * processors to apply the modifications.
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
	
	protected AbstractSaveActionsService(final StorageFactory storageFactory)
	{
		LOGGER.info("Save Actions Service \"" + this.getClass().getSimpleName() + "\" initialized.");
		this.storageFactory = storageFactory;
		this.processors = new ArrayList<>();
		this.javaAvailable = JAVA.equals(storageFactory);
		this.compilingAvailable = this.initCompilingAvailable();
	}
	
	@Override
	public synchronized void guardedProcessPsiFiles(
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
		final Engine engine = new Engine(
			this.storageFactory.getStorage(project), this.processors, project, psiFiles, activation,
			mode);
		engine.processPsiFilesIfNecessary();
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
