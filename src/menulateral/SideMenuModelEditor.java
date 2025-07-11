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
 * CORREGIDO para funcionar correctamente con el Property Sheet
 */
public class SideMenuModelEditor extends PropertyEditorSupport implements PropertyEditor {
    
    private SideMenuModel model;
    private SideMenuModel workingModel; // Modelo de trabajo para edición
    private JDialog dialog;
    private JTable table;
    private SideMenuTableModel tableModel;
    private boolean dialogResult = false; // Para saber si se confirmó o canceló
    
    @Override
    public boolean supportsCustomEditor() {
        return true;
    }
    
    @Override
    public Component getCustomEditor() {
        // Crear una copia del modelo para editar
        createWorkingModel();
        
        if (dialog == null) {
            createCustomEditor();
        }
        
        // Actualizar el modelo de la tabla con la copia
        tableModel.setModel(workingModel);
        
        // Mostrar el diálogo y esperar respuesta
        dialog.setVisible(true);
        
        return dialog;
    }
    
    /**
     * Crea una copia del modelo actual para edición
     */
    private void createWorkingModel() {
        workingModel = new SideMenuModel();
        
        if (model != null) {
            // Copiar todos los items del modelo original
            for (int i = 0; i < model.getItemCount(); i++) {
                SideMenuItem originalItem = model.getItem(i);
                SideMenuItem copyItem = new SideMenuItem(
                    originalItem.getText(), 
                    originalItem.getIcon()
                );
                copyItem.setTooltip(originalItem.getTooltip());
                copyItem.setEnabled(originalItem.isEnabled());
                workingModel.addItem(copyItem);
            }
        }
    }
    
    @Override
    public String getJavaInitializationString() {
        if (model == null || model.getItemCount() == 0) {
            return "new menulateral.SideMenuModel()";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("new menulateral.SideMenuModel()");
        
        // Generar código para inicializar el modelo
        for (int i = 0; i < model.getItemCount(); i++) {
            SideMenuItem item = model.getItem(i);
            sb.append(".addItem(new menulateral.SideMenuItem(\"")
              .append(item.getText())
              .append("\"))");
        }
        
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
        } else if (value == null) {
            this.model = new SideMenuModel();
        }
        
        // NO llamar a firePropertyChange() aquí para evitar loops
    }
    
    @Override
    public String getAsText() {
        if (model == null) {
            return "Modelo vacío";
        }
        return "Modelo con " + model.getItemCount() + " elementos";
    }
    
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        // No implementado para este editor
        throw new IllegalArgumentException("No se puede establecer como texto");
    }
    
    private void createCustomEditor() {
        // Crear diálogo modal
        Window parentWindow = null;
        try {
            // Intentar obtener la ventana padre de NetBeans
            parentWindow = SwingUtilities.getWindowAncestor(
                KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow()
            );
        } catch (Exception e) {
            // Usar null si no se puede obtener
        }
        
        if (parentWindow instanceof Frame) {
            dialog = new JDialog((Frame) parentWindow, "Editor de Menú", true);
        } else if (parentWindow instanceof Dialog) {
            dialog = new JDialog((Dialog) parentWindow, "Editor de Menú", true);
        } else {
            dialog = new JDialog((Frame) null, "Editor de Menú", true);
        }
        
        dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(parentWindow);
        
        // Crear modelo de tabla
        tableModel = new SideMenuTableModel();
        
        // Crear tabla
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Configurar columnas
        if (table.getColumnModel().getColumnCount() >= 4) {
            table.getColumnModel().getColumn(0).setPreferredWidth(200); // Texto
            table.getColumnModel().getColumn(1).setPreferredWidth(100); // Icono
            table.getColumnModel().getColumn(2).setPreferredWidth(200); // Tooltip
            table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Enabled
        }
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel de tabla
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Elementos del Menú"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        
        // Configurar el comportamiento del diálogo
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                dialogResult = false;
            }
        });
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel de botones de edición
        JPanel editPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton addButton = new JButton("Agregar");
        addButton.addActionListener(e -> addMenuItem());
        
        JButton removeButton = new JButton("Eliminar");
        removeButton.addActionListener(e -> removeMenuItem());
        
        JButton upButton = new JButton("↑ Subir");
        upButton.addActionListener(e -> moveUp());
        
        JButton downButton = new JButton("↓ Bajar");
        downButton.addActionListener(e -> moveDown());
        
        editPanel.add(addButton);
        editPanel.add(removeButton);
        editPanel.add(Box.createHorizontalStrut(10));
        editPanel.add(upButton);
        editPanel.add(downButton);
        
        // Panel de botones de confirmación
        JPanel confirmPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton okButton = new JButton("Aceptar");
        okButton.addActionListener(e -> {
            dialogResult = true;
            applyChanges();
            dialog.setVisible(false);
        });
        
        JButton cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(e -> {
            dialogResult = false;
            dialog.setVisible(false);
        });
        
        confirmPanel.add(okButton);
        confirmPanel.add(cancelButton);
        
        panel.add(editPanel, BorderLayout.WEST);
        panel.add(confirmPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Aplica los cambios del modelo de trabajo al modelo real
     */
    private void applyChanges() {
        if (workingModel != null) {
            // Limpiar el modelo actual
            if (model == null) {
                model = new SideMenuModel();
            } else {
                model.removeAllItems();
            }
            
            // Copiar todos los elementos del modelo de trabajo
            for (int i = 0; i < workingModel.getItemCount(); i++) {
                SideMenuItem item = workingModel.getItem(i);
                SideMenuItem copyItem = new SideMenuItem(item.getText(), item.getIcon());
                copyItem.setTooltip(item.getTooltip());
                copyItem.setEnabled(item.isEnabled());
                model.addItem(copyItem);
            }
            
            // Notificar que la propiedad ha cambiado
            firePropertyChange();
        }
    }
    
    private void addMenuItem() {
        String text = JOptionPane.showInputDialog(dialog, "Texto del elemento:", "Nuevo Elemento");
        if (text != null && !text.trim().isEmpty()) {
            SideMenuItem item = new SideMenuItem(text.trim());
            workingModel.addItem(item);
            tableModel.fireTableDataChanged();
        }
    }
    
    private void removeMenuItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            workingModel.removeItem(selectedRow);
            tableModel.fireTableDataChanged();
        } else {
            JOptionPane.showMessageDialog(dialog, "Seleccione un elemento para eliminar");
        }
    }
    
    private void moveUp() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow > 0) {
            SideMenuItem item = workingModel.getItem(selectedRow);
            workingModel.removeItem(selectedRow);
            workingModel.insertItem(selectedRow - 1, item);
            tableModel.fireTableDataChanged();
            table.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
        }
    }
    
    private void moveDown() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < workingModel.getItemCount() - 1) {
            SideMenuItem item = workingModel.getItem(selectedRow);
            workingModel.removeItem(selectedRow);
            workingModel.insertItem(selectedRow + 1, item);
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
                case 2: return item.getTooltip() != null ? item.getTooltip() : "";
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
                    if (value != null) {
                        item.setText(value.toString());
                    }
                    break;
                case 1:
                    // Para el icono - selector de archivo
                    String iconPath = value != null ? value.toString() : "";
                    if (!iconPath.trim().isEmpty()) {
                        try {
                            // Intentar cargar desde recursos primero
                            java.net.URL iconURL = getClass().getResource(iconPath);
                            if (iconURL != null) {
                                item.setIcon(new ImageIcon(iconURL));
                            } else {
                                // Intentar cargar desde archivo
                                item.setIcon(new ImageIcon(iconPath));
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(dialog, 
                                "Error cargando icono: " + e.getMessage());
                        }
                    } else {
                        item.setIcon(null);
                    }
                    break;
                case 2:
                    item.setTooltip(value != null ? value.toString() : null);
                    break;
                case 3:
                    if (value instanceof Boolean) {
                        item.setEnabled((Boolean) value);
                    }
                    break;
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }
}