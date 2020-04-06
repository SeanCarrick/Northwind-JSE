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
package com.northwind.enums;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 */
public enum PrimaryTabs {
    LOADS(0),
    FUEL(1),
    SERVICE(2),
    VEHICLES(3),
    CUSTOMERS(4),
    EMPLOYEES(5),
    GL(6);
    
    final int value;
    
    PrimaryTabs(int value) {
        this.value = value;
    }
    
    public int toInt() {
        return value;
    }
    
    @Override
    public String toString() {
        switch ( value ) {
            case 0:
                return "Loads Tracker";
            case 1:
                return ("Fuel Journal");
            case 2:
                return ("Service Journal");
            case 3:
                return ("Vehicle Tracker");
            case 4:
                return ("Customer Tracker");
            case 5:
                return ("Employee Tracker");
            case 6:
                return ("General Ledger");
            default:
                return null;
        }
    }
}
