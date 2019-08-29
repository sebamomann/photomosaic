package de.sebamomann.mosaic;

import static java.lang.Math.max;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import de.sebamomann.config.Config;
import de.sebamomann.gui.Console;
import de.sebamomann.runnable.IndexedThread;
import de.sebamomann.runnable.PartialImage;
import lombok.Data;

/**
 *
 */
@Data
public class Image
{
	private static Config cfg = new Config();
	private Random random = new Random();

	/**
	 * Threads to create for preprocessing images
	 */
	public final int THREADS_PREPARE = Integer.valueOf(cfg.getProperty("threadsPrepare"));
	/**
	 * Thread to create for building final image
	 */
	public final int THREADS_BUILD = (int) Math.sqrt(Integer.valueOf(cfg.getProperty("threadsBuild")));

	/**
	 * Width and height of image that replaces a pixel in the final image
	 */
	public int subImgSize;

	/**
	 * Final image width
	 */
	public int goalImgSizeX;

	/**
	 * Final image height
	 */
	private int goalImgSizeY;

	/**
	 * Starting possible color deviation of average color
	 */
	public static int beginningDeviation = Integer.valueOf(cfg.getProperty("beginningDeviation"));

	/**
	 * Deviation increase after no image in previous deviation range is found
	 */
	public static final int deviationIncrease = Integer.valueOf(cfg.getProperty("deviationIncrease"));

	/*
	 * Goal Image source
	 */
	private File goalImage;

	/*
	 * location of source Images
	 */
	private File sourceImages;

	/*
	 * location of result image
	 */
	private File destination;

	// CONSOLE UPDATE THREAD
	// UPDATE EVERY x TimeUnits
	private TimeUnit intervall = TimeUnit.SECONDS;
	private int intervallIndex = Integer.valueOf(cfg.getProperty("intervallIndex"));

	private int intervallsPassed = 0;

	// FOR CONSOLE UPDATE
	private int preparedImages = 0;
	private int replacedImages = 0;
	private int minimumRequiredImages = max(goalImgSizeX, goalImgSizeY);

	private BufferedImage src;

	private Console console;

	/**
	 * 
	 */
	public Image()
	{

	}

	/**
	 * Initialize console for logs
	 */
	public void initConsole()
	{
		/*
		 * Window for console outputs
		 */
		new Console();
	}

	/**
	 * Starts process of preparing images and building final image
	 */
	public void create()
	{
		/*
		 * Load goal image and resize it to size specified in configuration file
		 */
		src = Helper.resize(src, goalImgSizeX, goalImgSizeY);

		long start = System.currentTimeMillis();

		prepare();
		build();

		long end = System.currentTimeMillis();

		long millis = (end - start);

		if (millis / 60000 < 1)
		{

			System.out.println("Total Duration: " + millis / 1000 + " Seconds");
		} else
		{
			System.out.println("Total Duration: " + millis / 60000 + " Minutes");
		}
	}

	private void prepare()
	{
		System.out.println("Preparing images ...");

		long startPrepare = System.currentTimeMillis();

		Helper.deleteDir(new File(sourceImages.getAbsolutePath() + "/cropped"));

		File dir = sourceImages;
		File[] images = dir.listFiles();

		/*
		 * Check for images being uploaded
		 */
		if (images != null && images.length > minimumRequiredImages)
		{
			int totalAvailableImages = images.length;
			int imagesPerThread = totalAvailableImages / THREADS_PREPARE;

			/*
			 * Build up Threads and run them with a executorService
			 * Each Thread needs to prepare a split number of all images
			 * int imagesPerThread = totalAvailableImages / THREADS_PREPARE
			 */
			ExecutorService exec = Executors.newFixedThreadPool(THREADS_PREPARE);
			for (int threadIndex = 0; threadIndex < THREADS_PREPARE; threadIndex++)
			{
				Runnable runnable = new IndexedThread(threadIndex)
				{
					@Override
					public void run()
					{
						for (int imgIndex = 0; imgIndex < imagesPerThread; imgIndex++)
						{
							try
							{
								/*
								 * Read image to crop
								 * Offset in image array is ThreadIndex * imagesPerThread
								 */
								BufferedImage src = ImageIO
										.read(images[(this.getIndex() * imagesPerThread) + imgIndex]);

								/*
								 * Check if image is loaded correctly
								 */
								if (src != null)
								{
									src = Helper.resize(src, subImgSize, subImgSize);

									Color color = Helper.calculateAverageColor(src, 0, 0, subImgSize, subImgSize);

									File file = new File(sourceImages.getAbsolutePath() + "/cropped/" + color.getRed()
											+ "_" + color.getGreen() + "_" + color.getBlue() + "_"
											+ System.currentTimeMillis() + ".png");

									file.mkdirs();
									ImageIO.write(src, "png", file);
								}

							} catch (IOException e)
							{
								// No problem if image could not be read or cropped
							}

							preparedImages++;
						}
					}
				};
				exec.execute(runnable);
			}

			ScheduledExecutorService someScheduler = Executors.newScheduledThreadPool(1);
			Runnable runnable = new Runnable()
			{

				private volatile boolean stopWork;

				@Override
				public void run()
				{
					if (!stopWork)
					{
						float _croppedImages = preparedImages;
						float _totalImages = totalAvailableImages;

						/*
						 * Percentage of processed Images
						 */
						int percent = Math.round((_croppedImages / _totalImages) * 100);

						if (replacedImages < totalAvailableImages)
						{
							System.out.println("Prepared: " + percent + "%");

							/*
							 * Schedule time to execute Thread again
							 */
							someScheduler.schedule(this, intervallIndex, intervall);

							intervallsPassed++;
						} else
						{
							this.stop();
						}
					}
				}

				private void stop()
				{
					stopWork = true;
				}
			};

			someScheduler.schedule(runnable, intervallIndex, intervall);

			exec.shutdown();

			while (!exec.isTerminated())
			{
			}

			/*
			 * Shutdown scheduled Tread
			 */
			someScheduler.shutdownNow();

			System.out.println("Prepared: 100%");
			System.out.println("Preparation finished");

			long endPrepare = System.currentTimeMillis();

			long millis = (endPrepare - startPrepare);

			if (millis / 60000 < 1)
			{
				System.out.println("Preparation Duration: " + millis / 1000 + " Seconds");
			} else
			{
				System.out.println("Preparation Duration: " + millis / 60000 + " Minutes");
			}

			System.out.println();
			System.out.println("---------------------------------------------");
			System.out.println();
		} else
		{
			System.out.println("Exit .....");
			System.out.println("There are not enough Images in the \"/img\" foler.");
			System.out.println(
					"For a proper Mosaic there need to be at least " + minimumRequiredImages + " source images.");
			System.exit(0);
		}
	}

	private void build()
	{
		System.out.println("Building ...");

		long startBuild = System.currentTimeMillis();

		int threadCount = 0;

		int w = src.getWidth();
		int h = src.getHeight();

		/*
		 * Width and height each Threads need to take care of
		 */
		int threadWidth = (w / THREADS_BUILD);
		int threadHeight = (h / THREADS_BUILD);

		/*
		 * Create final image with final dimensions
		 * Due to each pixel getting replaced by an image, the final image width and
		 * height need to be multiplied by the size of the subimage
		 */
		BufferedImage resultImage = new BufferedImage(goalImgSizeX * subImgSize, goalImgSizeY * subImgSize,
				BufferedImage.TYPE_INT_ARGB);

		ExecutorService exec = Executors.newFixedThreadPool(THREADS_BUILD * THREADS_BUILD);

		int totalImagesToInsert = goalImgSizeX * goalImgSizeY;

		File dir = new File(sourceImages.getAbsolutePath() + "/cropped");
		File[] images = dir.listFiles();

		if (images != null && images.length > minimumRequiredImages)
		{

			/*
			 * Split final image into matrix
			 * Assign each field of matrix to a Thread
			 */
			for (int threadX = 0; threadX < THREADS_BUILD; threadX++)
			{
				for (int threadY = 0; threadY < THREADS_BUILD; threadY++)
				{
					Runnable runnable = new PartialImage(threadCount, src, threadWidth * threadX,
							threadHeight * threadY)
					{
						@Override
						public void run()
						{
							int currX;
							int currY;

							/*
							 * Y Axis
							 */
							for (int i = 0; i < (h / THREADS_BUILD); i++)
							{
								/*
								 * X Axis
								 */
								for (int j = 0; j < (w / THREADS_BUILD); j++)
								{
									/*
									 * Check for images
									 */
									Color color = new Color(
											this.getSrc().getRGB(j + this.getXOffset(), i + this.getYOffset()));
									ArrayList<BufferedImage> validImages = Helper.findImagesInColorRange(images, i, j,
											color);

									/*
									 * Create random index in case there are multiple images in ArrayList that are
									 * within the color range
									 */
									int index = random.nextInt(validImages.size());

									currX = j * subImgSize + this.getXOffset() * subImgSize;
									currY = i * subImgSize + this.getYOffset() * subImgSize;

//									for (int _i = 0; _i < subImgSize; _i++)
//									{
//										for (int _j = 0; _j < subImgSize; _j++)
//										{
//													validImages.get(index).getRGB(_j, _i));
//
//										}
//									}

									Graphics2D g2d = resultImage.createGraphics();
									g2d.drawImage(validImages.get(index), currX, currY, null);
									g2d.dispose();

									replacedImages++;
								}
							}
						}
					};

					threadCount++;
					exec.execute(runnable);
				}
			}
		} else
		{
			System.out.println("Exit .....");
			System.out.println("There are not enough Images in the \"/img/cropped\" foler");
			System.out.println("Not enough images were valid and could not be cropped and resized");
			System.out.println("For a proper Mosaic there need to be at least " + minimumRequiredImages
					+ " source images, that can be cropped");
			System.exit(0);
		}

		ScheduledExecutorService someScheduler = Executors.newScheduledThreadPool(1);
		Runnable runnable = new Runnable()
		{

			private volatile boolean stopWork;

			@Override
			public void run()
			{
				if (!stopWork)
				{
					float _replacedImages = replacedImages;
					float _totalImagesToInsert = totalImagesToInsert;

					/*
					 * Percentage of replaced images
					 */
					int percent = Math.round((_replacedImages / _totalImagesToInsert) * 100);

					float left = 100 - percent;

					float eta = (left / percent) * (intervallIndex + intervallsPassed);

					if (replacedImages < totalImagesToInsert)
					{
						System.out.println("Building " + percent + "% approx. " + Math.round(eta) + " "
								+ intervall.toString() + " left");
						someScheduler.schedule(this, intervallIndex, intervall);
						intervallsPassed++;
					} else
					{
						this.stop();
					}
				}
			}

			private void stop()
			{
				stopWork = true;
			}
		};

		someScheduler.schedule(runnable, intervallIndex, intervall);

		exec.shutdown();

		while (!exec.isTerminated())
		{
		}

		someScheduler.shutdownNow();

		System.out.println("Building 100%");

		long endBuild = System.currentTimeMillis();

		long millis = (endBuild - startBuild);

		if (millis / 60000 < 1)
		{
			System.out.println("Building Duration: " + millis / 1000 + " Seconds");
		} else
		{
			System.out.println("Building Duration: " + millis / 60000 + " Minutes");
		}

		System.out.println();
		System.out.println("---------------------------------------------");
		System.out.println();

		System.out.println("Rendering image as png ...");

		String name = goalImage.getName() + "_" + subImgSize + "-" + goalImgSizeX + "-" + goalImgSizeY + "_"
				+ System.currentTimeMillis() + ".png";
		File file = new File(destination.getAbsolutePath() + "/" + name);
		try
		{
			ImageIO.write(resultImage, "png", file);
			System.out.println("Rendering success!");
			System.out.println();
			System.out.println("Imaged saved as");
			System.out.println(name);
			System.out.println();
		} catch (IOException e)
		{
			System.out.println("Rendering failure!");
			System.out.println("Image could not be saved!");
			System.exit(0);
		}
	}
}
