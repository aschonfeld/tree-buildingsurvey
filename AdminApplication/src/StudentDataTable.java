/*
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 
//package events;

/*
 * TableListSelectionDemo.java requires no other files.
 */

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class StudentDataTable extends JPanel {
    JTextArea output;
    JList list; 
    JTable table;
    String newline = "\n";
    ListSelectionModel listSelectionModel;

    public StudentDataTable() {
        super(new BorderLayout());

        String[] columnNames = { "Name", 
        						 "Contains loop", 
        						 "All organism nodes terminal", 
        						 "Includes all organisms"};
        int rows = AdminApplication.studentNameToTree.size();
        Object[][] tableData = new Object[rows][columnNames.length];
        int row = 0;
        for(String studentName: AdminApplication.studentNameToTree.keySet()) {
        	Graph graph = AdminApplication.studentNameToTree.get(studentName);
        	tableData[row][0] = studentName;
        	tableData[row][1] = graph.containsCycle();
        	tableData[row][2] = graph.allOrganismsTerminal();
        	tableData[row][3] = graph.includesAllOrganisms();
        	row++;
        }
        table = new JTable(tableData, columnNames);
        listSelectionModel = table.getSelectionModel();
        listSelectionModel.addListSelectionListener(new SharedListSelectionHandler());
        table.setSelectionModel(listSelectionModel);
        JScrollPane tablePane = new JScrollPane(table);
        listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePane.setPreferredSize(new Dimension(420, 130));
        add(tablePane);
    }
    
    class SharedListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) { 
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            StringBuffer output = new StringBuffer();
            int firstIndex = e.getFirstIndex();
            int lastIndex = e.getLastIndex();
            boolean isAdjusting = e.getValueIsAdjusting(); 
            output.append("Event for indexes "
                          + firstIndex + " - " + lastIndex
                          + "; isAdjusting is " + isAdjusting
                          + "; selected indexes:");

            if (lsm.isSelectionEmpty()) {
                output.append(" <none>");
            } else {
                // Find out which indexes are selected.
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
                        output.append(" " + i);
                        if(!isAdjusting) AdminApplication.setCurrentGraph(i);
                    }
                }
            }
            System.out.println(output.toString());
        }
    }
}
