
package tbs.view.prompt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import tbs.TBSGraphics;
import tbs.model.TBSModel;
import tbs.properties.PropertyType;
import tbs.view.TBSButtonType;

public class SplashPrompt extends Prompt
{

	//Information to be used by all prompt types
	TBSModel model;
	Graphics2D g2 = null;
	Properties instrProps;
	Properties helpProps; 
	boolean seenIntro = false;

	//Prompt sizing information
	Dimension padding = new Dimension(10,5);
	Dimension promptSize = new Dimension();
	Point anchorPoint = null;
	int instructionStringY;
	Rectangle closeButton;
	Rectangle startButton;
	boolean renderStart;
	int buttonHeight;
	int textHeight;

	String instrString;

	public SplashPrompt(TBSModel model) {
		super();
		this.model = model;
		instrProps = model.getProperties(PropertyType.INSTRUCTIONS);
		helpProps = model.getProperties(PropertyType.HELP);
		renderStart = true;
	}


	public void keyPressed(KeyEvent e){}

	public void keyTyped(KeyEvent e){}

	public void mousePressed(MouseEvent e) {
		if(closeButton.contains(e.getPoint()))
				setFinished(true);	
		if(renderStart){
			if(startButton.contains(e.getPoint()))
				setFinished(true);
		}
		renderStart = false;
	}

	public void paintComponent(Graphics2D g2) 
	{
		this.g2 = g2;
		textHeight = TBSGraphics.getStringBounds(g2,"QOgj").height;
		promptSize.setSize(750 + padding.width * 2, 0);
		if(renderStart){
			List<String> introduction = TBSGraphics.breakStringByLineWidth(g2,
					instrProps.getProperty("instrIntro"),
					promptSize.width - padding.width * 2);
			List<String> directions = TBSGraphics.breakStringByLineWidth(g2,
					instrProps.getProperty("instrDir"),
					promptSize.width - padding.width * 2);

			calculateValues(introduction.size() + directions.size() + 5);
			drawBox();
			drawButtons();
			drawText(introduction);
			instructionStringY += textHeight + padding.height;
			TBSGraphics.drawCenteredString(g2, instrProps.getProperty("instrHeader"),
					anchorPoint.x + padding.width, instructionStringY,
					promptSize.width - padding.width * 2,
					textHeight,TBSGraphics.emptyNodeColor);
			instructionStringY += (textHeight + padding.height) * 2;
			drawText(directions);
		}else{
			List<String> helpText = new LinkedList<String>();
			for(TBSButtonType bt : model.getButtons()){
				helpText.addAll(TBSGraphics.breakStringByLineWidth(g2,
					helpProps.getProperty("help_" + bt.getText()),
					promptSize.width - padding.width * 2));
			}
			calculateValues(helpText.size() + 2);
			drawBox();
			drawButtons();
			drawText(helpText);
		}
	}

	public void calculateValues(int lineCount) {
		instructionStringY = 0;
		promptSize.setSize(promptSize.width, (textHeight * lineCount) + 
				(padding.height * (lineCount + 1)));
		int centerX = model.getApplet().getWidth() / 2;
		int centerY = model.getApplet().getHeight() / 2;
		anchorPoint = new Point(centerX - (promptSize.width / 2), 
				centerY - (promptSize.height / 2));
		instructionStringY = anchorPoint.y;
		buttonHeight = textHeight + padding.height;
		closeButton = new Rectangle((anchorPoint.x + promptSize.width)-buttonHeight, anchorPoint.y,
				buttonHeight, buttonHeight);
		if(renderStart)
			startButton = new Rectangle(anchorPoint.x, anchorPoint.y + (promptSize.height - buttonHeight),
					promptSize.width, buttonHeight);
		else
			startButton = new Rectangle();
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

	public void drawText(List<String> lines) {
		int startX = anchorPoint.x + padding.width;
		for(String line : lines){
			drawString(line, startX, instructionStringY);
			instructionStringY += textHeight + padding.height;
		}
	}

	public void drawString(String s, int x, int y){
		drawString(s, x, y, false);
	}

	public void drawString(String s, int x, int y, boolean isSelected) {
		if(s != null && s.length() > 0)
			TBSGraphics.drawCenteredString(g2, s, x, y, 0, textHeight, 
					isSelected ? TBSGraphics.emptyNodeColor : Color.BLACK);
	}

	public void drawButtons()
	{
		TBSGraphics.renderButtonBackground(g2, closeButton, false);
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(3));
		g2.draw(closeButton);
		int x,y,w,h;
		x = closeButton.x+1;
		y = closeButton.y+1;
		w = closeButton.width-1;
		h = closeButton.height-1;
		g2.draw(new Line2D.Double(x,y,x+w,y+h));
		g2.draw(new Line2D.Double(x,y+h,x+w,y));
		g2.setStroke(new BasicStroke());

		TBSGraphics.renderButtonBackground(g2, startButton, false);
		g2.setColor(Color.gray);
		g2.draw(startButton);
		TBSGraphics.drawCenteredString(g2, "Start", startButton.x,
				startButton.y + (startButton.height - 2), startButton.width, 0);
	}


	public boolean isOverButton(MouseEvent e){
		if(closeButton.contains(e.getPoint()))
			return true;
		if(renderStart){
			if(startButton.contains(e.getPoint()))
				return true;
		}
		return false;
	}

}

