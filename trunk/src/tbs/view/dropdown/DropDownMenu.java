package tbs.view.dropdown;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import tbs.TBSGraphics;

public class DropDownMenu<T> {

	private List<T> elements;
	private T selection;
	private int y;
	private Rectangle selectionArea;
	private Rectangle fireDropDownButton;
	private Rectangle entriesArea;
	private boolean displayDropDownMenu = false;
	private DropDownRowRenderer rowRenderer;
	private boolean renderSelection;

	public DropDownMenu(Collection<T> elements, DropDownRowRenderer rowRenderer) {
		selection = null;
		this.elements = new LinkedList<T>();
		for (T element : elements)
			this.elements.add(element);
		selectionArea = new Rectangle();
		entriesArea = new Rectangle();
		this.rowRenderer = rowRenderer;
		renderSelection = true;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setRenderSelection(boolean renderSelection) {
		this.renderSelection = renderSelection;
	}

	public void clearSelection() {
		selection = null;
	}

	public void render(Graphics2D g2, int x, int width,
			Collection<String> labels) {
		int maxLabelWidth = TBSGraphics.get2DStringBounds(g2, labels).width;
		int selectWidth = TBSGraphics.getStringBounds(g2, "Select").width;
		if (selectWidth > maxLabelWidth)
			maxLabelWidth = selectWidth;
		int adjX = x
				+ ((width / 2) - ((maxLabelWidth
						+ (TBSGraphics.padding.width * 2)
						+ TBSGraphics.textHeight + TBSGraphics.padding.height) / 2));
		selectionArea = new Rectangle(adjX, y, maxLabelWidth
				+ (TBSGraphics.padding.width * 2), TBSGraphics.textHeight
				+ TBSGraphics.padding.height);
		fireDropDownButton = new Rectangle(adjX + maxLabelWidth
				+ (TBSGraphics.padding.width * 2), y, TBSGraphics.textHeight
				+ TBSGraphics.padding.height, TBSGraphics.textHeight
				+ TBSGraphics.padding.height);
		entriesArea = new Rectangle(adjX, y + TBSGraphics.textHeight
				+ TBSGraphics.padding.height, selectionArea.width
				+ fireDropDownButton.width, elements.size()
				* (TBSGraphics.textHeight + TBSGraphics.padding.height));
		g2.setColor(Color.WHITE);
		g2.fill(selectionArea);
		if (renderSelection && selection != null)
			rowRenderer
					.renderRow(new Object[] { selection }, selectionArea, g2);
		else
			TBSGraphics.drawCenteredString(g2, "Select", selectionArea.x,
					selectionArea.y, selectionArea.width, selectionArea.height);
		g2.setColor(Color.BLACK);
		g2.draw(selectionArea);

		TBSGraphics.renderButtonBackground(g2, fireDropDownButton, false);
		g2.setColor(Color.BLACK);
		g2.draw(fireDropDownButton);
		TBSGraphics.drawCenteredString(g2, "\u25BC", fireDropDownButton.x,
				fireDropDownButton.y, fireDropDownButton.width,
				fireDropDownButton.height);

		if (displayDropDownMenu) {
			g2.setColor(Color.WHITE);
			g2.fill(entriesArea);
			g2.setColor(Color.BLACK);
			g2.draw(entriesArea);
			Rectangle temp = new Rectangle(entriesArea.x, entriesArea.y,
					entriesArea.width, TBSGraphics.textHeight
							+ TBSGraphics.padding.height);
			for (T element : elements) {
				rowRenderer.renderRow(new Object[] { element }, temp, g2);
				temp.setLocation(temp.x, temp.y + temp.height);
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		if (displayDropDownMenu) {
			if (entriesArea.contains(e.getPoint())) {
				displayDropDownMenu = false;
				int index = (e.getY() - (entriesArea.y))
						/ (TBSGraphics.textHeight + TBSGraphics.padding.height);
				selection = elements.get(index);
			}
		}
		if (fireDropDownButton.contains(e.getPoint()))
			displayDropDownMenu = !displayDropDownMenu;
	}

	public boolean isMouseOver(MouseEvent e) {
		if (fireDropDownButton.contains(e.getPoint()))
			return true;
		else if (displayDropDownMenu)
			return entriesArea.contains(e.getPoint());
		return false;
	}

	public T getSelection() {
		return selection;
	}
}
