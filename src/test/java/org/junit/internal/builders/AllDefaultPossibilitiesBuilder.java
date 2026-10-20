package org.junit.internal.builders;

import org.junit.runner.Runner;
import org.junit.runners.model.RunnerBuilder;


/**
 * @deprecated Compat for junit-vintage-runner
 */
@SuppressWarnings("all")
@Deprecated
public class AllDefaultPossibilitiesBuilder extends RunnerBuilder
{
	public AllDefaultPossibilitiesBuilder(final boolean canUseSuiteMethod)
	{
	}
	
	public Runner safeRunnerForClass(Class<?> testClass)
	{
		return null;
	}
}
