package software.xdev.saveactions.junit;

import java.lang.reflect.InvocationTargetException;

import org.opentest4j.AssertionFailedError;

import com.intellij.rt.execution.junit.FileComparisonFailure;


public final class JUnit5Utils
{
	private JUnit5Utils()
	{
	}
	
	public static void rethrowAsJunit5Error(final AssertionError error)
	{
		if(error.getCause() instanceof final InvocationTargetException intellijInternal
			&& intellijInternal.getCause() instanceof final FileComparisonFailure fileComparisonFailure)
		{
			final String expected = fileComparisonFailure.getExpected();
			final String actual = fileComparisonFailure.getActual();
			throw new AssertionFailedError("Expected file do not match actual file", expected, actual);
		}
		
		throw error;
	}
	
	public static void rethrowAsJunit5Error(final Runnable runnable)
	{
		try
		{
			runnable.run();
		}
		catch(final AssertionError error)
		{
			rethrowAsJunit5Error(error);
		}
	}
}
