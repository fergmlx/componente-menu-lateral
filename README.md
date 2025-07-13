# Componente Menu Lateral

<p align="center">
  <img src="componente/src/capturas/banner.png" alt="Banner del Componente Menu Lateral" width="100%">
</p>

<p align="center">
  <a href="#descripción">Descripción</a> •
  <a href="#capturas-de-pantalla">Capturas</a> •
  <a href="#características-principales">Características</a> •
  <a href="#métodos-principales">Métodos</a> •
  <a href="#instrucciones-de-uso">Uso</a> •
  <a href="#créditos">Créditos</a>
</p>

## Descripción

Componente de menú lateral personalizable desarrollado en Java. Este componente permite agregar y eliminar opciones desde un editor personalizado que ofrece agregar, eliminar, subir y bajar
opciones, así como agregar hijos a cada opción. Además, cuenta con un método que permite agregar un ActionListener a cada opción.

## Capturas de Pantalla

<div align="center">
  <img src="componente/src/capturas/ss1.png" alt="Menú desplegado" width="48%">
</div>

<div align="center">
  <img src="componente/src/capturas/ss3.png" alt="Personalización de colores, agregación de items y de items hijos" width="48%">
</div>

## Características Principales

- ✨ **Animación** en la apertura y cierre del menú
- 🎨 **Personalización completa** de colores, iconos y fuentes
- 📱 **Diseño** adaptable a diferentes tamaños de ventana
- 🧩 **Integración sencilla** con cualquier proyecto Java
- 🔒 **Editor personalizado del modelo** para editar las propiedades de las opciones

## Métodos Principales

A continuación se muestran algunos de los métodos más relevantes del componente:

### Inicialización del Menú

```java
/**
 * Inicializa el menú lateral con la configuración por defecto
 */
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
```

### Constructor de un item

```java
public SideMenuItem(String text, String iconPath, String tooltip) {
    this.text = text;
    this.iconPath = iconPath;
    this.iconUrl = resolveUrl(iconPath);
    this.icon = (iconUrl != null) ? new ImageIcon(iconUrl) : null;
    this.tooltip = tooltip;
    this.children = new ArrayList<>();
}
```

### Establecimiento de los componentes de cada panel que contiene un item

```java
private void setupComponents() {
    // Icono (lado izquierdo)
    iconLabel = new JLabel();
    iconLabel.setOpaque(false);
    iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
    iconLabel.setVerticalAlignment(SwingConstants.CENTER);
    iconLabel.setPreferredSize(new Dimension(iconLabelWidth, 32));
    iconLabel.setMinimumSize(new Dimension(iconLabelWidth, 32));
    iconLabel.setMaximumSize(new Dimension(iconLabelWidth, 48));
    
    // Texto (centro)
    textLabel = new JLabel();
    textLabel.setOpaque(false);
    textLabel.setForeground(textColor);
    textLabel.setFont(opcionesFont);
    textLabel.setBorder(new EmptyBorder(3, 0, 0, 0));
    textLabel.setVerticalAlignment(SwingConstants.CENTER);
    
    // Indicador de expansión (derecha)
    expandCollapseLabel = new JLabel();
    expandCollapseLabel.setOpaque(false);
    expandCollapseLabel.setHorizontalAlignment(SwingConstants.CENTER);
    expandCollapseLabel.setPreferredSize(new Dimension(24, 24));
    updateExpandCollapseIcon();
    
    add(iconLabel, BorderLayout.WEST);
    add(textLabel, BorderLayout.CENTER);
    
    // Solo añadimos el indicador si el menú está expandido y tiene hijos
    updateExpandCollapseVisibility();
}
```

### Método que usa el editor personalizado del modelo para generar el código del nuevo modelo cuando se modifica
```java
@Override
    public String getJavaInitializationString() {
        StringBuilder sb = new StringBuilder();
        sb.append("new menulateral.SideMenuModel()");

        // Solo genera código para agregar elementos si hay elementos en el modelo
        if (model != null && model.getItemCount() > 0) {
            sb.delete(0, sb.length());  // Limpiar el StringBuilder
            sb.append("new menulateral.SideMenuModel() {{ ");

            for (SideMenuItem item : model.getItems()) {
                generateItemCode(sb, item, null);
            }

            sb.append("}}");
        }

        return sb.toString();
    }
```

## Instrucciones de Uso

### Integración

1. **Añade el componente a tu proyecto**

   Puedes clonar este repositorio e importar las clases necesarias a tu proyecto:

   ```bash
   git clone https://github.com/fergmlx/componente-menu-lateral.git
   ```

   O añadir el JAR compilado a tus dependencias.

2. **Inicializa el componente en tu aplicación**

   ```java
   import menulateral.*;
    import javax.swing.*;
    import java.awt.*;
    
    public class EjemploMenuLateral extends JFrame {
    
        public EjemploMenuLateral() {
            // Configurar ventana
            setTitle("Ejemplo SideMenu");
            setSize(800, 600);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // Crear el panel principal con BorderLayout
            JPanel mainPanel = new JPanel(new BorderLayout());
            
            // Crear e inicializar el SideMenuComponent
            SideMenuComponent menuLateral = new SideMenuComponent();
            menuLateral.setBackgroundColor(new Color(40, 40, 40));
            menuLateral.setHoverColor(new Color(220, 220, 220));
            menuLateral.setTextColor(Color.WHITE);
            
            // Crear elementos principales
            SideMenuItem itemInicio = new SideMenuItem("Inicio", "icons/home.png");
            
            // Crear elemento con submenús
            SideMenuItem itemConfig = new SideMenuItem("Configuración", "icons/config.png");
            
            // Agregar hijos al elemento de configuración
            SideMenuItem subItemPerfil = new SideMenuItem("Perfil de Usuario", "icons/user.png");
            SideMenuItem subItemSeguridad = new SideMenuItem("Seguridad", "icons/security.png");
            SideMenuItem subItemNotificaciones = new SideMenuItem("Notificaciones", "icons/notify.png");
            
            // Agregar subítems al ítem padre
            itemConfig.addChild(subItemPerfil);
            itemConfig.addChild(subItemSeguridad);
            itemConfig.addChild(subItemNotificaciones);
            
            // Expandir el menú de configuración
            itemConfig.setExpanded(true);
            
            // Agregar elementos al menú
            menuLateral.addMenuItem(itemInicio);
            menuLateral.addMenuItem(new SideMenuItem("Mensajes", "icons/message.png"));
            menuLateral.addMenuItem(new SideMenuItem("Calendario", "icons/calendar.png"));
            menuLateral.addMenuItem(itemConfig); // Elemento con submenús
            menuLateral.addMenuItem(new SideMenuItem("Ayuda", "icons/help.png"));
            
            // Crear panel central como ejemplo
            JPanel centerPanel = new JPanel();
            centerPanel.setBackground(Color.WHITE);
            centerPanel.add(new JLabel("Contenido Principal"));
            
            // Añadir componentes al panel principal
            mainPanel.add(menuLateral, BorderLayout.WEST);
            mainPanel.add(centerPanel, BorderLayout.CENTER);
            
            // Configurar la ventana
            setContentPane(mainPanel);
            setVisible(true);
        }
        
        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> new EjemploMenuLateral());
        }
    }
    ```

3. **Controlar el menú**
```java
         // Expandir o contraer el menú
         menuLateral.setExpanded(true);  // Expandir
         menuLateral.setExpanded(false); // Contraer
```

## 🎥 Video demostración

> 📹 **[Ver video demostrativo en YouTube](https://youtu.be/hWHMxNumKTw?si=MZPEAgN63LoH28Ur)**

## Créditos

<div align="center">
  <table>
    <tr>
      <td align="center">
        <a href="https://github.com/fergmlx">
          <img src="https://github.com/fergmlx.png" width="100px;" alt=""/>
          <br />
          <sub><b>Luis Fernando González Miguel</b></sub>
        </a>
        <br />
        <sub>Miembro del equipo</sub>
      </td>
      <!-- Añade más miembros del equipo aquí siguiendo el mismo formato -->
      <td align="center">
        <a href="https://github.com/JonathanRene">
          <img src="https://github.com/JonathanRene.png" width="100px;" alt=""/>
          <br />
          <sub><b>Jonathan Rene Cruz Gutiérrez</b></sub>
        </a>
        <br />
        <sub>Miembro del equipo</sub>
      </td>
    </tr>
  </table>
</div>

---
