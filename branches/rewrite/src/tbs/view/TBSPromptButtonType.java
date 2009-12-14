package tbs.view;

import java.util.LinkedList;
import java.util.List;

public enum TBSPromptButtonType {

	SUBMIT("Submit", false, 0),
	STRONGLY_AGREE("Strongly Agree", true, 1),
	AGREE("Agree", true, 2),
	NOT_SURE("Not Sure", true, 3),
	DISAGREE("Disagree", true, 4),
	STRONGLY_DISAGREE("Strongly Disagree", true, 5);
	
	private String text;
	
	private boolean radio;
	
	private int value;
	
	private TBSPromptButtonType(String text, boolean radio, int value){
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
	
	public int getValue(){
		return value;
	}
	
	@Override
	public String toString(){
		return text;
	}
	
	public static List<TBSPromptButtonType> getButtons(boolean isRadio){
		List<TBSPromptButtonType> buttons = new LinkedList<TBSPromptButtonType>();
		for(TBSPromptButtonType button : values()){
			if(button.radio == isRadio)
				buttons.add(button);
		}
		return buttons;
	}
	
	public static String getRadioText(int value){
		for(TBSPromptButtonType button : values()){
			if(button.getValue() == value)
				return button.toString();
		}
		return "";
	}
}
