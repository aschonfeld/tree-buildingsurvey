//TBS version 0.4
//TBS utils: utility functions of TBS

package tbs;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import tbs.model.Node;

public class TBSUtils {

	public static boolean isInRange(int val, int min, int max){
		return ((val >= min) && (val <= max));
	}
	
	/**
	 * This method takes in two (@link Node) objects as parameters and based on where the
	 * two objects are in proportion to each other, two (@link Point) objects are computed
	 * representing the start and end of a line that will represent a connection in the applet.
	 * 
	 * @param start, (@link Node) where connection starts
	 * @param end, (@ Node) where connection ends
	 * @return (@link Point)[], start and and points of the connection
	 */
	public static Point[] getConnectionBounds(Node n1, Node n2){
		Point start = new Point(),end = new Point();
		Point n1Mid = getNodeCenter(n1), n2Mid = getNodeCenter(n2);
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
		return new Point[]{start, end};
	}

	/**
	* Takes a value and a one-dimensional range defined by min and max,
	* and returns true if the object is within the range, inclusive.
	*/
	private static List<Line2D> nodeSides(Node n){
		int width, height, x, y;
		width = n.getWidth();
		height = n.getHeight();
		x = n.getLeftX();
		y = n.getUpperY();
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
	public static Point getNodeCenter(Node n){
		return new Point(n.getLeftX() + (n.getWidth()/2),
				n.getUpperY() + (n.getHeight()/2));	
	}

}