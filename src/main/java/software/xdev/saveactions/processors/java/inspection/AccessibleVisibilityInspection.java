package software.xdev.saveactions.processors.java.inspection;

import com.intellij.codeInspection.visibility.VisibilityInspection;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiMember;
import com.intellij.psi.SyntaxTraverser;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/**
 * Fork of {@link com.intellij.codeInspection.visibility.VisibilityInspection} but accessible for the plugin.
 */
public final class AccessibleVisibilityInspection {

    private static final Logger LOG = Logger.getInstance(AccessibleVisibilityInspection.class);

    private AccessibleVisibilityInspection() {
    }

    public static boolean containsReferenceTo(PsiElement source, PsiElement target) {
        return SyntaxTraverser.psiTraverser(source)
                .filter(PsiJavaCodeReferenceElement.class)
                .filter(ref -> ref.isReferenceTo(target))
                .isNotEmpty();
    }

    // reflection is needed because VisibilityInspection members are private
    @SuppressWarnings({"java:S3011"})
    public static int getMinVisibilityLevel(VisibilityInspection myVisibilityInspection, @NotNull PsiMember member) {
        try {
            Method getMinVisibilityLevel = myVisibilityInspection.getClass()
                    .getDeclaredMethod("getMinVisibilityLevel", PsiMember.class);

            getMinVisibilityLevel.setAccessible(true);

            return (int) getMinVisibilityLevel.invoke(myVisibilityInspection, member);
        } catch (Exception e) {
            LOG.error("Failed to invoke getMinVisibilityLevel", e);
            throw new IllegalStateException(e);
        }
    }
}
