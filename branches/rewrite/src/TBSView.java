//TBSView.java version 0.01

import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.io.*;

public class TBSView extends JComponent {

	private TBSModel model;

			//boundary between active and inactive elements. Name can be
			//changed. 
	public static int LINE_OF_DEATH = 120;

	
	// Contains the length and width of all organism nodes
	private int organismNodeWidth;
	private int organismNodeHeight;
	
	// minimum number of pixels around the right and left of an organism's name
	private int paddingWidth;
	
	// Space between bottom and top of images
	private int ySpacing;
	
	// Font Properties
	private String fontName; // Use default font
	private int fontStyle;
	private int fontSize;
	
	public TBSView(TBSModel m, int oNW, int oNH, int pw, int ys, String fName, int fStyle, int fSize) {
        model = m;
        organismNodeWidth = oNW;
        organismNodeHeight = oNH;
        paddingWidth = pw;
        ySpacing = ys;
        fontName = fName;
        fontStyle = fStyle;
        fontSize = fSize;
	}
	
	public void drawString(Graphics2D g2, OrganismNode on, int xOffset) {
		// ReneringHints tell
		g2.setColor(Color.black);
		int stringHeight = 0;
		int stringWidth = 0;
		int x = 0;
		int y = 0;
		RenderingHints rh = new RenderingHints(
		RenderingHints.KEY_TEXT_ANTIALIASING,
		RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		x = xOffset;
		stringHeight = (int) on.getStringBounds().getHeight();
		y = on.getUpperY() + organismNodeHeight - (organismNodeHeight - stringHeight) / 2;
   		Point2D loc = new Point(x, y);
   		Font f = new Font(fontName, fontStyle, fontSize);
   		g2.setFont(f);
   		FontRenderContext frc = g2.getFontRenderContext();
   		TextLayout layout = new TextLayout(on.getName(), f, frc);
   		layout.draw(g2, (float)loc.getX(), (float)loc.getY());
		Rectangle2D bounds = layout.getBounds();
	}
	
	public void renderModelElement(Graphics g, ModelElement me) {
		Graphics2D g2 = (Graphics2D) g;
		int stringWidth = 0;
		int imageWidth = 0;
		int imageStartX = 0;
		int stringStartX = 0;
		if(me instanceof OrganismNode) {
			OrganismNode on = (OrganismNode) me;
			stringWidth = (int) on.getStringBounds().getWidth();
			imageWidth = on.getImage().getWidth();
			// center image and text
			int imageXOffset = (organismNodeWidth - imageWidth - stringWidth) / 2;
			imageStartX = on.getLeftX() + imageXOffset;
			stringStartX = on.getLeftX() + imageXOffset + imageWidth + paddingWidth;
			g2.setColor(Color.white);
			g2.fillRect(on.getLeftX(), on.getUpperY(), organismNodeWidth, organismNodeHeight);
			g2.drawImage(on.getImage(), imageStartX, on.getUpperY(), null);
			drawString(g2, on, stringStartX);
		}
	}
	
	public void refreshGraphics() {
		repaint();	
	}

	// this is what the applet calls to refresh the screen
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.black);
		g2.fillRect(0, 0, getWidth(), getHeight());
		refreshGraphics();
		Iterator<ModelElement> itr = model.getElements().iterator();
		while(itr.hasNext()) {
			renderModelElement(g, itr.next());
		}
	}
}
