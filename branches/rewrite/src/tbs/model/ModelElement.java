package tbs.model;


public abstract class ModelElement {
		
	public abstract boolean collidesWith(ModelElement e);
	public abstract boolean contains(int x, int y);
}
