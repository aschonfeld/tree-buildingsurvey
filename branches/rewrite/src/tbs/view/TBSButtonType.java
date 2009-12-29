package tbs.view;

public enum TBSButtonType {

	SELECT("Select", true, true),
	ADD("Add", true, true),
	DELETE("Delete", false, false),
	LINK("Link", true, false),
	UNLINK("Unlink", true, false),
	LABEL("Label", true, false),
	PRINT("Print", false, true), 
	UNDO("Undo", false, false), 
	SAVE("Save", false, true),
	CLEAR("Clear", false, false),
	HELP("Help",false, true);
	
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
	
	private TBSButtonType(String text, Boolean mode, Boolean activeWhenCreated){
		this.text = text;
		this.mode = mode;
		this.activeWhenCreated = activeWhenCreated;
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
	
	@Override
	public String toString(){
		return text;
	}
}
