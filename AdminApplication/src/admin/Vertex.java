package admin;
import java.awt.*;
import java.util.ArrayList;


public class Vertex implements Renderable {
	 
    private VertexInfo info;
	private Point upperLeft;
    private ArrayList<Vertex> toVertices;
    private ArrayList<Vertex> fromVertices;
    private boolean error = false;
    private Mark mark = Mark.WHITE;
    private ArrayList<Vertex> ancestors;
	private ArrayList<Vertex> descendants;
	private int indexInGraph = -1;
	public boolean visited = false;

    Graphics2D g2 = null;
	Rectangle r1 = null;
	Point upperLeftAdj = null; // adjusted by offset
	
    Vertex(VertexInfo info, Point upperLeft) {
    	this.info = info;
    	this.upperLeft = upperLeft;
    	toVertices = new ArrayList<Vertex>();
    	fromVertices = new ArrayList<Vertex>();
		ancestors = new ArrayList<Vertex>();
		descendants = new ArrayList<Vertex>();
   }
   

/***************************************
* Parent/Child Relationships           *
***************************************/
 
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
    

	public ArrayList<Vertex> getToVertices()
	{
		return toVertices;	
	}

	public ArrayList<Vertex> getFromVertices()
	{
		return fromVertices;
	}

/**************************************
* Ancestor/Descendant Relationships   *
**************************************/

	public boolean ancestorOf(Vertex v)
	{
		return (descendants.contains(v));
	}

	public boolean descendantOf(Vertex v)
	{
		return (ancestors.contains(v));
	}


	public void addDescendants(ArrayList<Vertex> descList)
	{
		descendants.addAll(descList);
	}

	public void addAncestors(ArrayList<Vertex> ancList)
	{
		ancestors.addAll(ancList);
	}
	
	public ArrayList<Vertex> getDescendants()
	{
		return descendants;
	}

	public ArrayList<Vertex> getAncestors()
	{
		return ancestors;
	}

    public ArrayList<Vertex> getAdjVertices() {
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
    

/***************************************
* used for cycle detection algorithm   *
***************************************/

	public enum Mark {
		WHITE,
		GREY,
		BLACK;
	}
	
	public void setMark(Mark mark) {
		this.mark = mark;
	}
	
	public Mark getMark() {
		return mark;
	}

    public boolean isTerminal(boolean directional) {
		if((toVertices.size() == 0) && (fromVertices.size() == 1)) return true;
		if((toVertices.size() == 1) && (fromVertices.size() == 0)) return true;
    	if(directional) return false;
    	if((toVertices.size() == 1) && (fromVertices.size() == 1)) return true;
    	return false;
    }
   

/******************************************
* Error stuff
******************************************/ 
    public void setError(boolean error) {
    	this.error = error;
    }
    
    public boolean hasError() {
    	return error;
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
	


/**************
* Rendering   *
**************/  
    public void render(Graphics g, Point offset) {
    	g2 = (Graphics2D) g;
    	upperLeftAdj = new Point(upperLeft.x - offset.x, upperLeft.y - offset.y);
    	if(info.getImage() != null) {
    		renderVertexWithImage();
    	} else {
    		renderVertex();
    	}
    	renderError();
    }
    
    private void renderVertexWithImage() {
        g2.drawImage(info.getImage(), upperLeftAdj.x, upperLeftAdj.y, null);
    }
    
	private void renderVertex() {
		g2.setColor(Common.emptyNodeColor);
		Rectangle bounds = getVertexBounds();
		g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		Common.drawCenteredString(g2, info.getName(), bounds.x, bounds.y, bounds.width, bounds.height, Color.black);
	}

	private Rectangle getVertexBounds() {
		if(info.getImage() != null) {
			return new Rectangle(upperLeftAdj.x, upperLeftAdj.y, 
					info.getImage().getWidth(), info.getImage().getHeight());
		} else {
			if(Common.isStringEmpty(info.getName())) {
				return new Rectangle (upperLeftAdj.x, upperLeftAdj.y, 
					Common.emptyNodeWidth, Common.emptyNodeHeight);
			} else {
				Dimension stringBounds = Common.getStringBounds(g2, info.getName());
				int width = stringBounds.width + Common.paddingWidth * 2;
				return new Rectangle(upperLeftAdj.x, upperLeftAdj.y, width, 
					Common.emptyNodeHeight);
			}
		}
	}
	
	public Point getUpperLeft() {
		return upperLeft;
	}
	
	public Rectangle getVertexBounds(Graphics2D g2D, Point offset) {
		Point tempUpperLeftAdj = new Point(upperLeft.x - offset.x, 
				upperLeft.y - offset.y);
		if(this.info.getImage() != null) {
			return new Rectangle(upperLeftAdj.x, upperLeftAdj.y, 
				info.getImage().getWidth(), info.getImage().getHeight());
		} else {
			if(Common.isStringEmpty(info.getName())) {
				return new Rectangle (tempUpperLeftAdj.x, tempUpperLeftAdj.y, 
					Common.emptyNodeWidth, Common.emptyNodeHeight);
			} else {
				Dimension stringBounds = Common.getStringBounds(g2D, info.getName());
				int width = stringBounds.width + Common.paddingWidth * 2;
				return new Rectangle(tempUpperLeftAdj.x, tempUpperLeftAdj.y, 
					width, Common.emptyNodeHeight);
			}
		}
	}

/************************************
* Routine getters/setters/toString  *
************************************/	

	public String toString() {
		return new String("VERTEX: " + info.getName() + " + img: " + 
				(info.getImage() != null) + ", location: " + 
					upperLeft.x + "," + upperLeft.y);
	}

	public String getName() {
		if (info.getName()!=null) return info.getName();
		return (new String("I" + indexInGraph));
	}
	
	public VertexInfo.VertexType getType(){
		return info.getVertexType();
	}
	
	public VertexInfo getInfo(){
		return info;
	}
    public void setIndex(int index) {
    	indexInGraph = index;
	}

	public int getIndex() {
		return indexInGraph;
	}

}
