package tbs.model;
//Non-Swing button class, based on OrganismNode

import java.awt.Rectangle;

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
	private Rectangle stringBounds;
	private TBSButtonType action;

	public boolean collidesWith(ModelElement e)
	{
		return false;
	}

	public TBSButton(int id, String name, Rectangle sb, TBSButtonType a,
 			int x, int y, int h, int w)
	{
		super(id);
		this.name = name;
		stringBounds=sb;
		action=a;
		leftX=x;
		upperY=y;
		width=w;
		height=h;
	}

	public Rectangle getStringBounds()
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
			case LINK:
				break;
			case DELETE:
				break;
			case PRINT:
				break;
			case UNDO:
				break;
		}
	}

	@Override
	public String dump() {
		// TODO Auto-generated method stub
		return null;
	}
}
