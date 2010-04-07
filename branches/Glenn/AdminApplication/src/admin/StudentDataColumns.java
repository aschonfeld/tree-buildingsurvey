package admin;
import java.awt.Color;
import java.util.ArrayList;


public class StudentDataColumns {
	
    public ArrayList<ColumnDataHandler> columnDataHandlers;
    
	StudentDataColumns() {
    	columnDataHandlers = new ArrayList<ColumnDataHandler>();
        columnDataHandlers.add(new Student_Name());
        columnDataHandlers.add(new Graph_Type());
        columnDataHandlers.add(new Has_Branches());
        columnDataHandlers.add(new Groups_Are_Labelled());
        columnDataHandlers.add(new All_Organisms_Terminal());
        columnDataHandlers.add(new Includes_All_Organisms());
        columnDataHandlers.add(new Has_Hull_Collisions());
        columnDataHandlers.add(new Has_Single_Common_Ancestor());
        columnDataHandlers.add(new Grouping_Invertebrates());
        columnDataHandlers.add(new Grouping_Vertebrates());
        columnDataHandlers.add(new Grouping_Mammals());
        columnDataHandlers.add(new Grouping_Nonmammals());
	}
	
    abstract class ColumnDataHandler {
    	
    	private boolean visible = true;
    	
    	public abstract Object getData(Graph graph);
    	
    	public String getName() {
    		return this.getClass().getSimpleName().replace('_', ' ');
    	}
    	public boolean isVisible() {
    		if(isAlwaysVisible()) return true;
    		return visible;
    	}
    	public boolean isAlwaysVisible() {
    		if (this.getClass().getSimpleName().equals("Student_Name")) return true;
    		return false;
    	}
    	   	
    	public void toggleVisible() {
    		if(isAlwaysVisible()) return;
    		this.visible = !visible;
    	}
    	
    	public Color getBackgroundColor(Object data) {
    		if (this.getClass().getSimpleName().contains("Grouping")) {
    			Float fData = (Float) data;
    			float value = fData.floatValue();
    			if(value <= 1.0) return Color.red;
    			if(value <= 1.25) return Color.yellow;
    			return Color.green;
    		}
    		return Color.white;
    	}
    }
    
    class Student_Name extends ColumnDataHandler {
    	public Object getData(Graph graph) {return graph.getStudentName();}
    }
    class Graph_Type extends ColumnDataHandler {
    	public Object getData(Graph graph) {return graph.getType();}
    }
    class Has_Branches extends ColumnDataHandler {
    	public Object getData(Graph graph) {return graph.hasBranches();}
    }
    class Groups_Are_Labelled extends ColumnDataHandler {
		public Object getData(Graph graph) {return graph.groupsAreLabelled();}
    }
    class All_Organisms_Terminal extends ColumnDataHandler {
    	public Object getData(Graph graph) {return graph.allOrganismsTerminal();}
    }
    class Includes_All_Organisms extends ColumnDataHandler {
    	public Object getData(Graph graph) {return graph.includesAllOrganisms();}
    }
    class Has_Hull_Collisions extends ColumnDataHandler {
    	public Object getData(Graph graph) {return graph.getHasHullCollisions();}
    }
    class Has_Single_Common_Ancestor extends ColumnDataHandler {
    	public Object getData(Graph graph) {return graph.hasSingleCommonAncestor();}
    }
    class Grouping_Invertebrates extends ColumnDataHandler {
    	public Object getData(Graph graph) {return graph.groupingInvertebrates();}
    }
    class Grouping_Vertebrates extends ColumnDataHandler {
    	public Object getData(Graph graph) {return graph.groupingVertebrates();}
    }
    class Grouping_Mammals extends ColumnDataHandler {
    	public Object getData(Graph graph) {return graph.groupingMammals();}
    }
    class Grouping_Nonmammals extends ColumnDataHandler {
    	public Object getData(Graph graph) {return graph.groupingNonmammals();}
    }  

}