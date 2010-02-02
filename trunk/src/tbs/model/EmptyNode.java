//TBS version 0.4
//EmptyNode: connector node, represents a hypothetical common ancestor

package tbs.model;

import java.awt.Point;

import tbs.TBSGraphics;


/**
* EmptyNodes are the junctions by which Nodes are joined to one another. 
*/	
public class EmptyNode extends Node 
{
	private int alteredWidth = -1;
	private boolean beingLabeled;
	
	/**
	* EmptyNode(model) sets name to null, position to the default
	* (left-hand panel, beneath ONodes)
	*/
	public EmptyNode(int id) {
		super(id, "");
		setAnchorPoint(new Point(TBSGraphics.emptyNodeLeftX, TBSGraphics.emptyNodeUpperY));
		beingLabeled = false;
		System.out.println("Created EmptyNode #" +id);
	}
	
	public int getHeight() {
		return TBSGraphics.emptyNodeHeight;
	}
	
	public int getWidth() {
		if(alteredWidth != -1)
			return alteredWidth;
		return TBSGraphics.emptyNodeWidth;
	}
	
	public void setAlteredWidth(int alteredWidth){
		this.alteredWidth = alteredWidth;
	}

	public boolean isBeingLabeled() {return beingLabeled;}

	public void setBeingLabeled(boolean beingLabeled) {this.beingLabeled = beingLabeled;}

	public Point getDefaultPoint() {
		return new Point(TBSGraphics.emptyNodeLeftX, TBSGraphics.emptyNodeUpperY);
	}
}
