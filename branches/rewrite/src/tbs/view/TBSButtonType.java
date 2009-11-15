package tbs.view;

public enum TBSButtonType {
	CONNECT("connect"), DELETE("delete"), PRINT("print"), UNDO("undo"), SAVE("save");
	
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
