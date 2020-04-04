/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.northwind.settings;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 */
public class AppPreferences {
    
    private static Preferences prefs;
    private static AppPreferences appPrefs = new AppPreferences();
    
    private AppPreferences() {
        prefs = Preferences.userNodeForPackage(AppPreferences.class);
    }
    
    public static AppPreferences getInstance() {
        return appPrefs;
    }

    public boolean isDebugging() {
        return getBoolean("northwind.dbg", false);
    }
    
    public void setDebugging(boolean debugging) {
        putBoolean("northwind.dbg", debugging);
    }
    
    public String getPrefsFolder() {
        return get("northwind.home", System.getProperty("user.dir"));
    }
    
    public void setPrefsFolder(String path) {
        if ( path == null || path.isBlank() || path.isEmpty() ) {
            path = System.getProperty("user.dir")
                    + File.separator + ".northwind" + File.separator;
        }
        
        put("northwind.home", path);
    }
    
    public String getLogsFolder() {
        return get("northwind.logs", getPrefsFolder() + "var" + File.separator 
                + "logs" + File.separator);
    }
    
    public void setLogsFolder(String path) {
        if ( path == null || path.isBlank() || path.isEmpty() ) {
            path = getPrefsFolder() + "var" + File.separator + "logs";
        }
        
        put("northwind.logs", path);
    }
    
    public String getErrFolder() {
        return get("northwind.logs.errors", getPrefsFolder() + "var" 
                + File.separator + "err" 
                + File.separator);
    }
    
    public void setErrFolder(String path) {
        if ( path == null || path.isBlank() || path.isEmpty() ) {
            path = getPrefsFolder() + "var" + File.separator + "err" 
                    + File.separator;
        }
        
        put("northwind.log.errors", path);
    }
    
    public String getDataPath() {
        return get("northwind.data", getPrefsFolder() + "data" + File.separator);
    }
    
    public void setDataPath(String path) {
        if ( path == null || path.isBlank() || path.isEmpty() ) {
            path = getPrefsFolder() + "data" + File.separator;
        }
        
        put("northwind.data", path);
    }

    public void put (String key, String value) {
        prefs.put(key, value);
    }
    
    public void putBoolean(String key, boolean value) {
        prefs.putBoolean(key, value);
    }
    
    public void putByteArray(String key, byte[] value) {
        prefs.putByteArray(key, value);
    }
    
    public void putDouble(String key, double value) {
        prefs.putDouble(key, value);
    }
    
    public void putFloat(String key, float value) {
        prefs.putFloat(key, value);
    }
    
    public void putInt(String key, int value) {
        prefs.putInt(key, value);
    }
    
    public void putLong(String key, long value) {
        prefs.putLong(key, value);
    }
    
    public String get (String key, String dflt) {
        return prefs.get(key, dflt);
    }
    
    public boolean getBoolean(String key, boolean value) {
        return prefs.getBoolean(key, value);
    }
    
    public byte[] getByteArray(String key, byte[] value) {
        return prefs.getByteArray(key, value);
    }
    
    public double getDouble(String key, double value) {
        return prefs.getDouble(key, value);
    }
    
    public float getFloat(String key, float value) {
        return prefs.getFloat(key, value);
    }
    
    public int getInt(String key, int value) {
        return prefs.getInt(key, value);
    }
    
    public long getLong(String key, long value) {
        return prefs.getLong(key, value);
    }
    
    public void flush() {
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            System.err.println("An error occurred while writing application "
                    + "preferences. Preferences may not be saved.");
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }
    
}
