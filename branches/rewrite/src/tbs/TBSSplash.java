
package tbs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import tbs.model.TBSModel;

public class TBSSplash 
	{
	
	//Information to be used by all prompt types
	TBSModel model;
	Graphics2D g2 = null;
	Properties instrProps = null;
	boolean finished = false;
	boolean seenIntro = false;
	
	//Prompt sizing information
	List<String> instructions;
	int numLines = 8; // number of lines of text input
	Dimension padding = new Dimension(10,5);
	Dimension promptSize = new Dimension();
	Point anchorPoint = null;
	int instructionStringY;
	Rectangle buttonsArea;
	int buttonHeight;
	int textHeight;

	String instrString;
 
	public TBSSplash(TBSModel model) {
		this.model = model;
		instrProps = model.getInstructionProperties();
		finished = false;
	}
	

	public boolean isFinished() {
		return finished;
	}
	
	

	public void keyPressed(KeyEvent e) 
	{
	}
	
	public void keyTyped(KeyEvent e) 
	{
	}
	
	public void paintComponent(Graphics2D g2) 
	{
		this.g2 = g2;
		textHeight = TBSGraphics.getStringBounds(g2,"QOgj").height;
		promptSize.setSize(750 + padding.width * 2, 0);
		instructions = new LinkedList<String>();
		if (instrProps==null)
		{
			 instrString = "This is a dummy string";
		}
		else
		{
			instrString=instrProps.getProperty("instr");
		}
			instructions.addAll(TBSGraphics.breakStringByLineWidth(g2,
					instrString, promptSize.width - padding.width * 2));
		calculateValues();
		drawBox();
		drawText(instructions);
		instructionStringY += ((textHeight+4) * instructions.size());
	}
	
	public void calculateValues() {
		int lineCount = 2 + instructions.size();
		promptSize.setSize(promptSize.width, (textHeight * lineCount) + 
			(padding.height * (lineCount + 1)));
		int centerX = model.getApplet().getWidth() / 2;
		int centerY = model.getApplet().getHeight() / 2;
		anchorPoint = new Point(centerX - (promptSize.width / 2), 
			centerY - (promptSize.height / 2));
		instructionStringY = anchorPoint.y;
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
		int startY = instructionStringY;
		int startX = anchorPoint.x + padding.width;
		for(int i=0;i<lines.size();i++)
		{
			drawString(lines.get(i), startX, startY);
			startY += textHeight + 4;
		}
	}
	
	public void drawString(String s, int x, int y){
		drawString(s, x, y, false);
	}
	
	public void drawString(String s, int x, int y, boolean isSelected) {
		if(s != null && s.length() > 0)
			TBSGraphics.drawCenteredString(g2, s, x, y, 0, textHeight + 4, 
				isSelected ? TBSGraphics.emptyNodeColor : Color.BLACK);
	}
/*	
	public void drawButtons()
	{
		Rectangle buttonRect = new Rectangle(buttonsArea.x, buttonsArea.y,
				buttonsArea.width/buttons.size(), buttonsArea.height);
		for(TBSPromptButtonType button: buttons) {
			TBSGraphics.renderButtonBackground(g2, buttonRect, false);
			g2.setColor(Color.gray);
			g2.draw(buttonRect);
			TBSGraphics.drawCenteredString(g2, button.toString(), buttonRect.x,
					buttonRect.y + (buttonRect.height - 2), buttonRect.width, 0);
			buttonRect.setLocation(buttonRect.x + buttonRect.width, buttonRect.y);
		}
	}
*/	
	
	
	public boolean isOverButton(MouseEvent e){
		if(buttonsArea.contains(e.getPoint()))
			return true;
		return false;
	}
	
}

