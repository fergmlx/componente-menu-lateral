package menulateral;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * Editor personalizado para el modelo de SideMenuComponent
 * Permite editar los ítems del menú desde la ventana de propiedades de NetBeans
 */
public class SideMenuModelEditor extends PropertyEditorSupport {
    
    private SideMenuModel model;
    private JPanel customPanel;
    private MenuItemTableModel tableModel;
    
    public SideMenuModelEditor() {
        // Inicializar con un modelo vacío si no se proporciona uno
        model = new SideMenuModel();
    }
    
    @Override
    public void setValue(Object value) {
        if (value instanceof SideMenuModel) {
            this.model = (SideMenuModel) value;
        } else {
            this.model = new SideMenuModel();
        }
        
        // Notificar cambio para actualizar editor
        if (tableModel != null) {
            tableModel.fireTableDataChanged();
        }
        
        // Notificar a PropertyEditor que el valor ha cambiado
        firePropertyChange();
    }
    
    @Override
    public Object getValue() {
        return model;
    }
    
    @Override
    public boolean supportsCustomEditor() {
        return true;
    }
    
    @Override
    public Component getCustomEditor() {
        if (customPanel == null) {
            customPanel = createEditorPanel();
        }
        return customPanel;
    }
    
    @Override
    public String getAsText() {
        return model.toString();
    }
    
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        // No es necesario implementar esto, usamos editor personalizado
    }
    
    /**
    * Genera el código Java para inicializar la propiedad del modelo
    * Este método es usado por NetBeans para generar código en el Source
    */
   @Override
   public String getJavaInitializationString() {
       StringBuilder sb = new StringBuilder();
       sb.append("new menulateral.SideMenuModel()");

       // Solo genera código para agregar elementos si hay elementos en el modelo
       if (model != null && model.getItemCount() > 0) {
           // Cambiar a un enfoque más directo sin usar lambdas
           sb.delete(0, sb.length());  // Limpiar el StringBuilder

           sb.append("new menulateral.SideMenuModel() {{ ");

           // Agregar código para cada elemento del modelo usando un bloque de inicialización
           for (SideMenuItem item : model.getItems()) {
               if (item != null) {
                   sb.append("addItem(new menulateral.SideMenuItem(");

                   // Texto del ítem (siempre presente)
                   sb.append("\"").append(escapeJavaString(item.getText())).append("\"");

                   // Si tiene una ruta de icono, incluirla
                   if (item.getIconPath() != null && !item.getIconPath().isEmpty()) {
                       sb.append(", \"").append(escapeJavaString(item.getIconPath())).append("\"");
                   } else if (item.getTooltip() != null && !item.getTooltip().isEmpty()) {
                       // No hay icono pero necesitamos incluir null para llegar al tooltip
                       sb.append(", null");
                   }

                   // Si tiene tooltip, agregarlo como tercer parámetro
                   if (item.getTooltip() != null && !item.getTooltip().isEmpty()) {
                       sb.append(", \"").append(escapeJavaString(item.getTooltip())).append("\"");
                   }

                   sb.append(")); ");

                   // Si está deshabilitado, añadir código para deshabilitarlo
                   if (!item.isEnabled()) {
                       sb.append("getItem(").append(model.indexOf(item)).append(").setEnabled(false); ");
                   }
               }
           }

           sb.append("}}");
       }

       return sb.toString();
   }
    
    /**
     * Escapa caracteres especiales en strings Java
     */
    private String escapeJavaString(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
    }
    
    /**
     * Crea el panel del editor personalizado
     */
    private JPanel createEditorPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Crear modelo de tabla y tabla
        tableModel = new MenuItemTableModel();
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        
        // Configurar renderizadores y editores para la columna de iconos
        table.getColumnModel().getColumn(1).setCellRenderer(new IconRenderer());
        table.getColumnModel().getColumn(1).setCellEditor(new IconEditor());
        
        // Agregar tabla a un JScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(350, 200));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton addButton = new JButton("Agregar");
        addButton.addActionListener(e -> addNewItem());
        
        JButton removeButton = new JButton("Eliminar");
        removeButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                removeItem(selectedRow);
            }
        });
        
        JButton moveUpButton = new JButton("↑");
        moveUpButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow > 0) {
                moveItem(selectedRow, selectedRow - 1);
                table.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
            }
        });
        
        JButton moveDownButton = new JButton("↓");
        moveDownButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < tableModel.getRowCount() - 1) {
                moveItem(selectedRow, selectedRow + 1);
                table.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
            }
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(moveUpButton);
        buttonPanel.add(moveDownButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Agrega un nuevo ítem al modelo
     */
    private void addNewItem() {
        SideMenuItem newItem = new SideMenuItem("Nuevo Item");
        
        // Crear una copia del modelo actual y agregar el nuevo ítem
        SideMenuModel newModel = cloneModel(model);
        newModel.addItem(newItem);
        
        // Establecer el nuevo modelo y notificar cambio
        model = newModel;
        tableModel.fireTableDataChanged();
        firePropertyChange();
    }
    
    /**
     * Elimina un ítem del modelo
     */
    private void removeItem(int index) {
        // Crear una copia del modelo actual y eliminar el ítem
        SideMenuModel newModel = cloneModel(model);
        newModel.removeItem(index);
        
        // Establecer el nuevo modelo y notificar cambio
        model = newModel;
        tableModel.fireTableDataChanged();
        firePropertyChange();
    }
    
    /**
     * Mueve un ítem de una posición a otra
     */
    private void moveItem(int fromIndex, int toIndex) {
        List<SideMenuItem> items = model.getItems();
        SideMenuItem item = items.get(fromIndex);
        
        // Crear una copia del modelo con el nuevo orden
        SideMenuModel newModel = cloneModel(model);
        List<SideMenuItem> newItems = newModel.getItems();
        newItems.remove(fromIndex);
        newItems.add(toIndex, item);
        newModel.setItems(newItems);
        
        // Establecer el nuevo modelo y notificar cambio
        model = newModel;
        tableModel.fireTableDataChanged();
        firePropertyChange();
    }
    
    /**
    * Crea una copia del modelo para evitar problemas de referencia
    */
   private SideMenuModel cloneModel(SideMenuModel sourceModel) {
       SideMenuModel newModel = new SideMenuModel();
       for (SideMenuItem item : sourceModel.getItems()) {
           // Clonar cada ítem preservando el iconPath
           SideMenuItem newItem;

           if (item.getIconPath() != null && !item.getIconPath().isEmpty()) {
               // Si tiene una ruta de icono, usarla para conservar la referencia
               newItem = new SideMenuItem(
                       item.getText(),
                       item.getIconPath(),
                       item.getTooltip());
           } else {
               // Si no tiene ruta pero sí icono, usar el constructor con ImageIcon
               newItem = new SideMenuItem(
                       item.getText(),
                       item.getIcon(),
                       item.getTooltip());
           }

           newItem.setEnabled(item.isEnabled());
           newItem.setUserData(item.getUserData());

           // Asegurarse de que las URLs también se preserven
           if (item.getIconUrl() != null) {
               newItem.setIconUrl(item.getIconUrl());
           }

           newModel.addItem(newItem);
       }
       return newModel;
   }
    
    /**
     * Modelo de tabla para los ítems del menú
     */
    private class MenuItemTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Texto", "Icono", "Tooltip", "Habilitado"};
        
        @Override
        public int getRowCount() {
            return model.getItemCount();
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
                case 1: return ImageIcon.class;
                case 3: return Boolean.class;
                default: return String.class;
            }
        }
        
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            SideMenuItem item = model.getItem(rowIndex);
            if (item == null) return null;
            
            switch (columnIndex) {
                case 0: return item.getText();
                case 1: return item.getIcon();
                case 2: return item.getTooltip();
                case 3: return item.isEnabled();
                default: return null;
            }
        }
        
        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            // Crear una copia del modelo actual
            SideMenuModel newModel = cloneModel(model);
            SideMenuItem item = newModel.getItem(rowIndex);
            if (item == null) return;

            switch (columnIndex) {
                case 0:
                    item.setText((String) value);
                    break;
                case 1:
                    if (value instanceof IconData) {
                        IconData iconData = (IconData) value;
                        // Actualizar tanto el icono como su ruta
                        item.setIcon(iconData.icon);
                        item.setIconPath(iconData.path);
                    } else if (value instanceof ImageIcon) {
                        // Manejar el caso antiguo para compatibilidad
                        item.setIcon((ImageIcon) value);
                    }
                    break;
                case 2:
                    item.setTooltip((String) value);
                    break;
                case 3:
                    item.setEnabled((Boolean) value);
                    break;
            }

            // Actualizar el modelo y notificar cambios
            model = newModel;
            fireTableCellUpdated(rowIndex, columnIndex);
            firePropertyChange(); // Notificar cambio a PropertyEditor
        }
    }
    
    /**
     * Renderizador para la columna de iconos
     */
    private class IconRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, "", isSelected, hasFocus, row, column);
            
            if (value instanceof ImageIcon) {
                label.setIcon((ImageIcon) value);
                label.setHorizontalAlignment(JLabel.CENTER);
            } else {
                label.setIcon(null);
            }
            
            return label;
        }
    }
    
    /**
     * Editor para la columna de iconos
     */
    private class IconEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button;
        private ImageIcon currentIcon;
        private String currentIconPath; // Añadir para almacenar la ruta del icono

        public IconEditor() {
            button = new JButton();
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);

            button.addActionListener(e -> {
                // Mostrar diálogo para seleccionar icono
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                        "Imágenes", "jpg", "png", "gif"));

                int result = chooser.showOpenDialog(button);
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        // Guardar la ruta del archivo seleccionado
                        currentIconPath = chooser.getSelectedFile().getPath();

                        // Crear el icono para la previsualización
                        ImageIcon icon = new ImageIcon(currentIconPath);
                        // Redimensionar icono
                        Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                        currentIcon = new ImageIcon(img);
                        button.setIcon(currentIcon);
                        fireEditingStopped();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(button, 
                                "Error al cargar la imagen: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }

        @Override
        public Object getCellEditorValue() {
            // Devolver el iconPath en lugar del ImageIcon
            return new IconData(currentIcon, currentIconPath);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {

            // Inicializar con los valores actuales del ítem
            SideMenuItem item = model.getItem(row);
            if (item != null) {
                currentIcon = item.getIcon();
                currentIconPath = item.getIconPath();
            } else {
                currentIcon = null;
                currentIconPath = null;
            }

            button.setIcon(currentIcon);
            return button;
        }
    }
    
    /**
    * Clase auxiliar para transportar tanto el icono como su ruta
    */
    private class IconData {
        ImageIcon icon;
        String path;

        public IconData(ImageIcon icon, String path) {
            this.icon = icon;
            this.path = path;
        }
    }
}