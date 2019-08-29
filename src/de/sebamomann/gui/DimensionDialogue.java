package de.sebamomann.gui;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import lombok.Data;

/**
 * @author Sebastian Momann
 *
 */
@Data
public class DimensionDialogue
{

	private JTextField goalX;
	private JTextField goalY;
	private JTextField sub;

	/**
	 * @return Dialogue panel
	 */
	public JPanel getDialogue()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		goalX = new JTextField();

		goalY = new JTextField();

		sub = new JTextField();

		panel.add(createRow("Zielbild (x, y)", goalX, goalY));
		panel.add(createRow("Unterbild (x & y)", sub, null));

		return panel;
	}

	private Component createRow(String string, JComponent component, JComponent component2)
	{
		JPanel panel = new JPanel();
		panel.setSize(200, 25);
		panel.setLayout(new GridLayout(1, 3, 15, 5));

		JLabel label = new JLabel(string);
		panel.add(label);

		panel.add(component);

		if (component2 == null)
		{
			component2 = new JLabel("");
		}
		panel.add(component2);

		panel.setBorder(new EmptyBorder(10, 20, 10, 20));

		return panel;
	}
}
