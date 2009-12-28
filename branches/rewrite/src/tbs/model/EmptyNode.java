//TBS version 0.4
//EmptyNode: connector node, represents a hypothetical common ancestor

package tbs.model;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;

import tbs.TBSGraphics;


/**
* EmptyNodes are the junctions by which Nodes are joined to one another. 
*/	
public class EmptyNode extends Node 
{
	// need this for calculating EmptyNode size
	public static Graphics2D g2 = null;
	
	/**
	* EmptyNode's fully-specified constructor sets position and name
	* explicitly. This is never called. 
	*/
	public EmptyNode(int id, Point anchorPoint) {
		super(id, "", anchorPoint,
				TBSGraphics.emptyNodeHeight, TBSGraphics.emptyNodeWidth);
		setInTree(true);
		initName();
		System.out.println("Added EmptyNode #" +id);
		imgFileName="";
	}
	
	/**
	* EmptyNode(model) sets name to null, position to the default
	* (left-hand panel, beneath ONodes)
	*/
	public EmptyNode(int id) {
		super(id, "", new Point(TBSGraphics.emptyNodeLeftX,
				TBSGraphics.emptyNodeUpperY), TBSGraphics.emptyNodeHeight,
				TBSGraphics.emptyNodeWidth);
		initName();
		System.out.println("Created EmptyNode #" +id);
	}
	
	public int getHeight() {
		return getDefaultHeight();
	}
	
	public int getWidth() {
		return getDefaultWidth();
	}
	
	public void initName() {
		rename(getName());
	}
	
	public void rename(String name) {
		if(name.length()==0){
			setName("");
			setWidth(TBSGraphics.emptyNodeWidth);
			setHeight(TBSGraphics.emptyNodeHeight);
			return;
		}
		int width = TBSGraphics.emptyNodeWidth;
		int height = TBSGraphics.emptyNodeHeight;
		int padding = TBSGraphics.emptyNodePadding;
		Dimension stringBounds = TBSGraphics.getStringBounds(g2, name);
		int testWidth = stringBounds.width + 2 * padding;
		int testHeight = stringBounds.height + 2 * padding;
		if (testWidth > TBSGraphics.emptyNodeWidth) width = testWidth;
		if (testHeight > TBSGraphics.emptyNodeHeight) height = testHeight;
		setName(name);
		setWidth(width);
		setHeight(height);		
	}
	
	
}
