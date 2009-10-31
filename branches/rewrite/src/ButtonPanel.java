//TBS version 0.2 - ButtonPanel
//A panel. Filled with buttons. Duh.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class ButtonPanel extends JPanel
{
	private JButton link;
	private JButton unlink;
	private JButton delete;
	private JButton split;
	private JButton print;
	private JButton undo;

	public ButtonPanel()
	{
		ButtonListener listener= new ButtonListener();
		

		link =new JButton("Link");
		unlink = new JButton ("Unlink");
		delete = new JButton ("Delete");
		split = new JButton ("Split");
		print = new JButton ("Print");
		undo = new JButton ("Undo");

		link.setEnabled(false);
		unlink.setEnabled(false);
		delete.setEnabled(false);
		split.setEnabled(false);
		print.setEnabled(false);
		undo.setEnabled(false);

		link.addActionListener(listener);
		unlink.addActionListener(listener);
		delete.addActionListener(listener);
		split.addActionListener(listener);
		print.addActionListener(listener);
		undo.addActionListener(listener);

		add(link);
		add(unlink);
		add(delete);
		add(split);
		add(print);
		add(undo);

	}
}


