package renderer;

import model.Point;
import rasterize.LineRasterizer;
import solids.Solid;
import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Point3D;
import transforms.Vec3D;

import java.awt.*;
import java.util.ArrayList;

public class WiredRenderer {
    private LineRasterizer lineRasterizer;
    private Mat4 view;
    private Mat4 proj;

    public WiredRenderer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
        this.view = new Mat4Identity();
    }

    public void render(Solid solid) {
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

            // TODO: ořezání
            
            // TODO: dehomogenizace

            Vec3D v1 = new Vec3D(a);
            Vec3D v2 = new Vec3D(b);

            v1 = transformToWindow(v1);
            v2 = transformToWindow(v2);

            lineRasterizer.rasterize(
                    (int)Math.round(v1.getX()), (int)Math.round(v1.getY()),
                    (int)Math.round(v2.getX()), (int)Math.round(v2.getY()),
                    Color.RED);
        }
    }

    public Vec3D transformToWindow(Vec3D p) {
        return p.mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D((800 - 1) / 2., (600 - 1) / 2., 1));
    }

    public void renderScene(ArrayList<Solid> scene) {
        for (Solid solid : scene)
            render(solid);
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProj(Mat4 proj) {
        this.proj = proj;
    }
}
