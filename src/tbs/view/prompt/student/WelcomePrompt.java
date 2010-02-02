
package tbs.view.prompt.student;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
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
	Graphics2D g2 = null;
	Properties instrProps;
	
	//Prompt sizing information
	Dimension padding = new Dimension(10,5);
	Dimension promptSize = new Dimension(770,0);
	Point anchorPoint = null;
	int welcomeStringY;
	Rectangle closeButton;
	Rectangle startButton;
	int buttonHeight;
	
	String instrString;
	String welcomeMessage;

	public WelcomePrompt(StudentModel model) {
		super();
		this.model = model;
		instrProps = PropertyLoader.getProperties("instructions");
    startButton = new Rectangle();
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
		if(startButton.contains(e.getPoint()))
				setFinished(true);
		if(closeButton.contains(e.getPoint()))
				setFinished(true);
	}

	/**
	* Instructions for rendering this Prompt
	*/
	public void paintComponent(Graphics2D g2) 
	{
		this.g2 = g2;
		TBSGraphics.textHeight = TBSGraphics.getStringBounds(g2,"QOgj").height;
		List<String> incompletedItems = model.incompletedItems();
		String introString = "";
		if(incompletedItems.size() == OpenQuestionButtonType.values().length+1)
			introString = String.format(instrProps.getProperty("instrIntro"),
					welcomeMessage(incompletedItems));
		else
			introString = welcomeMessage(incompletedItems);	
		List<String> introduction = TBSGraphics.breakStringByLineWidth(g2,introString,
				promptSize.width - padding.width * 2);
	
		List<String> directions = TBSGraphics.breakStringByLineWidth(g2,
				instrProps.getProperty("welcome_instr"),
				promptSize.width - padding.width * 2);
		calculateValues(introduction.size() + directions.size() + 5);
		drawBox();
		TBSGraphics.drawCloseButton(g2, closeButton);
		drawButtons();
		welcomeStringY = anchorPoint.y;
		TBSGraphics.drawCenteredString(g2,"Welcome",
				anchorPoint.x + padding.width, welcomeStringY,
				promptSize.width - padding.width * 2,
				buttonHeight,TBSGraphics.selectedPromptTextColor);
		welcomeStringY += buttonHeight;
		drawText(introduction);
		welcomeStringY += TBSGraphics.textHeight + padding.height;
		TBSGraphics.drawCenteredString(g2, instrProps.getProperty("instrHeader"),
				anchorPoint.x + padding.width, welcomeStringY,
				promptSize.width - padding.width * 2,
				TBSGraphics.textHeight,TBSGraphics.selectedPromptTextColor);
		welcomeStringY += (TBSGraphics.textHeight + padding.height) * 2;
		drawText(directions);
	}


	/**
	* Calculates sundry useful numbers, including applet's center point,
	* top left corner of Prompt, and locations of buttons.
	*/
	public void calculateValues(int lineCount) {
		buttonHeight = TBSGraphics.textHeight + padding.height;
		promptSize.setSize(promptSize.width, (TBSGraphics.textHeight * lineCount) + 
				(padding.height * (lineCount + 1)) + buttonHeight);
		int centerX = model.getApplet().getWidth() / 2;
		int centerY = model.getApplet().getHeight() / 2;
		anchorPoint = new Point(centerX - (promptSize.width / 2), 
				centerY - (promptSize.height / 2));
		closeButton = new Rectangle((anchorPoint.x + promptSize.width)-buttonHeight, anchorPoint.y,
				buttonHeight, buttonHeight);
		startButton = new Rectangle(anchorPoint.x, anchorPoint.y + (promptSize.height - buttonHeight),
					promptSize.width, buttonHeight);
	}

	/**
	* Draws the frame for this Prompt
	*/
	public void drawBox() {
		Rectangle box = new Rectangle(anchorPoint.x-2, anchorPoint.y-2, promptSize.width+4, promptSize.height+4);
		g2.setColor(Color.lightGray);
		g2.fill(box);
		g2.setColor(Color.white);
		g2.setStroke(new BasicStroke(3));
		g2.draw(new Rectangle2D.Double(box.x-1.5, box.y-1.5, box.width+3, box.getHeight()+3));
		g2.setStroke(new BasicStroke());
	}

	/**
	* Probably this could be moved up to Prompt.java
	*/
	public void drawText(List<String> lines) {
		int startX = anchorPoint.x + padding.width;
		for(String line : lines){
			drawString(line, startX, welcomeStringY);
			welcomeStringY += TBSGraphics.textHeight + padding.height;
		}
	}
	
	/**
	* Probably this could be moved up to Prompt.java
	*/
	public void drawText(List<String> lines, String header) {
		int startX = anchorPoint.x + padding.width;
		TBSGraphics.drawCenteredString(g2, header, startX, welcomeStringY, 0, TBSGraphics.textHeight, TBSGraphics.selectedPromptTextColor);
		startX += TBSGraphics.buttonsWidth;
		for(String line : lines){
			drawString(line, startX, welcomeStringY);
			welcomeStringY += TBSGraphics.textHeight + padding.height;
		}
	}

	/**
	* Probably this could be moved up to Prompt.java
	*/
	public void drawString(String s, int x, int y){
		drawString(s, x, y, false);
	}

	/**
	* Probably this could be moved up to Prompt.java
	*/
	public void drawString(String s, int x, int y, boolean isSelected) {
		if(s != null && s.length() > 0)
			TBSGraphics.drawCenteredString(g2, s, x, y, 0, TBSGraphics.textHeight, 
					isSelected ? TBSGraphics.selectedPromptTextColor : Color.BLACK);
	}

	/**
	* Probably this could be moved up to Prompt.java
	*/
	public void drawButtons()
	{		
		TBSGraphics.renderButtonBackground(g2, startButton, false);
		g2.setColor(Color.gray);
		g2.draw(startButton);
		TBSGraphics.drawCenteredString(g2, "Start", startButton.x,
				startButton.y + (startButton.height - 2), startButton.width, 0);
	}


	/**
	* Probably this could be moved up to Prompt.java
	*/
	public boolean isOverButton(MouseEvent e){
		if(startButton.contains(e.getPoint()))
				return true;
		if(closeButton.contains(e.getPoint()))
				return true;
		return false;
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

