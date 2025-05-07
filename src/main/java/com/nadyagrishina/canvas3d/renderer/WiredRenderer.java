package com.nadyagrishina.canvas3d.renderer;

import com.nadyagrishina.canvas3d.solids.ColoredAxes;
import com.nadyagrishina.canvas3d.solids.Solid;
import com.nadyagrishina.canvas3d.transforms.Mat4;
import com.nadyagrishina.canvas3d.transforms.Mat4Identity;
import com.nadyagrishina.canvas3d.transforms.Point3D;
import com.nadyagrishina.canvas3d.transforms.Vec3D;

import java.awt.*;

public class WiredRenderer {
    private Mat4 view;
    private Mat4 proj;

    public WiredRenderer() {
        this.view = new Mat4Identity();
    }
    public void render(final Graphics2D gc, Solid solid, Color color) {
        // Solid má index buffer, projdu ho v cyklu
        // pro každé dva prvky si načtu odpovídající vertex
        // spojím vertexy linou

        for (int i = 0; i < solid.getIb().size(); i += 2) {
            int indexA = solid.getIb().get(i);
            int indexB = solid.getIb().get(i + 1);

            Point3D a = solid.getVb().get(indexA);
            Point3D b = solid.getVb().get(indexB);

            a = a.mul(solid.getModel()).mul(view).mul(proj);
            b = b.mul(solid.getModel()).mul(view).mul(proj);

            //Clipping
            if (clipLine(a, b)) {
                continue;
            }

            //Dehomogenizace
            a = dehomogenize(a);
            b = dehomogenize(b);

            Vec3D v1 = new Vec3D(a);
            Vec3D v2 = new Vec3D(b);

            //Window-viewport transformation
            v1 = transformToWindow(v1);
            v2 = transformToWindow(v2);

            //Rasterization
            gc.setStroke(new BasicStroke(2));
            gc.setColor(color);
            gc.drawLine(
                (int)Math.round(v1.getX()), (int)Math.round(v1.getY()),
                (int)Math.round(v2.getX()), (int)Math.round(v2.getY())
            );
        }
    }
    private boolean clipLine(Point3D a, Point3D b) {
        double xMin = -1.0;
        double xMax = 1.0;
        double yMin = -1.0;
        double yMax = 1.0;
        double zMin = 1.0;
        double zMax = 1.0;
        return !(Math.min(a.getX(), b.getX()) < xMin || Math.max(a.getX(), b.getX()) > xMax
                || Math.min(a.getY(), b.getY()) < yMin || Math.max(a.getY(), b.getY()) > yMax
                || Math.min(a.getZ(), b.getZ()) < zMin || Math.max(a.getZ(), b.getZ()) > zMax);
    }

    private Point3D dehomogenize(Point3D point) {
        double x = point.getX();
        double y = point.getY();
        double z = point.getZ();
        double w = point.getW();
        return new Point3D(x / w, y / w, z / w);
    }

    public Vec3D transformToWindow(Vec3D p) {
        return p.mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D((800 - 1) / 2., (600 - 1) / 2., 1));
    }
    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProj(Mat4 proj) {
        this.proj = proj;
    }
    public void renderAxes(final Graphics2D gc) {
        ColoredAxes coloredAxes = new ColoredAxes(this);
        coloredAxes.render(gc);
    }
    public void renderLine(final Graphics2D gc, Point3D start, Point3D end, Color color) {
        start = start.mul(view).mul(proj);
        end = end.mul(view).mul(proj);

        if (clipLine(start, end)) {
            return;
        }

        start = dehomogenize(start);
        end = dehomogenize(end);

        Vec3D v1 = new Vec3D(start);
        Vec3D v2 = new Vec3D(end);

        v1 = transformToWindow(v1);
        v2 = transformToWindow(v2);

        gc.setStroke(new BasicStroke(2));
        gc.setColor(color);
        gc.drawLine(
            (int) Math.round(v1.getX()), (int) Math.round(v1.getY()),
            (int) Math.round(v2.getX()), (int) Math.round(v2.getY())
        );
    }
}

