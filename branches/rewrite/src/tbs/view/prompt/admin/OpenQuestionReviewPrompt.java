
package tbs.view.prompt.admin;

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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import tbs.TBSGraphics;
import tbs.model.TBSModel;
import tbs.properties.PropertyType;
import tbs.view.OpenQuestionButtonType;
import tbs.view.prompt.Prompt;
import tbs.view.prompt.buttons.OpenQuestionPromptButtonType;

public class OpenQuestionReviewPrompt extends Prompt
{

	//Information to be used by all prompt types
	TBSModel model;
	Graphics2D g2 = null;
	Properties questionProps;
	
	//Prompt sizing information
	Dimension padding = new Dimension(10,5);
	Dimension promptSize = new Dimension(820,0);
	Point anchorPoint = null;
	int responseStringY;
	Rectangle closeButton;
	Rectangle questionButtons;
	OpenQuestionButtonType[] buttons = OpenQuestionButtonType.values();
	OpenQuestionButtonType currentQuestion;
	int buttonHeight;
	int textHeight = -1;
	Map<OpenQuestionButtonType, String> writtenAnswers;
	List<String[]> questionThree;
	
	public OpenQuestionReviewPrompt(TBSModel model) {
		super();
		this.model = model;
		questionProps = model.getProperties(PropertyType.QUESTIONS);
		writtenAnswers = new HashMap<OpenQuestionButtonType, String>();
		writtenAnswers.put(OpenQuestionButtonType.ONE, model.getQuestion(OpenQuestionButtonType.ONE));
		writtenAnswers.put(OpenQuestionButtonType.TWO, model.getQuestion(OpenQuestionButtonType.TWO));
		List<String> radioAnswers = new LinkedList<String>();
		String answerText = model.getQuestion(OpenQuestionButtonType.THREE);
		int numRadios = 13;//Default number of radio questions
		String numRadiosString = questionProps.getProperty("questionThree.numQuestions");
		try{
			numRadios = Integer.parseInt(numRadiosString);
		} catch(NumberFormatException e){
			System.out.println("OpenQuestionReviewPrompt:Error parsing radio question count(value-" + numRadiosString + ") from questions.properties");
		}
		if(answerText == null || answerText == "")
			answerText = "0,0,0,0,0,0,0,0,0,0,0,0,0";
		List<String> answerTextVals = new LinkedList<String>();
		for(String answer : answerText.split(","))
			answerTextVals.add(answer);
		if(answerTextVals.size() < numRadios){
			for(int i=answerTextVals.size();i<=numRadios;i++)
				answerTextVals.add("0");
		}else if(answerTextVals.size() > numRadios){
			while(answerTextVals.size() != numRadios)
				answerTextVals.remove(answerTextVals.size()-1);
		}
		int answerNum;
		OpenQuestionPromptButtonType[] radioButtons = OpenQuestionPromptButtonType.values();
		for(String answerTextVal : answerTextVals){
			answerNum = 0;
			try{
				answerNum = Integer.parseInt(answerTextVal);
			}catch(NumberFormatException e){
				System.out.println("OpenQuestionReviewPrompt:Number format exception for answer text: " + answerTextVal);
			}
			radioAnswers.add(radioButtons[answerNum].getText());
		}
		questionThree = new LinkedList<String[]>();
		int index = model.hasArrows() ? 1 : radioAnswers.size()-1;
		String[] radioPair;
		for(String answer : radioAnswers){
			radioPair = new String[2];
			radioPair[0] = questionProps.getProperty("questionThree"+index);
			radioPair[1] = answer;
			questionThree.add(radioPair);
			if(model.hasArrows())
				index++;
			else
				index--;
		}
		currentQuestion = OpenQuestionButtonType.ONE;
		
	}


	public void keyPressed(KeyEvent e){}

	public void keyTyped(KeyEvent e){}

	public void mousePressed(MouseEvent e) {
		if(closeButton.contains(e.getPoint()))
				setFinished(true);
		else{
			if(questionButtons.contains(e.getPoint())){
	        	int index = (int) ((e.getX() - questionButtons.getX()) * buttons.length) / promptSize.width;
	        	currentQuestion = buttons[index];
	        }
		}
	}

	public void paintComponent(Graphics2D g2) 
	{
		this.g2 = g2;
		if(textHeight == -1)
			textHeight = TBSGraphics.getStringBounds(g2,"QOgj").height;
		List<String> writtenQuestionText = new LinkedList<String>();
		List<String> writtenAnswerText = new LinkedList<String>();
		int writtenLines = 0;
		if(!currentQuestion.isRadio()){
			String answerText = writtenAnswers.get(currentQuestion);
			String questionText = questionProps.getProperty(currentQuestion.getQuestionKey());
			writtenQuestionText = TBSGraphics.breakStringByLineWidth(g2,questionText,
					promptSize.width - padding.width * 2);
			writtenLines += writtenQuestionText.size();
			writtenAnswerText = TBSGraphics.breakStringByLineWidth(g2,answerText,
					promptSize.width - padding.width * 2);
			writtenLines += writtenAnswerText.size();
			calculateValues(writtenLines + 2);
		}else
			calculateValues(questionThree.size() + 2);
		drawBox();
		drawButtons();
		
		responseStringY = anchorPoint.y;
		TBSGraphics.drawCenteredString(g2,"Open Responses - " + currentQuestion.getAdminText(),
				anchorPoint.x + padding.width, responseStringY,
				promptSize.width - padding.width * 2,
				buttonHeight,TBSGraphics.emptyNodeColor);
		responseStringY += buttonHeight;
		
		if(!currentQuestion.isRadio()){
			drawWritten(writtenQuestionText, false);
			drawWritten(writtenAnswerText, true);
		}else
			drawRadio(questionThree);
	}

	public void calculateValues(int lineCount) {
		buttonHeight = textHeight + padding.height;
		promptSize.setSize(promptSize.width, (textHeight * lineCount) + 
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
			responseStringY += textHeight + padding.height;
		}
	}
	
	public void drawRadio(List<String[]> lines) {
		int questionX = anchorPoint.x + padding.width;
		int answerX = anchorPoint.x + promptSize.width;
		for(String[] line : lines){
			drawString(line[0], questionX, responseStringY);
			int answerWidth = TBSGraphics.getStringBounds(g2,line[1]).width + 4;
			drawString(line[1], answerX-answerWidth, responseStringY, true);
			responseStringY += textHeight + padding.height;
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
		
		Rectangle buttonRect = new Rectangle(questionButtons.x, questionButtons.y,
				questionButtons.width/buttons.length, questionButtons.height);
		for(OpenQuestionButtonType button: buttons) {
			if(button.equals(currentQuestion))
				TBSGraphics.renderButtonBackground(g2, buttonRect, true);
			else
				TBSGraphics.renderButtonBackground(g2, buttonRect, false);
			g2.setColor(Color.gray);
			g2.draw(buttonRect);
			TBSGraphics.drawCenteredString(g2, button.getAdminText(), buttonRect.x,
					buttonRect.y + (buttonRect.height - 2), buttonRect.width, 0);
			buttonRect.setLocation(buttonRect.x + buttonRect.width, buttonRect.y);
		}
	}


	public boolean isOverButton(MouseEvent e){
		return (closeButton.contains(e.getPoint()) || questionButtons.contains(e.getPoint()));
	}

}

