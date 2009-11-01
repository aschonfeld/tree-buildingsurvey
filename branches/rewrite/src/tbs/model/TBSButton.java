package tbs.model;
//TBS version 0.3
//Non-Swing button class, based on OrganismNode

import java.awt.geom.*;

public class TBSButton extends ModelElement
{
	private String name;
	private int leftX;
	private int upperY;
	private int width;
	private int height;
	private Rectangle2D stringBounds;
	private TBSModel model;
	private int action;
	public static final int LINK=0;
	public static final int UNLINK=1;
	public static final int LABEL=2;
	public static final int DELETE=3;
	public static final int SPLIT=4;
	public static final int PRINT=5;
	public static final int UNDO=6;
	public static final int SAVE=7;

	public boolean collidesWith(ModelElement e)
		{
			return false;
		}

	public TBSButton(TBSModel m, String n, Rectangle2D sb, int a,
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
			case LINK:
				break;
			case UNLINK:
				break;
			case LABEL:
				break;
			case DELETE:
				break;
			case SPLIT:
				break;
			case PRINT:
				break;
			case UNDO:
				break;
			case SAVE:
				break;
		}
	}
}
