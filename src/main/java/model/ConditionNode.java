package model;

import java.util.Objects;

public class ConditionNode {
    private String condId;
    private String physicalChannel;
    private String params;

    public ConditionNode(){}

    public ConditionNode(String condId, String physicalChannel, String params) {
        this.condId = condId;
        this.physicalChannel = physicalChannel;
        this.params = params;
    }

    public String getCondId() {
        return condId;
    }

    public void setCondId(String condId) {
        this.condId = condId;
    }

    public String getPhysicalChannel() {
        return physicalChannel;
    }

    public void setPhysicalChannel(String physicalChannel) {
        this.physicalChannel = physicalChannel;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConditionNode)) return false;
        ConditionNode that = (ConditionNode) o;
        return Objects.equals(getCondId(), that.getCondId()) && Objects.equals(getPhysicalChannel(), that.getPhysicalChannel()) && Objects.equals(getParams(), that.getParams());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCondId(), getPhysicalChannel(), getParams());
    }

    @Override
    public String toString() {
        return "ConditionNode{" +
                "condId='" + condId + '\'' +
                ", physicalChannel='" + physicalChannel + '\'' +
                ", params='" + params + '\'' +
                '}';
    }
}
