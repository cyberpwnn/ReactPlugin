package org.cyberpwn.react.controller;

import org.bukkit.event.Listener;
import org.cyberpwn.react.React;
import org.cyberpwn.react.util.Dispatcher;

public class Controller implements Controllable, Listener
{
	protected final Dispatcher dispatcher;
	protected final React react;
	
	public Controller(React react)
	{
		this.react = react;
		this.dispatcher = new Dispatcher(this.getClass().getSimpleName());
		this.react.register(this);
		this.react.registerController(this);
	}
	
	@Override
	public void start()
	{
		
	}
	
	@Override
	public void stop()
	{
		
	}
	
	@Override
	public void tick()
	{
		
	}
	
	public void i(String s)
	{
		dispatcher.i(s);
	}
	
	public void s(String s)
	{
		dispatcher.s(s);
	}
	
	public void f(String s)
	{
		dispatcher.f(s);
	}
	
	public void w(String s)
	{
		dispatcher.w(s);
	}
	
	public void o(String s)
	{
		dispatcher.o(s);
	}
	
	public Dispatcher getDispatcher()
	{
		return dispatcher;
	}
	
	public React getReact()
	{
		return react;
	}
}
