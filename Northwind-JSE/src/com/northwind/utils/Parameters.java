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
package com.northwind.utils;

import com.northwind.settings.AppPreferences;
import java.io.File;
import java.util.prefs.Preferences;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 */
public class Parameters {
    private static final String jdbcDriver = "org.hsqldb.jdbcDriver";
    private static final String defaultUrl = "jdbc:hsqldb:";
    private static final String defaultOptions = ";shutdown=true";
    private static final String defaultDatabase = "data/nwind";
    
    private Preferences prefs = Preferences.userNodeForPackage(Parameters.class);
    
    public String getDatabase() {
        return prefs.get("database", System.getProperty("user.home") + 
                File.separator + defaultDatabase);
    }
    
    public void setDatabase(String database) {
        prefs.put("database", database);
    }
    
    public String getJdbcUrl() {
        return defaultUrl + getDatabase() + defaultOptions;
    }
    
    public String getJdbcDriver() {
        return jdbcDriver;
    }
}
