
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import tbs.TBSGraphics;
import tbs.model.TBSModel;
import tbs.model.admin.RadioResponse;
import tbs.properties.PropertyType;
import tbs.view.OpenQuestionButtonType;
import tbs.view.prompt.Prompt;
import tbs.view.prompt.buttons.OpenQuestionPromptButtonType;

public class OpenQuestionPrompt extends Prompt{
	
	//Information to be used by all prompt types
	TBSModel model;
	Graphics2D g2 = null;
	Properties questionProps;
	OpenQuestionButtonType currentQuestion;
	String userInput = "";
	
	//Prompt sizing information
	List<String> lineBrokenQuestion;
	List<OpenQuestionPromptButtonType> buttons;
	int numLines = 8; // number of lines of text input
	Dimension padding = new Dimension(10,5);
	Dimension promptSize = new Dimension(770,0);
	Point anchorPoint = null;
	int questionStringY;
	Rectangle buttonsArea;
	Rectangle closeButton;
	int buttonHeight;
	int textHeight;
	
	//Question Text
	Map<OpenQuestionButtonType, List<String>> questionTexts;
	
	//Question 3(Radio) Properties
	int currentRadioQuestion;
	Rectangle radioQuestionSelection;
	
	// if buttons != null, value of button pressed is returned
	// if buttons == null, text input is assumed
	public OpenQuestionPrompt(TBSModel model) {
		super();
		this.model = model;
		questionProps = model.getProperties(PropertyType.QUESTIONS);
		questionTexts = new HashMap<OpenQuestionButtonType, List<String>>();
	}
	
	public void mousePressed(MouseEvent e){
		if(buttonsArea.contains(e.getPoint())){
        	int index = (int) ((e.getX() - buttonsArea.getX()) * buttons.size()) / promptSize.width;
        	OpenQuestionPromptButtonType buttonClicked = buttons.get(index);
        	if(OpenQuestionPromptButtonType.SUBMIT.equals(buttonClicked)){
    			if(!currentQuestion.isRadio()){
    				model.getStudent().getResponse(currentQuestion).updateText(userInput);
    				setCurrentQuestion(OpenQuestionButtonType.values()[currentQuestion.ordinal()+1]);
    			}else
    				setFinished(true);
    		}else{
    			if(currentQuestion.isRadio()){
    				model.getStudent().getResponse(currentQuestion).updateText(currentRadioQuestion, buttons.get(index));
    				if(currentRadioQuestion == (TBSGraphics.numberOfRadioQuestions-1))
    					buttons = OpenQuestionPromptButtonType.getButtons(false);
    				currentRadioQuestion++;
    			}
    		}
        }else if(closeButton.contains(e.getPoint()))
        	setFinished(true);
        else{
        	if(currentQuestion.isRadio()){
        		if(radioQuestionSelection.contains(e.getPoint())){
        			if(currentRadioQuestion == TBSGraphics.numberOfRadioQuestions)
    					buttons = OpenQuestionPromptButtonType.getButtons(true);
        			currentRadioQuestion = ((int) ((e.getY() - radioQuestionSelection.getY()) * TBSGraphics.numberOfRadioQuestions) / radioQuestionSelection.height);
        		}
        	}
        }
	}

	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_DELETE) {
			if(userInput.length() > 0)
				userInput = userInput.substring(0 , userInput.length() - 1);
		}
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			//String[] lines = userInput.split("\n");
			//if(lines.length < numLines - 1) userInput += "\n";
		}
	}
	
	public void keyTyped(KeyEvent e) {
		int lineWidth = 0;
		char c = e.getKeyChar();
		String[] lines = userInput.split("\n");
		String currentLine = lines[lines.length - 1];
		if(currentLine.length() > 0) {
			lineWidth = TBSGraphics.getStringBounds(g2,currentLine).width;
		} else {
			lineWidth = 0;
		}
		if(lineWidth > (promptSize.width - padding.width * 2)) {
			// automatically insert new line for long lines if enough room
			if(lines.length < numLines - 1) {
				if(c != '\b') userInput += "\n";
			} else {
				// out of space
				return;
			}
		}
		if(c == '\b'){
			if(userInput.length() > 0)
				userInput = userInput.substring(0 , userInput.length() - 1);
		} else
			userInput += c;
		// check if max number of lines exceeded
		lines = userInput.split("\n");
		if(lines.length >= numLines) 
			userInput = userInput.substring(0 , userInput.length() - 1);
	}
	
	public void paintComponent(Graphics2D g2) {
		this.g2 = g2;
		textHeight = TBSGraphics.getStringBounds(g2,"QOgj").height;
		lineBrokenQuestion = new LinkedList<String>();
		List<String> text = new LinkedList<String>();
		List<String[]> radioText = new LinkedList<String[]>();
		int totalLines = 0;
		if(!currentQuestion.isRadio()){
			if(!questionTexts.containsKey(currentQuestion)){
				text = TBSGraphics.breakStringByLineWidth(g2,
						questionProps.getProperty(currentQuestion.getQuestionKey()),
						promptSize.width - (padding.width * 2));
				questionTexts.put(currentQuestion, text);
			}else
				text = questionTexts.get(currentQuestion);
			totalLines = text.size() + 3 + numLines;
		}else{
			if(!questionTexts.containsKey(currentQuestion)){
				text = TBSGraphics.breakStringByLineWidth(g2,
						questionProps.getProperty(currentQuestion.getQuestionKey()),
						promptSize.width - (padding.width * 2));
				questionTexts.put(currentQuestion, text);
			}else
				text = questionTexts.get(currentQuestion);
			totalLines = text.size() + 3;
			radioText = new LinkedList<String[]>();
			int index = model.getStudent().hasArrows() ? 1 : TBSGraphics.numberOfRadioQuestions-1;
			String[] radioPair;
			RadioResponse response = (RadioResponse) model.getStudent().getResponse(OpenQuestionButtonType.THREE);
			List<OpenQuestionPromptButtonType> radioAnswers = response.getRadioAnswers();
			for(OpenQuestionPromptButtonType answer : radioAnswers){
				radioPair = new String[2];
				radioPair[0] = questionProps.getProperty("questionThree"+index);
				radioPair[1] = answer.getText();
				radioText.add(radioPair);
				if(model.getStudent().hasArrows())
					index++;
				else
					index--;
			}
			totalLines += radioText.size();
		}		
		calculateValues(totalLines);
		drawBox();
		drawButtons();
		
		questionStringY = anchorPoint.y;
		TBSGraphics.drawCenteredString(g2,"Open Response - " + currentQuestion.getAdminText(),
				anchorPoint.x + padding.width, questionStringY,
				promptSize.width - padding.width * 2,
				buttonHeight,TBSGraphics.emptyNodeColor);
		questionStringY += buttonHeight;
		
		drawWritten(text);
		questionStringY += textHeight + padding.height;
		if(currentQuestion.isRadio()){
			radioQuestionSelection = new Rectangle(anchorPoint.x + padding.width, questionStringY,
					TBSGraphics.questionButtonsWidth, TBSGraphics.numberOfRadioQuestions * (textHeight + padding.height));
			drawRadioSelectionButtons();
			drawRadio(radioText);
		}else{
			if(userInput != null)
				drawWritten(Arrays.asList(userInput.split("\n")));
		}
	}
	
	public void calculateValues(int lineCount) {
		buttonHeight = textHeight + padding.height;
		promptSize.setSize(promptSize.width, (textHeight * lineCount) +
				(padding.height * (lineCount + 1))  + buttonHeight);
		int centerX = model.getApplet().getWidth() / 2;
		int centerY = model.getApplet().getHeight() / 2;
		anchorPoint = new Point(centerX - (promptSize.width / 2), centerY - (promptSize.height / 2));
		buttonHeight = textHeight + padding.height;
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
			questionStringY += textHeight + padding.height;
		}
	}
	
	public void drawRadio(List<String[]> lines) {
		int questionX = anchorPoint.x + TBSGraphics.questionButtonsWidth + (padding.width*2);
		int answerX = anchorPoint.x + promptSize.width;
		boolean selected;
		int i = 0;
		for(String[] line : lines){
			selected = currentRadioQuestion == i && buttons.size() > 1;
			drawString(line[0], questionX, questionStringY, selected);
			int answerWidth = TBSGraphics.getStringBounds(g2,line[1]).width + 4;
			drawString(line[1], answerX-answerWidth, questionStringY, selected);
			questionStringY += textHeight + padding.height;
			i++;
		}
	}
	
	public void drawString(String s, int x, int y){
		drawString(s, x, y, false);
	}
	
	public void drawString(String s, int x, int y, boolean isSelected) {
		if(s != null && s.length() > 0)
			TBSGraphics.drawCenteredString(g2, s, x, y, 0, textHeight + 4, isSelected ? TBSGraphics.emptyNodeColor : Color.BLACK);
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
				radioQuestionSelection.width, radioQuestionSelection.height/TBSGraphics.numberOfRadioQuestions);
		for(int i=0;i<TBSGraphics.numberOfRadioQuestions;i++){
			if(i == currentRadioQuestion) 
				TBSGraphics.renderButtonBackground(g2, buttonRect, true);
			else
				TBSGraphics.renderButtonBackground(g2, buttonRect, false);
			g2.setColor(Color.gray);
			g2.draw(buttonRect);
			TBSGraphics.drawCenteredString(g2, "" + (i+1), buttonRect.x,
					buttonRect.y + (buttonRect.height - 2), buttonRect.width, 0);
			buttonRect.setLocation(buttonRect.x, buttonRect.y + (radioQuestionSelection.height/TBSGraphics.numberOfRadioQuestions));
		}
	}
	
	public OpenQuestionButtonType getCurrentQuestion() {
		return currentQuestion;
	}
	
	public void setCurrentQuestion(OpenQuestionButtonType currentQuestion) {
		this.currentQuestion = currentQuestion;
		buttons = OpenQuestionPromptButtonType.getButtons(currentQuestion.isRadio());
		userInput = model.getStudent().getResponse(currentQuestion).getText();
		if(currentQuestion.isRadio())
			currentRadioQuestion = 0;
	}
	
	public boolean isOverButton(MouseEvent e){
		if(buttonsArea.contains(e.getPoint()))
			return true;
		if(closeButton.contains(e.getPoint()))
			return true;
		if(currentQuestion.isRadio())
			return radioQuestionSelection.contains(e.getPoint());
		return false;
	}
	
}
