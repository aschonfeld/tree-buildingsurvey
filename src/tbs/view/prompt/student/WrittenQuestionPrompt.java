
package tbs.view.prompt.student;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;

import javax.swing.Timer;

import tbs.TBSGraphics;
import tbs.TBSUtils;
import tbs.model.TBSModel;
import tbs.properties.PropertyLoader;
import tbs.view.OpenQuestionButtonType;
import tbs.view.prompt.Prompt;
import tbs.view.prompt.buttons.OpenQuestionPromptButtonType;

/**
* Creates a text-entry box for an open-response question.
*/
public class WrittenQuestionPrompt extends Prompt{

	//Information to be used by all prompt types
	TBSModel model;
	private ActionListener hider = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			cursorIsOn = !cursorIsOn;
		}
	};
	private Timer timer = new Timer(500, hider);
	private List<Integer> pressedKeys;
	private boolean cursorIsOn = true;
	private int cursorIndex;
	private int lineIndex;
	private int cursorWidth = 2;
	private Color offColor = Color.white;
	private Color onColor = Color.darkGray;

	Properties questionProps;
	OpenQuestionButtonType currentQuestion;
	ArrayList<String> userInputLines;
	String userInput = "";

	//Prompt sizing information
	List<String> lineBrokenQuestion;
	List<OpenQuestionPromptButtonType> buttons;
	
	//Question Text
	Map<OpenQuestionButtonType, List<String>> questionTexts;

	// if buttons != null, value of button pressed is returned
	// if buttons == null, text input is assumed
	public WrittenQuestionPrompt(TBSModel model) {
		super(true, false, new Dimension(770,0), model);
		this.model = model;
		questionProps = PropertyLoader.getProperties("questions");
		questionTexts = new HashMap<OpenQuestionButtonType, List<String>>();
		pressedKeys = new LinkedList<Integer>();
		pressedKeys.add(KeyEvent.VK_DELETE);
		pressedKeys.add(KeyEvent.VK_UP);
		pressedKeys.add(KeyEvent.VK_DOWN);
		pressedKeys.add(KeyEvent.VK_RIGHT);
		pressedKeys.add(KeyEvent.VK_LEFT);
	}


	
	public void mousePressed(MouseEvent e){
		if(getBottomButtons().contains(e.getPoint())){
			OpenQuestionPromptButtonType buttonClicked = buttons.get(getSelectedButtonIndex(e.getX(),buttons.size()));
			if(OpenQuestionPromptButtonType.SUBMIT.equals(buttonClicked)){
				model.getStudent().getResponse(currentQuestion).updateText(convertLinesToUserInput());
				List<OpenQuestionButtonType> writtenQuestions = OpenQuestionButtonType.getWrittenButtons();
				if(currentQuestion.ordinal() == writtenQuestions.size()-1)
					setFinished(true);
				else
					setCurrentQuestion(writtenQuestions.get(currentQuestion.ordinal()+1));
			}
		}else if(getCloseButton().contains(e.getPoint()))
			setFinished(true);
		if(isFinished())
			timer.stop();
	}

	public void keyPressed(KeyEvent e) {
		if(!pressedKeys.contains(e.getKeyCode()))
			return;
		int size = userInputLines.size();	
		String currentLine = userInputLines.get(lineIndex);
		StringBuffer temp = new StringBuffer(currentLine);
		int len = currentLine.length();
		if(e.getKeyCode() == KeyEvent.VK_DELETE){
			if(cursorIndex < len){
				temp.deleteCharAt(cursorIndex);
				userInputLines.set(lineIndex, temp.toString());
			}
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT){
			if(cursorIndex > 0)
				cursorIndex--;
			else{
				if(lineIndex > 0){
					lineIndex--;
					currentLine = userInputLines.get(lineIndex);
					len = currentLine.length();
					cursorIndex = len;
				}
			}	
		}else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			if(cursorIndex < len)
				cursorIndex++;
			else{
				if(lineIndex < size-1){
					lineIndex++;
					cursorIndex = 0;
				}
			}
		}else if(e.getKeyCode() == KeyEvent.VK_DOWN){
			if(size > 1 && lineIndex < size-1){
				lineIndex++;
				currentLine = userInputLines.get(lineIndex);
				len = currentLine.length();
				if(cursorIndex > len-1)
					cursorIndex = userInputLines.get(lineIndex).length();
			}
		}else if(e.getKeyCode() == KeyEvent.VK_UP){
			if(size > 1 && lineIndex > 0){
				lineIndex--;
				currentLine = userInputLines.get(lineIndex);
				len = currentLine.length();
				if(cursorIndex > len-1)
					cursorIndex = userInputLines.get(lineIndex).length();
			}
		}
	}

	public void keyTyped(KeyEvent e) {
		if(pressedKeys.contains(e.getKeyCode()))
			return;
		char c = e.getKeyChar();
		//Catch for illegal database delimeters (+,=)
		Matcher m = TBSGraphics.writtenResponseIllegalCharacters.matcher("" + c);
		if(m.find())
			return;

		String currentLine = userInputLines.get(lineIndex);
		StringBuffer temp = new StringBuffer(currentLine);
		if(c == '\b'){
			if(cursorIndex > 0){
				temp.deleteCharAt(cursorIndex-1);
				userInputLines.set(lineIndex, temp.toString());
				cursorIndex--;
			}else{
				if(lineIndex > 0){
					userInputLines.remove(lineIndex);
					lineIndex--;
					currentLine = userInputLines.get(lineIndex);
					temp = new StringBuffer(currentLine);
					cursorIndex = temp.length();
				}
			}
		}else{
			int totalLines = 0;
			for(int i=0;i<userInputLines.size();i++){
				if(i==lineIndex && c != '\b' && c != '\n')
					totalLines += TBSGraphics.breakStringByLineWidth(getGraphics(), userInputLines.get(i)+c, getUnpaddedWidth()).size();
				else
					totalLines += TBSGraphics.breakStringByLineWidth(getGraphics(), userInputLines.get(i), getUnpaddedWidth()).size();
			}
			if(c == '\n'){
				if(totalLines < TBSGraphics.maxLinesOfWrittenText){
					if(lineIndex == userInputLines.size()-1)
						userInputLines.add("");
					else
						userInputLines.add(lineIndex+1,"");
					lineIndex++;
					cursorIndex = 0;
					return;
				}
			}else{
				if(totalLines <= TBSGraphics.maxLinesOfWrittenText){
					temp.insert(cursorIndex, c);
					userInputLines.set(lineIndex, temp.toString());
					cursorIndex++;
				}
			}
		}	
	}

	public void paintComponent(Graphics2D g2) {
		setGraphics(g2);
		lineBrokenQuestion = new LinkedList<String>();
		List<String> text = new LinkedList<String>();
		int totalLines = 0;
		if(!questionTexts.containsKey(currentQuestion)){
			text = TBSGraphics.breakStringByLineWidth(g2,
					questionProps.getProperty(currentQuestion.getQuestionKey()),getUnpaddedWidth());
			questionTexts.put(currentQuestion, text);
		}else
			text = questionTexts.get(currentQuestion);
		totalLines = text.size() + 3 + TBSGraphics.maxLinesOfWrittenText;	
		calculateValues(totalLines, true, true);
		drawBox();
		drawCloseButton();
		drawButtons(buttons.toArray());
		drawHeader(new StringBuffer("Open Response - ").append(currentQuestion.getAdminText()).toString());
		incrementStringY();
		drawText(text);
		incrementStringY();
		String line = "";
		for(int i=0;i<userInputLines.size();i++){
			line = userInputLines.get(i);
			if(i != lineIndex){
				if(!TBSUtils.isStringEmpty(line))
					drawText(TBSGraphics.breakStringByLineWidth(g2,line,getUnpaddedWidth()));
				else
					incrementStringY();
			}else{
				TextLayout layout;
				int x, y;
				List<String> tempLines = TBSGraphics.breakStringByLineWidth(g2,line,getUnpaddedWidth());
				int currentSize = 0;
				int cursorY = 0;
				String cursorLine = "";
				int cursorLineIndex = 0;
				int adjCursorIndex = cursorIndex;
				if(tempLines.size() == 1){
					cursorLine = line;
					cursorY = getStringY();
				}else{
					String tempLine = "";
					for(int j=0;j<tempLines.size();j++){
						tempLine = tempLines.get(j);
						if(adjCursorIndex <= tempLine.length() && cursorLineIndex == 0){
							cursorLine = tempLine;
							cursorY = getStringY();
							incrementStringY();
							cursorLineIndex = j;
						}else{
							drawString(tempLine, getX() + TBSGraphics.padding.width, getStringY());
							incrementStringY();
							currentSize += tempLine.length();
							if(j>=cursorLineIndex)
								adjCursorIndex -= tempLine.length();
						}
						i++;	
					}
				}
				// calculate dimensions of String s
				x = getX() + TBSGraphics.padding.width;
				y = cursorY + TBSGraphics.textHeight;
				boolean cursorWithinName = adjCursorIndex < cursorLine.length();
				String beforeCursor = cursorWithinName ? cursorLine.substring(0, adjCursorIndex) : cursorLine;
				int cursorX = x;
				if(!TBSUtils.isStringEmpty(beforeCursor)){
					layout = new TextLayout(beforeCursor, g2.getFont(), g2.getFontRenderContext());
					layout.draw(g2, x, y);
					cursorX += ((int) layout.getBounds().getWidth() + 2);
				}
				else
					cursorX += 2;
				if(cursorWithinName){
					String afterCursor = cursorLine.substring(adjCursorIndex);
					if(!TBSUtils.isStringEmpty(afterCursor)){
						layout = new TextLayout(afterCursor, g2.getFont(), g2.getFontRenderContext());
						layout.draw(g2, cursorX + cursorWidth, y);
					}
				}
				drawCursor(new Point(cursorX,cursorY), new Point(cursorWidth, TBSGraphics.textHeight));
				if(adjCursorIndex != cursorIndex)
					incrementStringY();
			}	
		}
	}

	public void drawCursor(Point upperLeft, Point size) {
		if(cursorIsOn) 
			getGraphics().setColor(onColor);
		else
			getGraphics().setColor(offColor);
		getGraphics().fillRect(upperLeft.x, upperLeft.y, size.x, size.y);
	}

	public OpenQuestionButtonType getCurrentQuestion() {
		return currentQuestion;
	}

	public void setCurrentQuestion(OpenQuestionButtonType currentQuestion) {
		this.currentQuestion = currentQuestion;
		buttons = OpenQuestionPromptButtonType.getWrittenButtons();
		userInput = model.getStudent().getResponse(currentQuestion).getText();
		userInputLines = new ArrayList<String>();
		for(String line : userInput.split("\n"))
			userInputLines.add(line);

		int size = 0;
		if(!userInputLines.isEmpty())
			size = userInputLines.size();

		if(size == 0){
			userInputLines.add("");
			lineIndex = 0;
			cursorIndex = 0;
		}else{
			lineIndex = size-1;
			String temp = userInputLines.get(lineIndex);
			if(!TBSUtils.isStringEmpty(temp))
				cursorIndex = temp.length();
			else
				cursorIndex = 0;
		}
		timer.start();
	}

	private String convertLinesToUserInput(){
		StringBuffer returnString = new StringBuffer("");
		for(String line : userInputLines)
			returnString.append(line).append("\n");

		if(!TBSUtils.isStringEmpty(returnString.toString()))
			return returnString.substring(0, returnString.length()-1).toString();
		return "";
	}
	
	public void forceAcceptChanges(){
		model.getStudent().getResponse(currentQuestion).updateText(convertLinesToUserInput());
		setFinished(true);
	}

}
