package solids;

import transforms.*;

import java.awt.*;
import renderer.WiredRenderer;
public class ColoredAxes {
    private final WiredRenderer wiredRenderer;

    public ColoredAxes(WiredRenderer wiredRenderer) {
        this.wiredRenderer = wiredRenderer;
    }

    public void render() {
        renderAxis(new Vec3D(1, 0, 0), Color.decode("#DDAFCE")); //x green
        renderAxis(new Vec3D(0, 1, 0), Color.decode("#F03E01")); //y red
        renderAxis(new Vec3D(0, 0, 1), Color.decode("#09A9E2")); //z blue
    }

    private void renderAxis(Vec3D direction, Color color) {
        Point3D origin = new Point3D(0, 0, 0);
        Point3D end = new Point3D(direction);
        wiredRenderer.renderLine(origin, end, color);
    }
}
