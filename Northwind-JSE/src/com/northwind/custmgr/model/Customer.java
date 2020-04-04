/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.northwind.custmgr.model;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 */
public class Customer {
    
    public static final long serialVersionUID = 8734569812536L;
    
    private static int count = 0;
    
    private int id;
    private String companyName;
    private String streetAddress;
    private String suiteNumber;
    private String city;
    private String state;
    private String zipCode;
    private String phoneNumber;
    private String faxNumber;
    private String emailAddress;
    private String contactName;
    private String notes;
    private boolean active;
    
    private boolean sortByState = false;
    
    public Customer() {
        
        Long tmpID = System.currentTimeMillis();
        String strID = tmpID.toString();
        strID = strID.substring(strID.length() - 12);
        
//        this.id = Integer.valueOf(strID);
        this.id = ++count;
        
    }
    
    public Customer(String company, String street, String suite, String city,
            String state, String zip, String phone, String fax, String email,
            String contact, String notes, boolean isActive) {
        this();
        
        this.city = city;
        this.companyName = company;
        this.contactName = contact;
        this.emailAddress = email;
        this.faxNumber = fax;
        this.notes = notes;
        this.phoneNumber = phone;
        this.state = state;
        this.streetAddress = street;
        this.suiteNumber = suite;
        this.zipCode = zip;
        this.active = isActive;
        
    }
    
    public Customer(int id, String company, String street, String suite, 
            String city, String state, String zip, String phone, String fax, 
            String email, String contact, String notes, boolean isActive) {
        
        this.city = city;
        this.companyName = company;
        this.contactName = contact;
        this.emailAddress = email;
        this.faxNumber = fax;
        this.id = id;
        this.notes = notes;
        this.phoneNumber = phone;
        this.state = state;
        this.streetAddress = street;
        this.suiteNumber = suite;
        this.zipCode = zip;
        this.active = isActive;
        
    }
    
    
    public int getId() {
        return this.id;
    }
    
    public void setId(int newID) {
        this.id = newID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getSuiteNumber() {
        return suiteNumber;
    }

    public void setSuiteNumber(String suiteNumber) {
        this.suiteNumber = suiteNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public String toString() {
        if ( this.sortByState ) 
            return this.getState();
        else 
            return this.getCompanyName();
    }
}
