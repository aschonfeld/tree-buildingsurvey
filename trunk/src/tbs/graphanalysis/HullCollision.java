package tbs.graphanalysis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import tbs.TBSUtils;
import tbs.model.AdminModel;
import tbs.model.OrganismNode;
import tbs.view.dropdown.SubDropDown;

public class HullCollision extends SubDropDown {

	private int level;
	private List<ConvexHull> hulls;
	private Set<List<Point>> collisionPoints;
	private List<OrganismNode> singleNodeCollisions;
	private List<Point> unionPoints;
	private List<Point> centroids;
	private String commaSepGroups;
	private String analysisText;
	private OptimalHulls optimalHulls;

	public HullCollision(int level, List<ConvexHull> hulls) {
		this.level = level;
		this.hulls = hulls;
		commaSepGroups = TBSUtils.commaSeparatedString(hulls);
		analysisText = new StringBuffer(" \u2022 Groups ").append(
				commaSepGroups).append(" have a collision.").toString();
		
		centroids = new LinkedList<Point>();
		unionPoints = new LinkedList<Point>();
		collisionPoints = new HashSet<List<Point>>();
		Set<Set<Integer>> subGroups = SubGroupGenerator.getIndexSubGroups(hulls.size());
		
		Area union = new Area();
		for(Set<Integer> subGroup : subGroups){
			Integer[] indexes = subGroup.toArray(new Integer[0]);
			Area intersect = new Area(hulls.get(indexes[0]).getHullShape());
			for(int i=1;i<indexes.length;i++)
				intersect.intersect(new Area(hulls.get(indexes[i]).getHullShape()));
			if(!intersect.isEmpty()){
				List<Point> points = TBSUtils.convertAreaToPoints(intersect);
				int centroidX = 0, centroidY= 0;
				for(Point p : points){
					centroidX += p.x;
					centroidY += p.y;
				}
				union.add(intersect);
				centroidX = (centroidX / points.size());
				centroidY = (centroidY / points.size());
				centroids.add(new Point(centroidX, centroidY));
				collisionPoints.add(points);
			}
		}
		unionPoints.addAll(TBSUtils.convertAreaToPoints(union));
		singleNodeCollisions = TBSUtils.smallCollision(hulls);
	}

	public void render(Graphics2D g2, int xOffset, int yOffset, AdminModel model) {
		Polygon collisionShape;
		g2.setColor(new Color(255, 36, 0, 160));
		collisionShape = new Polygon();
		for (Point p : unionPoints)
			collisionShape.addPoint(p.x - xOffset, p.y - yOffset);
		g2.fill(collisionShape);
		//Displaying Single Node Collisions
		for (OrganismNode o : singleNodeCollisions)
			g2.fill(new Rectangle2D.Double(o.getX() - (1.5 + xOffset), o.getY()
					- (1.5 + yOffset), o.getWidth() + 3, o.getHeight() + 3));
		g2.setStroke(new BasicStroke(3));
		for (ConvexHull hull : hulls) {
			g2.setColor(model.getGroupColor(hull.getHullName()));
			hull.render(g2, xOffset, yOffset, model);
		}
		
		g2.setStroke(new BasicStroke());
	}

	public String getAnalysisText() {
		return analysisText;
	}

	public List<Point> getCentroids() {
		return centroids;
	}
	
	public List<Point> getUnion(){
		return unionPoints;
	}

	public List<ConvexHull> getHulls() {
		return hulls;
	}

	public Set<List<Point>> getCollisionPoints() {
		return collisionPoints;
	}

	public int getLevel() {
		return level;
	}

	public OptimalHulls getOptimalHulls() {
		if (optimalHulls == null)
			optimalHulls = new OptimalHulls(this);
		return optimalHulls;
	}

	public String toString() {
		return commaSepGroups + (getDisplay() ? " \u2713" : "");
	}

}
