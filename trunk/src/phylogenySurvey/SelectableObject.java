package phylogenySurvey;

import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.jdom.Element;

public abstract class SelectableObject extends JLabel {
	
	private boolean selected;
	protected String text;
	
	public SelectableObject(ImageIcon image) {
		super(image);
		selected = false;
	}
	
	public SelectableObject(String text, ImageIcon image) {
		super(text, image, SwingConstants.CENTER);
		this.text = text;
		selected = false;
	}
	
	public SelectableObject(String text) {
		super(text);
		this.text = text;
		selected = false;
	}
		
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public abstract Point getCenter();
	
	public abstract Element save();
}
