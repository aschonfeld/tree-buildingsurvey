package admin;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

public class Edge {
	
	boolean directional;
	Vertex v1;
	Vertex v2;
	
	Rectangle r1 = null;
	Rectangle r2 = null;
	Graphics2D g2 = null;
	Point offset = null;
	
    Edge(Vertex v1, Vertex v2) {
    	this.v1 = v1;
    	this.v2 = v2;
    	directional = true;
    }
    
    public Vertex getV1() {
    	return v1;
    }
    
    public Vertex getV2() {
    	return v2;
    }
    
    public void render(Graphics g, Point offset) {
    	this.g2 = (Graphics2D) g;
    	this.offset = offset;
    	r1 = v1.getVertexBounds(g2, offset);
    	r2 = v2.getVertexBounds(g2, offset);
    	g2.setStroke(new BasicStroke(3));
		Line2D conn = getConnectionBounds();
		conn = new Line2D.Double(conn.getX1(), conn.getY1(), conn.getX2(), conn.getY2());
		g2.setColor(Common.connectionColor);
		g2.draw(conn);
		if(directional){
			g2.draw(getArrowHead(conn, 0.75 * Math.PI));
			g2.draw(getArrowHead(conn, 1.25 * Math.PI));
		}
		g2.setStroke(new BasicStroke());
	}
    
	private Line2D getConnectionBounds(){
		Point start = new Point(),end = new Point();
		Point v1Mid = new Point((int) r1.getCenterX(), (int) r1.getCenterY());
		Point v2Mid = new Point((int) r2.getCenterX(), (int) r2.getCenterY());
		Line2D l1 = new Line2D.Double(v1Mid.x, v1Mid.y, v2Mid.x, v2Mid.y);
		List<Line2D> v1Sides = vertexSides(v1);
		List<Line2D> v2Sides = vertexSides(v2);
		Point2D p;
		for(Line2D l : v1Sides){
			if(l1.intersectsLine(l)){
				p =  getIntersectionPoint(l1, l);
				start = new Point((int) p.getX(), (int) p.getY());
				break;
			}
		}
		for(Line2D l : v2Sides){
			if(l1.intersectsLine(l)){
				p =  getIntersectionPoint(l1, l);
				end = new Point((int) p.getX(), (int) p.getY());
				break;
			}
		}
		return new Line2D.Double(start, end);
	}


	public Line2D getArrowHead(Line2D conn, double angle) {
		double dx = dx(conn);
		double dy = dy(conn);
		double dArrowX = Math.round(dx * Math.cos(angle) + dy * Math.sin(angle));
		double dArrowY = Math.round(dy * Math.cos(angle) - dx * Math.sin(angle));
		double arrowLength0 = Math.sqrt(dx * dx + dy * dy);
		dArrowX /= arrowLength0 * arrowLength;
		dArrowY /= arrowLength0 * arrowLength;
		int arrowX = (int) Math.round(dArrowX);
		int arrowY = (int) Math.round(dArrowY);
		return new Line2D.Double(
			conn.getP2().getX(),
			conn.getP2().getY(),
			conn.getP2().getX() + arrowX,
			conn.getP2().getY() + arrowY);
	}
	
	private double arrowLength = 0.1;
	private double dx(Line2D l){return l.getX2() - l.getX1();}
	private double dy(Line2D l){return l.getY2() - l.getY1();}
	
	private List<Line2D> vertexSides(Vertex v){
		Rectangle rect = v.getVertexBounds(g2, offset);
		int width, height, x, y;
		width = (int) rect.getWidth();
		height = (int) rect.getHeight();
		x = (int) rect.getX();
		y = (int) rect.getY();
		List<Line2D> sides = new LinkedList<Line2D>();
		sides.add(new Line2D.Double(x, y+height,x+width, y+height));
		sides.add(new Line2D.Double(x+width, y+height,x+width, y));
		sides.add(new Line2D.Double(x+width, y,x, y));
		sides.add(new Line2D.Double(x, y,x, y+height));
		return sides;		
	}
	
	private static Point2D getIntersectionPoint(Line2D line1, Line2D line2) {
		double px = line1.getX1(),
		py = line1.getY1(),
		rx = line1.getX2()-px,
		ry = line1.getY2()-py;
		double qx = line2.getX1(),
		qy = line2.getY1(),
		sx = line2.getX2()-qx,
		sy = line2.getY2()-qy;

		double det = sx*ry - sy*rx;
		if (det == 0) {
			return null;
		} else {
			double z = (sx*(qy-py)+sy*(px-qx))/det;
			if (z==0 ||  z==1)
				return null;  // intersection at end point!
			return new Point2D.Double((float)(px+z*rx), (float)(py+z*ry));
		}
	}
	
	public String toString() {
		return new String("EDGE: \n V1:" + v1 + "\n V2:" + v2);
	}

    
}
