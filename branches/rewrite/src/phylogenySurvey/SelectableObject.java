package phylogenySurvey;

import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.jdom.Element;


/**
 * The base class for most of the objects in this package. 
 */
public abstract class SelectableObject extends JLabel {
	
	private boolean selected;

	protected String text;
	
/**
 * Constructs a SelectableObject with the specified image.
 */
	public SelectableObject(ImageIcon image) {
		super(image);
		selected = false;
	}
	
/**
 *Constructs a SelectableObject with the specified text and image
 */
	public SelectableObject(String text, ImageIcon image) {
		super(text, image, SwingConstants.CENTER);
		this.text = text;
		selected = false;
	}
	
/**
 * Constructs and SelectableObject with the specified text.
 */
	public SelectableObject(String text) {
		super(text);
		this.text = text;
		selected = false;
	}
	
/**
 * Returns value of boolean selected, which is false by default. 
 */	
	public boolean isSelected() {
		return selected;
	}
	
/**
 * Sets the value of "selected". 
 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
/**
 * Couldn't this be implemented as (getLocation().x+getSize().width)/2
 * and vice versa for y and height? Why is this implemented separately
 * for each descendant class?
 */
	public abstract Point getCenter();
	
	public abstract Element save();
}
