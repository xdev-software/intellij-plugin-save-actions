package software.xdev.saveactions.utils;

import java.util.Arrays;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;


public final class Helper
{
	private Helper()
	{
	}
	
	public static VirtualFile[] toVirtualFiles(final PsiFile[] psiFiles)
	{
		return Arrays.stream(psiFiles).map(PsiFile::getVirtualFile).toArray(VirtualFile[]::new);
	}
}
