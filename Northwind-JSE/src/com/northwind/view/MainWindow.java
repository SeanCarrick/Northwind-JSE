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
package com.northwind.view;

import com.northwind.actions.controller.ActionSupport;
import com.northwind.custmgr.model.Customer;
import com.northwind.custmgr.view.CustomersTableModel;
import com.northwind.settings.AppProperties;
import com.northwind.utils.Logger;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 */
public class MainWindow extends javax.swing.JFrame {
    
    private AppProperties props;
    private LogRecord record;
    private Logger log;
    
    private ActionSupport actionSupport = new ActionSupport(this);
    
    public void addActionListener(ActionListener listener) {
        actionSupport.addActionListener(listener);
    }
    
    public void removeActionListener(ActionListener listener) {
        actionSupport.removeActionListener(listener);
    }
    
    public void setStatus(String msg, boolean isError) {
        tipsLabel.setText(msg);
        
        if ( isError ) 
            tipsLabel.setForeground(Color.red);
        else
            tipsLabel.setForeground(SystemColor.textText);
    }
    
    private void enableEdit(boolean enabled) {
        editButton.setEnabled(enabled);
        editMenuItem.setEnabled(enabled);
    }
    
    private void enableMark(boolean enabled) {
        markButton.setEnabled(enabled);
        markMenuItem.setEnabled(enabled);
    }
    
    private void enableRemove(boolean enabled) {
        removeButton.setEnabled(enabled);
        removeMenuItem.setEnabled(enabled);
    }
    
    private ListSelectionListener customerSelectionListener = new 
            ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            enableEdit(customers.getSelectedRowCount() == 1);
            enableMark(customers.getSelectedRowCount() >= 1);
            enableRemove(customers.getSelectedRowCount() >= 1);
        }
    };
    
    private CustomersTableModel getCustomersTableModel() {
        return (CustomersTableModel)customers.getModel();
    }
    
    public void setCustomerList(List<Customer> list) {
        this.customers.setModel(new CustomersTableModel(list));
    }
    
    public Customer getSelectedCustomer() {
        return ((CustomersTableModel)customers.getModel()).getCustomerValues(
                customers.getSelectedRow());
    }
    
    public Customer[] getSelectedCustomers() {
        Customer[] selection = new Customer[customers.getSelectedRowCount()];
        int[] indices = customers.getSelectedRows();
        int j = 0;
        
        for ( int i: indices ) 
            selection[j++] = ((CustomersTableModel)customers.getModel())
                    .getCustomerValues(i);
        
        return selection;
    }
    
    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        
        props = AppProperties.getInstance();
        
        versionLabel.setText(props.getName() + " " + props.getVersion());
        userLabel.setText(System.getProperty("user.name"));
        setTitle(props.getProjectName() + " - Basic Edition");
        
        setLocationRelativeTo(null);
        customers.setAutoCreateColumnsFromModel(false);
        FontMetrics fm = customers.getFontMetrics(customers.getFont());
        
        customers.setModel(new CustomersTableModel(new ArrayList<Customer>()));
        customers.getSelectionModel().addListSelectionListener(
                customerSelectionListener);
        enableEdit(false);
        enableRemove(false);
        enableMark(false);
        
        // Attach action listeners to the menu items.
        newMenuItem.addActionListener(actionSupport);
        openMenuItem.addActionListener(actionSupport);
        saveMenuItem.addActionListener(actionSupport);
        printSetupMenu.addActionListener(actionSupport);
        printMenuItem.addActionListener(actionSupport);
        exitMenuItem.addActionListener(actionSupport);
        editMenuItem.addActionListener(actionSupport);
        markMenuItem.addActionListener(actionSupport);
        removeMenuItem.addActionListener(actionSupport);
        loadMenuItem.addActionListener(actionSupport);
        fuelMenuItem.addActionListener(actionSupport);
        serviceMenuItem.addActionListener(actionSupport);
        repairMenuItem.addActionListener(actionSupport);
        tiresMenuItem.addActionListener(actionSupport);
        customerMenuItem.addActionListener(actionSupport);
        employeeMenuItem.addActionListener(actionSupport);
        toolsMenu.addActionListener(actionSupport);
        contentsMenuItem.addActionListener(actionSupport);
        indexMenuItem.addActionListener(actionSupport);
        aboutMenuItem.addActionListener(actionSupport);
        
        // Attach action listeners to the toolbar buttons.
        exitButton.addActionListener(actionSupport);
        newButton.addActionListener(actionSupport);
        openButton.addActionListener(actionSupport);
        saveButton.addActionListener(actionSupport);
        editButton.addActionListener(actionSupport);
        markButton.addActionListener(actionSupport);
        removeButton.addActionListener(actionSupport);
        loadButton.addActionListener(actionSupport);
        fuelButton.addActionListener(actionSupport);
        serviceButton.addActionListener(actionSupport);
        repairButton.addActionListener(actionSupport);
        tiresButton.addActionListener(actionSupport);
        customerButton.addActionListener(actionSupport);
        employeeButton.addActionListener(actionSupport);
        optionsButton.addActionListener(actionSupport);
        helpButton.addActionListener(actionSupport);
    }
    
    private class FormListener implements ActionListener, MouseListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if ( e.getSource() == exitButton ||
                    e.getSource() == exitMenuItem )
                MainWindow.this.exitActionPerformed(e);
            else if ( e.getSource() == newMenuItem ||
                    e.getSource() == newButton )
                MainWindow.this.newActionPerformed(e);
            else if ( e.getSource() == openMenuItem ||
                    e.getSource() == openButton ) 
                MainWindow.this.openActionPerformed(e);
            else if ( e.getSource() == saveMenuItem ||
                    e.getSource() == saveButton )
                MainWindow.this.saveActionPerformed(e);
            else if ( e.getSource() == printSetupMenu )
                MainWindow.this.printSetupActionPerformed(e);
            else if ( e.getSource() == printMenuItem )
                MainWindow.this.printActionPerformed(e);
            else if ( e.getSource() == editMenuItem ||
                    e.getSource() == editButton )
                MainWindow.this.editActionPerformed(e);
            else if ( e.getSource() == markMenuItem ||
                    e.getSource() == markButton )
                MainWindow.this.markActionPerformed(e);
            else if ( e.getSource() == removeMenuItem ||
                    e.getSource() == removeButton )
                MainWindow.this.removeActionPerformed(e);
            else if ( e.getSource() == sortMenuItem )
                MainWindow.this.sortByActionPerformed(e);
            else if ( e.getSource() == loadMenuItem ||
                    e.getSource() == loadButton )
                MainWindow.this.loadActionPerformed(e);
            else if ( e.getSource() == fuelMenuItem ||
                    e.getSource() == fuelButton )
                MainWindow.this.fuelActionPerformed(e);
            else if ( e.getSource() == serviceMenuItem ||
                    e.getSource() == serviceButton )
                MainWindow.this.serviceActionPerformed(e);
            else if ( e.getSource() == repairMenuItem ||
                    e.getSource() == repairButton )
                MainWindow.this.repairActionPerformed(e);
            else if ( e.getSource() == tiresMenuItem ||
                    e.getSource() == tiresButton )
                MainWindow.this.tiresActionPerformed(e);
            else if ( e.getSource() == customerMenuItem ||
                    e.getSource() == customerButton )
                MainWindow.this.customerActionPerformed(e);
            else if ( e.getSource() == employeeMenuItem ||
                    e.getSource() == employeeButton )
                MainWindow.this.employeeActionPerformed(e);
            else if ( e.getSource() == toolsMenu ||
                    e.getSource() == optionsButton ) 
                MainWindow.this.optionsActionPerformed(e);
            else if ( e.getSource() == contentsMenuItem ||
                    e.getSource() == helpButton )
                MainWindow.this.contentsActionPerformed(e);
            else if ( e.getSource() == indexMenuItem )
                MainWindow.this.indexActionPerformed(e);
            else if ( e.getSource() == aboutMenuItem )
                MainWindow.this.aboutActionPerformed(e);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if ( e.getSource() == loads )
                MainWindow.this.loadsMouseClicked(e);
            else if ( e.getSource() == fuelPurchases )
                MainWindow.this.fuelMouseClicked(e);
            else if ( e.getSource() == maintenance )
                MainWindow.this.maintenanceMouseClicked(e);
            else if ( e.getSource() == vehicles )
                MainWindow.this.vehiclesMouseClicked(e);
            else if ( e.getSource() == customers )
                MainWindow.this.customersMouseClicked(e);
            else if ( e.getSource() == employees )
                MainWindow.this.employeesMouseClicked(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            
        }

        @Override
        public void mouseExited(MouseEvent e) {
            
        }
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainToolbar = new javax.swing.JToolBar();
        exitButton = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        newButton = new javax.swing.JButton();
        openButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        editButton = new javax.swing.JButton();
        markButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        loadButton = new javax.swing.JButton();
        fuelButton = new javax.swing.JButton();
        serviceButton = new javax.swing.JButton();
        repairButton = new javax.swing.JButton();
        tiresButton = new javax.swing.JButton();
        customerButton = new javax.swing.JButton();
        employeeButton = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        optionsButton = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        helpButton = new javax.swing.JButton();
        mainStatusbar = new org.jdesktop.swingx.JXStatusBar();
        tipsLabel = new javax.swing.JLabel();
        versionLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        userLabel = new javax.swing.JLabel();
        mainTabPane = new javax.swing.JTabbedPane();
        loadsTab = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        loads = new javax.swing.JTable();
        fuelTab = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        fuelPurchases = new javax.swing.JTable();
        maintenanceTab = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        maintenance = new javax.swing.JTable();
        vehiclesTab = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        vehicles = new javax.swing.JTable();
        customersTab = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        customers = new javax.swing.JTable();
        employeesPanel = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        employees = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        perMileBreakdown = new org.jdesktop.swingx.JXTreeTable();
        mainMenubar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        saveMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        printSetupMenu = new javax.swing.JMenuItem();
        printMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        editMenuItem = new javax.swing.JMenuItem();
        markMenuItem = new javax.swing.JMenuItem();
        removeMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        sortMenuItem = new javax.swing.JMenuItem();
        maintainMenu = new javax.swing.JMenu();
        addMenuItem = new javax.swing.JMenu();
        loadMenuItem = new javax.swing.JMenuItem();
        fuelMenuItem = new javax.swing.JMenuItem();
        serviceMenuItem = new javax.swing.JMenuItem();
        repairMenuItem = new javax.swing.JMenuItem();
        tiresMenuItem = new javax.swing.JMenuItem();
        customerMenuItem = new javax.swing.JMenuItem();
        employeeMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        optionsMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentsMenuItem = new javax.swing.JMenuItem();
        indexMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainToolbar.setRollover(true);

        exitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/Turn off.png"))); // NOI18N
        exitButton.setFocusable(false);
        exitButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exitButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitActionPerformed(evt);
            }
        });
        mainToolbar.add(exitButton);
        mainToolbar.add(jSeparator5);

        newButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/newDB.png"))); // NOI18N
        newButton.setFocusable(false);
        newButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newActionPerformed(evt);
            }
        });
        mainToolbar.add(newButton);

        openButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/openDB.png"))); // NOI18N
        openButton.setFocusable(false);
        openButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openActionPerformed(evt);
            }
        });
        mainToolbar.add(openButton);

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/save-database.png"))); // NOI18N
        saveButton.setEnabled(false);
        saveButton.setFocusable(false);
        saveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActionPerformed(evt);
            }
        });
        mainToolbar.add(saveButton);
        mainToolbar.add(jSeparator6);

        editButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/edit.png"))); // NOI18N
        editButton.setFocusable(false);
        editButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editActionPerformed(evt);
            }
        });
        mainToolbar.add(editButton);

        markButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/mark.png"))); // NOI18N
        markButton.setEnabled(false);
        markButton.setFocusable(false);
        markButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        markButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        markButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                markActionPerformed(evt);
            }
        });
        mainToolbar.add(markButton);

        removeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/delete.png"))); // NOI18N
        removeButton.setEnabled(false);
        removeButton.setFocusable(false);
        removeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeActionPerformed(evt);
            }
        });
        mainToolbar.add(removeButton);
        mainToolbar.add(jSeparator7);

        loadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/freight.png"))); // NOI18N
        loadButton.setFocusable(false);
        loadButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        loadButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadActionPerformed(evt);
            }
        });
        mainToolbar.add(loadButton);

        fuelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/GasPump.png"))); // NOI18N
        fuelButton.setFocusable(false);
        fuelButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fuelButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fuelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fuelActionPerformed(evt);
            }
        });
        mainToolbar.add(fuelButton);

        serviceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/Wrench.png"))); // NOI18N
        serviceButton.setFocusable(false);
        serviceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        serviceButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        serviceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serviceActionPerformed(evt);
            }
        });
        mainToolbar.add(serviceButton);

        repairButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/repair.png"))); // NOI18N
        repairButton.setFocusable(false);
        repairButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        repairButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        repairButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                repairActionPerformed(evt);
            }
        });
        mainToolbar.add(repairButton);

        tiresButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/tires24.png"))); // NOI18N
        tiresButton.setFocusable(false);
        tiresButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tiresButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tiresButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tiresActionPerformed(evt);
            }
        });
        mainToolbar.add(tiresButton);

        customerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/users.png"))); // NOI18N
        customerButton.setFocusable(false);
        customerButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        customerButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        customerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customerActionPerformed(evt);
            }
        });
        mainToolbar.add(customerButton);

        employeeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/people.png"))); // NOI18N
        employeeButton.setFocusable(false);
        employeeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        employeeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        employeeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                employeeActionPerformed(evt);
            }
        });
        mainToolbar.add(employeeButton);
        mainToolbar.add(jSeparator8);

        optionsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/config16.png"))); // NOI18N
        optionsButton.setFocusable(false);
        optionsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        optionsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        optionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionsActionPerformed(evt);
            }
        });
        mainToolbar.add(optionsButton);
        mainToolbar.add(jSeparator9);

        helpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/help.png"))); // NOI18N
        helpButton.setFocusable(false);
        helpButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        helpButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpActionPerformed(evt);
            }
        });
        mainToolbar.add(helpButton);

        tipsLabel.setText("Watch here for helpful information...");

        versionLabel.setText("{Application.Name} - {Application.Edition} v. {Application.Version} build {Application.Build}");

        jLabel1.setText("User:");

        userLabel.setText("{user.name}");

        javax.swing.GroupLayout mainStatusbarLayout = new javax.swing.GroupLayout(mainStatusbar);
        mainStatusbar.setLayout(mainStatusbarLayout);
        mainStatusbarLayout.setHorizontalGroup(
            mainStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainStatusbarLayout.createSequentialGroup()
                .addComponent(tipsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(versionLabel)
                .addGap(24, 24, 24)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(userLabel)
                .addContainerGap())
        );
        mainStatusbarLayout.setVerticalGroup(
            mainStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainStatusbarLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(mainStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tipsLabel)
                    .addComponent(versionLabel)
                    .addComponent(jLabel1)
                    .addComponent(userLabel)))
        );

        loads.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        loads.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loadsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(loads);

        javax.swing.GroupLayout loadsTabLayout = new javax.swing.GroupLayout(loadsTab);
        loadsTab.setLayout(loadsTabLayout);
        loadsTabLayout.setHorizontalGroup(
            loadsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1150, Short.MAX_VALUE)
        );
        loadsTabLayout.setVerticalGroup(
            loadsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );

        mainTabPane.addTab("Loads", new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/freight24.png")), loadsTab); // NOI18N

        fuelPurchases.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        fuelPurchases.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fuelMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(fuelPurchases);

        javax.swing.GroupLayout fuelTabLayout = new javax.swing.GroupLayout(fuelTab);
        fuelTab.setLayout(fuelTabLayout);
        fuelTabLayout.setHorizontalGroup(
            fuelTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1150, Short.MAX_VALUE)
        );
        fuelTabLayout.setVerticalGroup(
            fuelTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );

        mainTabPane.addTab("Fuel", new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/GasPump.png")), fuelTab); // NOI18N

        maintenance.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        maintenance.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                maintenanceMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(maintenance);

        javax.swing.GroupLayout maintenanceTabLayout = new javax.swing.GroupLayout(maintenanceTab);
        maintenanceTab.setLayout(maintenanceTabLayout);
        maintenanceTabLayout.setHorizontalGroup(
            maintenanceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1150, Short.MAX_VALUE)
        );
        maintenanceTabLayout.setVerticalGroup(
            maintenanceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );

        mainTabPane.addTab("Maintenance", new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/Wrench.png")), maintenanceTab); // NOI18N

        vehicles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        vehicles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                vehiclesMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(vehicles);

        javax.swing.GroupLayout vehiclesTabLayout = new javax.swing.GroupLayout(vehiclesTab);
        vehiclesTab.setLayout(vehiclesTabLayout);
        vehiclesTabLayout.setHorizontalGroup(
            vehiclesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1150, Short.MAX_VALUE)
        );
        vehiclesTabLayout.setVerticalGroup(
            vehiclesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );

        mainTabPane.addTab("Vehicles", new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/Delivery.png")), vehiclesTab); // NOI18N

        customers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        customers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                customersMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(customers);

        javax.swing.GroupLayout customersTabLayout = new javax.swing.GroupLayout(customersTab);
        customersTab.setLayout(customersTabLayout);
        customersTabLayout.setHorizontalGroup(
            customersTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 1150, Short.MAX_VALUE)
        );
        customersTabLayout.setVerticalGroup(
            customersTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );

        mainTabPane.addTab("Customers", new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/users.png")), customersTab); // NOI18N

        employees.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        employees.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                employeesMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(employees);

        javax.swing.GroupLayout employeesPanelLayout = new javax.swing.GroupLayout(employeesPanel);
        employeesPanel.setLayout(employeesPanelLayout);
        employeesPanelLayout.setHorizontalGroup(
            employeesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 1150, Short.MAX_VALUE)
        );
        employeesPanelLayout.setVerticalGroup(
            employeesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );

        mainTabPane.addTab("Employees", new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/people.png")), employeesPanel); // NOI18N

        jScrollPane1.setViewportView(perMileBreakdown);

        fileMenu.setMnemonic('F');
        fileMenu.setText("File");

        newMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/newDB.png"))); // NOI18N
        newMenuItem.setMnemonic('N');
        newMenuItem.setText("New Data Store...");
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newActionPerformed(evt);
            }
        });
        fileMenu.add(newMenuItem);

        openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/openDB.png"))); // NOI18N
        openMenuItem.setMnemonic('O');
        openMenuItem.setText("Open Data Store...");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);
        fileMenu.add(jSeparator1);

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/save-database.png"))); // NOI18N
        saveMenuItem.setMnemonic('S');
        saveMenuItem.setText("Save Data Store");
        saveMenuItem.setEnabled(false);
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);
        fileMenu.add(jSeparator2);

        printSetupMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/printersetup.png"))); // NOI18N
        printSetupMenu.setMnemonic('e');
        printSetupMenu.setText("Print Setup...");
        printSetupMenu.setEnabled(false);
        printSetupMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printSetupActionPerformed(evt);
            }
        });
        fileMenu.add(printSetupMenu);

        printMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        printMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/print.png"))); // NOI18N
        printMenuItem.setMnemonic('P');
        printMenuItem.setText("Print...");
        printMenuItem.setEnabled(false);
        printMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printActionPerformed(evt);
            }
        });
        fileMenu.add(printMenuItem);
        fileMenu.add(jSeparator3);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        exitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/Turn off.png"))); // NOI18N
        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        mainMenubar.add(fileMenu);

        editMenu.setMnemonic('E');
        editMenu.setText("Edit");

        editMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.ALT_MASK));
        editMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/edit.png"))); // NOI18N
        editMenuItem.setMnemonic('E');
        editMenuItem.setText("Edit Selected Item...");
        editMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editActionPerformed(evt);
            }
        });
        editMenu.add(editMenuItem);

        markMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, java.awt.event.InputEvent.CTRL_MASK));
        markMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/mark.png"))); // NOI18N
        markMenuItem.setMnemonic('M');
        markMenuItem.setText("Mark Selected Item(s)");
        markMenuItem.setEnabled(false);
        markMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                markActionPerformed(evt);
            }
        });
        editMenu.add(markMenuItem);

        removeMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        removeMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/delete.png"))); // NOI18N
        removeMenuItem.setMnemonic('R');
        removeMenuItem.setText("Remove Seleted Item(s)");
        removeMenuItem.setEnabled(false);
        removeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeActionPerformed(evt);
            }
        });
        editMenu.add(removeMenuItem);

        mainMenubar.add(editMenu);

        viewMenu.setMnemonic('V');
        viewMenu.setText("View");

        sortMenuItem.setText("Sort By...");
        sortMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortByActionPerformed(evt);
            }
        });
        viewMenu.add(sortMenuItem);

        mainMenubar.add(viewMenu);

        maintainMenu.setMnemonic('M');
        maintainMenu.setText("Maintain");

        addMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/add.png"))); // NOI18N
        addMenuItem.setMnemonic('A');
        addMenuItem.setText("Add New");

        loadMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/freight.png"))); // NOI18N
        loadMenuItem.setMnemonic('L');
        loadMenuItem.setText("Load");
        loadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadActionPerformed(evt);
            }
        });
        addMenuItem.add(loadMenuItem);

        fuelMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/GasPump.png"))); // NOI18N
        fuelMenuItem.setMnemonic('F');
        fuelMenuItem.setText("Fuel Purchase...");
        fuelMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fuelActionPerformed(evt);
            }
        });
        addMenuItem.add(fuelMenuItem);

        serviceMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/Wrench.png"))); // NOI18N
        serviceMenuItem.setMnemonic('S');
        serviceMenuItem.setText("Service...");
        serviceMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serviceActionPerformed(evt);
            }
        });
        addMenuItem.add(serviceMenuItem);

        repairMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/repair.png"))); // NOI18N
        repairMenuItem.setMnemonic('R');
        repairMenuItem.setText("Repair...");
        repairMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                repairActionPerformed(evt);
            }
        });
        addMenuItem.add(repairMenuItem);

        tiresMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/tires.png"))); // NOI18N
        tiresMenuItem.setMnemonic('T');
        tiresMenuItem.setText("Tire Purchase...");
        tiresMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tiresActionPerformed(evt);
            }
        });
        addMenuItem.add(tiresMenuItem);

        customerMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/users.png"))); // NOI18N
        customerMenuItem.setMnemonic('C');
        customerMenuItem.setText("Customer...");
        customerMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customerActionPerformed(evt);
            }
        });
        addMenuItem.add(customerMenuItem);

        employeeMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/people.png"))); // NOI18N
        employeeMenuItem.setMnemonic('E');
        employeeMenuItem.setText("Employee...");
        employeeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                employeeActionPerformed(evt);
            }
        });
        addMenuItem.add(employeeMenuItem);

        maintainMenu.add(addMenuItem);

        mainMenubar.add(maintainMenu);

        toolsMenu.setMnemonic('T');
        toolsMenu.setText("Tools");

        optionsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        optionsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/config16.png"))); // NOI18N
        optionsMenuItem.setMnemonic('O');
        optionsMenuItem.setText("Options");
        optionsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionsActionPerformed(evt);
            }
        });
        toolsMenu.add(optionsMenuItem);

        mainMenubar.add(toolsMenu);

        helpMenu.setMnemonic('H');
        helpMenu.setText("Help");

        contentsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/help.png"))); // NOI18N
        contentsMenuItem.setMnemonic('C');
        contentsMenuItem.setText("Help Contents");
        contentsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contentsActionPerformed(evt);
            }
        });
        helpMenu.add(contentsMenuItem);

        indexMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/help-idx.png"))); // NOI18N
        indexMenuItem.setMnemonic('I');
        indexMenuItem.setText("Help Index...");
        indexMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                indexActionPerformed(evt);
            }
        });
        helpMenu.add(indexMenuItem);
        helpMenu.add(jSeparator4);

        aboutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/info.png"))); // NOI18N
        aboutMenuItem.setMnemonic('N');
        aboutMenuItem.setText("About Northwind");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        mainMenubar.add(helpMenu);

        setJMenuBar(mainMenubar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainToolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(mainStatusbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainTabPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(mainTabPane, javax.swing.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainStatusbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
        dispose();
    }//GEN-LAST:event_exitActionPerformed

    private void newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_newActionPerformed

    private void openActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_openActionPerformed

    private void saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_saveActionPerformed

    private void editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_editActionPerformed

    private void markActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_markActionPerformed

    private void removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_removeActionPerformed

    private void loadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_loadActionPerformed

    private void fuelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fuelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fuelActionPerformed

    private void serviceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serviceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_serviceActionPerformed

    private void repairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_repairActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_repairActionPerformed

    private void tiresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tiresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tiresActionPerformed

    private void customerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_customerActionPerformed

    private void employeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_employeeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_employeeActionPerformed

    private void optionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_optionsActionPerformed

    private void helpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_helpActionPerformed

    private void printSetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printSetupActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_printSetupActionPerformed

    private void printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_printActionPerformed

    private void loadsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loadsMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_loadsMouseClicked

    private void fuelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fuelMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_fuelMouseClicked

    private void maintenanceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_maintenanceMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_maintenanceMouseClicked

    private void vehiclesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vehiclesMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_vehiclesMouseClicked

    private void customersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_customersMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_customersMouseClicked

    private void employeesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_employeesMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_employeesMouseClicked

    private void contentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contentsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_contentsActionPerformed

    private void indexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indexActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_indexActionPerformed

    private void sortByActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortByActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sortByActionPerformed

    private void aboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutActionPerformed
        String msg = props.getName() + "\n";
        msg += props.getVersion() + "\n\n";
        msg += props.getComments();
        msg += "Released under the GNU General Public License v. 3";
        String ttl = "About " + props.getProjectName();
        JOptionPane.showMessageDialog(this, msg, ttl, 
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_aboutActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenu addMenuItem;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JButton customerButton;
    private javax.swing.JMenuItem customerMenuItem;
    private javax.swing.JTable customers;
    private javax.swing.JPanel customersTab;
    private javax.swing.JButton editButton;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem editMenuItem;
    private javax.swing.JButton employeeButton;
    private javax.swing.JMenuItem employeeMenuItem;
    private javax.swing.JTable employees;
    private javax.swing.JPanel employeesPanel;
    private javax.swing.JButton exitButton;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JButton fuelButton;
    private javax.swing.JMenuItem fuelMenuItem;
    private javax.swing.JTable fuelPurchases;
    private javax.swing.JPanel fuelTab;
    private javax.swing.JButton helpButton;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem indexMenuItem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JButton loadButton;
    private javax.swing.JMenuItem loadMenuItem;
    private javax.swing.JTable loads;
    private javax.swing.JPanel loadsTab;
    private javax.swing.JMenuBar mainMenubar;
    private org.jdesktop.swingx.JXStatusBar mainStatusbar;
    private javax.swing.JTabbedPane mainTabPane;
    private javax.swing.JToolBar mainToolbar;
    private javax.swing.JMenu maintainMenu;
    private javax.swing.JTable maintenance;
    private javax.swing.JPanel maintenanceTab;
    private javax.swing.JButton markButton;
    private javax.swing.JMenuItem markMenuItem;
    private javax.swing.JButton newButton;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JButton openButton;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JButton optionsButton;
    private javax.swing.JMenuItem optionsMenuItem;
    private org.jdesktop.swingx.JXTreeTable perMileBreakdown;
    private javax.swing.JMenuItem printMenuItem;
    private javax.swing.JMenuItem printSetupMenu;
    private javax.swing.JButton removeButton;
    private javax.swing.JMenuItem removeMenuItem;
    private javax.swing.JButton repairButton;
    private javax.swing.JMenuItem repairMenuItem;
    private javax.swing.JButton saveButton;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JButton serviceButton;
    private javax.swing.JMenuItem serviceMenuItem;
    private javax.swing.JMenuItem sortMenuItem;
    private javax.swing.JLabel tipsLabel;
    private javax.swing.JButton tiresButton;
    private javax.swing.JMenuItem tiresMenuItem;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JLabel userLabel;
    private javax.swing.JTable vehicles;
    private javax.swing.JPanel vehiclesTab;
    private javax.swing.JLabel versionLabel;
    private javax.swing.JMenu viewMenu;
    // End of variables declaration//GEN-END:variables
}
