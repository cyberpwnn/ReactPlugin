package org.cyberpwn.react.util;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class GSocket extends Socket
{
	public GSocket(String s, int p) throws UnknownHostException, IOException
	{
		super(s, p);
	}
}
