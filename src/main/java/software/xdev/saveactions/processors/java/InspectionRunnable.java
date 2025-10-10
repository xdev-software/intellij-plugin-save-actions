package software.xdev.saveactions.processors.java;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.intellij.codeInspection.GlobalInspectionContext;
import com.intellij.codeInspection.InspectionEngine;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionEP;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.QuickFix;
import com.intellij.codeInspection.ex.InspectionToolWrapper;
import com.intellij.codeInspection.ex.LocalInspectionToolWrapper;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import software.xdev.saveactions.core.service.SaveActionsService;


/**
 * Implements a runnable for inspections commands.
 */
class InspectionRunnable implements Runnable
{
	private static final Logger LOGGER = Logger.getInstance(SaveActionsService.class);
	
	private final Project project;
	private final Set<PsiFile> psiFiles;
	private final InspectionToolWrapper<LocalInspectionTool, LocalInspectionEP> toolWrapper;
	
	InspectionRunnable(final Project project, final Set<PsiFile> psiFiles, final LocalInspectionTool inspectionTool)
	{
		this.project = project;
		this.psiFiles = psiFiles;
		this.toolWrapper = new LocalInspectionToolWrapper(inspectionTool);
		LOGGER.info(String.format("Running inspection for %s - %s", inspectionTool.getShortName(), project.getName()));
	}
	
	@Override
	public void run()
	{
		final InspectionManager inspectionManager = InspectionManager.getInstance(this.project);
		final GlobalInspectionContext context = inspectionManager.createNewGlobalContext();
		this.psiFiles.forEach(pf -> this.getProblemDescriptors(context, pf).forEach(this::writeQuickFixes));
	}
	
	private List<ProblemDescriptor> getProblemDescriptors(
		final GlobalInspectionContext context,
		final PsiFile psiFile)
	{
		try
		{
			return InspectionEngine.runInspectionOnFile(psiFile, this.toolWrapper, context);
		}
		catch(final IndexNotReadyException exception)
		{
			LOGGER.info(String.format(
				"Cannot inspect file %s: index not ready (%s)",
				psiFile.getName(),
				exception.getMessage()));
			return Collections.emptyList();
		}
	}
	
	@SuppressWarnings({"unchecked", "squid:S1905", "squid:S3740"})
	private void writeQuickFixes(final ProblemDescriptor problemDescriptor)
	{
		final QuickFix<?>[] fixes = problemDescriptor.getFixes();
		if(fixes == null)
		{
			return;
		}
		
		final Set<QuickFix<ProblemDescriptor>> quickFixes = Arrays.stream(fixes)
			.filter(Objects::nonNull)
			.map(qf -> (QuickFix<ProblemDescriptor>)qf)
			.collect(Collectors.toSet());
		
		for(final QuickFix<ProblemDescriptor> typedFix : quickFixes)
		{
			try
			{
				LOGGER.info(String.format("Applying fix \"%s\"", typedFix.getName()));
				typedFix.applyFix(this.project, problemDescriptor);
			}
			catch(final Exception e)
			{
				LOGGER.error(e.getMessage(), e);
			}
		}
	}
}
