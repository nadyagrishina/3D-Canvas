package com.nadyagrishina.canvas3d.solids;

import com.nadyagrishina.canvas3d.transforms.Mat4;
import com.nadyagrishina.canvas3d.transforms.Mat4Identity;
import com.nadyagrishina.canvas3d.transforms.Point3D;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
public class Solid {
    protected ArrayList<Point3D> vb = new ArrayList<>();
    protected ArrayList<Integer> ib = new ArrayList<>();

    @Setter protected Mat4 model = new Mat4Identity();

    protected void addIndices(Integer... indices) {
        ib.addAll(Arrays.asList(indices));
    }
}
