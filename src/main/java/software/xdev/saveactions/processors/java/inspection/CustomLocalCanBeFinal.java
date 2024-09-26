package software.xdev.saveactions.processors.java.inspection;

import java.util.Arrays;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.localCanBeFinal.LocalCanBeFinal;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypeElement;


@SuppressWarnings("InspectionDescriptionNotFoundInspection")
public class CustomLocalCanBeFinal extends LocalCanBeFinal
{
	@Override
	public ProblemDescriptor[] checkMethod(
		@NotNull final PsiMethod method,
		@NotNull final InspectionManager manager,
		final boolean isOnTheFly)
	{
		return this.checkProblemDescriptors(super.checkMethod(method, manager, isOnTheFly));
	}
	
	@Override
	public ProblemDescriptor[] checkClass(
		@NotNull final PsiClass aClass,
		@NotNull final InspectionManager manager,
		final boolean isOnTheFly)
	{
		return this.checkProblemDescriptors(super.checkClass(aClass, manager, isOnTheFly));
	}
	
	private ProblemDescriptor[] checkProblemDescriptors(@Nullable final ProblemDescriptor[] descriptors)
	{
		return Arrays
			.stream(Optional.ofNullable(descriptors).orElse(new ProblemDescriptor[0]))
			.filter(descriptor -> this.isNotLombokVal(descriptor.getPsiElement()))
			.toArray(ProblemDescriptor[]::new);
	}
	
	private boolean isNotLombokVal(final PsiElement element)
	{
		return Arrays
			.stream(element.getParent().getChildren())
			.noneMatch(child -> child instanceof PsiTypeElement && child.getText().equals("val"));
	}
}
