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
package com.northwind.custmgr.controller;

import com.northwind.api.Manager;
import com.northwind.custmgr.model.Customer;
import com.northwind.exceptions.DataStoreException;
import java.util.List;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 */
public class CustomerManager extends Manager {

    public CustomerManager(String name, char[] pWord,
            String table, List<Customer> model) throws DataStoreException {
        super(name, pWord, table, model);
        
    }
    
    @Override
    public List<?> query(String fields, String where, String sort) 
            throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
