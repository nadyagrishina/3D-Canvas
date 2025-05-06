package com.nadyagrishina.canvas3d;

import com.nadyagrishina.canvas3d.rasterize.LineRasterizer;
import com.nadyagrishina.canvas3d.renderer.WiredRenderer;
import com.nadyagrishina.canvas3d.solids.*;
import com.nadyagrishina.canvas3d.transforms.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.Serial;

public class Canvas3D {
    private final JPanel panel;
    private final BufferedImage raster;
    private final WiredRenderer wiredRenderer;
    private Camera camera = new Camera();
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
    private double deltaX;
    private double deltaY;
    private Solid pyramid;
    private Solid octahedron;
    private Solid activeSolid;
    private Solid ferguson;
    private Solid bezier;
    private Solid coons;
    private boolean isPyramidSelected = true;
    private boolean isOctahedronSelected = true;
    private boolean isBothSelected = true;
    private boolean isOrthographicProjection = false;
    private boolean axesVisible = true;
    private boolean isCubicVisible = true;
    private boolean isBothVisible = true;
    private boolean isPyramidVisible = true;
    private boolean isOctahedronVisible = true;
    public Canvas3D(int width, int height) {
        JPanel descriptionPanel = new JPanel();
        JLabel keyDescriptionLabel = new JLabel(
                "<html>" +
                        "<style>" +
                        "strong { color: #000000;}" +
                        "p {color: #202020; font-size: 11px; margin: 4px 0;}" +
                        "</style>" +
                        "<div style='width: 180px; padding: 5px 10px;'>" +
                        "<p><strong>[WSADQE]: </strong> to move camera.</p>" +
                        "<p><strong>[P]: </strong> to toggle active figure.</p>" +
                        "<p><strong>[C]: </strong> return the figure to its original position.</p>" +
                        "<p><strong>[R + mouse]: </strong> to rotate.</p>" +
                        "<p><strong>[T] + mouse: </strong> to scale.</p>" +
                        "<p><strong>[SHIFT + mouse]: </strong> to translate.</p>" +
                        "<p><strong>[M]: </strong> to show/off axes.</p>" +
                        "<p><strong>[N]: </strong> to show/off cubic lines.</p>" +
                        "<p><strong>[1]: </strong> to show/off octahedron.</p>" +
                        "<p><strong>[2]: </strong> to show/off pyramid.</p>" +
                        "</div>"
        );
        descriptionPanel.add(keyDescriptionLabel);

        JFrame frame = new JFrame();

        frame.setLayout(new BorderLayout());
        frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        LineRasterizer lineRasterizer = new LineRasterizer(raster);
        wiredRenderer = new WiredRenderer(lineRasterizer);

        panel = new JPanel() {
            @Serial
            private static final long serialVersionUID = 1L;
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        frame.add(panel, BorderLayout.CENTER);
        frame.add(descriptionPanel, BorderLayout.EAST);
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
            case KeyEvent.VK_SHIFT -> {
                transformMode = Mode.TRANSLATION;
                checkSelection();
            }
            case KeyEvent.VK_T -> {
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
            case KeyEvent.VK_P -> toggleSelectedSolid();
            case KeyEvent.VK_M -> toggleAxes();
            case KeyEvent.VK_N -> toggleCubic();
            case KeyEvent.VK_1 -> togglePyramid();
            case KeyEvent.VK_2 -> toggleOctahedron();
            case KeyEvent.VK_C -> {
                translOctahedron = new Mat4Transl(0.,0.,0.);
                translPyramid = new Mat4Transl(0.,0.,0.);
                rotateOctahedron = new Mat4Identity();
                rotatePyramid = new Mat4Identity();
                scaleOctahedron = new Mat4Identity();
                scalePyramid = new Mat4Identity();
            }
            case KeyEvent.VK_SPACE -> {
                toggleProjection();
                initScene();
            }
        }
        renderScene();
    }
    private void handleMouseDrag(MouseEvent e) {
        deltaX = (e.getX() - oldX) / 200.0;
        deltaY = (e.getY() - oldY) / 200.0;

        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double maxTranslationDistance = 0.1;
        if (distance > maxTranslationDistance) {
            double scaleFactor = maxTranslationDistance / distance;
            deltaX *= scaleFactor;
            deltaY *= scaleFactor;
        }
        switch (transformMode) {
            case TRANSLATION -> {
                if (isBothSelected && isBothVisible) {
                    translPyramid = translPyramid.mul(new Mat4Transl(deltaX * 2, 0.0, -deltaY * 2));
                    translOctahedron = translOctahedron.mul(new Mat4Transl(deltaX * 2, 0.0, -deltaY * 2));
                } else if (isPyramidSelected && isPyramidVisible) {
                    translPyramid = translPyramid.mul(new Mat4Transl(deltaX * 2, 0.0, -deltaY * 2));
                } else {
                    translOctahedron = translOctahedron.mul(new Mat4Transl(deltaX * 2, 0.0, -deltaY * 2));
                }
            }
            case SCALE -> {
                double scaleFactor = calculateScaleFactor(e.getX(), e.getY());
                if (isBothSelected && isBothVisible) {
                    scalePyramid = scalePyramid.mul(new Mat4Scale(scaleFactor, scaleFactor, scaleFactor));
                    scaleOctahedron = scaleOctahedron.mul(new Mat4Scale(scaleFactor, scaleFactor, scaleFactor));
                } else if (isPyramidSelected && isPyramidVisible) {
                    scalePyramid = scalePyramid.mul(new Mat4Scale(scaleFactor, scaleFactor, scaleFactor));
                } else {
                    scaleOctahedron = scaleOctahedron.mul(new Mat4Scale(scaleFactor, scaleFactor, scaleFactor));
                }
            }
            case DEFAULT -> {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    camera = camera.left(deltaX).up(deltaY);
                    renderScene();
                }
            }
            case ROTATION -> {
                Mat4 rotationMatrix = calculateRotationMatrix(e.getX(), e.getY());
                if (isBothSelected && isBothVisible) {
                    rotatePyramid = rotationMatrix.mul(rotatePyramid);
                    rotateOctahedron = rotationMatrix.mul(rotateOctahedron);
                } else if (isPyramidSelected && isPyramidVisible) {
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
        deltaX = 0.0;
        deltaY = 0.0;

        if (isOrthographicProjection) {
            // Initialize orthographic camera parameters
            camera = new Camera(new Vec3D(0.4, 0, -0.3),
                    Math.toRadians(90),
                    Math.toRadians(0),
                    0.1,
                    true);
            projectionMatrix = new Mat4OrthoRH(5.07, 3.9, 0.1, 20);
        } else {
            // Initialize perspective camera parameters
            camera = new Camera(new Vec3D(0.4, -5, 1),
                    Math.toRadians(90),
                    Math.toRadians(-15),
                    0.1,
                    true);
            projectionMatrix = new Mat4PerspRH(Math.PI / 4, 600 / 800., 0.1, 20);
        }

        pyramid = new Pyramid();
        octahedron = new Octahedron();

        ferguson = new Ferguson(100, new Point3D(-1,-1,-1),new Point3D(-2.5,-2,0),new Point3D(1,-3,2),new Point3D(0, 0, 1));
        bezier = new Bezier(100, new Point3D(-1,-1,-1),new Point3D(-2.5,-2,0),new Point3D(1,-3,2),new Point3D(0, 0, 1));
        coons = new Coons(100, new Point3D(-1,-1,-1),new Point3D(-2.5,-2,0),new Point3D(1,-3,2),new Point3D(0, 0, 1));

        setActiveSolid(pyramid);
        renderScene();
    }

    private void renderScene() {
        clear();

        //Transformace tvaru a polohy
        if (isBothSelected) {
            Mat4 modelMatrixPyramid = rotatePyramid.mul(scalePyramid).mul(translPyramid);
            Mat4 modelMatrixOctahedron = rotateOctahedron.mul(scaleOctahedron).mul(translOctahedron);
            pyramid.setModel(modelMatrixPyramid);
            octahedron.setModel(modelMatrixOctahedron);
        } else {
            Mat4 modelMatrix = (isPyramidSelected ? rotatePyramid.mul(scalePyramid).mul(translPyramid)
                    : rotateOctahedron.mul(scaleOctahedron).mul(translOctahedron));
            activeSolid.setModel(modelMatrix);
        }

        //Camera
        wiredRenderer.setView(camera.getViewMatrix());

        //Projection
        wiredRenderer.setProj(projectionMatrix);

        //Rasterization
        if (axesVisible) {
            wiredRenderer.renderAxes();
        }

        Color pyramidColor = Color.decode("#7455F1");
        Color octahedronColor = Color.decode("#D3F157");

        if (isBothVisible) {
            wiredRenderer.render(pyramid, pyramidColor);
            wiredRenderer.render(octahedron, octahedronColor);
        } else if (isOctahedronVisible) {
            wiredRenderer.render(octahedron, octahedronColor);
        } else {
            wiredRenderer.render(pyramid, pyramidColor);
        }
        if (isCubicVisible) {
            wiredRenderer.render(ferguson, Color.decode("#F1A855"));
            wiredRenderer.render(bezier, Color.decode("#F55993"));
            wiredRenderer.render(coons, Color.decode("#6AF155"));
        }

        panel.repaint();
    }

    private void clear() {
        final Graphics gc = raster.getGraphics();
        gc.setColor(Color.BLACK);
        gc.clearRect(0, 0, raster.getWidth(), raster.getHeight());
    }

    private void present(Graphics graphics) {
        graphics.drawImage(raster, 0, 0, null);
    }

    private double calculateScaleFactor(int mouseX, int mouseY) {
        return (1.0 + (2.0 * (oldY - mouseY) / panel.getHeight()))
                * (1.0 - (2.0 * (oldX - mouseX) / panel.getWidth()));
    }
    private Mat4 calculateRotationMatrix(int mouseX, int mouseY) {
        double rotY = 1.5 * Math.PI * (oldY - mouseY) / panel.getHeight();
        double rotZ = 1.5 * Math.PI * (oldX - mouseX) / panel.getWidth();

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
        renderScene();
    }
    private void togglePyramid(){
        if(isBothVisible){
            isBothVisible = false;
            isPyramidVisible = true;
            isOctahedronVisible = false;
        } else if (isPyramidVisible) {
            isOctahedronVisible = true;
            isBothVisible = true;
        }
    }
    private void toggleOctahedron(){
        if(isBothVisible){
            isBothVisible = false;
            isPyramidVisible = false;
            isOctahedronVisible = true;
        } else if (isOctahedronVisible) {
            isPyramidVisible = true;
            isBothVisible = true;
        }
    }
    private void toggleAxes(){
        axesVisible = !axesVisible;
    }
    private void toggleCubic(){
        isCubicVisible = !isCubicVisible;
    }
    private void toggleProjection(){
        isOrthographicProjection = !isOrthographicProjection;
        initScene();
        renderScene();
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Canvas3D(800, 600));
    }
    private enum Mode {
        DEFAULT, TRANSLATION, SCALE, ROTATION
    }
}
