package com.nadyagrishina.canvas3d;

import com.nadyagrishina.canvas3d.renderer.WiredRenderer;
import com.nadyagrishina.canvas3d.solids.*;
import com.nadyagrishina.canvas3d.transforms.*;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Canvas3D extends JPanel {
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
    @Setter private Solid activeSolid;
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
        wiredRenderer = new WiredRenderer();

        this.setBackground(Color.BLACK);
        this.setPreferredSize(new Dimension(width, height));

        initScene();

        this.setFocusable(true);
        this.requestFocusInWindow();

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                handleKeyPress(e);
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                transformMode = Mode.DEFAULT;
            }
        });
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                oldX = e.getX();
                oldY = e.getY();
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
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
        this.repaint();
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
                    this.repaint();
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
        this.repaint();
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
        this.repaint();
    }

    @Override
    public void paintComponent(final Graphics gc) {
        super.paintComponent(gc);

        gc.setColor(Color.BLACK);
        gc.fillRect(0, 0, getWidth(), getHeight());

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
            wiredRenderer.renderAxes((Graphics2D) gc);
        }

        Color pyramidColor = Color.decode("#7455F1");
        Color octahedronColor = Color.decode("#D3F157");

        if (isBothVisible) {
            wiredRenderer.render((Graphics2D) gc, pyramid, pyramidColor);
            wiredRenderer.render((Graphics2D) gc,octahedron, octahedronColor);
        } else if (isOctahedronVisible) {
            wiredRenderer.render((Graphics2D) gc,octahedron, octahedronColor);
        } else {
            wiredRenderer.render((Graphics2D) gc,pyramid, pyramidColor);
        }
        if (isCubicVisible) {
            wiredRenderer.render((Graphics2D) gc,ferguson, Color.decode("#F1A855"));
            wiredRenderer.render((Graphics2D) gc,bezier, Color.decode("#F55993"));
            wiredRenderer.render((Graphics2D) gc,coons, Color.decode("#6AF155"));
        }

        this.repaint();
    }

    private double calculateScaleFactor(int mouseX, int mouseY) {
        return (1.0 + (2.0 * (oldY - mouseY) / this.getHeight()))
                * (1.0 - (2.0 * (oldX - mouseX) / this.getWidth()));
    }
    private Mat4 calculateRotationMatrix(int mouseX, int mouseY) {
        double rotY = 1.5 * Math.PI * (oldY - mouseY) / this.getHeight();
        double rotZ = 1.5 * Math.PI * (oldX - mouseX) / this.getWidth();

        Mat4 rotYMatrix = new Mat4RotY(rotY);
        Mat4 rotZMatrix = new Mat4RotZ(rotZ);

        return rotYMatrix.mul(rotZMatrix);
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
        this.repaint();
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
        this.repaint();
    }
    private enum Mode {
        DEFAULT, TRANSLATION, SCALE, ROTATION
    }
}
