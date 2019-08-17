package de.sebamomann.runnable;

import java.awt.image.BufferedImage;

/**
 *
 */
public class PartialImage implements Runnable
{
	private int index;
	private BufferedImage src;
	private int XOffset;
	private int YOffset;

	/**
	 * @param index
	 * @param src
	 * @param x
	 * @param y
	 */
	public PartialImage(int index, BufferedImage src, int x, int y)
	{
		this.index = index;
		this.src = src;
		this.XOffset = x;
		this.YOffset = y;
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @return index
	 */
	public int getIndex()
	{
		return index;
	}

	/**
	 * @param index
	 */
	public void setIndex(int index)
	{
		this.index = index;
	}

	/**
	 * @return src
	 */
	public BufferedImage getSrc()
	{
		return src;
	}

	/**
	 * @param src
	 */
	public void setSrc(BufferedImage src)
	{
		this.src = src;
	}

	/**
	 * @return XOffset
	 */
	public int getXOffset()
	{
		return XOffset;
	}

	/**
	 * @param xOffset
	 */
	public void setXOffset(int xOffset)
	{
		XOffset = xOffset;
	}

	/**
	 * @return YOffset
	 */
	public int getYOffset()
	{
		return YOffset;
	}

	/**
	 * @param yOffset
	 */
	public void setYOffset(int yOffset)
	{
		YOffset = yOffset;
	}

}
