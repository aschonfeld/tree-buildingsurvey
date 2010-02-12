
package tbs.view.prompt.student;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
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
	Properties instrProps;
	Properties helpProps; 

	//Prompt sizing information
	HelpPromptButtonType[] options = HelpPromptButtonType.values();
	HelpPromptButtonType selectedOption;
	int buttonHeaderWidth;
	List<String> introduction;
	List<String> instructions;

	List<String[]> buttonInfo;
	int buttonsLines = -1;
	List<String> buttonHeaders;
	List<List<String>> buttonTexts;

	public HelpPrompt(StudentModel model) {
		super(true, false, new Dimension(820, 0), model);
		this.model = model;
		instrProps = PropertyLoader.getProperties("instructions");
		helpProps = PropertyLoader.getProperties("help");
		selectedOption = HelpPromptButtonType.BUTTON_INFO;
		introduction = new LinkedList<String>();
		instructions = new LinkedList<String>();
		buttonInfo = new LinkedList<String[]>();
		for(TBSButtonType bt : TBSButtonType.values()){
			if(!bt.isAdmin() && !TBSButtonType.HELP.equals(bt)){
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
		if(getCloseButton().contains(e.getPoint()))
			setFinished(true);
		else{
			if(getBottomButtons().contains(e.getPoint())){
				int index = (int) ((e.getX() - getBottomButtons().getX()) * options.length) / getWidth();
				selectedOption = options[index];
			}
		}
	}

	/**
	 * Instructions for rendering this object. 
	 */
	public void paintComponent(Graphics2D g2) 
	{
		setGraphics(g2);
		getPromptSize().setSize(800 + TBSGraphics.padding.width * 2, 0);
		List<String> text = new LinkedList<String>();
		int totalLines = 0;
		if(HelpPromptButtonType.SURVEY_STATUS.equals(selectedOption)){
			StringBuffer status = new StringBuffer(model.surveyStatus());
			if(status.length() == 0)
				status.append("Currently you have created a tree and entered ") 
				.append("responses to all the open-response questions. ")
				.append("You are ready to submit your survey.");
			text = TBSGraphics.breakStringByLineWidth(g2,status.toString(),
					getWidth() - TBSGraphics.padding.width * 2);
			totalLines += text.size() + 2;
		}else if(HelpPromptButtonType.INSTRUCTIONS.equals(selectedOption)){
			if(instructions.isEmpty()){
				instructions = TBSGraphics.breakStringByLineWidth(g2,
						instrProps.getProperty("instrDir"),
						getWidth() - TBSGraphics.padding.width * 2);

				List<String> directions2 = TBSGraphics.breakStringByLineWidth(g2,
						instrProps.getProperty("instrDir2"),
						getWidth() - TBSGraphics.padding.width * 2);
				List<String> directions3 = TBSGraphics.breakStringByLineWidth(g2,
						instrProps.getProperty("instrDir3"),
						getWidth() - TBSGraphics.padding.width * 2);
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
						getWidth() - TBSGraphics.padding.width * 2);
			}
			text = introduction;
			totalLines += text.size() + 2;
		}else{
			if(buttonsLines == -1){
				List<String> temp;
				for(String[] button : buttonInfo){
					temp = TBSGraphics.breakStringByLineWidth(g2,button[1],
							getWidth() - ((TBSGraphics.padding.width * 2) + TBSGraphics.buttonsWidth));
					buttonsLines += temp.size();
					buttonHeaders.add(button[0]);
					buttonTexts.add(temp);
				}
			}
			totalLines = buttonsLines + 2;
		}
		if(HelpPromptButtonType.BUTTON_INFO.equals(selectedOption))
			calculateValues(totalLines, (buttonInfo.size() * TBSGraphics.padding.width) + TBSGraphics.textHeight + TBSGraphics.padding.height, true, true);
		else
			calculateValues(totalLines, TBSGraphics.textHeight + TBSGraphics.padding.height, true, true);
		drawBox();
		drawCloseButton();
		drawButtons(options, selectedOption.toString());
		drawHeader(new StringBuffer("Help - ").append(selectedOption.getText()).toString());
		incrementStringY();

		if(HelpPromptButtonType.BUTTON_INFO.equals(selectedOption)){
			buttonHeaderWidth = TBSGraphics.getStringBounds(g2, "Questions").width + (TBSGraphics.padding.width * 2);
			for(int i=0;i<buttonTexts.size();i++)
				drawText(buttonTexts.get(i), buttonHeaders.get(i));
		}else
			drawText(text);
	}

	/**
	 * Draws an array of Strings to the screen, plus a header
	 */	
	public void drawText(List<String> lines, String header) {
		int startX = getX() + TBSGraphics.padding.width;
		TBSGraphics.drawCenteredString(getGraphics(), header, startX, getStringY(), 0, TBSGraphics.textHeight, TBSGraphics.selectedPromptTextColor);
		startX += buttonHeaderWidth;
		for(String line : lines){
			drawString(line, startX, getStringY());
			incrementStringY();
		}
		incrementStringY(TBSGraphics.padding.width);
	}


	/**
	 * Returns true if {@link MouseEvent} e occurs within the boundaries
	 * of any of this Prompt's buttons. 
	 */
	public boolean isOverButton(MouseEvent e){
		if(getCloseButton().contains(e.getPoint()))
			return true;
		if(getBottomButtons().contains(e.getPoint()))
			return true;
		return false;
	}
}

