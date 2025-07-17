package menulateral;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * Representa un elemento individual del menú lateral
 */
public class SideMenuItem implements Serializable {
    private String text;
    private ImageIcon icon;
    private String tooltip;
    private boolean enabled = true;
    private boolean shown = true;
    private Object userData;
    private URL iconUrl;
    private String iconPath;
    private transient ActionListener actionListener;
    
    // Propiedades para submenús
    private boolean hasChildren = false;
    private boolean expanded = false;
    private List<SideMenuItem> children;
    private int level = 0; // Nivel de anidamiento
    
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
        this.children = new ArrayList<>();
    }
    
    public SideMenuItem(String text, String iconPath, String tooltip) {
        this.text = text;
        this.iconPath = iconPath;
        this.iconUrl = resolveUrl(iconPath);
        this.icon = (iconUrl != null) ? new ImageIcon(iconUrl) : null;
        this.tooltip = tooltip;
        this.children = new ArrayList<>();
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
    
    // Métodos para submenús
    public void addChild(SideMenuItem child) {
        if (child != null) {
            child.setLevel(this.level + 1);
            this.children.add(child);
            this.hasChildren = true;
        }
    }
    
    public void removeChild(SideMenuItem child) {
        if (this.children.remove(child)) {
            this.hasChildren = !this.children.isEmpty();
        }
    }
    
    public void removeChild(int index) {
        if (index >= 0 && index < children.size()) {
            this.children.remove(index);
            this.hasChildren = !this.children.isEmpty();
        }
    }
    
    public List<SideMenuItem> getChildren() {
        return new ArrayList<>(children);
    }
    
    public int getChildCount() {
        return children.size();
    }
    
    public SideMenuItem getChild(int index) {
        if (index >= 0 && index < children.size()) {
            return children.get(index);
        }
        return null;
    }
    
    public boolean isHasChildren() {
        return hasChildren;
    }
    
    public boolean isExpanded() {
        return expanded;
    }
    
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
    
    public void toggleExpanded() {
        this.expanded = !this.expanded;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    // Getters y Setters originales
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

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
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
    
    /**
     * Establece el listener que se ejecutará cuando se haga clic en este ítem.
     * 
     * @param actionListener El listener a ejecutar
     */
    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }
    
    /**
     * Obtiene el listener asociado a este ítem.
     * 
     * @return El listener actual o null si no hay ninguno
     */
    public ActionListener getActionListener() {
        return actionListener;
    }
    
    /**
     * Ejecuta la acción asociada a este ítem si existe.
     */
    public void executeAction() {
        if (actionListener != null) {
            actionListener.actionPerformed(new java.awt.event.ActionEvent(
                this, java.awt.event.ActionEvent.ACTION_PERFORMED, getText()));
        }
    }
}