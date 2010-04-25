package tbs.view.dropdown;


public enum SubDropDownType {

	HULL(true, 5),COLLISION(false, 6),OPTIMAL_HULL(false, 7);
	
	private boolean viewMultiple;
	private int dropDownIndex;
	
	private SubDropDownType(boolean viewMultiple, int dropDownIndex){
		this.viewMultiple = viewMultiple;
		this.dropDownIndex = dropDownIndex;
	}
	
	public boolean getViewMultiple(){
		return viewMultiple;	
	}
	
	public int getDropDownIndex(){
		return dropDownIndex;	
	}
}
