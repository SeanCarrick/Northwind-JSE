/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.northwind.actions.view;

import com.northwind.custmgr.model.Customer;
import com.northwind.custmgr.view.CustomersTableModel;
import java.awt.Color;
import java.awt.Component;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 */
public class TableCellRenderer extends DefaultTableCellRenderer {
    
    public TableCellRenderer() {
        super();
    }
    
    public Color clrCustomer(Customer o) {
        if ( o.isActive() ) 
            return Color.WHITE;
        else {
            return Color.PINK;
        }
    }
    
    public Object format(Object o) {
        if (o instanceof Date) {
            Date d = (Date)o;
            DateFormat df = DateFormat.getDateInstance();
            return df.format(d);
        } else if ( o instanceof Boolean ) {
            return (Boolean)o ? "Y" : "N";
        } else {
            return o;
        }
    }
    
    public Component getTableCellRendererComponent(javax.swing.JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row, 
            int column) {
        value = format(value);
        JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, 
                isSelected, hasFocus, row, column);
        if ( column != 1 ) {
            label.setHorizontalAlignment(JLabel.CENTER);
        }
        TableModel tm = table.getModel();
        Customer cust = ((CustomersTableModel)tm).getCustomerValues(row);
        if ( isSelected ) {
            label.setForeground(clrCustomer(cust));
            label.setBackground(Color.GRAY);
        } else {
            label.setForeground(Color.BLACK);
            label.setBackground(clrCustomer(cust));
        }
        
        return label;
    }
    
}
