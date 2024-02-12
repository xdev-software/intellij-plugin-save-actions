package software.xdev.saveactions.core.listener;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

import software.xdev.saveactions.core.ExecutionMode;
import software.xdev.saveactions.core.service.SaveActionsService;
import software.xdev.saveactions.core.service.SaveActionsServiceManager;
import software.xdev.saveactions.model.Action;


/**
 * FileDocumentManagerListener to catch save events. This listener is registered as ExtensionPoint.
 */
public final class SaveActionsDocumentManagerListener implements FileDocumentManagerListener
{
	private static final Logger LOGGER = Logger.getInstance(SaveActionsService.class);
	
	private final Project project;
	private PsiDocumentManager psiDocumentManager;
	
	public SaveActionsDocumentManagerListener(final Project project)
	{
		this.project = project;
	}
	
	@Override
	public void beforeAllDocumentsSaving()
	{
		LOGGER.debug(
			"[+] Start SaveActionsDocumentManagerListener#beforeAllDocumentsSaving, " + this.project.getName());
		final List<Document> unsavedDocuments = Arrays.asList(FileDocumentManager.getInstance().getUnsavedDocuments());
		if(!unsavedDocuments.isEmpty())
		{
			LOGGER.debug(String.format(
				"Locating psi files for %d documents: %s",
				unsavedDocuments.size(),
				unsavedDocuments));
			this.beforeDocumentsSaving(unsavedDocuments);
		}
		LOGGER.debug("End SaveActionsDocumentManagerListener#beforeAllDocumentsSaving");
	}
	
	public void beforeDocumentsSaving(final List<Document> documents)
	{
		if(this.project.isDisposed())
		{
			return;
		}
		this.initPsiDocManager();
		final Set<PsiFile> psiFiles = documents.stream()
			.map(this.psiDocumentManager::getPsiFile)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
		SaveActionsServiceManager.getService()
			.guardedProcessPsiFiles(this.project, psiFiles, Action.activate, ExecutionMode.saveAll);
	}
	
	private synchronized void initPsiDocManager()
	{
		if(this.psiDocumentManager == null)
		{
			this.psiDocumentManager = PsiDocumentManager.getInstance(this.project);
		}
	}
}
