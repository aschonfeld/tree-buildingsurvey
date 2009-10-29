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
	
	private ArrayList<TBSModel.ModelElement> viewElements = null;
	private TBSController controller;
	private TBSModel tbsModel;
	
	// For internal use only
	private TreeMap<String, BufferedImage> organismNameToImage;
	
	// Contains the length and width of all organism nodes
	private int organismNodeWidth = 0;
	private int organismNodeHeight = 0;
	
	// minimum number of pixels around the right and left of an organism's name
	private int paddingWidth = 5;
	
	// Space between bottom and top of images
	private int ySpacing = 1;
	
	// Font Properties
	private String fontName = null; // Use default font
	private int fontStyle = Font.PLAIN;
	private int fontSize = 16;
	
	public TBSView(TBSModel tbsM, TreeMap<String, BufferedImage> oNTI) {
        organismNameToImage = oNTI;
        super.paintComponent(getGraphics());
        tbsModel = tbsM;
	}
	
	// called the first time this component is drawn by paintComponent
	protected void createModelElements(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		TreeMap<String, Rectangle2D> organismNameToStringBounds;
		organismNameToStringBounds = new TreeMap<String, Rectangle2D>();
		ImageObserver imageObserver = null;
		BufferedImage img = null;
		Rectangle2D rect = null;
		int currentX = 0;
		int currentY = 0;
		int intRectWidth = 0;
		int intRectHeight = 0;
		int maxNameWidth = 0; // widest name string in pixels
		int maxNameHeight = 0; // tallest name string in pixels
		int maxImageWidth = 0; // widest image in pixels
		int maxImageHeight = 0; // tallest image in pixels
		String organismName = "";
		Set<String> organismNames = organismNameToImage.keySet();
		Iterator<String> itr = organismNames.iterator();
		TBSModel.ModelElement on = null;
		while(itr.hasNext()) {
			organismName = itr.next();
			img = organismNameToImage.get(organismName);
			if (img.getWidth() > maxImageWidth) maxImageWidth = img.getWidth();
			if (img.getHeight() > maxImageHeight) maxImageHeight = img.getHeight();
			rect = getStringBounds(g2, organismName);
			intRectWidth = (int) rect.getWidth();
			intRectHeight = (int) rect.getHeight();
			if (intRectWidth > maxNameWidth) maxNameWidth = intRectWidth;
			if (intRectHeight > maxNameHeight) maxNameHeight = intRectWidth;
			organismNameToStringBounds.put(organismName, rect);
		}
		itr = organismNames.iterator();
		organismNodeWidth = maxNameWidth + (2 * paddingWidth) + maxImageWidth;
		if(maxImageHeight > maxNameHeight) {
			organismNodeHeight = maxImageHeight;
		} else {
			organismNodeHeight = maxNameHeight;
		}
		currentX = 0;
		currentY = ySpacing;
		Rectangle2D currentRect = null;
		BufferedImage currentImage = null;
		while(itr.hasNext()) {
			organismName = itr.next();
			img = organismNameToImage.get(organismName);
			rect = organismNameToStringBounds.get(organismName);
			tbsModel.addElement(new TBSModel.OrganismNode(img, organismName, rect, currentX, currentY, organismNodeWidth, organismNodeHeight));
			currentY += organismNodeHeight + ySpacing;
		}
	}
	
	// calculate the area occupied by a string
	public Rectangle2D getStringBounds(Graphics2D g2, String name) {
		// ReneringHints tell
		RenderingHints rh = new RenderingHints(
		RenderingHints.KEY_TEXT_ANTIALIASING,
		RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
   		Font f = new Font(fontName, fontStyle, fontSize);
   		g2.setFont(f);
   		FontRenderContext frc = g2.getFontRenderContext();
   		TextLayout layout = new TextLayout(name, f, frc);
   		return layout.getBounds();
	}
	
	public void drawString(Graphics2D g2, TBSModel.OrganismNode on, int xOffset) {
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
	
	public void renderModelElement(Graphics g, TBSModel.ModelElement me) {
		Graphics2D g2 = (Graphics2D) g;
		int stringWidth = 0;
		int imageWidth = 0;
		int imageStartX = 0;
		int stringStartX = 0;
		if(me instanceof TBSModel.OrganismNode) {
			TBSModel.OrganismNode on = (TBSModel.OrganismNode) me;
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
		refreshGraphics();
	}
	
	public void refreshGraphics() {
		repaint();	
	}

	// this is what the applet calls to refresh the screen
	public void paintComponent(Graphics g) {
		if(tbsModel.numElements() == 0) createModelElements(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.black);
		g2.fillRect(0, 0, getWidth(), getHeight());
		refreshGraphics();
		Iterator<TBSModel.ModelElement> itr = tbsModel.getElements().iterator();
		while(itr.hasNext()) {
			renderModelElement(g, itr.next());
		}
	}
}
