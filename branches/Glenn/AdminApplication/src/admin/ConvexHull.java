package admin;
import java.awt.Point;
import java.awt.Polygon;
import java.util.LinkedList;
import java.util.List;

/**
 * A class that demonstrates the graph algorithm, by accepting a group
 * of points from the current graph and by way of either the Quick Hull
 * algorithm or Brute Force algorithm, the Convex Hull of those points 
 * is calculated and returned in the form of a List of lines or points.
 */
public class ConvexHull {
	/** 
	 * Variable indicates which algorithm we are using
	 */
	public static final int Brute = 1;
	public static final int QUICK = 2;

	/**
	 * Stores all the points
	 */
	private List<Point> points = new LinkedList<Point>();
	
	/**
	 * Stores shape of the hull for collision detection
	 */
	private Polygon hullShape;
	
	/**
	 * Stores all the lines in the Hull
	 */
	private List<Line> hull;
	private List<Line> tempHull;

	/**
	 * Stores the name of the Hull
	 */
	private String hullName;
	
	/**
	 * The point we are comparing with the chkLn
	 */
	private Point currPt = new Point();
	
	public ConvexHull(int algor, List<Point> points, String hullName) {
		this.points = points;
		this.hullName = hullName;
		hullShape = new Polygon();
		hull = new LinkedList<Line>();
		switch (algor) {
			case Brute:
			BruteForce();
			break;
			case QUICK:
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

	public Polygon getHullShape() {return hullShape;}
	public List<Line> getHull() {return hull;}
	public String getHullName() {return hullName;}

	/**
	  * Brute Force Algorithm implementation
	  */
	 public void BruteForce() {
		 boolean leftMost, rightMost;
		 for (int x = 0; x < points.size(); x++) {
			 for (int y = (x+1); y < points.size(); y++) {
				 leftMost  = true;
				 rightMost = true;
				 Line temp = new Line(points.get(x), points.get(y));

				 for (int z = 0; z < points.size(); z++) {
					 currPt = points.get(z);
					 
					 if ((z != x) && (z != y)) {
						 if (temp.onLeft(points.get(z)))
							 leftMost = false;
						 else
							 rightMost = false;
					 }
				 }

				 if (leftMost || rightMost)
					 hull.add(new Line(points.get(x), points.get(y)));
			 }
		 }
	 }

	 /** 
	  * Quick Hull Algorithm implementation.
	  */
	 public void quickHull() {
		 tempHull = new LinkedList<Line>();
		 List<Point> P1 = new LinkedList<Point>();
		 List<Point> P2 = new LinkedList<Point>();
		 Point l = points.get(0);
		 Point r = points.get(0);
		 int minX = l.x;
		 int maxX = l.x;
		 int minAt = 0;
		 int maxAt = 0;	

		 /* find the max and min x-coord point */
		 for (int i = 1; i < points.size(); i++) {
			 currPt = points.get(i);	
			 if (currPt.x > maxX) {
				 r = currPt;
				 maxX = currPt.x;
				 maxAt = i;
			 }

			 if (currPt.x < minX) {
				 l = currPt;
				 minX = currPt.x;
				 minAt = i;
			 }
		 }

		 Line lr = new Line(l, r);
		 
		 /* find out each point is over or under the line formed by the two points */
		 /* with min and max x-coord, and put them in 2 group according to whether */
		 /* they are above or under                                                */
		 for (int i = 0; i < points.size(); i++) {
			 if ((i != maxAt) && (i != minAt)) {
				 currPt = points.get(i);
				 if (lr.onLeft(currPt))
					 P1.add(currPt);
				 else
					 P2.add(currPt);
			 }

		 };

		 /* put the max and min x-cord points in each group */
		 P1.add(l);
		 P1.add(r);

		 P2.add(l);
		 P2.add(r);

		 /* calculate the upper hull */
		 quick(P1, l, r, 0);

		 /* put the upper hull result in final result */
		 for (int k=0; k<tempHull.size(); k++)
			 hull.add(new Line(tempHull.get(k).point1, tempHull.get(k).point2));
		 
		 /* calculate the lower hull */
		 quick(P2, l, r, 1);

		 /* append the result from lower hull to final result */
		 for (int k=0; k<tempHull.size(); k++)
			 hull.add(new Line(tempHull.get(k).point1, tempHull.get(k).point2));

	 }


	 /**
	  * Recursive method to find out the Hull.
	  * faceDir is 0 if we are calculating the upper hull.
	  * faceDir is 1 if we are calculating the lower hull.
	  */
	 public synchronized void quick(List<Point> P, Point l, Point r, int faceDir) {
		 if (P.size() == 2) {
			 tempHull.add(new Line(P.get(0), P.get(1)));
			 return;
		 } else {
			 int hAt = splitAt(P, l, r);
			 Line lh = new Line(l, P.get(hAt));
			 Line hr = new Line(P.get(hAt), r);
			 List<Point> P1 = new LinkedList<Point>();
			 List<Point> P2 = new LinkedList<Point>();

			 for (int i = 0; i < (P.size() - 2); i++) {
				 if (i != hAt) {
					 currPt = P.get(i);
					 if (faceDir == 0) {
						 if (lh.onLeft(currPt))
							 P1.add(currPt);

						 if ((hr.onLeft(currPt)))
							 P2.add(currPt);
					 } else {
						 if (!(lh.onLeft(currPt)))
							 P1.add(currPt);

						 if (!(hr.onLeft(currPt)))
							 P2.add(currPt);
					 }
				 }
			 }

			 P1.add(l);
			 P1.add(P.get(hAt));

			 P2.add(P.get(hAt));
			 P2.add(r);

			 Point h = P.get(hAt);

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
	  * the point we are looking for.
	  * Return the index of this point in the Vector/
	  */
	 public synchronized int splitAt(List<Point> P, Point l, Point r) {
		 double    maxDist = 0;
		 Line newLn = new Line(l, r);

		 int x3 = 0, y3 = 0;
		 double distance = 0;
		 int farPt = 0;

		 for (int i = 0; i < (P.size() - 2); i++) {
			 if (newLn.slopeUndefine) {
				 x3 = l.x;
				 y3 = P.get(i).y;
			 } else {
				 if (r.y == l.y) {
					 x3 = P.get(i).x;
					 y3 = l.y;
				 } else {
					 x3 = (int) (((P.get(i).x + newLn.slope *
							 (newLn.slope * l.x - l.y + P.get(i).y))
									 / (1 + newLn.slope * newLn.slope)));
					 y3 = (int) ((newLn.slope * (x3 - l.x) + l.y));
				 }
			 }
			 int x1 = P.get(i).x;
			 int y1 = P.get(i).y;
			 distance = Math.sqrt(Math.pow((y1-y3), 2) + Math.pow((x1-x3), 2));

			 if (distance > maxDist) {
				 maxDist = distance;
				 farPt = i;
			 }
		 }
		 return farPt;
	 }
	 
	 public boolean equals( Object o ) {
		 if ( this == o )
			 return true;
		 if ( !(o instanceof ConvexHull) )
			 return false;
		 return ((ConvexHull) o).getHullName().equals(hullName);
	 }
	 
	 public class Line {
			private Point point1;
			private Point point2;
			private float    slope;
			private boolean  slopeUndefine;

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

			public Point getPoint1() {return point1;}
			public Point getPoint2() {return point2;}
			public float getSlope() {return slope;}
			public boolean isSlopeUndefine() {return slopeUndefine;}
		}
}