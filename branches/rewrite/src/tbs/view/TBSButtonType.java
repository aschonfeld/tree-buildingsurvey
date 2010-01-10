package tbs.view;

public enum TBSButtonType {

	SELECT("Select", true, true, false, false, false),
	ADD("Add", true, true, false, false, false),
	DELETE("Delete", false, false, false, false, false),
	LINK("Link", true, false, true, false, false),
	UNLINK("Unlink", true, false, false, false, true),
	LABEL("Label", true, false, true, false, true),
	PRINT("Print", false, true, false, false, false), 
	UNDO("Undo", false, false, false, true, false), 
	SAVE("Save", false, true, false, false, false),
	CLEAR("Clear", false, false, false, true, false),
	HELP("Help",false, true, false, false, false),
	TREE("Tree",false, true, false, false, false),
	OPEN_RESPONSE("Open Responses",false, true, false, false, false),
	ANALYSIS("Analysis",false, true, false, false, false);
	
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
	
	private Boolean confirmation;
	
	private Boolean cursorVariant;
	
	private TBSButtonType(String text, Boolean mode,
			Boolean activeWhenCreated, Boolean itemSelectionBased,
			Boolean confirmation, Boolean cursorVariant){
		this.text = text;
		this.mode = mode;
		this.activeWhenCreated = activeWhenCreated;
		this.itemSelectionBased = itemSelectionBased;
		this.confirmation = confirmation;
		this.cursorVariant = cursorVariant;
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
	
	public Boolean isConfirmation(){
		return confirmation;
	}
	
	public Boolean isCursorVariant(){
		return cursorVariant;
	}
	
	@Override
	public String toString(){
		return text;
	}
	
	public static TBSButtonType[] getButtons(boolean admin){
		TBSButtonType[] buttons = TBSButtonType.values();
		TBSButtonType[] buttonsSublist = admin ? new TBSButtonType[3] : new TBSButtonType[11];
		int index = 0;
		int startIndex = admin ? 11 : 0;
		int endIndex = admin ? buttons.length : 11;
		for(int i=startIndex;i<endIndex;i++){
				buttonsSublist[index] = buttons[i];
				index++;
		}
		return buttonsSublist;
	}
}
