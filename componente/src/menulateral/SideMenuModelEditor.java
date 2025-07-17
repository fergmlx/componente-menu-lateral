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
    private JTable table;
    private JButton addChildButton;
    
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

            // Generar código para todos los elementos (recursivamente)
            for (SideMenuItem item : model.getItems()) {
                generateItemCode(sb, item, null);
            }

            sb.append("}}");
        }

        return sb.toString();
    }
    
    /**
     * Genera código recursivamente para un ítem y sus hijos
     */
    private void generateItemCode(StringBuilder sb, SideMenuItem item, String parentVar) {
        // Variable para este ítem (necesaria para referenciar en hijos)
        String itemVar = null;
        
        if (parentVar == null) {
            // Es un ítem de nivel superior, agregarlo directamente al modelo
            sb.append("addItem(");
        } else {
            // Es un hijo, necesitamos crear una variable temporal
            itemVar = "item" + System.identityHashCode(item) % 10000;
            sb.append("menulateral.SideMenuItem ").append(itemVar).append(" = ");
        }
        
        // Crear el constructor del ítem
        sb.append("new menulateral.SideMenuItem(");
        
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
        
        sb.append(")");
        
        // Si no tiene padre, terminar la llamada a addItem
        if (parentVar == null) {
            sb.append("); ");
            // Si este ítem necesitará ser referenciado, guardar en variable
            if (item.isHasChildren()) {
                itemVar = "item" + System.identityHashCode(item) % 10000;
                sb.append("menulateral.SideMenuItem ").append(itemVar)
                  .append(" = getItem(").append(model.indexOf(item)).append("); ");
            }
        } else {
            sb.append("; ");
            // Agregar este ítem como hijo del padre
            sb.append(parentVar).append(".addChild(").append(itemVar).append("); ");
        }
        
        // Si está deshabilitado, añadir código para deshabilitarlo
        if (!item.isEnabled()) {
            if (itemVar != null) {
                sb.append(itemVar).append(".setEnabled(false); ");
            } else {
                sb.append("getItem(").append(model.indexOf(item)).append(").setEnabled(false); ");
            }
        }
        
        // Si no se muestra entonces se oculta
        if (!item.isShown()) {
            if (itemVar != null) {
                sb.append(itemVar).append(".setShown(false); ");
            } else {
                sb.append("getItem(").append(model.indexOf(item)).append(").setShown(false); ");
            }
        }
        
        // Generar código para todos los hijos
        if (item.isHasChildren()) {
            for (SideMenuItem child : item.getChildren()) {
                generateItemCode(sb, child, itemVar);
            }
        }
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
        table = new JTable(tableModel);
        table.setRowHeight(25);
        
        // Configurar renderizadores y editores para la columna de iconos
        table.getColumnModel().getColumn(1).setCellRenderer(new IconRenderer());
        table.getColumnModel().getColumn(1).setCellEditor(new IconEditor());
        
        // Configurar renderer para mostrar indentación en la columna de texto
        table.getColumnModel().getColumn(0).setCellRenderer(new IndentedTextRenderer());
        
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
        
        addChildButton = new JButton("Agregar Hijo");
        addChildButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                addChildItem(selectedRow);
            }
        });
        // Inicialmente desactivado hasta que se seleccione una fila
        addChildButton.setEnabled(false);
        
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
        
        // Listener de selección para habilitar/deshabilitar botones
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                addChildButton.setEnabled(table.getSelectedRow() >= 0);
            }
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(addChildButton);
        buttonPanel.add(moveUpButton);
        buttonPanel.add(moveDownButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Agrega un nuevo ítem al modelo (nivel superior)
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
     * Agrega un nuevo ítem hijo al ítem seleccionado
     */
    private void addChildItem(int parentIndex) {
        // Obtener todos los ítems aplanados (incluyendo hijos)
        List<FlatMenuItem> flatItems = getFlatMenuItems();
        if (parentIndex < 0 || parentIndex >= flatItems.size()) return;
        
        FlatMenuItem parentFlatItem = flatItems.get(parentIndex);
        SideMenuItem parentItem = parentFlatItem.item;
        
        // Crear nuevo ítem hijo
        SideMenuItem newChild = new SideMenuItem("Hijo de " + parentItem.getText());
        newChild.setLevel(parentItem.getLevel() + 1);
        
        // Crear una copia del modelo actual para modificarlo
        SideMenuModel newModel = cloneModel(model);
        
        // Buscar el ítem padre en el nuevo modelo clonado
        SideMenuItem newParentItem = findItemById(newModel, parentItem);
        if (newParentItem != null) {
            // Agregar el hijo al padre
            newParentItem.addChild(newChild);
            // Expandir el padre para mostrar los hijos
            newParentItem.setExpanded(true);
        }
        
        // Establecer el nuevo modelo y notificar cambio
        model = newModel;
        tableModel.fireTableDataChanged();
        firePropertyChange();
    }
    
    /**
     * Busca un ítem en el modelo basado en la identidad de otro ítem
     * (Usamos texto y nivel como identificador aproximado)
     */
    private SideMenuItem findItemById(SideMenuModel targetModel, SideMenuItem sourceItem) {
        // Buscar en ítems de nivel superior
        for (SideMenuItem item : targetModel.getItems()) {
            SideMenuItem found = findItemInHierarchy(item, sourceItem);
            if (found != null) return found;
        }
        return null;
    }
    
    private SideMenuItem findItemInHierarchy(SideMenuItem currentItem, SideMenuItem targetItem) {
        // Verificar si este es el ítem que buscamos
        if (currentItem.getText().equals(targetItem.getText()) && 
            currentItem.getLevel() == targetItem.getLevel()) {
            return currentItem;
        }
        
        // Buscar en los hijos
        if (currentItem.isHasChildren()) {
            for (SideMenuItem child : currentItem.getChildren()) {
                SideMenuItem found = findItemInHierarchy(child, targetItem);
                if (found != null) return found;
            }
        }
        
        return null;
    }
    
    /**
     * Elimina un ítem del modelo
     */
    private void removeItem(int index) {
        // Obtener lista aplanada de ítems
        List<FlatMenuItem> flatItems = getFlatMenuItems();
        if (index < 0 || index >= flatItems.size()) return;
        
        FlatMenuItem flatItem = flatItems.get(index);
        SideMenuItem itemToRemove = flatItem.item;
        SideMenuItem parentItem = flatItem.parent;
        
        // Crear una copia del modelo actual
        SideMenuModel newModel = cloneModel(model);
        
        if (parentItem == null) {
            // Es un ítem de nivel superior
            newModel.removeItem(model.indexOf(itemToRemove));
        } else {
            // Es un ítem hijo
            SideMenuItem newParent = findItemById(newModel, parentItem);
            if (newParent != null) {
                for (int i = 0; i < newParent.getChildCount(); i++) {
                    SideMenuItem child = newParent.getChild(i);
                    if (child.getText().equals(itemToRemove.getText()) &&
                        child.getLevel() == itemToRemove.getLevel()) {
                        newParent.removeChild(i);
                        break;
                    }
                }
            }
        }
        
        // Establecer el nuevo modelo y notificar cambio
        model = newModel;
        tableModel.fireTableDataChanged();
        firePropertyChange();
    }
    
    /**
     * Mueve un ítem de una posición a otra (solo para items del mismo nivel)
     */
    private void moveItem(int fromIndex, int toIndex) {
        // Obtener lista aplanada de ítems
        List<FlatMenuItem> flatItems = getFlatMenuItems();
        if (fromIndex < 0 || fromIndex >= flatItems.size() || 
            toIndex < 0 || toIndex >= flatItems.size()) return;
        
        FlatMenuItem fromItem = flatItems.get(fromIndex);
        FlatMenuItem toItem = flatItems.get(toIndex);
        
        // Solo permitir mover items en el mismo nivel y con el mismo padre
        if (fromItem.parent != toItem.parent || fromItem.item.getLevel() != toItem.item.getLevel()) {
            JOptionPane.showMessageDialog(customPanel, 
                "Solo se pueden mover elementos dentro del mismo nivel y padre",
                "Error al mover", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Crear una copia del modelo actual
        SideMenuModel newModel = cloneModel(model);
        
        if (fromItem.parent == null) {
            // Son ítems de nivel superior
            List<SideMenuItem> items = new ArrayList<>(model.getItems());
            SideMenuItem item = items.get(model.indexOf(fromItem.item));
            items.remove(model.indexOf(fromItem.item));
            items.add(model.indexOf(toItem.item), item);
            newModel.setItems(items);
        } else {
            // Son ítems hijo del mismo padre
            SideMenuItem newParent = findItemById(newModel, fromItem.parent);
            if (newParent != null) {
                List<SideMenuItem> children = new ArrayList<>(newParent.getChildren());
                // Encontrar índices en la lista de hijos
                int fromChildIndex = -1, toChildIndex = -1;
                for (int i = 0; i < children.size(); i++) {
                    SideMenuItem child = children.get(i);
                    if (child.getText().equals(fromItem.item.getText())) fromChildIndex = i;
                    if (child.getText().equals(toItem.item.getText())) toChildIndex = i;
                }
                
                if (fromChildIndex >= 0 && toChildIndex >= 0) {
                    SideMenuItem item = children.get(fromChildIndex);
                    children.remove(fromChildIndex);
                    children.add(toChildIndex, item);
                    
                    // Recrear la lista de hijos
                    newParent.getChildren().clear();
                    for (SideMenuItem child : children) {
                        newParent.addChild(child);
                    }
                }
            }
        }
        
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
        
        // Clonar los items de nivel superior
        for (SideMenuItem item : sourceModel.getItems()) {
            SideMenuItem newItem = cloneItemWithChildren(item);
            newModel.addItem(newItem);
        }
        
        return newModel;
    }
    
    /**
     * Clona un ítem y sus hijos recursivamente
     */
    private SideMenuItem cloneItemWithChildren(SideMenuItem item) {
        // Crear un nuevo ítem con las mismas propiedades
        SideMenuItem newItem;
        if (item.getIconPath() != null && !item.getIconPath().isEmpty()) {
            newItem = new SideMenuItem(
                    item.getText(),
                    item.getIconPath(),
                    item.getTooltip());
        } else {
            newItem = new SideMenuItem(
                    item.getText(),
                    null,
                    item.getTooltip());
        }
        
        // Copiar otras propiedades
        newItem.setEnabled(item.isEnabled());
        newItem.setUserData(item.getUserData());
        newItem.setLevel(item.getLevel());
        newItem.setExpanded(item.isExpanded());
        
        // Si tiene URL de icono, preservarla
        if (item.getIconUrl() != null) {
            newItem.setIconUrl(item.getIconUrl());
        }
        
        // Clonar hijos recursivamente
        if (item.isHasChildren()) {
            for (SideMenuItem child : item.getChildren()) {
                SideMenuItem newChild = cloneItemWithChildren(child);
                newItem.addChild(newChild);
            }
        }
        
        return newItem;
    }
    
    /**
     * Clase auxiliar para representar un ítem en la lista plana
     * con referencia a su padre
     */
    private class FlatMenuItem {
        SideMenuItem item;
        SideMenuItem parent;
        
        public FlatMenuItem(SideMenuItem item, SideMenuItem parent) {
            this.item = item;
            this.parent = parent;
        }
    }
    
    /**
     * Obtiene una lista aplanada de todos los ítems visibles
     * incluyendo los hijos expandidos
     */
    private List<FlatMenuItem> getFlatMenuItems() {
        List<FlatMenuItem> flatItems = new ArrayList<>();
        
        // Agregar ítems de nivel superior y sus hijos recursivamente
        for (SideMenuItem item : model.getItems()) {
            flatItems.add(new FlatMenuItem(item, null));
            if (item.isHasChildren() && item.isExpanded()) {
                addChildrenToFlatList(item, flatItems);
            }
        }
        
        return flatItems;
    }
    
    /**
     * Agrega los hijos de un ítem a la lista plana recursivamente
     */
    private void addChildrenToFlatList(SideMenuItem parent, List<FlatMenuItem> flatItems) {
        for (SideMenuItem child : parent.getChildren()) {
            flatItems.add(new FlatMenuItem(child, parent));
            if (child.isHasChildren() && child.isExpanded()) {
                addChildrenToFlatList(child, flatItems);
            }
        }
    }
    
    /**
     * Modelo de tabla para los ítems del menú
     */
    private class MenuItemTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Texto", "Icono", "Tooltip", "Habilitado", "Mostrado", "Expandido"};
        private List<FlatMenuItem> flatItems;
        
        public MenuItemTableModel() {
            refreshFlatItems();
        }
        
        private void refreshFlatItems() {
            this.flatItems = getFlatMenuItems();
        }
        
        @Override
        public int getRowCount() {
            refreshFlatItems();
            return flatItems.size();
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
                case 3: case 4: case 5: return Boolean.class;
                default: return String.class;
            }
        }
        
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            // La columna "Expandido" solo es editable si el ítem tiene hijos
            if (columnIndex == 5) {
                return flatItems.get(rowIndex).item.isHasChildren();
            }
            return true;
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex < 0 || rowIndex >= flatItems.size()) return null;
            
            SideMenuItem item = flatItems.get(rowIndex).item;
            if (item == null) return null;
            
            switch (columnIndex) {
                case 0: return item.getText();
                case 1: return item.getIcon();
                case 2: return item.getTooltip();
                case 3: return item.isEnabled();
                case 4: return item.isShown();
                case 5: return item.isExpanded();
                default: return null;
            }
        }
        
        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            if (rowIndex < 0 || rowIndex >= flatItems.size()) return;
            
            // Obtener el ítem original y su padre
            FlatMenuItem flatItem = flatItems.get(rowIndex);
            SideMenuItem originalItem = flatItem.item;
            SideMenuItem parentItem = flatItem.parent;
            
            // Crear una copia del modelo actual
            SideMenuModel newModel = cloneModel(model);
            
            // Encontrar el ítem correspondiente en el nuevo modelo
            SideMenuItem newItem;
            if (parentItem == null) {
                // Es un ítem de nivel superior
                newItem = newModel.getItem(model.indexOf(originalItem));
            } else {
                // Es un ítem hijo
                SideMenuItem newParent = findItemById(newModel, parentItem);
                if (newParent == null) return;
                
                // Buscar el hijo correspondiente
                newItem = null;
                for (SideMenuItem child : newParent.getChildren()) {
                    if (child.getText().equals(originalItem.getText()) &&
                        child.getLevel() == originalItem.getLevel()) {
                        newItem = child;
                        break;
                    }
                }
                if (newItem == null) return;
            }
            
            // Actualizar la propiedad correspondiente
            switch (columnIndex) {
                case 0: // Texto
                    newItem.setText((String) value);
                    break;
                case 1: // Icono
                    if (value instanceof IconData) {
                        IconData iconData = (IconData) value;
                        newItem.setIcon(iconData.icon);
                        newItem.setIconPath(iconData.path);
                    } else if (value instanceof ImageIcon) {
                        newItem.setIcon((ImageIcon) value);
                    }
                    break;
                case 2: // Tooltip
                    newItem.setTooltip((String) value);
                    break;
                case 3: // Habilitado
                    newItem.setEnabled((Boolean) value);
                    break;
                case 4: // Mostrado
                    newItem.setShown((Boolean) value);
                    break;
                case 5: // Expandido
                    newItem.setExpanded((Boolean) value);
                    break;
            }
            
            // Actualizar el modelo y notificar cambios
            model = newModel;
            fireTableDataChanged();
            firePropertyChange();
        }
    }
    
    /**
     * Renderizador para la columna de texto con indentación
     */
    private class IndentedTextRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            
            // Añadir indentación basada en el nivel del ítem
            if (row >= 0 && row < getFlatMenuItems().size()) {
                SideMenuItem item = getFlatMenuItems().get(row).item;
                int level = item.getLevel();
                
                // Añadir prefijo visual según el nivel y si tiene hijos
                String prefix = "";
                for (int i = 0; i < level; i++) {
                    prefix += "   "; // 3 espacios por nivel
                }
                
                if (item.isHasChildren()) {
                    prefix += item.isExpanded() ? "▼ " : "► ";
                } else if (level > 0) {
                    prefix += "• ";
                }
                
                label.setText(prefix + value);
            }
            
            return label;
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
        private String currentIconPath;

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
            return new IconData(currentIcon, currentIconPath);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {

            // Inicializar con los valores actuales del ítem
            if (row >= 0 && row < getFlatMenuItems().size()) {
                SideMenuItem item = getFlatMenuItems().get(row).item;
                if (item != null) {
                    currentIcon = item.getIcon();
                    currentIconPath = item.getIconPath();
                }
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