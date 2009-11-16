//TBS version 0.5
package tbs.model;

/**
* ModelElement is the abtract superclass for everything handled by TBSModel:
* OrganismNodes, EmptyNodes, and Connections. 
*/
public abstract class ModelElement {
	private boolean selected = false;
	
	/**
	* CollidesWith should return true if this object overlaps with
	* another ModelElement
	*/
	public abstract boolean collidesWith(ModelElement e);
	
	/**
	* Contains should return true for a point which is within the
	* boundaries delineated by this ModelElement.
	*/ 
	public abstract boolean contains(int x, int y);
	
	/**
	* Returns true if this ModelElement is the selected object. 	
	*/
	public boolean getSelected() {return selected;}

	/**
	* Sets this object's "selected" status
	*/
	public void setSelected(boolean selected){this.selected = selected;}

}
