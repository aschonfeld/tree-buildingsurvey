
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import tbs.TBSGraphics;
import tbs.model.TBSModel;
import tbs.model.admin.RadioResponse;
import tbs.properties.PropertyLoader;
import tbs.view.OpenQuestionButtonType;
import tbs.view.prompt.Prompt;
import tbs.view.prompt.buttons.OpenQuestionPromptButtonType;

public class RadioQuestionPrompt extends Prompt{

	//Information to be used by all prompt types
	TBSModel model;
	Graphics2D g2 = null;

	Properties questionProps;
	ArrayList<String> userInputLines;
	RadioResponse response;
	int questionCount;

	//Prompt sizing information
	List<String> lineBrokenQuestion;
	List<OpenQuestionPromptButtonType> buttons;
	int numLines = 8; // number of lines of text input
	Dimension padding = new Dimension(10,5);
	Dimension promptSize = new Dimension(770,0);
	int width = 750;
	Point anchorPoint = null;
	int questionStringY;
	Rectangle buttonsArea;
	Rectangle closeButton;
	int buttonHeight;
	
	//Question Text
	List<String> questionText;
	List<String[]> radioText;

	//Question 3(Radio) Properties
	OpenQuestionButtonType currentRadioQuestion;
	int currentRadioSubQuestion;
	Rectangle radioQuestionSelection;

	// if buttons != null, value of button pressed is returned
	// if buttons == null, text input is assumed
	public RadioQuestionPrompt(TBSModel model) {
		super();
		this.model = model;
		buttons = OpenQuestionPromptButtonType.getRadioButtons();
		questionProps = PropertyLoader.getProperties("questions");
		questionText = new LinkedList<String>();
		radioText = new LinkedList<String[]>();
		questionCount = 0;
	}

	public void mousePressed(MouseEvent e){
		if(buttonsArea.contains(e.getPoint())){
			int index = (int) ((e.getX() - buttonsArea.getX()) * buttons.size()) / promptSize.width;
			OpenQuestionPromptButtonType buttonClicked = buttons.get(index);
			if(OpenQuestionPromptButtonType.SUBMIT.equals(buttonClicked)){
				List<OpenQuestionButtonType> radioQuestions = OpenQuestionButtonType.getRadioButtons();
				if(currentRadioQuestion.ordinal() == radioQuestions.size()-1)
					setFinished(true);
				else
					setCurrentQuestion(radioQuestions.get(currentRadioQuestion.ordinal()+1));
			}else{
				model.getStudent().getResponse(currentRadioQuestion).updateText(currentRadioSubQuestion, buttons.get(index));
				if(currentRadioSubQuestion == (questionCount-1))
					buttons = OpenQuestionPromptButtonType.getWrittenButtons();
				currentRadioSubQuestion++;
			}
		}else if(closeButton.contains(e.getPoint()))
			setFinished(true);
		else{
			if(radioQuestionSelection.contains(e.getPoint())){
				if(currentRadioSubQuestion == questionCount)
					buttons = OpenQuestionPromptButtonType.getRadioButtons();
				currentRadioSubQuestion = ((int) ((e.getY() - radioQuestionSelection.getY()) * questionCount) / radioQuestionSelection.height);
			}
		}
	}

	public void paintComponent(Graphics2D g2) {
		this.g2 = g2;
		lineBrokenQuestion = new LinkedList<String>();
		List<String> text = new LinkedList<String>();
		int totalLines = 0;
		if(questionText.size() == 0){
			text = TBSGraphics.breakStringByLineWidth(g2,
					questionProps.getProperty(currentRadioQuestion.getQuestionKey()),width);
			questionText = text;
		}else
			text = questionText;
		totalLines = text.size() + 3 + radioText.size();
		if(radioText.size() == 0){
			int index = model.getStudent().hasArrows() ? 1 : questionCount;
			String[] radioPair;
			for(int i=0;i<questionCount;i++){
				radioPair = new String[2];
				radioPair[0] = questionProps.getProperty(currentRadioQuestion.getQuestionKey()+index);
				radioPair[1] = "";
				radioText.add(radioPair);
				if(model.getStudent().hasArrows())
					index++;
				else
					index--;
			}
		}
		List<OpenQuestionPromptButtonType> radioAnswers = response.getRadioAnswers();
		for(int i=0;i<radioAnswers.size();i++)
			radioText.get(i)[1] = radioAnswers.get(i).getText();
		calculateValues(totalLines);
		drawBox();
		TBSGraphics.drawCloseButton(g2, closeButton);
		drawButtons();

		questionStringY = anchorPoint.y;
		TBSGraphics.drawCenteredString(g2,
				new StringBuffer("Open Response - ").append(currentRadioQuestion.getAdminText()).toString(),
				anchorPoint.x + padding.width, questionStringY,width,
				buttonHeight,TBSGraphics.selectedPromptTextColor);
		questionStringY += buttonHeight;

		drawWritten(text);
		questionStringY += TBSGraphics.textHeight + padding.height;
		radioQuestionSelection = new Rectangle(anchorPoint.x + padding.width, questionStringY,
				TBSGraphics.questionButtonsWidth, response.getQuestionCount() * (TBSGraphics.textHeight + padding.height));
		drawRadioSelectionButtons();
		drawRadio(radioText);
	}

	public void calculateValues(int lineCount) {
		buttonHeight = TBSGraphics.textHeight + padding.height;
		promptSize.setSize(promptSize.width, (TBSGraphics.textHeight * lineCount) +
				(padding.height * (lineCount + 1))  + buttonHeight);
		int centerX = model.getApplet().getWidth() / 2;
		int centerY = model.getApplet().getHeight() / 2;
		anchorPoint = new Point(centerX - (promptSize.width / 2), centerY - (promptSize.height / 2));
		buttonHeight = TBSGraphics.textHeight + padding.height;
		closeButton = new Rectangle((anchorPoint.x + promptSize.width)-buttonHeight, anchorPoint.y,
				buttonHeight, buttonHeight);
		buttonsArea = new Rectangle(anchorPoint.x, anchorPoint.y + (promptSize.height - buttonHeight),
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

	public void drawWritten(List<String> lines) {
		int startX = anchorPoint.x + padding.width;
		for(String line : lines){
			drawString(line, startX, questionStringY);
			questionStringY += TBSGraphics.textHeight + padding.height;
		}
	}

	public void drawRadio(List<String[]> lines) {
		int questionX = anchorPoint.x + TBSGraphics.questionButtonsWidth + (padding.width*2);
		int answerX = anchorPoint.x + promptSize.width;
		boolean selected;
		int i = 0;
		for(String[] line : lines){
			selected = currentRadioSubQuestion == i && buttons.size() > 1;
			drawString(line[0], questionX, questionStringY, selected);
			int answerWidth = TBSGraphics.getStringBounds(g2,line[1]).width + 4;
			drawString(line[1], answerX-answerWidth, questionStringY, selected);
			questionStringY += TBSGraphics.textHeight + padding.height;
			i++;
		}
	}

	public void drawString(String s, int x, int y){
		drawString(s, x, y, false);
	}

	public void drawString(String s, int x, int y, boolean isSelected) {
		if(s != null && s.length() > 0)
			TBSGraphics.drawCenteredString(g2, s, x, y, 0, TBSGraphics.textHeight + 4, isSelected ? TBSGraphics.selectedPromptTextColor : Color.BLACK);
	}

	public void drawButtons()
	{
		Rectangle buttonRect = new Rectangle(buttonsArea.x, buttonsArea.y,
				buttonsArea.width/buttons.size(), buttonsArea.height);
		for(OpenQuestionPromptButtonType button: buttons) {
			TBSGraphics.renderButtonBackground(g2, buttonRect, false);
			g2.setColor(Color.gray);
			g2.draw(buttonRect);
			TBSGraphics.drawCenteredString(g2, button.toString(), buttonRect.x,
					buttonRect.y + (buttonRect.height - 2), buttonRect.width, 0);
			buttonRect.setLocation(buttonRect.x + buttonRect.width, buttonRect.y);
		}
	}

	public void drawRadioSelectionButtons(){
		Rectangle buttonRect = new Rectangle(radioQuestionSelection.x, radioQuestionSelection.y,
				radioQuestionSelection.width, radioQuestionSelection.height/questionCount);
		for(int i=0;i<questionCount;i++){
			if(i == currentRadioSubQuestion) 
				TBSGraphics.renderButtonBackground(g2, buttonRect, true);
			else
				TBSGraphics.renderButtonBackground(g2, buttonRect, false);
			g2.setColor(Color.gray);
			g2.draw(buttonRect);
			TBSGraphics.drawCenteredString(g2,new StringBuffer(i+1).toString(), buttonRect.x,
					buttonRect.y + (buttonRect.height - 2), buttonRect.width, 0);
			buttonRect.setLocation(buttonRect.x, buttonRect.y + (radioQuestionSelection.height/questionCount));
		}
	}

	public void setCurrentQuestion(OpenQuestionButtonType currentQuestion) {
		currentRadioQuestion = currentQuestion;
		questionText = new LinkedList<String>();
		response = (RadioResponse) model.getStudent().getResponse(currentRadioQuestion);
		questionCount = response.getQuestionCount();
		currentRadioSubQuestion = 0;
	}

	public boolean isOverButton(MouseEvent e){
		if(buttonsArea.contains(e.getPoint()))
			return true;
		if(closeButton.contains(e.getPoint()))
			return true;
		return radioQuestionSelection.contains(e.getPoint());
	}

	public void keyPressed( KeyEvent e ) {}
	public void keyTyped( KeyEvent e ) {}

}
