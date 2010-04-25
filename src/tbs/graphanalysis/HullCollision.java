package tbs.graphanalysis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.LinkedList;
import java.util.List;

import tbs.model.AdminModel;
import tbs.view.dropdown.SubDropDown;

public class HullCollision extends SubDropDown{

	private int level;
	private ConvexHull hull1;
	private ConvexHull hull2;
	private List<Point> collisionPoints;
	private Point centroid;
	private String analysisText;
	private OptimalHulls optimalHulls;
	
	public HullCollision(int level, ConvexHull hull1, ConvexHull hull2){
		this.level = level;
		this.hull1 = hull1;
		this.hull2 = hull2;
		analysisText = new StringBuffer(" \u2022 ").append(hull1)
		.append(" group collides with the ")
		.append(hull2).append(" group.").toString();
		
		Area intersect = new Area(hull1.getHullShape()); 
		intersect.intersect(new Area(hull2.getHullShape()));
		
		AffineTransform at = new AffineTransform();
		PathIterator pi = intersect.getPathIterator(at);
		collisionPoints = new LinkedList<Point>();
		int segType;
		int centroidX = 0, centroidY = 0;
		while (pi.isDone() == false) {
			float[] coords = new float[6];
			segType = pi.currentSegment(coords);
			if(segType == PathIterator.SEG_LINETO || segType == PathIterator.SEG_MOVETO){
				Point p = new Point((int)coords[0], (int)coords[1]);
				collisionPoints.add(p);
				centroidX += p.x;
				centroidY += p.y;
			}
			pi.next();
		}
		centroidX = (centroidX/collisionPoints.size());
		centroidY = (centroidY/collisionPoints.size());
		centroid = new Point(centroidX, centroidY);
		optimalHulls = new OptimalHulls(this);
	}
	
	public void render(Graphics2D g2, int xOffset, int yOffset, AdminModel model){
		Polygon hull1Shape = new Polygon(), hull2Shape = new Polygon(),
			collisonShape = new Polygon();
		
		for(Point p : hull1.getHull())
			hull1Shape.addPoint(p.x - xOffset, p.y - yOffset);
		
		for(Point p : hull2.getHull())
			hull2Shape.addPoint(p.x - xOffset, p.y - yOffset);
		
		for(Point p : collisionPoints)
			collisonShape.addPoint(p.x - xOffset, p.y - yOffset);
		
		g2.setColor(new Color(255,36,0,160));
		g2.fill(collisonShape);
		
		g2.setStroke(new BasicStroke(3));
		g2.setColor(model.getGroupColor(hull1.getHullName()));
		g2.draw(hull1Shape);
		g2.setColor(model.getGroupColor(hull2.getHullName()));
		g2.draw(hull2Shape);
		g2.setStroke(new BasicStroke());
	}
	
	public String getAnalysisText(){return analysisText;}	
	public Point getCentroid() {return centroid;}
	public ConvexHull getHull1(){return hull1;}
	public ConvexHull getHull2(){return hull2;}
	public int getLevel(){return level;}
	public OptimalHulls getOptimalHulls(){return optimalHulls;}
	public String toString(){return hull1.getHullName() + " - " + hull2.getHullName() + (getDisplay() ? " \u2713" : "");}
	
}
