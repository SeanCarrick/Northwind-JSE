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
package com.northwind.api;

import com.northwind.exceptions.DataStoreException;
import com.northwind.settings.AppProperties;
import com.northwind.utils.Logger;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import static javax.management.Query.value;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 */
public abstract class Manager {
    
    protected AppProperties props;
    protected Logger log;
    protected LogRecord record;
    
    protected Connection con;
    protected Statement stmt;
    protected ResultSet rs;
    
    protected String dbURL;
    protected String dbName;
    protected String userName;
    protected char[] password;
    protected String tableName;
    
    protected List<?> model;
    
    /**
     * Creates a new `Manager` object for accessing and managing the data for a
     * specific table in the database.
     * 
     * @param url       the database URL for connecting to the data store
     * @param db        the name of the data store for storing data
     * @param name      the user name, if any, for accessing the data store
     * @param pWord     the password, if any, for accessing the data store
     * @param table     the table within the data store to be used by this 
     *                  `Manager` object
     * @param model     the data model for this `Manager` object to use for 
     *                  mapping the table to the data
     * @throws SQLException in the event any database errors are experienced
     */
    public Manager(String url, String db, String name, char[] pWord,
            String table, List<?> model) throws DataStoreException {
        props = AppProperties.getInstance();
        log = Logger.getInstance();
        record = new LogRecord(Level.FINEST, "Initializing the Manager object");
        record.setSourceClassName(Manager.class.getName());
        record.setSourceMethodName("Manger");
        record.setParameters(new Object[]{url, db, name, pWord, table, model});
        log.enter(record);
        
        this.dbName = db;
        this.dbURL = url;
        this.model = model;
        this.password = pWord;
        this.tableName = table;
        this.userName = name;
        
        ////////////////////////////////////////////////////////////////////////
        // LEAVE THE FOLLOWING AT THE END OF THE METHOD!                      //
        ////////////////////////////////////////////////////////////////////////
        record.setMessage("Completed initialization of the Manager object.");
        log.exit(record, null);
    }
    
    /**
     * Performs a disconnect and reconnect to the backing data store. This is
     * the proper way to make a connection to the data store, so that only one
     * connection is open at a time. Furthermore, this method prevents memory
     * leaks by making sure that all objects are set up for garbage collection
     * once they are done being used.
     * 
     * @param db    the name of the data store to use. If no data store name is
     *              provided, the default data store is used. If the provided
     *              data store name is `null`, the default data store is used.
     * @throws DataStoreException in the event any errors occur while connecting
     *                            to the data store.
     */
    public void reconnect(String db) throws DataStoreException {
        record.setSourceMethodName("reconnect");
        record.setParameters(new Object[]{db});
        record.setMessage("Attempting to reconnect to the data store...");
        log.enter(record);
        
        record.setMessage("First, we will attempt to disconnect.");
        disconnect();
        
        record.setSourceMethodName("reconnect");
        record.setMessage("Next, we will make sure we were given a good data "
                + "store name to connect to.");
        log.debug(record);
        if ( db == null || db.isBlank() || db.isEmpty() ) {
            record.setMessage("The data store name given to us was null, "
                    + "blank, or empty, so we are setting the default table "
                    + "name to the parameter `db`.");
            log.debug(record);
            db = props.getProperty("app.last.db", tableName);
            
            props.setProperty("app.last.db", db);
        }
        
        record.setMessage("Now that we have a valid data store table name, we "
                + "can attempt to connect to it.");
        log.debug(record);
        connect();
        
        record.setSourceMethodName("reconnect");
        record.setMessage("Connection attempt complete. Returning from whence "
                + "we came...");
        log.exit(record, null);
    }
    
    protected void disconnect() {
        record.setSourceMethodName("disconnect");
        record.setMessage("Attempting to disconnect from the data store...");
        log.enter(record);
        
        try {
            if ( con != null )
                con.close();
            con = null;
        } catch ( SQLException ex ) {
            // We are going to ignore the exception, however, we are going to 
            //+ write it to the log for the sake of posterity.
            record.setMessage("An error occurred while closing the Connection "
                    + "object. We are not throwing any "
                    + "Exceptions for this, just noting it here.\n\nMessage: " 
                    + ex.getMessage());
            record.setThrown(ex);
            log.error(record);
        } finally {
            record.setMessage("Disconnect complete. Returning from whence we "
                    + "came...");
            log.exit(record, null);
        }
    }
    
    protected void connect() throws DataStoreException {
        record.setSourceMethodName("connect");
        record.setMessage("Creating connection to the data store");
        log.enter(record);
        
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            String url = "jdbc:hsqldb:" + dbName + ";shutdown=true";
            con = DriverManager.getConnection(url);
            
            record.setMessage("Connection succeeded! Checking the table.");
            log.debug(record);
            
            if ( !checkTable() ) 
                record.setSourceMethodName("connect");
                record.setMessage("Table did not exist. Creating the table...");
                log.debug(record);
                createTable();
        } catch ( DataStoreException ex ) {
            record.setSourceMethodName("connect");
            record.setMessage("Could not initialize the table. Throwing new "
                    + "DataStoreException...");
            record.setThrown(ex);
            log.error(record);
            throw new DataStoreException("Could not initialize the table");
        } catch ( ClassNotFoundException ex ) {
            record.setSourceMethodName("connect");
            record.setMessage("Could not load the driver. Throwing new "
                    + "DataStoreException...");
            record.setThrown(ex);
            log.error(record);
            throw new DataStoreException("Could not load the driver");
        } catch ( SQLException ex ) {
            record.setSourceMethodName("connect");
            record.setMessage("Could not open the data store. Throwing new "
                    + "DataStoreException...");
            record.setThrown(ex);
            log.error(record);
            throw new DataStoreException("Could not open the data store: " 
                    + ex.getMessage());
        } finally {
            record.setSourceMethodName("connect");
            record.setMessage("Connection establishment complete.");
            log.exit(record, null);
        }
    }
    
    protected boolean checkTable() {
        record.setSourceMethodName("checkTable");
        record.setMessage("Checking the data store table.");
        log.enter(record);
        
        boolean ret = false;
        
        try {
            String sql = "SELECT COUNT(*) FROM customers";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            
            record.setMessage("Table has been successfully initialized. "
                    + "Returning true.");
            log.debug(record);
            ret = true;
        } catch ( SQLException ex ) {
            record.setMessage("Could not initialize the table. Returning false");
            record.setThrown(ex);
            log.error(record);
            ret = false;
        } finally {
            record.setMessage("Completed checking the data store table. "
                    + "Performing housekeeping...");
            log.debug(record);
            cleanUp();
            
            record.setSourceMethodName("checkTable");
            record.setMessage("Housekeeping complete. Exiting...");
            log.exit(record, ret);
            return ret;
        }
    }
    
    protected void createTable() throws DataStoreException {
        record.setSourceMethodName("createTable");
        record.setMessage("Attempting to create the data table...");
        log.enter(record);
        
        update( "CREATE TABLE " + tableName + "("
                + "id IDENTITY, "
                + "companName VARCHAR(40) NOT NULL, "
                + "streetAddress VARCHAR(30) NOT NULL, "
                + "suiteNumber VARCHAR(15), "
                + "city VARCHAR(30) NOT NULL, "
                + "state VARCHAR(2) NOT NULL, "
                + "zipCode VARCHAR(10) NOT NULL, "
                + "phoneNumber VARCHAR(14), "
                + "faxNumber VARCHAR(14), "
                + "emailAddress VARCHAR(50), "
                + "contactName VARCHAR(30), "
                + "notes LONGVARCHAR, "
                + "active BOOLEAN DEFAULT=FALSE)");
        
        record.setSourceMethodName("createTable");
        record.setMessage("Returning from whence we came...");
        log.exit(record, null);
    }
    
    protected void update(String sql) throws DataStoreException {
        record.setSourceMethodName("update");
        record.setParameters(new Object[]{sql});
        record.setMessage("Attempting to execute the SQL provided...");
        log.enter(record);
        
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
        } catch ( SQLException ex ) {
            record.setMessage("Could not modify the table. Throwing new "
                    + "DataStoreException...");
            record.setThrown(ex);
            log.error(record);
            throw new DataStoreException("Could not modify the table", ex);
        } finally {
            record.setMessage("Performing housekeeping before we leave...");
            log.debug(record);
            
            cleanUp();
            
            record.setSourceMethodName("update");
            record.setParameters(new Object[]{sql});
            record.setMessage("Housekeeping complete. Returning from whence we "
                    + "came...");
            log.exit(record, null);
        }
    }
    
    protected void cleanUp() {
        record.setSourceMethodName("cleanUp");
        record.setMessage("Performing housekeeping tasks");
        log.enter(record);
        
        try {
            if ( rs != null ) 
                rs.close();
            rs = null;
            
            if ( stmt != null )
                stmt.close();
            stmt = null;
        } catch ( SQLException ex ) {
            // We are going to ignore the exception, however, we are going to 
            //+ write it to the log for the sake of posterity.
            record.setMessage("An error occurred while closing the ResultSet "
                    + "and Statement objects. We are not throwing any "
                    + "Exceptions for this, just noting it here.\n\nMessage: " 
                    + ex.getMessage());
            record.setThrown(ex);
            log.error(record);
        } finally {
            record.setMessage("Housekeeping complete. Returning from whence we "
                    + "came...");
            log.exit(record, null);
        }
    }
    
    /**
     * This is a convenience method to allow the calling procedure to update a
     * field that holds an integer value, without needing to do the conversions
     * prior to storing the value. Conversion is taken care of within this 
     * method.
     * 
     * @param field name of field to update
     * @param value the new value to enter
     * @param id    the id of the record to update
     * @return      `true` on success, `false` otherwise
     * @throws DataStoreException in the event that an error is encountered 
     *                            during the update process
     */
    public boolean updateIntField(String field, int value , int id) 
            throws DataStoreException {
        record.setSourceMethodName("updateIntField");
        record.setParameters(new Object[]{field, value, id});
        record.setMessage("Attempting to update field (" + field + ") "
                + "identified by " + id + "...");
        log.enter(record);
        
        boolean ret = false;
        
        try {
            update("UPDATE " + tableName + " SET " + field + " = " + value 
                    + " WHERE id = " + id);
            
            ret = true;
        } catch ( DataStoreException ex ) {
            record.setSourceMethodName("updateIntField");
            record.setThrown(ex);
            record.setMessage(ex.getMessage());
            log.error(record);
            ret = false;
            
            throw ex;
        } finally {
            record.setSourceMethodName("updateIntField");
            record.setMessage("Performing housekeeping before we leave...");
            log.debug(record);
            
            cleanUp();
            
            record.setSourceMethodName("updateIntField");
            record.setMessage("Returning from whence we came and returning " 
                    + ret + " to the calling procedure");
            log.exit(record, new Object[]{ret});
            return ret;
        }
    }
    
    /**
     * This is a convenience method to allow the calling procedure to update a
     * field that holds an double value, without needing to do the conversions
     * prior to storing the value. Conversion is taken care of within this 
     * method.
     * 
     * @param field name of field to update
     * @param value the new value to enter
     * @param id    the id of the record to update
     * @return      `true` on success, `false` otherwise
     * @throws DataStoreException in the event that an error is encountered 
     *                            during the update process
     */
    public boolean updateDblField(String field, double value, int id) 
            throws DataStoreException {
        record.setSourceMethodName("updateDblField");
        record.setParameters(new Object[]{field, value, id});
        record.setMessage("Attempting to update field (" + field + ") "
                + "identified by " + id + "...");
        log.enter(record);
        
        boolean ret = false;
        
        try {
            update("UPDATE " + tableName + " SET " + field + " = " + value 
                    + " WHERE id = " + id);
            
            ret = true;
        } catch ( DataStoreException ex ) {
            record.setSourceMethodName("updateDblField");
            record.setThrown(ex);
            record.setMessage(ex.getMessage());
            log.error(record);
            ret = false;
            
            throw ex;
        } finally {
            record.setSourceMethodName("updateDblField");
            record.setMessage("Performing housekeeping before we leave...");
            log.debug(record);
            
            cleanUp();
            
            record.setSourceMethodName("updateDblField");
            record.setMessage("Returning from whence we came and returning " 
                    + ret + " to the calling procedure");
            log.exit(record, new Object[]{ret});
            return ret;
        }
    }
    
    /**
     * This is a convenience method to allow the calling procedure to update a
     * field that holds an float value, without needing to do the conversions
     * prior to storing the value. Conversion is taken care of within this 
     * method.
     * 
     * @param field name of field to update
     * @param value the new value to enter
     * @param id    the id of the record to update
     * @return      `true` on success, `false` otherwise
     * @throws DataStoreException in the event that an error is encountered 
     *                            during the update process
     */
    public boolean updateFltFied(String field, float value, int id) 
            throws DataStoreException {
        record.setSourceMethodName("updateFltField");
        record.setParameters(new Object[]{field, value, id});
        record.setMessage("Attempting to update field (" + field + ") "
                + "identified by " + id + "...");
        log.enter(record);
        
        boolean ret = false;
        
        try {
            update("UPDATE " + tableName + " SET " + field + " = " + value 
                    + " WHERE id = " + id);
            
            ret = true;
        } catch ( DataStoreException ex ) {
            record.setSourceMethodName("updateFltField");
            record.setThrown(ex);
            record.setMessage(ex.getMessage());
            log.error(record);
            ret = false;
            
            throw ex;
        } finally {
            record.setSourceMethodName("updateFltField");
            record.setMessage("Performing housekeeping before we leave...");
            log.debug(record);
            
            cleanUp();
            
            record.setSourceMethodName("updateFltField");
            record.setMessage("Returning from whence we came and returning " 
                    + ret + " to the calling procedure");
            log.exit(record, new Object[]{ret});
            return ret;
        }
    }

    /**
     * This is the standard method to update an individual field. This method is
     * useful for updating string values. If you need to update a numerical or
     * boolean field, consider using one of the convenience methods.
     * 
     * @param field name of field to update
     * @param value the new value to enter
     * @param id    the id of the record to update
     * @return      `true` on success, `false` otherwise
     * @throws DataStoreException in the event that an error is encountered 
     *                            during the update process
     */
    public boolean udpateField(String field, String value, int id) 
            throws DataStoreException {
        record.setSourceMethodName("udpateField");
        record.setParameters(new Object[]{field, value, id});
        record.setMessage("Attempting to update field (" + field + ") "
                + "identified by " + id + "...");
        log.enter(record);
        
        boolean ret = false;
        
        try {
            update("UPDATE " + tableName + " SET " + field + " = " + value 
                    + " WHERE id = " + id);
            
            ret = true;
        } catch ( DataStoreException ex ) {
            record.setSourceMethodName("udpateField");
            record.setThrown(ex);
            record.setMessage(ex.getMessage());
            log.error(record);
            ret = false;
            
            throw ex;
        } finally {
            record.setSourceMethodName("udpateField");
            record.setMessage("Performing housekeeping before we leave...");
            log.debug(record);
            
            cleanUp();
            
            record.setSourceMethodName("udpateField");
            record.setMessage("Returning from whence we came and returning " 
                    + ret + " to the calling procedure");
            log.exit(record, new Object[]{ret});
            return ret;
        }
    }

    /**
     * This is a convenience method to allow the calling procedure to update a
     * field that holds an boolean value, without needing to do the conversions
     * prior to storing the value. Conversion is taken care of within this 
     * method.
     * 
     * @param field name of field to update
     * @param value the new value to enter
     * @param id    the id of the record to update
     * @return      `true` on success, `false` otherwise
     * @throws DataStoreException in the event that an error is encountered 
     *                            during the update process
     */
    public boolean updateBoolField(String field, boolean value, int id) 
            throws DataStoreException {
        record.setSourceMethodName("updateBoolField");
        record.setParameters(new Object[]{field, value, id});
        record.setMessage("Attempting to update field (" + field + ") "
                + "identified by " + id + "...");
        log.enter(record);
        
        boolean ret = false;
        
        try {
            update("UPDATE " + tableName + " SET " + field + " = " + value 
                    + " WHERE id = " + id);
            
            ret = true;
        } catch ( DataStoreException ex ) {
            record.setSourceMethodName("updateBoolField");
            record.setThrown(ex);
            record.setMessage(ex.getMessage());
            log.error(record);
            ret = false;
            
            throw ex;
        } finally {
            record.setSourceMethodName("updateBoolField");
            record.setMessage("Performing housekeeping before we leave...");
            log.debug(record);
            
            cleanUp();
            
            record.setSourceMethodName("updateBoolField");
            record.setMessage("Returning from whence we came and returning " 
                    + ret + " to the calling procedure");
            log.exit(record, new Object[]{ret});
            return ret;
        }
    }
    
    /**
     * This is only method provided for updating all fields at once. Since this
     * practice is not that common, it is better practice to use the various
     * methods for updating an individual field, even if your program has one or
     * two fields to update in the same record.
     * <p>
     * The name of the first parameter, `fieldsValues`, tells you how to set up
     * your `HashMap` for sending into this method. The field names need to be
     * set as the keys and their corresponding values need to be set as the
     * values in the map.</p>
     * <dl>
     *  <dt>Example:</dt>
     *  <dd>You have a table named "students", which has the two fields, "id" 
     *      and "name". When setting up your `HashMap`, you would set the 
     *      key/value pairs as `id=value`, `name=value`. This way, you are 
     *      always using the name of the field as the key, and its value as the
     *      value, in the key/value pair.</dd>
     * </dl>
     * 
     * @param fieldsValues a `HashMap` of the field names in your table as the
     *                     keys, with their respective field values as the
     *                     values, in the map.
     * @param id    the id of the record to update
     * @return      `true` on success, `false` otherwise
     * @throws DataStoreException in the event that an error is encountered 
     *                            during the update process
     */
    public boolean updateAllFields(HashMap fieldsValues, int id) 
            throws DataStoreException {
        record.setSourceMethodName("updateAllFields");
        record.setParameters(new Object[]{fieldsValues, id});
        record.setMessage("Attempting to update all fields "
                + "identified by " + id + "...");
        log.enter(record);
        
        boolean ret = false;
        
        try {
            String sql = "UPDATE " + tableName + " SET ";
            
            Iterator it = fieldsValues.entrySet().iterator();
            
            while ( it.hasNext() ) {
                Map.Entry element = (Map.Entry)it.next();
                sql += element.getKey() + " = " + element.getValue() + ", ";
            }
            
            sql += "WHERE id = " + id;
            
            update(sql);
            
            ret = true;
        } catch ( DataStoreException ex ) {
            record.setSourceMethodName("updateAllFields");
            record.setThrown(ex);
            record.setMessage(ex.getMessage());
            log.error(record);
            ret = false;
            
            throw ex;
        } finally {
            record.setSourceMethodName("updateAllFields");
            record.setMessage("Performing housekeeping before we leave...");
            log.debug(record);
            
            cleanUp();
            
            record.setSourceMethodName("updateAllFields");
            record.setMessage("Returning from whence we came and returning " 
                    + ret + " to the calling procedure");
            log.exit(record, new Object[]{ret});
            return ret;
        }
    }
    
    abstract public List<?> query(String fields, String where, String sort) 
            throws DataStoreException;
    
}
