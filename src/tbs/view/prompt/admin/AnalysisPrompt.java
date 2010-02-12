
package tbs.view.prompt.admin;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import tbs.model.TBSModel;
import tbs.view.prompt.Prompt;

public class AnalysisPrompt extends Prompt
{

	//Information to be used by all prompt types
	TBSModel model;
	
	String instrString;
	String welcomeMessage;

	public AnalysisPrompt(TBSModel model) {
		super(true, false, new Dimension(620,0), model);
		this.model = model;
	}


	public void keyPressed(KeyEvent e){}

	public void keyTyped(KeyEvent e){}

	public void mousePressed(MouseEvent e) {
		if(getCloseButton().contains(e.getPoint()))
				setFinished(true);		
	}

	public void paintComponent(Graphics2D g2) {
		setGraphics(g2);
		calculateValues(3, true, false);
		drawBox();
		drawCloseButton();
		drawHeader("Anaylsis - Currently Under Construction");
	}

	public boolean isOverButton(MouseEvent e){
		return getCloseButton().contains(e.getPoint());
	}
}

