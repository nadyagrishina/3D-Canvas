package fill;

import model.Edge;
import model.Point;
import model.Polygon;
import rasterize.LineRasterizer;
import rasterize.PolygonRasterizer;

import java.util.ArrayList;

public class ScanLineFiller implements Filler{
    private LineRasterizer lineRasterizer;
    private PolygonRasterizer polygonRasterizer;
    private Polygon polygon;

    public ScanLineFiller(LineRasterizer lineRasterizer, PolygonRasterizer polygonRasterizer, Polygon polygon) {
        this.lineRasterizer = lineRasterizer;
        this.polygonRasterizer = polygonRasterizer;
        this.polygon = polygon;
    }

    @Override
    public void fill() {
        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < polygon.size(); i++){
            Point p1 = polygon.getPoint(i);
            int indexB = i + 1;
            if(indexB == polygon.size())
                indexB = 0;
            Point p2 = polygon.getPoint(indexB);

            Edge edge = new Edge(p1.x, p1.y, p2.x, p2.y);
            if(edge.isHorizontal())
                continue;

            edges.add(edge);
        }

        // TODO: najít yMin a yMax - projít pointy polygonu
        // yMin, yMax, defaultně naplnit první pointem

        // TODO: for cyklus od yMin po yMax
        // {
            //  Pro všechny hrany
            // {
                //  1. TODO: zjistíme, jestli existuje průsečík
                //  2. TODO: pokud existuje, tak ho spočítáme. Výsledek uložím.
            // }

            // TODO: Seřadit nalezené průsečíky
            // TODO: spojit luchý se sudým
        // }

        // TODO: obtáhnu polygon
    }
}
