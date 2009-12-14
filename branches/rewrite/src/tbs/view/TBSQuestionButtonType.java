package tbs.view;

public enum TBSQuestionButtonType {

	ONE("1","questionOne",false),
	TWO("2", "questionTwo",false),
	THREE("3", "questionThree.one",true);
	
	private String text;
	
	private String questionKey;
	
	private boolean radio;
	
	private TBSQuestionButtonType(String text, String questionKey, boolean radio){
		this.text = text;
		this.questionKey = questionKey;
		this.radio = radio;
	}
	
	public String getText(){
		return text;	
	}
	
	public String getQuestionKey(){
		return questionKey;	
	}
	
	public boolean isRadio(){
		return radio;
	}
	
	@Override
	public String toString(){
		return text;
	}
}
