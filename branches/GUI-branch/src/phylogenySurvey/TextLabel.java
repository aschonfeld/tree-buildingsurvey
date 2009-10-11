package phylogenySurvey;

import java.awt.Point;

import org.jdom.Element;

/**
 * This class represents the labels created arbitrarily by the user.
 * It's the sole direct descendant of SelectableObject, and the only
 * object currently available for manipulation in the running version of
 * the code. (10/2/09)
 */ 
public class TextLabel extends SelectableObject {
	
	private static int counter = 0;
	
	private int id;

/**
 * Takes a String, which is the text of this label. "id" field is
 * assigned serially through static variable "counter". 
 */
	public TextLabel(String text) {
		super(text);
		id = counter;
		counter++;
	}
	
/**
 * Takes label text (String) and id (int). Counter advances to id value
 * if id>counter, but if id<counter, you have duplicates (?) (Same issue
 * as Node, probably I will understand both at the same time)
 */
	public TextLabel(String text, int id) {
		super(text);
		this.id = id;
		if (id > counter) {
			counter = id;
		}
	}

/**
 * Returns null. TextLabels have no centers, not even creamy ones.
 * They're just candy shell, all the way through. 
 */
	public Point getCenter() {
		return null;
	}

/**
 * Creates an Element and places this object's fields in it, then
 * returns that object. Same save routine as other objects here.  
 * Unlike other objects, this seeks out parameters from the Component
 * level: getLocation().x and .y, and getSize().width and .height
 */
	public Element save() {
		Element e = new Element("TextLabel");
		e.setAttribute("Id", String.valueOf(id));
		e.setAttribute("Text", text);
		e.setAttribute("X", String.valueOf(getLocation().x));
		e.setAttribute("Y", String.valueOf(getLocation().y));
		e.setAttribute("width", String.valueOf(getSize().width));
		e.setAttribute("height", String.valueOf(getSize().height));
		return e;
	}

}
