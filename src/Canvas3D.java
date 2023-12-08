import fill.SeedFiller;
import model.Point;
import model.Polygon;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
import rasterize.PolygonRasterizer;
import rasterize.RasterBufferedImage;
import renderer.WiredRenderer;
import solids.Cube;
import solids.Solid;
import transforms.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * trida pro kresleni na platno: vyuzita tridy RasterBufferedImage
 *
 * @author PGRF FIM UHK
 * @version 2023.c04
 */

public class Canvas3D {
    private JPanel panel;
    private RasterBufferedImage raster;
    private LineRasterizer lineRasterizer;
    private WiredRenderer wiredRenderer;
    private Solid cube;

    private Camera camera;
    private Mat4 projection;

    private double translX = 0;

    public Canvas3D(int width, int height) {
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new RasterBufferedImage(width, height);
        lineRasterizer = new LineRasterizerGraphics(raster);
        wiredRenderer = new WiredRenderer(lineRasterizer);

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

        initScene();

        panel.requestFocus();
        panel.requestFocusInWindow();

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_LEFT)
                    camera = camera.left(0.1);
                if(e.getKeyCode() == KeyEvent.VK_RIGHT)
                    camera = camera.right(0.1);
                if(e.getKeyCode() == KeyEvent.VK_UP)
                    camera = camera.forward(0.1);
                if(e.getKeyCode() == KeyEvent.VK_DOWN)
                    camera = camera.backward(0.1);

                renderScene();
            }
        });
    }

    public void initScene() {
        camera = new Camera(
                new Vec3D(0.5,-1,0.3),
                Math.toRadians(90),
                Math.toRadians(-15),
                1. ,
                true
        );
        projection = new Mat4PerspRH(
                Math.PI / 4,
                600 / 800.,
                0.1,
                20.
        );


        cube = new Cube();
    }

    public void renderScene() {
        clear(0x000000);

        wiredRenderer.setView(camera.getViewMatrix());
        wiredRenderer.setProj(projection);
        wiredRenderer.render(cube);

        panel.repaint();
    }

    public void clear(int color) {
        raster.setClearColor(color);
        raster.clear();
    }

    public void present(Graphics graphics) {
        raster.repaint(graphics);
    }

    public void start() {
        renderScene();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Canvas3D(800, 600).start());
    }

}
