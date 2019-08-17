package de.sebamomann.runnable;

/**
 * @author Sebastian Momann
 *
 */
public class IndexedThread implements Runnable
{

	private int index;

	/**
	 * Basic Constructor
	 * 
	 * @param index Index of Thread
	 */
	public IndexedThread(int index)
	{
		this.index = index;
	}

	@Override
	public void run()
	{
	}

	/**
	 * @return int Index of Thread
	 */
	public int getIndex()
	{
		return this.index;
	}
}
