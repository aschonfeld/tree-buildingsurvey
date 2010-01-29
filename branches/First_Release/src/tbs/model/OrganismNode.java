//TBS version 0.4
//OrganismNode: represents "organisms" manipulated by user

package tbs.model;

import java.awt.Point;
import java.awt.image.BufferedImage;

import tbs.TBSGraphics;

public class OrganismNode extends Node
{
	private BufferedImage img;
	private Point defaultPoint;
		
	public OrganismNode(int id, String name, Point anchorPoint, BufferedImage i) 
	{
		super(id, name, anchorPoint, TBSGraphics.organismNodeHeight, TBSGraphics.organismNodeWidth);
		img = i;
			//This is crude and brittle; should be changed to pass image file name in setup
			//rather than reverse engineering it from the name of the organisme. Works for now,
			//though.
		imgFileName = name.toLowerCase().replace(" ", "") +".gif";
		
		defaultPoint = anchorPoint;
	}
				
	public void resetPosition(){
		setAnchorPoint(defaultPoint);
	}
		
	public BufferedImage getImage() {return img;}
	
	public int getHeight() {
		if(getX() > 0) {
			return img.getHeight();
		}
		return getDefaultHeight();
	}
	
	public int getWidth() {
		if(getX() > 0) {
			return img.getWidth();
		}
		return getDefaultWidth();
	}
	
	public Point getDefaultPoint() {
		return defaultPoint;
	}
	
	public void reset(){
		getConnectedTo().clear();
		getConnectedFrom().clear();
		setInTree(false);
		resetPosition();
	}

	public boolean isBeingLabeled() {return false;}

	public void setBeingLabeled(boolean beingLabeled) {}
	
}
