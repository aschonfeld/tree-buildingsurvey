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
	public EmptyNode(int serial, int x, int y) {
		super("Empty Node #"+serial, x, y,
				TBSGraphics.emptyNodeHeight, TBSGraphics.emptyNodeWidth);
		setInTree(true);
		System.out.println("Added EmptyNode # " +serial);
	}
	
	/**
	* EmptyNode(model) sets name to null, position to the default
	* (left-hand panel, beneath ONodes)
	*/
	public EmptyNode() {
		super("Empty Node", TBSGraphics.emptyNodeLeftX,
				TBSGraphics.emptyNodeUpperY, TBSGraphics.emptyNodeHeight,
				TBSGraphics.emptyNodeWidth);
		System.out.println("Created EmptyNode # " +serial);
	}	
}
