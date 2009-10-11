package phylogenySurvey;

import org.jdom.Element;

/**
 * Represents the connection between two objects, stored internally as
 * "oneLabel" and "otherLabel". 
 * Linked objects can be SelectableLinkableObjects, which means Nodes
 * and OrganismLabels. The field names suggest the latter are meant. I
 * have not yet determined which are currently Linked in practice.
 */
public class Link {
	
	private SelectableLinkableObject oneLabel;
	private SelectableLinkableObject otherLabel;
	
/**
 * Takes two SelectableLinkableObjects (=JLabels with benefits) 
 */
	public Link(SelectableLinkableObject oneLabel, SelectableLinkableObject otherLabel) {
		this.oneLabel = oneLabel;
		this.otherLabel = otherLabel;
	}

/**
 * Returns one of the two objects linked. 
 */
	public SelectableLinkableObject getOneLabel() {
		return oneLabel;
	}

/**
 *Returns the other of the two objects linked. 
 */
	public SelectableLinkableObject getOtherLabel() {
		return otherLabel;
	}

/**
 * Creates an XML object with the contents of both objects connected by
 * this Link. "Contents" = output of object's save() method, meaning two
 * Elements are created and concatenated into one, which is the returned
 * by this method.
 */	
	public Element save() {
		Element e = new Element("Link");
		Element s1e = new Element("FirstSLO");
		s1e.addContent(oneLabel.save());
		Element s2e = new Element("SecondSLO");
		s2e.addContent(otherLabel.save());
		e.addContent(s1e);
		e.addContent(s2e);
		return e;
	}

}
