package org.cyberpwn.react.timings;

public class TimingsObject
{
	private Long time;
	private Long count;
	private Long avg;
	private Long violations;
	
	public TimingsObject(Long time, Long count, Long avg, Long violations)
	{
		if(time == null)
		{
			time = 0l;
		}
		
		if(count == null)
		{
			count = 0l;
		}
		
		if(avg == null)
		{
			avg = 0l;
		}
		
		if(violations == null)
		{
			violations = 0l;
		}
		
		this.time = time;
		this.count = count;
		this.avg = avg;
		this.violations = violations;
	}
	
	public Long getTime()
	{
		return time;
	}
	
	public void setTime(Long time)
	{
		this.time = time;
	}
	
	public Long getCount()
	{
		return count;
	}
	
	public void setCount(Long count)
	{
		this.count = count;
	}
	
	public Long getAvg()
	{
		return avg;
	}
	
	public void setAvg(Long avg)
	{
		this.avg = avg;
	}
	
	public Long getViolations()
	{
		return violations;
	}
	
	public void setViolations(Long violations)
	{
		this.violations = violations;
	}
	
	public void add(Long time, Long count, Long avg, Long violations)
	{
		if(time == null)
		{
			time = 0l;
		}
		
		if(count == null)
		{
			count = 0l;
		}
		
		if(avg == null)
		{
			avg = 0l;
		}
		
		if(violations == null)
		{
			violations = 0l;
		}
		
		this.time += time;
		this.count += count;
		this.avg += avg;
		this.violations += violations;
	}
	
	public double getMs()
	{
		return (double) (((double) getTime() / 1000000.0) / (double) getCount());
	}
}
