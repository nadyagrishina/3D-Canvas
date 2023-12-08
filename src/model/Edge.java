package model;

public record Edge(int x1, int y1, int x2, int y2) {
    public boolean isHorizontal() {
        return y1 == y2;
    }

    public Point intersection(Point p0, Point p1) {
        double denominator = (p0.getX() - p1.getX()) * (this.y1 - this.y2) - (p0.getY() - p1.getY()) * (this.x1 - this.x2);
        double x = ((p0.getX() * p1.getY() - p0.getY() * p1.getX()) * (this.x1 - this.x2) - (this.x1 * this.y2 - this.y1 * this.x2) * (p0.getX() - p1.getX())) / denominator;
        double y = ((p0.getX() * p1.getY() - p0.getY() * p1.getX()) * (this.y1 - this.y2) - (this.x1 * this.y2 - this.y1 * this.x2) * (p0.getY() - p1.getY())) / denominator;
        return new Point(x, y);
    }

    public boolean isInside(Point point) {
        return (this.y2 - this.y1) * point.getX() - (this.x2 - this.x1) * point.getY() + this.x2 * this.y1 - this.y2 * this.x1 > 0.0;
    }
}

