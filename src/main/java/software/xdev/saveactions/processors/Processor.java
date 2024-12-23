package software.xdev.saveactions.processors;

import java.util.Comparator;
import java.util.Set;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import software.xdev.saveactions.core.ExecutionMode;
import software.xdev.saveactions.model.Action;


/**
 * Processor interface contains the provider method, the action that enables the processors, and the modes for which the
 * processor is available.
 */
public interface Processor
{
	Action getAction();
	
	Set<ExecutionMode> getModes();
	
	int getOrder();
	
	SaveCommand getSaveCommand(Project project, Set<PsiFile> psiFiles);
	
	class OrderComparator implements Comparator<Processor>
	{
		
		@Override
		public int compare(final Processor o1, final Processor o2)
		{
			return Integer.compare(o1.getOrder(), o2.getOrder());
		}
	}
}
