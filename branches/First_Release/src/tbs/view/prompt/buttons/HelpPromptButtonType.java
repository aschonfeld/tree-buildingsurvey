package tbs.view.prompt.buttons;


public enum HelpPromptButtonType {

	INTRODUCTION("Introduction"),
	INSTRUCTIONS("Instructions"),
	BUTTON_INFO("Button Info"),
	SURVEY_STATUS("Survey Status");
	
	private String text;
	
	private HelpPromptButtonType(String text){
		this.text = text;
	}
	
	public String getText(){
		return text;	
	}
	
	public String toString(){
		return text;
	}
}
