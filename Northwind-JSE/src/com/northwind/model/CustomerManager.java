/*
 * Copyright (C) 2020 PekinSOFT Systems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.northwind.model;

import com.northwind.exceptions.DatabaseException;
import com.northwind.exceptions.ValidationException;
import com.northwind.utils.Logger;
import com.northwind.utils.Parameters;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 */
public class CustomerManager {
    
    private Parameters params;
    private Connection con;
    private Statement stmt;
    private ResultSet rs;
    private Logger log = Logger.getInstance();
    private LogRecord record = new LogRecord(Level.FINE, 
            "Initializing CustomerManager");
    
    public CustomerManager(Parameters params) throws DatabaseException {
        record.setSourceClassName(CustomerManager.class.getCanonicalName());
        record.setSourceMethodName("CustomerManager");
        log.enter(record);
        
        record.setMessage("Initializing parameters and connecting to the database");
        log.config(record);
        this.params = params;
        connect();
        
        record.setMessage("Leaving constructor");
        log.exit(record, "");
    }
    
    public void reconnect(String database) throws DatabaseException {
        record.setSourceMethodName("reconnect");
        record.setMessage("Entering reconnect");
        record.setParameters(new Object[]{database});
        log.enter(record);
        
        record.setMessage("Calling disconnect...");
        log.debug(record);
        disconnect();
        
        record.setMessage("Setting the parameters for the database to: " + database);
        log.config(record);
        params.setDatabase(database);
        
        record.setMessage("Attempting to connect to the database: " + database);
        log.debug(record);
        connect();
        
        record.setMessage("Leaving reconnect");
        log.exit(record, new Object[]{"VOID"});
    }
    
    public void connect() throws DatabaseException {
        record.setMessage("Entering connect");
        record.setSourceMethodName("connect");
        record.setParameters(null);
        log.enter(record);
        
        record.setMessage("Attempting to connect to the database: " + 
                params.getDatabase() + ": using driver [" + params.getJdbcDriver()
                + "]: at URL {" + params.getJdbcUrl() + "}");
        log.debug(record);
        try {
            Class.forName(params.getJdbcDriver());
            String url = params.getJdbcUrl();
            con = DriverManager.getConnection(url , "sa", "");
            
            record.setMessage("Checking to see if tables exist.");
            log.debug(record);
            if ( !checkTables() ) {
                createTables();
            }
        } catch ( DatabaseException ex ) {
            record.setMessage("Throwing Database Exception: Cannot initialize "
                    + "the database tables.\n\nCause: " + ex.getCause());
            record.setThrown(ex);
            log.error(record);
            throw new DatabaseException ("Cannot initilize the database tables",
                    ex.getCause());
        } catch ( ClassNotFoundException ex ) {
            record.setMessage("Throwing Database Exception: Cannot load the "
                    + "database driver.\n\nCause: " + ex.getCause());
            record.setThrown(ex);
            log.error(record);
            throw new DatabaseException ( "Cannot load the database driver.",
                    ex );
        } catch ( SQLException ex ) {
            record.setMessage("Throwing Database Exception: Cannot open the "
                    + "database.\n\nCause: " + ex.getCause() );
            record.setThrown(ex);
            log.error(record);
            throw new DatabaseException ( "Cannot open the database", ex );
        }
        
        record.setMessage("Leaving connect.");
        log.exit(record, new Object[]{"VOID"});
    }
    
    private boolean checkTables() {
        record.setMessage("Entering checkTables");
        record.setSourceMethodName("checkTables");
        log.enter(record);
        
        record.setMessage("Attempting to count tables in the database.");
        log.debug(record);
        
        try {
            String sql = "SELECT CHOUNT(*) FROM nwind";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            
            record.setMessage("Connection successful and tables exist.");
            log.debug(record);
            return true;
        } catch ( SQLException ex ) {
            return false;
        } finally {
            record.setMessage("Performing cleanup.");
            log.info(record);
            cleanUp();
            
            record.setMessage("Returning from whence we came.");
            log.exit(record, new Object[]{"VOID"});
        }
    }
    
    private void createTables() throws DatabaseException {
        record.setSourceMethodName("createTables");
        record.setMessage("Entering createTables");
        log.enter(record);
        
        record.setMessage("Calling update with the customer table creation "
                + "string.");
        log.debug(record);
        update("CREATE TABLE customers (" +
                "id IDENTITY, "
                + "companyName VARCHAR(40) NOT NULL, "
                + "streetAddress VARCHAR(50) NOT NULL, "
                + "suiteNumber VARCHAR(15), "
                + "city VARCHAR(30) NOT NULL, "
                + "state VARCHAR(2) NOT NULL, "
                + "zipCode VARCHAR(10) NOT NULL, "
                + "phoneNumber VARCHAR(14), "
                + "faxNumber VARCHAR(14), "
                + "emailAddress VARCHAR(50), "
                + "contactName VARCHAR(40),"
                + "notes LONGVARCHAR, "
                + "active BIT)");
        
        record.setMessage("Leaving createTables");
        log.exit(record, new Object[]{"VOID"});
    }
    
    public void disconnect() {
        record.setSourceMethodName("disconnect");
        record.setMessage("Commencing the disconnect from the database.");
        log.enter(record);
        
        try {
            if ( con != null ) {
                con.close();
            }
            con = null;
        } catch ( SQLException ex ) {
            // Ignores the exception.
        } finally {
            record.setMessage("Returning from whence we came.");
            log.exit(record, new Object[]{"VOID"});
        }
    }
    
    private void cleanUp() {
        record.setSourceMethodName("cleanUp");
        record.setMessage("Commencing cleanup");
        log.enter(record);
        
        try {
            if ( rs != null ) {
                rs.close();
            }
            rs = null;
            
            if ( stmt != null ) {
                stmt.close();
            }
            stmt = null;
        } catch ( SQLException ex ) {
            // Ignores the exception.
        } finally {
            record.setMessage("Cleanup complete.");
            log.exit(record, new Object[]{"VOID"});
        }
    }
    
    private void update(String sql) throws DatabaseException {
        record.setSourceMethodName("update");
        record.setParameters(new Object[]{sql});
        record.setMessage("Updating the database, if necessary");
        log.enter(record);
        
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
        } catch ( SQLException ex ) {
            record.setMessage("SQL Exception thrown. Converting to "
                    + "DatabaseException");
            record.setThrown(ex);
            log.error(record);
            throw new DatabaseException("Cannot modify the database", ex);
        } finally {
            record.setMessage("Cleaning up before we leave.");
            log.debug(record);
            cleanUp();
            
            record.setMessage("Returning from whence we came.");
            log.exit(record, new Object[]{"VOID"});
        }
    }
    
    private PreparedStatement prepare(String sql) throws SQLException {
        record.setSourceMethodName("prepare");
        record.setParameters(new Object[]{sql});
        record.setMessage("Preparing a statement.");
        log.enter(record);
        
        try {
            PreparedStatement pst = con.prepareStatement(sql);
            stmt = pst;
            return pst;
        } finally {
            record.setMessage("Cleaning up before we leave.");
            log.debug(record);
            cleanUp();
            
            record.setMessage("Returning from whence we came.");
            log.exit(record, new Object[]{"VOID"});
        }
    }
    
    private List<Customer> query(String where, String orderBy) 
            throws DatabaseException {
        record.setSourceMethodName("query");
        record.setParameters(new Object[]{where, orderBy});
        record.setMessage("Performing a query and returning it as a List<T>");
        log.enter(record);
        
        List<Customer> result = new ArrayList<>();
        
        try {
            String sql = "SELECT id, companyName, streetAddress, city, state, "
                    + "zipCode, active FROM customers ";
            if ( where != null )
                sql += "WHERE " + where + " ";
            if ( orderBy != null )
                sql += "ORDER BY " + orderBy;
            
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            
            while ( rs.next() ) {
                Customer customer = new Customer();
                customer.setId(rs.getInt(1));
                customer.setCompanyName(rs.getString(2));
                customer.setStreetAddress(rs.getString(3));
                customer.setCity(rs.getString(4));
                customer.setState(rs.getString(5));
                customer.setZipCode(rs.getString(6));
                customer.setActive(rs.getBoolean(7));
                result.add(customer);
            }
        } catch ( SQLException ex ) {
            record.setMessage("An SQL Exception has been thrown");
            record.setThrown(ex);
            log.error(record);
            throw new DatabaseException("Cannot fetch from the database", ex);
        } finally {
            record.setMessage("Cleaning up before we leave.");
            log.debug(record);
            cleanUp();
            
            record.setMessage("Returning from whence we came.");
            log.exit(record, result);
        }
        
        return result;
    }
    
    private void modify(String sql, Customer customer) throws DatabaseException {
        record.setSourceMethodName("modify");
        record.setParameters(new Object[]{sql, customer});
        record.setMessage("Performing data modification.");
        log.enter(record);
        
        try {
            PreparedStatement pst = con.prepareStatement(sql);
            stmt = pst;
            pst.setInt(1, customer.getId());
            pst.setString(2, customer.getCompanyName());
            pst.setString(3, customer.getStreetAddress());
            pst.setString(4, customer.getCity());
            pst.setString(5, customer.getState());
            pst.setString(6, customer.getZipCode());
            pst.setBoolean(7, customer.isActive());
            pst.executeUpdate();
        } catch ( SQLException ex ) {
            record.setMessage("An SQLException has been thrown");
            record.setThrown(ex);
            log.error(record);
            throw new DatabaseException("Cannot update the database", ex);
        } finally {
            record.setMessage("Cleaning up before we leave.");
            cleanUp();
            
            record.setMessage("Returning from whence we came.");
            log.exit(record, new Object[]{"VOID"});
        }
    }
    
    public List<Customer> listAllCustomers(boolean byCompany) 
            throws DatabaseException {
        record.setSourceMethodName("listAllCustomers");
        record.setMessage("Listing customers by either company name or stat");
        log.enter(record);
        record.setMessage("Returning from whence we came.");
        log.exit(record, query(null, byCompany ? "companyName, streetAddress, "
                + "city, state, zipCode, id, active" : "state, companyName, "
                + "streetAddress, city, zipCode, id, active"));
        return query(null, byCompany ? "companyName, streetAddress, city, "
                + "state, zipCode, id, active" : "state, companyName, "
                        + "streetAddress, city, zipCode, id, active");
    }
    
    public List<Customer> listActiveCustomersOnly() throws DatabaseException {
        record.setSourceMethodName("listActiveCustomersOnly");
        record.setMessage("Listing only active customers");
        log.enter(record);
        record.setMessage("Returning from whence we came.");
        log.exit(record, query("active = true", "companyName, state"));
        return query("active = true", "companyName, state");
    }
    
    public void addCustomer(Customer customer) throws ValidationException,
            DatabaseException {
        record.setSourceMethodName("addCustomer");
        record.setParameters(new Object[]{customer});
        record.setMessage("Adding a new customer");
        log.enter(record);
        
        record.setMessage("Validating the data");
        log.debug(record);
        validate(customer);
        
        record.setMessage("Generating the SQL string to add the record.");
        log.debug(record);
        String sql = "INSERT INTO customers ("
                + "companyName, streetAddress, suiteNumber, city, state, "
                + "zipCode, phoneNumber, faxNumber, emailAddress, contact, "
                + "notes, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        record.setMessage("Using the following SQL statement:\n\n" + sql);
        log.debug(record);
        modify(sql, customer);
        
        record.setMessage("Returning from whence we came.");
        log.exit(record, new Object[]{"VOID"});
    }
    
    public void updateCustomer(Customer customer) throws ValidationException,
            DatabaseException {
        record.setSourceMethodName("updateCustomer");
        record.setParameters(new Object[]{customer});
        record.setMessage("Updating an existing customer record");
        log.enter(record);
        validate(customer);
        
        String sql = "UPDATE customers SET "
                + "companyName = ?, streetAddress = ?, suiteNumber = ?, "
                + "city = ?, state = ?, zipCode = ?, phoneNumber = ?, "
                + "faxNumber = ?, emailAddress = ?, contactName = ?, "
                + "notes = ?, active = ? "
                + "WHERE id = " + customer.getId();
        
        record.setMessage("Using the following SQL statement:\n\n" + sql);
        log.debug(record);
        modify(sql, customer);
        
        record.setMessage("Returning from whence we came.");
        log.exit(record, new Object[]{"VOID"});
    }
    
    public void changeAciveState(int id, boolean active) 
            throws DatabaseException {
        record.setSourceMethodName("changeActiveState");
        record.setParameters(new Object[]{id, active});
        record.setMessage("Changing the active state of Customer ID: " + id
                + " to " + active);
        log.enter(record);
        update("UPDATE customers SET active = " + active + " WHERE "
                + "id = " + id);
        
        record.setMessage("Returning from whence we came.");
        log.exit(record, new Object[]{"VOID"});
    }
    
    public void removeCustomer(int id) throws DatabaseException {
        record.setSourceMethodName("removeCustomer");
        record.setParameters(new Object[]{id});
        record.setMessage("Removing Customer ID " + id + " from the database");
        log.enter(record);
        update("DELETE FROM customers WHERE id = " + id);
        
        record.setMessage("Returning from whence we came.");
        log.exit(record, new Object[]{"VOID"});
    }
    
    private boolean isEmpty(String str) {
        record.setSourceMethodName("isEmpty");
        record.setParameters(new Object[]{str});
        record.setMessage("Checking for data in parameter");
        log.enter(record);
        record.setMessage("Returning from whence we came.");
        log.exit(record, new Object[]{str == null || str.trim().length() == 0});
        return str == null || str.trim().length() == 0;
    }
    
    private void validate(Customer customer) throws ValidationException {
        record.setSourceMethodName("validate");
        record.setParameters(new Object[]{customer});
        record.setMessage("Validating the data");
        if ( isEmpty(customer.getCity()) ) {
            record.setMessage("Missing city data");
            log.exit(record, new Object[]{new ValidationException("City is a "
                    + "required field")});
            throw new ValidationException("City is a required field");
        }
        if ( isEmpty(customer.getCompanyName()) ) {
            record.setMessage("Missing company name data");
            log.exit(record, new Object[]{new ValidationException("Company Name"
                    + " is a required field")});
            throw new ValidationException("Company Name is a required field");
        }
        if ( isEmpty(customer.getState()) ) {
            record.setMessage("Missing state data");
            log.exit(record, new Object[]{new ValidationException("State is a "
                    + "required field")});
            throw new ValidationException("State is a required field");
        }
        if ( isEmpty(customer.getStreetAddress()) ) {
            record.setMessage("Missing street address data");
            log.exit(record, new Object[]{new ValidationException("Street "
                    + "Address is a required field")});
            throw new ValidationException("Street Address is a required field");
        }
        if ( isEmpty(customer.getZipCode()) ) {
            record.setMessage("Missing Zip Code data");
            log.exit(record, new Object[]{new ValidationException("Zip Code is a "
                    + "required field")});
            throw new ValidationException("Zip Code is a required field");
        }
    }
    
}
