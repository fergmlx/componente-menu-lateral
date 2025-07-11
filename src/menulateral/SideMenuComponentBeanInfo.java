package menulateral;

import java.beans.*;
import java.awt.Image;
import javax.swing.ImageIcon;

/**
 * BeanInfo para el componente SideMenuComponent
 * Define las propiedades que aparecerán en el Property Sheet de NetBeans
 */
public class SideMenuComponentBeanInfo extends SimpleBeanInfo {
    
    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            PropertyDescriptor[] properties = new PropertyDescriptor[11];
            
            // Propiedad model (NUEVA)
            properties[0] = new PropertyDescriptor("model", SideMenuComponent.class);
            properties[0].setDisplayName("Menu Model");
            properties[0].setShortDescription("Modelo de datos para los ítems del menú");
            properties[0].setPropertyEditorClass(SideMenuModelEditor.class);
            properties[0].setBound(true);
            
            // Propiedad expanded
            properties[1] = new PropertyDescriptor("expanded", SideMenuComponent.class);
            properties[1].setDisplayName("Expanded");
            properties[1].setShortDescription("Indica si el menú está expandido o colapsado");
            
            // Propiedad collapsedWidth
            properties[2] = new PropertyDescriptor("collapsedWidth", SideMenuComponent.class);
            properties[2].setDisplayName("Collapsed Width");
            properties[2].setShortDescription("Ancho del menú cuando está colapsado");
            
            // Propiedad expandedWidth
            properties[3] = new PropertyDescriptor("expandedWidth", SideMenuComponent.class);
            properties[3].setDisplayName("Expanded Width");
            properties[3].setShortDescription("Ancho del menú cuando está expandido");
            
            // Propiedad backgroundColor
            properties[4] = new PropertyDescriptor("backgroundColor", SideMenuComponent.class);
            properties[4].setDisplayName("Background Color");
            properties[4].setShortDescription("Color de fondo del menú completo");
            
            // Propiedad iconColor
            properties[5] = new PropertyDescriptor("defaultHamburgerIconColor", SideMenuComponent.class);
            properties[5].setDisplayName("Hamburger Color");
            properties[5].setShortDescription("Color del icono de toggle predeterminado");
            
            // Propiedad logoFont
            properties[6] = new PropertyDescriptor("opcionesFont", SideMenuComponent.class);
            properties[6].setDisplayName("Opciones Font");
            properties[6].setShortDescription("Fuente del texto de las opciones");
            
            // Propiedad hamburgerIcon
            properties[7] = new PropertyDescriptor("hamburgerIcon", SideMenuComponent.class);
            properties[7].setDisplayName("Hamburger Icon");
            properties[7].setShortDescription("Icono de hamburger");
            
            // Propiedad closeIcon
            properties[8] = new PropertyDescriptor("closeIcon", SideMenuComponent.class);
            properties[8].setDisplayName("Close Icon");
            properties[8].setShortDescription("Icono de cerrar menú");
            
            // Propiedad logoIcon
            properties[9] = new PropertyDescriptor("logoIcon", SideMenuComponent.class);
            properties[9].setDisplayName("Logo Icon");
            properties[9].setShortDescription("Icono del logo empresa");
            
            // Propiedad hoverColor
            properties[10] = new PropertyDescriptor("hoverColor", SideMenuComponent.class);
            properties[10].setDisplayName("Hover Color");
            properties[10].setShortDescription("Color que toma una opción al pasar el mouse sobre ella");
            
            // Propiedad textColor
            properties[11] = new PropertyDescriptor("textColor", SideMenuComponent.class);
            properties[11].setDisplayName("Text Color");
            properties[11].setShortDescription("Color de texto de las opciones");
            
            return properties;
            
        } catch (IntrospectionException e) {
            e.printStackTrace();
            return super.getPropertyDescriptors();
        }
    }
    
    @Override
    public Image getIcon(int iconKind) {
        // Puedes crear un icono personalizado para el componente en la paleta
        // Por ahora devolvemos null para usar el icono por defecto
        return null;
    }
    
    @Override
    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptor descriptor = new BeanDescriptor(SideMenuComponent.class);
        descriptor.setDisplayName("Side Menu Component");
        descriptor.setShortDescription("Componente de menú lateral expandible/colapsable con modelo de datos");
        return descriptor;
    }
}