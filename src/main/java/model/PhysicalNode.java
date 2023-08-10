package model;

import java.util.Objects;

public class PhysicalNode {
    private String physicalChannel;
    public PhysicalNode(){};

    public PhysicalNode(String physicalChannel) {
        this.physicalChannel = physicalChannel;
    }

    public String getPhysicalChannel() {
        return physicalChannel;
    }

    public void setPhysicalChannel(String physicalChannel) {
        this.physicalChannel = physicalChannel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhysicalNode)) return false;
        PhysicalNode that = (PhysicalNode) o;
        return Objects.equals(getPhysicalChannel(), that.getPhysicalChannel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPhysicalChannel());
    }

    @Override
    public String toString() {
        return "PhysicalNode{" +
                "physicalChannel='" + physicalChannel + '\'' +
                '}';
    }
}
