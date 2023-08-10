package model;

import graph.Edge;

import java.util.Objects;

public class DataPair {
    private Edge leftEdge;
    private Edge rightEdge;

    public DataPair(Edge leftEdge, Edge rightEdge) {
        this.leftEdge = leftEdge;
        this.rightEdge = rightEdge;
    }

    public Edge getLeftEdge() {
        return leftEdge;
    }

    public void setLeftEdge(Edge leftEdge) {
        this.leftEdge = leftEdge;
    }

    public Edge getRightEdge() {
        return rightEdge;
    }

    public void setRightEdge(Edge rightEdge) {
        this.rightEdge = rightEdge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataPair)) return false;
        DataPair dataPair = (DataPair) o;
        return (leftEdge.equals(dataPair.leftEdge) && rightEdge.equals(dataPair.rightEdge)) ||
                (leftEdge.equals(dataPair.rightEdge) && rightEdge.equals(dataPair.leftEdge));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLeftEdge(), getRightEdge());
    }
}
