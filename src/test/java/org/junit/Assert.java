package org.junit;

import org.junit.jupiter.api.Assertions;


/**
 * @see <a href="https://github.com/junit-team/junit4/blob/main/src/main/java/junit/framework/Assert.java">Source</a>
 * @deprecated Compat for com.intellij.testFramework.fixtures.impl.BaseFixture
 */
@SuppressWarnings("all")
@Deprecated
public class Assert
{
	protected Assert()
	{
	}
	
	public static void assertTrue(final String message, final boolean condition)
	{
		Assertions.assertTrue(condition, message);
	}
	
	public static void assertTrue(final boolean condition)
	{
		Assertions.assertTrue(condition);
	}
	
	public static void assertFalse(final String message, final boolean condition)
	{
		Assertions.assertFalse(condition, message);
	}
	
	public static void assertFalse(final boolean condition)
	{
		Assertions.assertFalse(condition);
	}
	
	public static void fail(final String message)
	{
		Assertions.fail(message);
	}
	
	public static void fail()
	{
		Assertions.fail();
	}
	
	public static void assertEquals(final String message, final Object expected, final Object actual)
	{
		Assertions.assertEquals(expected, actual, message);
	}
	
	public static void assertEquals(final Object expected, final Object actual)
	{
		Assertions.assertEquals(expected, actual);
	}
	
	public static void assertEquals(final String message, final String expected, final String actual)
	{
		Assertions.assertEquals(expected, actual, message);
	}
	
	public static void assertEquals(final String expected, final String actual)
	{
		Assertions.assertEquals(expected, actual);
	}
	
	public static void assertEquals(
		final String message,
		final double expected,
		final double actual,
		final double delta)
	{
		Assertions.assertEquals(expected, actual, delta, message);
	}
	
	public static void assertEquals(final double expected, final double actual, final double delta)
	{
		Assertions.assertEquals(expected, actual, delta);
	}
	
	public static void assertEquals(final String message, final float expected, final float actual, final float delta)
	{
		Assertions.assertEquals(expected, actual, delta, message);
	}
	
	public static void assertEquals(final float expected, final float actual, final float delta)
	{
		Assertions.assertEquals(expected, actual, delta);
	}
	
	public static void assertEquals(final String message, final long expected, final long actual)
	{
		Assertions.assertEquals(expected, actual, message);
	}
	
	public static void assertEquals(final long expected, final long actual)
	{
		Assertions.assertEquals(expected, actual);
	}
	
	public static void assertEquals(final String message, final boolean expected, final boolean actual)
	{
		Assertions.assertEquals(expected, actual, message);
	}
	
	public static void assertEquals(final boolean expected, final boolean actual)
	{
		Assertions.assertEquals(expected, actual);
	}
	
	public static void assertEquals(final String message, final byte expected, final byte actual)
	{
		Assertions.assertEquals(expected, actual, message);
	}
	
	public static void assertEquals(final byte expected, final byte actual)
	{
		Assertions.assertEquals(expected, actual);
	}
	
	public static void assertEquals(final String message, final char expected, final char actual)
	{
		Assertions.assertEquals(expected, actual, message);
	}
	
	public static void assertEquals(final char expected, final char actual)
	{
		Assertions.assertEquals(expected, actual);
	}
	
	public static void assertEquals(final String message, final short expected, final short actual)
	{
		Assertions.assertEquals(expected, actual, message);
	}
	
	public static void assertEquals(final short expected, final short actual)
	{
		Assertions.assertEquals(expected, actual);
	}
	
	public static void assertEquals(final String message, final int expected, final int actual)
	{
		Assertions.assertEquals(expected, actual, message);
	}
	
	public static void assertEquals(final int expected, final int actual)
	{
		Assertions.assertEquals(expected, actual);
	}
	
	public static void assertNotNull(final Object object)
	{
		Assertions.assertNotNull(object);
	}
	
	public static void assertNotNull(final String message, final Object object)
	{
		Assertions.assertNotNull(object, message);
	}
	
	public static void assertNull(final Object object)
	{
		Assertions.assertNull(object);
	}
	
	public static void assertNull(final String message, final Object object)
	{
		Assertions.assertNull(object, message);
	}
	
	public static void assertSame(final String message, final Object expected, final Object actual)
	{
		Assertions.assertSame(expected, actual, message);
	}
	
	public static void assertSame(final Object expected, final Object actual)
	{
		Assertions.assertSame(expected, actual);
	}
	
	public static void assertNotSame(final String message, final Object expected, final Object actual)
	{
		Assertions.assertNotSame(expected, actual, message);
	}
	
	public static void assertNotSame(final Object expected, final Object actual)
	{
		Assertions.assertNotSame(expected, actual);
	}
}
