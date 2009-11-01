//TBSModel v0.02

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

public class TBSModel 
{
	private TBSView view;
	private TBSController controller;
	private ArrayList<ModelElement> modelElements;
	
	// Contains the length and width of all organism nodes
	private int organismNodeWidth = 0;
	private int organismNodeHeight = 0;
	
	// minimum number of pixels around the right and left of an organism's name
	private int paddingWidth = 5;
	
	// Space between bottom and top of images
	private int ySpacing = 1;
	
	// Font Properties
	private String fontName = "default"; // Use default font
	private int fontStyle = Font.PLAIN;
	private int fontSize = 16;
	
	public TBSModel(Graphics g, TreeMap<String, BufferedImage> organismNameToImage) {
		modelElements = new ArrayList<ModelElement>();
		createModelElements(g, organismNameToImage);
		view = new TBSView(this, organismNodeWidth, organismNodeHeight, paddingWidth, ySpacing, fontName, fontStyle, fontSize);
		controller = new TBSController(this, view);
	}
	
	public TBSView getView() {
		return view;
	}
	
	public TBSController getController() {
		return controller;
	}	
	
	public void addElement(ModelElement m) {
		modelElements.add(m);
	}

	public void delete(EmptyNode en)
	{
		modelElements.remove(en);		
	}	

	public int numElements() {
		return modelElements.size();
	}
	
	public ModelElement getElement(int i) {
		return modelElements.get(i);
	}
	
	public ArrayList<ModelElement> getElements() {
		return modelElements;
	}

	public void setElement(int i, ModelElement me) {
		modelElements.set(i, me);
	}
	
	// called during setup to create organism nodes
	protected void createModelElements(Graphics g, TreeMap<String, BufferedImage> organismNameToImage) {
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
		ModelElement on = null;
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
			addElement(new OrganismNode(img, organismName, rect, currentX, currentY, organismNodeWidth, organismNodeHeight));
			currentY += organismNodeHeight + ySpacing;
		}
		// leave commented unless testing
		// addElement(new EmptyNode(this, currentX+20, currentY, "Empty"));
		//20 is arbitrary, to move it away from the side so you can see
		//it better. Change at whim.
	}
	
	// calculate the area occupied by a string using default font
	public Rectangle2D getStringBounds(Graphics2D g2, String name) {
		return getStringBounds(g2, name, null);
	}
	
	// calculate the area occupied by a string
	public Rectangle2D getStringBounds(Graphics2D g2, String name, Font f) {
		// ReneringHints tell
		RenderingHints rh = new RenderingHints(
		RenderingHints.KEY_TEXT_ANTIALIASING,
		RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		if(f == null) {
			// default font
			f = new Font(fontName, fontStyle, fontSize);
			g2.setFont(f);
		}
   		FontRenderContext frc = g2.getFontRenderContext();
   		TextLayout layout = new TextLayout(name, f, frc);
   		return layout.getBounds();
	}
	
	
}
