package tbs.model;
//Non-Swing button class, based on OrganismNode

import java.awt.geom.Rectangle2D;

import tbs.view.TBSButtonType;


/**
* Deprecated home-made button class. Buttons are currently implemented
* in TBSController.
*/
public class TBSButton extends ModelElement
{
	private String name;
	private int leftX;
	private int upperY;
	private int width;
	private int height;
	private Rectangle2D stringBounds;
	private TBSModel model;
	private TBSButtonType action;

	public boolean collidesWith(ModelElement e)
	{
		return false;
	}

	public TBSButton(TBSModel m, String n, Rectangle2D sb, TBSButtonType a,
 			int x, int y, int h, int w)
	{
		model = m;
		name = n;
		stringBounds=sb;
		action=a;
		leftX=x;
		upperY=y;
		width=w;
		height=h;
	}

	public Rectangle2D getStringBounds()
	{
		return stringBounds;
	}

	public boolean contains(int x, int y)
	{
		if ( (x>leftX) && (x<(leftX+width))  &&
			  (y>upperY) && (y<(upperY+height)) )
		{
			return true;
		}
		return false;
		
	}
	public int getLeftX()
	{
		return leftX;
	}
	
	public int getUpperY()
	{
		return upperY;
	}

	public int getHeight()
	{
		return height;
	}

	public int getWidth()
	{
		return width;
	}
	
	public String getName()
	{
		return name;
	}

	public void doThis()
	{
		switch (action) 
		{
			case CONNECT:
				break;
			case DELETE:
				break;
			case PRINT:
				break;
			case UNDO:
				break;
			case SAVE:
				break;
		}
	}
	
	// This is just here so we don't get a warning
	public TBSModel getModel() {
		return model;
	}
	
	public void removeFromTree() {};
}
