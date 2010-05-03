package admin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.TextLayout;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.BreakIterator;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class Common {

	public static boolean screenPrintMode = false;

	public static Color backgroundColor = Color.BLACK;
	public static Color emptyNodeNameColor = Color.BLACK;
	public static Color studentNameColor = Color.WHITE;
	public static Color organismStringColor = Color.BLACK;
	public static Color organismBoxColor = Color.WHITE;
	public static Color connectionColor = new Color(0.5f, 1.0f, 0.5f);
	public static Color hullColor = Color.GREEN;
	public static Color emptyNodeColor = new Color(0.5f, 0.5f, 1.0f);
	public static Color[] defualtGroupColors = new Color[] { Color.MAGENTA,
			Color.ORANGE, Color.PINK, new Color(0, 100, 0), Color.BLUE,
			Color.CYAN, Color.GREEN, Color.LIGHT_GRAY, Color.YELLOW,
			Color.WHITE };

	public static Dimension padding = new Dimension(10, 5);

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

	public static Font tooltipFont = new Font("default", Font.PLAIN, 12);
	public static Color tooltipColor = Color.CYAN;

	public static String[] questions = new String[] {
			new String(
					"Explain in words how you went about organizing these organisms. "
							+ "Use one or two specific examples and describe why you put them where you did."),
			new String(
					"How did you decide if organisms were closely related to one another or not closely related? "
							+ "Use one or two specific examples from your work to explain your reasoning.") };

	/**
	 * Returns the @Rectangle2D surrounding a piece of text
	 */
	public static Dimension getStringBounds(Graphics2D g2, String s) {
		if (isStringEmpty(s))
			return new Dimension();
		TextLayout layout = new TextLayout(s, g2.getFont(), g2
				.getFontRenderContext());
		Rectangle2D bounds = layout.getBounds();
		return new Dimension((int) bounds.getWidth(), (int) bounds.getHeight());
	}

	public static boolean isStringEmpty(String s) {
		return (s == null || s.length() == 0);
	}

	public static Dimension get2DStringBounds(Graphics2D g2,
			Collection<?> strings) {
		Point max = new Point(0, 0);
		for (Object s : strings) {
			Dimension bounds = getStringBounds(g2, s.toString());
			if (bounds.width > max.x)
				max.x = (int) bounds.getWidth();
			if (bounds.height > max.y)
				max.y = (int) bounds.getHeight();
		}
		return new Dimension(max.x, max.y);
	}

	public static Dimension get2DImageBounds(Graphics2D g2,
			Collection<BufferedImage> images) {
		Point max = new Point(0, 0);
		for (BufferedImage i : images) {
			if (i.getWidth() > max.x)
				max.x = i.getWidth();
			if (i.getHeight() > max.y)
				max.y = i.getHeight();
		}
		return new Dimension(max.x, max.y);
	}

	public static void drawCenteredString(Graphics2D g2, String s, int leftX,
			int upperY, int width, int height) {
		drawCenteredString(g2, s, leftX, upperY, width, height, Color.black);
	}

	/**
	 * Paints a string centered in the rectangle defined.
	 */
	public static void drawCenteredString(Graphics2D g2, String s, int leftX,
			int upperY, int width, int height, Color c) {
		drawCenteredString(g2, s, leftX, upperY, width, height, c, font);
	}

	public static void drawCenteredString(Graphics2D g2, String s, int leftX,
			int upperY, int width, int height, Color c, Font f) {
		if (isStringEmpty(s))
			return;
		g2.setColor(c);
		TextLayout layout = new TextLayout(s, g2.getFont(), g2
				.getFontRenderContext());
		Rectangle2D bounds = layout.getBounds();
		int stringHeight = (int) bounds.getHeight();
		int stringWidth = (int) bounds.getWidth();
		float x, y;
		if (width == 0)
			x = leftX;
		else
			x = leftX + (width - stringWidth) / 2;
		if (height == 0)
			y = upperY;
		else
			y = upperY + height - (height - stringHeight) / 2;
		// if width or height is 0, do not center along that axis
		layout.draw(g2, x, y);
	}

	public static List<String> breakStringByLineWidth(Graphics2D g2, String s,
			int width) {
		String currentLine = "";
		List<String> widthBrokenString = new LinkedList<String>();
		if (isStringEmpty(s)) {
			widthBrokenString.add("");
			return widthBrokenString;
		}
		for (String token : s.split(" ")) {
			if (getStringBounds(g2, currentLine + token).width > width) {
				widthBrokenString.add(currentLine);
				currentLine = token + " ";
			} else {
				currentLine += token + " ";
			}
		}
		if (currentLine.length() > 0)
			widthBrokenString.add(currentLine);
		return widthBrokenString;
	}

	public static List<HullCollision> hullCollisions(int level, List<ConvexHull> hulls) {
		List<HullCollision> collisions = new LinkedList<HullCollision>();
		if(hulls.size() > 1 && collide(hulls))
			collisions.add(new HullCollision(level, hulls));
		return collisions;
	}

	public static boolean collide(List<ConvexHull> hulls) {
		Set<Set<Integer>> indexSubGroups = SubGroupGenerator.getIndexSubGroups(hulls.size());
		for(Set<Integer> subGroup : indexSubGroups){
			List<Integer> indexes = new LinkedList<Integer>();
			indexes.addAll(subGroup);
			Area intersect = new Area(hulls.get(indexes.get(0)).getHullShape());
			for (int i = 1; i < hulls.size(); i++)
				intersect.intersect(new Area(hulls.get(i).getHullShape()));
			if(!intersect.isEmpty())
				return true;
		}
		return false;
	}
	
	public static boolean isPasswordCorrect(char[] input) {
		boolean isCorrect = true;
		char[] correctPassword = { 'l', 'a', 'b', '0', '9', 'a', 'c', 'c', 'e',
				'5', '5' };

		if (input.length != correctPassword.length) {
			isCorrect = false;
		} else {
			isCorrect = Arrays.equals(input, correctPassword);
		}

		// Zero out the password.
		Arrays.fill(correctPassword, '0');

		return isCorrect;
	}

	public static String commaSeparatedString(Collection<?> strings) {
		StringBuilder buff = new StringBuilder();
		String sep = "";
		for (Object str : strings) {
			buff.append(sep);
			buff.append(str.toString());
			sep = ", ";
		}
		return buff.toString();
	}

	public static String wrapText(JLabel label, int width, String[] text) {
		// measure the length of font in pixel
		FontMetrics fm = label.getFontMetrics(label.getFont());

		// to find the word separation
		BreakIterator boundary = BreakIterator.getWordInstance();
		// main string to be added
		StringBuffer m = new StringBuffer("<html>");
		// loop each index of array
		for (String str : text) {
			boundary.setText(str);
			// save each line
			StringBuffer line = new StringBuffer();
			// save each paragraph
			StringBuffer par = new StringBuffer();
			int start = boundary.first();
			// wrap loop
			for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary
					.next()) {
				String word = str.substring(start, end);
				line.append(word);
				// compare width with font metrics
				int trialWidth = SwingUtilities.computeStringWidth(fm, line
						.toString());
				// if bigger, add new line
				if (trialWidth > width) {
					line = new StringBuffer(word);
					par.append("<br />");
				}
				// add new word to paragraphs
				par.append(word);
			}
			// add new line each paragraph
			par.append("<br />");
			// add paragraph into main string
			m.append(par);
		}
		// closed tag
		m.append("</html>");

		return m.toString();
	}

	public static void setColorsForPrinting() {
		backgroundColor = Color.WHITE;
		studentNameColor = Color.BLACK;
		connectionColor = Color.BLACK;
		emptyNodeColor = Color.BLACK;
		tooltipColor = Color.BLACK;
		hullColor = Color.BLACK;
		screenPrintMode = true;
	}

	public static void setColorsForDisplay() {
		backgroundColor = Color.BLACK;
		studentNameColor = Color.WHITE;
		connectionColor = new Color(0.5f, 1.0f, 0.5f);
		emptyNodeColor = new Color(0.5f, 0.5f, 1.0f);
		tooltipColor = Color.CYAN;
		hullColor = Color.GREEN;
		screenPrintMode = false;
	}
}