package tbs.graphanalysis;

import java.awt.Point;

public class HullLine {

	private Point point1;
	private Point point2;
	private double length;
	private float    slope;
	private boolean  slopeUndefine;

	public HullLine(Point p1, Point p2) {
		point1 = p1;
		point2 = p2;
		length = p1.distance(p2);
		if (p1.x == p2.x)
			slopeUndefine = true;
		else {    
			if (p2.y == p1.y)
				slope = (float)0;
			else
				slope = (float) (p2.y - p1.y) / (p2.x - p1.x);
			slopeUndefine = false;
		}
	}

	/**
	 * Given a Check point and determine if this check point is lying on the
	 * left side or right side of the first point of the line.
	 */
	public boolean onLeft(Point chkpt) {
		if (this.slopeUndefine) {
			if (chkpt.x < point1.x) return true;
			else {
				if (chkpt.x == point1.x) {
					if (((chkpt.y > point1.y) && (chkpt.y < point2.y)) ||
							((chkpt.y > point2.y) && (chkpt.y < point1.y)))
						return true;
					else
						return false;
				}
				else return false;
			}
		}
		else {            
			/* multiply the result to avoid the rounding error */
			int x3 = (int) (((chkpt.x + slope * (slope * point1.x 
					- point1.y + chkpt.y)) /
					(1 + slope * slope)) * 10000);
			int y3 = (int) ((slope * (x3 / 10000 - point1.x) + point1.y) * 10000);

			if (slope == (float)0) {
				if ((chkpt.y*10000) > y3) return true; else return false; }
			else { if (slope > (float)0) {
				if (x3 > (chkpt.x * 10000)) return true; else return false; }
			else {
				if ((chkpt.x * 10000) > x3) return true; else return false; }
			}
		}
	}
	
	public Boolean equals(HullLine hl){
		return (hl.getPoint1().equals(point1) || hl.getPoint1().equals(point2))
			&& (hl.getPoint2().equals(point1) || hl.getPoint2().equals(point2));
	}

	public Point getPoint1() {return point1;}
	public Point getPoint2() {return point2;}
	public double getLength() {return length;}
	public float getSlope() {return slope;}
	public boolean isSlopeUndefine() {return slopeUndefine;}
	
}
