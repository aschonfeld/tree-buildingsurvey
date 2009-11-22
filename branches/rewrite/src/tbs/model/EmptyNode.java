//TBS version 0.4
//EmptyNode: connector node, represents a hypothetical common ancestor

package tbs.model;

import java.awt.Point;
import java.awt.geom.Rectangle2D;

import tbs.TBSGraphics;


/**
* EmptyNodes are the junctions by which Nodes are joined to one another. 
*/	
public class EmptyNode extends Node 
{
	/**
	* EmptyNode's fully-specified constructor sets position and name
	* explicitly. This is never called. 
	*/
	public EmptyNode(int id, Point anchorPoint) {
		super(id, String.format(TBSGraphics.emptyNodeLabel, id), anchorPoint,
				TBSGraphics.emptyNodeHeight, TBSGraphics.emptyNodeWidth);
		setInTree(true);
		System.out.println("Added EmptyNode #" +id);
	}
	
	/**
	* EmptyNode(model) sets name to null, position to the default
	* (left-hand panel, beneath ONodes)
	*/
	public EmptyNode(int id) {
		super(id, TBSGraphics.emptyNodeDefaultLabel, new Point(TBSGraphics.emptyNodeLeftX,
				TBSGraphics.emptyNodeUpperY), TBSGraphics.emptyNodeHeight,
				TBSGraphics.emptyNodeWidth);
		System.out.println("Created EmptyNode #" +id);
	}
	
	public Rectangle2D getRectangle(){
		return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
	}
	
	public int getHeight() {
		if(this.isInTree()) {
			return getDefaultHeight();
		} else {
			return getDefaultHeight();
		}
	}
	
	public int getWidth() {
		if(this.isInTree()) {
			return getDefaultWidth();
		} else {
			return getDefaultWidth();
		}
	}
	
}
