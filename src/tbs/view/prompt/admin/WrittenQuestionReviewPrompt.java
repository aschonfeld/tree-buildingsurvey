
package tbs.view.prompt.admin;

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
	Graphics2D g2 = null;
	Properties questionProps;

	//Prompt sizing information
	Dimension padding = new Dimension(10,5);
	Dimension promptSize = new Dimension(820,0);
	Point anchorPoint = null;
	int responseStringY;
	Rectangle closeButton;
	Rectangle pageButtons = new Rectangle();
	int buttonHeight;
	List<OpenQuestionButtonType> writtenQuestions = OpenQuestionButtonType.getWrittenButtons();
	List<List<String>> writtenQuestionTexts;
	int maxLinesOfQuestionText;
	int currentPage;
	int pageButtonWidth;
	List<String> pageButtonText;

	public WrittenQuestionReviewPrompt(AdminModel model) {
		super();
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
		if(closeButton.contains(e.getPoint()))
			setFinished(true);
		else{
			if(pageButtons.contains(e.getPoint())){
				int index = (int) ((e.getX() - pageButtons.getX()) * writtenQuestions.size()) / promptSize.width;
				currentPage = index+1;
			}
		}
	}

	public void paintComponent(Graphics2D g2) 
	{
		this.g2 = g2;
		calculateValues();
		drawBox();
		TBSGraphics.drawCloseButton(g2, closeButton);
		drawButtons();

		responseStringY = anchorPoint.y;
		int maxQuestion = currentPage * 2;
		TBSGraphics.drawCenteredString(g2,
				new StringBuffer("Open Responses - Questions ").append(maxQuestion-1)
				.append(" & ").append(maxQuestion).toString(),
				anchorPoint.x + padding.width, responseStringY,
				promptSize.width - padding.width * 2,
				buttonHeight,TBSGraphics.emptyNodeColor);
		responseStringY += buttonHeight;
		
		for(int i=(maxQuestion-2);i<maxQuestion;i++){
			drawWritten(writtenQuestionTexts.get(i), false);
			List<String> writtenAnswerText = new LinkedList<String>();
			String writtenAnswer = model.getStudent().getResponse(writtenQuestions.get(i)).getText();
			writtenAnswerText = TBSGraphics.breakStringByLineWidth(g2,writtenAnswer,
					promptSize.width - padding.width * 2);
			drawWritten(writtenAnswerText, true);
			responseStringY += (TBSGraphics.textHeight + padding.height)*(TBSGraphics.maxLinesOfWrittenText-writtenAnswerText.size());
		}
	}

	public void calculateValues() {
		int lineCount = TBSGraphics.maxLinesOfWrittenText*2;
		if(writtenQuestionTexts.size() == 0){
			for(OpenQuestionButtonType writtenQuestion : writtenQuestions){
				List<String> temp = TBSGraphics.breakStringByLineWidth(g2,
						questionProps.getProperty(writtenQuestion.getQuestionKey()),
				promptSize.width - padding.width * 2);
				if(temp.size() > maxLinesOfQuestionText)
					maxLinesOfQuestionText = temp.size();
				writtenQuestionTexts.add(temp);
			}
		}
		lineCount += maxLinesOfQuestionText*2;
		buttonHeight = TBSGraphics.textHeight + padding.height;
		promptSize.setSize(promptSize.width, (TBSGraphics.textHeight * lineCount) + 
				(padding.height * (lineCount + 1)) + buttonHeight);
		AdminView view = (AdminView) model.getView();
		int scrollWidth = view.hasStudentScroll() ? view.getStudentBar().getWidth() : 0;
		int studentButtonWidth = TBSGraphics.maxStudentNameWidth + TBSGraphics.checkWidth + TBSGraphics.arrowWidth;
        int adminWidth = model.getApplet().getWidth() - (view.getVerticalBar().getWidth() + scrollWidth + studentButtonWidth);
        
		int centerX = (adminWidth / 2) + scrollWidth + studentButtonWidth;
		int centerY = model.getApplet().getHeight() / 2;
		anchorPoint = new Point(centerX - (promptSize.width / 2), 
				centerY - (promptSize.height / 2));
		int questionCount = writtenQuestions.size();
		if(questionCount > 2){
			if(pageButtonText.size() == 0){
				for(int i=1;i<=questionCount/2;i++)
					pageButtonText.add(""+i);
				pageButtonWidth = TBSGraphics.get2DStringBounds(g2, pageButtonText).width;
				pageButtonWidth += TBSGraphics.paddingWidth * 2;
			}
			int pageButtonsWidth = (pageButtonWidth*questionCount);
			int pageButtonStart = (anchorPoint.x + (promptSize.width/2)) - (pageButtonsWidth/2);
			pageButtons = new Rectangle(pageButtonStart, anchorPoint.y + (promptSize.height - buttonHeight),
					pageButtonsWidth, buttonHeight);
		}
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
			responseStringY += TBSGraphics.textHeight + padding.height;
		}
	}

	public void drawRadio(List<String[]> lines) {
		int questionX = anchorPoint.x + padding.width;
		int answerX = anchorPoint.x + promptSize.width;
		for(String[] line : lines){
			drawString(line[0], questionX, responseStringY);
			int answerWidth = TBSGraphics.getStringBounds(g2,line[1]).width + 4;
			drawString(line[1], answerX-answerWidth, responseStringY, true);
			responseStringY += TBSGraphics.textHeight + padding.height;
		}
	}



	public void drawString(String s, int x, int y){
		drawString(s, x, y, false);
	}

	public void drawString(String s, int x, int y, boolean isSelected) {
		if(s != null && s.length() > 0)
			TBSGraphics.drawCenteredString(g2, s, x, y, 0, TBSGraphics.textHeight, 
					isSelected ? TBSGraphics.emptyNodeColor : Color.BLACK);
	}

	public void drawButtons()
	{
		if(pageButtonText.size() > 0){
			Rectangle buttonRect = new Rectangle(pageButtons.x, pageButtons.y,
					pageButtons.width/pageButtonText.size(), pageButtons.height);
			for(String page : pageButtonText) {
				if(page.equals(Integer.toString(currentPage)))
					TBSGraphics.renderButtonBackground(g2, buttonRect, true);
				else
					TBSGraphics.renderButtonBackground(g2, buttonRect, false);
				g2.setColor(Color.gray);
				g2.draw(buttonRect);
				TBSGraphics.drawCenteredString(g2, page, buttonRect.x,
						buttonRect.y + (buttonRect.height - 2), buttonRect.width, 0);
				buttonRect.setLocation(buttonRect.x + buttonRect.width, buttonRect.y);
			}
		}
	}


	public boolean isOverButton(MouseEvent e){
		return (closeButton.contains(e.getPoint()) || pageButtons.contains(e.getPoint()));
	}

}

