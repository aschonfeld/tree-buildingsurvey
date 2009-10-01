package phylogenySurvey;

import java.awt.Point;

import javax.swing.ImageIcon;

import org.jdom.Element;

public class Node extends SelectableLinkableObject {
 
 public static int counter = 0;
 
 private int id;

 public Node(ImageIcon image) {
  super(image);
  id = counter;
  counter++;
 }
 
 public Node(ImageIcon image, int id) {
  super(image);
  this.id = id;
  if (id > counter) {
   counter = id;
  }
 }
 
 //does this assume that nodes are 10X10?
 public Point getCenter() {
  return new Point(getLocation().x + 5,
    getLocation().y + 5);
 }
 
 public int getID() {
  return id;
 }

 public Element save() {
  Element e = new Element("Node");
  e.setAttribute("Id", String.valueOf(id));
  e.setAttribute("X", String.valueOf(getLocation().x));
  e.setAttribute("Y", String.valueOf(getLocation().y));
  return e;
 }

}
