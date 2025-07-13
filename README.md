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

Componente de menú lateral personalizable desarrollado en Java. Este componente permite una navegación intuitiva en aplicaciones de escritorio, ofreciendo una experiencia de usuario moderna con transiciones fluidas y opciones de personalización.

## Capturas de Pantalla

<div align="center">
  <img src="assets/screenshot1.png" alt="Menú desplegado" width="48%">
  <img src="assets/screenshot2.png" alt="Menú contraído" width="48%">
</div>

<div align="center">
  <img src="assets/screenshot3.png" alt="Personalización de colores" width="48%">
  <img src="assets/screenshot4.png" alt="Ejemplo de uso" width="48%">
</div>

## Características Principales

- ✨ **Animaciones fluidas** en la apertura y cierre del menú
- 🎨 **Personalización completa** de colores, iconos y fuentes
- 📱 **Diseño responsive** adaptable a diferentes tamaños de ventana
- 🧩 **Integración sencilla** con cualquier proyecto Java
- 🔒 **Control de acceso** para diferentes niveles de usuario

## Métodos Principales

A continuación se muestran algunos de los métodos más relevantes del componente:

### Inicialización del Menú

```java
/**
 * Inicializa el menú lateral con la configuración por defecto
 * @param parent El contenedor principal donde se añadirá el menú
 * @param items Lista de elementos que contendrá el menú
 */
public void initialize(JPanel parent, List<MenuItem> items) {
    this.parent = parent;
    this.menuItems = items;
    
    setupMenuPanel();
    loadMenuItems();
    setupAnimations();
    
    parent.add(menuPanel, BorderLayout.WEST);
    parent.revalidate();
}
```

### Control de Animaciones

```java
/**
 * Controla la animación de apertura y cierre del menú lateral
 * @param expanded Estado objetivo del menú (true=expandido, false=contraído)
 */
public void toggleMenu(boolean expanded) {
    if (animationInProgress) return;
    
    animationInProgress = true;
    int targetWidth = expanded ? expandedWidth : collapsedWidth;
    int currentWidth = menuPanel.getWidth();
    
    Timer timer = new Timer(5, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int newWidth;
            if (expanded) {
                newWidth = Math.min(currentWidth + animationStep, targetWidth);
            } else {
                newWidth = Math.max(currentWidth - animationStep, targetWidth);
            }
            
            menuPanel.setPreferredSize(new Dimension(newWidth, menuPanel.getHeight()));
            parent.revalidate();
            
            if ((expanded && newWidth >= targetWidth) || (!expanded && newWidth <= targetWidth)) {
                ((Timer)e.getSource()).stop();
                animationInProgress = false;
                updateMenuItemsVisibility(expanded);
            }
        }
    });
    
    timer.start();
}
```

### Personalización del Aspecto

```java
/**
 * Personaliza los colores del menú lateral
 * @param backgroundColor Color de fondo del menú
 * @param textColor Color del texto de los elementos
 * @param hoverColor Color de resaltado al pasar el cursor
 * @param selectedColor Color del elemento seleccionado
 */
public void setMenuColors(Color backgroundColor, Color textColor, Color hoverColor, Color selectedColor) {
    this.backgroundColor = backgroundColor;
    this.textColor = textColor;
    this.hoverColor = hoverColor;
    this.selectedColor = selectedColor;
    
    // Aplicar colores a todos los componentes
    menuPanel.setBackground(backgroundColor);
    
    for (Component comp : menuPanel.getComponents()) {
        if (comp instanceof JPanel) {
            JPanel itemPanel = (JPanel) comp;
            itemPanel.setBackground(backgroundColor);
            
            for (Component c : itemPanel.getComponents()) {
                if (c instanceof JLabel) {
                    ((JLabel) c).setForeground(textColor);
                }
            }
        }
    }
    
    menuPanel.repaint();
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
   // Importaciones necesarias
   import com.miapp.components.LateralMenu;
   import com.miapp.components.MenuItem;
   
   // Crear instancia del menú
   LateralMenu menu = new LateralMenu();
   
   // Crear elementos del menú
   List<MenuItem> items = new ArrayList<>();
   items.add(new MenuItem("Inicio", new ImageIcon("icons/home.png"), e -> showHomePage()));
   items.add(new MenuItem("Perfil", new ImageIcon("icons/profile.png"), e -> showProfilePage()));
   items.add(new MenuItem("Configuración", new ImageIcon("icons/settings.png"), e -> showSettingsPage()));
   
   // Inicializar el menú en tu panel principal
   menu.initialize(mainPanel, items);
   
   // Personalizar colores (opcional)
   menu.setMenuColors(
       new Color(50, 50, 50),    // Fondo
       new Color(240, 240, 240), // Texto
       new Color(70, 70, 70),    // Hover
       new Color(0, 120, 215)    // Seleccionado
   );
   ```

3. **Controlar el menú programáticamente**

   ```java
   // Expandir o contraer el menú
   menu.toggleMenu(true);  // Expandir
   menu.toggleMenu(false); // Contraer
   
   // Seleccionar un elemento específico
   menu.selectItem(2); // Selecciona el tercer elemento (índice 2)
   ```

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
