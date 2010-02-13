import java.awt.BasicStroke;
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
    private ArrayList<Vertex> fromVertices;
    private boolean error = false;
    private Mark mark = Mark.WHITE;
    private Type type; // set by constructor
	private ArrayList<Vertex> ancestors;
	private ArrayList<Vertex> descendants;    


    Graphics2D g2 = null;
	Rectangle r1 = null;
	Point upperLeftAdj = null; // adjusted by offset
	
	// would like to generalize these sometime, but use biology terms for now
	public enum Type {
		ORGANISM,
		EMPTY;
	}
	
	// set by constructor
	public Type getType() {
		return type;
	}	
	
	// used for cycle detection algorithm
	public enum Mark {
		WHITE,
		GREY,
		BLACK;
	}
	
	// used for cycle detection algorithm
	public void setMark(Mark mark) {
		this.mark = mark;
	}
	
	// used for cycle detection algorithm
	public Mark getMark() {
		return mark;
	}
	
    Vertex(String name, Point upperLeft) {
    	this.type = Type.EMPTY;
    	this.name = name;
    	this.upperLeft = upperLeft;
    	this.img = null;
    	toVertices = new ArrayList<Vertex>();
    	fromVertices = new ArrayList<Vertex>();
    }
    
    Vertex(String name, Point upperLeft, BufferedImage img) {
    	this.type = Type.ORGANISM;
    	this.name = name;
    	this.upperLeft = upperLeft;
    	this.img = img;
    	toVertices = new ArrayList<Vertex>();
    	fromVertices = new ArrayList<Vertex>();
    }
    
    public void addFrom(Vertex fromVertex) {
    	if(!fromVertices.contains(fromVertex)) {
    		fromVertices.add(fromVertex); 
    	}
    }
    
    public void addTo(Vertex toVertex) {
    	if(!toVertices.contains(toVertex)) {
    		toVertices.add(toVertex); 
    	}
    }
    
    public ArrayList<Vertex> getFrom() {
    	return fromVertices; 
    }
    
    public ArrayList<Vertex> getTo() {
    	return toVertices;
    }
    
    public ArrayList<Vertex> getAdjVertices(boolean directional) {
    	ArrayList<Vertex> returnVal = new ArrayList<Vertex>();
    	if(directional) {
    		return toVertices;
    	} else {
    		returnVal.addAll(toVertices);
    		for(Vertex v: fromVertices) {
    			// check for bidirectional links
    			if(!toVertices.contains(v)) returnVal.add(v);
    		}
    	}
    	return returnVal;
    }
    
    public boolean isTerminal(boolean directional) {
		if((toVertices.size() == 0) && (fromVertices.size() == 1)) return true;
		if((toVertices.size() == 1) && (fromVertices.size() == 0)) return true;
    	if(directional) return false;
    	if((toVertices.size() == 1) && (fromVertices.size() == 1)) return true;
    	return false;
    }
    
    public void setError(boolean error) {
    	this.error = error;
    }
    
    public boolean hasError() {
    	return error;
    }
    
    public void render(Graphics g, Point offset) {
    	g2 = (Graphics2D) g;
    	upperLeftAdj = new Point(upperLeft.x - offset.x, upperLeft.y - offset.y);
    	if(img != null) {
    		renderVertexWithImage();
    	} else {
    		renderVertex();
    	}
    	renderError();
    }
    
    public ArrayList<Vertex> getAdjVertices() {
    	return toVertices;
    }

    private void renderVertexWithImage() {
        g2.drawImage(img, upperLeftAdj.x, upperLeftAdj.y, null);
    }
    
	private void renderVertex() {
		g2.setColor(Common.emptyNodeColor);
		Rectangle bounds = getVertexBounds();
		g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		Common.drawCenteredString(g2, name, bounds.x, bounds.y, bounds.width, bounds.height, Color.black);
	}

	// draw a red box around vertex if has error
	private void renderError() {
		if(error) {
			Rectangle bounds = getVertexBounds();
			g2.setColor(Color.red);
			g2.setStroke(new BasicStroke(3));
			g2.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
	}
	
	private Rectangle getVertexBounds() {
		if(this.img != null) {
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
		return new String("VERTEX: " + name + " + img: " + (img != null) + ", location: " + upperLeft.x + "," + upperLeft.y);
	}

}
