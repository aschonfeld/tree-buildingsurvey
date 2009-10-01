package phylogenySurvey;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

import org.jdom.Element;

public class OrganismLabel extends SelectableLinkableObject {
	
	private String type;
	private String imageFileName;
	
	public OrganismLabel(String name, ImageIcon image, String imageFileName, String type) {
		super(name, image);
		this.imageFileName = imageFileName;
		Font originalFont = this.getFont();
		Font newFont = originalFont.deriveFont(9.0f);
		setFont(newFont);
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.type = type;
	}
		
	public String getName() {
		return text;
	}
	
	public String getType() {
		return type;
	}
	
	public Point getCenter() {
		return new Point(getLocation().x + SurveyUI.LABEL_WIDTH/2,
				getLocation().y + SurveyUI.LABEL_HEIGHT/2);
	}

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
