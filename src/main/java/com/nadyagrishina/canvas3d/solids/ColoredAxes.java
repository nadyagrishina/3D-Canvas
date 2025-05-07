package com.nadyagrishina.canvas3d.solids;

import com.nadyagrishina.canvas3d.renderer.WiredRenderer;
import com.nadyagrishina.canvas3d.transforms.Point3D;
import com.nadyagrishina.canvas3d.transforms.Vec3D;

import java.awt.*;

public class ColoredAxes {
    private final WiredRenderer wiredRenderer;

    public ColoredAxes(WiredRenderer wiredRenderer) {
        this.wiredRenderer = wiredRenderer;
    }

    public void render(final Graphics2D gc) {
        renderAxis(gc, new Vec3D(1, 0, 0), Color.decode("#DDAFCE")); //x green
        renderAxis(gc, new Vec3D(0, 1, 0), Color.decode("#F03E01")); //y red
        renderAxis(gc, new Vec3D(0, 0, 1), Color.decode("#09A9E2")); //z blue
    }

    private void renderAxis(final Graphics2D gc, Vec3D direction, Color color) {
        Point3D origin = new Point3D(0, 0, 0);
        Point3D end = new Point3D(direction);
        wiredRenderer.renderLine(gc, origin, end, color);
    }
}
