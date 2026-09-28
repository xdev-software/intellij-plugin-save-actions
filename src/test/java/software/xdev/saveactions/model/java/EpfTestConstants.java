package software.xdev.saveactions.model.java;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


public final class EpfTestConstants
{
	static final Path ROOT = new File(".").toPath().toAbsolutePath().getParent();
	static final Path ROOT_RESOURCES = Paths.get(ROOT.toString(), "src", "test", "resources");
	static final Path ROOT_EPF = Paths.get(ROOT_RESOURCES.toString(), "software", "xdev", "saveactions", "model");
	
	public static final Path EXAMPLE_EPF_0 = Paths.get(ROOT_EPF.toString(), "example0.epf");
	public static final Path EXAMPLE_EPF_1 = Paths.get(ROOT_EPF.toString(), "example1.epf");
	public static final Path EXAMPLE_EPF_2 = Paths.get(ROOT_EPF.toString(), "example2.epf");
	
	private EpfTestConstants()
	{
	}
}
