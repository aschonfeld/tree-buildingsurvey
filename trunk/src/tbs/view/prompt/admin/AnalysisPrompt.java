package tbs.view.prompt.admin;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import tbs.TBSGraphics;
import tbs.TBSUtils;
import tbs.model.AdminModel;
import tbs.view.prompt.Prompt;

public class AnalysisPrompt extends Prompt {

	// Information to be used by all prompt types
	AdminModel model;
	List<String> analysisText;

	public AnalysisPrompt(AdminModel model) {
		super(true, false, new Dimension(620, 0), model);
		this.model = model;
		analysisText = new LinkedList<String>();
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (getCloseButton().contains(e.getPoint()))
			setFinished(true);
	}

	public void reset() {
		analysisText = new LinkedList<String>();
	}

	public void paintComponent(Graphics2D g2) {
		setGraphics(g2);
		if (analysisText.isEmpty()) {
			List<String> collisonText = TBSUtils.collisonText(model);
			analysisText.addAll(TBSGraphics.breakStringByLineWidth(g2,
					"1) All organism nodes terminal: "
							+ (model.getGraph().allOrganismsTerminal() ? "Yes"
									: "No"), getWidth()
							- TBSGraphics.padding.width * 2));
			analysisText.addAll(TBSGraphics.breakStringByLineWidth(g2,
					"2) All organism nodes included: "
							+ (model.outOfTreeElements().isEmpty() ? "Yes"
									: "No"), getWidth()
							- TBSGraphics.padding.width * 2));
			if (collisonText.isEmpty()) {
				analysisText
						.addAll(TBSGraphics
								.breakStringByLineWidth(
										g2,
										"3) There are no collisions between organism groups(Mammal, Invert, NMV)",
										getWidth() - TBSGraphics.padding.width
												* 2));
			} else {
				analysisText
						.addAll(TBSGraphics
								.breakStringByLineWidth(
										g2,
										"3) There were the following collisions between organism groups:",
										getWidth() - TBSGraphics.padding.width
												* 2));
				analysisText.addAll(collisonText);
			}

		}
		calculateValues(analysisText.size() + 1, false);
		drawBox();
		drawHeader("Tree Anaylsis");
		incrementStringY();
		drawText(analysisText);
	}
}
