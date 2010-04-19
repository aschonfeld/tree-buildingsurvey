package tbs.view.prompt.buttons;

public enum YesNoButtonType {

	YES("Yes",true),
	NO("No",false);
	
	private String text;
	
	private boolean value;
	
	private YesNoButtonType(String text, boolean value){
		this.text = text;
		this.value = value;
	}
	
	public String getText(){
		return text;	
	}
	
	public boolean getValue(){
		return value;
	}
	
	public String toString(){
		return text;
	}
}
