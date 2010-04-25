//TBS version 0.4
//TBS utils: utility functions of TBS

package tbs;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import tbs.graphanalysis.ConvexHull;
import tbs.graphanalysis.HullCollision;
import tbs.model.AdminModel;
import tbs.model.Node;
import tbs.view.dropdown.SubDropDown;

public class TBSUtils {

	public static double dx(Line2D l){return l.getX2() - l.getX1();}
	public static double dy(Line2D l){return l.getY2() - l.getY1();}
	public static double slope(Line2D l){return dx(l)/dy(l);}
	
	/**
	 * This method takes in two (@link Node) objects as parameters and based on where the
	 * two objects are in proportion to each other, two (@link Point) objects are computed
	 * representing the start and end of a line that will represent a connection in the applet.
	 * 
	 * @param n1, (@link Node) where connection starts
	 * @param n2, (@link Node) where connection ends
	 * @return (@link Point)[], start and and points of the connection
	 */
	public static Line2D getConnectionBounds(Node n1, Node n2){
		Point start = new Point(),end = new Point();
		Point n1Mid = n1.getCenter(), n2Mid = n2.getCenter();
		Line2D l1 = new Line2D.Double(n1Mid.x, n1Mid.y, n2Mid.x, n2Mid.y);
		List<Line2D> n1Sides = nodeSides(n1);
		List<Line2D> n2Sides = nodeSides(n2);
		Point2D p;
		for(Line2D l : n1Sides){
			if(l1.intersectsLine(l)){
				p =  getIntersectionPoint(l1, l);
				start = new Point((int) p.getX(), (int) p.getY());
				break;
			}
		}
		for(Line2D l : n2Sides){
			if(l1.intersectsLine(l)){
				p =  getIntersectionPoint(l1, l);
				end = new Point((int) p.getX(), (int) p.getY());
				break;
			}
		}
		return new Line2D.Double(start, end);
	}

	/**
	* Takes a value and a one-dimensional range defined by min and max,
	* and returns true if the object is within the range, inclusive.
	*/
	private static List<Line2D> nodeSides(Node n){
		int width, height, x, y;
		width = n.getWidth();
		height = n.getHeight();
		x = n.getX();
		y = n.getY();
		List<Line2D> sides = new LinkedList<Line2D>();
		sides.add(new Line2D.Double(x, y+height,x+width, y+height));
		sides.add(new Line2D.Double(x+width, y+height,x+width, y));
		sides.add(new Line2D.Double(x+width, y,x, y));
		sides.add(new Line2D.Double(x, y,x, y+height));
		return sides;		
	}
	
	public static Point2D getIntersectionPoint(Line2D line1, Line2D line2) {
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
	
	/**
	 * Draw the arrowhead at the end of a connection.
	 */
	public static Line2D getArrowHead(Line2D conn, double angle) {
		double dx = TBSUtils.dx(conn);
		double dy = TBSUtils.dy(conn);
		double dArrowX = Math.round(dx * Math.cos(angle) + dy * Math.sin(angle));
		double dArrowY = Math.round(dy * Math.cos(angle) - dx * Math.sin(angle));
		double arrowLength = Math.sqrt(dx * dx + dy * dy);
		dArrowX /= arrowLength * TBSGraphics.arrowLength;
		dArrowY /= arrowLength * TBSGraphics.arrowLength;
		int arrowX = (int) Math.round(dArrowX);
		int arrowY = (int) Math.round(dArrowY);
		return new Line2D.Double(
				conn.getP2().getX(),
				conn.getP2().getY(),
				conn.getP2().getX() + arrowX,
				conn.getP2().getY() + arrowY);
	}
  
	public static boolean isStringEmpty(String s){
		return (s == null || s.length() == 0);
	}
	
	public static String commaSeparated(Collection<?> values){
		StringBuffer returnVal = new StringBuffer();
		Iterator<?> iter = values.iterator();
		while(iter.hasNext()){
			String value = iter.next().toString();
			returnVal.append(value).append(",");
		}
		if(returnVal.length() > 0)
			return returnVal.deleteCharAt(returnVal.lastIndexOf(",")).toString();
		else
			return returnVal.toString();
	}
	
	public static List<HullCollision> hullCollisions(List<ConvexHull> hulls){
		List<HullCollision> hullCollisions = new LinkedList<HullCollision>();
		ConvexHull ch1, ch2;
		if(hulls.size() > 1){
			for(int i1=0;i1<hulls.size();i1++){
				for(int i2=hulls.size()-1;i2>i1;i2--){
					ch1 = hulls.get(i1);
					ch2 = hulls.get(i2);
					if (collide(ch1.getHullShape(), ch2.getHullShape()))
						hullCollisions.add(new HullCollision(1, ch1, ch2));
				}
			}
		}
		return hullCollisions;
	}
	
	public static boolean collide(Polygon p1, Polygon p2){
		if(p1 == null || p2 == null)
			return false;
		Area intersect = new Area(p1); 
		intersect.intersect(new Area(p2)); 
		return !intersect.isEmpty();
	}
	
	public static List<String> collisonText(AdminModel model){
		List<String> collisionText = new LinkedList<String>();
		collisionText.addAll(collisionText(model.getHullCollisions(false)));
		for(ConvexHull ch : model.getHulls(false))
			collisionText.addAll(collisionText(ch.getChildCollisions()));
		return collisionText;
	}
	
	private static List<String> collisionText(List<HullCollision> collisions){
		List<String> collisionText = new LinkedList<String>();
		for(HullCollision hc : collisions)
			collisionText.add(hc.getAnalysisText());
		return collisionText;
	}
	
	public static String commaSeparatedString(Collection<?> strings){
		StringBuilder buff = new StringBuilder(); 
	    String sep = ""; 
	    for (Object str : strings) { 
	        buff.append(sep); 
	        buff.append(str.toString()); 
	        sep = ", "; 
	    } 
	    return buff.toString(); 
	}
}
