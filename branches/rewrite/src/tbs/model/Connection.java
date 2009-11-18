//TBS version 0.4
//represents a connection between two node objects
//implemented to make connections selectable

package tbs.model;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;

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


	/**
	* Removes this connection from the tree. If we want to execute
	* two-way disconnect, we can uncomment the commented line. That's a
	* UI decision.
	*/	
	public void removeFromTree() {
		this.setSelected(false);
		toNode.removeConnection(fromNode);
		//fromNode.removeConnection(toNode);
		model.delete(this);
	}

	/**
	* Returns true for a point on the line, as determined algebraically
	*/
	public boolean contains(int x, int y)
	{
		if(drawOrTestLine(null, TBSUtils.getConnectionBounds(toNode, fromNode), x, y))
			return true;
		return false;
	}
	
	public static boolean drawOrTestLine(Graphics2D g2, Line2D conn, int x, int y) {
		double slope = 0;
		boolean vertical = false; // slope is infinite for vertical line
		double dx = TBSUtils.dx(conn);
		double dy = TBSUtils.dy(conn);
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
					g2.drawLine((int) conn.getP1().getX() - xOffset, minXY.y, (int) conn.getP2().getX() - xOffset, maxXY.y);
				}
				return false;
			} else {
				for(int xOffset = -2; xOffset <= 2; xOffset++) {
					if (isOnLine((int) conn.getP1().getX() - xOffset, minXY.y, (int) conn.getP2().getX() - xOffset, maxXY.y, x, y)) return true;
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
				yIntercept = conn.getP1().getY() - slope * conn.getP1().getX();
				int drawY = (int) Math.round(slope * minXY.x + yIntercept);
				int drawY2 = (int) Math.round(slope * maxXY.x + yIntercept);				
				if(g2 != null) {
					g2.setColor(TBSGraphics.connectionColor);
					g2.drawLine((minXY.x + xdiff), (drawY + ydiff), (maxXY.x - xdiff), (drawY2 - ydiff));
					g2.drawLine((minXY.x + xdiff), (drawY - ydiff), (maxXY.x + xdiff), (drawY2 - ydiff));
					g2.drawLine((minXY.x + xdiff), (drawY + ydiff), (maxXY.x - xdiff), (drawY2 + ydiff));
					g2.drawLine((minXY.x + xdiff), (drawY - ydiff), (maxXY.x + xdiff), (drawY2 + ydiff));
				} else {
					if(xdiff > 1) xdiff = 1;
					if(ydiff > 1) ydiff = 1;
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
	
	private static Point getMinXY(Line2D l) {
		return new Point(
				(int) (l.getP1().getX() < l.getP2().getX() ? l.getP1().getX() : l.getP2().getX()),
				(int) (l.getP1().getY() < l.getP2().getY() ? l.getP1().getY() : l.getP2().getY()));
	}
	
	private static Point getMaxXY(Line2D l) {
		return new Point(
				(int) (l.getP1().getX() > l.getP2().getX() ? l.getP1().getX() : l.getP2().getX()),
				(int) (l.getP1().getY() > l.getP2().getY() ? l.getP1().getY() : l.getP2().getY()));
	}
	
	private static boolean isOnLine(int x0, int y0, int x1, int y1, int x, int y) {
		Line2D temp = new Line2D.Double(new Point(x0, y0), new Point(x1, y1));
		if(Math.abs(temp.ptSegDist(new Point(x, y))) < 1.0) return true;
		return false;
	}

}
