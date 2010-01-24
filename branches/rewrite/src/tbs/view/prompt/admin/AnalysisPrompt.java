
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
import java.util.List;

import tbs.TBSGraphics;
import tbs.model.TBSModel;
import tbs.view.prompt.Prompt;

public class AnalysisPrompt extends Prompt
{

	//Information to be used by all prompt types
	TBSModel model;
	Graphics2D g2 = null;
	
	//Prompt sizing information
	Dimension padding = new Dimension(10,5);
	Dimension promptSize = new Dimension();
	Point anchorPoint = null;
	int analysisStringY;
	Rectangle closeButton;
	Rectangle startButton;
	int buttonHeight;

	String instrString;
	String welcomeMessage;

	public AnalysisPrompt(TBSModel model) {
		super();
		this.model = model;
	}


	public void keyPressed(KeyEvent e){}

	public void keyTyped(KeyEvent e){}

	public void mousePressed(MouseEvent e) {
		if(closeButton.contains(e.getPoint()))
				setFinished(true);		
	}

	public void paintComponent(Graphics2D g2) 
	{
		this.g2 = g2;
		int width = 600;
		promptSize.setSize(width + padding.width * 2, 0);
		
		calculateValues(3);
		analysisStringY = anchorPoint.y;
		drawBox();
		TBSGraphics.drawCloseButton(g2, closeButton);
		drawButtons();
		analysisStringY = anchorPoint.y;
		TBSGraphics.drawCenteredString(g2,"Anaylsis - Currently Under Construction",
				anchorPoint.x + padding.width, analysisStringY,
				promptSize.width - padding.width * 2,
				buttonHeight,TBSGraphics.emptyNodeColor);
		analysisStringY += buttonHeight;
	}

	public void calculateValues(int lineCount) {
		promptSize.setSize(promptSize.width, (TBSGraphics.textHeight * lineCount) + 
				(padding.height * (lineCount + 1)));
		int centerX = model.getApplet().getWidth() / 2;
		int centerY = model.getApplet().getHeight() / 2;
		anchorPoint = new Point(centerX - (promptSize.width / 2), 
				centerY - (promptSize.height / 2));
		buttonHeight = TBSGraphics.textHeight + padding.height;
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
			drawString(line, startX, analysisStringY,  answer);
			analysisStringY += TBSGraphics.textHeight + padding.height;
		}
	}
	
	public void drawRadio(List<String[]> lines) {
		int questionX = anchorPoint.x + padding.width;
		int answerX = anchorPoint.x + promptSize.width;
		for(String[] line : lines){
			drawString(line[0], questionX, analysisStringY);
			int answerWidth = TBSGraphics.getStringBounds(g2,line[1]).width + 4;
			drawString(line[1], answerX-answerWidth, analysisStringY, true);
			analysisStringY += TBSGraphics.textHeight + padding.height;
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
	{}


	public boolean isOverButton(MouseEvent e){
		return closeButton.contains(e.getPoint());
	}

}

