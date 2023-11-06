package software.xdev.saveactions.processors.java.inspection.style;

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
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Copy pasting because: cannot extend. Do not reformat (useful for diffs)
 *
 * @implNote Class needs to be inside a special package otherwise name resolution fails as seen in
 * {@link com.siyeh.ig.GroupDisplayNameUtil}
 * @see com.siyeh.ig.style.UnqualifiedStaticUsageInspection.UnqualifiedStaticCallVisitor
 */
public class CustomUnqualifiedStaticUsageInspection extends UnqualifiedStaticUsageInspection {

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getDisplayName() {
        return getClass().getSimpleName();
    }

    @Override
    public BaseInspectionVisitor buildVisitor() {
        return new CustomUnqualifiedStaticCallVisitor();
    }

    private class CustomUnqualifiedStaticCallVisitor extends BaseInspectionVisitor {
        @Override
        public void visitMethodCallExpression(@NotNull PsiMethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            if (m_ignoreStaticMethodCalls) {
                return;
            }
            PsiReferenceExpression methodExpression = expression.getMethodExpression();
            if (!isUnqualifiedStaticAccess(methodExpression)) {
                return;
            }
            registerError(methodExpression, expression);
        }

        @Override
        public void visitReferenceExpression(@NotNull PsiReferenceExpression expression) {
            super.visitReferenceExpression(expression);
            if (m_ignoreStaticFieldAccesses) {
                return;
            }
            PsiElement element = expression.resolve();
            if (!(element instanceof PsiField)) {
                return;
            }
            PsiField field = (PsiField) element;
            if (field.hasModifierProperty(PsiModifier.FINAL) && PsiUtil.isOnAssignmentLeftHand(expression)) {
                return;
            }
            if (!isUnqualifiedStaticAccess(expression)) {
                return;
            }
            registerError(expression, expression);
        }

        private boolean isUnqualifiedStaticAccess(PsiReferenceExpression expression) {
            if (m_ignoreStaticAccessFromStaticContext) {
                PsiMember member = PsiTreeUtil.getParentOfType(expression, PsiMember.class);
                if (member != null && member.hasModifierProperty(PsiModifier.STATIC)) {
                    return false;
                }
            }
            PsiExpression qualifierExpression = expression.getQualifierExpression();
            if (qualifierExpression != null) {
                return false;
            }
            JavaResolveResult resolveResult = expression.advancedResolve(false);
            PsiElement currentFileResolveScope = resolveResult.getCurrentFileResolveScope();
            if (currentFileResolveScope instanceof PsiImportStaticStatement) {
                return false;
            }
            PsiElement element = resolveResult.getElement();
            if (!(element instanceof PsiField) && !(element instanceof PsiMethod)) {
                return false;
            }
            PsiMember member = (PsiMember) element;
            if (member instanceof PsiEnumConstant && expression.getParent() instanceof PsiSwitchLabelStatement) {
                return false;
            }
            PsiClass expressionClass = PsiTreeUtil.getParentOfType(expression, PsiClass.class);
            PsiClass memberClass = member.getContainingClass();
            if (memberClass != null && memberClass.equals(expressionClass)) {
                return false;
            }
            return member.hasModifierProperty(PsiModifier.STATIC);
        }
    }

}
