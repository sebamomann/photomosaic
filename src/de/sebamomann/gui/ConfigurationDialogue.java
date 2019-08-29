package de.sebamomann.gui;

import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import de.sebamomann.mosaic.Image;

/**
 *
 */
public class ConfigurationDialogue extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4897151371343009172L;
	private Image image;

	/**
	 * @param title Title of dialogue
	 */
	public ConfigurationDialogue(String title)
	{
		super(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.init();
		this.setVisible(true);
	}

	private void init()
	{
		/*
		 * Initialize Image
		 */
		image = new Image();

		/*
		 * GUI Build for selection of folders and files to create the Mosaic
		 */
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		setSize(400, 225);

		FileChooser _goalImageFile = new FileChooser("Zielbild", false);
		panel.add(_goalImageFile);

		FileChooser _sourceImageFolder = new FileChooser("Bildequellen", true);
		panel.add(_sourceImageFolder);

		FileChooser _destinationFolder = new FileChooser("Speicherort", true);
		panel.add(_destinationFolder);

		/*
		 * Save button for files and folder selection
		 */
		JPanel panelButton = new JPanel();
		JButton button = new JButton("Speichern");
		button.addActionListener((e) ->
		{
			/*
			 * Cache files and folders
			 */
			File sourceImageFolder = _sourceImageFolder.getFile();
			File goalImageFile = _goalImageFile.getFile();
			File destinationFolder = _destinationFolder.getFile();

			boolean valid = false;

			/*
			 * Check if there are unvalid selections made
			 */
			if (sourceImageFolder != null && goalImageFile != null && _destinationFolder != null)
			{
				/*
				 * Create BufferedImage from goalImge
				 */
				BufferedImage src;

				try
				{
					src = ImageIO.read(goalImageFile);
				} catch (IOException ex)
				{
					JOptionPane.showMessageDialog(null,
							"Goal image could not be loaded. Please make sure you selected a valid image");
					return;
				}

				/*
				 * Save cached data in image
				 */
				image.setSrc(src);
				image.setGoalImage(goalImageFile);
				image.setSourceImages(sourceImageFolder);
				image.setDestination(destinationFolder);

				/*
				 * Dialogue for size specifications
				 */
				DimensionDialogue dialogue = new DimensionDialogue();

				while (!valid)
				{
					valid = true;

					int i = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(button),
							dialogue.getDialogue(), "Mosaic - Dimensions", JOptionPane.PLAIN_MESSAGE);

					/*
					 * If pane is closed with "X", then dont show error message
					 */
					if (i == -1)
					{
						return;
					}

					/*
					 * Cache sizes
					 */
					int _goalX;
					int _goalY;
					int _sub;

					try
					{
						/*
						 * Process inputs and check if they are numeric
						 */
						_goalX = Integer.valueOf(dialogue.getGoalX().getText());
						image.setGoalImgSizeX(_goalX);
						_goalY = Integer.valueOf(dialogue.getGoalY().getText());
						image.setGoalImgSizeY(_goalY);
						_sub = Integer.valueOf(dialogue.getSub().getText());
						image.setSubImgSize(_sub);

						/*
						 * Check if specs exceed maximum buffered image array size
						 */
						if (_goalX * _sub >= 46340 || _goalY * _sub >= 46340)
						{
							JOptionPane.showMessageDialog(null,
									"Make sure, that the goalImgSizeX * subImgSize and goalImgSizeY * subImgSize are not bigger than 46340!");
							valid = false;
						}

						/*
						 * Values need to be at leas 1
						 */
						if (_sub < 1 || _goalX < 1 || _goalY < 1)
						{
							JOptionPane.showMessageDialog(null,
									"Image Dimensions cant be processed. They need to be bigger than 1.");
							valid = false;
						}

					} catch (Exception e2)
					{
						JOptionPane.showMessageDialog(null, "Please insert valid values");
						valid = false;
					}
				}

				/*
				 * Close condig and dimension windows
				 */
				Window w = SwingUtilities.getWindowAncestor(button);

				if (w != null)
				{
					w.setVisible(false);
				}

				dispose();

				/*
				 * Initialize window for console logs
				 */
				Thread t1Thread = new Thread(new Runnable()
				{

					@Override
					public void run()
					{
						image.initConsole();
					}
				});
				t1Thread.start();

				/*
				 * Run image building
				 */
				Thread t2Thread = new Thread(new Runnable()
				{

					@Override
					public void run()
					{
						image.create();
					}
				});
				t2Thread.start();

			} else
			{
				JOptionPane.showMessageDialog(null, "Please seelct the requested files and folders!");
			}
		});

		panelButton.add(button);
		panelButton.setBorder(new EmptyBorder(10, 0, 10, 0));
		panel.add(panelButton);

		this.add(panel);
	}
}
