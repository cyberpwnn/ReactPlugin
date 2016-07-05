package org.cyberpwn.react.util;

import java.io.InputStream;
import java.io.InputStreamReader;

public class GInputStreamReader extends InputStreamReader
{
	public GInputStreamReader(InputStream in)
	{
		super(in);
	}
}
