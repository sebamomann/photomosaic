package de.sebamomann.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author Sebastian Momann
 *
 */
public class FileChooser extends JPanel implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6927831424845237560L;
	JButton button;
	JFileChooser chooser;
	String choosertitle;
	JLabel label;

	boolean isFolder;

	/**
	 * @param titel
	 * @param folder
	 */
	public FileChooser(String titel, boolean folder)
	{
		super();

		isFolder = folder;

		setLayout(new GridLayout(1, 2));

		label = new JLabel(titel);
		label.setSize(200, 25);
		add(label);

		button = new JButton("Suchen");
		button.addActionListener(this);
		button.setSize(100, 25);
		add(button);

		setBorder(new EmptyBorder(10, 15, 10, 15));

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		/*
		 * Initialize file Chooser
		 */
		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("D:\\Informatik\\I002_JAVA\\J001_Mosaic"));

		chooser.setDialogTitle("Choose file / folder");
		/*
		 * Dont accept file chosing
		 */
		if (isFolder)
		{
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
		} else
		{
			FileNameExtensionFilter jf = new FileNameExtensionFilter("Image", "jpg", "jpeg", "png");
			chooser.setFileFilter(jf);
		}

		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			button.setText(chooser.getSelectedFile().getName());
		}
	}

	/**
	 * @return File | null Selected file
	 *         if no file is selected return null
	 */
	public File getFile()
	{
		try
		{
			return chooser.getSelectedFile();
		} catch (Exception e)
		{
			return null;
		}

	}
}
