package solids;

import transforms.Cubic;
import transforms.Mat4;
import transforms.Point3D;

import java.util.ArrayList;
public class Coons extends Solid {
    double step;
    public Coons(final int smoothness, Point3D a, Point3D b, Point3D c, Point3D d) {
        Mat4 coons;
        vb = new ArrayList<>();
        ib = new ArrayList<>();
        coons = Cubic.COONS;

        step = (Math.PI * 2 / smoothness);
        Cubic cubic = new Cubic(coons, a, b, c, d);

        for (int i = 0; i < smoothness; i++) {
            vb.add(cubic.compute((double) i / smoothness));

            if (i != 0) {
                ib.add(i - 1);
                ib.add(i);
            }
        }
    }
}