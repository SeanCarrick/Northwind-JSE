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
import com.northwind.api.db.DbConnection;
import com.northwind.custmgr.model.Customer;
import com.northwind.custmgr.view.CustomerEntryDlg;
import com.northwind.custmgr.view.CustomerSelectionDialog;
import com.northwind.custmgr.view.CustomersTableModel;
import com.northwind.exceptions.DataStoreException;
import com.northwind.loadmgr.view.ArrivalDialog;
import com.northwind.settings.AppProperties;
import com.northwind.utils.Logger;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 */
public class MainWindow extends javax.swing.JFrame {
    
    private AppProperties props;
    private LogRecord record;
    private Logger log;
    private Connection con;
    
    private ActionSupport actionSupport = new ActionSupport(this);
    
    public void addActionListener(ActionListener listener) {
        actionSupport.addActionListener(listener);
    }
    
    public void removeActionListener(ActionListener listener) {
        actionSupport.removeActionListener(listener);
    }
    
    public void setStatus(String msg, boolean isError) {
        this.tipsLabel.setText(msg);
        
        if ( isError ) 
            this.tipsLabel.setForeground(Color.red);
        else
            this.tipsLabel.setForeground(SystemColor.textText);
    }
    
    private void enableEdit(boolean enabled) {
        editMenuItem.setEnabled(enabled);
    }
    
    private void enableMark(boolean enabled) {
        markMenuItem.setEnabled(enabled);
    }
    
    private void enableRemove(boolean enabled) {
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
    
//    private void showTabs() {
//        loadsTab.setVisible(viewLoadsItem.isSelected());
//        fuelTab.setVisible(viewFuelItem.isSelected());
//        maintenanceTab.setVisible(viewMaintenanceItem.isSelected());
//        vehiclesTab.setVisible(viewVehiclesItem.isSelected());
//        customersTab.setVisible(viewCustomersItem.isSelected());
//        employeesTab.setVisible(viewEmployeesItem.isSelected());
//        glTab.setVisible(viewGLItem.isSelected());
//    }
    
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
        sortMenuItem.addActionListener(actionSupport);
//        viewLoadsItem.addActionListener(actionSupport);
//        viewFuelItem.addActionListener(actionSupport);
//        viewMaintenanceItem.addActionListener(actionSupport);
//        viewVehiclesItem.addActionListener(actionSupport);
//        viewCustomersItem.addActionListener(actionSupport);
//        viewEmployeesItem.addActionListener(actionSupport);
//        viewGLItem.addActionListener(actionSupport);
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
        
        // Expand/Collapse the Task Panes, as necessary.
        mainTabbedPaneStateChanged(null);
    }
    
    private class FormListener implements ActionListener, MouseListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if ( e.getSource() == exitMenuItem )
                MainWindow.this.exitActionPerformed(e);
            else if ( e.getSource() == newMenuItem )
                MainWindow.this.newActionPerformed(e);
            else if ( e.getSource() == openMenuItem ) 
                MainWindow.this.openActionPerformed(e);
            else if ( e.getSource() == saveMenuItem )
                MainWindow.this.saveActionPerformed(e);
            else if ( e.getSource() == printSetupMenu )
                MainWindow.this.printSetupActionPerformed(e);
            else if ( e.getSource() == printMenuItem )
                MainWindow.this.printActionPerformed(e);
            else if ( e.getSource() == editMenuItem )
                MainWindow.this.editActionPerformed(e);
            else if ( e.getSource() == markMenuItem )
                MainWindow.this.markActionPerformed(e);
            else if ( e.getSource() == removeMenuItem )
                MainWindow.this.removeActionPerformed(e);
            else if ( e.getSource() == sortMenuItem )
                MainWindow.this.sortByActionPerformed(e);
            else if ( e.getSource() == loadMenuItem )
                MainWindow.this.loadActionPerformed(e);
            else if ( e.getSource() == fuelMenuItem )
                MainWindow.this.fuelActionPerformed(e);
            else if ( e.getSource() == serviceMenuItem )
                MainWindow.this.serviceActionPerformed(e);
            else if ( e.getSource() == repairMenuItem )
                MainWindow.this.repairActionPerformed(e);
            else if ( e.getSource() == tiresMenuItem )
                MainWindow.this.tiresActionPerformed(e);
            else if ( e.getSource() == customerMenuItem )
                MainWindow.this.customerActionPerformed(e);
            else if ( e.getSource() == employeeMenuItem )
                MainWindow.this.employeeActionPerformed(e);
            else if ( e.getSource() == toolsMenu ) 
                MainWindow.this.optionsActionPerformed(e);
            else if ( e.getSource() == contentsMenuItem )
                MainWindow.this.contentsActionPerformed(e);
            else if ( e.getSource() == indexMenuItem )
                MainWindow.this.indexActionPerformed(e);
            else if ( e.getSource() == aboutMenuItem )
                MainWindow.this.aboutActionPerformed(e);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
//            if ( e.getSource() == loads )
//                MainWindow.this.loadsMouseClicked(e);
//            else if ( e.getSource() == fuelPurchases )
//                MainWindow.this.fuelMouseClicked(e);
//            else if ( e.getSource() == maintenance )
//                MainWindow.this.maintenanceMouseClicked(e);
//            else if ( e.getSource() == vehicles )
//                MainWindow.this.vehiclesMouseClicked(e);
//            else if ( e.getSource() == customers )
//                MainWindow.this.customersMouseClicked(e);
//            else if ( e.getSource() == employees )
//                MainWindow.this.employeesMouseClicked(e);
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
    
    private void doNewLoad() {
        CustomerSelectionDialog dlg = new CustomerSelectionDialog(this, true);
        dlg.pack();
        dlg.setVisible(true);
    }
    
    private void doShowArrival() {
        ArrivalDialog dlg = new ArrivalDialog(this, true);
        
        dlg.pack();
    }
    
    private void doShowDeparture() {
        
    }
    
    private void doExpandLoads(ActionEvent e) {
    }
    
    private void doShowLoadsQueue() {
        
    }

    private FileFilter hsqlDatabases = new FileFilter() {
        public String getDescription() {
            return "Task Lists - HSQLDB Databases (*.script)";
        }
        public boolean accept(File f) {
            if (f.isDirectory())
                return true;
            else if (f.getName().endsWith(".script"))
                return true;
            else
                return false;
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainStatusbar = new org.jdesktop.swingx.JXStatusBar();
        tipsLabel = new javax.swing.JLabel();
        versionLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        userLabel = new javax.swing.JLabel();
        mainTabbedPane = new javax.swing.JTabbedPane();
        loadsTab = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        loads = new javax.swing.JTable();
        fuelTab = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        fuel = new javax.swing.JTable();
        servicesTab = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        service = new javax.swing.JTable();
        vehiclesTab = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        vehicles = new javax.swing.JTable();
        customersTab = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        customers = new org.jdesktop.swingx.JXTable();
        employeesTab = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        employees = new javax.swing.JTable();
        glTab = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        generalLedger = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        mainTaskController = new org.jdesktop.swingx.JXTaskPaneContainer();
        appTaskPane = new org.jdesktop.swingx.JXTaskPane();
        loadsTaskPane = new org.jdesktop.swingx.JXTaskPane();
        fuelTaskPane = new org.jdesktop.swingx.JXTaskPane();
        servicesTaskPane = new org.jdesktop.swingx.JXTaskPane();
        vehiclesTaskPane = new org.jdesktop.swingx.JXTaskPane();
        customersTaskPane = new org.jdesktop.swingx.JXTaskPane();
        employeesTaskPane = new org.jdesktop.swingx.JXTaskPane();
        glTaskPane = new org.jdesktop.swingx.JXTaskPane();
        lowRightPanel = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        highRightPanel = new javax.swing.JPanel();
        rightTabbedPane = new javax.swing.JTabbedPane();
        perMileBreakdownTab = new javax.swing.JPanel();
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

        tipsLabel.setText("Watch here for helpful information...");

        versionLabel.setText("{Application.Name} - {Application.Edition} v. {Application.Version} build {Application.Build}");

        jLabel1.setText("User:");

        userLabel.setText("{user.name}");

        javax.swing.GroupLayout mainStatusbarLayout = new javax.swing.GroupLayout(mainStatusbar);
        mainStatusbar.setLayout(mainStatusbarLayout);
        mainStatusbarLayout.setHorizontalGroup(
            mainStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainStatusbarLayout.createSequentialGroup()
                .addComponent(tipsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 746, Short.MAX_VALUE)
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

        mainTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mainTabbedPaneStateChanged(evt);
            }
        });

        loads.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Order Number", "Trip Number", "Pickup Date", "Shipper", "Delivery Date", "Consignee", "Revenue", "Miles"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Double.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(loads);
        if (loads.getColumnModel().getColumnCount() > 0) {
            loads.getColumnModel().getColumn(0).setResizable(false);
            loads.getColumnModel().getColumn(0).setPreferredWidth(25);
            loads.getColumnModel().getColumn(1).setResizable(false);
            loads.getColumnModel().getColumn(1).setPreferredWidth(25);
            loads.getColumnModel().getColumn(2).setResizable(false);
            loads.getColumnModel().getColumn(2).setPreferredWidth(20);
            loads.getColumnModel().getColumn(4).setResizable(false);
            loads.getColumnModel().getColumn(4).setPreferredWidth(20);
            loads.getColumnModel().getColumn(6).setResizable(false);
            loads.getColumnModel().getColumn(6).setPreferredWidth(15);
            loads.getColumnModel().getColumn(7).setResizable(false);
            loads.getColumnModel().getColumn(7).setPreferredWidth(8);
        }

        javax.swing.GroupLayout loadsTabLayout = new javax.swing.GroupLayout(loadsTab);
        loadsTab.setLayout(loadsTabLayout);
        loadsTabLayout.setHorizontalGroup(
            loadsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 966, Short.MAX_VALUE)
            .addGroup(loadsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 966, Short.MAX_VALUE))
        );
        loadsTabLayout.setVerticalGroup(
            loadsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 653, Short.MAX_VALUE)
            .addGroup(loadsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Load Tracker", new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/freight.png")), loadsTab); // NOI18N

        fuel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Purchase Date", "Odometer", "Location", "Gallons", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(fuel);
        if (fuel.getColumnModel().getColumnCount() > 0) {
            fuel.getColumnModel().getColumn(0).setResizable(false);
            fuel.getColumnModel().getColumn(0).setPreferredWidth(20);
            fuel.getColumnModel().getColumn(1).setResizable(false);
            fuel.getColumnModel().getColumn(1).setPreferredWidth(20);
            fuel.getColumnModel().getColumn(2).setResizable(false);
            fuel.getColumnModel().getColumn(2).setPreferredWidth(30);
            fuel.getColumnModel().getColumn(3).setResizable(false);
            fuel.getColumnModel().getColumn(3).setPreferredWidth(15);
            fuel.getColumnModel().getColumn(4).setResizable(false);
            fuel.getColumnModel().getColumn(4).setPreferredWidth(15);
        }

        javax.swing.GroupLayout fuelTabLayout = new javax.swing.GroupLayout(fuelTab);
        fuelTab.setLayout(fuelTabLayout);
        fuelTabLayout.setHorizontalGroup(
            fuelTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 966, Short.MAX_VALUE)
            .addGroup(fuelTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 966, Short.MAX_VALUE))
        );
        fuelTabLayout.setVerticalGroup(
            fuelTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 653, Short.MAX_VALUE)
            .addGroup(fuelTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Fuel Journal", new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/GasPump.png")), fuelTab); // NOI18N

        service.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Svc. Date", "Location", "Unit Number", "Odometer", "Breakdown", "Road Call", "Towed", "Total Chg."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(service);
        if (service.getColumnModel().getColumnCount() > 0) {
            service.getColumnModel().getColumn(0).setResizable(false);
            service.getColumnModel().getColumn(0).setPreferredWidth(20);
            service.getColumnModel().getColumn(1).setResizable(false);
            service.getColumnModel().getColumn(1).setPreferredWidth(30);
            service.getColumnModel().getColumn(2).setResizable(false);
            service.getColumnModel().getColumn(2).setPreferredWidth(20);
            service.getColumnModel().getColumn(3).setResizable(false);
            service.getColumnModel().getColumn(3).setPreferredWidth(15);
            service.getColumnModel().getColumn(4).setResizable(false);
            service.getColumnModel().getColumn(4).setPreferredWidth(8);
            service.getColumnModel().getColumn(5).setResizable(false);
            service.getColumnModel().getColumn(5).setPreferredWidth(8);
            service.getColumnModel().getColumn(6).setResizable(false);
            service.getColumnModel().getColumn(6).setPreferredWidth(8);
            service.getColumnModel().getColumn(7).setResizable(false);
            service.getColumnModel().getColumn(7).setPreferredWidth(15);
        }

        javax.swing.GroupLayout servicesTabLayout = new javax.swing.GroupLayout(servicesTab);
        servicesTab.setLayout(servicesTabLayout);
        servicesTabLayout.setHorizontalGroup(
            servicesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 966, Short.MAX_VALUE)
            .addGroup(servicesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 966, Short.MAX_VALUE))
        );
        servicesTabLayout.setVerticalGroup(
            servicesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 653, Short.MAX_VALUE)
            .addGroup(servicesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Service Journal", new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/Wrench.png")), servicesTab); // NOI18N

        vehicles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Unit #", "Make", "Model", "VIN"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(vehicles);
        if (vehicles.getColumnModel().getColumnCount() > 0) {
            vehicles.getColumnModel().getColumn(0).setResizable(false);
            vehicles.getColumnModel().getColumn(0).setPreferredWidth(15);
            vehicles.getColumnModel().getColumn(1).setResizable(false);
            vehicles.getColumnModel().getColumn(1).setPreferredWidth(30);
            vehicles.getColumnModel().getColumn(2).setResizable(false);
            vehicles.getColumnModel().getColumn(2).setPreferredWidth(30);
        }

        javax.swing.GroupLayout vehiclesTabLayout = new javax.swing.GroupLayout(vehiclesTab);
        vehiclesTab.setLayout(vehiclesTabLayout);
        vehiclesTabLayout.setHorizontalGroup(
            vehiclesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 966, Short.MAX_VALUE)
            .addGroup(vehiclesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 966, Short.MAX_VALUE))
        );
        vehiclesTabLayout.setVerticalGroup(
            vehiclesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 653, Short.MAX_VALUE)
            .addGroup(vehiclesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Vehicle Tracker", new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/Delivery.png")), vehiclesTab); // NOI18N

        customers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Company Name", "Street Address", "Suite", "City", "State", "Zip Code", "Contact Name", "Phone Number", "Fax Number", "Active"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(customers);
        if (customers.getColumnModel().getColumnCount() > 0) {
            customers.getColumnModel().getColumn(0).setResizable(false);
            customers.getColumnModel().getColumn(0).setPreferredWidth(40);
            customers.getColumnModel().getColumn(1).setResizable(false);
            customers.getColumnModel().getColumn(1).setPreferredWidth(50);
            customers.getColumnModel().getColumn(2).setResizable(false);
            customers.getColumnModel().getColumn(2).setPreferredWidth(15);
            customers.getColumnModel().getColumn(3).setResizable(false);
            customers.getColumnModel().getColumn(3).setPreferredWidth(30);
            customers.getColumnModel().getColumn(4).setResizable(false);
            customers.getColumnModel().getColumn(4).setPreferredWidth(5);
            customers.getColumnModel().getColumn(5).setResizable(false);
            customers.getColumnModel().getColumn(5).setPreferredWidth(10);
            customers.getColumnModel().getColumn(6).setResizable(false);
            customers.getColumnModel().getColumn(6).setPreferredWidth(40);
            customers.getColumnModel().getColumn(7).setResizable(false);
            customers.getColumnModel().getColumn(7).setPreferredWidth(15);
            customers.getColumnModel().getColumn(8).setResizable(false);
            customers.getColumnModel().getColumn(8).setPreferredWidth(15);
            customers.getColumnModel().getColumn(9).setResizable(false);
            customers.getColumnModel().getColumn(9).setPreferredWidth(5);
        }

        javax.swing.GroupLayout customersTabLayout = new javax.swing.GroupLayout(customersTab);
        customersTab.setLayout(customersTabLayout);
        customersTabLayout.setHorizontalGroup(
            customersTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 966, Short.MAX_VALUE)
        );
        customersTabLayout.setVerticalGroup(
            customersTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE)
        );

        mainTabbedPane.addTab("Customer Tracker", new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/users.png")), customersTab); // NOI18N

        employees.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Last Name", "First Name", "Phone Number", "Email Address", "Hire Date", "Active"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane8.setViewportView(employees);
        if (employees.getColumnModel().getColumnCount() > 0) {
            employees.getColumnModel().getColumn(0).setResizable(false);
            employees.getColumnModel().getColumn(0).setPreferredWidth(25);
            employees.getColumnModel().getColumn(1).setResizable(false);
            employees.getColumnModel().getColumn(1).setPreferredWidth(20);
            employees.getColumnModel().getColumn(2).setResizable(false);
            employees.getColumnModel().getColumn(2).setPreferredWidth(15);
            employees.getColumnModel().getColumn(3).setResizable(false);
            employees.getColumnModel().getColumn(3).setPreferredWidth(50);
            employees.getColumnModel().getColumn(4).setResizable(false);
            employees.getColumnModel().getColumn(4).setPreferredWidth(15);
            employees.getColumnModel().getColumn(5).setResizable(false);
            employees.getColumnModel().getColumn(5).setPreferredWidth(5);
        }

        javax.swing.GroupLayout employeesTabLayout = new javax.swing.GroupLayout(employeesTab);
        employeesTab.setLayout(employeesTabLayout);
        employeesTabLayout.setHorizontalGroup(
            employeesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 966, Short.MAX_VALUE)
            .addGroup(employeesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 966, Short.MAX_VALUE))
        );
        employeesTabLayout.setVerticalGroup(
            employeesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 653, Short.MAX_VALUE)
            .addGroup(employeesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Employee Tracker", new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/people.png")), employeesTab); // NOI18N

        generalLedger.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Tx Date", "Type", "Description", "From Acct", "To Acct", "Amount", "Tax", "Balanced"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Boolean.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane9.setViewportView(generalLedger);
        if (generalLedger.getColumnModel().getColumnCount() > 0) {
            generalLedger.getColumnModel().getColumn(0).setResizable(false);
            generalLedger.getColumnModel().getColumn(0).setPreferredWidth(15);
            generalLedger.getColumnModel().getColumn(1).setResizable(false);
            generalLedger.getColumnModel().getColumn(1).setPreferredWidth(5);
            generalLedger.getColumnModel().getColumn(2).setResizable(false);
            generalLedger.getColumnModel().getColumn(2).setPreferredWidth(75);
            generalLedger.getColumnModel().getColumn(3).setResizable(false);
            generalLedger.getColumnModel().getColumn(3).setPreferredWidth(25);
            generalLedger.getColumnModel().getColumn(4).setResizable(false);
            generalLedger.getColumnModel().getColumn(4).setPreferredWidth(25);
            generalLedger.getColumnModel().getColumn(5).setResizable(false);
            generalLedger.getColumnModel().getColumn(5).setPreferredWidth(10);
            generalLedger.getColumnModel().getColumn(6).setResizable(false);
            generalLedger.getColumnModel().getColumn(6).setPreferredWidth(5);
            generalLedger.getColumnModel().getColumn(7).setResizable(false);
            generalLedger.getColumnModel().getColumn(7).setPreferredWidth(5);
        }

        javax.swing.GroupLayout glTabLayout = new javax.swing.GroupLayout(glTab);
        glTab.setLayout(glTabLayout);
        glTabLayout.setHorizontalGroup(
            glTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 966, Short.MAX_VALUE)
            .addGroup(glTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 966, Short.MAX_VALUE))
        );
        glTabLayout.setVerticalGroup(
            glTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 653, Short.MAX_VALUE)
            .addGroup(glTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("General Ledger", new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/Script.png")), glTab); // NOI18N

        org.jdesktop.swingx.VerticalLayout verticalLayout1 = new org.jdesktop.swingx.VerticalLayout();
        verticalLayout1.setGap(14);
        mainTaskController.setLayout(verticalLayout1);

        appTaskPane.setAutoscrolls(true);
        appTaskPane.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/Northwind16.png"))); // NOI18N
        appTaskPane.setMnemonic('N');
        appTaskPane.setScrollOnExpand(true);
        appTaskPane.setSpecial(true);
        appTaskPane.setTitle("Northwind Traders Tasks");
        appTaskPane.add(new AbstractAction() {
            {
                putValue(Action.NAME, "New Data Store...");
                putValue(Action.SHORT_DESCRIPTION, "Choose where to save a new data store");
                putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(getClass()
                    .getResource("/com/northwind/resources/newDB.png")));
        }

        public void actionPerformed(ActionEvent e) {
            newActionPerformed(e);
        }
    });

    appTaskPane.add(new AbstractAction() {
        {
            putValue(Action.NAME, "Open Data Store...");
            putValue(Action.SHORT_DESCRIPTION, "Choose which data store to use");
            putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(getClass()
                .getResource("/com/northwind/resources/openDB.png")));
    }

    public void actionPerformed(ActionEvent e) {
        openActionPerformed(e);
    }
    });

    appTaskPane.add(new JSeparator());

    appTaskPane.add(new AbstractAction() {
        {
            putValue(Action.NAME, "Save Data Store...");
            putValue(Action.SHORT_DESCRIPTION, "Save the data store to disk");
            putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/save-database.png")));
        }

        public void actionPerformed(ActionEvent e) {
            saveActionPerformed(e);
        }
    });

    appTaskPane.add(new JSeparator());

    appTaskPane.add(new AbstractAction() {
        {
            putValue(Action.NAME, "Printer Setup...");
            putValue(Action.SHORT_DESCRIPTION, "Set default options for printing");
            putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/printersetup.png")));
        }

        public void actionPerformed(ActionEvent e) {
            printSetupActionPerformed(e);
        }
    });

    appTaskPane.add(new AbstractAction() {
        {
            putValue(Action.NAME, "Print");
            putValue(Action.SHORT_DESCRIPTION, "Displays system print dialog to print "
                + "documents");
            putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(getClass()
                .getResource("/com/northwind/resources/print.png")));
    }

    public void actionPerformed(ActionEvent e) {
        printActionPerformed(e);
    }
    });

    appTaskPane.add(new JSeparator());

    appTaskPane.add(new AbstractAction() {
        {
            putValue(Action.NAME, "Options...");
            putValue(Action.SHORT_DESCRIPTION, "Displays the options dialog");
            putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(getClass()
                .getResource("/com/northwind/resources/config16.png")));
    }

    public void actionPerformed(ActionEvent e) {
        optionsActionPerformed(e);
    }
    });

    appTaskPane.add(new JSeparator());

    appTaskPane.add(new AbstractAction() {
        {
            putValue(Action.NAME, "Exit Northwind Traders");
            putValue(Action.SHORT_DESCRIPTION, "Exits the application and performs "
                + "housekeeping to release all resources");
            putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(getClass()
                .getResource("/com/northwind/resources/Turnoff.png")));
    }

    public void actionPerformed(ActionEvent e) {
        exitActionPerformed(e);
    }
    });
    mainTaskController.add(appTaskPane);

    loadsTaskPane.setAutoscrolls(true);
    loadsTaskPane.setCollapsed(true);
    loadsTaskPane.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/freight.png"))); // NOI18N
    loadsTaskPane.setMnemonic('L');
    loadsTaskPane.setScrollOnExpand(true);
    loadsTaskPane.setTitle("Load Tracker Tasks");
    //loadsTaskPane.add(new AbstractAction() {
        //{
            //    putValue(Action.NAME, "Close Load Tracker");
            //    putValue(Action.SHORT_DESCRIPTION, "Closes the Load Tracker window");
            //    putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(getClass()
                //            .getResource("/com/northwind/resources/Cancel.png")));
        //}
    //
    //public void actionPerformed(ActionEvent e) {
        //    if ( getValue(Action.NAME).toString().equalsIgnoreCase("Close Load Tracker") ) {
            //        putValue(Action.NAME, "Open Load Tracker");
            //        putValue(Action.SHORT_DESCRIPTION, "Opens the Load Tracker window");
            //        putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(getClass()
                //            .getResource("/com/northwind/resources/Cancel.png")));
        //        mainTabbedPane.remove(loadsTab);
        //        loadsTaskPane.setCollapsed(true);
        //    } else if ( getValue(Action.NAME).toString().equalsIgnoreCase("Open Load Tracker") ) {
        //        putValue(Action.NAME, "Close Load Tracker");
        //        putValue(Action.SHORT_DESCRIPTION, "Closes the Load Tracker window");
        //        putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(getClass()
            //                .getResource("/com/northwind/resources/Cancel.png")));
    //        mainTabbedPane.addTab("Load Tracker",
        //                new javax.swing.ImageIcon(getClass().getResource(
            //                        "/com/northwind/resources/freight.png")), loadsTab);
//    }
//}
//});
//
//loadsTaskPane.add(new JSeparator());

loadsTaskPane.add(new AbstractAction() {
    {
        putValue(Action.NAME, "Book New Load...");
        putValue(Action.SHORT_DESCRIPTION, "Displays the load booking dialog");
        putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(getClass()
            .getResource("/com/northwind/resources/add.png")));
    }

    public void actionPerformed(ActionEvent e) {
        doNewLoad();
    }
    });

    loadsTaskPane.add(new JSeparator());

    loadsTaskPane.add(new AbstractAction() {
        {
            putValue(Action.NAME, "Arrive at Stop...");
            putValue(Action.SHORT_DESCRIPTION, "Displays stop arrival dialog");
            putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(getClass()
                .getResource("/com/northwind/resources/Arrive.png")));
    }

    public void actionPerformed(ActionEvent e) {
        if ( getValue(Action.NAME).toString().equalsIgnoreCase("Arrive at Stop...") ) {
            putValue(Action.NAME, "Depart from Stop...");
            putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(getClass()
                .getResource("/com/northwind/resources/Depart.png")));
        doShowArrival();
    } else if ( getValue(Action.NAME).toString().equalsIgnoreCase("Depart from Stop...") ) {
        putValue(Action.NAME, "Arrive at Stop...");
        putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(getClass()
            .getResource("/com/northwind/resources/Arrive.png")));
    doShowDeparture();
    }
    }
    });

    loadsTaskPane.add(new JSeparator());

    loadsTaskPane.add(new AbstractAction() {
        {
            putValue(Action.NAME, "View Loads Queue...");
            putValue(Action.SHORT_DESCRIPTION, "Displays Loads Queue dialog");
            putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(getClass()
                .getResource("/com/northwind/resources/freight.png")));
    }

    public void actionPerformed(ActionEvent e) {
        doShowLoadsQueue();
    }
    });
    loadsTaskPane.addComponentListener(new java.awt.event.ComponentAdapter() {
        public void componentResized(java.awt.event.ComponentEvent evt) {
            loadsTaskPaneComponentResized(evt);
        }
    });
    mainTaskController.add(loadsTaskPane);

    fuelTaskPane.setAutoscrolls(true);
    fuelTaskPane.setCollapsed(true);
    fuelTaskPane.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/GasPump.png"))); // NOI18N
    fuelTaskPane.setMnemonic('F');
    fuelTaskPane.setScrollOnExpand(true);
    fuelTaskPane.setTitle("Fuel Journal Tasks");
    mainTaskController.add(fuelTaskPane);

    servicesTaskPane.setAutoscrolls(true);
    servicesTaskPane.setCollapsed(true);
    servicesTaskPane.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/Wrench.png"))); // NOI18N
    servicesTaskPane.setMnemonic('S');
    servicesTaskPane.setScrollOnExpand(true);
    servicesTaskPane.setTitle("Service Journal Tasks");
    mainTaskController.add(servicesTaskPane);

    vehiclesTaskPane.setAutoscrolls(true);
    vehiclesTaskPane.setCollapsed(true);
    vehiclesTaskPane.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/Delivery.png"))); // NOI18N
    vehiclesTaskPane.setMnemonic('V');
    vehiclesTaskPane.setScrollOnExpand(true);
    vehiclesTaskPane.setTitle("Vehicle Tracker Tasks");
    mainTaskController.add(vehiclesTaskPane);

    customersTaskPane.setAutoscrolls(true);
    customersTaskPane.setCollapsed(true);
    customersTaskPane.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/users.png"))); // NOI18N
    customersTaskPane.setMnemonic('C');
    customersTaskPane.setScrollOnExpand(true);
    customersTaskPane.setTitle("Customer Tracker Tasks");
    //customersTaskPane.add(new AbstractAction() {
        //{
            //    putValue(Action.NAME, "Close Customers List");
            //    putValue(Action.SHORT_DESCRIPTION, "Close Customers List");
            //    putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/Cancel.png")));
            //}
        //
        //public void actionPerformed(ActionEvent e) {
            //    if ( getValue(Action.NAME).toString().equalsIgnoreCase("Close Customers List") ) {
                //        putValue(Action.NAME, "Open Customers List");
                //        putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/open.png")));
                //        mainTabbedPane.remove(customersTab);
                //    } else if ( getValue(Action.NAME).toString().equalsIgnoreCase("Open Customers List") ) {
                //        putValue(Action.NAME, "Close Customers List");
                //        putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/Cancel.png")));
                //        mainTabbedPane.addTab("Customer List", new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/users.png")), customersTab, null );
                //    }
            //}
        //});
//
//customersTaskPane.add(new JSeparator());

customersTaskPane.add(new AbstractAction() {
    {
        putValue(Action.NAME, "Add New Customer");
        putValue(Action.SHORT_DESCRIPTION, "Adds a new customer record");
        putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/add.png")));
    }

    public void actionPerformed(ActionEvent e) {
        CustomerEntryDlg dlg = new CustomerEntryDlg(null, true);
        dlg.pack();
        dlg.setVisible(true);
    }
    });
    mainTaskController.add(customersTaskPane);

    employeesTaskPane.setAutoscrolls(true);
    employeesTaskPane.setCollapsed(true);
    employeesTaskPane.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/people.png"))); // NOI18N
    employeesTaskPane.setMnemonic('E');
    employeesTaskPane.setScrollOnExpand(true);
    employeesTaskPane.setTitle("Employee Tracker Tasks");
    mainTaskController.add(employeesTaskPane);

    glTaskPane.setAutoscrolls(true);
    glTaskPane.setCollapsed(true);
    glTaskPane.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/Script.png"))); // NOI18N
    glTaskPane.setMnemonic('G');
    glTaskPane.setName(""); // NOI18N
    glTaskPane.setScrollOnExpand(true);
    glTaskPane.setTitle("General Ledger Tasks");
    mainTaskController.add(glTaskPane);

    jScrollPane3.setViewportView(mainTaskController);

    jTextPane1.setContentType("text/html"); // NOI18N
    jTextPane1.setText("<html>\n  <head>\n\n  </head>\n  <body>\n    <h3>Information Panel</h3>\n    <p style=\"margin-top: 0\">\n      This information panel will provide information that will help you get the most out of Northwind Traders. Whenever you are doing anything in the application, look here first if you have questions.\n    </p>\n  </body>\n</html>\n");
    jScrollPane10.setViewportView(jTextPane1);

    javax.swing.GroupLayout lowRightPanelLayout = new javax.swing.GroupLayout(lowRightPanel);
    lowRightPanel.setLayout(lowRightPanelLayout);
    lowRightPanelLayout.setHorizontalGroup(
        lowRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jScrollPane10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
    );
    lowRightPanelLayout.setVerticalGroup(
        lowRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
    );

    jScrollPane1.setViewportView(perMileBreakdown);

    javax.swing.GroupLayout perMileBreakdownTabLayout = new javax.swing.GroupLayout(perMileBreakdownTab);
    perMileBreakdownTab.setLayout(perMileBreakdownTabLayout);
    perMileBreakdownTabLayout.setHorizontalGroup(
        perMileBreakdownTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 220, Short.MAX_VALUE)
        .addGroup(perMileBreakdownTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE))
    );
    perMileBreakdownTabLayout.setVerticalGroup(
        perMileBreakdownTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 404, Short.MAX_VALUE)
        .addGroup(perMileBreakdownTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE))
    );

    rightTabbedPane.addTab("Per Mile Breakdown", new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/Accounting.png")), perMileBreakdownTab); // NOI18N

    javax.swing.GroupLayout highRightPanelLayout = new javax.swing.GroupLayout(highRightPanel);
    highRightPanel.setLayout(highRightPanelLayout);
    highRightPanelLayout.setHorizontalGroup(
        highRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 0, Short.MAX_VALUE)
        .addGroup(highRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rightTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE))
    );
    highRightPanelLayout.setVerticalGroup(
        highRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 0, Short.MAX_VALUE)
        .addGroup(highRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(highRightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rightTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)))
    );

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
    exitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/Turnoff.png"))); // NOI18N
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

    sortMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.CTRL_MASK));
    sortMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/northwind/resources/sort-asc.png"))); // NOI18N
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
        .addComponent(mainStatusbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(layout.createSequentialGroup()
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(mainTabbedPane)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(lowRightPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(highRightPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap())
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(mainTabbedPane))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addComponent(highRightPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(lowRightPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(mainStatusbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
        dispose();
    }//GEN-LAST:event_exitActionPerformed

    private void newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newActionPerformed
        JFileChooser dlg = new JFileChooser();
        dlg.setDialogTitle("New Northwind Traders Data Store");
        dlg.setFileFilter(hsqlDatabases);
        File dir = new File(props.getDataFolder());
        dlg.setCurrentDirectory(dir);
        if ( dlg.showSaveDialog(null) == JFileChooser.APPROVE_OPTION ) {
            File newFile = dlg.getSelectedFile();
            props.setProperty("app.last.db", newFile.getName());
            if ( !newFile.exists() ) {
                try {
                    newFile.createNewFile();
                    
                } catch ( IOException ex ) {
                    this.setStatus(ex.getMessage(), true);
                }
            }

            DbConnection connect = new DbConnection();
            String dbName = newFile.getName();

            if ( dbName.endsWith(".script") )
                dbName = dbName.substring(0, dbName.length() - 7);

            try {
                con = connect.reconnect(dbName);
                this.setStatus("Data store ready for use: " + newFile.getName(), 
                        false);
            } catch (DataStoreException ex) {
                this.setStatus(ex.getMessage(), true);
                con = null;
            }
        }
    }//GEN-LAST:event_newActionPerformed

    private void openActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openActionPerformed
        JFileChooser dlg = new JFileChooser();
        dlg.setDialogTitle("Select Northwind Traders Data Store");
        dlg.setFileFilter(hsqlDatabases);
        File dir = new File(props.getDataFolder());
        dlg.setCurrentDirectory(dir);
        if ( dlg.showOpenDialog(null) == JFileChooser.APPROVE_OPTION ) {
            File newFile = dlg.getSelectedFile();
            props.setProperty("app.last.db", newFile.getName());
            if ( !newFile.exists() ) {
                try {
                    newFile.createNewFile();
                    
                } catch ( IOException ex ) {
                    this.setStatus(ex.getMessage(), true);
                }
            }

            DbConnection connect = new DbConnection();
            String dbName = newFile.getName();

            if ( dbName.endsWith(".script") )
                dbName = dbName.substring(0, dbName.length() - 7);
            
           try {
                con = connect.reconnect(dbName);
                this.setStatus("Data store ready for use: " + newFile.getName(), 
                        false);
             } catch (DataStoreException ex) {
                this.setStatus(ex.getMessage(), true);
                con = null;
            }
        }
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

    private void printSetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printSetupActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_printSetupActionPerformed

    private void printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_printActionPerformed

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

    private void loadsTaskPaneComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_loadsTaskPaneComponentResized
        // Make the load tracker tab active, if it is not already.
        int oldTab = mainTabbedPane.getSelectedIndex();
        
        if ( !loadsTaskPane.isCollapsed() ) {
            if ( mainTabbedPane.getSelectedIndex() != 0 )
                mainTabbedPane.setSelectedIndex(0);
            else 
                mainTabbedPane.setSelectedIndex(oldTab);
        }
        
        this.mainTabbedPaneStateChanged(null);
    }//GEN-LAST:event_loadsTaskPaneComponentResized

    private void mainTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_mainTabbedPaneStateChanged
        // When the user clicks a tab, expand that tab's task pane, and collapse
        //+ all of the others, except Northwind Traders Tasks.
        String selectedTab = mainTabbedPane.getTitleAt(
                mainTabbedPane.getSelectedIndex());
        
        switch ( selectedTab ) {
            case "Load Tracker":
                this.loadsTaskPane.setCollapsed(false);
                this.fuelTaskPane.setCollapsed(true);
                this.servicesTaskPane.setCollapsed(true);
                this.vehiclesTaskPane.setCollapsed(true);
                this.customersTaskPane.setCollapsed(true);
                this.employeesTaskPane.setCollapsed(true);
                this.glTaskPane.setCollapsed(true);
                break;
            case "Fuel Journal":
                this.loadsTaskPane.setCollapsed(true);
                this.fuelTaskPane.setCollapsed(false);
                this.servicesTaskPane.setCollapsed(true);
                this.vehiclesTaskPane.setCollapsed(true);
                this.customersTaskPane.setCollapsed(true);
                this.employeesTaskPane.setCollapsed(true);
                this.glTaskPane.setCollapsed(true);
                break;
            case "Service Journal":
                this.loadsTaskPane.setCollapsed(true);
                this.fuelTaskPane.setCollapsed(true);
                this.servicesTaskPane.setCollapsed(false);
                this.vehiclesTaskPane.setCollapsed(true);
                this.customersTaskPane.setCollapsed(true);
                this.employeesTaskPane.setCollapsed(true);
                this.glTaskPane.setCollapsed(true);
                break;
            case "Vehicle Tracker":
                this.loadsTaskPane.setCollapsed(true);
                this.fuelTaskPane.setCollapsed(true);
                this.servicesTaskPane.setCollapsed(true);
                this.vehiclesTaskPane.setCollapsed(false);
                this.customersTaskPane.setCollapsed(true);
                this.employeesTaskPane.setCollapsed(true);
                this.glTaskPane.setCollapsed(true);
                break;
            case "Customer Tracker":
                this.loadsTaskPane.setCollapsed(true);
                this.fuelTaskPane.setCollapsed(true);
                this.servicesTaskPane.setCollapsed(true);
                this.vehiclesTaskPane.setCollapsed(true);
                this.customersTaskPane.setCollapsed(false);
                this.employeesTaskPane.setCollapsed(true);
                this.glTaskPane.setCollapsed(true);
                break;
            case "Employee Tracker":
                this.loadsTaskPane.setCollapsed(true);
                this.fuelTaskPane.setCollapsed(true);
                this.servicesTaskPane.setCollapsed(true);
                this.vehiclesTaskPane.setCollapsed(true);
                this.customersTaskPane.setCollapsed(true);
                this.employeesTaskPane.setCollapsed(false);
                this.glTaskPane.setCollapsed(true);
                break;
            case "General Ledger":
                this.loadsTaskPane.setCollapsed(true);
                this.fuelTaskPane.setCollapsed(true);
                this.servicesTaskPane.setCollapsed(true);
                this.vehiclesTaskPane.setCollapsed(true);
                this.customersTaskPane.setCollapsed(true);
                this.employeesTaskPane.setCollapsed(true);
                this.glTaskPane.setCollapsed(false);
                break;
            default:
                this.loadsTaskPane.setCollapsed(true);
                this.fuelTaskPane.setCollapsed(true);
                this.servicesTaskPane.setCollapsed(true);
                this.vehiclesTaskPane.setCollapsed(true);
                this.customersTaskPane.setCollapsed(true);
                this.employeesTaskPane.setCollapsed(true);
                this.glTaskPane.setCollapsed(true);
                break;
        }
    }//GEN-LAST:event_mainTabbedPaneStateChanged

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
    private org.jdesktop.swingx.JXTaskPane appTaskPane;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JMenuItem customerMenuItem;
    private org.jdesktop.swingx.JXTable customers;
    private javax.swing.JPanel customersTab;
    private org.jdesktop.swingx.JXTaskPane customersTaskPane;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem editMenuItem;
    private javax.swing.JMenuItem employeeMenuItem;
    private javax.swing.JTable employees;
    private javax.swing.JPanel employeesTab;
    private org.jdesktop.swingx.JXTaskPane employeesTaskPane;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JTable fuel;
    private javax.swing.JMenuItem fuelMenuItem;
    private javax.swing.JPanel fuelTab;
    private org.jdesktop.swingx.JXTaskPane fuelTaskPane;
    private javax.swing.JTable generalLedger;
    private javax.swing.JPanel glTab;
    private org.jdesktop.swingx.JXTaskPane glTaskPane;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JPanel highRightPanel;
    private javax.swing.JMenuItem indexMenuItem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JMenuItem loadMenuItem;
    private javax.swing.JTable loads;
    private javax.swing.JPanel loadsTab;
    private org.jdesktop.swingx.JXTaskPane loadsTaskPane;
    private javax.swing.JPanel lowRightPanel;
    private javax.swing.JMenuBar mainMenubar;
    private org.jdesktop.swingx.JXStatusBar mainStatusbar;
    private javax.swing.JTabbedPane mainTabbedPane;
    private org.jdesktop.swingx.JXTaskPaneContainer mainTaskController;
    private javax.swing.JMenu maintainMenu;
    private javax.swing.JMenuItem markMenuItem;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem optionsMenuItem;
    private org.jdesktop.swingx.JXTreeTable perMileBreakdown;
    private javax.swing.JPanel perMileBreakdownTab;
    private javax.swing.JMenuItem printMenuItem;
    private javax.swing.JMenuItem printSetupMenu;
    private javax.swing.JMenuItem removeMenuItem;
    private javax.swing.JMenuItem repairMenuItem;
    private javax.swing.JTabbedPane rightTabbedPane;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JTable service;
    private javax.swing.JMenuItem serviceMenuItem;
    private javax.swing.JPanel servicesTab;
    private org.jdesktop.swingx.JXTaskPane servicesTaskPane;
    private javax.swing.JMenuItem sortMenuItem;
    private javax.swing.JLabel tipsLabel;
    private javax.swing.JMenuItem tiresMenuItem;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JLabel userLabel;
    private javax.swing.JTable vehicles;
    private javax.swing.JPanel vehiclesTab;
    private org.jdesktop.swingx.JXTaskPane vehiclesTaskPane;
    private javax.swing.JLabel versionLabel;
    private javax.swing.JMenu viewMenu;
    // End of variables declaration//GEN-END:variables
}
