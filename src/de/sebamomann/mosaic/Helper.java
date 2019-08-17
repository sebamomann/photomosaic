package de.sebamomann.mosaic;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * Helper class for various image processing methods
 */
public class Helper
{
	/**
	 * Delete a directory with all files recursively
	 * 
	 * @param dir Name of directory to delete
	 * @return boolean Weather deletion was successful or not
	 */
	public static boolean deleteDir(File dir)
	{
		if (dir.isDirectory())
		{
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++)
			{
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success)
				{
					return false;
				}
			}
		}
		return dir.delete();
	}

	/**
	 * Scale a BufferedImage to a new width and height width and height need to be
	 * the same
	 * 
	 * @param src
	 * @param finalHeight
	 * @param finalWidth
	 * @return quadratic image with given dimension
	 */
	public static BufferedImage resize(BufferedImage src, int finalHeight, int finalWidth)
	{
		int Xoffset;
		int Yoffset;
		int length;

		int height = src.getHeight();
		int width = src.getWidth();

		if (height < width)
		{
			Xoffset = (width - height) / 2;
			Yoffset = 0;
			length = height;
		} else
		{
			Yoffset = (height - width) / 2;
			Xoffset = 0;
			length = width;
		}

		BufferedImage res = new BufferedImage(length, length, BufferedImage.TYPE_INT_ARGB);

		res.getGraphics().drawImage(src, 0, 0, length, length, Xoffset, Yoffset, Xoffset + length, Yoffset + length,
				null);

		return createResizedCopy(res, finalWidth, finalHeight);
	}

	private static BufferedImage createResizedCopy(java.awt.Image orig, int scaledWidth, int scaledHeight)
	{
		BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = scaledBI.createGraphics();
		g.drawImage(orig, 0, 0, scaledWidth, scaledHeight, null);
		g.dispose();
		return scaledBI;
	}

	/**
	 * Get the average color of the complete image
	 * 
	 * @param img Image to get colors from
	 * @param x0  Start of area to look in (x axis)
	 * @param y0  Start of area to look in (y axis)
	 * @param w   Width of area to look in
	 * @param h   Height of area to look in
	 * @return Color Average color value of all pixels within given area of given
	 *         image
	 */
	public static Color calculateAverageColor(BufferedImage img, int x0, int y0, int w, int h)
	{
		int x1 = x0 + w;
		int y1 = y0 + h;
		long sumr = 0, sumg = 0, sumb = 0;
		for (int x = x0; x < x1; x++)
		{
			for (int y = y0; y < y1; y++)
			{
				Color pixel = new Color(img.getRGB(x, y));
				sumr += pixel.getRed();
				sumg += pixel.getGreen();
				sumb += pixel.getBlue();
			}
		}

		int num = w * h;
		return new Color((int) (sumr / num), (int) (sumg / num), (int) (sumb / num));
	}

	/**
	 * Find images, that that match the passed Color the most
	 * 
	 * @param images
	 * @param y
	 * @param x
	 * @param color
	 * @return ArrayList<BufferedImage> List of BufferedImages that are in average
	 *         color range
	 */
	public static ArrayList<BufferedImage> findImagesInColorRange(File[] images, int y, int x, Color color)
	{
		boolean imgNotFound = true;
		int variety = Image.beginningDeviation;

		ArrayList<BufferedImage> validImages = new ArrayList<BufferedImage>();

		while (imgNotFound)
		{
			for (File child : images)
			{
				String[] rgb = child.getName().split("_");

				if (avgColorIsValid(rgb, variety, color))
				{
					try
					{
						validImages.add(ImageIO.read(child));
					} catch (IOException e)
					{
						//
					}
				}
			}

			if (validImages.size() < 1 && variety < 255)
			{
				imgNotFound = true;
				variety += Image.deviationIncrease;
			} else if (variety < 255)
			{
				imgNotFound = false;
			} else
			{
				return validImages;
			}
		}

		return validImages;
	}

	private static boolean avgColorIsValid(String[] rgb, int variety, Color color)
	{
		return between(Integer.valueOf(rgb[0]), color.getRed() - variety, color.getRed() + variety)
				&& between(Integer.valueOf(rgb[1]), color.getGreen() - variety, color.getGreen() + variety)
				&& between(Integer.valueOf(rgb[2]), color.getBlue() - variety, color.getBlue() + variety);
	}

	private static boolean between(int value, int minValueInclusive, int maxValueInclusive)
	{
		if (value >= minValueInclusive && value <= maxValueInclusive)
			return true;
		else
			return false;
	}
}
