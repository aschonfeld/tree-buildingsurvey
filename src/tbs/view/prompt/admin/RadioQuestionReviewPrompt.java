
package tbs.view.prompt.admin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import tbs.TBSGraphics;
import tbs.model.AdminModel;
import tbs.model.admin.RadioResponse;
import tbs.properties.PropertyLoader;
import tbs.view.OpenQuestionButtonType;
import tbs.view.prompt.Prompt;
import tbs.view.prompt.buttons.OpenQuestionPromptButtonType;

public class RadioQuestionReviewPrompt extends Prompt
{

	//Information to be used by all prompt types
	AdminModel model;
	Graphics2D g2 = null;
	Properties questionProps;
	
	//Prompt sizing information
	Dimension padding = new Dimension(10,5);
	Dimension promptSize = new Dimension(820,0);
	Point anchorPoint = null;
	int responseStringY;
	Rectangle closeButton;
	Rectangle questionButtons;
	int buttonHeight;
	List<OpenQuestionButtonType> radioQuestions = OpenQuestionButtonType.getRadioButtons();
	OpenQuestionButtonType currentRadioQuestion;
	Map<OpenQuestionButtonType, List<String[]>> radioQuestionTexts;
	
	public RadioQuestionReviewPrompt(AdminModel model) {
		super();
		this.model = model;
		radioQuestions = OpenQuestionButtonType.getRadioButtons();
		currentRadioQuestion = radioQuestions.get(0);
		questionProps = PropertyLoader.getProperties("questions");
		
		radioQuestionTexts = new HashMap<OpenQuestionButtonType, List<String[]>>();
		List<String[]> radioQuestionText;
		for(OpenQuestionButtonType radioQuestion : radioQuestions){
			radioQuestionText = new LinkedList<String[]>();
			int index = model.getStudent().hasArrows() ? 1 : radioQuestion.getRadioQuestionCount();
			String[] radioPair;
			RadioResponse radioResponse = (RadioResponse) model.getStudent().getResponse(currentRadioQuestion);
			List<OpenQuestionPromptButtonType> radioAnswers = radioResponse.getRadioAnswers();
			for(OpenQuestionPromptButtonType answer : radioAnswers){
				radioPair = new String[2];
				radioPair[0] = questionProps.getProperty(radioQuestion.getQuestionKey()+index);
				radioPair[1] = answer.getText();
				radioQuestionText.add(radioPair);
				if(model.getStudent().hasArrows())
					index++;
				else
					index--;
			}
			radioQuestionTexts.put(radioQuestion, radioQuestionText);
		}		
	}


	public void keyPressed(KeyEvent e){}

	public void keyTyped(KeyEvent e){}

	public void mousePressed(MouseEvent e) {
		if(closeButton.contains(e.getPoint()))
				setFinished(true);
		else{
			if(questionButtons.contains(e.getPoint())){
	        	int index = (int) ((e.getX() - questionButtons.getX()) * radioQuestions.size()) / promptSize.width;
	        	currentRadioQuestion = radioQuestions.get(index);
	        }
		}
	}

	public void paintComponent(Graphics2D g2) 
	{
		this.g2 = g2;
		List<String[]> radioQuestionText = radioQuestionTexts.get(currentRadioQuestion);
		calculateValues(radioQuestionText.size() + 2);
		drawBox();
		TBSGraphics.drawCloseButton(g2, closeButton);
		drawButtons();
		
		responseStringY = anchorPoint.y;
		TBSGraphics.drawCenteredString(g2,
				new StringBuffer("Open Responses - ").append(currentRadioQuestion.getAdminText()).toString(),
				anchorPoint.x + padding.width, responseStringY,
				promptSize.width - padding.width * 2,
				buttonHeight,TBSGraphics.emptyNodeColor);
		responseStringY += buttonHeight;
		
		drawRadio(radioQuestionText);
	}

	public void calculateValues(int lineCount) {
		buttonHeight = TBSGraphics.textHeight + padding.height;
		promptSize.setSize(promptSize.width, (TBSGraphics.textHeight * lineCount) + 
				(padding.height * (lineCount + 1)) + buttonHeight);
		int centerX = model.getApplet().getWidth() / 2;
		int centerY = model.getApplet().getHeight() / 2;
		anchorPoint = new Point(centerX - (promptSize.width / 2), 
				centerY - (promptSize.height / 2));
		questionButtons = new Rectangle(anchorPoint.x, anchorPoint.y + (promptSize.height - buttonHeight),
				promptSize.width, buttonHeight);
		closeButton = new Rectangle((anchorPoint.x + promptSize.width)-buttonHeight, anchorPoint.y,
				buttonHeight, buttonHeight);
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

	public void drawWritten(List<String> lines, boolean answer) {
		int startX = anchorPoint.x + padding.width;
		for(String line : lines){
			drawString(line, startX, responseStringY,  answer);
			responseStringY += TBSGraphics.textHeight + padding.height;
		}
	}
	
	public void drawRadio(List<String[]> lines) {
		int questionX = anchorPoint.x + padding.width;
		int answerX = anchorPoint.x + promptSize.width;
		for(String[] line : lines){
			drawString(line[0], questionX, responseStringY);
			int answerWidth = TBSGraphics.getStringBounds(g2,line[1]).width + 4;
			drawString(line[1], answerX-answerWidth, responseStringY, true);
			responseStringY += TBSGraphics.textHeight + padding.height;
		}
	}
	
	

	public void drawString(String s, int x, int y){
		drawString(s, x, y, false);
	}

	public void drawString(String s, int x, int y, boolean isSelected) {
		if(s != null && s.length() > 0)
			TBSGraphics.drawCenteredString(g2, s, x, y, 0, TBSGraphics.textHeight, 
					isSelected ? TBSGraphics.emptyNodeColor : Color.BLACK);
	}

	public void drawButtons()
	{
		Rectangle buttonRect = new Rectangle(questionButtons.x, questionButtons.y,
				questionButtons.width/radioQuestions.size(), questionButtons.height);
		for(OpenQuestionButtonType radioQuestion: radioQuestions) {
			if(radioQuestion.equals(currentRadioQuestion))
				TBSGraphics.renderButtonBackground(g2, buttonRect, true);
			else
				TBSGraphics.renderButtonBackground(g2, buttonRect, false);
			g2.setColor(Color.gray);
			g2.draw(buttonRect);
			TBSGraphics.drawCenteredString(g2, radioQuestion.getAdminText(), buttonRect.x,
					buttonRect.y + (buttonRect.height - 2), buttonRect.width, 0);
			buttonRect.setLocation(buttonRect.x + buttonRect.width, buttonRect.y);
		}
	}


	public boolean isOverButton(MouseEvent e){
		return (closeButton.contains(e.getPoint()) || questionButtons.contains(e.getPoint()));
	}

}

