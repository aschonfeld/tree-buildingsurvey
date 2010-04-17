package tbs.graphanalysis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import tbs.TBSGraphics;

public class HullCollision {

	private int level;
	private String hull1;
	private int hull1Index;
	private List<HullLine> hull1Lines;
	private String hull2;
	private int hull2Index;
	private List<HullLine> hull2Lines;
	private String analysisText;
	private Boolean displayCollision;
	
	public static Comparator<Point> pointComparator = new Comparator<Point>() {
		public int compare( Point p1, Point p2 ) {
			return (new Double(Math.abs(Math.atan2(p1.y, p1.x)))).compareTo(new Double(Math.abs(Math.atan2(p2.y, p2.x))));
		}
	};
	
	public HullCollision(int level, int hull1Index, ConvexHull hull1,
			int hull2Index, ConvexHull hull2){
		this.level = level;
		this.hull1 = hull1.getHullName();
		this.hull1Index = hull1Index;
		this.hull1Lines = hull1.getHull();
		this.hull2 = hull2.getHullName();
		this.hull2Index = hull2Index;
		this.hull2Lines = hull2.getHull();
		analysisText = new StringBuffer(" \u2022 ").append(hull1)
		.append(" group collides with the ")
		.append(hull2).append(" group.").toString();
		displayCollision = false;
	}
	
	public void render(Graphics2D g2, int xOffset, int yOffset){
		Polygon hull1Shape = new Polygon(), hull2Shape = new Polygon();
		List<Point> hull1ShapePoints = new LinkedList<Point>(), hull2ShapePoints = new LinkedList<Point>();
		List<HullLine> adjHull1, adjHull2;
		Point point1, point2;
		adjHull1 = new LinkedList<HullLine>();
		for(HullLine l : hull1Lines){
			point1 = new Point(l.getPoint1().x - xOffset, l.getPoint1().y - yOffset);
			point2 = new Point(l.getPoint2().x - xOffset, l.getPoint2().y - yOffset);
			adjHull1.add(new HullLine(point1, point2));
			if(!hull1ShapePoints.contains(point1))
				hull1ShapePoints.add(point1);
			if(!hull1ShapePoints.contains(point2))
				hull1ShapePoints.add(point2);
		}
		Collections.sort(hull1ShapePoints, pointComparator);
		
		adjHull2 = new LinkedList<HullLine>();
		for(HullLine l : hull2Lines){
			point1 = new Point(l.getPoint1().x - xOffset, l.getPoint1().y - yOffset);
			point2 = new Point(l.getPoint2().x - xOffset, l.getPoint2().y - yOffset);
			adjHull2.add(new HullLine(point1, point2));
			if(!hull2ShapePoints.contains(point1))
				hull2ShapePoints.add(point1);
			if(!hull2ShapePoints.contains(point2))
				hull2ShapePoints.add(point2);
		}
		Collections.sort(hull2ShapePoints, pointComparator);
		
		for(Point p : hull1ShapePoints)
			hull1Shape.addPoint(p.x, p.y);
		
		for(Point p : hull2ShapePoints)
			hull2Shape.addPoint(p.x, p.y);
		
		Area intersect = new Area(hull1Shape); 
		intersect.intersect(new Area(hull2Shape)); 
		g2.setColor(Color.RED);
		g2.fill(intersect);
		g2.setStroke(new BasicStroke(3));
		g2.setColor(TBSGraphics.hullColors[hull1Index]);
		g2.draw(hull1Shape);
		//for(HullLine l : adjHull1)
		//	g2.draw(new Line2D.Double(l.getPoint1().x, l.getPoint1().y,
		//			l.getPoint2().x, l.getPoint2().y));
		g2.setColor(TBSGraphics.hullColors[hull2Index]);
		g2.draw(hull2Shape);
		//for(HullLine l : adjHull2)
		//	g2.draw(new Line2D.Double(l.getPoint1().x, l.getPoint1().y,
		//			l.getPoint2().x, l.getPoint2().y));
		g2.setStroke(new BasicStroke());
	}
	
	public String getAnalysisText(){
		return analysisText;
	}
	
	public Boolean getDisplayCollision() {return displayCollision;}
	public void setDisplayCollision(Boolean displayCollision) {this.displayCollision = displayCollision;}
	
	public void toggleCollision(){this.displayCollision = !displayCollision;}
	public String toString(){return hull1 + " - " + hull2 + (displayCollision ? " \u2713" : "");}
	
}
