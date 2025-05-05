package com.nadyagrishina.canvas3d.rasterize;

import java.awt.*;

public class LineRasterizerGraphics extends LineRasterizer {
    public LineRasterizerGraphics(Raster raster) {
        super(raster);
    }
    @Override
    public void drawLine(int x1, int y1, int x2, int y2, Color color)  {
        Graphics2D g = (Graphics2D) ((RasterBufferedImage)raster).getImg().getGraphics();
        g.setStroke(new BasicStroke(2));
        g.setColor(color);
        g.drawLine(x1, y1, x2, y2);
    }
}
