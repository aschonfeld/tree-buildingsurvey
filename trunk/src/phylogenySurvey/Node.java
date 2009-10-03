package phylogenySurvey;

import java.awt.Point;

import javax.swing.ImageIcon;

import org.jdom.Element;

/**
 * Represents a node; a branch point in a representation of an
 * evolutionary tree
 * @author	Brian White
 * @version	 %I%%G%
 */
public class Node extends SelectableLinkableObject {
 
/**
 * Used for assigning serial numbers to Nodes. 
 * Possible conflicts:
 * <ul>
 * <li> Nodes can be created with arbitrary values 
 * <li> Always starts at zero. Does this ever open previously saved data
 *   and add new Nodes?
 * </ul>
 */
 public static int counter = 0;
 

 // Serially assigned id number, ultimately exported via save() to xml
 // object. Comes from serial assignment (static counter) or from
 // explicit assignment (see constructors)

 private int id;


/**
 * Constructs Node with assigned image, next id number in sequence
 */

 public Node(ImageIcon image) {
  super(image);
  id = counter;
  counter++;
 }

/**
 * Constructs Node with assigned image and id. New id numbers will
 * be assigned following supplied id. No provision for assignment of id
 * prior to current (ie, already assigned). 
 */
 public Node(ImageIcon image, int id) {
  super(image);
  this.id = id;
  if (id > counter) {
   counter = id;
  }
 }
 
 /**
  * Returns center of this node, calculated as (X+5, Y+5) from current
  * location.
  * Are all nodes 10X10 in size?
  */
 public Point getCenter() {
  return new Point(getLocation().x + 5,
    getLocation().y + 5);
 }
 
 /**
  * Returns id.
  */

 public int getID() {
  return id;
 }

 /**
  * Creates an XML object (e) through org.jdom.Element and assigns it the
  * fields of this object as attributes, then returns e. This is the
  * general plan of save() in this project.
  */
 public Element save() {
  Element e = new Element("Node");
  e.setAttribute("Id", String.valueOf(id));
  e.setAttribute("X", String.valueOf(getLocation().x));
  e.setAttribute("Y", String.valueOf(getLocation().y));
  return e;
 }

}


