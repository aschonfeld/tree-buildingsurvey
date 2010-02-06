//TBS version 0.4
//OrganismNode: represents "organisms" manipulated by user

package tbs.model;

import java.awt.Point;
import java.awt.image.BufferedImage;

import tbs.TBSGraphics;

public class OrganismNode extends Node
{
	private BufferedImage img;
	private String imgFileName;
	private Point defaultPoint;

	public OrganismNode(int id, String name, Point anchorPoint, BufferedImage i) 
	{
		super(id, name);
		img = i;
		defaultPoint = new Point();
		//This is crude and brittle; should be changed to pass image file name in setup
		//rather than reverse engineering it from the name of the organisme. Works for now,
		//though.
		imgFileName = name.toLowerCase().replace(" ", "") +".gif";
	}

	public BufferedImage getImage() {return img;}
	
	/**
	* Returns the name of the image file associated with this object
	*/
	public String getImgFileName(){return imgFileName;}

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
	
	public String toString(){
		return getName() + " Node";
	}

}
