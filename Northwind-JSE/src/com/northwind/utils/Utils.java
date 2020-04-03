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
 *  Class      :   Utils.java
 *  Author     :   Sean Carrick
 *  Created    :   Mar 8, 2020 @ 12:32:47 PM
 *  Modified   :   Mar 8, 2020
 *  
 *  Purpose:
 *  
 *  Revision History:
 *  
 *  WHEN          BY                  REASON
 *  ------------  ------------------- ------------------------------------------
 *  Mar 8, 2020  Sean Carrick        Initial creation.
 *  Mar 21, 2020 Jiri Kovalsky       Added the getCenterPoint function.
 *  Mar 21, 2020 Sean Carrick        Moved getCenterPoint function into code 
 *                                   fold for `public static methods and 
 *                                   functions`.
 * *****************************************************************************
 */

package com.northwind.utils;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.net.URISyntaxException;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 * 
 * @version 0.1.0
 * @since 0.1.0
 */
public class Utils {
    //<editor-fold defaultstate="collapsed" desc="Constructor(s)">
    private Utils () {
        // Privatized in order to prohibit this class from being instantiated.
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private Static Methods and Functions">
    /**
     * Gets the name of the program's JAR file.
     * 
     * @return java.lang.String program's JAR file name
     */
    private static String getJarName()
    {
        return new File(Utils.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getName();
    }

    /**
     * Tests to see if the project is currently executing from within a JAR file
     * or from a folder structure. This is useful for testing if the program is
     * running inside or outside the IDE.
     * 
     * @return boolean true if running inside a JAR file; false if running in
     *                  the IDE
     */
    private static boolean runningFromJAR()
    {
        String jarName = getJarName();
        return jarName.contains(".jar");
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public Static Methods and Functions">
    /**
     * Retrieves the currently executing program's program directory. This 
     * should be the directory in which the program was executed, which could
     * also be considered the program's installation path.
     * 
     * @return java.lang.String the directory from which the program is running
     */
    public static String getProgramDirectory()
    {
        if (runningFromJAR())
        {
            return getCurrentJARDirectory();
        } else
        {
            return getCurrentProjectDirectory();
        }
    }

    /**
     * Retrieves the current project's project directory.
     * 
     * @return java.lang.String the project's directory
     */
    private static String getCurrentProjectDirectory()
    {
        return new File("").getAbsolutePath();
    }

    /**
     * Retrieves the JAR file's current directory location.
     * 
     * @return java.lang.String the directory in which the JAR file is located
     */
    private static String getCurrentJARDirectory()
    {
        try
        {
            return new File(Utils.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath())
                    .getParent();
        } catch (URISyntaxException exception)
        {
            exception.printStackTrace();
        }

        return "";
    }
    
    /**
     *  Calculates central position of the window within its container.
     * 
     * <dl>
     *  <dt>Contributed By</dt>
     *  <dd>Jiří Kovalský &lt;jiri dot kovalsky at centrum dot cz&gt;</dd>
     * </dl>
     * 
     * @param container Dimensions of parent container where window will be 
     *                  located.
     * @param window Dimensions of child window which will be displayed within 
     *               its parent container.
     * @return Location of top left corner of window to be displayed in the 
     *         center of its parent container.
     */
    public static Point getCenterPoint(Dimension container, Dimension window) {
        int x = container.width / 2;
        int y = container.height / 2;
        x = x - window.width / 2;
        y = y - window.height / 2;
        x = x < 0 ? 0 : x;
        y = y < 0 ? 0 : y;
        return new Point(x, y);
    }
    //</editor-fold>
}
