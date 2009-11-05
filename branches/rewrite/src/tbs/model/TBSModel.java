package tbs.model;
//TBSModel v0.03

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
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
		createButtons(g); // call before creating model elements
		createModelElements(g, organismNameToImage);
		view = new TBSView(this);
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
		Graphics2D g2 = (Graphics2D) g;
		TBSGraphics.getFont(g2);
		TBSGraphics.buttons = new ArrayList<String>();
		TBSGraphics.buttons.add("link");
		TBSGraphics.buttons.add("unlink");
		TBSGraphics.buttons.add("label");
		TBSGraphics.buttons.add("delete");
		TBSGraphics.buttons.add("split");
		TBSGraphics.buttons.add("print");
		TBSGraphics.buttons.add("undo");
		TBSGraphics.buttons.add("save");
		Point buttonBounds = TBSGraphics.get2DStringBounds(g2, TBSGraphics.buttons);
		TBSGraphics.buttonsWidth = buttonBounds.x + TBSGraphics.buttonsXPadding * 2;
		TBSGraphics.buttonsHeight = buttonBounds.y + TBSGraphics.buttonsYPadding * 2;
	}
	
	// called during setup to create organism nodes
	protected void createModelElements(Graphics g, TreeMap<String, BufferedImage> organismNameToImage) {
		Graphics2D g2 = (Graphics2D) g;
		BufferedImage img = null;
		int currentX = 0;
		int currentY = TBSGraphics.buttonsHeight + 10;
		String organismName = "";
		Collection<String> organismNames = organismNameToImage.keySet();
		Collection<BufferedImage> organismImages = organismNameToImage.values();
		Point stringBounds = TBSGraphics.get2DStringBounds(g2, organismNames);
		Point imageBounds = TBSGraphics.get2DImageBounds(g2, organismImages);
		TBSGraphics.organismNodeWidth = stringBounds.x + imageBounds.x + TBSGraphics.paddingWidth * 2;
		TBSGraphics.organismNodeHeight = imageBounds.y;
		int y0 = stringBounds.y;
		int y1 = imageBounds.y;
		if(y0 > y1) {
			TBSGraphics.organismNodeHeight = y0;
		} else {
			TBSGraphics.organismNodeHeight = y1;
		}
		Iterator<String> itr = organismNames.iterator();
		while(itr.hasNext()) {
			organismName = itr.next();
			img = organismNameToImage.get(organismName);
			addElement(new OrganismNode(img, organismName, currentX, currentY, TBSGraphics.organismNodeWidth, TBSGraphics.organismNodeHeight));
			currentY += TBSGraphics.organismNodeHeight + TBSGraphics.ySpacing;
		}
		// leave commented unless testing
		TBSGraphics.emptyNodeLeftX = TBSGraphics.organismNodeWidth / 2 - TBSGraphics.emptyNodeWidth / 2;
		TBSGraphics.emptyNodeUpperY = currentY + TBSGraphics.organismNodeHeight + TBSGraphics.emptyNodeYLabelOffset; 
		addElement(new EmptyNode(this));
		//20 is arbitrary, to move it away from the side so you can see
		//it better. Change at whim.
	}	
	
}