package tbs.view;

import java.util.LinkedList;
import java.util.List;

public enum TBSButtonType {

	SELECT("Select", true, true, false, false, false, false),
	ADD("Add", true, true, false, false, false, false),
	DELETE("Delete", true, false, true, false, false, false),
	LINK("Link", true, false, true, false, true, false),
	UNLINK("Unlink", true, false, true, false, true, false),
	LABEL("Label", true, false, true, false, true, false),
	PRINT("Print", false, true, false, false, false, false), 
	UNDO("Undo", false, false, false, true, false, false), 
	/*
	 * For now we will eliminate the use of the "SAVE" button until we
	 * can have some certainty that scripting within a Mac will work.
	 * Javascript operations will be handled by the web-form.
	 */
	//SAVE("Save", false, true, false, false, false, false),
	CLEAR("Clear", false, false, false, true, false, false),
	HELP("Help",false, true, false, false, false, false),
	TREE("Tree",false, true, false, false, false, true),
	OPEN_RESPONSE("Open Responses",false, true, false, false, false, true),
	ANALYSIS("Analysis",false, true, false, false, false, true);
	
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
	
	private Boolean admin;
	
	private TBSButtonType(String text, Boolean mode,
			Boolean activeWhenCreated, Boolean itemSelectionBased,
			Boolean confirmation, Boolean cursorVariant,
			Boolean admin){
		this.text = text;
		this.mode = mode;
		this.activeWhenCreated = activeWhenCreated;
		this.itemSelectionBased = itemSelectionBased;
		this.confirmation = confirmation;
		this.cursorVariant = cursorVariant;
		this.admin = admin;
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
	
	public Boolean isAdmin(){
		return admin;
	}
	
	public String toString(){
		return text;
	}
	
	public static List<TBSButtonType> getButtons(boolean admin){
		TBSButtonType[] buttons = TBSButtonType.values();
		List<TBSButtonType> buttonsSublist = new LinkedList<TBSButtonType>();
		for(TBSButtonType button : buttons){
			if(button.admin == admin)
				buttonsSublist.add(button);
		}
		return buttonsSublist;
	}
}
