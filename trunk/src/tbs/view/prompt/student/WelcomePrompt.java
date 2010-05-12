package tbs.view.prompt.student;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

import tbs.TBSGraphics;
import tbs.TBSUtils;
import tbs.model.StudentModel;
import tbs.model.admin.Student;
import tbs.properties.PropertyLoader;
import tbs.view.OpenQuestionButtonType;
import tbs.view.prompt.Prompt;

/**
 * Displays a welcome message on starting up the applet.
 */
public class WelcomePrompt extends Prompt {

	// Information to be used by all prompt types
	StudentModel model;
	Properties instrProps;

	// Prompt sizing information
	int buttonHeight;

	String instrString;
	String welcomeMessage;

	public WelcomePrompt(StudentModel model) {
		super(false, false, new Dimension(770, 0), model);
		this.model = model;
		instrProps = PropertyLoader.getProperties("instructions");
	}

	/**
	 * This Prompt does not handle keyboard input
	 */
	public void keyPressed(KeyEvent e) {
	}

	/**
	 * This Prompt does not handle keyboard input
	 */
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * Checks whether any mouse clicks occurred within the start button or the
	 * close button; if so, calls {@link setFinished} to close this Prompt.
	 */
	public void mousePressed(MouseEvent e) {
		if (getBottomButtons().contains(e.getPoint()))
			setFinished(true);
		if (getCloseButton().contains(e.getPoint()))
			setFinished(true);
	}

	/**
	 * Instructions for rendering this Prompt
	 */
	public void paintComponent(Graphics2D g2) {
		setGraphics(g2);
		List<String> incompletedItems = model.incompletedItems();
		String introString = "";
		if (incompletedItems.size() == OpenQuestionButtonType.values().length + 1)
			introString = MessageFormat.format(instrProps.getProperty("instrIntro"),
					welcomeMessage(incompletedItems));
		else
			introString = welcomeMessage(incompletedItems);
		List<String> introduction = TBSGraphics.breakStringByLineWidth(g2,
				introString, getWidth() - TBSGraphics.padding.width * 2);

		List<String> directions = TBSGraphics.breakStringByLineWidth(g2,
				instrProps.getProperty("welcome_instr"), getWidth()
						- TBSGraphics.padding.width * 2);
		calculateValues(introduction.size() + directions.size() + 5, true);
		drawBox();
		drawButtons(new Object[] { "Start" });
		drawHeader("Welcome");
		incrementStringY();
		drawText(introduction);
		incrementStringY();
		drawHeader(instrProps.getProperty("instrHeader"));
		incrementStringYMulti(2);
		drawText(directions);
	}

	/**
	 * Calculates the welcome message for the student, based on the state of the
	 * Model.
	 */
	private String welcomeMessage(List<String> incompletedItems) {
		Student student = model.getStudent();
		String name = student.getName();
		String lastUpdate = student.getLastUpdate();
		MessageFormat welcome = new MessageFormat(instrProps.getProperty("instrIntroStart"));
		Object[] args = new Object[]{"",""};
		StringBuffer params = new StringBuffer();
		if (incompletedItems.size() < OpenQuestionButtonType.values().length + 1)
			params.append(" back");
		if (!TBSUtils.isStringEmpty(name))
			params.append(", ").append(name).append(",");
		args[0] = params.toString();
		if (!TBSUtils.isStringEmpty(lastUpdate)) {
			if (incompletedItems.isEmpty())
				args[1] = instrProps.getProperty("instrCompleted");
			else {
				params = new StringBuffer();
				if (incompletedItems.size() == 1)
					params.append(incompletedItems.remove(0));
				else if (incompletedItems.size() <= OpenQuestionButtonType
						.values().length + 1) {
					params.append(incompletedItems.remove(0));
					String statusEnd = incompletedItems.remove(incompletedItems
							.size() - 1);
					for (String s : incompletedItems)
						params.append(", ").append(s);
					params.append(" & ").append(statusEnd);
				}
				params.append(" ");
				args[1] = MessageFormat.format(instrProps.getProperty("instrNeededToComplete"), params.toString());
			}
		}
		return welcome.format(args);
	}

}
