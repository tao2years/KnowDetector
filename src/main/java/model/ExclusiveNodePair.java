package model;

import java.util.Objects;

public class ExclusiveNodePair {
    private String source;
    private String target;

    public ExclusiveNodePair() {
    }

    public ExclusiveNodePair(String source, String target) {
        this.source = source;
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExclusiveNodePair)) return false;
        ExclusiveNodePair that = (ExclusiveNodePair) o;
        return Objects.equals(getSource(), that.getSource()) && Objects.equals(getTarget(), that.getTarget());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSource(), getTarget());
    }

    @Override
    public String toString() {
        return "ExclusiveNodePair{" +
                "source='" + source + '\'' +
                ", target='" + target + '\'' +
                '}';
    }
}
