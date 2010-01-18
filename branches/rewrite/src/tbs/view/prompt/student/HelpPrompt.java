
package tbs.view.prompt.student;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import tbs.TBSGraphics;
import tbs.model.TBSModel;
import tbs.model.admin.Response;
import tbs.model.admin.Student;
import tbs.properties.PropertyType;
import tbs.view.OpenQuestionButtonType;
import tbs.view.TBSButtonType;
import tbs.view.prompt.Prompt;
import tbs.view.prompt.buttons.HelpPromptButtonType;

public class HelpPrompt extends Prompt
{

	//Information to be used by all prompt types
	TBSModel model;
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
	int textHeight = -1;

	List<String> introduction;
	List<String> instructions;
	
	List<String[]> buttonInfo;
	int buttonsLines = -1;
	List<String> buttonHeaders;
	List<List<String>> buttonTexts;

	public HelpPrompt(TBSModel model) {
		super();
		this.model = model;
		instrProps = model.getProperties(PropertyType.INSTRUCTIONS);
		helpProps = model.getProperties(PropertyType.HELP);
		selectedOption = HelpPromptButtonType.BUTTON_INFO;
		introduction = new LinkedList<String>();
		instructions = new LinkedList<String>();
		buttonInfo = new LinkedList<String[]>();
		for(TBSButtonType bt : TBSButtonType.values()){
			if(!bt.isAdmin() && !TBSButtonType.HELP.equals(bt))
				buttonInfo.add(new String[]{bt.getText(), helpProps.getProperty("help_" + bt.getText())});
		}
		buttonInfo.add(new String[]{"1,2,3", helpProps.getProperty("help_123")});
		buttonHeaders = new LinkedList<String>();
		buttonTexts = new LinkedList<List<String>>();
	}


	public void keyPressed(KeyEvent e){}

	public void keyTyped(KeyEvent e){}

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

	public void paintComponent(Graphics2D g2) 
	{
		this.g2 = g2;
		if(textHeight == -1)
			textHeight = TBSGraphics.getStringBounds(g2,"QOgj").height;
		promptSize.setSize(800 + padding.width * 2, 0);
		List<String> text = new LinkedList<String>();
		int totalLines = 0;
		if(HelpPromptButtonType.SURVEY_STATUS.equals(selectedOption)){
			text = TBSGraphics.breakStringByLineWidth(g2,
					surveyStatus(),
					promptSize.width - padding.width * 2);
			totalLines += text.size() + 2;
		}else if(HelpPromptButtonType.INSTRUCTIONS.equals(selectedOption)){
			if(instructions.isEmpty()){
				instructions = TBSGraphics.breakStringByLineWidth(g2,
						instrProps.getProperty("instrDir"),
						promptSize.width - padding.width * 2);
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
		drawButtons();
		helpStringY = anchorPoint.y;
		TBSGraphics.drawCenteredString(g2,"Help - " + selectedOption.getText(),
				anchorPoint.x + padding.width, helpStringY,
				promptSize.width - padding.width * 2,
				buttonHeight,TBSGraphics.emptyNodeColor);
		helpStringY += buttonHeight;
		
		if(HelpPromptButtonType.BUTTON_INFO.equals(selectedOption)){
			for(int i=0;i<buttonTexts.size();i++)
				drawText(buttonTexts.get(i), buttonHeaders.get(i));
		}else
			drawText(text);
	}

	public void calculateValues(int lineCount) {
		buttonHeight = textHeight + padding.height;
		promptSize.setSize(promptSize.width, (textHeight * lineCount) + 
				(padding.height * (lineCount + 1)) + buttonHeight);
		int centerX = model.getApplet().getWidth() / 2;
		int centerY = model.getApplet().getHeight() / 2;
		anchorPoint = new Point(centerX - (promptSize.width / 2), 
				centerY - (promptSize.height / 2));
		closeButton = new Rectangle((anchorPoint.x + promptSize.width)-buttonHeight, anchorPoint.y,
				buttonHeight, buttonHeight);
		helpOptions = new Rectangle(anchorPoint.x, anchorPoint.y + (promptSize.height - buttonHeight),
				promptSize.width, buttonHeight);
	}

	public void drawBox() {
		Rectangle box = new Rectangle(anchorPoint.x-2, anchorPoint.y-2, promptSize.width+4, promptSize.height+4);
		g2.setColor(Color.lightGray);
		g2.fill(box);
		g2.setColor(Color.white);
		g2.setStroke(new BasicStroke(3));
		g2.draw(new Rectangle2D.Double(box.x-1.5, box.y-1.5, box.width+3, box.getHeight()+3));
		g2.setStroke(new BasicStroke());
	}

	public void drawText(List<String> lines) {
		int startX = anchorPoint.x + padding.width;
		for(String line : lines){
			drawString(line, startX, helpStringY);
			helpStringY += textHeight + padding.height;
		}
	}
	
	public void drawText(List<String> lines, String header) {
		int startX = anchorPoint.x + padding.width;
		TBSGraphics.drawCenteredString(g2, header, startX, helpStringY, 0, textHeight, TBSGraphics.emptyNodeColor);
		startX += TBSGraphics.buttonsWidth;
		for(String line : lines){
			drawString(line, startX, helpStringY);
			helpStringY += textHeight + padding.height;
		}
	}

	public void drawString(String s, int x, int y){
		drawString(s, x, y, false);
	}

	public void drawString(String s, int x, int y, boolean isSelected) {
		if(s != null && s.length() > 0)
			TBSGraphics.drawCenteredString(g2, s, x, y, 0, textHeight, 
					isSelected ? TBSGraphics.emptyNodeColor : Color.BLACK);
	}

	public void drawButtons()
	{
		TBSGraphics.renderButtonBackground(g2, closeButton, false);
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(3));
		g2.draw(closeButton);
		int x,y,w,h;
		x = closeButton.x+1;
		y = closeButton.y+1;
		w = closeButton.width-1;
		h = closeButton.height-1;
		g2.draw(new Line2D.Double(x,y,x+w,y+h));
		g2.draw(new Line2D.Double(x,y+h,x+w,y));
		g2.setStroke(new BasicStroke());
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


	public boolean isOverButton(MouseEvent e){
		if(closeButton.contains(e.getPoint()))
			return true;
		if(helpOptions.contains(e.getPoint()))
			return true;
		return false;
	}
	
	private String surveyStatus(){
		StringBuffer statusString = new StringBuffer("");
		List<String> incompletedItems = new LinkedList<String>();
		if(model.inTreeElements().isEmpty())
			incompletedItems.add("the tree");
		Student student = model.getStudent();
		for(Map.Entry<OpenQuestionButtonType, Response> response : student.getResponses().entrySet()){
			if(!response.getValue().isCompleted())
				incompletedItems.add(response.getKey().getAdminText());
		}
		if(incompletedItems.isEmpty()){
			statusString.append("Currently you have created a tree and entered responses to all the open-response questions. ");
			statusString.append("You are ready to submit your survey.");
		}else{
			if(incompletedItems.size() == 1){
				statusString.append("Currently you still need to complete ");
				statusString.append(incompletedItems.remove(0)).append(". ");
			}else if(incompletedItems.size() <= 4){
				statusString.append("Currently you still need to complete ");
				statusString.append(incompletedItems.remove(0));
				String statusEnd = incompletedItems.remove(incompletedItems.size()-1);
				for(String s : incompletedItems)
					statusString.append(", ").append(s);
				statusString.append(" & " + statusEnd + ". ");
			}
		}
		return statusString.toString();
	}

}

