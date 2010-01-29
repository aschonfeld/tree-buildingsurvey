//TBS version 0.4
//Model: creates and maintains the logical structure underlying TBS

package tbs.model;

import java.util.List;
import java.util.Properties;

import javax.swing.JComponent;

import tbs.TBSApplet;
import tbs.controller.TBSController;
import tbs.model.admin.Student;
import tbs.properties.PropertyType;
import tbs.view.TBSButtonType;
import tbs.view.TextEntryBox;
import tbs.view.prompt.Prompt;


public interface TBSModel 
{
	JComponent getView();
	
	TBSController getController();
	
	TBSApplet getApplet();
	
	List<TBSButtonType> getButtons();
	
	Boolean isButtonActive(TBSButtonType b);
	
	List<ModelElement> getElements();
	
	List<Node> inTreeElements();
	
	Prompt getPrompt();
	
	void clearPrompt();
	
	TextEntryBox getTextEntryBox();
	
	Student getStudent();
	
	String exportTree();
	
	Properties getProperties(PropertyType pt);
}
