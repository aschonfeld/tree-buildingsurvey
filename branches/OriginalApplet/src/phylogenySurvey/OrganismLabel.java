package phylogenySurvey;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

import org.jdom.Element;

/**
 * This seems to represent the organism itself. ("Label" seems to be a
 * reference to the class's eventual origin in "JLabel"). 
 */
public class OrganismLabel extends SelectableLinkableObject {
	
	private String type;
	private String imageFileName;
	

/**
 * Constructs a new organism object. 
 * Details of parameters are unclear at first glance.
 */

	public OrganismLabel(String name, ImageIcon image, String imageFileName, String type) {
		super(name, image);
		this.imageFileName = imageFileName;
		Font originalFont = this.getFont();
		Font newFont = originalFont.deriveFont(9.0f);
		setFont(newFont);
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.type = type;
	}
		
/**
 * Returns organism's name. 
 * "text" is the variable name in the SelectableObject class, two levels
 * up. 
 */
	public String getName() {
		return text;
	}
	
/**
 * Returns organism's type.
 * Will return to fill in details on "type"
 */
	public String getType() {
		return type;
	}
	

/**
 * Returns center point of this object. This is calculated from
 * constants in the class SurveyUI. Labels are 120 X 30. 
 * Nodes are 10 X 10, according to their getCenter(). 
 */
	public Point getCenter() {
		return new Point(getLocation().x + SurveyUI.LABEL_WIDTH/2,
				getLocation().y + SurveyUI.LABEL_HEIGHT/2);
	}

/**
 * As in other objects, save() exports the fields of this object to an
 * Element object (org.jdom.Element) and returns that Element. 
 */
	public Element save() {
		Element e = new Element("OrganismLabel");
		e.setAttribute("Name", text);
		e.setAttribute("ImageFileName", imageFileName);
		e.setAttribute("Type", type);
		e.setAttribute("X", String.valueOf(getLocation().x));
		e.setAttribute("Y", String.valueOf(getLocation().y));
		return e;
	}

}
