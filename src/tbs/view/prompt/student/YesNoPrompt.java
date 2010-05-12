package tbs.view.prompt.student;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import tbs.TBSGraphics;
import tbs.model.TBSModel;
import tbs.properties.PropertyLoader;
import tbs.view.TBSButtonType;
import tbs.view.prompt.Prompt;
import tbs.view.prompt.buttons.YesNoButtonType;

public class YesNoPrompt extends Prompt {

	// Information to be used by all prompt types
	TBSModel model;
	TBSButtonType promptType;
	String question;
	YesNoButtonType response;
	Properties yesnoProps;

	// Prompt sizing information
	List<String> lineBrokenQuestion;
	YesNoButtonType[] buttons;
	int buttonHeight;

	// if buttons != null, value of button pressed is returned
	// if buttons == null, text input is assumed
	public YesNoPrompt(TBSModel model, TBSButtonType promptType) {
		super(true, true, new Dimension(320, 0), model);
		setRenderClose(false);
		this.model = model;
		this.promptType = promptType;
		yesnoProps = PropertyLoader.getProperties("yesno");
		question = MessageFormat.format(yesnoProps.getProperty("text"), promptType.getText());
		buttons = YesNoButtonType.values();
	}

	public YesNoButtonType getResponse() {
		return response;
	}

	public TBSButtonType getPromptType() {
		return promptType;
	}

	public void mousePressed(MouseEvent e) {
		if (getBottomButtons().contains(e.getPoint())) {
			response = buttons[getSelectedButtonIndex(e.getX(), buttons.length)];
			setFinished(true);
		}
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void paintComponent(Graphics2D g2) {
		setGraphics(g2);
		lineBrokenQuestion = new LinkedList<String>();
		lineBrokenQuestion.addAll(TBSGraphics.breakStringByLineWidth(g2,
				question, getWidth() - TBSGraphics.padding.width * 2));
		calculateValues(2 + lineBrokenQuestion.size(), true);
		drawBox();
		drawButtons(buttons);
		drawText(lineBrokenQuestion);
	}
}
