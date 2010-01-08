
package tbs.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.Timer;
import tbs.model.EmptyNode;
import java.awt.Graphics2D;

public class TextEntryBox implements ActionListener {

	private Timer timer = null;
	private Color offColor = Color.white;
	private Color onColor = Color.darkGray;
	private Color stringColor = Color.black;
	private Color backgroundColor = Color.white;
	private Color borderColor = Color.yellow;
	private int cursorWidth = 2;
	private boolean cursorIsOn = true;
	private int flashRateInHz = 2;

	public TextEntryBox() {
		timer = new Timer(1000 / flashRateInHz, this);
 		timer.start();
	}
	
	public void actionPerformed(ActionEvent e) {
		cursorIsOn = !cursorIsOn;
	}
	
	public void renderCursor(Graphics2D g2, Point upperLeft, Point size) {
		if(cursorIsOn) {
			g2.setColor(onColor);
		} else {
			g2.setColor(offColor);
		}
		g2.fillRect(upperLeft.x, upperLeft.y, size.x, size.y);
	}
	
	public void renderTextEntryBox(Graphics2D g2, EmptyNode en, int yOffset) 
	{
		int leftX = (int) en.getAnchorPoint().getX();
		int upperY = (int) en.getAnchorPoint().getY() - yOffset;
		int width = en.getWidth();
		int height = en.getHeight();
		String s = en.getName();
		int x, y;
		// calculate cursor dimensions
		if(s == null || s.length() == 0) {
			g2.setColor(backgroundColor);
			g2.fillRect(leftX, upperY, width, height);
			g2.setColor(backgroundColor);
			g2.fillRect(leftX, upperY, width, height);
			renderCursor(g2, new Point(leftX, upperY), new Point(cursorWidth, height));
			g2.setColor(borderColor);
			g2.drawRect(leftX - 1, upperY, width, height);
			return;
		}
		// draw background
		g2.setColor(backgroundColor);
		g2.fillRect(leftX, upperY, width, height);
		// draw string starts here
		g2.setColor(stringColor);
		// calculate dimensions of String s
		TextLayout layout = new TextLayout(s, g2.getFont(), g2.getFontRenderContext());
		Rectangle2D bounds = layout.getBounds();
		int stringHeight = (int) bounds.getHeight();
		int stringWidth = (int) bounds.getWidth();
		if(width == 0)
			x = leftX;
		else
			x = leftX + (width - stringWidth) / 2;
		if(height == 0)
			y = upperY;
		else
			y = upperY + height - (height - stringHeight) / 2;
		// if width or height is 0, do not center along that axis
		layout.draw(g2, x, y);
		renderCursor(g2, new Point(x + stringWidth + 2, upperY), new Point(cursorWidth, height));
		g2.setColor(borderColor);
		int boxWidth = (x + stringWidth + 2 + cursorWidth) - leftX;
		if(width > boxWidth) boxWidth = width;
		g2.drawRect(leftX, upperY, boxWidth, height);
	}
	
	public void drawBorder(int x, int y, int width, int height) {
		
	}
	
}
