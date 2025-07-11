package menulateral;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;

/**
 * Editor personalizado para el SideMenuModel en NetBeans
 */
public class SideMenuModelEditor extends PropertyEditorSupport implements PropertyEditor {
    
    private SideMenuModel model;
    private JDialog dialog;
    private JTable table;
    private SideMenuTableModel tableModel;
    
    @Override
    public boolean supportsCustomEditor() {
        return true;
    }
    
    @Override
    public Component getCustomEditor() {
        if (dialog == null) {
            createCustomEditor();
        }
        
        // Actualizar el modelo de la tabla
        if (model != null) {
            tableModel.setModel(model);
        }
        
        return dialog;
    }
    
    @Override
    public String getJavaInitializationString() {
        if (model == null || model.getItemCount() == 0) {
            return "new prueba2.SideMenuModel()";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("new prueba2.SideMenuModel()");
        // Aquí podrías generar código para inicializar el modelo
        // Por simplicidad, devolvemos un modelo vacío
        return sb.toString();
    }
    
    @Override
    public Object getValue() {
        return model;
    }
    
    @Override
    public void setValue(Object value) {
        if (value instanceof SideMenuModel) {
            this.model = (SideMenuModel) value;
        } else {
            this.model = new SideMenuModel();
        }
        firePropertyChange();
    }
    
    @Override
    public String getAsText() {
        if (model == null) {
            return "null";
        }
        return model.toString();
    }
    
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        // No implementado para este editor
    }
    
    private void createCustomEditor() {
        dialog = new JDialog((Frame) null, "Editor de Menú", true);
        dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(null);
        
        // Crear modelo de tabla
        tableModel = new SideMenuTableModel();
        
        // Crear tabla
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setPreferredWidth(200); // Texto
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Icono
        table.getColumnModel().getColumn(2).setPreferredWidth(200); // Tooltip
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Enabled
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel de tabla
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton addButton = new JButton("Agregar");
        addButton.addActionListener(e -> addMenuItem());
        
        JButton removeButton = new JButton("Eliminar");
        removeButton.addActionListener(e -> removeMenuItem());
        
        JButton upButton = new JButton("Subir");
        upButton.addActionListener(e -> moveUp());
        
        JButton downButton = new JButton("Bajar");
        downButton.addActionListener(e -> moveDown());
        
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            firePropertyChange();
            dialog.setVisible(false);
        });
        
        JButton cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(e -> dialog.setVisible(false));
        
        panel.add(addButton);
        panel.add(removeButton);
        panel.add(upButton);
        panel.add(downButton);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(okButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private void addMenuItem() {
        SideMenuItem item = new SideMenuItem("Nuevo Item");
        if (model == null) {
            model = new SideMenuModel();
        }
        model.addItem(item);
        tableModel.fireTableDataChanged();
    }
    
    private void removeMenuItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0 && model != null) {
            model.removeItem(selectedRow);
            tableModel.fireTableDataChanged();
        }
    }
    
    private void moveUp() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow > 0 && model != null) {
            SideMenuItem item = model.getItem(selectedRow);
            model.removeItem(selectedRow);
            model.insertItem(selectedRow - 1, item);
            tableModel.fireTableDataChanged();
            table.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
        }
    }
    
    private void moveDown() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < model.getItemCount() - 1 && model != null) {
            SideMenuItem item = model.getItem(selectedRow);
            model.removeItem(selectedRow);
            model.insertItem(selectedRow + 1, item);
            tableModel.fireTableDataChanged();
            table.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
        }
    }
    
    /**
     * Modelo de tabla para editar los ítems del menú
     */
    private class SideMenuTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Texto", "Icono", "Tooltip", "Habilitado"};
        private SideMenuModel menuModel;
        
        public void setModel(SideMenuModel model) {
            this.menuModel = model;
            fireTableDataChanged();
        }
        
        @Override
        public int getRowCount() {
            return menuModel != null ? menuModel.getItemCount() : 0;
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0: return String.class;  // Texto
                case 1: return String.class;  // Icono (path)
                case 2: return String.class;  // Tooltip
                case 3: return Boolean.class; // Enabled
                default: return Object.class;
            }
        }
        
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (menuModel == null || rowIndex >= menuModel.getItemCount()) {
                return null;
            }
            
            SideMenuItem item = menuModel.getItem(rowIndex);
            switch (columnIndex) {
                case 0: return item.getText();
                case 1: return item.getIcon() != null ? item.getIcon().getDescription() : "";
                case 2: return item.getTooltip();
                case 3: return item.isEnabled();
                default: return null;
            }
        }
        
        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            if (menuModel == null || rowIndex >= menuModel.getItemCount()) {
                return;
            }
            
            SideMenuItem item = menuModel.getItem(rowIndex);
            switch (columnIndex) {
                case 0:
                    item.setText((String) value);
                    break;
                case 1:
                    // Para el icono, podrías implementar un selector de archivos
                    String iconPath = (String) value;
                    if (iconPath != null && !iconPath.trim().isEmpty()) {
                        try {
                            item.setIcon(new ImageIcon(iconPath));
                        } catch (Exception e) {
                            // Manejar error
                        }
                    } else {
                        item.setIcon(null);
                    }
                    break;
                case 2:
                    item.setTooltip((String) value);
                    break;
                case 3:
                    item.setEnabled((Boolean) value);
                    break;
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }
}