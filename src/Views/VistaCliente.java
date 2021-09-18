package Views;

import Connections.Archivo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.DefaultTableModel;

public class VistaCliente extends javax.swing.JFrame {

    // Variables de ayuda para la posición de las ventanas
    private static final int ancho = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
    private static final int alto = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
    private static File rutaLocal;
    static ArrayList<Archivo> archs_locales; // Lista de archivos dentro de la dirección local

    public VistaCliente() {
        initComponents();
        this.setExtendedState(MAXIMIZED_BOTH);
        // Agregamos las opciones de las tablas
        popupTablaLocal();

        // Ponemos el directorio local por default
        establecerRutaLocal("/");

        // Leemos la lista de archivos locales
        leerArchivosLocales();

        // Mostramos la lista de archivos locales
        cargarArchivosLocales();
    }

    public static void establecerRutaLocal(String ruta) {
        rutaLocal = new File(ruta);
        txtRutaLocal1.setText(rutaLocal.getAbsolutePath());
    }

    public static void leerArchivosLocales() {
        archs_locales = new ArrayList<Archivo>();
        archs_locales.add(new Archivo("../"));
        
        for (File arch : rutaLocal.listFiles()) {
            Archivo archivo = new Archivo(arch);

            // Leemos el nombre del archivo
            archivo.setNombre(arch.getName());
            // Leemos el tamaño del archivo
            archivo.setTam(arch.length());
            // Leemos la última modificación del archivo
            archivo.setUltima_mod(obtenerUltimaMod(arch));

            // Agregamos al arreglo
            archs_locales.add(archivo);
        }
    }

    // Función que inserta los archivos en la tabla de los archivos locales
    public static void cargarArchivosLocales() {
        DefaultTableModel model = (DefaultTableModel) tbLocal.getModel(); // Crea el modelo de la tabla a partir del actual

        // Limpiamos los registros anteriores de la tabla
        int filas = tbLocal.getRowCount();
        for (int i = 0; i < filas; i++) {
            model.removeRow(0);
        }

        for (Archivo archivo : archs_locales) {

            Object[] fila = new Object[3];// Crea el objeto de celdas para agregar
            fila[0] = archivo.getNombre();
            long tam = archivo.getTam();

            if (tam >= 1000000000) {
                fila[1] = archivo.getTam() / 1000000000.0 + " GB";
            } else if (tam >= 1000000) {
                fila[1] = archivo.getTam() / 1000000.0 + " MB";
            } else if (tam >= 1000) {
                fila[1] = archivo.getTam() / 1000.0 + " KB";
            } else {
                fila[1] = archivo.getTam() + " B";
            }

            fila[2] = archivo.getUltima_mod();

            model.addRow(fila); // Agrega la fila al modelo de la tabla
            tbLocal.setModel(model); // Reasigna el modelo pero ahora con los nuevos datos
        }
    }

    // Función que obtene la última modificación de un archivo
    public static String obtenerUltimaMod(File arch) {
        long lastModified = arch.lastModified();
        String pattern = "yyyy-MM-dd hh:mm aa";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date lastModifiedDate = new Date(lastModified);
        return simpleDateFormat.format(lastModifiedDate);
    }

    /*
        Creamos los pop-up para la tabla de archivos locales
     */
    public void popupTablaLocal() {
        JPopupMenu pM = new JPopupMenu(); //se crea el contenedor
        JMenuItem jmi1 = new JMenuItem("Abrir"); //las opciones

        jmi1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombreCarpeta = String.valueOf(tbLocal.getValueAt(tbLocal.getSelectedRow(), 0));
                String ruta;
                
                if(nombreCarpeta.equals("../")){
                    if(rutaLocal.getParentFile() != null){ // Comprobamos que haya directorio padre
                        // Establecemos la ruta para la nueva carpeta
                        establecerRutaLocal(rutaLocal.getParentFile().getAbsolutePath() + "//");
                    
                        // Leemos la lista de archivos locales                
                        leerArchivosLocales();

                        // Mostramos la lista de archivos locales
                        cargarArchivosLocales();
                    }
                    
                } else{
                    if(new File(rutaLocal.getAbsolutePath() + "//" + nombreCarpeta + "//").isDirectory()){ // Comprobamos que sea directorio
                        // Establecemos la ruta para la nueva carpeta
                        establecerRutaLocal(rutaLocal.getAbsolutePath() + "//" + nombreCarpeta + "//");

                        // Leemos la lista de archivos locales                
                        leerArchivosLocales();

                        // Mostramos la lista de archivos locales
                        cargarArchivosLocales();
                    }
                }        
                
                
            }
        });

        pM.add(jmi1);// se agregan las opciones al contenedor

        tbLocal.setComponentPopupMenu(pM);//se agrega el menú a la tabla con su respectivo evento
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tbRemoto = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbLocal = new javax.swing.JTable();
        btnBajar = new javax.swing.JButton();
        btnSubir = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtRutaLocal = new javax.swing.JLabel();
        txtRutaLocal1 = new javax.swing.JLabel();
        btnEliminar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tbRemoto.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre", "Tamaño", "Fecha modif."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tbRemoto);

        tbLocal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre", "Tamaño", "Fecha modif."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tbLocal);

        btnBajar.setBackground(new java.awt.Color(102, 255, 102));
        btnBajar.setText("<-- Bajar --");
        btnBajar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBajarActionPerformed(evt);
            }
        });

        btnSubir.setBackground(new java.awt.Color(51, 153, 255));
        btnSubir.setText("-- Subir -->");
        btnSubir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubirActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dubai Light", 0, 18)); // NOI18N
        jLabel1.setText("Servidor");

        jLabel2.setFont(new java.awt.Font("Dubai Light", 0, 18)); // NOI18N
        jLabel2.setText("Local");

        txtRutaLocal.setText("Ruta remota");

        txtRutaLocal1.setText("Ruta local");

        btnEliminar.setBackground(new java.awt.Color(255, 51, 51));
        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(304, 304, 304)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(330, 330, 330))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnSubir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnBajar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnEliminar, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))
                        .addGap(51, 51, 51))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(100, 100, 100)
                        .addComponent(txtRutaLocal1, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 228, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtRutaLocal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addGap(130, 130, 130))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(100, 100, 100)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(809, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(99, 99, 99)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRutaLocal)
                    .addComponent(txtRutaLocal1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(147, 147, 147))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnSubir)
                        .addGap(26, 26, 26)
                        .addComponent(btnEliminar)
                        .addGap(26, 26, 26)
                        .addComponent(btnBajar)
                        .addGap(338, 338, 338))))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(200, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(148, 148, 148)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBajarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBajarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBajarActionPerformed

    private void btnSubirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubirActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSubirActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnEliminarActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VistaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VistaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VistaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VistaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VistaCliente().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBajar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnSubir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private static javax.swing.JTable tbLocal;
    private static javax.swing.JTable tbRemoto;
    private javax.swing.JLabel txtRutaLocal;
    private static javax.swing.JLabel txtRutaLocal1;
    // End of variables declaration//GEN-END:variables
}
