package tbs.view.dropdown;

import java.awt.Graphics2D;

import tbs.model.AdminModel;

public abstract class SubDropDown {

	private boolean display;
	
	public SubDropDown(){
		display = false;
	}
	
	public Boolean getDisplay() {return display;}
	public void setDisplay(Boolean display) {this.display = display;}
	public void toggleDisplay(){this.display = !display;}
	public abstract void render(Graphics2D g2, int xOffset, int yOffset, AdminModel model);
	public abstract String toString();
}
