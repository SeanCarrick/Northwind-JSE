/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.northwind.utils;

import java.awt.Point;
import java.awt.Toolkit;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 */
public class ScreenUtils {
    
    public static Point centerDialog(Object dlg) {
        int x = Toolkit.getDefaultToolkit().getScreenSize().width / 2;
        int y = Toolkit.getDefaultToolkit().getScreenSize().height / 2;
        
        if ( dlg instanceof javax.swing.JFrame ) {
            x = x - (((javax.swing.JFrame)dlg).getWidth() / 2);
            y = y - (((javax.swing.JFrame)dlg).getHeight() / 2);
        } else if (dlg instanceof javax.swing.JDialog ) {
            x = x - (((javax.swing.JDialog)dlg).getWidth() / 2);
            y = y - (((javax.swing.JDialog)dlg).getHeight() / 2);
        }
        
        x = x < 0 ? 0 : x;
        y = y < 0 ? 0 : y;
        
        return new Point(x, y);
    }
}
