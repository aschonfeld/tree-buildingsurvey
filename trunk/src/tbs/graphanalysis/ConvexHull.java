package tbs.graphanalysis;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import tbs.TBSUtils;
import tbs.model.AdminModel;
import tbs.model.OrganismNode;
import tbs.view.dropdown.SubDropDown;

public class ConvexHull extends SubDropDown {

	public static Comparator<PolarPoint> polarPointComparator = new Comparator<PolarPoint>() {
		public int compare(PolarPoint p1, PolarPoint p2) {
			return p2.getPolarAngle().compareTo(p1.getPolarAngle());
		}
	};

	private List<OrganismNode> nodes;
	private List<Point> points;
	private ConvexHull parent = null;
	private int level;
	private List<Point> hull;
	private Polygon hullShape;
	private Point centroid;
	private String hullName;
	private List<ConvexHull> children = new LinkedList<ConvexHull>();
	private List<HullCollision> childCollisions;

	/**
	 * Constructor for base level hull (no parent)
	 */
	public ConvexHull(List<OrganismNode> nodes, String hullName) {
		this(nodes, hullName, null);
	}

	/**
	 * Constructor for child hull (has parent)
	 */
	public ConvexHull(List<OrganismNode> nodes, String hullName,
			ConvexHull parent) {
		this.level = parent == null ? 1 : parent.getLevel() + 1;
		this.nodes = nodes;

		// Construct points as well as any children hull that exist
		this.points = new LinkedList<Point>();
		Map<String, List<OrganismNode>> childrenGroups = new HashMap<String, List<OrganismNode>>();
		for (OrganismNode o : nodes) {
			this.points.add(o.getCenter());
			if (o.getTypes().containsKey(level + 1)) {
				if (childrenGroups.containsKey(o.getTypes().get(level + 1)))
					childrenGroups.get(o.getTypes().get(level + 1)).add(o);
				else {
					List<OrganismNode> temp = new LinkedList<OrganismNode>();
					temp.add(o);
					childrenGroups.put(o.getTypes().get(level + 1), temp);
				}
			}
		}

		this.hullName = hullName;
		this.parent = parent;
		hull = new ArrayList<Point>();
		hullShape = new Polygon();
		if (points.size() < 3) {
			hull.addAll(points);
			for (Point p : hull)
				hullShape.addPoint(p.x, p.y);
		} else
			GrahamScan();

		children = new LinkedList<ConvexHull>();
		if (childrenGroups.size() > 0) {
			for (Map.Entry<String, List<OrganismNode>> e : childrenGroups
					.entrySet())
				children.add(new ConvexHull(e.getValue(), e.getKey(), this));
		}
		childCollisions = TBSUtils.hullCollisions(level, children);

	}

	public String getHullName() {
		return hullName;
	}

	public List<Point> getHull() {
		return hull;
	}

	public Polygon getHullShape() {
		return hullShape;
	}

	public ConvexHull getParent() {
		return parent;
	}

	public int getLevel() {
		return level;
	}

	public List<ConvexHull> getChildren() {
		List<ConvexHull> allChildren = new LinkedList<ConvexHull>();
		for (ConvexHull child : children) {
			allChildren.add(child);
			allChildren.addAll(child.getChildren());
		}
		return allChildren;
	}

	public List<HullCollision> getChildCollisions() {
		return childCollisions;
	}

	public Point getCentroid() {
		return centroid;
	}

	public List<OrganismNode> getNodes() {
		return nodes;
	}

	public String toString() {
		return hullName + (getDisplay() ? " \u2713" : "");
	}

	public void render(Graphics2D g2, int xOffset, int yOffset, AdminModel model) {
		g2.setStroke(new BasicStroke(3));
		g2.setColor(model.getGroupColor(hullName));
		if(hull.size() > 2){
			Polygon temp = new Polygon();
			for (Point p : hull)
				temp.addPoint(p.x - xOffset, p.y - yOffset);
			g2.draw(temp);
		}else{
			if(hull.size() == 2){
				Line2D line = new Line2D.Double(hull.get(0), hull.get(1));
				g2.draw(line);
			}
			for(OrganismNode o : nodes)
				g2.draw(new Rectangle2D.Double(o.getX() - (1.5 + xOffset), o.getY()
						- (1.5 + yOffset), o.getWidth() + 3, o.getHeight() + 3));
		}
		g2.setStroke(new BasicStroke());
	}

	public void GrahamScan() {
		// Find the minimum y-coord, in case of tie find leftmost point out of
		// those
		List<Point> allPoints = new LinkedList<Point>();
		allPoints.addAll(points);
		int indexOfMin = 0;
		Point minimum = allPoints.get(0);
		for (int i = 1; i < allPoints.size(); i++) {
			if (allPoints.get(i).y <= minimum.y) {
				if (allPoints.get(i).y == minimum.y) {
					if (allPoints.get(i).x < minimum.x) {
						minimum = allPoints.get(i);
						indexOfMin = i;
					}
				} else {
					minimum = allPoints.get(i);
					indexOfMin = i;
				}
			}
		}

		/*
		 * Sort the remaining points by polar-angle in counter-clockwise order
		 * around 'leftMost', in case of two points having the same angle remove
		 * all but the farthest from 'leftMost'.
		 */
		allPoints.remove(indexOfMin);
		Map<Double, PolarPoint> polarAngles = new TreeMap<Double, PolarPoint>();
		for (Point p : allPoints) {
			Point vector = new Point(p.x - minimum.x, p.y - minimum.y);
			Double polarAngle = Math.atan2(vector.y, vector.x);
			if (polarAngles.containsKey(polarAngle)) {
				if (minimum.distance(polarAngles.get(polarAngle).getPoint()) < minimum
						.distance(p))
					polarAngles.put(polarAngle, new PolarPoint(p, polarAngle));
			} else
				polarAngles.put(polarAngle, new PolarPoint(p, polarAngle));
		}

		// Sort points in counterclockwise order (increasing polar angle)
		List<PolarPoint> polarPoints = new LinkedList<PolarPoint>();
		for (PolarPoint pp : polarAngles.values())
			polarPoints.add(pp);
		Collections.sort(polarPoints, polarPointComparator);
		polarPoints.add(0, new PolarPoint(minimum));

		Stack<Point> hullPoints = new Stack<Point>();
		hullPoints.push(polarPoints.get(0).getPoint());
		hullPoints.push(polarPoints.get(1).getPoint());
		hullPoints.push(polarPoints.get(2).getPoint());
		for (int i = 3; i < polarPoints.size(); i++) {
			Point head = polarPoints.get(i).getPoint();
			Point middle = hullPoints.pop();
			Point tail = hullPoints.pop();
			if (formsLeftTurn(tail, middle, head)) {
				hullPoints.push(tail);
				hullPoints.push(middle);
				hullPoints.push(head);
			} else {
				hullPoints.push(tail);
				i--;
			}
		}
		hullPoints.push(polarPoints.get(0).getPoint());
		hull.addAll(hullPoints);
		int centroidX = 0, centroidY = 0;
		for (Point p : hull) {
			hullShape.addPoint(p.x, p.y);
			centroidX += p.x;
			centroidY += p.y;
		}
		centroidX = (centroidX / hull.size());
		centroidY = (centroidY / hull.size());
		centroid = new Point(centroidX, centroidY);
	}

	public static boolean formsLeftTurn(Point a, Point b, Point c) {
		return (((a.x - b.x) * (c.y - b.y)) - ((c.x - b.x) * (a.y - b.y))) >= 0;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ConvexHull))
			return false;
		return ((ConvexHull) o).getHullName().equals(hullName);
	}

	public class PolarPoint {

		private Double polarAngle;
		private Point point;

		public PolarPoint(Point point) {
			this(point, new Double("0"));
		}

		public PolarPoint(Point point, Double polarAngle) {
			this.point = point;
			this.polarAngle = polarAngle;
		}

		public Double getPolarAngle() {
			return polarAngle;
		}

		public Point getPoint() {
			return point;
		}
	}
}