package tbs.view.prompt.student;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
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

public class RadioQuestionPrompt extends Prompt {

	// Information to be used by all prompt types
	TBSModel model;

	Properties questionProps;
	ArrayList<String> userInputLines;
	RadioResponse response;
	int questionCount;

	// Prompt sizing information
	List<String> lineBrokenQuestion;
	List<OpenQuestionPromptButtonType> buttons;
	int buttonHeight;

	// Question Text
	List<String> questionText;
	List<String[]> radioText;

	// Question 3(Radio) Properties
	OpenQuestionButtonType currentRadioQuestion;
	int currentRadioSubQuestion;
	Rectangle radioQuestionSelection;

	// if buttons != null, value of button pressed is returned
	// if buttons == null, text input is assumed
	public RadioQuestionPrompt(TBSModel model) {
		super(false, true, new Dimension(770, 0), model);
		this.model = model;
		buttons = OpenQuestionPromptButtonType.getRadioButtons();
		questionProps = PropertyLoader.getProperties("questions");
		questionText = new LinkedList<String>();
		radioText = new LinkedList<String[]>();
		questionCount = 0;
	}

	public void mousePressed(MouseEvent e) {
		if (getBottomButtons().contains(e.getPoint())) {
			int index = getSelectedButtonIndex(e.getX(), buttons.size());
			OpenQuestionPromptButtonType buttonClicked = buttons.get(index);
			if (OpenQuestionPromptButtonType.SUBMIT.equals(buttonClicked)) {
				List<OpenQuestionButtonType> radioQuestions = OpenQuestionButtonType
						.getRadioButtons();
				if (currentRadioQuestion.ordinal() == radioQuestions.size() - 1)
					setFinished(true);
				else
					setCurrentQuestion(radioQuestions.get(currentRadioQuestion
							.ordinal() + 1));
			} else {
				model
						.getStudent()
						.getResponse(currentRadioQuestion)
						.updateText(currentRadioSubQuestion, buttons.get(index));
				if (currentRadioSubQuestion == (questionCount - 1))
					buttons = OpenQuestionPromptButtonType.getWrittenButtons();
				currentRadioSubQuestion++;
			}
		} else if (getCloseButton().contains(e.getPoint()))
			setFinished(true);
		else {
			if (radioQuestionSelection.contains(e.getPoint())) {
				if (currentRadioSubQuestion == questionCount)
					buttons = OpenQuestionPromptButtonType.getRadioButtons();
				currentRadioSubQuestion = ((int) ((e.getY() - radioQuestionSelection
						.getY()) * questionCount) / radioQuestionSelection.height);
			}
		}
	}

	public void paintComponent(Graphics2D g2) {
		setGraphics(g2);
		lineBrokenQuestion = new LinkedList<String>();
		List<String> text = new LinkedList<String>();
		int totalLines = 0;
		if (questionText.size() == 0) {
			text = TBSGraphics.breakStringByLineWidth(g2, questionProps
					.getProperty(currentRadioQuestion.getQuestionKey()),
					getUnpaddedWidth());
			questionText = text;
		} else
			text = questionText;
		totalLines = text.size() + 3 + radioText.size();
		if (radioText.size() == 0) {
			int index = model.getStudent().hasArrows() ? 1 : questionCount;
			String[] radioPair;
			for (int i = 0; i < questionCount; i++) {
				radioPair = new String[2];
				radioPair[0] = questionProps.getProperty(currentRadioQuestion
						.getQuestionKey()
						+ index);
				radioPair[1] = "";
				radioText.add(radioPair);
				if (model.getStudent().hasArrows())
					index++;
				else
					index--;
			}
		}
		List<OpenQuestionPromptButtonType> radioAnswers = response
				.getRadioAnswers();
		for (int i = 0; i < radioAnswers.size(); i++)
			radioText.get(i)[1] = radioAnswers.get(i).getText();
		calculateValues(totalLines, true);
		drawBox();
		drawButtons(buttons.toArray());

		drawHeader(new StringBuffer("Open Response - ").append(
				currentRadioQuestion.getAdminText()).toString());
		incrementStringY();

		drawText(text);
		incrementStringY();
		radioQuestionSelection = new Rectangle(getX()
				+ TBSGraphics.padding.width, getStringY(),
				TBSGraphics.questionButtonsWidth, response.getQuestionCount()
						* (TBSGraphics.textHeight + TBSGraphics.padding.height));
		drawRadioSelectionButtons();
		drawRadio(radioText);
	}

	public void drawRadio(List<String[]> lines) {
		int questionX = getX() + TBSGraphics.questionButtonsWidth
				+ (TBSGraphics.padding.width * 2);
		int answerX = getX() + getWidth();
		boolean selected;
		int i = 0;
		for (String[] line : lines) {
			selected = currentRadioSubQuestion == i && buttons.size() > 1;
			drawString(line[0], questionX, getStringY(), selected);
			int answerWidth = TBSGraphics.getStringBounds(getGraphics(),
					line[1]).width + 4;
			drawString(line[1], answerX - answerWidth, getStringY(), selected);
			incrementStringY();
			i++;
		}
	}

	public void drawRadioSelectionButtons() {
		Rectangle buttonRect = new Rectangle(radioQuestionSelection.x,
				radioQuestionSelection.y, radioQuestionSelection.width,
				radioQuestionSelection.height / questionCount);
		for (int i = 0; i < questionCount; i++) {
			if (i == currentRadioSubQuestion)
				TBSGraphics.renderButtonBackground(getGraphics(), buttonRect,
						true);
			else
				TBSGraphics.renderButtonBackground(getGraphics(), buttonRect,
						false);
			getGraphics().setColor(Color.gray);
			getGraphics().draw(buttonRect);
			TBSGraphics.drawCenteredString(getGraphics(), new StringBuffer(
					i + 1).toString(), buttonRect.x, buttonRect.y
					+ (buttonRect.height - 2), buttonRect.width, 0);
			buttonRect.setLocation(buttonRect.x, buttonRect.y
					+ (radioQuestionSelection.height / questionCount));
		}
	}

	public void setCurrentQuestion(OpenQuestionButtonType currentQuestion) {
		currentRadioQuestion = currentQuestion;
		questionText = new LinkedList<String>();
		response = (RadioResponse) model.getStudent().getResponse(
				currentRadioQuestion);
		questionCount = response.getQuestionCount();
		currentRadioSubQuestion = 0;
	}

	/*
	 * Must leave this code for isOverButton() here because of the addition of
	 * radio question selection buttons, whereas all the other prompts only
	 * contain close button and/or bottom buttons.
	 */
	public boolean isOverButton(MouseEvent e) {
		if (getBottomButtons().contains(e.getPoint()))
			return true;
		if (getCloseButton().contains(e.getPoint()))
			return true;
		return radioQuestionSelection.contains(e.getPoint());
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

}
