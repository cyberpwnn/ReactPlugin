package org.cyberpwn.react.queue;

import org.cyberpwn.react.React;
import org.cyberpwn.react.util.ASYNC;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.PluginUtil;

public class ParallelPoolManager
{
	private QueueMode mode;
	private GList<ParallelThread> threads;
	private int next;
	private int threadCount;
	
	public ParallelPoolManager(int threadCount, QueueMode mode)
	{
		if(threadCount < 1)
		{
			threadCount = 1;
		}
		
		if(threadCount > 4)
		{
			System.out.println("WARNING: HIGH THREAD COUNT FOR CORETICK");
		}
		
		threads = new GList<ParallelThread>();
		this.threadCount = threadCount;
		next = 0;
		this.mode = mode;
	}
	
	public void start()
	{
		createThreads(threadCount);
	}
	
	@SuppressWarnings("deprecation")
	public void destroyOldThreads()
	{
		boolean k = false;
		
		for(Thread i : new GList<Thread>(Thread.getAllStackTraces().keySet()))
		{
			if(i.getName().startsWith("CT Parallel Tick Thread "))
			{
				k = true;
				
				try
				{
					System.out.println("WAITING FOR OLD THREAD TO DIE: " + i.getName());
					i.interrupt();
					i.join(100);
				}
				
				catch(InterruptedException e)
				{
					
				}
				
				catch(Throwable e)
				{
					e.printStackTrace();
				}
				
				if(i.isAlive())
				{
					try
					{
						System.out.println("FORCE KILLING");
						i.stop();
					}
					
					catch(Throwable e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		if(k)
		{
			System.out.println("Killed off stale threads from pre-reload");
		}
	}
	
	@SuppressWarnings("deprecation")
	public void destroyOldThreadsLocking()
	{
		boolean k = false;
		
		for(Thread i : new GList<Thread>(Thread.getAllStackTraces().keySet()))
		{
			if(i.getName().startsWith("CT Parallel Tick Thread "))
			{
				k = true;
				
				try
				{
					System.out.println("WAITING FOR OLD THREAD TO DIE: " + i.getName());
					i.interrupt();
					i.join(1000);
				}
				
				catch(InterruptedException e)
				{
					
				}
				
				catch(Throwable e)
				{
					e.printStackTrace();
				}
				
				if(i.isAlive())
				{
					try
					{
						System.out.println("FORCE KILLING");
						i.stop();
					}
					
					catch(Throwable e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		if(k)
		{
			System.out.println("Killed off stale threads from pre-reload");
		}
		
		threads.clear();
	}
	
	public void checkThreads()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				boolean msg[] = {false};
				
				new ASYNC()
				{
					@Override
					public void async()
					{
						msg[0] = true;
					}
				};
				
				int v = 0;
				
				while(!msg[0])
				{
					try
					{
						Thread.sleep(500);
					}
					
					catch(InterruptedException e)
					{
						
					}
					
					v++;
					
					if(v > 4)
					{
						System.out.println("WARNING: No Response from Thread Pool. Restarting...");
						PluginUtil.reload(React.instance());
						break;
					}
				}
			}
		}).start();
	}
	
	public void restart()
	{
		shutdown();
		destroyOldThreadsLocking();
		start();
	}
	
	public void shutdown()
	{
		for(ParallelThread i : threads)
		{
			i.interrupt();
		}
		
		destroyOldThreads();
	}
	
	public ParallelPoolManager(int threadCount)
	{
		this(threadCount, QueueMode.ROUND_ROBIN);
	}
	
	public void queue(Execution e)
	{
		nextThread().queue(e);
	}
	
	public int getSize()
	{
		return threads.size();
	}
	
	public ParallelThread[] getThreads()
	{
		return threads.toArray(new ParallelThread[threads.size()]);
	}
	
	private ParallelThread nextThread()
	{
		if(threads.size() == 1)
		{
			return threads.get(0);
		}
		
		int id = 0;
		
		switch(mode)
		{
			case ROUND_ROBIN:
				next = (next > threads.size() - 1 ? 0 : next + 1);
				id = next;
			case SMALLEST:
				int min = Integer.MAX_VALUE;
				
				for(ParallelThread i : threads)
				{
					int size = i.getQueue().size();
					
					if(size < min)
					{
						min = size;
						id = i.getInfo().getId();
					}
				}
				
			default:
				break;
		}
		
		return threads.get(id);
	}
	
	private void createThreads(int count)
	{
		for(int i = 0; i < count; i++)
		{
			ParallelThread p = new ParallelThread(i);
			p.start();
			threads.add(p);
			System.out.println("Started Parallel CoreTick Thread: " + i);
		}
	}
}
