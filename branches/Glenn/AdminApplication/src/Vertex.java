import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class Vertex implements Renderable {
	 
    private BufferedImage img;
    private Point upperLeft;
    private String name;
    private ArrayList<Vertex> toVertices;
    
    Graphics2D g2 = null;
	Rectangle r1 = null;
	Point upperLeftAdj = null; // adjusted by offset
    
    Vertex(String name, Point upperLeft) {
    	this.name = name;
    	this.upperLeft = upperLeft;
    	this.img = null;
    	toVertices = new ArrayList<Vertex>();
    }
    
    Vertex(String name, Point upperLeft, BufferedImage img) {
    	this.name = name;
    	this.upperLeft = upperLeft;
    	this.img = img;
    	toVertices = new ArrayList<Vertex>();
    }
    
    public void addEdge(Vertex toVertex) {
    	if(!toVertices.contains(toVertex)) {
    		toVertices.add(toVertex); 
    	}
    }
    
    public void render(Graphics g, Point offset) {
    	g2 = (Graphics2D) g;
    	upperLeftAdj = new Point(upperLeft.x - offset.x, upperLeft.y - offset.y);
    	if(img != null) {
    		renderVertexWithImage();
    	} else {
    		renderVertex();
    	}
    }
    
    public ArrayList<Vertex> getAdjVertices() {
    	return toVertices;
    }

    private void renderVertexWithImage() {
    	//g2.setColor(Color.white);
		//g2.fillRect((int) Math.random() * 600, (int) Math.random() * 600, 20, 20);
        Color stringColor = Common.organismStringColor;
        int stringWidth = 0;
        int imageWidth = 0;
        int imageStartX = 0;
        stringWidth = (int) Common.getStringBounds(g2, name).getWidth();
        imageWidth = img.getWidth();
        // center image and text
        //int imageXOffset = (Common.organismNodeWidth - imageWidth - stringWidth) / 2;
        imageStartX = upperLeftAdj.x; // + imageXOffset;
        int stringAreaLeftX = imageStartX + imageWidth + Common.paddingWidth;
        int stringAreaWidth = stringWidth;
        int stringAreaUpperY = upperLeftAdj.y;
        int stringAreaHeight = Common.organismNodeHeight;
        //g2.setColor(Common.organismBoxColor);
        //g2.fillRect(upperLeftAdj.x, upperLeftAdj.y, Common.organismNodeWidth, Common.organismNodeHeight);
        //Common.drawCenteredString(g2, name, stringAreaLeftX, stringAreaUpperY, stringAreaWidth, stringAreaHeight, stringColor);
        //g2.drawImage(img, imageStartX, upperLeftAdj.y, null);
        g2.drawImage(img, upperLeftAdj.x, upperLeftAdj.y, null);
    }
    
	private void renderVertex() {
    	//g2.setColor(Color.white);
		//g2.fillRect((int) Math.random() * 600, (int) Math.random() * 600, 20, 20);
		g2.setColor(Common.emptyNodeColor);
		Rectangle bounds = getVertexBounds();
		g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		Common.drawCenteredString(g2, name, bounds.x, bounds.y, bounds.width, bounds.height, Color.black);
	}
	
	private Rectangle getVertexBounds() {
		if(this.img != null) {
			//return new Rectangle(upperLeftAdj.x, upperLeftAdj.y, Common.organismNodeWidth, Common.organismNodeHeight);
			return new Rectangle(upperLeftAdj.x, upperLeftAdj.y, img.getWidth(), img.getHeight());
		} else {
			if(Common.isStringEmpty(name)) {
				return new Rectangle (upperLeftAdj.x, upperLeftAdj.y, Common.emptyNodeWidth, Common.emptyNodeHeight);
			} else {
				Dimension stringBounds = Common.getStringBounds(g2, name);
				int width = stringBounds.width + Common.paddingWidth * 2;
				return new Rectangle(upperLeftAdj.x, upperLeftAdj.y, width, Common.emptyNodeHeight);
			}
		}
	}
	
	public Rectangle getVertexBounds(Graphics2D g2D, Point offset) {
		Point tempUpperLeftAdj = new Point(upperLeft.x - offset.x, upperLeft.y - offset.y);
		if(this.img != null) {
			//return new Rectangle(tempUpperLeftAdj.x, tempUpperLeftAdj.y, Common.organismNodeWidth, Common.organismNodeHeight);
			return new Rectangle(upperLeftAdj.x, upperLeftAdj.y, img.getWidth(), img.getHeight());
		} else {
			if(Common.isStringEmpty(name)) {
				return new Rectangle (tempUpperLeftAdj.x, tempUpperLeftAdj.y, Common.emptyNodeWidth, Common.emptyNodeHeight);
			} else {
				Dimension stringBounds = Common.getStringBounds(g2D, name);
				int width = stringBounds.width + Common.paddingWidth * 2;
				return new Rectangle(tempUpperLeftAdj.x, tempUpperLeftAdj.y, width, Common.emptyNodeHeight);
			}
		}
	}
	
	public String toString() {
		return new String("VERTEX: " + name + " + img: " + (img != null) + ", location: " + upperLeft.x + "," + upperLeft);
	}

}
