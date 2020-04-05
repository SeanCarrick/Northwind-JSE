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
package com.northwind.actions.controller;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 */
public class ActionSupport implements ActionListener {
    
    private Window window;
    
    public ActionSupport(Window window) {
        this.window = window;
    }
    
    private List<ActionListener> listeners = new ArrayList<>();
    
    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }
    
    public void removeActionListener(ActionListener listener) {
        listeners.remove(listener);
    }
    
    public void fireActionEvent(ActionEvent e) {
        Iterator<ActionListener> it = listeners.iterator();
        
        while ( it.hasNext() ) {
            ActionListener listener = it.next();
            listener.actionPerformed(new ActionEvent(window, 
                    ActionEvent.ACTION_PERFORMED, e.getActionCommand()));
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        fireActionEvent(e);
    }
    
}
