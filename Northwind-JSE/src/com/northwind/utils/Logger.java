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
 * 
 * *****************************************************************************
 * *****************************************************************************
 *  Project    :   Northwind-Basic
 *  Class      :   Logger.java
 *  Author     :   Sean Carrick
 *  Created    :   Mar 8, 2020 @ 12:42:02 PM
 *  Modified   :   Mar 8, 2020
 *  
 *  Purpose:
 *  
 *  Revision History:
 *  
 *  WHEN          BY                  REASON
 *  ------------  ------------------- ------------------------------------------
 *  Mar 8, 2020  Sean Carrick        Initial creation.
 *  Mar 21, 2020 Sean Carrick        Added the parameter `modules` to the 
 *                                   `critical` function, so that installed
 *                                   modules may be added to the error log.
 * *****************************************************************************
 */

package com.northwind.utils;

import com.northwind.exceptions.InvalidLoggingLevelException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.logging.LogRecord;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 * 
 * @version 0.1.0
 * @since 0.1.0
 */
public class Logger {
    //<editor-fold defaultstate="collapsed" desc="Public Static Constants">
    /**
     * Debugging: lowest level of logging.
     */
    public static final int DEBUG = 0;
    /**
     * Configuration: for logging configuration settings.
     */
    public static final int CONFIG = 1;
    /**
     * Information: for logging informational messages above the debug and 
     * configuration levels.
     */
    public static final int INFO = 2;
    /**
     * Warning: for logging messages that were given to the user to warn them of
     * inappropriate use or invalid data, etc.
     */
    public static final int WARN = 3;
    /**
     * Error: for logging messages that were caused by recoverable errors in the
     * application.
     */
    public static final int ERROR = 4;
    /**
     * Critical: for logging messages that were caused by critical, 
     * unrecoverable errors in the application that caused the application to 
     * exit abnormally.
     */
    public static final int CRITICAL = 5;
    /**
     * Off: used for any errors in setting up the `Logger` object. The calling
     * application should check to make sure that logging is not turned off
     * before calling a logging event.
     */
    public static final int OFF = 100;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Private Static Constants">
    // The default path for storing the log files.
    private final String FILE_SEPARATOR;
    private final String TEMP_LOG_PATH;
    private final String LOG_PATH;
    private final String ERR_PATH;
                                           
    // Private constants to hold the message header and footer strings.
    private static final String MSG_HDR = "=".repeat(40 - (
            " BEGIN MESSAGE ".length() / 2)) + " B E G I N " + 
            "=".repeat((40 - (" BEGIN ".length() / 2)) - 1) + "\n";
    private static final String MSG_FTR = "\n" + "-".repeat(40 - (
            " END MESSAGE ".length() / 2)) + "  E N D " + 
            "-".repeat(40 - (" END ".length() / 2)) + "\n";
    private static final Logger logger = new Logger();
    //</editor-fold>

    {
        if ( System.getProperty("os.name").toLowerCase().contains("windows") ) {
            FILE_SEPARATOR = "\\";
        } else {
            FILE_SEPARATOR = "/";
        }
        
        LOG_PATH = System.getProperty("user.home") 
            + FILE_SEPARATOR + ".northwind" 
            + FILE_SEPARATOR + "var" 
            + FILE_SEPARATOR + "log" 
            + FILE_SEPARATOR + "application.log";    
        TEMP_LOG_PATH = System.getProperty("user.home") 
            + FILE_SEPARATOR + "pekinsoft.log";    
        ERR_PATH = System.getProperty("user.home") 
            + FILE_SEPARATOR + ".northwind"
            + FILE_SEPARATOR + "var" 
            + FILE_SEPARATOR + "err" 
            + FILE_SEPARATOR;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Private Member Fields">
    private FileWriter log; // The file to which messages will be written.
    private FileWriter err; // The file to which errors will be written.
    private boolean append; // Whether or not to append to existing file.
    private int level;      // Level at which to log messages.
    private LogRecord record;   // Record for logging messages.
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructor(s)">
    /**
     * Creates a default `Logger` object that places the log file in the user's
     * home folder. This log will log at the informational level and be appended
     * to for future runs of the application.
     */
    private Logger() {
        this ( System.getProperty("user.home") 
            + System.getProperty("file.separator") + ".northwind" 
            + System.getProperty("file.separator") + "var" 
            + System.getProperty("file.separator") + "logs" 
            + System.getProperty("file.separator") + "application.log");
    }
    
    /**
     * Creates a `Logger` object that places the log file in the path specified
     * by the calling application, logs informational or higher messages and is
     * appended to on future runs of the application.
     * 
     * @param path The path to the folder where the log file is to be placed.
     */
    private Logger(String path) {
        this ( path, INFO);
    }
    
    /**
     * Creates a `Logger` object that places the log file in the path specified
     * by the calling application, logs all messages at the provided logging
     * level or higher and is appended to on future runs of the application.
     * 
     * The valid levels are as follows:
     * <ul>
     *  <li>DEBUG: Lowest level of logging. All messages sent to the `Logger`
     *      will be written to the log file.</li>
     *  <li>CONFIG: Stores configuration messages to the log file.</li>
     *  <li>INFO:   Stores informational messages to the log file.</li>
     *  <li>WARN:   Stores messages to the log file that warned users of
     *      inappropriate use, bad data, invalid settings, etc.</li>
     *  <li>ERROR:  Stores messages from recoverable errors to the log file.</li>
     *  <li>CRITICAL: Stores messages from non-recoverable errors to the log
     *      file to keep track of errors and/or bugs that cause the application
     *      to fail and exit abnormally.</li>
     * </ul>
     * 
     * @param path  The path to the folder where the log file is to be placed.
     * @param level The minimum level at which to write messages to the log.
     */
    public Logger(String path, int level) {
        this ( path, level, true );
    }
    
    /**
     * Creates a `Logger` object that places the log file in the path specified
     * by the calling application, logs all messages at the provided logging
     * level or higher and has its ability to append messages from future runs
     * of the application set by the calling application.
     * 
     * The valid levels are as follows:
     * <ul>
     *  <li>DEBUG: Lowest level of logging. All messages sent to the `Logger`
     *      will be written to the log file.</li>
     *  <li>CONFIG: Stores configuration messages to the log file.</li>
     *  <li>INFO:   Stores informational messages to the log file.</li>
     *  <li>WARN:   Stores messages to the log file that warned users of
     *      inappropriate use, bad data, invalid settings, etc.</li>
     *  <li>ERROR:  Stores messages from recoverable errors to the log file.</li>
     *  <li>CRITICAL: Stores messages from non-recoverable errors to the log
     *      file to keep track of errors and/or bugs that cause the application
     *      to fail and exit abnormally.</li>
     * </ul>
     * 
     * @param path   The path to the folder where the log file is to be placed.
     * @param level  The minimum level at which to write messages to the log.
     * @param append Whether to append messages from future application runs to
     *               the same log file, or to create a new log file at the time
     *               of this application run.
     */
    public Logger(String path, int level, boolean append) {
        // The first thing we are going to do is to set our log field to null.
        this.log = null;    //+ In this way, it is "initialized" even though it
        //+ initialized to nothing (null), it will prevent some design-time 
        //+ errors from showing up.
        
        // Set our logging level to the level provided.
        this.level = level;
        
        // Set our appending ability to the appending ability provided.
        this.append = append;
        
        // In order to ACTUALLY initialize our log field, we will need to
        //+ enclose it in a try...catch() block. We will also need to do this
        //+ anytime we use the log field.
        try {            
            this.log = new FileWriter(path);
        } catch (IOException ex) {
            // We are going to simply show a message box to the user explaining
            //+ that logging setup failed and then we will turn off logging.
            String ttl = "Logging Setup Failure";
            
            MessageBox.showError(ex, ttl);
            
            // Now that we've told the calling application that there was an 
            //+ error, we can turn logging off.
            this.level = OFF;
        }
    }
    //</editor-fold>

    public static Logger getInstance() {
        return logger;
    }
    
    /**
     * This is a convenience method to allow an application to log a 
     * configuration level message without having to go through the rigamarole
     * of passing the level every time a configuration needs to be written.
     * 
     * @param record The `LogRecord` to use for this log entry.
     */
    public void config(LogRecord record) {
        // We need to try to log the message, however, we will only do so if 
        //+ logging is not turned off and level is set to config or higher.
        if ( this.level != OFF && this.level >= CONFIG || this.level == DEBUG) {
            // We're good to log the message to the log file.
            try {
                log.write(StringUtils.wrapAt("CONFIG: " + record.getMessage(),
                        80) + "\n");
                
                // Now, flush the buffer to be sure the data was written.
                log.flush();
            } catch ( IOException ex ) {
                // Let the user know that the message was not written.
                String ttl = "I/O Error: Entry Not Written";
                MessageBox.showError(ex, ttl);
            }
        }
    }
    
    /**
     * Provides the calling application with a method to close the log file
     * prior to the application exiting.
     */
    public void close() {
        // We need to try to close the log, however, we will only do so if 
        //+ logging is not turned off.
        if ( this.level != OFF && this.level >= CONFIG ) {
            // We're good to log the message to the log file.
            try {
                log.flush(); // First, flush it to be sure all data is written.
                log.close(); // Then close the file.
            } catch ( IOException ex ) {
                // Let the user know that the message was not written.
                String ttl = "I/O Error: Entry Not Written";
                MessageBox.showError(ex, ttl);
            }
        }
    }
    
    /**
     * Logs a system critical error to the log file just before the application
     * exits.This should only be used for unrecoverable errors in the program.
     * All other errors should be logged through the `error` method.<p>
     * The `extraData` parameter should contain information pertinent to the
     * user within the context of your application. The `error` logging method
     * places system and Java information into the message by default. All of
     * this extraneous information should aid the software designers, programmers
     * and engineers to be able to track down the error to a specific cause,
     * thereby allowing them to correct the application easier and quicker.</p>
     * <p>
     * When setting up your `LogRecord` for a call to this method, follow these
     * guidelines:</p>
     * 
     * <dl>
     *  <dt>`LogRecord.setSourceClassName()`</dt>
     *  <dd>This is used for exactly what the method says, the source class name
     *      where the `Exception` was thrown.</dd>
     *  <dt>`LogRecord.setSourceMethodName()`</dt>
     *  <dd>This is, likewise, used for what the method says, the source method
     *      name where the `Exception` was thrown</dd>
     *  <dt>`LogRecord.setThrown()`</dt>
     *  <dd>Again, use this for exactly what the method says, store the actual
     *      `Exception` that was thrown</dd>
     *  <dt>`LogRecord.setMessage()`</dt>
     *  <dd>Use this method to store the package in which the `Exception` was
     *      thrown, e.g., "com.myapp.mypkg"</dd>
     *  <dt>`LogRecord.setResourceBundle(String)`</dt>
     *  <dd>Use this method to pass the edition of the application that is being
     *      tested or run: i.e., Basic Edition, Corporate Edition, Enterprise
     *      Edition</dd>
     *  <dt>`LogRecord.setSequenceNumber()`</dt>
     *  <dd>Use this method to provide the build number that is being run at the
     *      time of the `Exception` being thrown</dd>
     *  <dt>`LogRecord.setLoggerName()`</dt>
     *  <dd>Use this method to provide the version that is being run at the time
     *      of the `Exception`, i.e. "1.2.19", "0.2.5", etc.</dd>
     *  <dt>`Logger.setParameters(Object[])`</dt>
     *  <dd>Use this method to provide a list of the installed modules when the
     *      exception was thrown</dd>
     * </dl>
     * 
     * @param record    A `LogRecord` containing all pertinent information as
     *                  described above.
     */
    public void critical(LogRecord record) {
        StringBuilder src = new StringBuilder();
        String rule = "-".repeat(80);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date now = new Date();
        String timeStamp = sdf.format(now);
        
        src.append("Error Location: ");
        src.append(record.getMessage());
        src.append(".");
        src.append(record.getSourceClassName().substring(
                record.getSourceClassName().lastIndexOf(".")+1));
        src.append(".");
        src.append(record.getSourceMethodName());
        src.append("\n\n");
        src.append(rule);
        src.append("\n\n");
        src.append("Exception:  ");
        src.append(record.getThrown().getClass().getName());
        src.append("\nMessage:     ");
        src.append(record.getThrown().getMessage());
        src.append("\nStacktrace:\n");
        
        StackTraceElement[] stack = record.getThrown().getStackTrace();
        
        for ( StackTraceElement element : stack ) {
            src.append(element.toString());
            src.append("\n");
        }
        
        src.append(rule);
        src.append("\n\n");
        src.append(" ".repeat(21));
        src.append("N O R T H W I N D   I N F O R M A T I O N");
        src.append("\n\n");
        src.append("Edition...............");
        src.append(record.getResourceBundleName());
        src.append("\nVersion...............");
        src.append(record.getLoggerName());
        src.append("\nBuild.................");
        src.append(record.getSequenceNumber());
        src.append("\nInstalled Modules:");
        
        for (Object module : record.getParameters()) {
            src.append("\n\t");
            src.append(module.toString());
        }
        
        src.append("\n\n");
        src.append(" ".repeat(24));
        src.append("S Y S T E M   I N F O R M A T I O N");
        src.append("\n\n");
        src.append("OS.................");
        src.append(System.getProperty("os.name"));
        src.append("\nOS Version.........");
        src.append(System.getProperty("os.version"));
        src.append("\nArchitecture.......");
        src.append(System.getProperty("os.arch"));
        src.append("\n\n");
        src.append(" ".repeat(26));
        src.append("J A V A   I N F O R M A T I O N");
        src.append("\n\n");
        src.append("Java Virtual Machine.....");
        src.append(System.getProperty("java.vm.name"));
        src.append("\nJava VM Version..........");
        src.append(System.getProperty("java.vm.version"));
        src.append("\nJava Runtime Name........");
        src.append(System.getProperty("java.runtime.name"));
        src.append("\nJava Runtime Version.....");
        src.append(System.getProperty("java.runtime.version"));
        src.append("\nJava Specification.......");
        src.append(System.getProperty("java.specification.name"));
        src.append("\nJava Spec. Version.......");
        src.append(System.getProperty("java.specification.version"));
        src.append("\nJava Vendor..............");
        src.append(System.getProperty("java.vendor"));
        src.append("\nJava Version.............");
        src.append(System.getProperty("java.version"));
        src.append("\nJava Version Date........");
        src.append(System.getProperty("java.version.date"));
        src.append("\nJava Class Path..........");
        src.append(System.getProperty("java.class.path"));
        src.append("\nJava Class Version.......");
        src.append(System.getProperty("java.class.version"));
        src.append("\nJava Library Path........");
        src.append(System.getProperty("java.library.path"));
        src.append("\n\n");
        src.append(" ".repeat(26));
        src.append("\n\n");
        src.append("User Country.............");
        src.append(System.getProperty("user.country"));
        src.append("\nUser Language............");
        src.append(System.getProperty("user.language"));
        src.append("\n\n~~~ END OF ERROR REPORT ~~~");
        
        // Make sure of the existence of the error log path.
        File errPath = new File(ERR_PATH);
        if ( !errPath.exists() ) {
            errPath.mkdirs();
        }
        
        // We need to try to log the message, however, we will only do so if 
        //+ logging is not turned off.
//        if ( this.level != OFF ) {
            // We're good to log the message to the log file.
            try {
                this.err = new FileWriter(ERR_PATH + 
                        record.getSourceClassName().substring(
                                record.getSourceClassName().lastIndexOf(".") 
                                        + 1) + "_" + 
                        "class_" + timeStamp + ".err");
        
//                err.write(MSG_HDR);
                err.write(src.toString());
//                err.write(MSG_FTR);
                
                // Now, flush the buffer to be sure the data was written.
                err.flush();
                src = new StringBuilder();
                src.append(MSG_HDR);
                src.append("See error log: ");
                src.append(StringUtils.wrapAt(ERR_PATH, 80));
                src.append(record.getSourceClassName());
                src.append("_");
                src.append(timeStamp);
                src.append(".err\n\n");
                
                log.write(src.toString());
                
                // Flush the log.
                log.flush();
            } catch ( IOException e ) {
                // Let the user know that the message was not written.
                String ttl = "I/O Error: Entry Not Written";
                MessageBox.showError(e, ttl);
            }
//        }
    }
    
    /**
     * If the `Logger` is currently enabled for debugging messages, then the
     * given message is written out to the log file.
     * 
     * @param record The `LogRecord` to use for this log entry.
     */
    public void debug(LogRecord record) {
        // We need to try to log the message, however, we will only do so if 
        //+ logging is not turned off and the level is set to debugging or
        //+ higher.
        if ( this.level != OFF && this.level >= DEBUG ) {
            // We're good to log the message to the log file.
            try {
                log.write(StringUtils.wrapAt("DEBUG: " + record.getMessage(), 
                        80) + "\n");

                // Now, flush the buffer to be sure the data was written.
                log.flush();
            } catch ( IOException ex ) {
                // Let the user know that the message was not written.
                String ttl = "I/O Error: Entry Not Written";
                MessageBox.showError(ex, ttl);
            }
        }
    }
    
//    /**
//     * As long as the `Logger` is not `OFF`, then an entry message to a method
//     * with no parameters will be written to the file, regardless of logging
//     * level.
//     * 
//     * @param sourceClass   The class the method being entered belongs to.
//     * @param sourceMethod  The name of the method being entered.
//     */
//    public void enter(String sourceClass, String sourceMethod) {
//        // We need to try to log the message, however, we will only do so if 
//        //+ logging is not turned off.
//        if ( this.level != OFF ) {
//            // We're good to log the message to the log file.
//            try {
//                log.write(MSG_HDR);
//                log.write(" -> " + LocalDateTime.now().toString() + "\n");
//                log.write(StringUtils.wrapAt("Entering: " + sourceClass + "." 
//                        + sourceMethod, 80) + "\n");
//                
//                // Now, flush the buffer to be sure the data was written.
//                log.flush();
//            } catch ( IOException ex ) {
//                // Let the user know that the message was not written.
//                String ttl = "I/O Error: Entry Not Written";
//                MessageBox.showError(ex, ttl);
//            }
//        }
//    }
    
    /**
     * As long as the `Logger` is not `OFF`, then an entry message to a method
     * with one parameter will be written to the file, regardless of logging
     * level.
     * 
     * @param record The `LogRecord` to use for this log entry.
     */
    public void enter(LogRecord record) {
        // We need to build our source before we write to the file.
        String src = record.getSourceClassName() + "." 
                + record.getSourceMethodName() + "( " 
                + record.getParameters() + " )";
        
        // We need to try to log the message, however, we will only do so if 
        //+ logging is not turned off.
        if ( this.level != OFF ) {
            // We're good to log the message to the log file.
            try {
                log.write(MSG_HDR);
                log.write(" -> " + LocalDateTime.now().toString() + "\n");
                log.write(StringUtils.wrapAt("Entering: " + src, 80) + "\n");
                
                // Now, flush the buffer to be sure the data was written.
                log.flush();
            } catch ( IOException ex ) {
                // Let the user know that the message was not written.
                String ttl = "I/O Error: Entry Not Written";
                MessageBox.showError(ex, ttl);
            }
        }
    }
    
//    /**
//     * As long as the `Logger` is not `OFF`, then an entry message to a method
//     * with multiple parameters will be written to the file, regardless of 
//     * logging level.
//     * 
//     * @param sourceClass   The class the method being entered belongs to.
//     * @param sourceMethod  The name of the method being entered.
//     * @param params        An array of the parameters passed to the method.
//     */
//    public void enter(String sourceClass, String sourceMethod, Object[] params) {
//        // We need to build our source before we write to the file.
//        String src = sourceClass + "." + sourceMethod + "( ";
//        String pars = "";
//        
//        for ( int idx = 0; idx < params.length; idx++ ) {
//            pars += params[idx];
//            if ( idx < params.length ) 
//                pars += "\n" + " ".repeat(src.length());
//            else
//                pars += " )";
//        }
//        
//        // Add our params to the source.
//        src += pars;
//        
//        // We need to try to log the message, however, we will only do so if 
//        //+ logging is not turned off.
//        if ( this.level != OFF ) {
//            // We're good to log the message to the log file.
//            try {
//                log.write(MSG_HDR);
//                log.write(" -> " + LocalDateTime.now().toString() + "\n");
//                log.write(StringUtils.wrapAt("Entering: " + src, 80) + "\n");
//                
//                // Now, flush the buffer to be sure the data was written.
//                log.flush();
//            } catch ( IOException ex ) {
//                // Let the user know that the message was not written.
//                String ttl = "I/O Error: Entry Not Written";
//                MessageBox.showError(ex, ttl);
//            }
//        }
//    }
    
    /**
     * Logs a non-critical error to the log file, typically, when it is thrown
     * and just before the program recovers from it. This should only be used 
     * for recoverable errors in the program. Any other errors, which are 
     * unrecoverable, should be logged through the `critical` method, just 
     * before the application exits.
     * <p>
     * The `extraData` parameter should contain information pertinent to the
     * user within the context of your application. The `error` logging method
     * places system and Java information into the message by default. All of
     * this extraneous information should aid the software designers, programmers
     * and engineers to be able to track down the error to a specific cause,
     * thereby allowing them to correct the application easier and quicker.</p>
     * 
     * @param record The `LogRecord` to use for this log entry.
     */
    public void error(LogRecord record) {
        // We need to create our message with the Exception and extra data that
        //+ has been provided.
        String src = "#".repeat(80) + "\n";
        src += "#".repeat(40 - (" E R R O R ".length() / 2)) + " E R R O R ";
        src += "#".repeat(39 - (" E R R O R ".length() / 2)) + "\n";
        src += "Message: " + record.getThrown().getMessage() + "\n";
        if ( record.getThrown().getCause() != null )
            src += "Source: " + record.getThrown().getCause().toString() + "\n";
        src += record.getMessage() + "\n\nStacktrace:\n";
        
        StackTraceElement[] stack = record.getThrown().getStackTrace();
        
        for ( StackTraceElement element : stack ) {
            src += element.toString() + "\n";
        }
        
        src += "#".repeat(40 - (" E N D   O F   E R R O R ".length() / 2));
        src += " E N D   O F   E R R O R ";
        src += "#".repeat(39 - (" E N D   O F   E R R O R ".length() / 2)) + "\n\n";
        src += " ".repeat(40 - ("USER INFORMATION".length() / 2));
        src += "USER INFORMATION"+  "\n\n";
        src += record.getMessage() + "\n";
        src += " ".repeat(40 - ("SYSTEM INFORMATION".length() / 2)) + "\n\n";
        src += "SYSTEM INFORMATION" + "\n\n";
        src += "OS\t\t" + System.getProperty("os.name") + "\n";
        src += "OS Version:\t" + System.getProperty("os.version") + "\n";
        src += "Architecture:\t" + System.getProperty("os.arch") + "\n\n";
        src += " ".repeat(40 - ("JAVA INFORMATION".length() / 2)) + "\n\n";
        src += "JAVA INFORMATION" + "\n\n";
        src += "Java Virtual Machine: " + System.getProperty("java.vm.name") + "\n";
        src += "Java VM Version:\t" + System.getProperty("java.vm.version") + "\n";
        src += "Java Runtime:\t" + System.getProperty("java.runtime.name") + "\n";
        src += "Java Runtime Version: " + System.getProperty("java.runtime.version") + "\n";
        src += "Java Specification:\t" + System.getProperty("java.specification.name") + "\n";
        src += "Java Spec. Version:\t" + System.getProperty("java.specification.version") + "\n";
        src += "\n";
        src += "JDK Module Path:\t" + System.getProperty("jdk.module.path") + "\n";
        src += "\n";
        src += "Java Library Path:\t" + System.getProperty("java.library.path") + "\n";
        src += "\n -> " + LocalDateTime.now().toString();
        
        // We need to try to log the message, however, we will only do so if 
        //+ logging is not turned off.
        if ( this.level != OFF ) {
            // We're good to log the message to the log file.
            try {
                log.write(MSG_HDR);
                log.write(src);
                log.write(MSG_FTR);
                
                // Now, flush the buffer to be sure the data was written.
                log.flush();
            } catch ( IOException e ) {
                // Let the user know that the message was not written.
                String ttl = "I/O Error: Entry Not Written";
                MessageBox.showError(e, ttl);
            }
        }
    }
    
//    /**
//     * As long as `Logging` is not `OFF`, then a message will be written to the
//     * log file whenever control passes back out of the method that has no
//     * return value, nor parameters.
//     * 
//     * @param sourceClass   The class to which the method being exited belongs.
//     * @param sourceMethod  The method being exited.
//     */
//    public void exit(String sourceClass, String sourceMethod) {
//        // We need to try to log the message, however, we will only do so if 
//        //+ logging is not turned off.
//        if ( this.level != OFF ) {
//            // We're good to log the message to the log file.
//            try {
//                log.write(StringUtils.wrapAt("Exiting: " + sourceClass + "." 
//                        + sourceMethod, 80));
//                log.write("\n -> " + LocalDateTime.now().toString());
//                log.write(MSG_FTR);
//                
//                // Now, flush the buffer to be sure the data was written.
//                log.flush();
//            } catch ( IOException ex ) {
//                // Let the user know that the message was not written.
//                String ttl = "I/O Error: Entry Not Written";
//                MessageBox.showError(ex, ttl);
//            }
//        }
//    }
    
    /**
     * As long as `Logging` is not `OFF`, then a message will be written to the
     * log file whenever control passes back out of the method that has a return
     * value, but no parameters.
     * 
     * @param record The `LogRecord` to use for this log entry.
     * @param returnValue   The name of the return variable.
     */
    public void exit(LogRecord record, Object returnValue) {
        // We need to build up our string to print.
        String src = record.getSourceClassName() + "." 
                + record.getSourceMethodName() 
                + " :: " + returnValue;
        
        // We need to try to log the message, however, we will only do so if 
        //+ logging is not turned off.
        if ( this.level != OFF ) {
            // We're good to log the message to the log file.
            try {
                log.write(StringUtils.wrapAt("Exiting: " + src, 80));
                log.write("\n -> " + LocalDateTime.now().toString());
                log.write(MSG_FTR);
                
                // Now, flush the buffer to be sure the data was written.
                log.flush();
            } catch ( IOException ex ) {
                // Let the user know that the message was not written.
                String ttl = "I/O Error: Entry Not Written";
                MessageBox.showError(ex, ttl);
            }
        }
    }
    
//    /**
//     * As long as `Logging` is not `OFF`, then a message will be written to the
//     * log file whenever control passes back out of the method that has a return
//     * value and a single parameter.
//     * 
//     * @param sourceClass   The class to which the method being exited belongs.
//     * @param sourceMethod  The name of the method being exited.
//     * @param param         The name of the parameter passed into the method.
//     * @param returnValue   The name of the return variable.
//     */
//    public void exit(String sourceClass, String sourceMethod, Object param,
//                     Object returnValue) {
//        // We need to build up our string to print.
//        String src = sourceClass + "." + sourceMethod  + "(" + param;
//        src += ") :: " + returnValue;
//        
//        // We need to try to log the message, however, we will only do so if 
//        //+ logging is not turned off.
//        if ( this.level != OFF ) {
//            // We're good to log the message to the log file.
//            try {
//                log.write(StringUtils.wrapAt("Exiting: " + src, 80));
//                log.write("\n -> " + LocalDateTime.now().toString());
//                log.write(MSG_FTR);
//                
//                // Now, flush the buffer to be sure the data was written.
//                log.flush();
//            } catch ( IOException ex ) {
//                // Let the user know that the message was not written.
//                String ttl = "I/O Error: Entry Not Written";
//                MessageBox.showError(ex, ttl);
//            }
//        }
//    }
    
    
//    public void exit(String sourceClass, String sourceMethod, Object[] params, 
//                     Object returnValue) {
//        // We need to build our source before we write to the file.
//        String src = sourceClass + "." + sourceMethod + "( ";
//        String pars = "";
//        
//        for ( int idx = 0; idx < params.length; idx++ ) {
//            pars += params[idx];
//            if ( idx < params.length ) 
//                pars += "\n" + " ".repeat(src.length());
//            else
//                pars += " )";
//        }
//        
//        // Add our params to the source.
//        src += pars + " :: " + returnValue;
//        
//        // We need to try to log the message, however, we will only do so if 
//        //+ logging is not turned off.
//        if ( this.level != OFF ) {
//            // We're good to log the message to the log file.
//            try {
//                log.write(StringUtils.wrapAt("Exiting: " + src, 80));
//                log.write("\n -> " + LocalDateTime.now().toString());
//                log.write(MSG_FTR);
//                
//                // Now, flush the buffer to be sure the data was written.
//                log.flush();
//            } catch ( IOException ex ) {
//                // Let the user know that the message was not written.
//                String ttl = "I/O Error: Entry Not Written";
//                MessageBox.showError(ex, ttl);
//            }
//        }
//    }
    
    /**
     * Provides a method for the calling application to determine the `Logger`'s
     * currently set logging level. Though not needed by a calling application,
     * it's always a good practice to provide a method of checking a setting of
     * one of the objects that a program uses, in case it IS ever needed.
     * 
     * @return The currently set logging level.
     */
    public int getLevel() {
        return this.level;
    }
    
    /**
     * Provides a method for retrieving the `LogRecord` that is currently stored
     * in the `Logger`, if any.
     * 
     * @return 
     */
    public LogRecord getRecord() {
        return this.record; 
    }
    
    /**
     * Logs a non-critical error to the log file, typically, when it is thrown
     * and just before the program handles it. This should only be used 
     * for handled errors in the program. Any other errors, which are 
     * unrecoverable, should be logged through the `critical` method, just 
     * before the application exits.
     * <p>
     * The `extraData` parameter should contain information pertinent to the
     * user within the context of your application..</p>
     * 
     * @param record The `LogRecord` to use for this log entry.
     */
    public void handledError(LogRecord record) {
        // We need to create our message with the Exception and extra data that
        //+ has been provided.
        String src = "#".repeat(80) + "\n";
        src += "#".repeat(40 - (" E R R O R ".length() / 2)) + " E R R O R ";
        src += "#".repeat(39 - (" E R R O R ".length() / 2)) + "\n";
        src += "Message: " + record.getThrown().getMessage() + "\n";
        if ( record.getThrown().getCause() != null )
            src += "Source: " + record.getThrown().getCause().toString() + "\n";
        src += " ".repeat(40 - ("Extra Data".length() / 2));
        src += "EXTRA DATA"+  "\n\n";
        src += record.getMessage() + "\n";
        src += "\nStacktrace:\n";
        
        StackTraceElement[] stack = record.getThrown().getStackTrace();
        
        for ( StackTraceElement element : stack ) {
            src += element.toString() + "\n";
        }
        
        src += "#".repeat(40 - (" E N D   O F   E R R O R ".length() / 2));
        src += " E N D   O F   E R R O R ";
        src += "#".repeat(39 - (" E N D   O F   E R R O R ".length() / 2));
        src += "\n -> " + LocalDateTime.now().toString();
        
        // We need to try to log the message, however, we will only do so if 
        //+ logging is not turned off.
        if ( this.level != OFF ) {
            // We're good to log the message to the log file.
            try {
                log.write(MSG_HDR);
                log.write(src);
                log.write(MSG_FTR);
                
                // Now, flush the buffer to be sure the data was written.
                log.flush();
            } catch ( IOException e ) {
                // Let the user know that the message was not written.
                String ttl = "I/O Error: Entry Not Written";
                MessageBox.showError(e, ttl);
            }
        }
    }
    
    /**
     * Provides a method of logging informational messages to the log file. For
     * this message to be logged, the `Logger` must have been properly 
     * established (NOT in the `OFF` state) and the level must be set at `INFO`,
     * `CONFIG` or `DEBUG`.
     * 
     * @param record The `LogRecord` to use for this log entry.
     */
    public void info(LogRecord record) {
        // We need to try to log the message, however, we will only do so if 
        //+ logging is not turned off and the level is set to info or higher.
        if ( this.level != OFF && this.level >= INFO || this.level == DEBUG ) {
            // We're good to log the message to the log file.
            try {
                log.write(StringUtils.wrapAt("INFO: " + record.getMessage(), 
                        80) + "\n");
                
                // Now, flush the buffer to be sure the data was written.
                log.flush();
            } catch ( IOException ex ) {
                // Let the user know that the message was not written.
                String ttl = "I/O Error: Entry Not Written";
                MessageBox.showError(ex, ttl);
            }
        }
    }
    
    /**
     * Provides a method of storing a `LogRecord` in the `Logger` for later use,
     * either in the same `Class` or `Method`, or another.
     * 
     * @param record 
     */
    public void setRecord (LogRecord record) {
        this.record = record;
    }
    
    /**
     * Set the log level specifying which message levels will be logged by this 
     * `Logger`. Message levels lower than this value will be discarded. The 
     * level value `Level.OFF` can be used to turn off logging altogether.
     * <p>
     * If the new level is `null`, it means that this `Logger` will turn itself
     * to `Level.OFF` to avoid any `NullPointerExceptions`.
     * 
     * @param logLevel
     * @throws InvalidLoggingLevelException 
     */
    public void setLevel(int logLevel) throws InvalidLoggingLevelException {
        // Validate the data before we set it to our field.
        switch ( logLevel ) {
            case DEBUG:
            case CONFIG:
            case INFO:
            case WARN:
            case ERROR:
            case CRITICAL:
                this.level = logLevel;
                break;
            case OFF:
            default:
                this.level = OFF;
                break;
        }
    }
    
    /**
     * Provides a method of logging warning messages to the log file. For this
     * message to be logged, the `Logger` must have been properly established
     * (NOT in the `OFF` state) and the level must be set at `WARN` or 
     * `CRITICAL`.
     * <p>
     * A good practice for this type of message is to place user, system and
     * java information into the message, as well as the information that you
     * were going to display. This will provide a solid base of knowledge for
     * tracking down the reason for the warning.</p>
     * 
     * @param record The `LogRecord` to use for this log entry.
     */
    public void warning(LogRecord record) {
        // We need to try to log the message, however, we will only do so if 
        //+ logging is not turned off and the level is set to warning or higher.
        if ( this.level != OFF && this.level >= WARN || this.level == DEBUG ) {
            // We're good to log the message to the log file.
            try {
                log.write(StringUtils.wrapAt("WARNING: " + record.getMessage(),
                        80) + "\n");
                
                // Now, flush the buffer to be sure the data was written.
                log.flush();
            } catch ( IOException ex ) {
                // Let the user know that the message was not written.
                String ttl = "I/O Error: Entry Not Written";
                MessageBox.showError(ex, ttl);
            }
        }
    }
}
