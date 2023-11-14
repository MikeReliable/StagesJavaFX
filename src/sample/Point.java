package sample;

import java.util.Objects;

public class Point {

    double coordinateX;
    double coordinateY;
    double Exx;

    public Point(double coordinateX, double coordinateY, double Exx) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.Exx = Exx;
    }

    public double getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(double coordinateX) {
        this.coordinateX = coordinateX;
    }

    public double getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(double coordinateY) {
        this.coordinateY = coordinateY;
    }

    public double getExx() {
        return Exx;
    }

    public void setExx(double exx) {
        Exx = exx;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.coordinateX, coordinateX) == 0 && Double.compare(point.coordinateY, coordinateY) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinateX, coordinateY);
    }
}
