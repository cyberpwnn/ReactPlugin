package org.cyberpwn.react.util;

import java.io.DataOutputStream;
import java.io.OutputStream;

public class GDataOutputStream extends DataOutputStream
{
	public GDataOutputStream(OutputStream out)
	{
		super(out);
	}
}
