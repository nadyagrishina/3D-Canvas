package rasterize;

public class LineRasterizerTrivial extends LineRasterizer{
    public LineRasterizerTrivial(Raster raster) {
        super(raster);
    }

    @Override
    protected void drawLine(int x1, int y1, int x2, int y2) {
        // spočítat k
        float k = (y2 - y1) / (float)(x2 - x1);
        // spočítat q
        float q = y1 - k * x1;

        if(x1 > x2) {
            int tmp = x1;
            x1 = x2;
            x2 = tmp;
        }

        // pro každé x od x1 do x2, spočítám y
        for(int x = x1; x <= x2; x++) {
            int y = Math.round(k * x + q);
            // TODO: co barva?
            raster.setPixel(x, y, 0xffff00);
        }


    }
}
