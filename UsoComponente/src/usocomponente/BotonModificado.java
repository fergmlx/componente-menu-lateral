package usocomponente;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class BotonModificado extends JButton {
    
    private Color backgroundColor = new Color(242, 242, 242);
    private Color hoverColor = new Color(230, 230, 230);
    private Color textColor = new Color(50, 50, 50);
    private int cornerRadius = 10;
    private int iconTextGap = 10;
    private boolean isHovered = false;
    
    public BotonModificado() {
        super();
        initialize();
    }
    
    public BotonModificado(String text) {
        super(text);
        initialize();
    }
    
    public BotonModificado(Icon icon) {
        super(icon);
        initialize();
    }
    
    public BotonModificado(String text, Icon icon) {
        super(text, icon);
        initialize();
    }
    
    private void initialize() {
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setForeground(textColor);
        setHorizontalAlignment(SwingConstants.LEFT);
        setIconTextGap(iconTextGap);
        
        // Padding del botón (top, left, bottom, right)
        setBorder(new EmptyBorder(10, 16, 10, 16));
        
        // Listeners para manejar hover
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dibuja el fondo con esquinas redondeadas
        g2d.setColor(isHovered ? hoverColor : backgroundColor);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        
        g2d.dispose();
        super.paintComponent(g);
    }
    
    // Métodos para personalizar el botón
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }
    
    public void setHoverColor(Color color) {
        this.hoverColor = color;
    }
    
    public void setTextColor(Color color) {
        this.textColor = color;
        setForeground(textColor);
    }
    
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }
    
    public void setIconGap(int gap) {
        this.iconTextGap = gap;
        setIconTextGap(gap);
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        // Asegura un tamaño mínimo para el botón
        return new Dimension(
                Math.max(size.width, 120),
                Math.max(size.height, 38)
        );
    }
}