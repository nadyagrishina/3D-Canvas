package solids;


import transforms.Point3D;

public class Cube extends Solid {

    public Cube() {
        // Vertex buffers
        vb.add(new Point3D(0,0,0)); //p0
        vb.add(new Point3D(1,0,0)); //p1
        vb.add(new Point3D(1,1,0)); //p2
        vb.add(new Point3D(0,1,0)); //p3

        // Index buffer
        addIndices(
                0, 1,
                1, 2,
                2, 3,
                3, 0
        );
    }



}
