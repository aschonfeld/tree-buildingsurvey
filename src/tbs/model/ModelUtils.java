//TBS version 0.4
//TBS utils: utility functions of TBS

package tbs.model;

import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tbs.TBSGraphics;
import tbs.model.history.Add;
import tbs.model.history.Delete;
import tbs.model.history.Link;
import tbs.model.history.Unlink;
import tbs.view.TBSButtonType;


public class ModelUtils {

  public static void addNode(Node n, TBSModel model, boolean undo)
  {
    if (n instanceof EmptyNode)//Immortal Empty Node
      addEmptyNode((EmptyNode) n, model, undo);
    else
      addStaticNode((OrganismNode) n, model, undo);
    model.setElementsInTree(true);
    if(model instanceof StudentModel){
    	Map<TBSButtonType, Boolean> buttonStates = ((StudentModel) model).getButtonStates();
    	buttonStates.put(TBSButtonType.DELETE, true);
    	if(model.inTreeElements().size() > 1)
    		buttonStates.put(TBSButtonType.LINK, true);
    	buttonStates.put(TBSButtonType.CLEAR, true);
    }
  }
  
  public static void addEmptyNode(EmptyNode en, TBSModel model, boolean undo){
	  EmptyNode newEN;
	  if(!en.isInTree()){
		  newEN = new EmptyNode(model.getSerial());
		  newEN.setAnchorPoint(en.getAnchorPoint());
		  newEN.setInTree(true);
		  en.setAnchorPoint(new Point(TBSGraphics.emptyNodeLeftX, TBSGraphics.emptyNodeUpperY));
	  }else
		  newEN = en;
	  model.addElement(newEN);
	  model.setEmptyNodesInTree(true);
	  if(model instanceof StudentModel && !undo){
		  try{
			  ((StudentModel) model).addActionToHistory(new Add((EmptyNode) newEN.clone(), (StudentModel) model)); 
		  }catch(CloneNotSupportedException c){
			  System.out.println("Unable to add action to history.");
		  }
		  ((StudentModel) model).getButtonStates().put(TBSButtonType.LABEL, true);
	  }
  }
  
  public static void addStaticNode(OrganismNode on, TBSModel model, boolean undo){
    on.setInTree(true);
    if(model instanceof StudentModel  && !undo){
      try{
       ((StudentModel) model).addActionToHistory(new Add((OrganismNode) on.clone(), (StudentModel) model)); 
      }catch(CloneNotSupportedException c){
        System.out.println("Unable to add action to history.");
      }
    }
  }
  
  public static void addConnection(Node from, Node to, TBSModel model, boolean undo){
    addConnection(from, to, -1, model, undo);
  }
  
  public static void addConnection(Node from, Node to, int id, TBSModel model, boolean undo)
  {
    Connection newConn = new Connection(id == -1 ? model.getSerial() : id, from, to);
    if(id == -1)
      model.addElement(newConn);
    else{
      if(id < model.elementCount())
        model.addElement(id, newConn);
      else{
        model.addElement(newConn);
        Collections.sort(model.getElements(), TBSGraphics.elementIdComparator);
      }
    }
    model.setConnectionsInTree(true);
    if(model instanceof StudentModel && !undo){
      try{
        ((StudentModel) model).addActionToHistory(new Link((Connection) newConn.clone()));
      }catch(CloneNotSupportedException c){
        System.out.println("Unable to add action to history.");
      }
      ((StudentModel) model).getButtonStates().put(TBSButtonType.UNLINK, true);
    }
  }
  
  public static void removeElement(ModelElement element, TBSModel model, boolean undo){
	  if(model instanceof StudentModel && !undo){
		  try {
			  ((StudentModel) model).addActionToHistory(new Delete(element, (StudentModel) model));
		  } catch (CloneNotSupportedException e) {
			  System.out.println("Unable to add action to history.");
		  }
	  }

	  if(element instanceof Node)
		  removeNode((Node) element, model);
	  else{
		  Connection c = (Connection) element;
		  removeConnections(getConnectionsByNodes(c.getFrom(), c.getTo(), model), model);
	  }

	  if(model instanceof StudentModel)
		  ((StudentModel) model).updateButtonStatesAfterRemove();
  }
  
  public static void removeNode(Node n, TBSModel model)
  {
    if(n instanceof EmptyNode)
      removeEmptyNode((EmptyNode) n, model);
    else
      removeStaticNode((OrganismNode) n, model);
  }
  
  public static void removeStaticNode(OrganismNode on, TBSModel model){
    unlinkNode(on, model);
    on.reset();
  }
  
  public static void removeEmptyNode(EmptyNode en, TBSModel model){
    if(!en.isInTree()){
      en.setAnchorPoint(new Point(TBSGraphics.emptyNodeLeftX, TBSGraphics.emptyNodeUpperY));
    }else{
      unlinkNode(en, model);
      model.removeElement(en);
    }
  }
  
  public static void removeConnection(Connection c, TBSModel model){
    c.getFrom().getConnectedTo().remove(c.getTo());
    c.getTo().getConnectedFrom().remove(c.getFrom());
    model.removeElement(c);
  }
  
  public static void removeConnections(List<Connection> connections, TBSModel model){
    for(Connection c : connections)
      removeConnection(c, model);
  }
  
  public static void unlinkElement(ModelElement m, TBSModel model){
	  if(model instanceof StudentModel)
		  ((StudentModel) model).addActionToHistory(new Unlink(m, (StudentModel) model));
	  
	  if(m instanceof Node)		  
		  unlinkNode((Node) m, model);
	  else
		  unlinkConnection((Connection) m, model);
	  
	  if(model instanceof StudentModel)
		  ((StudentModel) model).updateButtonStatesAfterRemove();
  }
  
  public static void unlinkNode(Node n, TBSModel model){
	  removeConnections(getConnectionsByNode(n, model), model);
  }

  public static void unlinkConnection(Connection c, TBSModel model){
	  removeConnections(getConnectionsByNodes(c.getFrom(), c.getTo(),model), model);
  }  
  
  public static List<Connection> getConnectionsByNode(Node n, TBSModel model){
    List<Connection> connections = new LinkedList<Connection>();
    Connection c;
    for (ModelElement me: model.getElements())
    {
      if(me instanceof Connection){
        c = (Connection) me;
        if(c.hasNode(n))
          connections.add(c);
      }
    }
    return connections;
  }
  
  public static List<Connection> getConnectionsByNodes(Node n1, Node n2, TBSModel model){
	  List<Connection> connections = new LinkedList<Connection>();
	  Connection c;
	  for (ModelElement me: model.getElements())
	  {
		  if(me instanceof Connection){
			  c = (Connection) me;
			  if(c.hasNode(n1) && c.hasNode(n2))
				  connections.add(c);
		  }
	  }
	  return connections;
  }
}
