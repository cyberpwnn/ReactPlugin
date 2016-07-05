package org.cyberpwn.react.util;

import java.io.BufferedReader;
import java.io.Reader;

public class GBufferedReader extends BufferedReader
{
	public GBufferedReader(Reader in)
	{
		super(in);
	}
	
	public GBufferedReader(Reader in, int k)
	{
		super(in, k);
	}
}
