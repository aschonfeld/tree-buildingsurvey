package phylogenySurvey;

import org.jdom.Element;

public class Link {
	
	private SelectableLinkableObject oneLabel;
	private SelectableLinkableObject otherLabel;
	
	public Link(SelectableLinkableObject oneLabel, SelectableLinkableObject otherLabel) {
		this.oneLabel = oneLabel;
		this.otherLabel = otherLabel;
	}

	public SelectableLinkableObject getOneLabel() {
		return oneLabel;
	}

	public SelectableLinkableObject getOtherLabel() {
		return otherLabel;
	}
	
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
