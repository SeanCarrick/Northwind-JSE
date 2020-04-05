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
package com.northwind.api.db;

import com.northwind.exceptions.DataStoreException;
import com.northwind.settings.AppProperties;
import com.northwind.utils.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 */
public class DbConnection {
    
    private Logger log;
    private LogRecord record;
    private AppProperties props;
    private Connection con;
    
    public DbConnection() {
        log = Logger.getInstance();
        props = AppProperties.getInstance();
        
        Level lvl;
        if ( props.getPropertyAsBoolean("debugging", "true") )
            lvl = Level.FINEST;
        else
            lvl = Level.INFO;
        
        record = new LogRecord(lvl, "Configuring the data store "
                + "connection.");
        
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
    public Connection reconnect(String db) throws DataStoreException {
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
            db = props.getDbName();
        }
            
        props.setProperty("app.last.db", db);
        
        Connection conn = null;
        
        record.setMessage("Now that we have a valid data store table name, we "
                + "can attempt to connect to it.");
        log.debug(record);
        conn = connect();
        
        record.setSourceMethodName("reconnect");
        record.setMessage("Connection attempt complete. Returning from whence "
                + "we came...");
        log.exit(record, conn);
        
        return conn;
    }
    
    private void disconnect() {
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
    
    private Connection connect() throws DataStoreException {
        record.setSourceMethodName("connect");
        record.setMessage("Creating connection to the data store");
        log.enter(record);
        
        String dbURL = props.getDbUrl();
        String db = props.getDbName();
        String dbOpts = props.getDbOptions();
        
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            String url = dbURL + db + dbOpts;
            con = DriverManager.getConnection(url);
            
            record.setMessage("Connection succeeded! Checking the table.");
            log.debug(record);
        } catch ( ClassNotFoundException ex ) {
            record.setSourceMethodName("connect");
            record.setMessage("Could not load the driver. Throwing new "
                    + "DataStoreException...");
            record.setThrown(ex);
            log.error(record);
            con = null;
            throw new DataStoreException("Could not load the driver");
        } catch ( SQLException ex ) {
            record.setSourceMethodName("connect");
            record.setMessage("Could not open the data store. Throwing new "
                    + "DataStoreException...");
            record.setThrown(ex);
            log.error(record);
            con = null;
            throw new DataStoreException("Could not open the data store: " 
                    + ex.getMessage());
        } finally {
            record.setSourceMethodName("connect");
            record.setMessage("Connection establishment complete.");
            log.exit(record, con);
            return con;
        }
    }
}
