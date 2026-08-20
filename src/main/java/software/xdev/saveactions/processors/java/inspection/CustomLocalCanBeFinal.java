package software.xdev.saveactions.processors.java.inspection;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.localCanBeFinal.LocalCanBeFinal;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiTypeElement;
import com.intellij.util.containers.CollectionFactory;

import software.xdev.saveactions.util.collections.MaxSizedLinkedHashMap;


@SuppressWarnings("InspectionDescriptionNotFoundInspection")
public class CustomLocalCanBeFinal extends LocalCanBeFinal
{
	@Override
	public ProblemDescriptor[] checkMethod(
		@NotNull final PsiMethod method,
		@NotNull final InspectionManager manager,
		final boolean isOnTheFly)
	{
		// Use a method based cache here to massively improve performance on repeated calls.
		// The underlying problem here is that ControlFlowFactory clears it's cache every time the file is changed.
		// This causes a very expensive recomputation (A file with 800 LoC and 40 methods needs ~400ms).
		// See #338 for more details
		return method.getProject().getService(MethodCache.class).computeIfAbsent(
			method,
			_ -> this.checkProblemDescriptors(super.checkMethod(method, manager, isOnTheFly)));
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
		return Optional.ofNullable(descriptors)
			.stream()
			.flatMap(Arrays::stream)
			.filter(descriptor -> this.isNotLombokVal(descriptor.getPsiElement()))
			.toArray(ProblemDescriptor[]::new);
	}
	
	private boolean isNotLombokVal(final PsiElement element)
	{
		return Arrays
			.stream(element.getParent().getChildren())
			.noneMatch(child -> child instanceof PsiTypeElement && "val".equals(child.getText()));
	}
	
	static class MethodCache
	{
		private final Map<PsiMethod,
			Map<Integer, // Signature / Parameter hash
				Map<Integer, // Body hash (store as hash instead of string to save memory)
					ProblemDescriptor[]>>> cache = CollectionFactory.createConcurrentWeakKeySoftValueMap();
		
		ProblemDescriptor[] computeIfAbsent(
			final PsiMethod method,
			final Function<PsiMethod, ProblemDescriptor[]> createIfAbsent)
		{
			final Map<Integer, Map<Integer, ProblemDescriptor[]>> methodSignatureCache =
				computeNewMaxSizeMapIfAbsent(this.cache, method);
			
			final Map<Integer, ProblemDescriptor[]> bodyCache = computeNewMaxSizeMapIfAbsent(
				methodSignatureCache,
				method.getSignature(PsiSubstitutor.EMPTY).hashCode());
			
			final PsiCodeBlock body = method.getBody();
			final int bodyHash = body != null ? body.getText().hashCode() : -1;
			
			// Validate that the cached ProblemDescriptors still reference valid PSI elements.
			// When a file is modified externally (e.g. by an AI assistant writing to disk),
			// the PSI tree is rebuilt and previously cached descriptors may hold stale references.
			// Applying fixes with stale PSI elements can corrupt the document or prevent the
			// editor from reloading the file. See #392
			final ProblemDescriptor[] cachedResult = bodyCache.get(bodyHash);
			if(cachedResult != null && areDescriptorsValid(cachedResult))
			{
				return cachedResult;
			}
			
			final ProblemDescriptor[] newResult = createIfAbsent.apply(method);
			bodyCache.put(bodyHash, newResult);
			return newResult;
		}
		
		private static <K, X, Y> Map<X, Y> computeNewMaxSizeMapIfAbsent(final Map<K, Map<X, Y>> target, final K key)
		{
			return target.computeIfAbsent(
				key,
				ignored -> Collections.synchronizedMap(new MaxSizedLinkedHashMap<>(2)));
		}
		
		private static boolean areDescriptorsValid(final ProblemDescriptor[] descriptors)
		{
			for(final ProblemDescriptor descriptor : descriptors)
			{
				final PsiElement element = descriptor.getPsiElement();
				if(element == null || !element.isValid())
				{
					return false;
				}
			}
			return true;
		}
	}
}
