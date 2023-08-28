package software.xdev.saveactions.junit;

import com.intellij.rt.execution.junit.FileComparisonFailure;
import org.opentest4j.AssertionFailedError;

import java.lang.reflect.InvocationTargetException;

public class JUnit5Utils {

    private JUnit5Utils() {
        // static
    }

    public static void rethrowAsJunit5Error(AssertionError error) {
        if (error.getCause() instanceof InvocationTargetException intellijInternal) {
            if (intellijInternal.getCause() instanceof FileComparisonFailure fileComparisonFailure) {
                String expected = fileComparisonFailure.getExpected();
                String actual = fileComparisonFailure.getActual();
                throw new AssertionFailedError("Expected file do not match actual file", expected, actual);
            }
        }
        throw error;
    }

    public static void rethrowAsJunit5Error(Runnable runnable) {
        try {
            runnable.run();
        } catch (AssertionError error) {
            rethrowAsJunit5Error(error);
        }
    }

}
