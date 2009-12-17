
package tbs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import tbs.model.TBSModel;
import tbs.view.TBSPromptButtonType;
import tbs.view.TBSQuestionButtonType;

public class TBSPrompt{
	
	TBSModel model;
	TBSQuestionButtonType currentQuestion;
	Properties questionProps;
	String question = null;
	List<String> radioAnswers;
	int currentRadioQuestion;
	List<String> lineBrokenQuestion;
	List<TBSPromptButtonType> buttons;
	String userInput = null;
	Graphics2D g2 = null;
	Color promptBackground = Color.lightGray;
	Color borderColor = Color.white;
	Color buttonBorder = Color.gray;
	int paddingWidth = 10;
	int paddingHeight = 5;
	int promptWidth = 0;
	int promptHeight = 0;
	int questionStringY;
	Rectangle buttonsArea;
	int buttonHeight;
	String minString = "This determines the minimum width of a prompt";
	int textHeight;
	Point anchorPoint = null;
	boolean finished = false;
	boolean getTextInput = false;
	int numLines = 8; // number of lines of text input
	
	// if buttons != null, value of button pressed is returned
	// if buttons == null, text input is assumed
	public TBSPrompt(TBSModel model, TBSQuestionButtonType currentQuestion) {
		this.model = model;
		this.currentQuestion = currentQuestion;
		questionProps = model.getQuestionProperties();
		this.question = questionProps.getProperty(currentQuestion.getQuestionKey());
		buttons = TBSPromptButtonType.getButtons(currentQuestion.isRadio());
		getTextInput = !currentQuestion.isRadio();
		userInput = model.getQuestion(currentQuestion);
		if(currentQuestion.isRadio()){
			radioAnswers = new LinkedList<String>();
			currentRadioQuestion = 1;
			if(userInput == null || userInput == "")
				userInput = "0,0,0,0,0,0,0,0,0,0,0,0,0";
			for(String answer : userInput.split(","))
					radioAnswers.add(answer);
		}
		finished = false;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public String getUserInput() {
		if(!getTextInput){
			String radioAnswersToString = "";
			for(String answer : radioAnswers)
				radioAnswersToString += answer+",";
			return radioAnswersToString.substring(0, radioAnswersToString.length()-2);
		}
		return userInput;
	}
	
	public void mousePressed(MouseEvent e){
		calculateValues();
        if(buttonsArea.contains(e.getPoint())){
        	int index = (int) ((e.getX() - buttonsArea.getX()) * buttons.size()) / promptWidth;
        	String buttonValue = buttons.get(index).getValue();
    		if("0".equals(buttonValue))
        		finished = true;
    		else{
    			if(currentQuestion.isRadio()){
    				radioAnswers.set(currentRadioQuestion-1, buttonValue);
    				if(currentRadioQuestion == radioAnswers.size())
    					buttons = TBSPromptButtonType.getButtons(false);
    				else
    					currentRadioQuestion++;
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
			lineWidth = getStringBounds(currentLine).width;
		} else {
			lineWidth = 0;
		}
		if(lineWidth > (promptWidth - paddingWidth * 2)) {
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
		} else {
			userInput += c;
		}
		// check if max number of lines exceeded
		lines = userInput.split("\n");
		if(lines.length >= numLines) 
			userInput = userInput.substring(0 , userInput.length() - 1);
	}
	
	public void paintComponent(Graphics2D g2) {
		this.g2 = g2;
		textHeight = getStringBounds("QOgj").height;
		promptWidth = 750 + paddingWidth * 2;
		lineBrokenQuestion = new LinkedList<String>();
		int questionLength = 0;
		if(currentQuestion.isRadio()){
			breakQuestionByLine();
			questionLength = lineBrokenQuestion.size();
			constructRadioQuestions();
		}else
			breakQuestionByLine();
		calculateValues();
		drawBox();
		drawText(lineBrokenQuestion, questionLength);
		questionStringY += ((textHeight+4) * lineBrokenQuestion.size());
		if(getTextInput && userInput != null)
			drawText(Arrays.asList(userInput.split("\n")));
		drawButtons();
	}
	
	public void calculateValues() {
		int lineCount = 2 + lineBrokenQuestion.size();
		if(getTextInput)
			lineCount += numLines;
		promptHeight = (textHeight * lineCount) + (paddingHeight * (lineCount + 1));
		int centerX = model.getApplet().getWidth() / 2;
		int centerY = model.getApplet().getHeight() / 2;
		anchorPoint = new Point(centerX - (promptWidth / 2), centerY - (promptHeight / 2));
		questionStringY = anchorPoint.y;
		buttonHeight = textHeight + paddingHeight;
		buttonsArea = new Rectangle(anchorPoint.x, anchorPoint.y + (promptHeight - buttonHeight),
				promptWidth, buttonHeight);
	}
	
	public void drawBox() {
		Rectangle box = new Rectangle(anchorPoint.x-2, anchorPoint.y-2, promptWidth+4, promptHeight+4);
		g2.setColor(promptBackground);
		g2.fill(box);
		g2.setColor(borderColor);
		g2.setStroke(new BasicStroke(3));
		g2.draw(new Rectangle2D.Double(box.x-1.5, box.y-1.5, box.width+3, box.getHeight()+3));
		g2.setStroke(new BasicStroke());
	}
	public void drawText(List<String> lines){ drawText(lines, 1); }
	
	public void drawText(List<String> lines, int startAnswers) {
		int startY = questionStringY;
		for(int i=0;i<lines.size();i++){
			if(currentQuestion.isRadio() && (currentRadioQuestion+startAnswers)==(i+1) && buttons.size() > 1)
				drawString(lines.get(i), anchorPoint.x + paddingWidth, startY, true);
			else
				drawString(lines.get(i), anchorPoint.x + paddingWidth, startY);
			startY += textHeight + 4;
		}
		if(currentQuestion.isRadio()){
			startY = questionStringY + ((textHeight + 4) * startAnswers);
			for(int i=0;i<radioAnswers.size();i++){
				String answerText = TBSPromptButtonType.getRadioText(radioAnswers.get(i));
				int answerWidth = getStringBounds(answerText).width + 4;
				if(currentRadioQuestion==(i+1) && buttons.size() > 1)
					drawString(answerText, anchorPoint.x + (promptWidth-answerWidth), startY, true);
				else
					drawString(answerText, anchorPoint.x + (promptWidth-answerWidth), startY);
				startY += textHeight + 4;
			}
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
		Rectangle buttonRect = new Rectangle(buttonsArea.x, buttonsArea.y,
				buttonsArea.width/buttons.size(), buttonsArea.height);
		Color start = new Color(0.45f, 0.55f, 0.65f);
		Color end = new Color(1.0f, 1.0f, 1.0f);
		for(TBSPromptButtonType button: buttons) {
			renderButtonBackground(buttonRect, start, end);
			g2.setColor(buttonBorder);
			g2.draw(buttonRect);
			TBSGraphics.drawCenteredString(g2, button.toString(), buttonRect.x,
					buttonRect.y + (buttonRect.height - 2), buttonRect.width, 0);
			buttonRect.setLocation(buttonRect.x + buttonRect.width, buttonRect.y);
		}
	}
	
	public void renderButtonBackground(Rectangle button, Color start, Color end) {
		float redDiff = end.getRed() - start.getRed();
		float greenDiff = end.getGreen() - start.getGreen();
		float blueDiff = end.getBlue() - start.getBlue();
		for(int y = button.y; y <= button.y + button.height / 3; y++) {
			float fy = (float) (y - button.y);
			float fh = (float) button.height / 3;
			float fdiff = 0.6f + 0.4f * fy / fh;
			float red = start.getRed() + redDiff * fdiff;
			float green = start.getGreen() + greenDiff * fdiff;
			float blue = start.getBlue() + blueDiff * fdiff;
			red /= 255.0f;
			green /= 255.0f;
			blue /= 255.0f;
			g2.setColor(new Color(red, green, blue));
			g2.drawLine(button.x, y , button.x + button.width, y);
		}
		for(int y = button.y + button.height / 3; y < button.y + button.height; y++) {
			float fy = (float) y - (button.height / 3) - button.y;
			float fh = (float) 2.0f * (button.height / 3);
			float fdiff = fy / fh;
			float red = end.getRed() - redDiff * fdiff;
			float green = end.getGreen() - greenDiff * fdiff;
			float blue = end.getBlue() - blueDiff * fdiff;
			red /= 255.0f;
			green /= 255.0f;
			blue /= 255.0f;
			g2.setColor(new Color(red, green, blue));
			g2.drawLine(button.x, y , button.x + button.width, y);
		}
	}
	
	public TBSQuestionButtonType getCurrentQuestion() {
		return currentQuestion;
	}
	
	public void breakQuestionByLine(){
		String currentLine = "";
		for(String token : question.split(" ")){
			if(getStringBounds(currentLine + token).width > (promptWidth - paddingWidth * 2)){
				lineBrokenQuestion.add(currentLine);
				currentLine = token + " ";
			}else{
				currentLine += token + " ";
			}
		}
		if(currentLine.length() > 0)
			lineBrokenQuestion.add(currentLine); 
	}
	
	public void constructRadioQuestions(){
		for(int i=1;i<=radioAnswers.size(); i++)
			lineBrokenQuestion.add(i + ")" + questionProps.getProperty("questionThree"+i));
	}
	
	public Dimension getStringBounds(String s) 
	{
		if(s == null || s == "")
			return new Dimension();
		Font f = new Font(null, TBSGraphics.fontStyle, TBSGraphics.fontSize);
		FontRenderContext frc = g2.getFontRenderContext();
		TextLayout layout = new TextLayout(s, f, frc);
		Rectangle2D bounds = layout.getBounds();
		return new Dimension((int) bounds.getWidth(), (int) bounds.getHeight());
	}
	
}
