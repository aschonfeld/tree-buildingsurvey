package tbs.view;

public enum TBSButtonType {

	SELECT("Select"),
	ADD("Add"),
	DELETE("Delete"),
	LINK("Link"),
	UNLINK("Unlink"),
	LABEL("Label"),
	PRINT("Print"), 
	UNDO("Undo"), 
	SAVE("Save"),
	CLEAR("Clear");
	
	private String text;
	
	private TBSButtonType(String text){
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
	
	@Override
	public String toString(){
		return text;
	}
}
