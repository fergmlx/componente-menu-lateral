package menulateral;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * Representa un elemento individual del menú lateral
 */
public class SideMenuItem implements Serializable {
    private String text;
    private ImageIcon icon;
    private String tooltip;
    private boolean enabled = true;
    private Object userData;
    private URL iconUrl;
    private String iconPath;
    
    public SideMenuItem() {
        this("", null);
    }
    
    public SideMenuItem(String text) {
        this(text, null);
    }
    
    public SideMenuItem(String text, String iconPath) {
        this.text = text;
        this.iconPath = iconPath;
        this.iconUrl = resolveUrl(iconPath);
        this.icon = (iconUrl != null) ? new ImageIcon(iconUrl) : null;
    }
    
    public SideMenuItem(String text, String iconPath, String tooltip) {
        this.text = text;
        this.iconPath = iconPath;
        this.iconUrl = resolveUrl(iconPath);
        this.icon = (iconUrl != null) ? new ImageIcon(iconUrl) : null;
        this.tooltip = tooltip;
    }
    
    private URL resolveUrl(String path) {
        if (path == null || path.isEmpty()) return null;

        // 1. Intentar como recurso del classpath (relativo al paquete)
        URL url = getClass().getResource(path);

        // 2. Si no se encuentra en el classpath, intentar como archivo local
        if (url == null) {
            File file = new File(path);
            if (file.exists()) {
                try {
                    url = file.toURI().toURL();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }

        return url;
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
    
    public URL getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(URL iconUrl) {
        this.iconUrl = iconUrl;
    }
    
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
    
    public String getIconPath() {
        return iconPath;
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