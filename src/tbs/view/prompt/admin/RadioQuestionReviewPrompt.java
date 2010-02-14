
package tbs.view.prompt.admin;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import tbs.TBSGraphics;
import tbs.model.AdminModel;
import tbs.model.admin.RadioResponse;
import tbs.properties.PropertyLoader;
import tbs.view.OpenQuestionButtonType;
import tbs.view.prompt.Prompt;
import tbs.view.prompt.buttons.OpenQuestionPromptButtonType;

public class RadioQuestionReviewPrompt extends Prompt
{

	//Information to be used by all prompt types
	AdminModel model;
	Graphics2D g2 = null;
	Properties questionProps;
	
	//Prompt sizing information
	Point anchorPoint = null;
	List<OpenQuestionButtonType> radioQuestions = OpenQuestionButtonType.getRadioButtons();
	OpenQuestionButtonType currentRadioQuestion;
	Map<OpenQuestionButtonType, List<String[]>> radioQuestionTexts;
	
	public RadioQuestionReviewPrompt(AdminModel model) {
		super(true, false, new Dimension(820,0), model);
		this.model = model;
		currentRadioQuestion = radioQuestions.get(0);
		questionProps = PropertyLoader.getProperties("questions");
		
		radioQuestionTexts = new HashMap<OpenQuestionButtonType, List<String[]>>();
		List<String[]> radioQuestionText;
		for(OpenQuestionButtonType radioQuestion : radioQuestions){
			radioQuestionText = new LinkedList<String[]>();
			int index = model.getStudent().hasArrows() ? 1 : radioQuestion.getRadioQuestionCount();
			String[] radioPair;
			RadioResponse radioResponse = (RadioResponse) model.getStudent().getResponse(currentRadioQuestion);
			List<OpenQuestionPromptButtonType> radioAnswers = radioResponse.getRadioAnswers();
			for(OpenQuestionPromptButtonType answer : radioAnswers){
				radioPair = new String[2];
				radioPair[0] = questionProps.getProperty(radioQuestion.getQuestionKey()+index);
				radioPair[1] = answer.getText();
				radioQuestionText.add(radioPair);
				if(model.getStudent().hasArrows())
					index++;
				else
					index--;
			}
			radioQuestionTexts.put(radioQuestion, radioQuestionText);
		}		
	}


	public void keyPressed(KeyEvent e){}

	public void keyTyped(KeyEvent e){}

	public void mousePressed(MouseEvent e) {
		if(getCloseButton().contains(e.getPoint()))
				setFinished(true);
		else{
			if(getBottomButtons().contains(e.getPoint()))
	        	currentRadioQuestion = radioQuestions.get(getSelectedButtonIndex(e.getX(),radioQuestions.size()));
		}
	}

	public void paintComponent(Graphics2D g2) 
	{
		setGraphics(g2);
		List<String[]> radioQuestionText = radioQuestionTexts.get(currentRadioQuestion);
		calculateValues(radioQuestionText.size() + 2, true, true);
		drawBox();
		drawCloseButton();
		drawButtons(radioQuestions.toArray());
		
		setStringY(anchorPoint.y);
		drawHeader(new StringBuffer("Open Responses - ").append(currentRadioQuestion.getAdminText()).toString());
		incrementStringY();
		
		drawRadio(radioQuestionText);
	}
	
	public void drawRadio(List<String[]> lines) {
		int questionX = anchorPoint.x + TBSGraphics.padding.width;
		int answerX = anchorPoint.x + getWidth();
		for(String[] line : lines){
			drawString(line[0], questionX, getStringY());
			int answerWidth = TBSGraphics.getStringBounds(g2,line[1]).width + 4;
			drawString(line[1], answerX-answerWidth, getStringY(), true);
			incrementStringY();
		}
	}
}

