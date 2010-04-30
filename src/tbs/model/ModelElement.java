//TBS version 0.5
package tbs.model;

/**
 * ModelElement is the abtract superclass for everything handled by TBSModel:
 * OrganismNodes, EmptyNodes, and Connections.
 */
public abstract class ModelElement {

	private Integer id;

	public ModelElement(int id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public abstract Boolean isInTree();

	public abstract void setInTree(Boolean inTree);

	/**
	 * CollidesWith should return true if this object overlaps with another
	 * ModelElement
	 */
	public abstract boolean collidesWith(ModelElement e);

	/**
	 * Contains should return true for a point which is within the boundaries
	 * delineated by this ModelElement.
	 */
	public abstract boolean contains(int x, int y);

	public abstract StringBuffer dump();

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ModelElement))
			return false;
		return ((ModelElement) o).getId().equals(id);
	}
}
