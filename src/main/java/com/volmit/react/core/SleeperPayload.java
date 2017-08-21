package com.volmit.react.core;

import com.volmit.react.util.M;

public class SleeperPayload
{
	private long targetDelay;
	private long lastWake;
	private long saturation;
	private boolean sleeping;
	
	public SleeperPayload(long targetDelay, boolean sleeping)
	{
		this.targetDelay = targetDelay;
		this.sleeping = sleeping;
		saturation = 0;
		lastWake = M.ms();
	}
	
	public SleeperPayload(long targetDelay)
	{
		this(targetDelay, false);
	}
	
	public void sleep()
	{
		sleeping = true;
	}
	
	public void stimulate()
	{
		sleeping = false;
	}
	
	public boolean isSleepy()
	{
		return sleeping;
	}
	
	public long getSaturation()
	{
		return saturation;
	}
	
	public int getSaturationTicks()
	{
		return (int) (saturation / 50);
	}
	
	public int recycle()
	{
		long smp = getSaturation();
		int time = 0;
		
		while(smp > getTargetDelay())
		{
			time++;
			smp -= getTargetDelay();
		}
		
		saturation = smp;
		return time;
	}
	
	public boolean trigger()
	{
		if(isSleepy())
		{
			if(M.ms() - lastWake > targetDelay)
			{
				lastWake = M.ms();
				saturation += ((M.ms() - lastWake) - targetDelay) > 0 ? (M.ms() - lastWake) - targetDelay : 0;
				
				return true;
			}
			
			return false;
		}
		
		return true;
	}
	
	public long getTargetDelay()
	{
		return targetDelay;
	}
	
	public void setTargetDelay(long targetDelay)
	{
		this.targetDelay = targetDelay;
	}
}
