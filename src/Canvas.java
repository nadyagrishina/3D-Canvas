import fill.ScanLineFiller;
import fill.SeedFiller;
import model.Line;
import model.Point;
import model.Polygon;
import rasterize.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * trida pro kresleni na platno: vyuzita tridy RasterBufferedImage
 *
 * @author PGRF FIM UHK
 * @version 2023.c04
 */

public class Canvas {

    private JPanel panel;
    private RasterBufferedImage raster;

    private LineRasterizerGraphics lineRasterizer;
    private PolygonRasterizer polygonRasterizer;

    private Polygon polygon;

    public Canvas(int width, int height) {
        JFrame frame = new JFrame();

        frame.setLayout(new BorderLayout());

        frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
        frame.setResizable(true);


        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new RasterBufferedImage(width, height);
        lineRasterizer = new LineRasterizerGraphics(raster);
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);

        polygon = new Polygon();

        panel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                raster.clear();

                if(e.getButton() == MouseEvent.BUTTON1) {
                    Point p = new Point(e.getX(), e.getY());
                    polygon.addPoint(p);
                }

                /*
                ScanLineFiller scanLineFiller = new ScanLineFiller(lineRasterizer, polygonRasterizer, polygon);
                scanLineFiller.fill();
                 */

                polygonRasterizer.rasterize(polygon, Color.RED);

                if(e.getButton() == MouseEvent.BUTTON3) {
                    SeedFiller seedFiller = new SeedFiller(raster,
                            raster.getPixel(e.getX(), e.getY()),
                            e.getX(), e.getY());
                    seedFiller.fill();
                }

                panel.repaint();
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                raster.clear();
                Point p1 = new Point(width / 2, height / 2);
                Point p2 = new Point(e.getX(), e.getY());
                Line line = new Line(p1, p2, 0xffff00);
                lineRasterizer.rasterize(line, Color.RED);
                panel.repaint();
            }
        });

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

            }
        });

    }

    public void clear(int color) {
        raster.setClearColor(color);
        raster.clear();

    }

    public void present(Graphics graphics) {
        raster.repaint(graphics);
    }

    public void start() {
        clear(0x000000);

        panel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Canvas(800, 600).start());
    }

}
