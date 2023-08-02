package software.xdev.saveactions.processors.java.inspection;

import com.intellij.codeInspection.ex.EntryPointsManagerBase;
import com.intellij.codeInspection.visibility.EntryPointWithVisibilityLevel;
import com.intellij.codeInspection.visibility.VisibilityInspection;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiMember;
import com.intellij.psi.SyntaxTraverser;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.NotNull;
import software.xdev.saveactions.model.Action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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

    @SuppressWarnings("java:S3011") // reflection is needed because VisibilityInspection members are private
    public static int getMinVisibilityLevel(VisibilityInspection myVisibilityInspection, @NotNull PsiMember member) {
        List<Exception> exceptions = new ArrayList<>();
        // 1. Try to access getMinVisibilityLevel directly
        try {
            Method getMinVisibilityLevel = myVisibilityInspection.getClass()
                    .getDeclaredMethod("getMinVisibilityLevel", PsiMember.class);

            getMinVisibilityLevel.setAccessible(true);

            return (int) getMinVisibilityLevel.invoke(myVisibilityInspection, member);
        } catch (Exception e) {
            LOG.error("Failed to invoke getMinVisibilityLevel", e);
            exceptions.add(e);
        }

        // 2. Fallback to isEntryPointEnabled
        LOG.warn("getMinVisibilityLevel: Trying fallback method: isEntryPointEnabled");
        try {
            Method isEntryPointEnabled = myVisibilityInspection.getClass()
                    .getDeclaredMethod("isEntryPointEnabled", EntryPointWithVisibilityLevel.class);
            isEntryPointEnabled.setAccessible(true);

            return StreamEx.of(EntryPointsManagerBase.DEAD_CODE_EP_NAME.getExtensions())
                    .select(EntryPointWithVisibilityLevel.class)
                    .filter(point -> {
                        try {
                            return (boolean) isEntryPointEnabled.invoke(myVisibilityInspection, point);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new IllegalStateException("Reflection call failed", e);
                        }
                    })
                    .mapToInt(point -> point.getMinVisibilityLevel(member))
                    .max().orElse(-1);
        } catch (Exception e) {
            LOG.error("Failed to invoke isEntryPointEnabled", e);
            exceptions.add(e);
        }

        LOG.error("Execution of getMinVisibilityLevel with reflection failed; "
                + "Please report the problem so that I can be fixed. In the meantime consider disabling '"
                + Action.accessCanBeTightened.getText()
                + "' or downgrade your IDE");
        throw new IllegalStateException(exceptions.get(0));
    }
}
