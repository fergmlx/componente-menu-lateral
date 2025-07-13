package menulateral;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.beans.BeanProperty;
import java.io.Serializable;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Componente de menú lateral personalizado para NetBeans
 * Soporta expansión/colapso y personalización de propiedades
 */
public class SideMenuComponent extends JPanel implements Serializable {
    
    // Propiedades del componente
    private boolean expanded = false;
    private int collapsedWidth = 60;
    private int expandedWidth = 250;
    private Color backgroundColor = Color.WHITE;
    private Color defaultHamburgerIconColor = Color.BLACK;
    private Color hoverColor = Color.CYAN;
    private Color textHoverColor = Color.BLACK;
    private Color textColor = Color.BLACK;
    private String logoText = "";
    private Font opcionesFont = new Font("Poppins SemiBold", Font.PLAIN, 14);
    private ChangeListener modelChangeListener;
    
    // Modelo de datos
    private SideMenuModel model;
    
    // Iconos personalizables
    private Icon hamburgerIcon;
    private Icon closeIcon;
    private Icon logoIcon;
    
    // Componentes internos
    private JPanel headerPanel;
    private JLabel logoLabel;
    private JButton toggleButton;
    private JPanel contentPanel;
    
    /**
     * Clase interna nombrada para el ChangeListener
     * Esto evita problemas de serialización con clases anónimas
     */
    private class ModelChangeListener implements ChangeListener, Serializable {
        @Override
        public void stateChanged(ChangeEvent e) {
            updateMenuItems();
        }
    }
    
    /**
    * Clase interna nombrada para el ExpandCollapseListener
    * Esto evita problemas de serialización con expresiones lambda
    */
    private class ItemExpandCollapseListener implements SideMenuItemPanel.ExpandCollapseListener, Serializable {
        @Override
        public void onExpandCollapse(SideMenuItem item) {
            updateMenuItems();
        }
    }
    
    public SideMenuComponent() {
        // Inicializar el modelo
        this.model = new SideMenuModel();
        
        initializeComponent();
        setupLayout();
        setupEventListeners();
        setupModelListener();
        updateLogo(); // Actualizar logo inicial
    }
    
    private void initializeComponent() {
        setLayout(new BorderLayout());
        setOpaque(false); // Clave para transparencia
        setPreferredSize(new Dimension(collapsedWidth, 400));
        setBorder(new EmptyBorder(0, 0, 0, 0));
        
        // Panel superior (header) - usa el mismo backgroundColor
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false); // Clave para transparencia
        headerPanel.setPreferredSize(new Dimension(collapsedWidth, 60));
        
        // Logo (inicialmente oculto)
        logoLabel = new JLabel(logoText, SwingConstants.LEFT);
        logoLabel.setBorder(new EmptyBorder(15, 10, 10, 10));
        //logoLabel.setBorder(new LineBorder(Color.BLACK));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setVisible(false);
        
        // Inicializar iconos por defecto
        createDefaultIcons();
        
        // Botón toggle
        toggleButton = new JButton();
        updateToggleButtonIcon();
        toggleButton.setBackground(Color.WHITE);
        toggleButton.setForeground(defaultHamburgerIconColor);
        toggleButton.setBorder(null);
        toggleButton.setFocusPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setPreferredSize(new Dimension(collapsedWidth, getPreferredSize().height));
        toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Panel de contenido - usa el mismo backgroundColor
        contentPanel = new JPanel();
        contentPanel.setOpaque(false); // Clave para transparencia
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        SideMenuItem item = new SideMenuItem("Inicio", "/icons/home.png");
        item.setTooltip("Ir a Inicio");
        model.addItem(item);
        item = new SideMenuItem("Perfil", "/icons/perfil.png");
        item.setTooltip("Ver perfil de usuario");
        model.addItem(item);
        item = new SideMenuItem("Mensajes", "/icons/mensaje.png");
        item.setTooltip("Ver mensajes");
        model.addItem(item);
        item = new SideMenuItem("Configuración", "/icons/config.png");
        item.setTooltip("Ir a configuración");
        model.addItem(item);
        item = new SideMenuItem("Ayuda", "/icons/ayuda.png");
        item.setTooltip("Ayuda");
        model.addItem(item);
        this.setModel(model);
    }
    
    private void setupLayout() {
        // Configurar header panel
        headerPanel.add(logoLabel, BorderLayout.CENTER);
        headerPanel.add(toggleButton, BorderLayout.EAST);
        
        // Agregar componentes principales
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        
        updateLayout();
    }
    
    private void setupEventListeners() {
        toggleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                toggleButton.setBackground(new Color(70, 70, 70));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                toggleButton.setBackground(Color.WHITE);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleMenu();
            }
        });
    }
    
    private void updateMenuItems() {
        try {
            contentPanel.removeAll();

            if (model == null) return; // Protección contra modelo nulo

            // Usar un método recursivo para mostrar items con jerarquía
            for (SideMenuItem item : model.getItems()) {
                try {
                    addMenuItemToPanel(item, contentPanel);
                } catch (Exception itemEx) {
                    System.err.println("Error procesando ítem: " + itemEx.getMessage());
                }
            }

            // Agregar espacio flexible al final
            contentPanel.add(Box.createVerticalGlue());

            revalidate();
            repaint();
        } catch (Exception ex) {
            System.err.println("Error actualizando ítems de menú: " + ex.getMessage());
        }
    }
    
    // Método recursivo para añadir items y sus hijos
    private void addMenuItemToPanel(SideMenuItem item, JPanel parentPanel) {
        // Crear el panel para este ítem
        SideMenuItemPanel itemPanel = new SideMenuItemPanel(item, collapsedWidth);
        itemPanel.setOpcionesFont(opcionesFont);
        itemPanel.setHoverColor(hoverColor);
        itemPanel.setTextHoverColor(textHoverColor);
        itemPanel.setTextColor(textColor);

        // Informar al panel si el menú está expandido o no
        itemPanel.updateMenuExpandedState(expanded);

        // Establecer listener para expansión/colapso usando la clase nombrada
        itemPanel.setExpandCollapseListener(new ItemExpandCollapseListener());

        parentPanel.add(itemPanel);
        parentPanel.add(Box.createVerticalStrut(2)); // Pequeño espaciado

        // Si tiene hijos y está expandido, mostrar los hijos
        if (item.isHasChildren() && item.isExpanded()) {
            for (SideMenuItem child : item.getChildren()) {
                addMenuItemToPanel(child, parentPanel);
            }
        }
    }
    
    /**
    * Busca un ítem por su texto y le asigna una acción.
    * 
    * @param text Texto del ítem a buscar
    * @param listener Acción a ejecutar cuando se haga clic en el ítem
    * @return true si se encontró y configuró el ítem, false en caso contrario
    */
    public boolean setMenuItemAction(String text, ActionListener listener) {
        SideMenuItem item = findMenuItemByText(text);
        if (item != null) {
            item.setActionListener(listener);
            return true;
        }
        return false;
    }

   /**
    * Busca un ítem por su texto en todo el modelo (incluyendo ítems anidados).
    * 
    * @param text Texto del ítem a buscar
    * @return El ítem encontrado o null si no existe
    */
    public SideMenuItem findMenuItemByText(String text) {
        if (model == null) return null;

        // Buscar en todos los ítems de nivel superior y sus hijos
        for (SideMenuItem item : model.getItems()) {
            SideMenuItem found = findItemByTextRecursive(item, text);
            if (found != null) return found;
        }
        return null;
    }

   /**
    * Método recursivo para buscar un ítem por texto.
    */
    private SideMenuItem findItemByTextRecursive(SideMenuItem current, String text) {
        // Verificar si este es el ítem que buscamos
        if (text.equals(current.getText())) {
            return current;
        }

        // Buscar en los hijos si los tiene
        if (current.isHasChildren()) {
            for (SideMenuItem child : current.getChildren()) {
                SideMenuItem found = findItemByTextRecursive(child, text);
                if (found != null) return found;
            }
        }

        return null;
    }
    
    /**
    * Método toggleMenu modificado para actualizar el estado de expansión
    */
    private void toggleMenu() {
        expanded = !expanded;
        updateLayout();
        updateItemsExpandedState(); // Actualizar estado de expansión en los ítems
        animateResize();
    }
    
    /**
    * Método para actualizar el estado de los ítems cuando el menú cambia
    */
    private void updateItemsExpandedState() {
        // Recorrer todos los componentes del contentPanel
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof SideMenuItemPanel) {
                ((SideMenuItemPanel) comp).updateMenuExpandedState(expanded);
            }
        }
    }
    
    private void updateLayout() {
        if (expanded) {
            // Menú expandido
            updateToggleButtonIcon();
            logoLabel.setVisible(true);
            
            // Mover el botón a la derecha
            headerPanel.remove(toggleButton);
            headerPanel.add(toggleButton, BorderLayout.EAST);
            
        } else {
            // Menú colapsado
            updateToggleButtonIcon();
            logoLabel.setVisible(false);
            
            // Mover el botón a la derecha
            headerPanel.remove(toggleButton);
            headerPanel.add(toggleButton, BorderLayout.EAST);
            
            // Ajustar padding del botón
            toggleButton.setBorder(new EmptyBorder(10, 10, 10, 10));
        }
        
        revalidate();
        repaint();
    }
    
    private void animateResize() {
        Timer timer = new Timer(10, null);
        final int targetWidth = expanded ? expandedWidth : collapsedWidth;
        final int currentWidth = getPreferredSize().width;
        final int step = (targetWidth - currentWidth) / 10;
        
        timer.addActionListener(e -> {
            int newWidth = getPreferredSize().width + step;
            
            if ((step > 0 && newWidth >= targetWidth) || (step < 0 && newWidth <= targetWidth)) {
                newWidth = targetWidth;
                timer.stop();
            }
            
            setPreferredSize(new Dimension(newWidth, getPreferredSize().height));
            headerPanel.setPreferredSize(new Dimension(newWidth, 60));
            
            Container parent = getParent();
            if (parent != null) {
                parent.revalidate();
                parent.repaint();
            }
        });
        
        timer.start();
    }
    
    /**
     * Crea iconos por defecto usando gráficos vectoriales
     */
    private void createDefaultIcons() {
        // Crear icono hamburger
        closeIcon = hamburgerIcon = createHamburgerIcon(24, defaultHamburgerIconColor);
    }
    
    /**
     * Crea un icono de hamburger (3 barras horizontales)
     */
    private ImageIcon createHamburgerIcon(int size, Color color) {
        return new ImageIcon(createIconImage(size, color, "hamburger"));
    }
    
    /**
     * Crea una imagen de icono vectorial
     */
    private java.awt.image.BufferedImage createIconImage(int size, Color color, String type) {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        // Anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        if ("hamburger".equals(type)) {
            // Dibujar 3 líneas horizontales
            int lineWidth = size - 6;
            int startX = 3;
            int line1Y = size / 4;
            int line2Y = size / 2;
            int line3Y = (size * 3) / 4;
            
            g2d.drawLine(startX, line1Y, startX + lineWidth, line1Y);
            g2d.drawLine(startX, line2Y, startX + lineWidth, line2Y);
            g2d.drawLine(startX, line3Y, startX + lineWidth, line3Y);
            
        }
        
        g2d.dispose();
        return img;
    }
    
    /**
     * Actualiza el icono del botón toggle según el estado
     */
    private void updateToggleButtonIcon() {
        if (expanded) {
            if (closeIcon != null) {
                toggleButton.setIcon(closeIcon);
                toggleButton.setText("");
            }
        } else {
            if (hamburgerIcon != null) {
                toggleButton.setIcon(hamburgerIcon);
                toggleButton.setText("");
            }
        }
    }
    
    /**
     * Actualiza el logo con icono o texto
     */
    private void updateLogo() {
        if (logoIcon != null) {
            int width = logoLabel.getWidth();
            int height = logoLabel.getHeight();

            if (width <= 0 || height <= 0) {
                width = 50;
                height = 50;
            }

            Icon scaledIcon = scaleIconToSize(logoIcon, width, height);
            logoLabel.setIcon(scaledIcon);
            logoLabel.setBorder(new EmptyBorder(0, 25, 0, 0));
            logoLabel.setText("");
        } else {
            logoLabel.setIcon(null);
            logoLabel.setText(logoText);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        // No llamar a super.paintComponent() para evitar pintar el fondo automáticamente
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Anti-aliasing para bordes suaves
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dibujar fondo con bordes redondeados usando backgroundColor
        g2d.setColor(backgroundColor);
        g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
        
        g2d.dispose();
    }
    
    private Icon scaleIconToSize(Icon icon, int width, int height) {
        // Convertir Icon a ImageIcon para obtener la Image
        ImageIcon imageIcon;
        if (icon instanceof ImageIcon) {
            imageIcon = (ImageIcon) icon;
        } else {
            // Si no es ImageIcon, convertirlo a BufferedImage primero
            BufferedImage bufferedImage = new BufferedImage(
                icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            icon.paintIcon(null, g2d, 0, 0);
            g2d.dispose();
            imageIcon = new ImageIcon(bufferedImage);
        }

        // Tu método exacto
        Image img = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
    
    // === MÉTODOS DEL MODELO ===
    
    /**
     * Obtiene el modelo de datos del menú
     */
    @BeanProperty(description = "Modelo de datos para los ítems del menú")
    public SideMenuModel getModel() {
        return model;
    }
    
    public void setModel(SideMenuModel model) {
        try {
            if (this.model != null && modelChangeListener != null) {
                // Remover listener del modelo anterior
                this.model.removeChangeListener(modelChangeListener);
            }

            this.model = (model != null) ? model : new SideMenuModel();

            // Agregar listener al nuevo modelo
            if (modelChangeListener == null) {
                modelChangeListener = new ModelChangeListener();
            }
            this.model.addChangeListener(modelChangeListener);

            // Actualizar la vista inmediatamente
            SwingUtilities.invokeLater(() -> {
                try {
                    updateMenuItems();
                    revalidate();
                    repaint();
                } catch (Exception ex) {
                    System.err.println("Error actualizando ítems del menú: " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Error en setModel: " + e.getMessage());
        }
    }
    
    private void setupModelListener() {
        // Crear el listener usando la clase nombrada en lugar de una anónima
        modelChangeListener = new ModelChangeListener();
        model.addChangeListener(modelChangeListener);
    }
    
    public void addMenuItem(String text) {
        model.addItem(new SideMenuItem(text));
    }

    public void addMenuItem(String text, String iconPath) {
        try {
            model.addItem(new SideMenuItem(text, iconPath));
        } catch (Exception e) {
            System.err.println("Error cargando icono desde: " + iconPath);
            // Agregar sin icono si falla
            model.addItem(new SideMenuItem(text));
        }
    }

    public void addMenuItem(SideMenuItem item) {
        model.addItem(item);
    }
    
    public void removeMenuItem(int index) {
        model.removeItem(index);
    }
    
    public void removeMenuItem(SideMenuItem item) {
        model.removeItem(item);
    }
    
    public void clearMenuItems() {
        model.removeAllItems();
    }
    
    public SideMenuItem getMenuItem(int index) {
        return model.getItem(index);
    }
    
    public int getMenuItemCount() {
        return model.getItemCount();
    }
    
    // Getters y Setters para las propiedades del JavaBean
    public boolean isExpanded() {
        return expanded;
    }
    
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        updateLayout();
        animateResize();
    }
    
    public int getCollapsedWidth() {
        return collapsedWidth;
    }
    
    public void setCollapsedWidth(int collapsedWidth) {
        this.collapsedWidth = collapsedWidth;
        if (!expanded) {
            setPreferredSize(new Dimension(collapsedWidth, getPreferredSize().height));
        }
        updateMenuItems();
    }
    
    public int getExpandedWidth() {
        return expandedWidth;
    }
    
    public void setExpandedWidth(int expandedWidth) {
        this.expandedWidth = expandedWidth;
        if (expanded) {
            setPreferredSize(new Dimension(expandedWidth, getPreferredSize().height));
        }
    }
    
    public Color getBackgroundColor() {
        return backgroundColor;
    }
    
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        // No necesitas setBackground() porque usamos paintComponent personalizado
        repaint();
    }
    
    public Color getDefaultHamburgerIconColor() {
        return defaultHamburgerIconColor;
    }
    
    public void setDefaultHamburgerIconColor(Color iconColor) {
        this.defaultHamburgerIconColor = iconColor;
        toggleButton.setForeground(iconColor);
        // Recrear iconos por defecto con el nuevo color
        createDefaultIcons();
        updateToggleButtonIcon();
    }
    
    public Color getHoverColor() {
        return hoverColor;
    }
    
    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
    }
    
    public Color getTextHoverColor() {
        return textHoverColor;
    }
    
    public void setTextHoverColor(Color textHoverColor) {
        this.textHoverColor = textHoverColor;
    }
    
    public Color getTextColor() {
        return textColor;
    }
    
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
        SwingUtilities.invokeLater(() -> {
            updateMenuItems();
            revalidate();
            repaint();
        });
    }
    
    public Font getOpcionesFont() {
        return opcionesFont;
    }
    
    public void setOpcionesFont(Font opcionesFont) {
        this.opcionesFont = opcionesFont;
        SwingUtilities.invokeLater(() -> {
            updateMenuItems();
            revalidate();
            repaint();
        });
    }
    
    // Getters y setters para los iconos
    public Icon getHamburgerIcon() {
        return hamburgerIcon;
    }
    
    public void setHamburgerIcon(Icon hamburgerIcon) {
        this.hamburgerIcon = hamburgerIcon;
        if (!expanded) {
            updateToggleButtonIcon();
        }
    }
    
    public Icon getCloseIcon() {
        return closeIcon;
    }
    
    public void setCloseIcon(Icon closeIcon) {
        this.closeIcon = closeIcon;
        if (expanded) {
            updateToggleButtonIcon();
        }
    }
    
    public Icon getLogoIcon() {
        return logoLabel.getIcon();
    }
    
    public void setLogoIcon(Icon logoIcon) {
        this.logoIcon = logoIcon;
        updateLogo();
    }
}