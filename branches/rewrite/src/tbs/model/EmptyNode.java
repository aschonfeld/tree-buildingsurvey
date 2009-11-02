package tbs.model;
//TBS version 0.3
//EmptyNode: connector node, represents a hypothetical common ancestor


	
public class EmptyNode extends Node {

	TBSModel model;
	private int initX, initY;
	private int emptyNodeWidth = 20;
	private int emptyNodeHeight = 20;
	
	public EmptyNode(TBSModel mod, int x, int y, String n) {
		leftX = x;
		upperY = y;
		initX = 70;
		initY = 575;
		width = emptyNodeWidth;
		height = emptyNodeHeight;
		name = n;
		model = mod;
	}
	
	public boolean collidesWith(ModelElement e) {return false;};

	//Re-fills "bottomless stack" of EmptyNodes
	public void addToTree()
	{
		model.addElement(new EmptyNode(model, initX, initY, "EmptyNode"));
	
	}
	
	public void removeFromTree()
	{

		model.delete(this);

	}
	
}
