package admin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Timer;

public class OptimalHulls extends Displayable implements Renderable {

	private String hull1;
	private String hull2;
	private List<Point> originalHull1;
	private List<Point> originalHull2;
	private List<Vertex> originalVertices1;
	private List<Vertex> originalVertices2;
	private Point originalCentroid;
	private int level;
	private String text;
	
	private List<Point> optimalHull1Points;
	private List<Vertex> removedHull1Nodes;
	private List<Vertex> remainingHull1Nodes;
	private ConvexHull inProgressHull1;
	private List<Point> optimalHull2Points;
	private List<Vertex> removedHull2Nodes;
	private List<Vertex> remainingHull2Nodes;
	private ConvexHull inProgressHull2;
	private Point centroidInProgress;
	
	private Timer iterationWait;
	private ActionListener iterate = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			iterationWait.stop();
			iterateOptimization();
			if(collisionExists)
				iterationWait.start();
		}
	};
	
	private List<Point> collision;
	private boolean collisionExists;
	
	public OptimalHulls(HullCollision collision){
		level = collision.getLevel();
		
		ConvexHull h1 = collision.getHull1();
		hull1 = h1.getHullName();
		originalHull1 = new LinkedList<Point>();
		originalHull1.addAll(h1.getHull());
		originalVertices1 = new LinkedList<Vertex>();
		originalVertices1.addAll(h1.getNodes());
		removedHull1Nodes = new LinkedList<Vertex>();
		remainingHull1Nodes = new LinkedList<Vertex>();
		optimalHull1Points = new LinkedList<Point>();
		
		ConvexHull h2 = collision.getHull2();
		hull2 = h2.getHullName();
		originalHull2 = new LinkedList<Point>();
		originalHull2.addAll(h2.getHull());
		originalVertices2 = new LinkedList<Vertex>();
		originalVertices2.addAll(h2.getNodes());
		removedHull2Nodes = new LinkedList<Vertex>();
		remainingHull2Nodes = new LinkedList<Vertex>();
		optimalHull2Points = new LinkedList<Point>();
		
		originalCentroid = collision.getCentroid();
		iterationWait = new Timer(1000, iterate);
		initOptimization();
		
	}
	
	public void fullOptimization(){
		if(iterationWait.isRunning())
			iterationWait.stop();
		initOptimization();
		while(collisionExists)
			iterateOptimization();
		createText();
	}
	
	private void initOptimization(){
		optimalHull1Points.clear();
		optimalHull1Points.addAll(originalHull1);
		optimalHull2Points.clear();
		optimalHull2Points.addAll(originalHull2);
		remainingHull1Nodes.clear();
		remainingHull1Nodes.addAll(originalVertices1);
		removedHull1Nodes.clear();
		inProgressHull1 = new ConvexHull(remainingHull1Nodes, "");
		remainingHull2Nodes.clear();
		remainingHull2Nodes.addAll(originalVertices2);
		removedHull2Nodes.clear();
		inProgressHull2 = new ConvexHull(remainingHull2Nodes, "");
		centroidInProgress = originalCentroid;
		collision = new LinkedList<Point>();
		collisionExists = true;
	}
	
	private void iterateOptimization(){
		int closest=0;
		boolean isH1 = true;
		double smallestDist = centroidInProgress.distance(optimalHull1Points.get(0));
		for(int i=1;i<optimalHull1Points.size();i++){
			double temp = centroidInProgress.distance(optimalHull1Points.get(i));
			if(temp < smallestDist){
				smallestDist = temp;
				closest = i;
			}
		}
		for(int i=0;i<optimalHull2Points.size();i++){
			double temp = centroidInProgress.distance(optimalHull2Points.get(i));
			if(temp < smallestDist){
				smallestDist = temp;
				closest = i;
				isH1 = false;
			}
		}
		if(isH1){
			Point p = optimalHull1Points.remove(closest);
			for(Vertex v : remainingHull1Nodes){
				if(v.getRectangle().contains(p)){
					removedHull1Nodes.add(v);
					break;
				}
			}
			remainingHull1Nodes.remove(removedHull1Nodes.get(removedHull1Nodes.size()-1));
			inProgressHull1 = new ConvexHull(remainingHull1Nodes, "");
			optimalHull1Points = inProgressHull1.getHull();
		}else{
			Point p = optimalHull2Points.remove(closest);
			for(Vertex v : remainingHull2Nodes){
				if(v.getRectangle().contains(p)){
					removedHull2Nodes.add(v);
					break;
				}
			}
			remainingHull2Nodes.remove(removedHull2Nodes.get(removedHull2Nodes.size()-1));
			inProgressHull2 = new ConvexHull(remainingHull2Nodes, "");
			optimalHull2Points = inProgressHull2.getHull();
		}
		collisionExists = Common.collide(inProgressHull1.getHullShape(), inProgressHull2.getHullShape());
		if(collisionExists){
			HullCollision tempHC = new HullCollision(0, inProgressHull1, inProgressHull2);
			centroidInProgress = tempHC.getCentroid();
			collision = tempHC.getCollisionPoints();
		}
	}
	
	private void createText(){
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
		if(collisionExists){
			Polygon collisionShape = new Polygon();
			for(Point p : collision)
				collisionShape.addPoint(p.x - offset.x, p.y - offset.y);
			g2.setColor(new Color(255,36,0,160));
			g2.fill(collisionShape);
		}
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
			if(!AdminApplication.showNames){
				int xVal = (bounds.x - offset.x) + (v.getInfo().getImage().getWidth()/2);
				int yVal = bounds.y - (offset.y + Common.ySpacing);
				g2.setFont(Common.tooltipFont);
				xVal -= Common.getStringBounds(g2, v.getInfo().getTypes().get(level)).width/2;
				Common.drawCenteredString(g2, v.getInfo().getTypes().get(level), xVal, yVal, 0,
						0, Common.tooltipColor, Common.tooltipFont);
				g2.setFont(Common.font);
			}
		}
	}
	
	public String toString(){return hull1 + " - " + hull2 + (getDisplay() ? " \u2713" : "");}
	public String getText(){return text;}	
	public void startOptimization(){
		initOptimization();
		iterationWait.start();
	}
}
