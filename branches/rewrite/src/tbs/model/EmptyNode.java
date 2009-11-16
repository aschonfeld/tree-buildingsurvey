//TBS version 0.4
//EmptyNode: connector node, represents a hypothetical common ancestor

package tbs.model;

import tbs.TBSGraphics;


/**
* EmptyNodes are the junctions by which Nodes are joined to one another. 
*/	
public class EmptyNode extends Node 
{
	String defaultName = "";
	

	/**
	* EmptyNode's fully-specified constructor sets position and name
	* explicitly. This is never called. 
	*/
	public EmptyNode(TBSModel mod, int lX, int uY, String n) {
		leftX = lX;
		upperY = uY;
		width = TBSGraphics.emptyNodeWidth;
		height = TBSGraphics.emptyNodeHeight;
		name = n;
		model = mod;
		inTree = true;
	}
	
	/**
	* EmptyNode(model) sets name to null, position to the default
	* (left-hand panel, beneath ONodes)
	*/
	public EmptyNode(TBSModel mod) {
		leftX = TBSGraphics.emptyNodeLeftX;
		upperY = TBSGraphics.emptyNodeUpperY;
		width = TBSGraphics.emptyNodeWidth;
		height = TBSGraphics.emptyNodeHeight;
		name = defaultName;
		model = mod;
		inTree = false;
	}
	
	/**
	* CollidesWith returns true if this element overlaps with another.
	*/
	public boolean collidesWith(ModelElement m) {
		if(m.contains(leftX, upperY+height))
			return true;
		if(m.contains(leftX+width, upperY+height))
			return true;
		if(m.contains(leftX+width, upperY))
			return true;
		if(m.contains(leftX, upperY))
			return true;
		return false;
	}

	/**
	* Creates a new emptyNode() in the default start position and sets
	* this node to inTree. 
	* addToTree is called when a Node is moved from the inactive left
	* panel position (where EmptyNodes are created, see
	* constructor(model) to the active portion of the display. When this
	* is called, it means that the initial EmptyNode has been moved, and
	* so it creates a new one. 
	*/
	public void addToTree()
	{
		if (this == model.getImmortalEmptyNode()) {
			EmptyNode newEN = new EmptyNode(model, leftX, upperY, "");
			newEN.inTree = true;
			model.addElement(newEN);
			this.leftX = TBSGraphics.emptyNodeLeftX;
			this.upperY = TBSGraphics.emptyNodeUpperY;
		} else {
			this.inTree = true;
		}
	}
	
	/**
	* removeFromTree unlinks any connections this EmptyNode may be
	* involved in and deletes it from the model. 
	*/
	public void removeFromTree()
	{	
		if (isInTree())
		{
			this.setSelected(false);
			unlink();
			model.delete(this);
		} else {
			// empty node was moved, but didn't cross line_of_death, restore to default state
			leftX = TBSGraphics.emptyNodeLeftX;
			upperY = TBSGraphics.emptyNodeUpperY;
		}
	}
	
	/**
	* Sets name of this string. Cannot be called on the initial EmptyNode
	* in the inactive panel. 
	*/
	public void setName(String n) {
		// left empty node cannot be renamed
		if(inTree) {
			name = n;
		}
	}
	
}
