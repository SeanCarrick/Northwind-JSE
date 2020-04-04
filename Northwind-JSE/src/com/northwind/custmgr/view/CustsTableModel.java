/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.northwind.custmgr.view;

import com.northwind.custmgr.model.Customer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 */
public class CustsTableModel extends AbstractTableModel {

    private final List<Customer> list;
    private List<Customer> filteredList;
    private boolean showActive;
    private boolean sortByState;
    private final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
    
    public CustsTableModel(List<Customer> list) {
        this.list = list;
        filterList();
    }
    
    @Override
    public Object getValueAt(int rowIndex, int colIndex) {
        
        Customer o = filteredList.get(rowIndex);
        
        switch ( colIndex ) {
            case 0: return o.getId();
            case 1: return o.getCompanyName();
            case 2: return o.getStreetAddress();
            case 3: return o.getSuiteNumber();
            case 4: return o.getCity();
            case 5: return o.getState();
            case 6: return o.getZipCode();
            case 7: return o.getPhoneNumber();
            case 8: return o.getFaxNumber();
            case 9: return o.getEmailAddress();
            case 10: return o.getContactName();
            case 11: return o.getNotes();
            case 12: return o.isActive();
            default: return null;
        }
        
    }
    
    @Override
    public int getRowCount() {
        
        return filteredList.size();
        
    }
    
    @Override
    public int getColumnCount() {
        
        return 13;
        
    }
    
    public Customer getCustomerValues(int rowIndex) {
        
        if ( rowIndex < 0 || rowIndex > filteredList.size() )
            return null;
        else
            return filteredList.get(rowIndex);
    }
    
    public boolean isShowActive() {
        return showActive;
    }
    
    public void setShowActive(boolean showActive) {
        this.showActive = showActive;
    }
    
    public boolean isSortByState() {
        return sortByState;
    }
    
    public void setSortByState(boolean sortByState) {
        this.sortByState = sortByState;
    }
    
    private void filterList() {
        filteredList = new ArrayList<>();
        
        for ( Customer c : list ) {
            if ( !isShowActive() && c.isActive() ) {
                continue;
            }
            
            filteredList.add(c);}
        
        if ( !isSortByState() ) 
            Collections.sort(filteredList, new SortListByCompany());
        else
            Collections.sort(filteredList, new SortListByState());
        
    }
    
    private class SortListByState implements Comparator<Customer> {

        @Override
        public int compare(Customer o1, Customer o2) {
            return o1.getState().compareTo(o2.getState());
        }
        
    }
    
    private class SortListByCompany implements Comparator<Customer> {
        
        @Override
        public int compare(Customer o1, Customer o2) {
            return o1.getCompanyName().compareTo(o2.getCompanyName());
        }
    }
    
}
