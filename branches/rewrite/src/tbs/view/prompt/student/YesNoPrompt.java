
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

import tbs.TBSGraphics;
import tbs.model.TBSModel;
import tbs.view.TBSButtonType;
import tbs.view.prompt.Prompt;
import tbs.view.prompt.buttons.YesNoButtonType;

public class YesNoPrompt extends Prompt{
	
	//Information to be used by all prompt types
	TBSModel model;
	Graphics2D g2 = null;
	TBSButtonType promptType;
	String question;
	YesNoButtonType response;
	
	//Prompt sizing information
	List<String> lineBrokenQuestion;
	YesNoButtonType[] buttons;
	int numLines = 8; // number of lines of text input
	Dimension padding = new Dimension(10,5);
	Dimension promptSize = new Dimension();
	Point anchorPoint = null;
	int questionStringY;
	Rectangle buttonsArea;
	int buttonHeight;
	int textHeight;
	
	// if buttons != null, value of button pressed is returned
	// if buttons == null, text input is assumed
	public YesNoPrompt(TBSModel model, TBSButtonType promptType) {
		super();
		this.model = model;
		this.promptType = promptType;
		question = "Are you sure you want to " + promptType.getText() +
			"? This action is undoable.";
		buttons = YesNoButtonType.values();
	}
	
	public YesNoButtonType getResponse() {
		return response;
	}
	
	public TBSButtonType getPromptType() {
		return promptType;
	}

	public void mousePressed(MouseEvent e){
		calculateValues();
        if(buttonsArea.contains(e.getPoint())){
        	int index = (int) ((e.getX() - buttonsArea.getX()) * buttons.length) / promptSize.width;
        	response = buttons[index];
        	setFinished(true);
        }
	}

	public void keyPressed(KeyEvent e) {}
	
	public void keyTyped(KeyEvent e) {}
	
	public void paintComponent(Graphics2D g2) {
		this.g2 = g2;
		textHeight = TBSGraphics.getStringBounds(g2,"QOgj").height;
		promptSize.setSize(300 + padding.width * 2, 0);
		lineBrokenQuestion = new LinkedList<String>();
		int questionLength = 0;
		lineBrokenQuestion.addAll(TBSGraphics.breakStringByLineWidth(g2,
					question, promptSize.width - padding.width * 2));
		calculateValues();
		drawBox();
		drawText(lineBrokenQuestion, questionLength);
		questionStringY += ((textHeight+4) * lineBrokenQuestion.size());
		drawButtons();
	}
	
	public void calculateValues() {
		int lineCount = 2 + lineBrokenQuestion.size();
		promptSize.setSize(promptSize.width, (textHeight * lineCount) + (padding.height * (lineCount + 1)));
		int centerX = model.getApplet().getWidth() / 2;
		int centerY = model.getApplet().getHeight() / 2;
		anchorPoint = new Point(centerX - (promptSize.width / 2), centerY - (promptSize.height / 2));
		questionStringY = anchorPoint.y;
		buttonHeight = textHeight + padding.height;
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
	public void drawText(List<String> lines){ drawText(lines, 1); }
	
	public void drawText(List<String> lines, int startAnswers) {
		int startY = questionStringY;
		int startX = anchorPoint.x + padding.width;
		for(int i=0;i<lines.size();i++){
			drawString(lines.get(i), startX, startY);
			startY += textHeight + 4;
		}
	}
	
	public void drawString(String s, int x, int y){
		if(s != null && s.length() > 0)
			TBSGraphics.drawCenteredString(g2, s, x, y, 0, textHeight + 4, Color.BLACK);
	}
	
	public void drawButtons()
	{
		Rectangle buttonRect = new Rectangle(buttonsArea.x, buttonsArea.y,
				buttonsArea.width/buttons.length, buttonsArea.height);
		for(YesNoButtonType button: buttons) {
			TBSGraphics.renderButtonBackground(g2, buttonRect, false);
			g2.setColor(Color.gray);
			g2.draw(buttonRect);
			TBSGraphics.drawCenteredString(g2, button.toString(), buttonRect.x,
					buttonRect.y + (buttonRect.height - 2), buttonRect.width, 0);
			buttonRect.setLocation(buttonRect.x + buttonRect.width, buttonRect.y);
		}
	}
	
	public boolean isOverButton(MouseEvent e){
		return buttonsArea.contains(e.getPoint());
	}
	
}
