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

/**
 * This class comprises the methods which create and manage the data
 * objects of the survey.
 */
public class SurveyData {
	
	private ArrayList<SelectableObject> items;
	private ArrayList<Link> links;
	
	private ArrayList<String> historyList;
	
/**
 * Instantiate the basic data structures involved in managing the data.
 * These are: an arraylist "items" of SelectableObjects - TextLabels,
 * Nodes, and OrganismLabels), an Arraylist of Link objects, and an
 * arraylist of Strings, the historyList. The strings in this last are
 * in fact states of the data; the history list (if I read this
 * correctly) is a list of complete dumps of the data set, maintained
 * for purposes of undo()ing commands. (see saveToHistoryList(),
 * getState(), and undo() for this - I may be wrong) 
 * ("I may be wrong" should be understood throughout this document, so
 * the explicit statement might be read as "I suspect that I am wrong,
 * but I haven't found how yet".)
 */
	public SurveyData() {
		items = new ArrayList<SelectableObject>();
		links = new ArrayList<Link>();
		historyList = new ArrayList<String>();
	}
	

/**
 * Adds a SelectableObject (SO henceforth) to the items list. Note that
 * all variants of add() involve the normal Java sense of adding a
 * reference, and not the sort of XML export that we've seen in the data
 * objects' save() methods. 
 */	
	public void add(SelectableObject n) {
		items.add(n);
	}
	

/**
 * Deletes a Node object from the links Arraylist. The actual procedure
 * is to create a new ArrayList of Links, and then scan through the
 * original list, adding back only links which do not contain the Node
 * being deleted. 
 */
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
	
/**
 * Adds a TextLabel object to the items list.(Using ArrayList.add(), no
 * local code). Is this method necessary?
 * Shouldn't TextLabel add via the previous add(SelectableObject)
 * method? Is this here so TextLabels will be added as TextLabels and
 * not as the more generic SO? (ie, to make delete(TextLabel) fly? Why
 * not delete(SelectableObject)?
 */
	public void add(TextLabel tl) {
		items.add(tl);
	}
	
/**
 * Remove the indicated TextLabel from the items list
 * Notice that there is no state dump with any of the delete() or add()
 * methods - does this mean that these are not subject to undo()?
 */
	public void delete(TextLabel tl) {
		items.remove(tl);
	}
	
/**
 * Add a Link to the list of links.
 * (Using ArrayList.add(), no local code)
 */
	public void add(Link l) {
		links.add(l);
	}
	

/**
 * Unlinks two objects, leaving the objects themselves in place.
 * Is the warning message a bit brusque?
 */
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
	
/**
 * Creates a node between two linked objects. Scans the links list until it 
 * finds one linking both SLO arguments to the function, and removes
 * that link. It then makes two new SelectLinkableObjects (SLOs), and assigns 
 * values found in that link to the new SLOs (won't these be the same as
 * SLOs a and b, provided in the method call?). It then creates a Node
 * at a point midway between the two SLOs, and creates links from a to
 * Node and from Node to b. <br>
 * Refactoring note: For readability, this might be renamed to avoid
 * confusion between splitting a link into segments and deleting the
 * link entirely.
 */ 
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
	
/**
 * Returns the ArrayList "items"
 */ 
	public ArrayList<SelectableObject> getItems() {
		return items;
	}
	
/**
 * Creates and returns a list of just those members of ArrayList "items"
 * which are OrganismLabels. This is accomplished by scanning the list
 * "items" and placing any item which is an instanceof OrganismLabel on
 * a new ArrayList, which is returned. Objects are explicitly recast as
 * OrganismLabels on adding.  
 */
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
	
/**
 * Returns the list of Links.
 */
	public ArrayList<Link> getLinks() {
		return links;
	}
	
/**
 * Creates an XML object called State and saves to it the contents of
 * every item on the items list and every link on the links list - this
 * object is returned as a String. 
 * By "contents" if every item and link, I mean the return value of that
 * item or link's save() method.
 */
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
	
/**
 * Adds the String returned by getState() to the ArrayList
 * "historyList". This seems an impecunious means of implementing
 * undo(). 
 */
	public void saveStateToHistoryList() {
		historyList.add(getState());
	}
	

/**
 * Reverts to previous state as saved on historyList. This is
 * accomplished by returning the string value stored in historyList's
 * penultimate element. Does not reset the size of historyList, so only
 * one level of revert is possible, although historyList continues to
 * grow. <br>
 * This could be useful, as it saves a good trail of the student's
 * actions, not at the cinematic level, but as a series of discrete
 * actions taken. If this is not going to be utilized, the same effect
 * could be achieved by maintaining only two states - "now" and "now
 * minus 1". 
	public String undo() {
		if (historyList.size() < 2) {
			return null;
		}
		historyList.remove(historyList.size() - 1);
		String state = historyList.get(historyList.size() - 1);
		return state;
	}
			
/**
 * Find an item on the items list given a jdom Element.
 */
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
