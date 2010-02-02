
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
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import tbs.TBSGraphics;
import tbs.model.StudentModel;
import tbs.properties.PropertyLoader;
import tbs.view.TBSButtonType;
import tbs.view.prompt.Prompt;
import tbs.view.prompt.buttons.HelpPromptButtonType;

/**
 * HelpPrompt is called when the Help button is pressed; it displays a
 * help window which can be switched between several categories of help.
 * The help strings are read from several .properties files, and can be
 * changed by modifying those files. <br>
 * "Introduction" is read from "instructions.properties", property name
 * "instrIntro"
 * "Instructions" tab is read from "instructions.properties", properties
 * "InstrDir", "InstrDir2", and "InstrDir3".
 * "Button Info" is read from "help.properties". 
 * "Survey Status" is generated based on the current state of the Model. 
 */
public class HelpPrompt extends Prompt
{

	//Information to be used by all prompt types
	StudentModel model;
	Graphics2D g2 = null;
	Properties instrProps;
	Properties helpProps; 

	//Prompt sizing information
	Dimension padding = new Dimension(10,5);
	Dimension promptSize = new Dimension(820, 0);
	Point anchorPoint = null;
	int helpStringY;
	Rectangle closeButton;
	Rectangle helpOptions;
	HelpPromptButtonType[] options = HelpPromptButtonType.values();
	HelpPromptButtonType selectedOption;
	int buttonHeight;
	int buttonHeaderWidth;
	List<String> introduction;
	List<String> instructions;

	List<String[]> buttonInfo;
	int buttonsLines = -1;
	List<String> buttonHeaders;
	List<List<String>> buttonTexts;

	public HelpPrompt(StudentModel model) {
		super();
		this.model = model;
		instrProps = PropertyLoader.getProperties("instructions");
		helpProps = PropertyLoader.getProperties("help");
		selectedOption = HelpPromptButtonType.BUTTON_INFO;
		introduction = new LinkedList<String>();
		instructions = new LinkedList<String>();
		buttonInfo = new LinkedList<String[]>();
		for(TBSButtonType bt : TBSButtonType.values()){
			if(!bt.isAdmin() && !TBSButtonType.HELP.equals(bt))
			{
				buttonInfo.add(new String[]{bt.getText(), 
						helpProps.getProperty("help_" + bt.getText())});
			}
		}
		buttonInfo.add(new String[]{"Questions", helpProps.getProperty("help_123")});
		buttonHeaders = new LinkedList<String>();
		buttonHeaderWidth = 0;
		buttonTexts = new LinkedList<List<String>>();
	}

	/**
	 * This prompt does not accept keyboard input
	 */
	public void keyPressed(KeyEvent e){}

	/**
	 * This prompt does not accept keyboard input
	 */
	public void keyTyped(KeyEvent e){}

	/**
	 * On a mouse click, this method checks whether the event occurred
	 * within a button. If it occurred within the close button, the {@link
	 * setFinished} method is used to tell the prompt to close itself. If
	 * the event is located within one of the selector buttons, that
	 * option is displayed.
	 */
	public void mousePressed(MouseEvent e) {
		if(closeButton.contains(e.getPoint()))
			setFinished(true);
		else{
			if(helpOptions.contains(e.getPoint())){
				int index = (int) ((e.getX() - helpOptions.getX()) * options.length) / promptSize.width;
				selectedOption = options[index];
			}
		}
	}

	/**
	 * Instructions for rendering this object. 
	 */
	public void paintComponent(Graphics2D g2) 
	{
		this.g2 = g2;
		promptSize.setSize(800 + padding.width * 2, 0);
		List<String> text = new LinkedList<String>();
		int totalLines = 0;
		if(HelpPromptButtonType.SURVEY_STATUS.equals(selectedOption)){
			StringBuffer status = new StringBuffer(model.surveyStatus());
			if(status.length() == 0)
				status.append("Currently you have created a tree and entered ") 
				.append("responses to all the open-response questions. ")
				.append("You are ready to submit your survey.");
			text = TBSGraphics.breakStringByLineWidth(g2,status.toString(),
					promptSize.width - padding.width * 2);
			totalLines += text.size() + 2;
		}else if(HelpPromptButtonType.INSTRUCTIONS.equals(selectedOption)){
			if(instructions.isEmpty()){
				instructions = TBSGraphics.breakStringByLineWidth(g2,
						instrProps.getProperty("instrDir"),
						promptSize.width - padding.width * 2);

				List<String> directions2 = TBSGraphics.breakStringByLineWidth(g2,
						instrProps.getProperty("instrDir2"),
						promptSize.width - padding.width * 2);
				List<String> directions3 = TBSGraphics.breakStringByLineWidth(g2,
						instrProps.getProperty("instrDir3"),
						promptSize.width - padding.width * 2);
				instructions.add("\n");
				instructions.addAll(directions2);
				instructions.add("\n");
				instructions.addAll(directions3);

			}
			text = instructions;
			totalLines += text.size() + 2;
		}else if(HelpPromptButtonType.INTRODUCTION.equals(selectedOption)){
			if(introduction.isEmpty()){
				introduction = TBSGraphics.breakStringByLineWidth(g2,
						String.format(instrProps.getProperty("instrIntro"),""),
						promptSize.width - padding.width * 2);
			}
			text = introduction;
			totalLines += text.size() + 2;
		}else{
			if(buttonsLines == -1){
				List<String> temp;
				for(String[] button : buttonInfo){
					temp = TBSGraphics.breakStringByLineWidth(g2,button[1],
							promptSize.width - ((padding.width * 2) + TBSGraphics.buttonsWidth));
					buttonsLines += temp.size();
					buttonHeaders.add(button[0]);
					buttonTexts.add(temp);
				}
			}
			totalLines = buttonsLines + 2;
		}
		calculateValues(totalLines);
		drawBox();
		TBSGraphics.drawCloseButton(g2, closeButton);
		drawButtons();
		helpStringY = anchorPoint.y;
		TBSGraphics.drawCenteredString(g2,
				new StringBuffer("Help - ").append(selectedOption.getText()).toString(),
				anchorPoint.x + padding.width, helpStringY,
				promptSize.width - padding.width * 2,
				buttonHeight,TBSGraphics.selectedPromptTextColor);
		helpStringY += buttonHeight;

		if(HelpPromptButtonType.BUTTON_INFO.equals(selectedOption)){
			buttonHeaderWidth = TBSGraphics.getStringBounds(g2, "Questions").width + (TBSGraphics.buttonsXPadding * 2);
			for(int i=0;i<buttonTexts.size();i++)
				drawText(buttonTexts.get(i), buttonHeaders.get(i));
		}else
			drawText(text);
	}

	/**
	 * Determines values of several interesting numbers, including the
	 * center point of the applet, an anchor point for the top left corner
	 * of the Prompt and locations of the close and selector buttons. 
	 */
	public void calculateValues(int lineCount) {
		buttonHeight = TBSGraphics.textHeight + padding.height;
		
		int height = (TBSGraphics.textHeight * lineCount) + 
			(padding.height * (lineCount + 1)) + buttonHeight;
		if(HelpPromptButtonType.BUTTON_INFO.equals(selectedOption))
			height += (buttonInfo.size() * TBSGraphics.paddingWidth);
		
		promptSize.setSize(promptSize.width, height);
		int centerX = model.getApplet().getWidth() / 2;
		int centerY = model.getApplet().getHeight() / 2;
		anchorPoint = new Point(centerX - (promptSize.width / 2), 
				centerY - (promptSize.height / 2));
		closeButton = new Rectangle((anchorPoint.x + promptSize.width) -
				buttonHeight, anchorPoint.y,buttonHeight, buttonHeight);
		helpOptions = new Rectangle(anchorPoint.x, 
				anchorPoint.y + (promptSize.height - buttonHeight),
				promptSize.width, buttonHeight);
	}

	/**
	 * Creates the frame for this Prompt
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
	 * Draws an array of Strings to the screen
	 */
	public void drawText(List<String> lines) {
		int startX = anchorPoint.x + padding.width;
		for(String line : lines){
			drawString(line, startX, helpStringY);
			helpStringY += TBSGraphics.textHeight + padding.height;
		}
	}

	/**
	 * Draws an array of Strings to the screen, plus a header
	 */	
	public void drawText(List<String> lines, String header) {
		int startX = anchorPoint.x + padding.width;
		TBSGraphics.drawCenteredString(g2, header, startX, helpStringY, 0, TBSGraphics.textHeight, TBSGraphics.selectedPromptTextColor);
		startX += buttonHeaderWidth;
		for(String line : lines){
			drawString(line, startX, helpStringY);
			helpStringY += TBSGraphics.textHeight + padding.height;
		}
		helpStringY += TBSGraphics.paddingWidth;
	}

	/**
	 *	Passes s, x, and y to drawString(String s, int x, int y, boolean
	 *	isSelected) with "false" as the final value.
	 */
	public void drawString(String s, int x, int y){
		drawString(s, x, y, false);
	}

	/**
	 * Calls the drawCenteredString method from {@link TBSGraphics} to put
	 * a string on the screen. Default color for selected text is the same
	 * as used for EmptyNode, but this can be changed. 
	 */
	public void drawString(String s, int x, int y, boolean isSelected) {
		if(s != null && s.length() > 0)
			TBSGraphics.drawCenteredString(g2, s, x, y, 0, TBSGraphics.textHeight, 
					isSelected ? TBSGraphics.selectedPromptTextColor : Color.BLACK);
	}

	/**
	 * Draws the close button and selector buttons. Buttons, as elsewhere
	 * in TBS, are not objects, but are simply painted on the screen and
	 * checked by contains() methods. 
	 */
	public void drawButtons()
	{
		Rectangle buttonRect = new Rectangle(helpOptions.x, helpOptions.y,
				helpOptions.width/options.length, helpOptions.height);
		for(HelpPromptButtonType option : options) {
			if(option.equals(selectedOption))
				TBSGraphics.renderButtonBackground(g2, buttonRect, true);
			else
				TBSGraphics.renderButtonBackground(g2, buttonRect, false);
			g2.setColor(Color.gray);
			g2.draw(buttonRect);
			TBSGraphics.drawCenteredString(g2, option.toString(), buttonRect.x,
					buttonRect.y + (buttonRect.height - 2), buttonRect.width, 0);
			buttonRect.setLocation(buttonRect.x + buttonRect.width, buttonRect.y);
		}
	}


	/**
	 * Returns true if {@link MouseEvent} e occurs within the boundaries
	 * of any of this Prompt's buttons. 
	 */
	public boolean isOverButton(MouseEvent e){
		if(closeButton.contains(e.getPoint()))
			return true;
		if(helpOptions.contains(e.getPoint()))
			return true;
		return false;
	}
}

