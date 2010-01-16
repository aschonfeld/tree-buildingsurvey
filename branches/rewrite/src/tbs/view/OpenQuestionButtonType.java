package tbs.view;

public enum OpenQuestionButtonType {

	ONE("1", "Question 1", "questionOne", false),
	TWO("2", "Question 2", "questionTwo", false),
	THREE("3", "Question 3", "questionThree",true);
	
	private String text;
	
	private String adminText;
	
	private String questionKey;
	
	private boolean radio;
	
	private OpenQuestionButtonType(String text, String adminText,
			String questionKey, boolean radio){
		this.text = text;
		this.adminText = adminText;
		this.questionKey = questionKey;
		this.radio = radio;
	}
	
	public String getText(){
		return text;	
	}
	
	public String getAdminText(){
		return adminText;	
	}
	
	public String getQuestionKey(){
		return questionKey;	
	}
	
	public boolean isRadio(){
		return radio;
	}
	
	public String toString(){
		return text;
	}
}
