//TBS version 0.4
//OrganismNode: represents "organisms" manipulated by user

package tbs.model;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
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
		defaultPoint = anchorPoint;
	}
				
	public void resetPosition(){
		setAnchorPoint(defaultPoint);
	}
		
	public BufferedImage getImage() {return img;}
	
	public Rectangle2D getRectangle(){
		return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
	}
	
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
	
}
