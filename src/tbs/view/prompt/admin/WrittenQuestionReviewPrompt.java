
package tbs.view.prompt.admin;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import tbs.TBSGraphics;
import tbs.model.AdminModel;
import tbs.properties.PropertyLoader;
import tbs.view.AdminView;
import tbs.view.OpenQuestionButtonType;
import tbs.view.prompt.Prompt;

public class WrittenQuestionReviewPrompt extends Prompt
{

	//Information to be used by all prompt types
	AdminModel model;
	Properties questionProps;

	//Prompt sizing information
	List<OpenQuestionButtonType> writtenQuestions = OpenQuestionButtonType.getWrittenButtons();
	List<List<String>> writtenQuestionTexts;
	int maxLinesOfQuestionText;
	int currentPage;
	int pageButtonWidth;
	List<String> pageButtonText;

	public WrittenQuestionReviewPrompt(AdminModel model) {
		super(true, false, new Dimension(820,0), model);
		this.model = model;
		questionProps = PropertyLoader.getProperties("questions");
		writtenQuestionTexts = new LinkedList<List<String>>();
		maxLinesOfQuestionText = 0;
		currentPage = 1;
		pageButtonText = new LinkedList<String>();
		pageButtonWidth = 0;
	}


	public void keyPressed(KeyEvent e){}

	public void keyTyped(KeyEvent e){}

	public void mousePressed(MouseEvent e) {
		if(getCloseButton().contains(e.getPoint()))
			setFinished(true);
		else{
			if(getBottomButtons().contains(e.getPoint()))
				currentPage = getSelectedButtonIndex(e.getX(),writtenQuestions.size())+1;
		}
	}

	public void paintComponent(Graphics2D g2) 
	{
		setGraphics(g2);
		int lineCount = TBSGraphics.maxLinesOfWrittenText*2;
		if(writtenQuestionTexts.size() == 0){
			for(OpenQuestionButtonType writtenQuestion : writtenQuestions){
				List<String> temp = TBSGraphics.breakStringByLineWidth(g2,
						questionProps.getProperty(writtenQuestion.getQuestionKey()),
            getWidth() - TBSGraphics.padding.width * 2);
				if(temp.size() > maxLinesOfQuestionText)
					maxLinesOfQuestionText = temp.size();
				writtenQuestionTexts.add(temp);
			}
		}
		lineCount += maxLinesOfQuestionText*2;
		calculateValues(lineCount);
		int maxQuestion = currentPage * 2;
		drawHeader(new StringBuffer("Open Responses - Questions ").append(maxQuestion-1)
				.append(" & ").append(maxQuestion).toString());
		drawBox();
		drawCloseButton();
		drawButtons(pageButtonText.toArray());
		incrementStringY();
		
		for(int i=(maxQuestion-2);i<maxQuestion;i++){
			drawText(writtenQuestionTexts.get(i));
			List<String> writtenAnswerText = new LinkedList<String>();
			String writtenAnswer = model.getStudent().getResponse(writtenQuestions.get(i)).getText();
			writtenAnswerText = TBSGraphics.breakStringByLineWidth(g2,writtenAnswer,
					getWidth() - TBSGraphics.padding.width * 2);
			drawText(writtenAnswerText, true);
			incrementStringY((TBSGraphics.textHeight + TBSGraphics.padding.height)*(TBSGraphics.maxLinesOfWrittenText-writtenAnswerText.size()));
		}
	}

	public void calculateValues(int lineCount) {
		
		int buttonHeight = TBSGraphics.textHeight + TBSGraphics.padding.height;
		getPromptSize().setSize(getWidth(), (TBSGraphics.textHeight * lineCount) + 
				(TBSGraphics.padding.height * (lineCount + 1)) + buttonHeight);
		AdminView view = (AdminView) model.getView();
		int scrollWidth = view.hasStudentScroll() ? view.getStudentBar().getWidth() : 0;
		int studentButtonWidth = TBSGraphics.maxStudentNameWidth + TBSGraphics.checkWidth + TBSGraphics.arrowWidth;
        int adminWidth = model.getApplet().getWidth() - (view.getVerticalBar().getWidth() + scrollWidth + studentButtonWidth);
        
		int centerX = (adminWidth / 2) + scrollWidth + studentButtonWidth;
		int centerY = model.getApplet().getHeight() / 2;
		setAnchorPoint(new Point(centerX - (getWidth() / 2), 
				centerY - (getHeight() / 2)));
		int questionCount = writtenQuestions.size();
		if(questionCount > 2){
			if(pageButtonText.size() == 0){
				for(int i=1;i<=questionCount/2;i++)
					pageButtonText.add(""+i);
				pageButtonWidth = TBSGraphics.get2DStringBounds(getGraphics(), pageButtonText).width;
				pageButtonWidth += TBSGraphics.padding.width * 2;
			}
			int pageButtonsWidth = (pageButtonWidth*questionCount);
			int pageButtonStart = (getX() + (getWidth()/2)) - (pageButtonsWidth/2);
			setBottomButtons(new Rectangle(pageButtonStart, getY() + (getHeight() - buttonHeight),
					pageButtonsWidth, buttonHeight));
		}
		setCloseButton(new Rectangle((getX() + getWidth())-buttonHeight, getY(),
				buttonHeight, buttonHeight));
		setStringY(getY());
	}
}

