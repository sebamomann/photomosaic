package de.sebamomann.gui;

import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JTextArea;

/**
 * @author Sebastian Momann
 *
 */
public class CustomOutputStream extends PrintStream
{
	private JTextArea textArea;

	/**
	 * @param out
	 * @param textArea
	 */
	public CustomOutputStream(OutputStream out, JTextArea textArea)
	{
		super(out);
		this.textArea = textArea;
	}

	@Override
	public void println(String s)
	{
		LocalDateTime myDateObj = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String formattedDate = myDateObj.format(myFormatObj);
		textArea.append("[" + formattedDate + "] " + s + "\r\n");
	}
}