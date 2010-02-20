package admin;
import java.awt.Point;
import java.awt.Polygon;
import java.util.List;
import java.util.Vector;

/**
 * An applet that demonstrates the graph algorithm, by letting the user
 * pick the points in the screen and choose either the Quick Hull algor
 * or Brute Force algorithm, the program simulated the execution event,
 * showing which line or which point is being compared.
 *
 */

/**
 * Main Screen Panel for showing the result.
 */
public class ConvexHull {
	/** 
	 * Variable indicates which algorithm we are using
	 */
	public static final int Brute = 1;
	public static final int QUICK = 2;
	
	/**
	 * Variable indicates the demonstration speed
	 */
	public static final int ZERO = 0;
	public static final int FAST = 20;
	public static final int SLOW = 100;
	int speed = SLOW;

	/**
	 * Stores all the points
	 */
	Vector<Point> points = new Vector<Point>();
	
	Polygon hullShape = new Polygon();
	
	/**
	 * Stores all the lines in the Hull
	 */
	Vector<Line> hull   = new Vector<Line>();

	/**
	 * Stores all the lines being checking 
	 */
	Vector<Line> chkLns = new Vector<Line>();
	Vector<Line> tempLns = new Vector<Line>();

	/**
	 * The point we are comparing with the chkLn
	 */
	Point currPt = new Point();
	int cx,cy,cz;

	public ConvexHull(int algor, List<Point> vertices) {
		for(Point p : vertices)
			points.add(new Point(p.x, p.y));
		switch (algor) {
			case Brute: hull.removeAllElements();
			BruteForce();
			break;
			case QUICK: hull.removeAllElements();
			quickHull();
			break;
			default:    System.out.println("Error in call algor\n");
		}
		if(hull.size() > 0){
			hullShape.addPoint(hull.get(0).point1.x, hull.get(0).point1.y);
			hullShape.addPoint(hull.get(0).point2.x, hull.get(0).point2.y);
			for(int i=1;i<hull.size();i++)
				hullShape.addPoint(hull.get(i).point2.x, hull.get(i).point2.y);	
		}
	}

	 /**
	  * Brute Force Algorithm implementation
	  */
	 public void BruteForce() {
		 boolean leftMost, rightMost;
		 for (cx = 0; cx < points.size(); cx++) {
			 for (int cy = (cx+1); cy < points.size(); cy++) {
				 leftMost  = true;
				 rightMost = true;
				 Line temp = new Line(points.elementAt(cx), points.elementAt(cy));

				 for (int cz = 0; cz < points.size(); cz++) {
					 currPt = points.elementAt(cz);
					 chkLns.removeAllElements();
					 chkLns.addElement(new Line(points.elementAt(cx), points.elementAt(cy)));

					 if ((cz != cx) && (cz != cy)) {
						 if (temp.onLeft(points.elementAt(cz)))
							 leftMost = false;
						 else
							 rightMost = false;
					 }
				 }

				 if (leftMost || rightMost)
					 hull.addElement(new Line(points.elementAt(cx), points.elementAt(cy)));
			 }
		 }
	 }

	 int indexChkLn = 0;

	 /** 
	  * Quick Hull Algorithm implementation.
	  * Calculate the hull first and display the execution with the information from
	  * chklns and tempHull.
	  */

	 Vector<Line> tempHull = new Vector<Line>();

	 public void quickHull() {
		 Vector<Point> P1 = new Vector<Point>();
		 Vector<Point> P2 = new Vector<Point>();
		 Point l = points.elementAt(0);
		 Point r = points.elementAt(0);
		 int minX = l.x;
		 int maxX = l.x;
		 int minAt = 0;
		 int maxAt = 0;	

		 chkLns.removeAllElements();
		 tempLns.removeAllElements();
		 tempHull.removeAllElements();

		 /* find the max and min x-coord point */

		 for (int i = 1; i < points.size(); i++) {
			 currPt = points.elementAt(i);	
			 if (points.elementAt(i).x > maxX) {
				 r = points.elementAt(i);
				 maxX = points.elementAt(i).x;
				 maxAt = i;
			 }

			 if (points.elementAt(i).x < minX) {
				 l = points.elementAt(i);
				 minX = points.elementAt(i).x;
				 minAt = i;
			 }
		 }

		 Line lr = new Line(l, r);
		 tempLns.addElement(new Line(points.elementAt(maxAt), points.elementAt(minAt)));
		 chkLns.addElement(new Line(points.elementAt(maxAt), points.elementAt(minAt)));
		 
		 /* find out each point is over or under the line formed by the two points */
		 /* with min and max x-coord, and put them in 2 group according to whether */
		 /* they are above or under                                                */
		 for (int i = 0; i < points.size(); i++) {
			 if ((i != maxAt) && (i != minAt)) {
				 currPt = points.elementAt(i);

				 if (lr.onLeft(points.elementAt(i)))
					 P1.addElement(points.elementAt(i));
				 else
					 P2.addElement(points.elementAt(i));
			 }

		 };

		 /* put the max and min x-cord points in each group */
		 P1.addElement(l);
		 P1.addElement(r);

		 P2.addElement(l);
		 P2.addElement(r);

		 /* calculate the upper hull */
		 quick(P1, l, r, 0);

		 /* display the how the upper hull was calculated */
		 for (int i=0; i<tempLns.size(); i++) {
			 chkLns.addElement(new Line(tempLns.elementAt(i).point1, tempLns.elementAt(i).point2));
			 for (int j=0; j<points.size(); j++) {
				 if (tempLns.elementAt(i).onLeft(points.elementAt(j)))	
					 currPt = points.elementAt(j);
			 }
		 }

		 /* put the upper hull result in final result */
		 for (int k=0; k<tempHull.size(); k++)
			 hull.addElement(new Line(tempHull.elementAt(k).point1, tempHull.elementAt(k).point2));
		 chkLns.removeAllElements();
		 tempLns.removeAllElements();

		 /* calculate the lower hull */
		 quick(P2, l, r, 1);

		 /* show how the lower hull was calculated */
		 for (int i=0; i<tempLns.size(); i++) {
			 chkLns.addElement(new Line(tempLns.elementAt(i).point1, tempLns.elementAt(i).point2));
			 for (int j=0; j<points.size(); j++) {
				 if (!tempLns.elementAt(i).onLeft(points.elementAt(j)))
					 currPt = points.elementAt(j);
			 }
		 }

		 /* append the result from lower hull to final result */
		 for (int k=0; k<tempHull.size(); k++)
			 hull.addElement(new Line(tempHull.elementAt(k).point1, tempHull.elementAt(k).point2));

		 chkLns.removeAllElements();
	 }


	 /**
	  * Recursive method to find out the Hull.
	  * faceDir is 0 if we are calculating the upper hull.
	  * faceDir is 1 if we are calculating the lower hull.
	  */
	 public synchronized void quick(Vector<Point> P, Point l, Point r, int faceDir) {
		 if (P.size() == 2) {
			 tempHull.addElement(new Line(P.elementAt(0), P.elementAt(1)));
			 return;
		 } else {
			 int hAt = splitAt(P, l, r);
			 Line lh = new Line(l, P.elementAt(hAt));
			 Line hr = new Line(P.elementAt(hAt), r);
			 Vector<Point> P1 = new Vector<Point>();
			 Vector<Point> P2 = new Vector<Point>();

			 for (int i = 0; i < (P.size() - 2); i++) {
				 if (i != hAt) {
					 currPt = P.elementAt(i);
					 if (faceDir == 0) {
						 if (lh.onLeft(P.elementAt(i)))
							 P1.addElement(P.elementAt(i));

						 if ((hr.onLeft(P.elementAt(i))))
							 P2.addElement(P.elementAt(i));
					 } else {
						 if (!(lh.onLeft(P.elementAt(i))))
							 P1.addElement(P.elementAt(i));

						 if (!(hr.onLeft(P.elementAt(i))))
							 P2.addElement(P.elementAt(i));
					 }
				 }
			 }

			 P1.addElement(l);
			 P1.addElement(P.elementAt(hAt));

			 P2.addElement(P.elementAt(hAt));
			 P2.addElement(r);

			 Point h = P.elementAt(hAt);

			 tempLns.addElement(new Line(l, h));
			 tempLns.addElement(new Line(h, r));

			 if (faceDir == 0) {
				 quick(P1, l, h, 0);
				 quick(P2, h, r, 0);
			 } else {
				 quick(P1, l, h, 1);
				 quick(P2, h, r, 1);
			 }
			 return;
		 }
	 }

	 /**
	  * Find out a point which is in the Hull for sure among a group of points
	  * Since all the point are on the same side of the line formed by l and r,
	  * so the point with the longest distance perpendicular to this line is 
	  * the point we are lokking for.
	  * Return the index of this point in the Vector/
	  */
	 public synchronized int splitAt(Vector<Point> P, Point l, Point r) {
		 double    maxDist = 0;
		 Line newLn = new Line(l, r);

		 int x3 = 0, y3 = 0;
		 double distance = 0;
		 int farPt = 0;

		 for (int i = 0; i < (P.size() - 2); i++) {
			 if (newLn.slopeUndefine) {
				 x3 = l.x;
				 y3 = P.elementAt(i).y;
			 } else {
				 if (r.y == l.y) {
					 x3 = P.elementAt(i).x;
					 y3 = l.y;
				 } else {
					 x3 = (int) (((P.elementAt(i).x + newLn.slope *
							 (newLn.slope * l.x - l.y + P.elementAt(i).y))
									 / (1 + newLn.slope * newLn.slope)));
					 y3 = (int) ((newLn.slope * (x3 - l.x) + l.y));
				 }
			 }
			 int x1 = P.elementAt(i).x;
			 int y1 = P.elementAt(i).y;
			 distance = Math.sqrt(Math.pow((y1-y3), 2) + Math.pow((x1-x3), 2));

			 if (distance > maxDist) {
				 maxDist = distance;
				 farPt = i;
			 }
		 }
		 return farPt;
	 }
	 
	 class Line {
			Point point1;
			Point point2;
			float    slope;
			boolean  slopeUndefine;

			/**
			 * Line constructor.
			 */
			public Line(Point p1, Point p2) {
				point1 = p1;
				point2 = p2;
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
		}
}