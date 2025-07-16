package usocomponente;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ArchivosAdjuntos extends JPanel {
    
    private BotonModificado btnAdjuntar;
    private JPanel panelArchivos;
    private JScrollPane scrollPane;
    private List<File> archivosSeleccionados = new ArrayList<>();
    private boolean multipleFiles = true;
    
    public ArchivosAdjuntos() {
        setLayout(new BorderLayout());
        
        // NO establecemos tamaño fijo aquí para permitir el redimensionamiento en el diseñador
        
        // Panel para el botón de adjuntar y archivos seleccionados
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBackground(new Color(0, 0, 0, 0));
        
        // Botón de adjuntar con tamaño ajustado
        btnAdjuntar = new BotonModificado("Adjuntar");
        btnAdjuntar.setIcon(new ImageIcon(getClass().getResource("clip.png")));
        btnAdjuntar.setCornerRadius(15);
        btnAdjuntar.setFont(new Font("Inter Medium", Font.PLAIN, 16));
        btnAdjuntar.setPreferredSize(new Dimension(125, 32));
        btnAdjuntar.setMargin(new Insets(0, 5, 0, 5));
        btnAdjuntar.addActionListener(e -> seleccionarArchivos());
        mainPanel.add(btnAdjuntar, BorderLayout.WEST);
        
        // Panel para archivos con scroll horizontal
        panelArchivos = new JPanel();
        panelArchivos.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
        panelArchivos.setOpaque(false);
        panelArchivos.setBackground(new Color(0, 0, 0, 0));
        scrollPane = new JScrollPane(panelArchivos, 
                JScrollPane.VERTICAL_SCROLLBAR_NEVER, 
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);
        
        // Establecemos un tamaño predeterminado que se usará como punto de partida
        // pero permitirá el redimensionamiento
        setPreferredSize(new Dimension(516, 36));
    }
    
    // Método para seleccionar archivos
    private void seleccionarArchivos() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(multipleFiles);
        
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            if (multipleFiles) {
                File[] files = fileChooser.getSelectedFiles();
                for (File file : files) {
                    agregarArchivo(file);
                }
            } else {
                agregarArchivo(fileChooser.getSelectedFile());
            }
        }
    }
    
    // Método para agregar un archivo a la lista
    private void agregarArchivo(File file) {
        if (!archivosSeleccionados.contains(file)) {
            archivosSeleccionados.add(file);
            
            // Crear panel para este archivo - optimizado para altura limitada
            JPanel filePanel = new JPanel(new BorderLayout(3, 0));
            filePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(2, 5, 2, 2)
            ));
            filePanel.setPreferredSize(new Dimension(
                Math.min(200, file.getName().length() * 7 + 30), 30));
            filePanel.setBackground(new Color(245, 245, 245));
            
            // Etiqueta con el nombre del archivo - truncado si es necesario
            String fileName = file.getName();
            if (fileName.length() > 20) {
                fileName = fileName.substring(0, 17) + "...";
            }
            JLabel lblFileName = new JLabel(fileName);
            lblFileName.setFont(new Font(lblFileName.getFont().getName(), Font.PLAIN, 12));
            lblFileName.setToolTipText(file.getAbsolutePath());
            filePanel.add(lblFileName, BorderLayout.CENTER);
            
            // Botón para eliminar - más pequeño
            JButton btnDelete = new JButton();
            btnDelete.setIcon(new ImageIcon(getClass().getResource("close.png")));
            btnDelete.setMargin(new Insets(0, 2, 0, 2));
            btnDelete.setPreferredSize(new Dimension(16, 16));
            btnDelete.setFocusPainted(false);
            btnDelete.setContentAreaFilled(false);
            btnDelete.setBorderPainted(false);
            btnDelete.setFont(new Font("Arial", Font.BOLD, 14));
            btnDelete.setForeground(Color.GRAY);
            btnDelete.addActionListener(e -> eliminarArchivo(file, filePanel));
            filePanel.add(btnDelete, BorderLayout.EAST);
            
            // Añadir a la interfaz
            panelArchivos.add(filePanel);
            panelArchivos.revalidate();
            scrollPane.getHorizontalScrollBar().setValue(
                scrollPane.getHorizontalScrollBar().getMaximum());
            
            // Notificar cambios
            firePropertyChange("filesChanged", null, getArchivosSeleccionados());
        }
    }
    
    // Método para eliminar un archivo
    private void eliminarArchivo(File file, JPanel filePanel) {
        archivosSeleccionados.remove(file);
        panelArchivos.remove(filePanel);
        panelArchivos.revalidate();
        panelArchivos.repaint();
        
        // Notificar cambios
        firePropertyChange("filesChanged", null, getArchivosSeleccionados());
    }
    
    // Getters y Setters
    public List<File> getArchivosSeleccionados() {
        return new ArrayList<>(archivosSeleccionados);
    }
    
    public void setMultipleFiles(boolean multipleFiles) {
        this.multipleFiles = multipleFiles;
    }
    
    public boolean isMultipleFiles() {
        return multipleFiles;
    }
    
    // Mantiene el tamaño fijo
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(516, 36);
    }
    
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
    
    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
}