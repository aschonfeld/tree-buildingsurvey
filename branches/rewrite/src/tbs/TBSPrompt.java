
package tbs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
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
	List<String> radioQuestions;
	int totalRadioQuestions;
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
	Rectangle2D buttonsArea;
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
		if(currentQuestion.isRadio()){
			totalRadioQuestions = Integer.parseInt(questionProps.getProperty("questionThree.numQuestions"));
			currentRadioQuestion = 1;
			radioQuestions = new LinkedList<String>();
		}
		getTextInput = !currentQuestion.isRadio();
		userInput = model.getQuestion(currentQuestion);
		finished = false;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public String getUserInput() {
		if(!getTextInput){
			String radioAnswers = "";
			for(String answer : radioQuestions)
				radioAnswers += answer+",";
			return radioAnswers.substring(0, radioAnswers.length()-2);
		}
		return userInput;
	}
	
	public void mousePressed(MouseEvent e){
		calculateValues();
        if(buttonsArea.contains(e.getPoint())){
        		if(!currentQuestion.isRadio())
        			finished = true;
        		if(getTextInput) 
        			return;
        		int index = ((e.getX() - anchorPoint.x) * buttons.size()) / (promptWidth + 1);
        		radioQuestions.add("" + buttons.get(index).getValue());
        		if(currentQuestion.isRadio() && buttons.size() == 1)
        			finished = true;
        		else if(currentRadioQuestion == totalRadioQuestions)
        			buttons = TBSPromptButtonType.getButtons(false);
        		else
        			currentRadioQuestion++;
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
			lineWidth = (int) getStringBounds(currentLine).getWidth();
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
	
	public void calculateValues() {
		int minWidth = 750;
		promptWidth = minWidth + paddingWidth * 2;
		if(!getTextInput)
			promptHeight = textHeight * (2 + lineBrokenQuestion.size()) + paddingHeight * (3 + lineBrokenQuestion.size());
		else
			promptHeight = textHeight * (2 + numLines + lineBrokenQuestion.size()) + paddingHeight * (3 + numLines + lineBrokenQuestion.size());
		int centerX = model.getApplet().getWidth() / 2;
		int centerY = model.getApplet().getHeight() / 2;
		anchorPoint = new Point(centerX - promptWidth / 2, centerY - promptHeight / 2);
		questionStringY = anchorPoint.y;
		buttonsArea = new Rectangle2D.Double(anchorPoint.x,
				anchorPoint.y + promptHeight - (textHeight + paddingHeight),
				promptWidth,
				anchorPoint.y + promptHeight);
	}
	
	public void paintComponent(Graphics2D g2) {
		this.g2 = g2;
		textHeight = (int) getStringBounds("QOgj").getHeight();
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
		renderButtons();
	}
	
	public void drawBox() {
		Rectangle2D box = new Rectangle2D.Double(anchorPoint.x-2, anchorPoint.y-2, promptWidth+4, promptHeight+4);
		g2.setColor(promptBackground);
		g2.fill(box);
		g2.setColor(borderColor);
		g2.setStroke(new BasicStroke(3));
		g2.draw(new Rectangle2D.Double(box.getX()-1.5, box.getY()-1.5, box.getWidth()+3, box.getHeight()+3));
		g2.setStroke(new BasicStroke());
	}
	public void drawText(List<String> lines){ drawText(lines, 1); }
	
	public void drawText(List<String> lines, int startAnswers) {
		int startY = questionStringY;
		for(String line : lines){
			drawString(line, anchorPoint.x + paddingWidth, startY);
			startY += textHeight + 4;
		}
		if(currentQuestion.isRadio()){
			startY = questionStringY + ((textHeight + 4) * startAnswers);
			for(String answer : radioQuestions){
				String answerText = TBSPromptButtonType.getRadioText(Integer.parseInt(answer));
				int answerWidth = (int) getStringBounds(answerText).getWidth() + 4;
				drawString(answerText, anchorPoint.x + (promptWidth-answerWidth), startY);
				startY += textHeight + 4;
			}
		}
	}
	
	public Rectangle2D getStringBounds(String s) 
	{
		if(s == null || s == "")
			return new Rectangle2D.Double();
		Font f = new Font(null, TBSGraphics.fontStyle, TBSGraphics.fontSize);
		FontRenderContext frc = g2.getFontRenderContext();
		TextLayout layout = new TextLayout(s, f, frc);
		return layout.getBounds();
	}
	
	public void drawString(String s, int x, int y) {
		if(s != null && s.length() > 0)
			TBSGraphics.drawCenteredString(g2, s, x, y, 0, textHeight + 4);
	}
	
	public void renderButtons()
	{
		int leftX = anchorPoint.x;
		int height = textHeight + paddingHeight;
		int buttonStringY = anchorPoint.y + promptHeight - height;
		int width = promptWidth / buttons.size();
		int upperY = buttonStringY + height - 2;
		Color start = new Color(0.45f, 0.55f, 0.65f);
		Color end = new Color(1.0f, 1.0f, 1.0f);
		for(TBSPromptButtonType button: buttons) {
			renderButtonBackground(g2, leftX, buttonStringY, width, height, start, end);
			g2.setColor(buttonBorder);
			g2.drawRect(leftX, buttonStringY, width, height);
			TBSGraphics.drawCenteredString(g2, button.toString(), leftX, upperY, width, 0);
			leftX += width;
		}
	}
	
	public void renderButtonBackground(Graphics2D g2, int leftX, int upperY, int width,
			int height, Color start, Color end) {
		float redDiff = end.getRed() - start.getRed();
		float greenDiff = end.getGreen() - start.getGreen();
		float blueDiff = end.getBlue() - start.getBlue();
		for(int y = upperY; y <= upperY + height / 3; y++) {
			float fy = (float) (y - upperY);
			float fh = (float) height / 3;
			float fdiff = 0.6f + 0.4f * fy / fh;
			float red = start.getRed() + redDiff * fdiff;
			float green = start.getGreen() + greenDiff * fdiff;
			float blue = start.getBlue() + blueDiff * fdiff;
			red /= 255.0f;
			green /= 255.0f;
			blue /= 255.0f;
			g2.setColor(new Color(red, green, blue));
			g2.drawLine(leftX, y , leftX + width, y);
		}
		for(int y = upperY + height / 3; y < upperY + height; y++) {
			float fy = (float) y - (height / 3) - upperY;
			float fh = (float) 2.0f * (height / 3);
			float fdiff = fy / fh;
			float red = end.getRed() - redDiff * fdiff;
			float green = end.getGreen() - greenDiff * fdiff;
			float blue = end.getBlue() - blueDiff * fdiff;
			red /= 255.0f;
			green /= 255.0f;
			blue /= 255.0f;
			g2.setColor(new Color(red, green, blue));
			g2.drawLine(leftX, y , leftX + width, y);
		}
	}
	
	public TBSQuestionButtonType getCurrentQuestion() {
		return currentQuestion;
	}
	
	public void breakQuestionByLine(){
		String currentLine = "";
		for(String token : question.split(" ")){
			if(getStringBounds(currentLine + token).getWidth() > (promptWidth - paddingWidth * 2)){
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
		for(int i=1;i<=totalRadioQuestions; i++)
			lineBrokenQuestion.add(i <= currentRadioQuestion ? i + ")" + questionProps.getProperty("questionThree"+i) : "");
	}
	
}
