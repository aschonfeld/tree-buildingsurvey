//TBSController: mousehandling and button handling for TBS

package tbs.controller;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import tbs.TBSGraphics;
import tbs.model.ModelElement;
import tbs.model.Node;
import tbs.model.TBSModel;
import tbs.view.TBSButtonType;
import tbs.view.TBSView;

/**
 * TBSController contains the methods used to manipulate the information
 * contained in the data model. There are two variations of the TBSController.
 * The StudentController is used when a student is taking the Tree-Building
 * Survey, and contains methods used in constructing a tree and answering the
 * survey questions. The AdminController is used in examining students' work and
 * viewing statistical patterns in the trees produced.
 **/
public abstract class TBSController implements MouseListener,
		MouseMotionListener, KeyListener, ComponentListener {
	private TBSModel model;
	private TBSView view;
	private TBSButtonType buttonClicked = TBSButtonType.SELECT;

	public TBSController(TBSModel model, TBSView inputView,
			TBSButtonType defaultButton) {
		this.model = model;
		this.view = inputView;
		this.view.getVerticalBar().addAdjustmentListener(
				new AdjustmentListener() {
					public void adjustmentValueChanged(AdjustmentEvent e) {
						view
								.setYOffset((e.getValue() * view.getHeight()) / 100);
					}
				});
		this.view.getHorizontalBar().addAdjustmentListener(
				new AdjustmentListener() {
					public void adjustmentValueChanged(AdjustmentEvent e) {
						view
								.setXOffset((e.getValue() * view.getHeight()) / 100);
					}
				});
		buttonClicked = defaultButton;
	}

	/**
	 * Returns the identity of the node at (x,y).
	 */
	public Node elementMouseIsHoveringOver(int x, int y) {
		int yOffset = 0;
		int xOffset = 0;
		if (x > TBSGraphics.LINE_OF_DEATH) {
			yOffset = view.getYOffset();
			xOffset = view.getXOffset();
		}
		for (ModelElement me : model.inTreeElements()) {
			if (me instanceof Node && me.contains(x + xOffset, y + yOffset))
				return (Node) me;
		}
		return null;
	}

	public int indexMouseIsOver(int x, int y) {
		int maxIndex = -1;
		int i = 0;
		int yOffset = 0;
		if (x > TBSGraphics.LINE_OF_DEATH)
			yOffset = view.getYOffset();
		for (ModelElement me : model.getElements()) {
			if (me.contains(x, y + yOffset))
				maxIndex = i;
			i++;
		}
		return maxIndex;
	}

	/**
	 * elementMouseIsOver returns the {@link ModelElement} containing the
	 * current mouse location.
	 */
	public ModelElement elementMouseIsOver(int x, int y) {
		int index = indexMouseIsOver(x, y);
		if (index >= 0)
			return model.getElement(index);
		return null;
	}

	/**
	 * getButtonClicked returns the {@link TBSButtonType} of the button selected
	 * by the user.
	 */
	public TBSButtonType getButtonClicked() {
		return buttonClicked;
	}

	public void setButtonClicked(TBSButtonType buttonClicked) {
		this.buttonClicked = buttonClicked;
	}

	/**
	 * NOT YET DOCUMENTED
	 */
	public abstract void handleMouseButtonPressed(int x, int y);

	/**
	 * NOT YET DOCUMENTED
	 */
	public abstract void handleMousePressed(int x, int y);
}
