package menulateral;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import javax.swing.ImageIcon;

/**
 * Representa un elemento individual del menú lateral
 */
public class SideMenuItem implements Serializable {
    private String text;
    private ImageIcon icon;
    private String tooltip;
    private boolean enabled = true;
    private Object userData; // Para datos personalizados del usuario
    
    public SideMenuItem() {
        this("", null);
    }
    
    public SideMenuItem(String text) {
        this(text, null);
    }
    
    public SideMenuItem(String text, ImageIcon icon) {
        this.text = text;
        this.icon = icon;
    }
    
    public SideMenuItem(String text, ImageIcon icon, String tooltip) {
        this.text = text;
        this.icon = icon;
        this.tooltip = tooltip;
    }
    
    // Getters y Setters
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public ImageIcon getIcon() {
        return icon;
    }
    
    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }
    
    public String getTooltip() {
        return tooltip;
    }
    
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public Object getUserData() {
        return userData;
    }
    
    public void setUserData(Object userData) {
        this.userData = userData;
    }
    
    @Override
    public String toString() {
        return text != null ? text : "";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        SideMenuItem that = (SideMenuItem) obj;
        return text != null ? text.equals(that.text) : that.text == null;
    }
    
    @Override
    public int hashCode() {
        return text != null ? text.hashCode() : 0;
    }
}