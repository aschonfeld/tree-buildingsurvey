//TBS version 0.4
//Model: creates and maintains the logical structure underlying TBS

package tbs.model;

import java.util.List;
import java.util.Properties;

import tbs.TBSApplet;
import tbs.controller.TBSController;
import tbs.properties.PropertyType;
import tbs.view.OpenQuestionButtonType;
import tbs.view.TBSButtonType;
import tbs.view.TBSView;
import tbs.view.TextEntryBox;
import tbs.view.prompt.Prompt;


public interface TBSModel 
{
	TBSView getView();
	
	TBSController getController();
	
	TBSApplet getApplet();
	
	Boolean isAdmin();
	
	TBSButtonType[] getButtons();
	
	Boolean isButtonActive(TBSButtonType b);
	
	ModelElement getImmortalEmptyNode();
	
	List<ModelElement> getElements();
	
	Boolean hasArrows();
	
	Prompt getPrompt();
	
	void clearPrompt();
	
	TextEntryBox getTextEntryBox();
	
	String getQuestion(OpenQuestionButtonType b);
	
	void setQuestion(String s, OpenQuestionButtonType b);
	
	String exportTree();
	
	Properties getProperties(PropertyType pt);
}
