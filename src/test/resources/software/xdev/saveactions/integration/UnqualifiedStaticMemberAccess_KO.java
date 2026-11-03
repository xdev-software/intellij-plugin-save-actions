package software.xdev.saveactions.integration;

// https://github.com/dubreuia/intellij-plugin-save-actions/issues/155


class Hello
{
	static final String STR = "Hello";
	
	void sayIt()
	{
		println(STR); // should qualify
	}
	
	class Other
	{
		String method()
		{
			final String s = STR; // should qualify
			return s.replace("l", "y");
		}
	}
}


class World extends Hello
{
	@Override
	void sayIt()
	{
		println(STR + " World!"); // should qualify
	}
}
