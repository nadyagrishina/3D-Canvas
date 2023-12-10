import rasterize.LineRasterizerGraphics;
import rasterize.RasterBufferedImage;
import renderer.WiredRenderer;
import solids.Octahedron;
import solids.Pyramid;
import solids.Solid;
import transforms.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

public class Canvas3D {
    private final JPanel panel;
    private final RasterBufferedImage raster;
    private final WiredRenderer wiredRenderer;
    private Solid pyramid;
    private Solid octahedron;
    private Camera camera;
    private Mat4 projectionMatrix;
    private Mat4 translPyramid = new Mat4Identity();
    private Mat4 scalePyramid = new Mat4Identity();
    private Mat4 rotatePyramid = new Mat4Identity();

    private Mat4 translOctahedron = new Mat4Identity();
    private Mat4 scaleOctahedron = new Mat4Identity();
    private Mat4 rotateOctahedron = new Mat4Identity();
    private Mode transformMode = Mode.DEFAULT;
    private int oldX = 0;
    private int oldY = 0;
    private Solid activeSolid;
    private boolean isPyramidSelected = true;
    private boolean isOctahedronSelected = true;
    private boolean isBothSelected = true;
    private boolean isOrthographicProjection = false;

    public Canvas3D(int width, int height) {
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new RasterBufferedImage(width, height);
        LineRasterizerGraphics lineRasterizer = new LineRasterizerGraphics(raster);
        wiredRenderer = new WiredRenderer(lineRasterizer);

        panel = new JPanel() {
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
            public void keyPressed(java.awt.event.KeyEvent e) {
                handleKeyPress(e);
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                transformMode = Mode.DEFAULT;
            }
        });
        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                oldX = e.getX();
                oldY = e.getY();
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDrag(e);
            }
        });
    }

    private void handleKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_T -> {
                transformMode = Mode.TRANSLATION;
                checkSelection();
            }
            case KeyEvent.VK_SPACE -> {
                transformMode = Mode.SCALE;
                checkSelection();
            }
            case KeyEvent.VK_R -> {
                transformMode = Mode.ROTATION;
                checkSelection();
            }
            case KeyEvent.VK_A -> camera = camera.left(0.3);
            case KeyEvent.VK_D -> camera = camera.right(0.3);
            case KeyEvent.VK_W -> camera = camera.forward(0.3);
            case KeyEvent.VK_S -> camera = camera.backward(0.3);
            case KeyEvent.VK_Q -> camera = camera.up(0.3);
            case KeyEvent.VK_E -> camera = camera.down(0.3);
            case KeyEvent.VK_O -> camera = camera.addZenith(0.3);
            case KeyEvent.VK_L -> camera = camera.addZenith(-0.3);
            case KeyEvent.VK_P -> toggleSelectedSolid();
            case KeyEvent.VK_SHIFT -> {
                toggleProjection();
                initScene();
            }
        }
        renderScene();
    }

    private void handleMouseDrag(MouseEvent e) {
        double deltaX = (e.getX() - oldX) / 200.0;
        double deltaY = (e.getY() - oldY) / 200.0;

        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double maxTranslationDistance = 2.0;
        if (distance > maxTranslationDistance) {
            double scaleFactor = maxTranslationDistance / distance;
            deltaX *= scaleFactor;
            deltaY *= scaleFactor;
        }
        switch (transformMode) {
            case TRANSLATION -> {
                if (isBothSelected) {
                    translPyramid = translPyramid.mul(new Mat4Transl(deltaX * 2, 0.0, -deltaY * 2));
                    translOctahedron = translOctahedron.mul(new Mat4Transl(deltaX * 2, 0.0, -deltaY * 2));
                } else if (isPyramidSelected) {
                    translPyramid = translPyramid.mul(new Mat4Transl(deltaX * 2, 0.0, -deltaY * 2));
                } else {
                    translOctahedron = translOctahedron.mul(new Mat4Transl(deltaX * 2, 0.0, -deltaY * 2));
                }
            }
            case SCALE -> {
                double scaleFactor = calculateScaleFactor(e.getX(), e.getY());
                if (isBothSelected) {
                    scalePyramid = scalePyramid.mul(new Mat4Scale(scaleFactor, scaleFactor, scaleFactor));
                    scaleOctahedron = scaleOctahedron.mul(new Mat4Scale(scaleFactor, scaleFactor, scaleFactor));
                } else if (isPyramidSelected) {
                    scalePyramid = scalePyramid.mul(new Mat4Scale(scaleFactor, scaleFactor, scaleFactor));
                } else {
                    scaleOctahedron = scaleOctahedron.mul(new Mat4Scale(scaleFactor, scaleFactor, scaleFactor));
                }
            }
            case ROTATION -> {
                Mat4 rotationMatrix = calculateRotationMatrix(e.getX(), e.getY());
                if (isBothSelected) {
                    rotatePyramid = rotationMatrix.mul(rotatePyramid);
                    rotateOctahedron = rotationMatrix.mul(rotateOctahedron);
                } else if (isPyramidSelected) {
                    rotatePyramid = rotationMatrix.mul(rotatePyramid);
                } else {
                    rotateOctahedron = rotationMatrix.mul(rotateOctahedron);
                }
            }
        }
        oldX = e.getX();
        oldY = e.getY();
        renderScene();
    }

    private void initScene() {
        camera = new Camera(new Vec3D(0.4, -5, 1),
                Math.toRadians(90),
                Math.toRadians(-15),
                1. ,
                true);

        projectionMatrix = isOrthographicProjection ?
                new Mat4OrthoRH(800,600, 0.1, 20) :
                new Mat4PerspRH(Math.PI / 4, 600 / 800., 0.1, 20);


        pyramid = new Pyramid();
        octahedron = new Octahedron();
        setActiveSolid(pyramid);
        renderScene();
    }

    private void renderScene() {
        clear();
        wiredRenderer.setView(camera.getViewMatrix());
        wiredRenderer.setProj(projectionMatrix);
        if (isBothSelected){
            Mat4 modelMatrixPyramid = translPyramid.mul(scalePyramid).mul(rotatePyramid);
            Mat4 modelMatrixOctahedron = translOctahedron.mul(scaleOctahedron).mul(rotateOctahedron);
            pyramid.setModel(modelMatrixPyramid);
            octahedron.setModel(modelMatrixOctahedron);
        } else {
            Mat4 modelMatrix = (isPyramidSelected ? translPyramid.mul(scalePyramid).mul(rotatePyramid)
                    : translOctahedron.mul(scaleOctahedron).mul(rotateOctahedron));
            activeSolid.setModel(modelMatrix);
        }
        wiredRenderer.render(pyramid, Color.decode("#12D97F"));
        wiredRenderer.render(octahedron, Color.decode("#DB126B"));

        panel.repaint();
    }

    private void clear() {
        raster.setClearColor(0x000000);
        raster.clear();
    }

    private void present(Graphics graphics) {
        raster.repaint(graphics);
    }

    private double calculateScaleFactor(int mouseX, int mouseY) {
        return (1.0 + (2.0 * (oldY - mouseY) / panel.getHeight()))
                * (1.0 - (2.0 * (oldX - mouseX) / panel.getWidth()));
    }

    private Mat4 calculateRotationMatrix(int mouseX, int mouseY) {
        double rotY = Math.PI * (oldY - mouseY) / panel.getHeight();
        double rotZ = Math.PI * (oldX - mouseX) / panel.getWidth();

        Mat4 rotYMatrix = new Mat4RotY(rotY);
        Mat4 rotZMatrix = new Mat4RotZ(rotZ);

        return rotYMatrix.mul(rotZMatrix);
    }
    public void setActiveSolid(Solid solid) {
        activeSolid = solid;
    }
    private void checkSelection() {
        isBothSelected = isPyramidSelected && isOctahedronSelected;
    }
    private void toggleSelectedSolid() {
        if (isBothSelected) {
            isBothSelected = false;
            isPyramidSelected = true;
            isOctahedronSelected = false;
            activeSolid = pyramid;
        } else if (isPyramidSelected) {
            isPyramidSelected = false;
            isOctahedronSelected = true;
            activeSolid = octahedron;
        } else {
            isBothSelected = true;
            isPyramidSelected = true;
            isOctahedronSelected = true;
            activeSolid = pyramid;
        }
    }
    private void toggleProjection(){
        isOrthographicProjection = !isOrthographicProjection;
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Canvas3D(800, 600));
    }

    private enum Mode {
        DEFAULT, TRANSLATION, SCALE, ROTATION
    }
}
