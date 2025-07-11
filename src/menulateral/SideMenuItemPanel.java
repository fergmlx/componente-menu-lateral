package menulateral;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Panel que representa visualmente un ítem del menú
 */
public class SideMenuItemPanel extends JPanel {
    private SideMenuItem menuItem;
    private JLabel iconLabel;
    private int iconLabelWidth;
    private int textLabelWidth;
    private JLabel textLabel;
    private Font opcionesFont = new Font("Poppins", Font.PLAIN, 14);
    private boolean isHovered = false;
    private boolean isSelected = false;
    private Color hoverColor = Color.WHITE;
    private Color selectedColor = new Color(80, 80, 80);
    private Color textColor = Color.WHITE;
    private Color disabledColor = new Color(120, 120, 120);
    
    public SideMenuItemPanel(SideMenuItem item, int iconLabelWidth) {
        this.menuItem = item;
        this.iconLabelWidth = iconLabelWidth;
        initializePanel();
        setupComponents();
        setupEventListeners();
        updateContent();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(250, 45));
        setBorder(new EmptyBorder(5, 0, 5, 10));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void setupComponents() {
        // Icono (lado izquierdo)
        iconLabel = new JLabel();
        iconLabel.setOpaque(false);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(iconLabelWidth, 32));
        iconLabel.setMinimumSize(new Dimension(32, 32));
        iconLabel.setMaximumSize(new Dimension(32, 32));
        //iconLabel.setBorder(new LineBorder(Color.BLACK));
        
        // Texto (centro)
        textLabel = new JLabel();
        textLabel.setOpaque(false);
        textLabel.setForeground(textColor);
        textLabel.setFont(opcionesFont);
        textLabel.setBorder(new EmptyBorder(3, 0, 0, 0));
        //textLabel.setBorder(new LineBorder(Color.BLACK));
        textLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        add(iconLabel, BorderLayout.WEST);
        add(textLabel, BorderLayout.CENTER);
    }
    
    private void setupEventListeners() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (menuItem.isEnabled()) {
                    isHovered = true;
                    repaint();
                    textLabel.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
                textLabel.setForeground(textColor);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (menuItem.isEnabled()) {
                    // Aquí puedes agregar lógica para manejar clicks
                    fireMenuItemClicked();
                }
            }
        };
        
        addMouseListener(mouseHandler);
    }
    
    private void updateContent() {
        // Actualizar icono
        if (menuItem.getIcon() != null) {
            try {
                // Redimensionar icono si es necesario
                ImageIcon originalIcon = menuItem.getIcon();
                Image img = originalIcon.getImage();
                
                // Redimensionar manteniendo proporción
                Image scaledImg = img.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImg);
                
                iconLabel.setIcon(scaledIcon);
                iconLabel.setText(""); // Limpiar texto si hay icono
                
                System.out.println("Icono cargado para: " + menuItem.getText()); // Debug
                
            } catch (Exception e) {
                System.err.println("Error procesando icono para " + menuItem.getText() + ": " + e.getMessage());
                // Si falla, mostrar un icono por defecto o texto
                setDefaultIcon();
            }
        } else {
            // Si no hay icono, crear uno por defecto o mostrar texto
            setDefaultIcon();
        }
        
        // Actualizar texto
        textLabel.setText(menuItem.getText());
        textLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        // Actualizar tooltip
        setToolTipText(menuItem.getTooltip());
        
        // Actualizar estado enabled/disabled
        updateEnabledState();
        
        revalidate();
        repaint();
    }
    
    private void setDefaultIcon() {
        // Crear un icono por defecto simple
        ImageIcon defaultIcon = createDefaultIcon(24, new Color(150, 150, 150));
        iconLabel.setIcon(defaultIcon);
        iconLabel.setText("");
    }
    
    /**
     * Crea un icono por defecto simple
     */
    private ImageIcon createDefaultIcon(int size, Color color) {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        
        // Dibujar un círculo simple como icono por defecto
        g2d.fillOval(2, 2, size - 4, size - 4);
        
        g2d.dispose();
        return new ImageIcon(img);
    }
    
    private void updateEnabledState() {
        boolean enabled = menuItem.isEnabled();
        iconLabel.setEnabled(enabled);
        textLabel.setEnabled(enabled);
        textLabel.setForeground(enabled ? textColor : disabledColor);
        setCursor(enabled ? new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dibujar fondo según el estado
        if (menuItem.isEnabled()) {
            if (isSelected) {
                g2d.setColor(selectedColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            } else if (isHovered) {
                g2d.setColor(hoverColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }
        }
        
        g2d.dispose();
    }
    
    // Getters y setters
    public SideMenuItem getMenuItem() {
        return menuItem;
    }
    
    public void setMenuItem(SideMenuItem menuItem) {
        this.menuItem = menuItem;
        updateContent();
    }
    
    public boolean isSelected() {
        return isSelected;
    }
    
    public void setSelected(boolean selected) {
        this.isSelected = selected;
        repaint();
    }
    
    public Color getHoverColor() {
        return hoverColor;
    }
    
    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
    }
    
    public Color getSelectedColor() {
        return selectedColor;
    }
    
    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
    }
    
    public Color getTextColor() {
        return textColor;
    }
    
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
        textLabel.setForeground(menuItem.isEnabled() ? textColor : disabledColor);
    }
    
    public Font getOpcionesFont() {
        return opcionesFont;
    }
    
    public void setOpcionesFont(Font opcionesFont) {
        this.opcionesFont = opcionesFont;
        textLabel.setFont(opcionesFont);
    }
    
    // Método para notificar clicks (puedes expandir esto con listeners)
    private void fireMenuItemClicked() {
        // Aquí puedes implementar un sistema de eventos personalizado
        System.out.println("Menu item clicked: " + menuItem.getText());
    }
}