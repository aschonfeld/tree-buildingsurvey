
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
import java.util.ArrayList;
import java.util.Properties;

import tbs.model.TBSModel;
import tbs.view.TBSQuestionButtonType;

public class TBSPrompt{
	
	TBSModel model;
	TBSQuestionButtonType currentQuestion = null;
	Properties questionProps;
	String question = null;
	ArrayList<String> buttons = null;
	String userInput = null;
	Graphics2D g2 = null;
	Color promptBackground = Color.lightGray;
	Color textColor = Color.black;
	Color textBoxColor = Color.white;
	Color borderColor = Color.blue;
	int paddingWidth = 10;
	int paddingHeight = 5;
	int promptWidth = 0;
	int promptHeight = 0;
	int questionStringY;
	int textAreaStartY;
	int buttonsStringY;
	int buttonsHeight;
	int buttonsWidth;
	String minString = "This determines the minimum width of a prompt";
	String heightString = "QOgj"; // used to calculate string height 
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
		buttons = new ArrayList<String>();
		if(currentQuestion.equals(TBSQuestionButtonType.THREE)){
			buttons.add("Strongly Agree");
			buttons.add("Agree");
			buttons.add("Not Sure");			
			buttons.add("Disagree");
			buttons.add("Strongly Disagree");
			getTextInput = false;
		}else{
			buttons.add("Submit");
			getTextInput = true;
		}
		userInput = model.getQuestion(currentQuestion);
		finished = false;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public String getUserInput() {
		return userInput;
	}
	
	public void mousePressed(MouseEvent e){
		calculateValues();
        int x = e.getX();
        int y = e.getY();
        int upperY = buttonsStringY;
        int lowerY = upperY + buttonsHeight;
        int leftX = anchorPoint.x;
        int rightX = leftX + promptWidth;
        if(y > upperY && y < lowerY) {
        	if(x > leftX && x < rightX) {
        		finished = true;
        		if(getTextInput) return;
        		int index = ((x - leftX) * buttons.size()) / (promptWidth + 1);
        		userInput = buttons.get(index);
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
		if(lines.length >= numLines) userInput = userInput.substring(0 , userInput.length() - 1);
	}
	
	public void calculateValues() {
		int minWidth = (int) getStringBounds(minString).getWidth();
		if(buttons.size() == 5) minWidth = 750;
		int questionWidth = 0;
		int temp;
		for(String line : question.split("\n")){
			temp = (int) getStringBounds(line).getWidth();
			if(temp > questionWidth)
				questionWidth = temp;
		}
		if(questionWidth < minWidth) questionWidth = minWidth;
		promptWidth = questionWidth + paddingWidth * 2;
		int textHeight = (int) getStringBounds(heightString).getHeight();
		if(!getTextInput) {
			promptHeight = textHeight * 2 + paddingHeight * 3;
		} else {
			promptHeight = textHeight * (2 + numLines) + paddingHeight * (3 + numLines);
		}
		int centerX = model.getApplet().getWidth() / 2;
		int centerY = model.getApplet().getHeight() / 2;
		anchorPoint = new Point(centerX - promptWidth / 2, centerY - promptHeight / 2);
		questionStringY = anchorPoint.y;
		buttonsStringY = anchorPoint.y + promptHeight - textHeight - paddingHeight;
		buttonsHeight = textHeight + paddingHeight;
		buttonsWidth = promptWidth / buttons.size();
	}
	
	public void paintComponent(Graphics2D g2) {
		this.g2 = g2;
		int textHeight = (int) getStringBounds(heightString).getHeight();
		calculateValues();
		drawBox();
		for(String line : question.split("\n")){
			drawString(line, anchorPoint.x + paddingWidth, questionStringY);
			questionStringY += textHeight;
		}
		if(getTextInput) {
			drawTextInput();
		}
		renderButtons();
	}
	
	public void drawTextInput() {
		if(userInput == null)
			return;
		String[] lines = userInput.split("\n");
		int upperY = (int) anchorPoint.getY() + buttonsHeight;
		for(int index = 0; index < lines.length; index++) {
			if(lines[index].length() > 0) drawString(lines[index], anchorPoint.x + paddingWidth, upperY);
			upperY += promptHeight / 10;
		}
	}
	
	public Rectangle2D getStringBounds(String s) 
	{
		Font f = new Font(null, TBSGraphics.fontStyle, TBSGraphics.fontSize);
		FontRenderContext frc = g2.getFontRenderContext();
		TextLayout layout = new TextLayout(s, f, frc);
		return layout.getBounds();
	}
	
	public void drawString(String s, int x, int y) {
		TBSGraphics.drawCenteredString(g2, s, x, y, 0, buttonsHeight);
	}
	
	public void drawBox() {
		int padding = 1;
		double x = (double) anchorPoint.getX() - padding;
		double y = (double) anchorPoint.getY() - padding;
		int w = promptWidth + padding * 4;
		int h = promptHeight + padding * 4;
		g2.setColor(promptBackground);
		g2.fillRect((int) x - padding, (int) y - padding, w, h);
		//g2.setColor(borderColor);
		//g2.setStroke(new BasicStroke(3));
		//g2.draw(new Rectangle2D.Double(x - 1.5, y - 1.5, w + 3.0d, h + 3.0d));
	}
	
	public void renderButtons()
	{
		int leftX = anchorPoint.x;
		int upperY = buttonsStringY + buttonsHeight - 2;
		Color start = new Color(0.45f, 0.55f, 0.65f);
		Color end = new Color(1.0f, 1.0f, 1.0f);
		g2.setColor(Color.BLACK);
		for(String s: buttons) {
			if(s == "") {
				leftX += buttonsWidth;
				continue;
			}
			renderButtonBackground(g2, leftX, buttonsStringY, start, end);
			g2.setColor(Color.gray);
			g2.setStroke(new BasicStroke(1));
			g2.drawRect(leftX, buttonsStringY, buttonsWidth, buttonsHeight);
			TBSGraphics.drawCenteredString(g2, s, leftX, upperY, buttonsWidth, 0);
			leftX += buttonsWidth;
		}
	}
	
	public void renderButtonBackground(Graphics2D g2, int leftX, int upperY, Color start, Color end) {
		float redDiff = end.getRed() - start.getRed();
		float greenDiff = end.getGreen() - start.getGreen();
		float blueDiff = end.getBlue() - start.getBlue();
		for(int y = upperY; y <= upperY + buttonsHeight / 3; y++) {
			float fy = (float) (y - upperY);
			float fh = (float) buttonsHeight / 3;
			float fdiff = 0.6f + 0.4f * fy / fh;
			float red = start.getRed() + redDiff * fdiff;
			float green = start.getGreen() + greenDiff * fdiff;
			float blue = start.getBlue() + blueDiff * fdiff;
			red /= 255.0f;
			green /= 255.0f;
			blue /= 255.0f;
			g2.setColor(new Color(red, green, blue));
			g2.drawLine(leftX, y , leftX + buttonsWidth, y);
		}
		for(int y = upperY + buttonsHeight / 3; y < upperY + buttonsHeight; y++) {
			float fy = (float) y - (buttonsHeight / 3) - upperY;
			float fh = (float) 2.0f * (buttonsHeight / 3);
			float fdiff = fy / fh;
			float red = end.getRed() - redDiff * fdiff;
			float green = end.getGreen() - greenDiff * fdiff;
			float blue = end.getBlue() - blueDiff * fdiff;
			red /= 255.0f;
			green /= 255.0f;
			blue /= 255.0f;
			g2.setColor(new Color(red, green, blue));
			g2.drawLine(leftX, y , leftX + buttonsWidth, y);
		}
	}
	
	public TBSQuestionButtonType getCurrentQuestion() {
		return currentQuestion;
	}
	
}
