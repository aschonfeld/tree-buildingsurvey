package tbs.view.prompt.buttons;

import java.util.LinkedList;
import java.util.List;

public enum OpenQuestionPromptButtonType {

	SUBMIT("Submit", false, "0"),
	STRONGLY_AGREE("Strongly Agree", true, "1"),
	AGREE("Agree", true, "2"),
	NOT_SURE("Not Sure", true, "3"),
	DISAGREE("Disagree", true, "4"),
	STRONGLY_DISAGREE("Strongly Disagree", true, "5");
	
	private String text;
	
	private boolean radio;
	
	private String value;
	
	private OpenQuestionPromptButtonType(String text, boolean radio, String value){
		this.text = text;
		this.radio = radio;
		this.value = value;
	}
	
	public String getText(){
		return text;	
	}
	
	public boolean isRadio(){
		return radio;	
	}
	
	public String getValue(){
		return value;
	}
	
	public String toString(){
		return text;
	}
	
	public static List<OpenQuestionPromptButtonType> getRadioButtons(){
		List<OpenQuestionPromptButtonType> buttons = new LinkedList<OpenQuestionPromptButtonType>();
		for(OpenQuestionPromptButtonType button : values()){
			if(button.radio)
				buttons.add(button);
		}
		return buttons;
	}
	
	public static List<OpenQuestionPromptButtonType> getWrittenButtons(){
		List<OpenQuestionPromptButtonType> buttons = new LinkedList<OpenQuestionPromptButtonType>();
		for(OpenQuestionPromptButtonType button : values()){
			if(!button.radio)
				buttons.add(button);
		}
		return buttons;
	}
	
	public static String getRadioText(String value){
		for(OpenQuestionPromptButtonType button : values()){
			if(button.getValue().equals(value))
				return button.toString();
		}
		return "";
	}
}
