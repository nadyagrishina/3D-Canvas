package com.nadyagrishina.canvas3d.solids;

import com.nadyagrishina.canvas3d.transforms.Point3D;

public class Cube extends Solid {
    public Cube() {
        // Vertex buffers
        vb.add(new Point3D(0,0,0)); //p0
        vb.add(new Point3D(1,0,0)); //p1
        vb.add(new Point3D(1,1,0)); //p2
        vb.add(new Point3D(0,1,0)); //p3

        vb.add(new Point3D(0,0,1)); //p4
        vb.add(new Point3D(1,0,1)); //p5
        vb.add(new Point3D(1,1,1)); //p6
        vb.add(new Point3D(0,1,1)); //p7

        // Index buffer
        addIndices(
                0, 1,  // Dolní tvář
                1, 2,
                2, 3,
                3, 0,
                4, 5,  // Horní tvář
                5, 6,
                6, 7,
                7, 4,
                0, 4,  // Spojovací hrany
                1, 5,
                2, 6,
                3, 7
        );
    }
}
