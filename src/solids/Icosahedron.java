package solids;

import transforms.Point3D;

public class Icosahedron extends Solid {
    public Icosahedron() {
        // Zlatý řez (Golden Ratio)
        double phi = (1 + Math.sqrt(5)) / 2;

        //vertex buffers
        vb.add(new Point3D(phi, 1, 0)); //p0
        vb.add(new Point3D(-phi, 1, 0)); //p1
        vb.add(new Point3D(phi, -1, 0)); //p2
        vb.add(new Point3D(-phi, -1, 0)); //p3

        vb.add(new Point3D(1, 0, phi)); //p4
        vb.add(new Point3D(1, 0, -phi)); //p5
        vb.add(new Point3D(-1, 0, phi)); //p6
        vb.add(new Point3D(-1, 0, -phi)); //p7


        vb.add(new Point3D(0, phi, 1)); //p8
        vb.add(new Point3D(0, -phi, 1)); //p9
        vb.add(new Point3D(0, phi, -1)); //p10
        vb.add(new Point3D(0, -phi, -1)); //p11

        // Index buffer
        addIndices(
                0, 8, 4,
                0, 5, 10,
                2, 4, 9,
                2, 11, 5,
                1, 6, 8,

                1, 10, 7,
                3, 9, 6,
                3, 7, 11,
                0, 10, 8,
                1, 8, 10,

                2, 9, 11,
                3, 9, 11,
                4, 2, 0,
                5, 0, 2,
                6, 1, 3,

                7, 3, 1,
                8, 6, 4,
                9, 4, 6,
                10, 5, 7,
                11, 7, 5
        );
    }
}
