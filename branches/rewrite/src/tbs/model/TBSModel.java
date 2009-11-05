package tbs.model;
//TBSModel v0.03

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import tbs.TBSGraphics;
import tbs.controller.TBSController;
import tbs.view.TBSView;

public class TBSModel 
{
	private TBSView view;
	private TBSController controller;
	private ArrayList<ModelElement> modelElements;


	public TBSModel(Graphics g, TreeMap<String, BufferedImage> organismNameToImage) {
		modelElements = new ArrayList<ModelElement>();
		createModelElements(g, organismNameToImage);
		view = new TBSView(this);
		controller = new TBSController(this, view);
		createButtons(g); // need to come after view is created
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

	public ModelElement getElement(int i) {
		return modelElements.get(i);
	}
	
	public ArrayList<ModelElement> getElements() {
		return modelElements;
	}

	public void setElement(int i, ModelElement me) {
		modelElements.set(i, me);
	}
	
	public void createButtons(Graphics g)
	{
		int screenWidth=800; //currently, could change - pass this from Applet to allow resizing
		int currentX=0;	//start at left edge
		TBSGraphics.buttonHeight= 20; //arbitrary, tweak to fit
		int buttonCount = 8;
		TBSGraphics.buttonWidth = screenWidth / buttonCount; 	//all buttons same width, occupy whole screen
		Graphics2D g2 = (Graphics2D) g;
		TBSGraphics.linkButton = new TBSButton(this, "Link", getStringBounds(g2, "Link"), TBSButton.LINK, 
			 currentX,0, TBSGraphics.buttonHeight, TBSGraphics.buttonWidth);
		currentX+=TBSGraphics.buttonWidth;

		TBSGraphics.unlinkButton = new TBSButton(this, "Unlink", getStringBounds(g2, "Unlink"), TBSButton.UNLINK, 
			 currentX,0, TBSGraphics.buttonHeight, TBSGraphics.buttonWidth);
		currentX+=TBSGraphics.buttonWidth;
		TBSGraphics.labelButton = new TBSButton(this, "Label", getStringBounds(g2, "Label"), TBSButton.LABEL, 
			 currentX,0, TBSGraphics.buttonHeight, TBSGraphics.buttonWidth);
		currentX+=TBSGraphics.buttonWidth;
		TBSGraphics.deleteButton = new TBSButton(this, "Delete", getStringBounds(g2, "Delete"), TBSButton.DELETE, 
			 currentX,0, TBSGraphics.buttonHeight, TBSGraphics.buttonWidth);
		currentX+=TBSGraphics.buttonWidth;
		TBSGraphics.splitButton = new TBSButton(this, "Split", getStringBounds(g2, "Split"), TBSButton.SPLIT, 
			 currentX,0, TBSGraphics.buttonHeight, TBSGraphics.buttonWidth);
		currentX+=TBSGraphics.buttonWidth;
		TBSGraphics.printButton = new TBSButton(this, "Print", getStringBounds(g2, "Print"), TBSButton.PRINT, 
			 currentX,0, TBSGraphics.buttonHeight, TBSGraphics.buttonWidth);
		currentX+=TBSGraphics.buttonWidth;
		TBSGraphics.undoButton = new TBSButton(this, "Undo", getStringBounds(g2, "Undo"), TBSButton.UNDO, 
			 currentX,0, TBSGraphics.buttonHeight, TBSGraphics.buttonWidth);
		currentX+=TBSGraphics.buttonWidth;
		TBSGraphics.saveButton = new TBSButton(this, "Save", getStringBounds(g2, "Save"), TBSButton.SAVE, 
			 currentX,0, TBSGraphics.buttonHeight, TBSGraphics.buttonWidth);
		currentX+=TBSGraphics.buttonWidth;
	
		addElement(TBSGraphics.linkButton);
		addElement(TBSGraphics.unlinkButton);
		addElement(TBSGraphics.labelButton);
		addElement(TBSGraphics.deleteButton);
		addElement(TBSGraphics.splitButton);
		addElement(TBSGraphics.printButton);
		addElement(TBSGraphics.undoButton);
		addElement(TBSGraphics.saveButton);
	}
	// called during setup to create organism nodes
	protected void createModelElements(Graphics g, TreeMap<String, BufferedImage> organismNameToImage) {
		Graphics2D g2 = (Graphics2D) g;
		TreeMap<String, Rectangle2D> organismNameToStringBounds;
		organismNameToStringBounds = new TreeMap<String, Rectangle2D>();
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
		TBSGraphics.organismNodeWidth = maxNameWidth + (2 * TBSGraphics.paddingWidth) + maxImageWidth;
		if(maxImageHeight > maxNameHeight) {
			TBSGraphics.organismNodeHeight = maxImageHeight;
		} else {
			TBSGraphics.organismNodeHeight = maxNameHeight;
		}
		currentX = 0;
		currentY = TBSGraphics.ySpacing +25;
		while(itr.hasNext()) {
			organismName = itr.next();
			img = organismNameToImage.get(organismName);
			rect = organismNameToStringBounds.get(organismName);
			addElement(new OrganismNode(img, organismName, rect, currentX, currentY, TBSGraphics.organismNodeWidth, TBSGraphics.organismNodeHeight));
			currentY += TBSGraphics.organismNodeHeight + TBSGraphics.ySpacing;
		}
		// leave commented unless testing
		addElement(new EmptyNode(this, 70,575, "Empty"));
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
			f = new Font(TBSGraphics.fontName, TBSGraphics.fontStyle, TBSGraphics.fontSize);
			g2.setFont(f);
		}
   		FontRenderContext frc = g2.getFontRenderContext();
   		TextLayout layout = new TextLayout(name, f, frc);
   		return layout.getBounds();
	}
	
	
}
