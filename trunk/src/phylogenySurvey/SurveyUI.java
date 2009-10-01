package phylogenySurvey;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import scoring.Scorer;

public class SurveyUI {

 public static final int LABEL_WIDTH = 120;
 public static final int LABEL_HEIGHT = 30;

 private static final String PASSWORD = "treedata";

 private boolean scoringOn;

 private Container masterContainer;
 private DrawingPanel workPanel;

 private SurveyData surveyData;

 // the currently selected items
 //  max of 2 at a time
 private int numSelectedItems;
 private SelectableLinkableObject selectionA;
 private SelectableLinkableObject selectionB;
 private SelectableObject selectionOnly;    // can only have one of these selected
 // it's a plain text label
 // this is to prevent linking to a text label

 // location of where clicked in the dragged item
 //  prevents jerky movement
 private int xAdjustment;
 private int yAdjustment;
 private SelectableObject dragComponent;

 private JButton linkButton;
 private JButton unlinkButton;
 private JButton labelButton;
 private JButton deleteButton;
 private JButton splitButton;
 private JButton printButton;
 private JButton undoButton;
 private JButton scoreButton;


 public SurveyUI(Container masterContainer) {
  surveyData = new SurveyData();
  this.masterContainer = masterContainer;
  numSelectedItems = 0;
  selectionA = null;
  selectionB = null;
  selectionOnly = null;
  scoringOn = false;
 }

 public void setupUI(boolean scoringEnabled, String password) {

  if (scoringEnabled && password.equals(PASSWORD)) {
   scoringOn = true;
  }

  JPanel buttonPanel = new JPanel();
  buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

  linkButton = new JButton("Link");
  linkButton.setToolTipText("Link two selected items");
  linkButton.setEnabled(false);
  buttonPanel.add(linkButton);

  unlinkButton = new JButton("Unlink");
  unlinkButton.setToolTipText("Un-Link two selected items");
  unlinkButton.setEnabled(false);
  buttonPanel.add(unlinkButton);

  labelButton = new JButton("Label");
  buttonPanel.add(labelButton);
  labelButton.setToolTipText("Add a label");

  deleteButton = new JButton("Delete");
  deleteButton.setEnabled(false);
  buttonPanel.add(deleteButton);
  deleteButton.setToolTipText("Delete selected Node or Label");

  splitButton = new JButton("Split");
  splitButton.setToolTipText("Insert a Node between two connected items");
  splitButton.setEnabled(false);
  buttonPanel.add(splitButton);

  printButton = new JButton("Print");
  buttonPanel.add(printButton);

  undoButton = new JButton("Undo");
  buttonPanel.add(undoButton);

  if (scoringOn) {
   scoreButton = new JButton("Score");
   buttonPanel.add(scoreButton);
  }

  masterContainer.add(buttonPanel, BorderLayout.NORTH);

  workPanel = new DrawingPanel(this);
  workPanel.setLayout(null);
  workPanel.addMouseListener(new MoveLabelHandler());
  workPanel.addMouseMotionListener(new MoveLabelHandler());

  masterContainer.add(workPanel, BorderLayout.CENTER);

  linkButton.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent e) {
    if ((selectionA instanceof SelectableLinkableObject) && (selectionB instanceof SelectableLinkableObject)) {
     surveyData.add(new Link(selectionA, selectionB));
     workPanel.repaint();
     surveyData.saveStateToHistoryList();
    }
   }
  });

  unlinkButton.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent e) {
    surveyData.deleteLink(selectionA, selectionB);
    workPanel.repaint();
    surveyData.saveStateToHistoryList();
   }
  });

  labelButton.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent e) {
    String s = (String)JOptionPane.showInputDialog(
      masterContainer,
      "Enter Label Text:",
      "Create a Label",
      JOptionPane.PLAIN_MESSAGE,
      null,
      null,
    "");
    if (s != null) {
     TextLabel tl = new TextLabel(s);
     tl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
     surveyData.add(tl);
     workPanel.add(tl);
     tl.setBounds(500, 
       500, 
       (workPanel.getFontMetrics(workPanel.getFont())).stringWidth(tl.getText()) + 5, 
       SurveyUI.LABEL_HEIGHT);
    }
    workPanel.repaint();
    surveyData.saveStateToHistoryList();
   }
  });

  deleteButton.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent e) {
    // only can delete single Nodes or TextLabels
    if (selectionA instanceof Node) {
     surveyData.delete((Node)selectionA);
     workPanel.remove(selectionA);
     selectionA = null;
     workPanel.repaint();
     surveyData.saveStateToHistoryList();
    }

    if (selectionOnly != null) {
     surveyData.delete((TextLabel)selectionOnly);
     workPanel.remove(selectionOnly);
     selectionOnly = null;
     workPanel.repaint();
     surveyData.saveStateToHistoryList();
    }
   }
  });

  splitButton.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent e) {
    NodeWithLocation result = surveyData.split(selectionA, selectionB);
    if (result == null) return;
    Node node = result.getNode();
    workPanel.add(node);
    node.setBounds(result.getX(), result.getY(), 12, 12);
    workPanel.repaint();
    surveyData.saveStateToHistoryList();
    selectionA.setSelected(false);
    selectionA.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    selectionB.setSelected(false);
    selectionB.setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
   }
  });

  printButton.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent e) {
    PrinterJob printJob = PrinterJob.getPrinterJob();
    printJob.setPrintable(workPanel);
    if (printJob.printDialog())
     try { 
      printJob.print();
     } catch(PrinterException pe) {
      System.out.println("Error printing: " + pe);
     }
   }
  });

  undoButton.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent e) {
    undo();
   }
  });


  if (scoringOn) {

   scoreButton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
     JLabel message = new JLabel(
       Scorer.getInstance().score(surveyData));
     JOptionPane.showMessageDialog(null, message);
    }
   });
  }
 }

 public void loadOrganisms() {
  URL listFileURL = this.getClass().getResource("/images/list.txt");
  String line = "";
  int row = 0;
  try {
   InputStream in = listFileURL.openStream();
   BufferedReader dis =  new BufferedReader (new InputStreamReader (in));
   while ((line = dis.readLine ()) != null) {
    String[] parts = line.split(",");
    OrganismLabel ol = new OrganismLabel(
      parts[0],
      new ImageIcon(this.getClass().getResource("/images/" + parts[1])),
      parts[1],
      parts[2]);
    surveyData.add(ol);
    workPanel.add(ol);
    ol.setOpaque(true);
    ol.setBackground(Color.WHITE);
    ol.setBounds(0, (SurveyUI.LABEL_HEIGHT * row), 
      SurveyUI.LABEL_WIDTH, SurveyUI.LABEL_HEIGHT);
    row++;
   }
   in.close ();
  }
  catch (IOException e) {
   e.printStackTrace();
  }

  workPanel.repaint();
 }

 public void reset() {
  surveyData = new SurveyData();
 }

 public SurveyData getSurveyData() {
  return surveyData;
 }

 private class MoveLabelHandler implements MouseMotionListener, MouseListener {

  public void mouseDragged(MouseEvent e) {
   if (dragComponent == null) return;
   dragComponent.setLocation(e.getX() + xAdjustment, e.getY() + yAdjustment);
   workPanel.repaint();
  }

  public void mouseMoved(MouseEvent e) {}

  public void mouseClicked(MouseEvent e) {
   Component c = workPanel.findComponentAt(e.getX(), e.getY());
   if (c instanceof SelectableObject) {
    updateSelections((SelectableObject)c);
   } else if ((c instanceof DrawingPanel) && e.isShiftDown()) {
    Node node = new Node(new ImageIcon(this.getClass().getResource("/images/node.gif" )));
    surveyData.add(node);
    workPanel.add(node);
    node.setBounds(e.getX(), e.getY(), 12, 12);
    surveyData.saveStateToHistoryList();
   }
  }

  public void mouseEntered(MouseEvent e) {}

  public void mouseExited(MouseEvent e) {}

  public void mousePressed(MouseEvent e) {
   dragComponent = null;

   Component c = workPanel.findComponentAt(e.getX(), e.getY());

   if (c instanceof JPanel) return;

   if (c instanceof SelectableObject) {
    dragComponent = (SelectableObject)c;
    xAdjustment = dragComponent.getLocation().x - e.getX();
    yAdjustment = dragComponent.getLocation().y - e.getY();
    dragComponent.setLocation(e.getX() + xAdjustment, e.getY() + yAdjustment);
    workPanel.repaint();
   }
  }

  public void mouseReleased(MouseEvent e) {
   if (dragComponent != null) {
    surveyData.saveStateToHistoryList();
    dragComponent = null;
   }
   workPanel.repaint();
  }
 }

 private void updateSelections(SelectableObject so) {
  // see if it can be linked
  if (so instanceof SelectableLinkableObject) {
   if (so.isSelected()) {
    if (so == selectionA) {
     selectionA.setSelected(false);
     selectionA.setBorder(BorderFactory.createLineBorder(Color.BLACK));
     selectionA = selectionB;
     selectionB = null;
    }
    if (so == selectionB) {
     selectionB = null;
    }
    so.setSelected(false);
    so.setBorder(BorderFactory.createLineBorder(Color.BLACK));
   } else {
    if (selectionA != null) {
     if (selectionB != null) {
      selectionB.setSelected(false);
      selectionB.setBorder(BorderFactory.createLineBorder(Color.BLACK));
     }
     selectionB = selectionA;
    } 
    selectionA = (SelectableLinkableObject)so;
    so.setSelected(true);
    so.setBorder(BorderFactory.createLineBorder(Color.RED)); 

    //clear any selected text labels
    if (selectionOnly != null) {
     selectionOnly.setSelected(false);
     selectionOnly.setBorder(BorderFactory.createLineBorder(Color.BLACK));
     selectionOnly = null;
    }
   }

   numSelectedItems = 0;
   if (selectionA != null) numSelectedItems++;
   if (selectionB != null) numSelectedItems++;

   switch (numSelectedItems) {
   case 2:
    linkButton.setEnabled(true);
    unlinkButton.setEnabled(true);
    splitButton.setEnabled(true);
    break;

   case 1:
    if (selectionA instanceof Node) {
     deleteButton.setEnabled(true);
    }
    linkButton.setEnabled(false);
    unlinkButton.setEnabled(false); 
    splitButton.setEnabled(false);
    break;

   case 0:
    linkButton.setEnabled(false);
    unlinkButton.setEnabled(false); 
    deleteButton.setEnabled(false);
    splitButton.setEnabled(false);
   }

   //if not, deal with it specially - it is a TextLabel
   //  need to clear other selections
   //  don't keep track of it 
  } else {
   if (so.isSelected()) {
    so.setSelected(false);
    so.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    selectionOnly = null;
    deleteButton.setEnabled(false);
   } else {
    //clear any other selections
    if (selectionA != null) {
     selectionA.setSelected(false);
     selectionA.setBorder(BorderFactory.createLineBorder(Color.BLACK));
     selectionA = null;
    }
    if (selectionB != null) {
     selectionB.setSelected(false);
     selectionB.setBorder(BorderFactory.createLineBorder(Color.BLACK));
     selectionB = null;
    }
    so.setSelected(true);
    so.setBorder(BorderFactory.createLineBorder(Color.RED));
    selectionOnly = so;
    deleteButton.setEnabled(true);
   }
  }
 }

 private void undo() {
  String newState = surveyData.undo();
  if (newState == null) {
   return;
  }
  setState(newState);
  workPanel.repaint();
  selectionA = null;
  selectionB = null;
  selectionOnly = null;
  linkButton.setEnabled(false);
  unlinkButton.setEnabled(false); 
  deleteButton.setEnabled(false);
  splitButton.setEnabled(false);  
 }

 /*
  * public methods for setting & getting the state of the drawing
  */
 public String getState() {
  return surveyData.getState();
 }

 public void setState(String newState) {
  surveyData = null;
  surveyData = new SurveyData();

  Document doc = null;
  SAXBuilder builder = new SAXBuilder();
  try {
   doc = builder.build(new StringReader(newState));
  } catch (JDOMException e) {
   e.printStackTrace();
  } catch (IOException e) {
   e.printStackTrace();
  }

  List<Element> elements = doc.getRootElement().getChildren();
  Iterator<Element> elIt = elements.iterator();
  while (elIt.hasNext()) {
   Element e = elIt.next();
   String name = e.getName();
   if (name.equals("Items")) {
    processItems(e);
   }
   if (name.equals("Links")) {
    processLinks(e);
   }
  }

  workPanel.repaint();
  selectionA = null;
  selectionB = null;
  selectionOnly = null;
  linkButton.setEnabled(false);
  unlinkButton.setEnabled(false); 
  deleteButton.setEnabled(false);
  splitButton.setEnabled(false);  
 }

 private void processItems(Element e) {
  List<Element> elements = e.getChildren();
  Iterator<Element> elIt = elements.iterator();
  while (elIt.hasNext()) {
   Element current = elIt.next();
   String name = current.getName();

   if (name.equals("OrganismLabel")) {
    OrganismLabel ol = new OrganismLabel(
      current.getAttributeValue("Name"),
      new ImageIcon(
        this.getClass().getResource(
          "/images/" + current.getAttributeValue("ImageFileName"))),
          current.getAttributeValue("ImageFileName"),
          current.getAttributeValue("Type"));
    surveyData.add(ol); 
    workPanel.add(ol);
    ol.setOpaque(true);
    ol.setBackground(Color.WHITE);
    ol.setBounds(Integer.parseInt(current.getAttributeValue("X")), 
      Integer.parseInt(current.getAttributeValue("Y")), 
      SurveyUI.LABEL_WIDTH, SurveyUI.LABEL_HEIGHT);
   }

   if (name.equals("Node")) {
    Node node = new Node(new ImageIcon(this.getClass().getResource("/images/node.gif" )),
      Integer.parseInt(current.getAttributeValue("Id")));
    surveyData.add(node);
    workPanel.add(node);
    node.setBounds(Integer.parseInt(current.getAttributeValue("X")), 
      Integer.parseInt(current.getAttributeValue("Y")), 
      12, 12);
   }

   if (name.equals("TextLabel")) {
    TextLabel tl = new TextLabel(current.getAttributeValue("Text"),
      Integer.parseInt(current.getAttributeValue("Id")));
    tl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    surveyData.add(tl);
    workPanel.add(tl);
    tl.setBounds(Integer.parseInt(current.getAttributeValue("X")),
      Integer.parseInt(current.getAttributeValue("Y")),
      Integer.parseInt(current.getAttributeValue("width")),
      Integer.parseInt(current.getAttributeValue("height")));
   }

  }
 }

 private void processLinks(Element e) {
  List<Element> elements = e.getChildren();
  Iterator<Element> elIt = elements.iterator();
  while (elIt.hasNext()) {
   Element current = elIt.next();
   String name = current.getName();
   if (name.equals("Link")) {
    List<Element> ends = current.getChildren();
    Iterator<Element> endsIt = ends.iterator();
    SelectableLinkableObject firstSLO = null;
    SelectableLinkableObject secondSLO = null;
    while (endsIt.hasNext()) {
     Element end = endsIt.next();
     String endName = end.getName();
     if (endName.equals("FirstSLO")) {
      firstSLO = surveyData.findItemByName((Element)end.getChildren().get(0));
     }
     if (endName.equals("SecondSLO")) {
      secondSLO = surveyData.findItemByName((Element)end.getChildren().get(0));      
     }
    }
    if ((firstSLO != null) && (secondSLO != null)) {
     surveyData.add(new Link(firstSLO, secondSLO));
    }
   }
  }
 }

}
