//TBS version 0.4
//represents a connection between two node objects
//implemented to make connections selectable

package tbs.model;

import java.awt.Graphics2D;
import java.awt.Point;

import tbs.TBSGraphics;
import tbs.TBSUtils;

public class Connection extends ModelElement
{

	private Node toNode;
	private Node fromNode;
	private TBSModel model;


	/**
	* Connection registers a connection between two nodes
	*/
	public Connection(TBSModel m, Node to, Node from)
	{
		toNode=to;
		fromNode=from;
		model = m;
	}
	
	/**
	* Returns the target node of this connection
	*/
	public Node getToNode()
	{
		return toNode;
	}
		
	/**
	* Returns the origin node of this connection
	*/
	public Node getFromNode()
	{
		return fromNode;
	}

	/**
	* We might use this (in future) to resolve connection/node collisions
	*/
	public boolean collidesWith(ModelElement e)
	{
		return false;
	}
	
	public void removeFromTree() {
		toNode.disconnectFrom(fromNode);
		fromNode.disconnectFrom(toNode);
		toNode.removeConnection(fromNode);
		fromNode.removeConnection(toNode);
		model.delete(this);
	}

	/**
	* Returns true for a point on the line, as determined algebraically
	*/
	public boolean contains(int x, int y)
	{
		Point[] conn = TBSUtils.computeConnectionBounds(toNode, fromNode);
		if(drawOrTestLine(null, conn, x, y)) return true;
		return false;
	}
	
	public static boolean drawOrTestLine(Graphics2D g2, Point[] conn, int x, int y) {
		double slope = 0;
		boolean vertical = false; // slope is infinite for vertical line
		double dx = (conn[1].x - conn[0].x);
		double dy = (conn[1].y - conn[0].y);
		double yIntercept = 0;
		if(dx == 0) {
			vertical = true;
		} else {
			slope = dy / dx;
		}
		Point minXY = getMinXY(conn);
		Point maxXY = getMaxXY(conn);
		if(vertical) {
			if(g2 != null) {
				g2.setColor(TBSGraphics.connectionColor);
				for(int xOffset = -2; xOffset <= 2; xOffset++) {
					g2.drawLine(conn[0].x - xOffset, minXY.y, conn[1].x - xOffset, maxXY.y);
				}
				return false;
			} else {
				for(int xOffset = -2; xOffset <= 2; xOffset++) {
					if (isOnLine(conn[0].x - xOffset, minXY.y, conn[1].x - xOffset, maxXY.y, x, y)) return true;
				}
				return false;
			}
		}
		int xOffset = 3;
		int yOffset = 3;
		double lineLength = Math.sqrt(dx * dx + dy * dy);
		double normalY = dy / lineLength / Math.sqrt(2.0);
		double normalX = dx / lineLength / Math.sqrt(2.0);
		xOffset -= (int) Math.round(Math.abs(normalX));
		yOffset -= (int) Math.round(Math.abs(normalY));
		for(int x2 = x - xOffset; x2 <= x + xOffset; x2++) {
			for(int y2 = y - yOffset; y2 <= y + yOffset; y2++) {
				int xdiff = x2 - x;
				int ydiff = y2 - y;
				yIntercept = conn[0].y - slope * conn[0].x;
				int drawY = (int) Math.round(slope * minXY.x + yIntercept);
				int drawY2 = (int) Math.round(slope * maxXY.x + yIntercept);				
				if(g2 != null) {
					g2.setColor(TBSGraphics.connectionColor);
					g2.drawLine((minXY.x + xdiff), (drawY + ydiff), (maxXY.x - xdiff), (drawY2 - ydiff));
					g2.drawLine((minXY.x + xdiff), (drawY - ydiff), (maxXY.x + xdiff), (drawY2 - ydiff));
					g2.drawLine((minXY.x + xdiff), (drawY + ydiff), (maxXY.x - xdiff), (drawY2 + ydiff));
					g2.drawLine((minXY.x + xdiff), (drawY - ydiff), (maxXY.x + xdiff), (drawY2 + ydiff));
				} else {
					//System.out.println("=========================================");
					boolean b0 = isOnLine((minXY.x + xdiff), (drawY + ydiff), (maxXY.x - xdiff), (drawY2 - ydiff), x, y);
					boolean b1 = isOnLine((minXY.x + xdiff), (drawY - ydiff), (maxXY.x + xdiff), (drawY2 - ydiff), x, y);
					boolean b2 = isOnLine((minXY.x + xdiff), (drawY + ydiff), (maxXY.x - xdiff), (drawY2 + ydiff), x, y);
					boolean b3 = isOnLine((minXY.x + xdiff), (drawY - ydiff), (maxXY.x + xdiff), (drawY2 + ydiff), x, y);
					//System.out.println("=========================================");
					if(b0 || b1 || b2 || b3) return true; 				
				}
			}
		}
		return false;
	}
	
	private static Point getMinXY(Point[] p) {
		int minX = p[0].x;
		int minY = p[0].y;
		for(int i = 1; i < p.length; i++) {
			if (p[i].x < minX) minX = p[i].x;
			if (p[i].y < minY) minY = p[i].y;
		}
		return new Point(minX, minY);
	}
	
	private static Point getMaxXY(Point[] p) {
		int maxX = p[0].x;
		int maxY = p[0].y;
		for(int i = 1; i < p.length; i++) {
			if (p[i].x > maxX) maxX = p[i].x;
			if (p[i].y > maxY) maxY = p[i].y;
		}
		return new Point(maxX, maxY);
	}
	
	private static boolean isOnLine(int x0, int y0, int x1, int y1, int x, int y) {
		double dx = (x1 - x0);
		double dy = (y1 - y0);
		double slope = 0;
		double yIntercept = 0;
		Point[] p = new Point[2];
		p[0] = new Point(x0, y0);
		p[1] = new Point(x1, y1);
		Point minXY = getMinXY(p);
		Point maxXY = getMaxXY(p);
		if((maxXY.x - minXY.x) < 4) {
			// eliminates a bug when slope is large
			minXY.x -= 2;
			maxXY.x += 2;
			if((x >= minXY.x) && (x <= maxXY.x) && (y >= minXY.y) && (y <= maxXY.y)) return true;
			return false;
		}
		if(dx == 0) {
			if(((x == x0) || (x == x1)) && ((y >= minXY.y) && (y <= maxXY.y))) return true;
			return false;
		} else {
			slope = dy / dx;
		}
		if((y >= minXY.y) && (y <= maxXY.y)) {
			if((x >= minXY.x) && (x <= maxXY.x)) {
				// x and y are within bounds 
				// use slope intercept form to see if point is on line
				yIntercept = y0 - slope * x0;
				if (y == (int) Math.round((slope * x) + yIntercept)) {
					return true;
				}
			}
		}
		//System.out.println(x0 + " " + y0 + " " + x1 + " " + y1 + " " + x + " " + y);
		return false;
	}
	
}
