package org.cyberpwn.react.object;

public class Failure
{
	private final Long time;
	private final GList<StackTraceElement> stackTrace;
	private final String message;
	private final String type;
	
	public Failure(Long time, GList<StackTraceElement> stackTrace, String message, String type)
	{
		this.time = time;
		this.stackTrace = stackTrace;
		this.message = message;
		this.type = type;
	}
	
	public Long getTime()
	{
		return time;
	}
	
	public GList<StackTraceElement> getStackTrace()
	{
		return stackTrace;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public String getType()
	{
		return type;
	}
}
