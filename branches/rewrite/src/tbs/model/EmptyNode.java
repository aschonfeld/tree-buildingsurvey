package tbs.model;

import tbs.TBSGraphics;
//TBS version 0.3
//EmptyNode: connector node, represents a hypothetical common ancestor


	
public class EmptyNode extends Node {

	TBSModel model;
	String defaultName = "[NO NAME]";
	

	public EmptyNode(TBSModel mod, int lX, int uY, String n) {
		leftX = lX;
		upperY = uY;
		width = TBSGraphics.emptyNodeWidth;
		height = TBSGraphics.emptyNodeHeight;
		name = n;
		model = mod;
		inTree = true;
	}
	
	// all empty nodes start in left panel
	public EmptyNode(TBSModel mod) {
		leftX = TBSGraphics.emptyNodeLeftX;
		upperY = TBSGraphics.emptyNodeUpperY;
		width = TBSGraphics.emptyNodeWidth;
		height = TBSGraphics.emptyNodeHeight;
		name = defaultName;
		model = mod;
		inTree = false;
	}
	
	public boolean collidesWith(ModelElement e) {return false;};

	//Re-fills "bottomless stack" of EmptyNodes
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
	
	public void removeFromTree()
	{	
		if(this != model.getImmortalEmptyNode()) {
			model.clearConnections(this);
			model.delete(this);
		} else {
			// empty node was moved, but didn't cross line_of_death, restore to default state
			leftX = TBSGraphics.emptyNodeLeftX;
			upperY = TBSGraphics.emptyNodeUpperY;
		}
	}
	
	public void setName(String n) {
		// left empty node cannot be renamed
		if(inTree) {
			name = n;
		}
	}
	
}
