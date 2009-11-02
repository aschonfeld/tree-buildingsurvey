package tbs.model;

//TBS version 0.3
//ModelElement: superclass for everything handled by TBSModel

public abstract class ModelElement {
		
	public abstract boolean collidesWith(ModelElement e);
	public abstract boolean contains(int x, int y);
}
