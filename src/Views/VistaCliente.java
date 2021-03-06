package Views;

import Connections.Archivo;
import static Connections.Cliente.*;
import java.io.File;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class VistaCliente extends javax.swing.JFrame {

    // Variables de ayuda para la posición de las ventanas
    private static final int ancho = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
    private static final int alto = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

    private static File rutaLocal;

    static ArrayList<Archivo> archs_locales; // Lista de archivos dentro de la dirección local
    static ArrayList<Archivo> archs_remotos; // Lista de archivos dentro de la dirección local

    public VistaCliente() {
        initComponents();
        this.setExtendedState(MAXIMIZED_BOTH);
        iniciarApp();
    }

    // Agregamos las rutas y cargamos archivos
    public static void iniciarApp() {
        /* Archivos locales */
        // Ponemos el directorio local por default
        establecerRutaLocal("/");
        // Leemos la lista de archivos locales
        leerArchivosLocales();
        // Mostramos la lista de archivos locales
        cargarArchivos(archs_locales, tbLocal);

        /* Archivos remotos */
        conectarApp();

    }

    /* Conectamos la app al servidor y recibimos la lista de archivos de la carpeta principal*/
    public static void conectarApp() {
        /* Mandamos a conetar */
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        try {
            conectar(); // Creamos la conexión al socket
            System.out.println("Conexión establecida en: " + direccion + ":" + puerto);

            flujoSalidaSkt(); // Creamos el flujo de escritura del socket
            flujoEntradaSkt(); // Creamos el flujo de lectura del socket
            System.out.println("Flujos sobre el socket creados correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Hubo error al conectar al servidor y/o hacer los flujos");
        }

        /* Solicitamos la lista de archivos para mostrarla */
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        try {
            solicitarLista();
            System.out.println("Solicitud de lista correcta");
            archs_remotos = new ArrayList<Archivo>();
            archs_remotos.add(new Archivo("../"));

            for (Archivo arch : archs_relativos) {
                archs_remotos.add(arch);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Hubo error al solicitar los archivos del servidor");
        }

        /* Cargamos los archivos en la tabla remota */
        cargarArchivos(archs_remotos, tbRemoto);

        /* Actualizamos la ruta */
        txtRutaRemota.setText(dir_relativa);
    }

    public static void establecerRutaLocal(String ruta) {
        rutaLocal = new File(ruta);
        txtRutaLocal.setText(rutaLocal.getAbsolutePath());
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
    public static void cargarArchivos(ArrayList<Archivo> archivos, JTable tbl) {
        DefaultTableModel model = (DefaultTableModel) tbl.getModel(); // Crea el modelo de la tabla a partir del actual

        // Limpiamos los registros anteriores de la tabla
        int filas = tbl.getRowCount();
        for (int i = 0; i < filas; i++) {
            model.removeRow(0);
        }

        for (Archivo archivo : archivos) {

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
            tbl.setModel(model); // Reasigna el modelo pero ahora con los nuevos datos
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

    // Función que obtiene el directorio padre de una dirección relativa
    public static String obtenerPadre(String ruta) {
        String[] parts = ruta.split("\\\\");
        ruta = "";
        for (int i = 0; i < parts.length - 1; i++) {
            ruta += parts[i] + "\\";
        }
        return ruta;
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
        txtRutaRemota = new javax.swing.JLabel();
        txtRutaLocal = new javax.swing.JLabel();
        btnEliminar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

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
        tbRemoto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbRemotoMouseClicked(evt);
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
        tbLocal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbLocalMouseClicked(evt);
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

        txtRutaRemota.setText("Ruta remota");

        txtRutaLocal.setText("Ruta local");

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
                        .addComponent(txtRutaLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 228, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtRutaRemota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .addComponent(txtRutaRemota)
                    .addComponent(txtRutaLocal))
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
        // Obtenemos los archivos seleccionados en la tabla remota
        int[] selectedRows = tbRemoto.getSelectedRows();
        String nombres[] = new String[selectedRows.length];

        // Obtenemos el nombre de cada archivo y el arreglo de archivos a enviar
        for (int i = 0; i < selectedRows.length; i++) {
            nombres[i] = String.valueOf(tbRemoto.getValueAt(selectedRows[i], 0));     
            System.out.println(nombres[i]);
        }
        
        // Asignamos la dirección donde se descargarán
        dir_absoluta = rutaLocal.getAbsolutePath();
        
        /* Enviando la de archivos */
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        try {
            recibirArchivo(nombres);
            System.out.println("Solicitud de archivo correcto");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Hubo un error en la petición del archivo");
        }
        
        // Leemos la lista de archivos locales
        leerArchivosLocales();
        // Mostramos la lista de archivos locales
        cargarArchivos(archs_locales, tbLocal);
    }//GEN-LAST:event_btnBajarActionPerformed

    private void btnSubirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubirActionPerformed
        // Obtenemos los archivos seleccionados en la tabla local
        int[] selectedRows = tbLocal.getSelectedRows();
        String nombres[] = new String[selectedRows.length];

        // Creamos el arreglo que guardará la selección de archivos
        File seleccion[] = new File[selectedRows.length];

        // Obtenemos el nombre de cada archivo y el arreglo de archivos a enviar
        for (int i = 0; i < selectedRows.length; i++) {
            nombres[i] = String.valueOf(tbLocal.getValueAt(selectedRows[i], 0));

            for (Archivo archivo : archs_locales) {
                if (archivo.getNombre().equals(nombres[i])) {
                    seleccion[i] = archivo.getFile();
                }
            }
        }

        /* Envíamos los archivos al servidor */
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        try {
            // Compresión de archivos
            File comprimido = Archivo.comprimir(seleccion);
            enviarArchivo(comprimido); // Enviamos el archivo comprimido por el socket
            comprimido.delete(); // Eliminamos el archivo comprimido

            System.out.println(seleccion.length + " archivos/carpetas han sido enviados correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Hubo error al enviar el archivo");
        }

        /* Cargamos los archivos en la tabla remota */
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        try {
            solicitarLista();
            System.out.println("Solicitud de lista correcta");
            archs_remotos = new ArrayList<Archivo>();
            archs_remotos.add(new Archivo("../"));

            for (Archivo arch : archs_relativos) {
                archs_remotos.add(arch);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Hubo error al solicitar los archivos del servidor");
        }

        /* Cargamos los archivos en la tabla remota */
        cargarArchivos(archs_remotos, tbRemoto);
    }//GEN-LAST:event_btnSubirActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // Obtenemos los archivos seleccionados en la tabla en remoto
        int[] selectedRows = tbRemoto.getSelectedRows();
        String nombres[] = new String[selectedRows.length];
        
        // Obtenemos el nombre de cada archivo
        for (int i = 0; i < selectedRows.length; i++) {
            nombres[i] = String.valueOf(tbRemoto.getValueAt(selectedRows[i], 0));
            System.out.println(nombres[i]);
        }
        
        /* Envíamos la solicitud de eliminar archivos al servidor */
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        try {
            eliminarArchivo(nombres);
            System.out.println("Solicitud de eliminación de archivos correcto");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Hubo un error en la petición de eliminar el archivo.");
        }
        
        /* Cargamos los archivos en la tabla remota */
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        try {
            solicitarLista();
            System.out.println("Solicitud de lista correcta");
            archs_remotos = new ArrayList<Archivo>();
            archs_remotos.add(new Archivo("../"));

            for (Archivo arch : archs_relativos) {
                archs_remotos.add(arch);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Hubo error al solicitar los archivos del servidor");
        }

        /* Cargamos los archivos en la tabla remota */
        cargarArchivos(archs_remotos, tbRemoto);
    }//GEN-LAST:event_btnEliminarActionPerformed

    // Agregamos el eventlistener para manejar los directorios locales
    private void tbLocalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbLocalMouseClicked
        if (evt.getClickCount() == 2) {
            String nombreCarpeta = String.valueOf(tbLocal.getValueAt(tbLocal.getSelectedRow(), 0));
            String ruta;

            if (nombreCarpeta.equals("../")) {
                if (rutaLocal.getParentFile() != null) { // Comprobamos que haya directorio padre
                    // Establecemos la ruta para la nueva carpeta
                    establecerRutaLocal(rutaLocal.getParentFile().getAbsolutePath() + "//");

                    // Leemos la lista de archivos locales                
                    leerArchivosLocales();

                    // Mostramos la lista de archivos locales
                    cargarArchivos(archs_locales, tbLocal);
                }

            } else {
                if (new File(rutaLocal.getAbsolutePath() + "//" + nombreCarpeta + "//").isDirectory()) { // Comprobamos que sea directorio
                    // Establecemos la ruta para la nueva carpeta
                    establecerRutaLocal(rutaLocal.getAbsolutePath() + "//" + nombreCarpeta + "//");

                    // Leemos la lista de archivos locales                
                    leerArchivosLocales();

                    // Mostramos la lista de archivos locales
                    cargarArchivos(archs_locales, tbLocal);
                }
            }
        }
    }//GEN-LAST:event_tbLocalMouseClicked

    // Agregamos el eventlistener para manejar los directorios locales
    private void tbRemotoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbRemotoMouseClicked
        if (evt.getClickCount() == 2) {
            String nombreCarpeta = String.valueOf(tbRemoto.getValueAt(tbRemoto.getSelectedRow(), 0));

            if (nombreCarpeta.equals("../")) {
                if (!dir_relativa.equals("")) { // Comprobamos que haya directorio padre

                    /* Solicitamos la lista de archivos */
                    System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
                    try {
                        // Obtenemos la dirección relativa del padre
                        dir_relativa = obtenerPadre(dir_relativa);

                        // Enviamos la solicitud
                        solicitarLista();
                        System.out.println("Solicitud de lista correcta");
                        archs_remotos = new ArrayList<Archivo>();
                        archs_remotos.add(new Archivo("../"));

                        for (Archivo arch : archs_relativos) {
                            archs_remotos.add(arch);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Hubo error al solicitar los archivos del servidor");
                    }

                    /* Cargamos los archivos en la tabla remota */
                    cargarArchivos(archs_remotos, tbRemoto);

                    /* Actualizamos la ruta */
                    txtRutaRemota.setText(dir_relativa);

                }

            } else {
                /* Solicitamos la lista de archivos */
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
                try {
                    dir_relativa = dir_relativa + nombreCarpeta + "\\";
                    solicitarLista();
                    System.out.println("Solicitud de lista correcta");
                    archs_remotos = new ArrayList<Archivo>();
                    archs_remotos.add(new Archivo("../"));

                    for (Archivo arch : archs_relativos) {
                        archs_remotos.add(arch);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Hubo error al solicitar los archivos del servidor");
                }

                /* Cargamos los archivos en la tabla remota */
                cargarArchivos(archs_remotos, tbRemoto);

                /* Actualizamos la ruta */
                txtRutaRemota.setText(dir_relativa);
            }
        }
    }//GEN-LAST:event_tbRemotoMouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        cerrarConexion();
        this.dispose();
    }//GEN-LAST:event_formWindowClosing

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
    private static javax.swing.JLabel txtRutaLocal;
    private static javax.swing.JLabel txtRutaRemota;
    // End of variables declaration//GEN-END:variables
}
