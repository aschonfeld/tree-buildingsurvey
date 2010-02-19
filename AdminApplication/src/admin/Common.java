package admin;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collection;


public class Common {
	
    public static Color organismStringColor = Color.BLACK;
    public static Color organismBoxColor = Color.WHITE;
    public static Color connectionColor = new Color(0.5f, 1.0f, 0.5f);
    public static Color emptyNodeColor = new Color(0.5f, 0.5f, 1.0f);

    /**
     * The fixed height of all OrganismNodes. Value is calculated in
     * TBSModel.createModelElements (CHECK THIS)
     */ 
    public static int organismNodeWidth = 0;
    public static int organismNodeHeight = 0;
	public static int ySpacing = 5;

	/**
	* The fixed width of all EmptyNodes. 
	*/ 
	public static int emptyNodeWidth = 20;

	/**
	* The fixed height of all EmptyNodes. 
	*/ 
	public static int emptyNodeHeight = 20;

    
    /**
     * Minimum number of pixels around the right and left of an organism's name
     */
     public static int paddingWidth = 5;    
    
     public static Font font = new Font("default", Font.BOLD, 16);
     
    /**
     * Returns the @Rectangle2D surrounding a piece of text
     */
     public static Dimension getStringBounds(Graphics2D g2, String s) {
             if(isStringEmpty(s))
                     return new Dimension();
             TextLayout layout = new TextLayout(s, g2.getFont(), g2.getFontRenderContext());
             Rectangle2D bounds = layout.getBounds();
             return new Dimension((int) bounds.getWidth(), (int) bounds.getHeight());
     }
       
      public static boolean isStringEmpty(String s){
          return (s == null || s.length() == 0);
  }

      public static Dimension get2DStringBounds(Graphics2D g2, Collection<?> strings) 
      {
              Point max = new Point(0,0);
              for(Object s: strings) 
              {
                      Dimension bounds = getStringBounds(g2, s.toString());
                      if(bounds.width > max.x) 
                              max.x = (int) bounds.getWidth();
                      if(bounds.height > max.y) 
                              max.y = (int) bounds.getHeight();
              }
              return new Dimension(max.x, max.y);
      }
      
      public static Dimension get2DImageBounds(Graphics2D g2, Collection<BufferedImage> images) 
      {
              Point max = new Point(0,0);
              for(BufferedImage i: images) 
              {
                      if(i.getWidth() > max.x) 
                              max.x = i.getWidth();
                      if(i.getHeight() > max.y) 
                              max.y = i.getHeight();
              }
              return new Dimension(max.x, max.y);
      }
      
      public static void drawCenteredString(Graphics2D g2, String s, 
                      int leftX, int upperY, int width, int height) { 
              drawCenteredString(g2, s, leftX, upperY, width, height, Color.black);
      }
      
      /**
       * Paints a string centered in the rectangle defined.
       */      
       public static void drawCenteredString(Graphics2D g2, String s, 
                       int leftX, int upperY, int width, int height, Color c){
               drawCenteredString(g2, s, leftX, upperY, width, height, c, font);
       }
       
       public static void drawCenteredString(Graphics2D g2, String s, 
                               int leftX, int upperY, int width, int height, Color c, Font f) 
       {
               if(isStringEmpty(s))
                       return;
               g2.setColor(c);
               TextLayout layout = new TextLayout(s, g2.getFont(), g2.getFontRenderContext());
               Rectangle2D bounds = layout.getBounds();
               int stringHeight = (int) bounds.getHeight();
               int stringWidth = (int) bounds.getWidth();
               float x,y;
               if(width == 0)
                       x = leftX;
               else
                       x = leftX + (width - stringWidth) / 2;
               if(height == 0)
                       y = upperY;
               else
                       y = upperY + height - (height - stringHeight) / 2;
               // if width or height is 0, do not center along that axis
               layout.draw(g2, x, y);
       }
       

       
}