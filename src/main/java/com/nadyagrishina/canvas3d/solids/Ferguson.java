package com.nadyagrishina.canvas3d.solids;

import com.nadyagrishina.canvas3d.transforms.Cubic;
import com.nadyagrishina.canvas3d.transforms.Mat4;
import com.nadyagrishina.canvas3d.transforms.Point3D;

import java.util.ArrayList;

public class Ferguson extends Solid {
    double step;
    public Ferguson(final int smoothness, Point3D a, Point3D b, Point3D c, Point3D d) {
        Mat4 fergunson;
        vb = new ArrayList<>();
        ib = new ArrayList<>();
        fergunson = Cubic.FERGUSON;

        step = (Math.PI * 2 / smoothness);
        Cubic cubic = new Cubic(fergunson, a, b, c, d);

        for (int i = 0; i < smoothness; i++) {
            vb.add(cubic.compute((double) i / smoothness));

            if (i != 0) {
                ib.add(i - 1);
                ib.add(i);
            }
        }
    }
}

