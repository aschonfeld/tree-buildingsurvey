package tbs.view.dropdown;

public enum SubDropDownType {

	HULL(true), COLLISION(false), OPTIMAL_HULL(false);

	private boolean viewMultiple;
	
	private SubDropDownType(boolean viewMultiple) {
		this.viewMultiple = viewMultiple;
	}

	public boolean getViewMultiple() {
		return viewMultiple;
	}
}
