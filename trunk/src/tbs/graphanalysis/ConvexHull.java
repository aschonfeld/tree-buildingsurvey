package tbs.graphanalysis;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tbs.TBSUtils;
import tbs.model.OrganismNode;

/**
 * A class that demonstrates the graph algorithm, by accepting a group
 * of points from the current graph and by way of either the Quick Hull
 * algorithm or Brute Force algorithm, the Convex Hull of those points 
 * is calculated and returned in the form of a List of lines or points.
 * 
 * @author Jeff C. So(University of Lethbridge) & Andrew Schonfeld
 *  
 */
public class ConvexHull {
	/**
	 * Stores all the points
	 */
	private List<Point> points = new LinkedList<Point>();
	
	private ConvexHull parent = null;
	
	private int level;
	
	private List<ConvexHull> children = new LinkedList<ConvexHull>();
	
	private List<HullCollision> childCollisions;
	
	/**
	 * Stores shape of the hull for collision detection
	 */
	private Polygon hullShape;
	
	/**
	 * Stores all the lines in the Hull
	 */
	private List<HullLine> hull;
	private List<HullLine> tempHull;

	/**
	 * Stores the name of the Hull
	 */
	private String hullName;
	
	private Boolean displayHull;
	
	/**
	 * The point we are comparing with the chkLn
	 */
	private Point currPt = new Point();
	
	/**
	 * Constructor for base level hull (no parent)
	 */
	public ConvexHull(List<OrganismNode> nodes, String hullName) {
		this(nodes, hullName, null);
	}
	
	/**
	 * Constructor for child hull (has parent)
	 */
	public ConvexHull(List<OrganismNode> nodes, String hullName, ConvexHull parent) {
		this.level = parent == null ? 1 : parent.getLevel()+1;
		
		//Construct points as well as any children hull that exist
		this.points = new LinkedList<Point>();
		Map<String, List<OrganismNode>> childrenGroups = new HashMap<String, List<OrganismNode>>();
		for(OrganismNode o : nodes){
			this.points.add(o.getCenter());
			if(o.getTypes().containsKey(level+1)){
				if(childrenGroups.containsKey(o.getTypes().get(level+1)))
					childrenGroups.get(o.getTypes().get(level+1)).add(o);
				else{
					List<OrganismNode> temp = new LinkedList<OrganismNode>();
					temp.add(o);
					childrenGroups.put(o.getTypes().get(level+1), temp);
				}
			}
		}
		
		this.hullName = hullName;
		this.parent = parent;
		displayHull = false;
		hullShape = new Polygon();
		hull = new LinkedList<HullLine>();
		quickHull();
		if(hull.size() > 0){
			for(HullLine hl : hull){
				if(!hullShape.contains(hl.getPoint1()))
					hullShape.addPoint(hl.getPoint1().x, hl.getPoint1().y);
				if(!hullShape.contains(hl.getPoint2()))
					hullShape.addPoint(hl.getPoint2().x, hl.getPoint2().y);
			}
		}
		children = new LinkedList<ConvexHull>();
		if(childrenGroups.size() > 0){
			for(Map.Entry<String, List<OrganismNode>> e : childrenGroups.entrySet())
				children.add(new ConvexHull(e.getValue(), e.getKey(), this));
		}
		childCollisions = TBSUtils.hullCollisions(children);
		
	}

	public Polygon getHullShape() {return hullShape;}
	public List<HullLine> getHull() {return hull;}
	public String getHullName() {return hullName;}
	public Boolean getDisplayHull() {return displayHull;}
	public void setDisplayHull(Boolean displayHull) {this.displayHull = displayHull;}
	public ConvexHull getParent() {
		return parent;
	}

	public int getLevel() {
		return level;
	}

	public List<ConvexHull> getChildren() {
		List<ConvexHull> allChildren = new LinkedList<ConvexHull>();
		for(ConvexHull child : children){
			allChildren.add(child);
			allChildren.addAll(child.getChildren());
		}
		return allChildren;
	}

	public List<HullCollision> getChildCollisions() {
		return childCollisions;
	}

	public void toggleHull(){this.displayHull = !displayHull;}
	public String toString(){return hullName + (displayHull ? " \u2713" : "");}
	
	public void render(Graphics2D g2, int xOffset, int yOffset){
		for(HullLine l : hull)
			g2.draw(new Line2D.Double(l.getPoint1().x - xOffset,
					l.getPoint1().y - yOffset,
					l.getPoint2().x - xOffset,
					l.getPoint2().y - yOffset));
	}
	 

	/** 
	  * Quick Hull Algorithm implementation.
	  */
	 public void quickHull() {
		 tempHull = new LinkedList<HullLine>();
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

		 HullLine lr = new HullLine(l, r);
		 
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
			 hull.add(new HullLine(tempHull.get(k).getPoint1(), tempHull.get(k).getPoint2()));
		 
		 /* calculate the lower hull */
		 quick(P2, l, r, 1);

		 /* append the result from lower hull to final result */
		 for (int k=0; k<tempHull.size(); k++)
			 hull.add(new HullLine(tempHull.get(k).getPoint1(), tempHull.get(k).getPoint2()));

	 }


	 /**
	  * Recursive method to find out the Hull.
	  * faceDir is 0 if we are calculating the upper hull.
	  * faceDir is 1 if we are calculating the lower hull.
	  */
	 public synchronized void quick(List<Point> P, Point l, Point r, int faceDir) {
		 if (P.size() == 2) {
			 tempHull.add(new HullLine(P.get(0), P.get(1)));
			 return;
		 } else {
			 int hAt = splitAt(P, l, r);
			 HullLine lh = new HullLine(l, P.get(hAt));
			 HullLine hr = new HullLine(P.get(hAt), r);
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
		 HullLine newLn = new HullLine(l, r);

		 int x3 = 0, y3 = 0;
		 double distance = 0;
		 int farPt = 0;

		 for (int i = 0; i < (P.size() - 2); i++) {
			 if (newLn.isSlopeUndefine()) {
				 x3 = l.x;
				 y3 = P.get(i).y;
			 } else {
				 if (r.y == l.y) {
					 x3 = P.get(i).x;
					 y3 = l.y;
				 } else {
					 x3 = (int) (((P.get(i).x + newLn.getSlope() *
							 (newLn.getSlope() * l.x - l.y + P.get(i).y))
									 / (1 + newLn.getSlope() * newLn.getSlope())));
					 y3 = (int) ((newLn.getSlope() * (x3 - l.x) + l.y));
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
}