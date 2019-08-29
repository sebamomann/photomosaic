package de.sebamomann.gui;

import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * @author Sebastian Momann
 *
 */
public class Console extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2315492757243466644L;
	private JScrollPane scroll;

	/**
	 * Console for logs
	 */
	public Console()
	{
		setTitle("Mosaic build");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel middlePanel = new JPanel();
		middlePanel.setBorder(new TitledBorder(new EtchedBorder(), "Console output"));

		// create the middle panel components

		JTextArea display = new JTextArea(16, 30);
		display.setText("Console initialized \r\n");
		display.setEditable(false); // set textArea non-editable

		scroll = new JScrollPane(display);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		// Add Textarea in to middle panel
		middlePanel.add(scroll);

		// My code
		add(middlePanel);
		pack();
		setLocationRelativeTo(null);

		PrintStream printStream = new CustomOutputStream(System.out, display);
		System.setOut(printStream);

		Thread thread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				while (true)
				{
					scrollDown();
				}
			}
		});

		thread.start();

		setVisible(true);
	}

	private void scrollDown()
	{
		JScrollBar bar = scroll.getVerticalScrollBar();
		bar.setValue(bar.getMaximum());
	}
}
