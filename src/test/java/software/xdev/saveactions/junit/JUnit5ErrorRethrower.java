package software.xdev.saveactions.junit;

import java.lang.reflect.InvocationTargetException;

import org.opentest4j.AssertionFailedError;

import com.intellij.platform.testFramework.core.FileComparisonFailedError;


public final class JUnit5ErrorRethrower
{
	private JUnit5ErrorRethrower()
	{
	}
	
	public static void rethrow(final AssertionError error)
	{
		if(error.getCause() instanceof final InvocationTargetException intellijInternal
			&& intellijInternal.getCause() instanceof final FileComparisonFailedError fileComparisonFailure)
		{
			final String expected = fileComparisonFailure.getExpected().getStringRepresentation();
			final String actual = fileComparisonFailure.getActual().getStringRepresentation();
			throw new AssertionFailedError("Expected file do not match actual file", expected, actual);
		}
		
		throw error;
	}
	
	public static void rethrow(final Runnable runnable)
	{
		try
		{
			runnable.run();
		}
		catch(final AssertionError error)
		{
			rethrow(error);
		}
	}
}
