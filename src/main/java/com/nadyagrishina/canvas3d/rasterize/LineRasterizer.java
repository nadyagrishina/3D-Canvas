package com.nadyagrishina.canvas3d.rasterize;

import java.awt.*;
import java.awt.image.BufferedImage;

public class LineRasterizer {
    BufferedImage raster;

    public LineRasterizer(BufferedImage raster){
        this.raster = raster;
    }

    public void rasterize(int x1, int y1, int x2, int y2, Color color) {
        Graphics2D g = (Graphics2D) raster.getGraphics();
        g.setStroke(new BasicStroke(2));
        g.setColor(color);
        g.drawLine(x1, y1, x2, y2);
    }
}
