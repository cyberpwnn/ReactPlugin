package org.cyberpwn.react.util;

import java.io.File;
import java.net.URI;

public class GFile extends File
{
	private static final long serialVersionUID = 1L;

	public GFile(File parent, String child)
	{
		super(parent, child);
	}
	
	public GFile(URI uri)
	{
		super(uri);
	}
	
	public GFile(String parent, String child)
	{
		super(parent, child);
	}
	
	public GFile(String child)
	{
		super(child);
	}
}
