
package tbs.view.prompt.student;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
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
public class WelcomePrompt extends Prompt
{

	//Information to be used by all prompt types
	StudentModel model;
	Properties instrProps;
	
	//Prompt sizing information
	int buttonHeight;
	
	String instrString;
	String welcomeMessage;

	public WelcomePrompt(StudentModel model) {
		super(false, false, new Dimension(770,0), model);
		this.model = model;
		instrProps = PropertyLoader.getProperties("instructions");
	}

	/**
	* This Prompt does not handle keyboard input
	*/
	public void keyPressed(KeyEvent e){}

	/**
	* This Prompt does not handle keyboard input
	*/
	public void keyTyped(KeyEvent e){}

	/**
	* Checks whether any mouse clicks occurred within the start button or
	* the close button; if so, calls {@link setFinished} to close this
	* Prompt. 
	*/
	public void mousePressed(MouseEvent e) {
		if(getBottomButtons().contains(e.getPoint()))
				setFinished(true);
		if(getCloseButton().contains(e.getPoint()))
				setFinished(true);
	}

	/**
	* Instructions for rendering this Prompt
	*/
	public void paintComponent(Graphics2D g2) 
	{
		setGraphics(g2);
		List<String> incompletedItems = model.incompletedItems();
		String introString = "";
		if(incompletedItems.size() == OpenQuestionButtonType.values().length+1)
			introString = String.format(instrProps.getProperty("instrIntro"),
					welcomeMessage(incompletedItems));
		else
			introString = welcomeMessage(incompletedItems);	
		List<String> introduction = TBSGraphics.breakStringByLineWidth(g2,introString,
				getWidth() - TBSGraphics.padding.width * 2);
	
		List<String> directions = TBSGraphics.breakStringByLineWidth(g2,
				instrProps.getProperty("welcome_instr"),
        getWidth() - TBSGraphics.padding.width * 2);
		calculateValues(introduction.size() + directions.size() + 5, true);
		drawBox();
		drawButtons(new Object[]{"Start"});
		drawHeader("Welcome");
		incrementStringY();
		drawText(introduction);
		incrementStringY();
		drawHeader(instrProps.getProperty("instrHeader"));
		incrementStringYMulti(2);
		drawText(directions);
	}

	/**
	 * Calculates the welcome message for the student, based on the state
	 * of the Model.
	 */
	private String welcomeMessage(List<String> incompletedItems){
		Student student = model.getStudent();
		String name = student.getName();
		String lastUpdate = student.getLastUpdate();
		StringBuffer welcome = new StringBuffer("Welcome");
		if(incompletedItems.size() < OpenQuestionButtonType.values().length+1)
			welcome.append(" back");
		if(!TBSUtils.isStringEmpty(name))
			welcome.append(", ").append(name).append(", ");
		welcome.append(" to the Diversity Of Life Survey! ");
		if(!TBSUtils.isStringEmpty(lastUpdate)){
			if(incompletedItems.isEmpty())
				welcome.append("You have completed the survey and recieved 15 points. ");
			else{
				if(incompletedItems.size() == 1){
					welcome.append("You still need to complete ");
					welcome.append(incompletedItems.remove(0)).append(". ");
				}
				else if(incompletedItems.size() <= OpenQuestionButtonType.values().length+1){
					welcome.append("You still need to complete ");
					welcome.append(incompletedItems.remove(0));
					String statusEnd = incompletedItems.remove(incompletedItems.size()-1);
					for(String s : incompletedItems)
						welcome.append(", ").append(s);
					welcome.append(" & ").append(statusEnd).append(". ");
				}
			}
		}
		return welcome.toString();
	}

}

