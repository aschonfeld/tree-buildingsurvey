package tbs.view.dropdown;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public interface DropDownRowRenderer {

	public void renderRow(Object[] data, Rectangle row, Graphics2D g2);
}
