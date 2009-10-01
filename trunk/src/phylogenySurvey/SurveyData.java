package phylogenySurvey;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class SurveyData {
	
	private ArrayList<SelectableObject> items;
	private ArrayList<Link> links;
	
	private ArrayList<String> historyList;
		
	public SurveyData() {
		items = new ArrayList<SelectableObject>();
		links = new ArrayList<Link>();
		historyList = new ArrayList<String>();
	}
				
	public void add(SelectableObject n) {
		items.add(n);
	}
	
	public void delete(Node n) {
		//remake node list without any links
		//  that include this node
		ArrayList<Link> newLinks = new ArrayList<Link>();
		Iterator<Link> it = links.iterator();
		while (it.hasNext()) {
			Link l = it.next();
			if ((l.getOneLabel() != n) && (l.getOtherLabel() != n)) {
				newLinks.add(l);
			}
		}
		links = newLinks;
		items.remove(n);
	}
	
	public void add(TextLabel tl) {
		items.add(tl);
	}
	
	public void delete(TextLabel tl) {
		items.remove(tl);
	}
	
	public void add(Link l) {
		links.add(l);
	}
	
	public void deleteLink(SelectableLinkableObject a, SelectableLinkableObject b) {
		Iterator<Link> it = links.iterator();
		while (it.hasNext()) {
			Link l = it.next();
			if ( ( (l.getOneLabel() == a) && (l.getOtherLabel() == b) )  ||
					( (l.getOneLabel() == b) && (l.getOtherLabel() == a))	
			) {
				links.remove(l);
				return;
			}
		}
		JOptionPane.showMessageDialog(null, 
				"Please select two linked objects to un-link", 
				"Nothing to un-link", 
				JOptionPane.WARNING_MESSAGE);

	}
	
	public NodeWithLocation split(SelectableLinkableObject a, SelectableLinkableObject b) {
		Iterator<Link> it = links.iterator();
		while(it.hasNext()) {
			Link l = it.next();
			if (
					((l.getOneLabel() == a) && (l.getOtherLabel() == b)) ||
					((l.getOneLabel() == a) && (l.getOtherLabel() == b))
			) {
				links.remove(l);
				SelectableLinkableObject slo1 = l.getOneLabel();
				SelectableLinkableObject slo2 = l.getOtherLabel();
				int x = (slo1.getCenter().x + slo2.getCenter().x)/2;
				int y = (slo1.getCenter().y + slo2.getCenter().y)/2;
				Node node = new Node(new ImageIcon(this.getClass().getResource("/images/node.gif" )));
				items.add(node);
				links.add(new Link(a, node));
				links.add(new Link(b, node));
				return new NodeWithLocation(node, x, y);
			}
		}
		return null;
	}
	
	public ArrayList<SelectableObject> getItems() {
		return items;
	}
	
	public ArrayList<OrganismLabel> getOrganismLabels() {
		ArrayList<OrganismLabel> orgs = new ArrayList<OrganismLabel>();
		Iterator<SelectableObject> it = items.iterator();
		while (it.hasNext()) {
			SelectableObject item = it.next();
			if (item instanceof OrganismLabel) {
				orgs.add((OrganismLabel)item);
			}
		}
		return orgs;
	}
	
	public ArrayList<Link> getLinks() {
		return links;
	}
	
	public String getState() {
		Element root = new Element("State");

		Element itemEl = new Element("Items");
		Iterator<SelectableObject> itemIt = items.iterator();
		while (itemIt.hasNext()) {
			SelectableObject item = itemIt.next();
			itemEl.addContent(item.save());
		}
		root.addContent(itemEl);

		Element linkEl = new Element("Links");
		Iterator<Link> linkIt = links.iterator();
		while (linkIt.hasNext()) {
			Link link = linkIt.next();
			linkEl.addContent(link.save());
		}
		root.addContent(linkEl);
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		return out.outputString(new Document(root));
	}
	
	public void saveStateToHistoryList() {
		historyList.add(getState());
	}
	
	public String undo() {
		if (historyList.size() < 2) {
			return null;
		}
		historyList.remove(historyList.size() - 1);
		String state = historyList.get(historyList.size() - 1);
		return state;
	}
			
	public SelectableLinkableObject findItemByName(Element e) {
		String name = e.getName();
		if (name.equals("OrganismLabel")) {
			Iterator<SelectableObject> it = items.iterator();
			while (it.hasNext()) {
				SelectableObject so = it.next();
				if (so instanceof OrganismLabel) {
					if (so.getName().equals(e.getAttributeValue("Name"))) {
						return (SelectableLinkableObject)so;
					}
				}
			}
		}
		if (name.equals("Node")) {
			Iterator<SelectableObject> it = items.iterator();
			while (it.hasNext()) {
				SelectableObject so = it.next();
				if (so instanceof Node) {
					if (((Node)so).getID() == Integer.parseInt(e.getAttributeValue("Id"))) {
						return (SelectableLinkableObject)so;
					}
				}
			}
		}
		return null;
	}
	
}
