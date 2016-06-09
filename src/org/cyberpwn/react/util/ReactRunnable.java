package org.cyberpwn.react.util;

import org.cyberpwn.react.React;

public class ReactRunnable implements Runnable
{
	private React react;
	
	public void run(React react)
	{
		this.react = react;
	}
	
	@Override
	public void run()
	{
		
	}
	
	public React getReact()
	{
		return react;
	}
}
