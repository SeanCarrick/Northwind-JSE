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
package com.northwind;

import com.northwind.exceptions.InvalidLoggingLevelException;
import com.northwind.settings.AppProperties;
import com.northwind.settings.VersionCalculator;
import com.northwind.utils.ArgumentParser;
import com.northwind.utils.Logger;
import com.northwind.view.MainWindow;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 */
public class Northwind {
    private static final Logger log = Logger.getInstance();
    private static final LogRecord record;
    private static final AppProperties props = AppProperties.getInstance();
    
    static {
        record = new LogRecord(Level.ALL, "Application Starting");
    }
    
    private Northwind() { /* to allow access from outside the class. */ }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        record.setSourceClassName(Northwind.class.getCanonicalName());
        record.setSourceMethodName("main");
        record.setResourceBundleName("Basic Edition");
        String build = Long.valueOf(System.currentTimeMillis()).toString();
        build = build.substring(build.length() - 9);
        record.setSequenceNumber(Long.valueOf(build));
        record.setLoggerName("0.1.5");
        Object[] mods = new Object[]{ "Base Module", 
                "Customer Management Module"};
        record.setParameters(mods);
        log.welcome(record);
        
        record.setParameters(args);
        log.enter(record);
        
        record.setMessage("Parsing the command-line arguments");
        log.debug(record);
        ArgumentParser parser = new ArgumentParser(args);
        record.setMessage("Checking logging level...");
        log.debug(record);
        int levelToSet = 0;
        String level = "";
        if (parser.isSwitchPresent("--debug") || parser.isSwitchPresent("-d")) {
            props.setProperty("debugging", Boolean.TRUE.toString());
            levelToSet = Logger.DEBUG;
            level = "DEBUG";
        } else {
            props.setProperty("debugging", Boolean.FALSE.toString());
            levelToSet = Logger.CONFIG;
            level = "CONFIG";
        }
        
        record.setMessage("Calculating or getting the application version...");
        record.setSourceClassName(Northwind.class.getName());
        record.setSourceMethodName("main");
        record.setParameters(args);
        log.debug(record);
        VersionCalculator version = new VersionCalculator();
        
        record.setMessage("Setting logging level to: " + level + " (" 
                + levelToSet + ")");
        log.debug(record);
        
        record.setMessage("Application Version: " + props.getVersion());
        log.debug(record);
        
        try {
            log.setLevel(levelToSet);
        } catch (InvalidLoggingLevelException ex) {
            record.setInstant(Instant.now());
            record.setMessage("Exception while setting logging level");
            record.setThrown(ex);
            log.error(record);
        }
        
        String lafName;
        if (parser.isSwitchPresent("--laf") ) {
            lafName = parser.getSwitchValue("--laf");
            
            if ( lafName.toLowerCase().contains("cde") ||
                    lafName.toLowerCase().contains("motif") )
                lafName = "CDE/Motif";
        } else if ( parser.isSwitchPresent("-l") ) {
            lafName = parser.getSwitchValue("-l");
            
            if ( lafName.toLowerCase().contains("cde") ||
                    lafName.toLowerCase().contains("motif") )
                lafName = "CDE/Motif";
        } else {
            lafName = "SYSTEM";
        }
        
        record.setMessage("For the record, we are going to list all installed "
                + "Look and Feels:");
        log.debug(record);
        int x = 0;
        
        for ( javax.swing.UIManager.LookAndFeelInfo info : 
                javax.swing.UIManager.getInstalledLookAndFeels() ) {
            record.setMessage("LaF #" + (++x) + ": " + info.getName() + " [ " 
                    + info.getClassName() + " ]");
            log.debug(record);
        }
        
        record.setMessage("Attempting to set the look and feel.");
        log.config(record);
        if ( !lafName.equals("SYSTEM") ) {
            for (javax.swing.UIManager.LookAndFeelInfo info : 
                    javax.swing.UIManager.getInstalledLookAndFeels()) {
                record.setMessage(info.getName());
                log.debug(record);

                if ( info.getName().equalsIgnoreCase(lafName) )
                    try {
                        record.setMessage("Setting look and feel to " 
                                + info.getName() + ".");
                        log.debug(record);
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                } catch (ClassNotFoundException | 
                        InstantiationException | 
                        IllegalAccessException | 
                        UnsupportedLookAndFeelException ex) {
                    record.setMessage(ex.getLocalizedMessage());
                    record.setThrown(ex);
                    log.error(record);
                }
            }
        } else {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | 
                        InstantiationException | 
                        IllegalAccessException | 
                        UnsupportedLookAndFeelException ex) {
                    record.setMessage(ex.getLocalizedMessage());
                    record.setThrown(ex);
                    log.error(record);
                }
        }
        
        record.setMessage("Creating and showing the MainWindow...");
        MainWindow frame = new MainWindow();
        frame.pack();
        frame.setVisible(true);
        
        record.setMessage("Exiting the startup procedure...");
        log.exit(record, "");
    }
    
}
