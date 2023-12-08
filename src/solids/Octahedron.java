package solids;

import transforms.Point3D;
public class Octahedron extends Solid {
    public Octahedron() {
        //vertex buffers
        vb.add(new Point3D(1, 0, 0)); //p0
        vb.add(new Point3D(0, 1, 0)); //p1
        vb.add(new Point3D(-1, 0, 0)); //p2
        vb.add(new Point3D(0, -1, 0)); //p3
        vb.add(new Point3D(0, 0, 1)); //p4
        vb.add(new Point3D(0, 0, -1)); //p5

        // Index buffer
        addIndices(
                0, 1,
                1, 2,
                2, 3,
                3, 0,
                0, 4,
                1, 4,
                2, 4,
                3, 4,
                0, 5,
                1, 5,
                2, 5,
                3, 5
        );
    }
}
