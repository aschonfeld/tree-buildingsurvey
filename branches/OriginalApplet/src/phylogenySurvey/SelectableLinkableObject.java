package phylogenySurvey;

import java.awt.Point;

import javax.swing.ImageIcon;

public abstract class SelectableLinkableObject extends SelectableObject {
	
	public SelectableLinkableObject(ImageIcon image) {
		super(image);
	}
	
	public SelectableLinkableObject(String text, ImageIcon image) {
		super(text, image);
	}
	
	public abstract Point getCenter();

}
