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
import com.northwind.utils.ArgumentParser;
import com.northwind.utils.Logger;
import com.northwind.view.MainWindow;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 */
public class Northwind {
    private static final Logger log = Logger.getInstance();
    private static final LogRecord record;
    
    static {
        record = new LogRecord(Level.ALL, "Application Starting");
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        record.setSourceClassName(Northwind.class.getCanonicalName());
        record.setSourceMethodName("main");
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
            levelToSet = Logger.DEBUG;
            level = "DEBUG";
        } else {
            levelToSet = Logger.CONFIG;
            level = "CONFIG";
        }
        
        record.setMessage("Setting logging level to: " + level + " (" 
                + levelToSet + ")");
        log.debug(record);
        
        try {
            log.setLevel(levelToSet);
        } catch (InvalidLoggingLevelException ex) {
            record.setInstant(Instant.now());
            record.setMessage("Exception while setting logging level");
            record.setThrown(ex);
            log.error(record);
        }
        
        record.setMessage("Creating and showing the MainWindow...");
        MainWindow frame = new MainWindow();
        frame.pack();
        frame.setVisible(true);
        
        record.setMessage("Exiting the startup procedure...");
        log.exit(record, "");
    }
    
}
