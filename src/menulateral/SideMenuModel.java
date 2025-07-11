package menulateral;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Modelo de datos para el SideMenuComponent
 */
public class SideMenuModel implements Serializable {
    private List<SideMenuItem> items;
    private List<ChangeListener> listeners;
    
    public SideMenuModel() {
        this.items = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }
    
    public void addItem(SideMenuItem item) {
        if (item != null) {
            items.add(item);
            fireChangeEvent();
        }
    }
    
    public void addItem(String text) {
        addItem(new SideMenuItem(text));
    }
    
    public void addItem(String text, javax.swing.ImageIcon icon) {
        addItem(new SideMenuItem(text, icon));
    }
    
    public void insertItem(int index, SideMenuItem item) {
        if (item != null && index >= 0 && index <= items.size()) {
            items.add(index, item);
            fireChangeEvent();
        }
    }
    
    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            fireChangeEvent();
        }
    }
    
    public void removeItem(SideMenuItem item) {
        if (items.remove(item)) {
            fireChangeEvent();
        }
    }
    
    public void removeAllItems() {
        if (!items.isEmpty()) {
            items.clear();
            fireChangeEvent();
        }
    }
    
    public SideMenuItem getItem(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }
    
    public int getItemCount() {
        return items.size();
    }
    
    public List<SideMenuItem> getItems() {
        return new ArrayList<>(items);
    }
    
    public void setItems(List<SideMenuItem> items) {
        this.items = new ArrayList<>(items != null ? items : new ArrayList<>());
        fireChangeEvent();
    }
    
    public int indexOf(SideMenuItem item) {
        return items.indexOf(item);
    }
    
    // Listeners para notificar cambios
    public void addChangeListener(ChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }
    
    private void fireChangeEvent() {
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener listener : listeners) {
            listener.stateChanged(event);
        }
    }
    
    @Override
    public String toString() {
        return "SideMenuModel[" + items.size() + " items]";
    }
}