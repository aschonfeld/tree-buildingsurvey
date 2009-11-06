package tbs;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tbs.model.Node;

public class TBSUtils {

	/**
	 * This method takes in two (@link Node) objects as parameters and based on where the
	 * two objects are in proportion to each other, two (@link Point) objects are computed
	 * representing the start and end of a line that will represent a connection in the applet.
	 * 
	 * @param start, (@link Node) where connection starts
	 * @param end, (@ Node) where connection ends
	 * @return (@link Point)[], start and and points of the connection
	 */
	public static Point[] computeConnectionBounds(Node start , Node end){
		List<Point> bounds = new LinkedList<Point>();
		int width, height;
		width = start.getWidth();
		height = start.getHeight();
		Map<String, Point> startCorners = new HashMap<String,Point>();
		startCorners.put("a",new Point(start.getLeftX(), start.getUpperY()+height));
		startCorners.put("ab", new Point(start.getLeftX()+(width/2), start.getUpperY()+height));
		startCorners.put("b",new Point(start.getLeftX()+width, start.getUpperY()+height));
		startCorners.put("bc", new Point(start.getLeftX()+width, start.getUpperY()+(height/2)));
		startCorners.put("c",new Point(start.getLeftX()+width, start.getUpperY()));
		startCorners.put("cd", new Point(start.getLeftX()+(width/2), start.getUpperY()));
		startCorners.put("d",new Point(start.getLeftX(), start.getUpperY()));
		startCorners.put("da", new Point(start.getLeftX(), start.getUpperY()+(height/2)));
		width = end.getWidth();
		height = end.getHeight();
		Map<String, Point> endCorners = new HashMap<String,Point>();
		endCorners.put("a",new Point(end.getLeftX(), end.getUpperY()+height));
		endCorners.put("ab", new Point(end.getLeftX()+(width/2), end.getUpperY()+height));
		endCorners.put("b",new Point(end.getLeftX()+width, end.getUpperY()+height));
		endCorners.put("bc", new Point(end.getLeftX()+width, end.getUpperY()+(height/2)));
		endCorners.put("c",new Point(end.getLeftX()+width, end.getUpperY()));
		endCorners.put("cd", new Point(end.getLeftX()+(width/2), end.getUpperY()));
		endCorners.put("d",new Point(end.getLeftX(), end.getUpperY()));
		endCorners.put("da", new Point(end.getLeftX(), end.getUpperY()+(height/2)));
		//Case 1(NorthWest)
		if((endCorners.get("c").x < startCorners.get("a").x) &&
				(endCorners.get("c").y > startCorners.get("a").y))
			return new Point[]{startCorners.get("a"), endCorners.get("c")};
		//Case 2(North)
		if((isInRange(endCorners.get("a").x, startCorners.get("a").x, startCorners.get("b").x) ||
				isInRange(endCorners.get("b").x, startCorners.get("a").x, startCorners.get("b").x)) &&
				(endCorners.get("d").y > startCorners.get("a").y))
			return new Point[]{startCorners.get("ab"), endCorners.get("cd")};
		//Case 3(NorthEast)
		if((endCorners.get("d").x > startCorners.get("b").x) &&
				(endCorners.get("d").y > startCorners.get("b").y))
			return new Point[]{startCorners.get("b"), endCorners.get("d")};	
		//Case 4(East)
		if((isInRange(endCorners.get("a").y, startCorners.get("c").y, startCorners.get("b").y) ||
				isInRange(endCorners.get("d").y, startCorners.get("c").y, startCorners.get("b").y)) &&
				(endCorners.get("a").x > startCorners.get("b").x))
			return new Point[]{startCorners.get("bc"), endCorners.get("da")};
		//Case 5(SouthEast)
		if((endCorners.get("a").x > startCorners.get("c").x) &&
				(endCorners.get("a").y < startCorners.get("c").y))
			return new Point[]{startCorners.get("c"), endCorners.get("a")};	
		//Case 6(South)
		if((isInRange(endCorners.get("a").x, startCorners.get("a").x, startCorners.get("b").x) ||
				isInRange(endCorners.get("b").x, startCorners.get("a").x, startCorners.get("b").x)) &&
				(endCorners.get("a").y < startCorners.get("d").y))
			return new Point[]{startCorners.get("cd"), endCorners.get("ab")};
		//Case 7(SouthWest)
		if((endCorners.get("b").x < startCorners.get("d").x) &&
				(endCorners.get("b").y < startCorners.get("d").y))
			return new Point[]{startCorners.get("d"), endCorners.get("b")};	
		//Case 8(West)
		if((isInRange(endCorners.get("b").y, startCorners.get("a").y, startCorners.get("d").y) ||
				isInRange(endCorners.get("c").y, startCorners.get("a").y, startCorners.get("d").y)) &&
				(endCorners.get("b").x < startCorners.get("a").x))
			return new Point[]{startCorners.get("da"), endCorners.get("bc")};
		return new Point[]{startCorners.get("da"), endCorners.get("bc")};
	}
	
	public static boolean isInRange(int val, int min, int max){
		return ((val >= min) && (val <= max));
	}
}
