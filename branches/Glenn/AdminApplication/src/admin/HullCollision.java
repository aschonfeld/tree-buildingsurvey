package admin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class HullCollision extends Displayable implements Renderable {

	private int level;
	private List<ConvexHull> hulls;
	private Set<List<Point>> collisionPoints;
	private List<Point> centroids;
	private String commaSepGroups;
	private String analysisText;
	private OptimalHulls optimalHulls;

	public HullCollision(int level, List<ConvexHull> hulls) {
		this.level = level;
		this.hulls = hulls;
		commaSepGroups = Common.commaSeparatedString(hulls);
		analysisText = new StringBuffer(" \u2022 Groups ").append(
				commaSepGroups).append(" have a collision.").toString();

		centroids = new LinkedList<Point>();
		collisionPoints = new HashSet<List<Point>>();
		Set<Set<Integer>> subGroups = SubGroupGenerator.getIndexSubGroups(hulls.size());
		
		for(Set<Integer> subGroup : subGroups){
			Integer[] indexes = subGroup.toArray(new Integer[0]);
			Area intersect = new Area(hulls.get(indexes[0]).getHullShape());
			for(int i=1;i<indexes.length;i++)
				intersect.intersect(new Area(hulls.get(indexes[i]).getHullShape()));
			if(!intersect.isEmpty()){
				AffineTransform at = new AffineTransform();
				PathIterator pi = intersect.getPathIterator(at);
				LinkedList<Point> points = new LinkedList<Point>();
				Polygon temp = new Polygon();
				int segType;
				int centroidX = 0, centroidY = 0;
				while (pi.isDone() == false) {
					float[] coords = new float[6];
					segType = pi.currentSegment(coords);
					if (segType == PathIterator.SEG_LINETO
							|| segType == PathIterator.SEG_MOVETO) {
						Point p = new Point((int) coords[0], (int) coords[1]);
						temp.addPoint(p.x, p.y);
						points.add(p);
						centroidX += p.x;
						centroidY += p.y;
					}
					pi.next();
				}
				centroidX = (centroidX / points.size());
				centroidY = (centroidY / points.size());
				centroids.add(new Point(centroidX, centroidY));
				collisionPoints.add(points);
			}
		}
	}

	public void render(Graphics g, Point offset) {
		Graphics2D g2 = (Graphics2D) g;
		Polygon collisionShape;
		g2.setColor(new Color(255, 36, 0, 160));
		for(List<Point> collision : collisionPoints){
			collisionShape = new Polygon();
			for (Point p : collision)
				collisionShape.addPoint(p.x - offset.x, p.y - offset.y);
			g2.fill(collisionShape);
		}

		g2.setStroke(new BasicStroke(3));
		for (ConvexHull hull : hulls) {
			g2.setColor(AdminApplication.getGroupColor(hull.getHullName()));
			hull.render(g2, offset);
		}
		g2.setStroke(new BasicStroke());
	}

	public String getAnalysisText() {
		return analysisText;
	}

	public List<Point> getCentroids() {
		return centroids;
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
