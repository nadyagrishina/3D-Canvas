package model;

public class Rectangle extends Polygon {

    public Rectangle(Point p1, Point p3) {
        addPoint(p1);
        addPoint(new Point(p1.x, p3.y));
        addPoint(p3);
        addPoint(new Point(p3.x, p1.y));
    }
}
