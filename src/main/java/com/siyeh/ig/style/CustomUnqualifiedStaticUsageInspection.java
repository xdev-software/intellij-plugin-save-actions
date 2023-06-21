package com.siyeh.ig.style;

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
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Copy pasting because: cannot extend. Do not reformat (useful for diffs)
 *
 * @see com.siyeh.ig.style.UnqualifiedStaticUsageInspection.UnqualifiedStaticCallVisitor
 */
public class CustomUnqualifiedStaticUsageInspection extends UnqualifiedStaticUsageInspection {

  @Override
  public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getDisplayName() {

    return this.getClass().getSimpleName();
  }


  @Override
  public BaseInspectionVisitor buildVisitor() {

    return new CustomUnqualifiedStaticCallVisitor();
  }


  private class CustomUnqualifiedStaticCallVisitor extends BaseInspectionVisitor {

    @Override
    public void visitMethodCallExpression(
        @NotNull final PsiMethodCallExpression expression) {

      super.visitMethodCallExpression(expression);
      if (m_ignoreStaticMethodCalls) {
        return;
      }
      final PsiReferenceExpression methodExpression =
          expression.getMethodExpression();
      if (!isUnqualifiedStaticAccess(methodExpression)) {
        return;
      }
      registerError(methodExpression, expression);
    }


    @Override
    public void visitReferenceExpression(
        @NotNull final PsiReferenceExpression expression) {

      super.visitReferenceExpression(expression);
      if (m_ignoreStaticFieldAccesses) {
        return;
      }
      final PsiElement element = expression.resolve();
      if (!(element instanceof PsiField)) {
        return;
      }
      final PsiField field = (PsiField) element;
      if (field.hasModifierProperty(PsiModifier.FINAL) &&
          PsiUtil.isOnAssignmentLeftHand(expression)) {
        return;
      }
      if (!isUnqualifiedStaticAccess(expression)) {
        return;
      }
      registerError(expression, expression);
    }


    private boolean isUnqualifiedStaticAccess(
        final PsiReferenceExpression expression) {

      if (m_ignoreStaticAccessFromStaticContext) {
        final PsiMember member =
            PsiTreeUtil.getParentOfType(expression,
                PsiMember.class);
        if (member != null &&
            member.hasModifierProperty(PsiModifier.STATIC)) {
          return false;
        }
      }
      final PsiExpression qualifierExpression =
          expression.getQualifierExpression();
      if (qualifierExpression != null) {
        return false;
      }
      final JavaResolveResult resolveResult =
          expression.advancedResolve(false);
      final PsiElement currentFileResolveScope =
          resolveResult.getCurrentFileResolveScope();
      if (currentFileResolveScope instanceof PsiImportStaticStatement) {
        return false;
      }
      final PsiElement element = resolveResult.getElement();
      if (!(element instanceof PsiField) &&
          !(element instanceof PsiMethod)) {
        return false;
      }
      final PsiMember member = (PsiMember) element;
      if (member instanceof PsiEnumConstant &&
          expression.getParent() instanceof PsiSwitchLabelStatement) {
        return false;
      }
      final PsiClass expressionClass = PsiTreeUtil.getParentOfType(expression, PsiClass.class);
      final PsiClass memberClass = member.getContainingClass();
      if (memberClass != null && memberClass.equals(expressionClass)) {
        return false;
      }
      return member.hasModifierProperty(PsiModifier.STATIC);
    }
  }

}
