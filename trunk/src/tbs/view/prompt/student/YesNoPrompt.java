
package tbs.view.prompt.student;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
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
	TBSButtonType promptType;
	String question;
	YesNoButtonType response;
	
	//Prompt sizing information
	List<String> lineBrokenQuestion;
	YesNoButtonType[] buttons;
	int buttonHeight;
	
	// if buttons != null, value of button pressed is returned
	// if buttons == null, text input is assumed
	public YesNoPrompt(TBSModel model, TBSButtonType promptType) {
		super(false, true, new Dimension(320,0), model);
		this.model = model;
		this.promptType = promptType;
		question = new StringBuffer("Are you sure you want to ").append(promptType.getText())
			.append("? You will not be able to undo.").toString();
		buttons = YesNoButtonType.values();
	}
	
	public YesNoButtonType getResponse() {
		return response;
	}
	
	public TBSButtonType getPromptType() {
		return promptType;
	}

	public void mousePressed(MouseEvent e){
		if(getBottomButtons().contains(e.getPoint())){
        	int index = (int) ((e.getX() - getBottomButtons().getX()) * buttons.length) / getWidth();
        	response = buttons[index];
        	setFinished(true);
        }
	}

	public void keyPressed(KeyEvent e) {}
	
	public void keyTyped(KeyEvent e) {}
	
	public void paintComponent(Graphics2D g2) {
		setGraphics(g2);
		lineBrokenQuestion = new LinkedList<String>();
		lineBrokenQuestion.addAll(TBSGraphics.breakStringByLineWidth(g2,
					question, getWidth() - TBSGraphics.padding.width * 2));
		calculateValues(2 + lineBrokenQuestion.size(), false, true);
		drawBox();
		drawButtons(buttons);
		drawText(lineBrokenQuestion);
	}
	
	public boolean isOverButton(MouseEvent e){
		return getBottomButtons().contains(e.getPoint());
	}

}
