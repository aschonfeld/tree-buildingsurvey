package tbs.view;

public enum TBSButtonType {

	SELECT("Select", true),
	ADD("Add", true),
	DELETE("Delete", false),
	LINK("Link", true),
	UNLINK("Unlink", true),
	LABEL("Label", true),
	PRINT("Print", false), 
	UNDO("Undo", false), 
	SAVE("Save", false),
	CLEAR("Clear", false),
	HELP("Help",false);
	
	private String text;
	
	/**
	 * If this value is True after the user clicks this button once
	 * the button stays selected, otherwise it automatically rolls back
	 * to the SELECT button being selected.
	 */
	
	private Boolean isMode;
	
	private TBSButtonType(String text, Boolean isMode){
		this.text = text;
		this.isMode = isMode;
	}
	
	public String getText(){
		return text;	
	}
	
	public Boolean getIsMode(){
		return isMode;
	}
	
	@Override
	public String toString(){
		return text;
	}
}
