package admin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

public class OptimalHulls extends Displayable implements Renderable {

	private String hull1;
	private String hull2;
	private int level;
	private String text;
	
	private List<Point> optimalHull1Points;
	private List<Vertex> removedHull1Nodes;
	private List<Point> optimalHull2Points;
	private List<Vertex> removedHull2Nodes;
	
	public OptimalHulls(HullCollision collision){
		removedHull1Nodes = new LinkedList<Vertex>();
		removedHull2Nodes = new LinkedList<Vertex>();
		ConvexHull h1 = collision.getHull1();
		ConvexHull h2 = collision.getHull2();
		level = collision.getLevel();
		hull1 = h1.getHullName();
		hull2 = h2.getHullName();
		List<Point> hull1Points = new LinkedList<Point>();
		hull1Points.addAll(h1.getHull());
		List<Point> hull2Points = new LinkedList<Point>();
		hull2Points.addAll(h2.getHull());
		List<Vertex> hull1Nodes = new LinkedList<Vertex>();
		hull1Nodes.addAll(h1.getNodes());
		List<Vertex> hull2Nodes = new LinkedList<Vertex>();
		hull2Nodes.addAll(h2.getNodes());
		ConvexHull tempHull1 = new ConvexHull(hull1Nodes, "");
		ConvexHull tempHull2 = new ConvexHull(hull2Nodes, "");
		Point collisionCentroid = collision.getCentroid();
		boolean collisionExists = true;
		while(collisionExists){
			int closest=0;
			boolean isH1 = true;
			double smallestDist = collisionCentroid.distance(hull1Points.get(0));
			for(int i=1;i<hull1Points.size();i++){
				double temp = collisionCentroid.distance(hull1Points.get(i));
				if(temp < smallestDist){
					smallestDist = temp;
					closest = i;
				}
			}
			for(int i=0;i<hull2Points.size();i++){
				double temp = collisionCentroid.distance(hull2Points.get(i));
				if(temp < smallestDist){
					smallestDist = temp;
					closest = i;
					isH1 = false;
				}
			}
			if(isH1){
				Point p = hull1Points.remove(closest);
				for(Vertex v : hull1Nodes){
					if(v.getRectangle().contains(p)){
						removedHull1Nodes.add(v);
						break;
					}
				}
				hull1Nodes.remove(removedHull1Nodes.get(removedHull1Nodes.size()-1));
				tempHull1 = new ConvexHull(hull1Nodes, "");
				hull1Points = tempHull1.getHull();
			}else{
				Point p = hull2Points.remove(closest);
				for(Vertex v : hull2Nodes){
					if(v.getRectangle().contains(p)){
						removedHull2Nodes.add(v);
						break;
					}
				}
				hull2Nodes.remove(removedHull2Nodes.get(removedHull2Nodes.size()-1));
				tempHull2 = new ConvexHull(hull2Nodes, "");
				hull2Points = tempHull2.getHull();
			}
			collisionExists = Common.collide(tempHull1.getHullShape(), tempHull2.getHullShape());
			if(collisionExists){
				HullCollision tempHC = new HullCollision(0, tempHull1, tempHull2);
				collisionCentroid = tempHC.getCentroid();
			}
		}
		optimalHull1Points = hull1Points;
		optimalHull2Points = hull2Points;
		StringBuffer textBuff = new StringBuffer("\"Optimal Groups\" is optimization of the current student's arrangement of organisms so that groupings no longer collide.");
		textBuff.append(" This particular optimization required the removal of ");
		textBuff.append(removedHull1Nodes.size()).append(" ").append(hull1);
		if(removedHull1Nodes.size() > 1)
			textBuff.append("s");
		textBuff.append(" and ");
		textBuff.append(removedHull2Nodes.size()).append(" ").append(hull2);
		if(removedHull2Nodes.size() > 1)
			textBuff.append("s");
		textBuff.append(" in order to eliminate group collisions.");
		text = textBuff.toString();
	}
	
	
	
	public void render(Graphics g, Point offset){
		Graphics2D g2 = (Graphics2D) g;
		Polygon hull1Shape = new Polygon(), hull2Shape = new Polygon();

		for(Point p : optimalHull1Points)
			hull1Shape.addPoint(p.x - offset.x, p.y - offset.y);

		for(Point p : optimalHull2Points)
			hull2Shape.addPoint(p.x - offset.x, p.y - offset.y);

		g2.setStroke(new BasicStroke(3));
		g2.setColor(AdminApplication.getGroupColor(hull1));
		g2.draw(hull1Shape);
		g2.setColor(AdminApplication.getGroupColor(hull2));
		g2.draw(hull2Shape);
		renderRemoved(g2, removedHull1Nodes, offset);
		renderRemoved(g2, removedHull2Nodes, offset);
	}
	
	public void renderRemoved(Graphics2D g2, List<Vertex> nodes, Point offset){
		Rectangle bounds;
		for(Vertex v : nodes){
			bounds = v.getRectangle();
			g2.setStroke(new BasicStroke(3));
			g2.setColor(Color.RED);
			g2.draw(new Rectangle2D.Double(bounds.x-(1.5+offset.x), bounds.y-(1.5+offset.y),
					bounds.width + 3, bounds.height + 3));
			g2.setStroke(new BasicStroke());
			int xVal = (bounds.x - offset.x) + (v.getInfo().getImage().getWidth()/2);
			int yVal = bounds.y - (offset.y + Common.ySpacing);
			g2.setFont(Common.tooltipFont);
			xVal -= Common.getStringBounds(g2, v.getInfo().getTypes().get(level)).width/2;
			Common.drawCenteredString(g2, v.getInfo().getTypes().get(level), xVal, yVal, 0,
					0, Common.tooltipColor, Common.tooltipFont);
			g2.setFont(Common.font);
		}
	}
	
	public String toString(){return hull1 + " - " + hull2 + (getDisplay() ? " \u2713" : "");}
	public String getText(){return text;}	
}
