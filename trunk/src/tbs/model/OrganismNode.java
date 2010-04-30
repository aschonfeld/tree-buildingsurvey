//TBS version ????
//OrganismNode: represents "organisms" manipulated by user

package tbs.model;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tbs.TBSGraphics;
import tbs.graphanalysis.Vertex;
import tbs.graphanalysis.VertexInfo;

public class OrganismNode extends Node {
	private BufferedImage img;
	private Map<Integer, String> types;
	private Point defaultPoint;
	private int stringWidth;
	private int imageStartX = -1;
	private int stringAreaLeftX = -1;

	public OrganismNode(int id, String name, List<String> types,
			Point anchorPoint, BufferedImage i, int stringWidth) {
		super(id, name);
		this.types = new HashMap<Integer, String>();
		int index = 1;
		for (String type : types) {
			this.types.put(index, type);
			index++;
		}

		img = i;
		defaultPoint = new Point();
		this.stringWidth = stringWidth;
	}

	public BufferedImage getImage() {
		return img;
	}

	public int getHeight() {
		if (getX() > 0)
			return img.getHeight();
		return TBSGraphics.organismNodeHeight;
	}

	public int getWidth() {
		if (getX() > 0)
			return img.getWidth();
		return TBSGraphics.organismNodeWidth;
	}

	public Point getDefaultPoint() {
		return new Point(
				0,
				(TBSGraphics.buttonsHeight + 10)
						+ (getId() * (TBSGraphics.organismNodeHeight + TBSGraphics.ySpacing)));
	}

	public Map<Integer, String> getTypes() {
		return types;
	}

	public void reset() {
		getConnectedTo().clear();
		getConnectedFrom().clear();
		setInTree(false);
		setAnchorPoint(defaultPoint);
	}

	public boolean isBeingLabeled() {
		return false;
	}

	public void setBeingLabeled(boolean beingLabeled) {
	}

	public int getImageStartX() {
		if (TBSGraphics.organismNodeWidth > 0 && imageStartX < 0)
			imageStartX = getDefaultPoint().x
					+ ((TBSGraphics.organismNodeWidth - (img.getWidth() + stringWidth)) / 2);
		return imageStartX;
	}

	public int getStringAreaLeftX() {
		if (stringAreaLeftX < 0)
			stringAreaLeftX = getImageStartX() + img.getWidth()
					+ TBSGraphics.padding.height;
		return stringAreaLeftX;
	}

	public int getStringWidth() {
		return stringWidth;
	}

	/*
	 * This is a default method that is used by setScreenString in the
	 * Controller & also some logging to get information about a ModelElement
	 * object. Even though the element is a ModelElement when toString is called
	 * it will refer to the resulting subclass, in this case OrganismNode.
	 */
	public String toString() {
		return getName() + " Node";
	}

	public Vertex convertToVertex() {
		return new Vertex(new VertexInfo(getName(), types, img),
				getAnchorPoint());
	}
}
