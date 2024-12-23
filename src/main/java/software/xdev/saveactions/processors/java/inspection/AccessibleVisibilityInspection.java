package software.xdev.saveactions.processors.java.inspection;

import java.lang.reflect.Method;

import org.jetbrains.annotations.NotNull;

import com.intellij.codeInspection.visibility.VisibilityInspection;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiMember;
import com.intellij.psi.SyntaxTraverser;


/**
 * Fork of {@link com.intellij.codeInspection.visibility.VisibilityInspection} but accessible for the plugin.
 */
public final class AccessibleVisibilityInspection
{
	private static final Logger LOG = Logger.getInstance(AccessibleVisibilityInspection.class);
	
	private AccessibleVisibilityInspection()
	{
	}
	
	public static boolean containsReferenceTo(final PsiElement source, final PsiElement target)
	{
		return SyntaxTraverser.psiTraverser(source)
			.filter(PsiJavaCodeReferenceElement.class)
			.filter(ref -> ref.isReferenceTo(target))
			.isNotEmpty();
	}
	
	// reflection is needed because VisibilityInspection members are private
	@SuppressWarnings({"java:S3011"})
	public static int getMinVisibilityLevel(
		final VisibilityInspection myVisibilityInspection,
		@NotNull final PsiMember member)
	{
		try
		{
			final Method getMinVisibilityLevel = myVisibilityInspection.getClass()
				.getDeclaredMethod("getMinVisibilityLevel", PsiMember.class);
			
			getMinVisibilityLevel.setAccessible(true);
			
			return (int)getMinVisibilityLevel.invoke(myVisibilityInspection, member);
		}
		catch(final Exception e)
		{
			LOG.error("Failed to invoke getMinVisibilityLevel", e);
			throw new IllegalStateException(e);
		}
	}
}
