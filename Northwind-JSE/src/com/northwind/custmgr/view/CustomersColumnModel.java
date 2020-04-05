/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.northwind.custmgr.view;

import com.northwind.actions.view.TableCellRenderer;
import java.awt.FontMetrics;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 */
public class CustomersColumnModel extends DefaultTableColumnModel {
    
    private TableColumn createColumn(int columnIndex, int width, FontMetrics fm,
            boolean resizable, String text) {
        int textWidth = fm.stringWidth(text + "  ");
        if ( width < textWidth )
            width = textWidth;
        TableColumn col = new TableColumn(columnIndex);
        col.setCellRenderer(new TableCellRenderer());
        col.setHeaderRenderer(null);
        col.setHeaderValue(text);
        col.setPreferredWidth(width);
        if ( !resizable ) {
            col.setMaxWidth(width);
            col.setMinWidth(width);
        }
        col.setResizable(resizable);
        return col;
    }
    
    public CustomersColumnModel(FontMetrics fm) {
        int digit = fm.stringWidth("0");
        int alpha = fm.stringWidth("W");
        addColumn(createColumn(0, 5 * digit, fm, false, "ID #"));
        addColumn(createColumn(1, 30 * alpha, fm, false, "Company Name"));
        addColumn(createColumn(2, 30 * alpha, fm, true, "Street Address"));
        addColumn(createColumn(3, 15 * alpha, fm, false, "Suite"));
        addColumn(createColumn(4, 30 * alpha, fm, false, "City"));
        addColumn(createColumn(5, 3 * digit, fm, false, "ST"));
        addColumn(createColumn(6, 10 * alpha, fm, false, "Zip Code"));
        addColumn(createColumn(7, 15 * alpha, fm, false, "Phone Number"));
        addColumn(createColumn(8, 15 * alpha, fm, false, "Fax Number"));
        addColumn(createColumn(9, 45 * alpha, fm, true, "Email Address"));
        addColumn(createColumn(10, 20 * alpha, fm, true, "Contact Name"));
        addColumn(createColumn(11, 20 * alpha, fm, true, "Notes"));
        addColumn(createColumn(12, 2 * alpha, fm, false, "A"));
    }
    
}
