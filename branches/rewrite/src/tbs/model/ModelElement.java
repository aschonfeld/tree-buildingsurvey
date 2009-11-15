package tbs.model;

//ModelElement: superclass for everything handled by TBSModel

public abstract class ModelElement {
	private boolean selected = false;
	
	public abstract boolean collidesWith(ModelElement e);
	public abstract boolean contains(int x, int y);
	
	public boolean getSelected() {return selected;}
	public void setSelected(boolean selected){this.selected = selected;}

}
