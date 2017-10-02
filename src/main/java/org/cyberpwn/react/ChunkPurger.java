package org.cyberpwn.react;

import java.util.Iterator;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.cyberpwn.react.queue.M;
import org.cyberpwn.react.util.Average;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.Task;
import org.cyberpwn.react.util.TaskLater;

public abstract class ChunkPurger
{
	private long threshold;
	private boolean save;
	private boolean force;
	private Iterator<Chunk> it;
	private Average cptt;
	private int[] m;

	public ChunkPurger(long threshold, boolean save, boolean force)
	{
		cptt = new Average(16);
		m = new int[] {0};
		this.threshold = threshold;
		this.save = save;
		this.force = force;
	}

	public void purge(World w)
	{
		if(it == null)
		{
			cptt.getData().clear();
			m[0] = 0;
			long mx = M.ms();

			it = new GList<Chunk>(w.getLoadedChunks()).iterator();

			new Task(0)
			{
				@Override
				public void run()
				{
					long msx = M.ms();
					int[] lx = new int[] {0};

					while(M.ms() - msx < threshold && it.hasNext())
					{
						Chunk c = it.next();

						for(Entity i : c.getEntities())
						{
							if(i instanceof Player)
							{
								onPlayerInUnloadingChunk((Player) i);
							}
						}

						new TaskLater(2)
						{
							@SuppressWarnings("deprecation")
							@Override
							public void run()
							{
								lx[0]++;
								m[0]++;
								c.unload(save, !force);
							}
						};
					}

					if(!it.hasNext())
					{
						cancel();
						it = null;
						onPurgeComplete(m[0], M.ms() - mx, (int) cptt.getAverage());
					}

					else
					{
						cptt.put(lx[0]);
					}
				}
			};

			return;
		}
	}

	public boolean isRunning()
	{
		return it != null;
	}

	public abstract void onPlayerInUnloadingChunk(Player p);

	public abstract void onPurgeComplete(int purgedChunks, long time, int cpt);

	public long getThreshold()
	{
		return threshold;
	}
}
