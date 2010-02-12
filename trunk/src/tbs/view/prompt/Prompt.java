package tbs.view.prompt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import tbs.TBSGraphics;
import tbs.model.AdminModel;
import tbs.model.TBSModel;
import tbs.view.AdminView;

/**
* Prompts are used to display information of various sorts on the
* screen; they are essentially text boxes capable of bearing various
* sorts of buttons.
*/
public abstract class Prompt {
	
	private Graphics2D g2;
	private Point anchorPoint;
	private Dimension promptSize;
	private Rectangle closeButton;
	private Rectangle bottomButtons;
	private int buttonHeight = TBSGraphics.textHeight + TBSGraphics.padding.height;
	private int stringY = 0;
	
	private TBSModel model;
	private boolean finished;
	private boolean renderButtonsAndString;
	private boolean renderElements;
	
	public Prompt(boolean renderButtonsAndString, boolean renderElements, Dimension promptSize,
			TBSModel model){
		finished = false;
		this.renderButtonsAndString = renderButtonsAndString;
		this.renderElements = renderElements;
		this.promptSize = promptSize;
		anchorPoint = new Point();
		closeButton = new Rectangle();
		bottomButtons = new Rectangle();
		this.model = model;
	}
	
	public Graphics2D getGraphics() {return g2;}
	public void setGraphics( Graphics2D g2 ) {this.g2 = g2;}
	public Dimension getPromptSize() {return promptSize;}
	public int getWidth(){return promptSize.width;}
	public int getUnpaddedWidth(){return promptSize.width - (TBSGraphics.padding.width*2);}
	public int getHeight(){return promptSize.height;}
	public void setAnchorPoint(Point anchorPoint){this.anchorPoint = anchorPoint;}
	public int getX(){return anchorPoint.x;}
	public int getY(){return anchorPoint.y;}
	public Rectangle getCloseButton() {return closeButton;}
	public void setCloseButton( Rectangle closeButton ) {this.closeButton = closeButton;}
	public Rectangle getBottomButtons() {return bottomButtons;}
	public int getStringY() {return stringY;}
	public void setStringY( int stringY ) {this.stringY = stringY;}
	public void incrementStringY(){ stringY += buttonHeight;}
	public void incrementStringY(int value){ stringY += value;}
	public void incrementStringYMulti(int value){ stringY += buttonHeight * value;}

	public void setBottomButtons( Rectangle bottomButtons ) {
		this.bottomButtons = bottomButtons;
	}
	public boolean renderButtonsAndString() {return renderButtonsAndString;}
	public boolean renderElements() {return renderElements;}
	/**
	* Returns true if Prompt is ready to close
	*/
	public boolean isFinished() {
		return finished;
	}

	/**
	* Sets a flag; if input value is true, Prompt will close itself
	*/
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
	/**
	* Contains instructions for painting this Prompt to the screen
	*/
	public abstract void paintComponent(Graphics2D g2);
	
	/**
	* Returns true if {@link MouseEvent} e has x,y coordinates within one
	* of this Prompt's buttons
	*/
	public abstract boolean isOverButton(MouseEvent e);
	
	public Cursor getCursor( MouseEvent e ) {
		if(isOverButton(e))
			return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
		else
			return Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	}
	
	/**
	* Deals with mouse button clicks
	*/
	public abstract void mousePressed(MouseEvent e);
	
	/**
	 * Deals with keyboard input
	 */
	public abstract void keyPressed(KeyEvent e);

	/**
	 * Deals with other keyboard input
	 */
	public abstract void keyTyped(KeyEvent e);
	
	public void calculateValues(int lineCount, boolean hasClose, boolean hasBottomButtons) {
		calculateValues(lineCount, 0, hasClose, hasBottomButtons);
	}
	
	/**
	 * Determines values of several interesting numbers, including the
	 * center point of the applet, an anchor point for the top left corner
	 * of the Prompt and locations of the close and selector buttons. 
	 */
	public void calculateValues(int lineCount, int extraHeight, boolean hasClose, boolean hasBottomButtons) {
		promptSize.setSize(promptSize.width, (TBSGraphics.textHeight * lineCount) + (TBSGraphics.padding.height * (lineCount + 1)) + extraHeight);
		
		int centerX;
		if(model instanceof AdminModel){
			AdminView view = (AdminView) model.getView();
			int scrollWidth = view.hasStudentScroll() ? view.getStudentBar().getWidth() : 0;
			int studentButtonWidth = TBSGraphics.maxStudentNameWidth + TBSGraphics.checkWidth + TBSGraphics.arrowWidth;
			int adminWidth = model.getApplet().getWidth() - (view.getVerticalBar().getWidth() + scrollWidth + studentButtonWidth);
			centerX =  (adminWidth / 2) + scrollWidth + studentButtonWidth;
		}else
			centerX = model.getApplet().getWidth() / 2;
		
		int centerY = model.getApplet().getHeight() / 2;
		anchorPoint = new Point(centerX - (promptSize.width / 2), centerY - (promptSize.height / 2));
		
		if(hasBottomButtons)
			setBottomButtons(new Rectangle(anchorPoint.x, anchorPoint.y + (promptSize.height - buttonHeight),
					promptSize.width, buttonHeight));
		if(hasClose)
			setCloseButton(new Rectangle((anchorPoint.x + promptSize.width)-buttonHeight,
							anchorPoint.y,buttonHeight,buttonHeight));
		stringY = anchorPoint.y;
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
	
	/**
	 *	Passes s, x, and y to drawString(String s, int x, int y, boolean
	 *	isSelected) with "false" as the final value.
	 */
	public void drawString(String s, int x, int y){
		drawString(s, x, y, false);
	}

	/**
	 * Calls the drawCenteredString method from {@link TBSGraphics} to put
	 * a string on the screen. Default color for selected text is the same
	 * as used for EmptyNode, but this can be changed. 
	 */
	public void drawString(String s, int x, int y,  boolean isSelected){
		if(s != null && s.length() > 0)
			TBSGraphics.drawCenteredString(g2, s, x, y, 0, TBSGraphics.textHeight + 4, isSelected ? TBSGraphics.selectedPromptTextColor : Color.BLACK);
	}
	
	public void drawText(List<String> lines){
		drawText(lines, false);
	}
	public void drawText(List<String> lines, boolean selected) {
		int startX = anchorPoint.x + TBSGraphics.padding.width;
		for(String line : lines){
			drawString(line, startX, stringY, selected);
			incrementStringY();
		}
	}
	
	public void drawHeader(String s){
		TBSGraphics.drawCenteredString(g2, s, anchorPoint.x + TBSGraphics.padding.width,
				stringY, promptSize.width - (TBSGraphics.padding.width*2),
				buttonHeight,TBSGraphics.selectedPromptTextColor);
	}
	
	public void drawButtons(Object[] buttons){
		drawButtons(buttons, null);
	}
	/**
	 * Draws the close button and selector buttons. Buttons, as elsewhere
	 * in TBS, are not objects, but are simply painted on the screen and
	 * checked by contains() methods. 
	 */
	public void drawButtons(Object[] buttons, String selected)
	{
		if(buttons.length > 0){
			Rectangle buttonRect = new Rectangle(bottomButtons.x, bottomButtons.y,
					bottomButtons.width/buttons.length, bottomButtons.height);
			for(Object button: buttons) {
				TBSGraphics.renderButtonBackground(g2, buttonRect, button.toString().equals(selected));

				g2.setColor(Color.gray);
				g2.draw(buttonRect);
				TBSGraphics.drawCenteredString(g2, button.toString(), buttonRect.x,
						buttonRect.y + (buttonRect.height - 2), buttonRect.width, 0);
				buttonRect.setLocation(buttonRect.x + buttonRect.width, buttonRect.y);
			}
		}
	}
	
	public void drawCloseButton(){
		TBSGraphics.renderButtonBackground(g2, closeButton, false);
		g2.setColor(Color.BLACK);
		g2.setStroke(TBSGraphics.closeButtonStroke);
		g2.draw(closeButton);
		int x,y,w,h;
		x = closeButton.x+1;
		y = closeButton.y+1;
		w = closeButton.width-1;
		h = closeButton.height-1;
		g2.draw(new Line2D.Double(x,y,x+w,y+h));
		g2.draw(new Line2D.Double(x,y+h,x+w,y));
		g2.setStroke(new BasicStroke());
	}
}
