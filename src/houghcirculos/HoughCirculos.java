/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package houghcirculos;

/**
 *
 * @author LoreyFaby
 */

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class HoughCirculos {
    private static final String imagePath = "Imagen1.jpg";
    private static JFrame frame;
    private static BufferedImage image;
    private static JLabel imageLabel;

    public static void main(String[] args) {
        // Cargar la biblioteca de OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Leer la imagen en formato JPG
        Mat imagen = Imgcodecs.imread(imagePath);

        // Convertir la imagen a escala de grises
        Mat grises = new Mat();
        Imgproc.cvtColor(imagen, grises, Imgproc.COLOR_BGR2GRAY);

        // Aplicar suavizado a la imagen en escala de grises
        Mat suavizar = new Mat();
        Imgproc.GaussianBlur(grises, suavizar, new Size(5, 5), 0);

        // Crear el contenedor para mostrar la imagen en un JOptionPane
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Imagen con círculos detectados");
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);

        // Mostrar la imagen original en el contenedor
        image = matToBufferedImage(imagen);
        imageLabel = new JLabel(new ImageIcon(image));
        frame.getContentPane().add(imageLabel, BorderLayout.CENTER);

        // Crear un panel para los controles de modificación
        JPanel panel = new JPanel();

        // Crear campos de texto para los parámetros
        JTextField dpField = new JTextField("1", 5);
        JTextField minDistField = new JTextField("50", 5);
        JTextField param1Field = new JTextField("40", 5);
        JTextField param2Field = new JTextField("20", 5);
        JTextField minRadioField = new JTextField("25", 5);
        JTextField maxRadioField = new JTextField("40", 5);

        // Crear un botón para ejecutar el código con los nuevos parámetros
        JButton executeButton = new JButton("Ejecutar");
        executeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Obtener los nuevos valores para los parámetros
                double dp = Double.parseDouble(dpField.getText());
                double minDist = Double.parseDouble(minDistField.getText());
                double param1 = Double.parseDouble(param1Field.getText());
                double param2 = Double.parseDouble(param2Field.getText());
                int minRadio = Integer.parseInt(minRadioField.getText());
                int maxRadio = Integer.parseInt(maxRadioField.getText());

                // Detectar círculos en la imagen utilizando la transformada de Hough circular
                Mat circulos = new Mat();
                Imgproc.HoughCircles(suavizar, circulos, Imgproc.HOUGH_GRADIENT, dp, minDist, param1, param2, minRadio, maxRadio);

                // Dibujar los círculos detectados en la imagen
                Mat imagenConCirculos = imagen.clone();
                for (int i = 0; i < circulos.cols(); i++) {
                    double[] circle = circulos.get(0, i);
                    double x = circle[0];
                    double y = circle[1];
                    double radius = circle[2];

                    // Dibujar el contorno del círculo
                    Imgproc.circle(imagenConCirculos, new Point(x, y), (int) radius, new Scalar(0, 255, 0), 2);

                    // Marcar el centro del círculo
                    Imgproc.circle(imagenConCirculos, new Point(x, y), 2, new Scalar(255, 0, 0), 3);
                }

                
                
                // Convertir la imagen con círculos a BufferedImage
                image = matToBufferedImage(imagenConCirculos);

                // Actualizar la imagen mostrada en el contenedor
                imageLabel.setIcon(new ImageIcon(image));
                frame.pack();
                
                // Guardar la imagen con cirulos detectados
                Imgcodecs.imwrite("imagen_con_ciruclos.jpg", imagenConCirculos);
            }
        });

        // Agregar los campos de texto y el botón al panel
        panel.add(new JLabel("dp:"));
        panel.add(dpField);
        panel.add(new JLabel("minDist:"));
        panel.add(minDistField);
        panel.add(new JLabel("param1:"));
        panel.add(param1Field);
        panel.add(new JLabel("param2:"));
        panel.add(param2Field);
        panel.add(new JLabel("minRadio:"));
        panel.add(minRadioField);
        panel.add(new JLabel("maxRadio:"));
        panel.add(maxRadioField);
        panel.add(executeButton);

        // Agregar el panel al contenedor
        frame.getContentPane().add(panel, BorderLayout.SOUTH);

        // Mostrar el contenedor
        frame.setVisible(true);
    }

    private static BufferedImage matToBufferedImage(Mat mat) {
        // Obtener las dimensiones de la imagen
        int ancho = mat.cols();
        int alto = mat.rows();

        // Crear un BufferedImage con el mismo tamaño y tipo de la imagen de OpenCV
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_3BYTE_BGR);

        // Obtener el arreglo de bytes de la imagen de OpenCV
        byte[] dato = new byte[ancho * alto * (int) mat.elemSize()];
        mat.get(0, 0, dato);

        // Establecer los datos de píxeles en la imagen de BufferedImage
        WritableRaster raster = imagen.getRaster();
        raster.setDataElements(0, 0, ancho, alto, dato);

        return imagen;
    }
}
