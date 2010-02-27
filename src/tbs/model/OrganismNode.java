//TBS version ????
//OrganismNode: represents "organisms" manipulated by user

package tbs.model;

import java.awt.Point;
import java.awt.image.BufferedImage;

import tbs.TBSGraphics;
import tbs.graphanalysis.Vertex;
import tbs.graphanalysis.VertexInfo;

public class OrganismNode extends Node
{
	private BufferedImage img;
	private String organismType;
	private Point defaultPoint;
	private int stringWidth;
	private int imageStartX = -1;
	private int stringAreaLeftX = -1;

	public OrganismNode(int id, String name, String organismType, Point anchorPoint, BufferedImage i, int stringWidth) 
	{
		super(id, name);
		this.organismType = organismType;
		img = i;
		defaultPoint = new Point();
		this.stringWidth = stringWidth;
	}

	public BufferedImage getImage() {return img;}
	

	public int getHeight() {
		if(getX() > 0)
			return img.getHeight();
		return TBSGraphics.organismNodeHeight;
	}

	public int getWidth() {
		if(getX() > 0)
			return img.getWidth();
		return TBSGraphics.organismNodeWidth;
	}

	public Point getDefaultPoint() {
		return new Point(0, (TBSGraphics.buttonsHeight + 10) + (getId()*(TBSGraphics.organismNodeHeight + TBSGraphics.ySpacing)));
	}

	public void reset(){
		getConnectedTo().clear();
		getConnectedFrom().clear();
		setInTree(false);
		setAnchorPoint(defaultPoint);
	}

	public boolean isBeingLabeled() {return false;}

	public void setBeingLabeled(boolean beingLabeled) {}
	
	public int getImageStartX() {
		if(TBSGraphics.organismNodeWidth > 0 && imageStartX < 0)
			imageStartX = getDefaultPoint().x + ((TBSGraphics.organismNodeWidth - (img.getWidth() + stringWidth)) / 2);
		return imageStartX;
	}

	public int getStringAreaLeftX() {
		if(stringAreaLeftX < 0)
			stringAreaLeftX = getImageStartX() + img.getWidth() + TBSGraphics.padding.height;
		return stringAreaLeftX;
	}

	public int getStringWidth() {
		return stringWidth;
	}

	public String getOrganismType() {
		return organismType;
	}
	
	/*
	 * This is a default method that is used by setScreenString in the 
	 * Controller & also some logging to get information about a
	 * ModelElement object.  Even though the element is a ModelElement
	 * when toString is called it will refer to the resulting subclass,
	 * in this case OrganismNode. 
	 */
	public String toString(){
		return getName() + " Node";
	}

	public Vertex convertToVertex() {
		return new Vertex(new VertexInfo(getName(), organismType, img), getAnchorPoint());
	}
}
