package software.xdev.saveactions.processors.java.inspection.style;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import com.intellij.psi.JavaResolveResult;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiImportStaticStatement;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiSwitchLabelStatement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.style.UnqualifiedStaticUsageInspection;


/**
 * Copy pasting because: cannot extend. Do not reformat (useful for diffs)
 *
 * @implNote Class needs to be inside a special package otherwise name resolution fails as seen in
 * {@link com.siyeh.ig.GroupDisplayNameUtil}
 * @see com.siyeh.ig.style.UnqualifiedStaticUsageInspection.UnqualifiedStaticCallVisitor
 */
public class CustomUnqualifiedStaticUsageInspection extends UnqualifiedStaticUsageInspection
{
	@Override
	public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getDisplayName()
	{
		return this.getClass().getSimpleName();
	}
	
	@Override
	public BaseInspectionVisitor buildVisitor()
	{
		return new CustomUnqualifiedStaticCallVisitor();
	}
	
	private final class CustomUnqualifiedStaticCallVisitor extends BaseInspectionVisitor
	{
		@Override
		public void visitMethodCallExpression(@NotNull final PsiMethodCallExpression expression)
		{
			super.visitMethodCallExpression(expression);
			if(CustomUnqualifiedStaticUsageInspection.this.m_ignoreStaticMethodCalls)
			{
				return;
			}
			final PsiReferenceExpression methodExpression = expression.getMethodExpression();
			if(!this.isUnqualifiedStaticAccess(methodExpression))
			{
				return;
			}
			this.registerError(methodExpression, expression);
		}
		
		@Override
		public void visitReferenceExpression(@NotNull final PsiReferenceExpression expression)
		{
			super.visitReferenceExpression(expression);
			if(CustomUnqualifiedStaticUsageInspection.this.m_ignoreStaticFieldAccesses)
			{
				return;
			}
			final PsiElement element = expression.resolve();
			if(!(element instanceof final PsiField field))
			{
				return;
			}
			if(field.hasModifierProperty(PsiModifier.FINAL) && PsiUtil.isOnAssignmentLeftHand(expression))
			{
				return;
			}
			if(!this.isUnqualifiedStaticAccess(expression))
			{
				return;
			}
			this.registerError(expression, expression);
		}
		
		@SuppressWarnings("PMD.NPathComplexity")
		private boolean isUnqualifiedStaticAccess(final PsiReferenceExpression expression)
		{
			if(CustomUnqualifiedStaticUsageInspection.this.m_ignoreStaticAccessFromStaticContext)
			{
				final PsiMember member = PsiTreeUtil.getParentOfType(expression, PsiMember.class);
				if(member != null && member.hasModifierProperty(PsiModifier.STATIC))
				{
					return false;
				}
			}
			final PsiExpression qualifierExpression = expression.getQualifierExpression();
			if(qualifierExpression != null)
			{
				return false;
			}
			final JavaResolveResult resolveResult = expression.advancedResolve(false);
			final PsiElement currentFileResolveScope = resolveResult.getCurrentFileResolveScope();
			if(currentFileResolveScope instanceof PsiImportStaticStatement)
			{
				return false;
			}
			final PsiElement element = resolveResult.getElement();
			if(!(element instanceof PsiField) && !(element instanceof PsiMethod))
			{
				return false;
			}
			final PsiMember member = (PsiMember)element;
			if(member instanceof PsiEnumConstant && expression.getParent() instanceof PsiSwitchLabelStatement)
			{
				return false;
			}
			final PsiClass expressionClass = PsiTreeUtil.getParentOfType(expression, PsiClass.class);
			final PsiClass memberClass = member.getContainingClass();
			if(memberClass != null && memberClass.equals(expressionClass))
			{
				return false;
			}
			return member.hasModifierProperty(PsiModifier.STATIC);
		}
	}
}
