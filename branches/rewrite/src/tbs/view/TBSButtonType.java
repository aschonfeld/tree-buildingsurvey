package tbs.view;

public enum TBSButtonType {

	SELECT("Select", true, true, false),
	ADD("Add", true, true, false),
	DELETE("Delete", false, false, false),
	LINK("Link", true, false, true),
	UNLINK("Unlink", true, false, false),
	LABEL("Label", true, false, false),
	PRINT("Print", false, true, false), 
	UNDO("Undo", false, false, false), 
	SAVE("Save", false, true, false),
	CLEAR("Clear", false, false, false),
	HELP("Help",false, true, false);
	
	private String text;
	
	/**
	 * If this value is True after the user clicks this button once
	 * the button stays selected, otherwise it automatically rolls back
	 * to the SELECT button being selected.
	 */
	private Boolean mode;
	
	/**
	 * If this value is True then the text within the button will not be grayed
	 * out when the View is first created (These actions will do nothing in 
	 * starting state of tree).
	 */
	private Boolean activeWhenCreated;
	
	private Boolean itemSelectionBased;
	
	private TBSButtonType(String text, Boolean mode,
			Boolean activeWhenCreated, Boolean itemSelectionBased){
		this.text = text;
		this.mode = mode;
		this.activeWhenCreated = activeWhenCreated;
		this.itemSelectionBased = itemSelectionBased;
	}
	
	public String getText(){
		return text;	
	}
	
	public Boolean isMode(){
		return mode;
	}
	
	public Boolean isActiveWhenCreated(){
		return activeWhenCreated;
	}
	
	public Boolean isItemSelectionBased(){
		return itemSelectionBased;
	}
	
	@Override
	public String toString(){
		return text;
	}
}
