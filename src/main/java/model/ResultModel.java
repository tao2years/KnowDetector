package model;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class ResultModel {
    private String pattern;
    private String relatedFiles;
    private String relatedEdges;

    public ResultModel(String pattern, String relatedFiles, String relatedEdges) {
        this.pattern = pattern;
        this.relatedFiles = relatedFiles;
        this.relatedEdges = relatedEdges;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getRelatedFiles() {
        return relatedFiles;
    }

    public void setRelatedFiles(String relatedFiles) {
        this.relatedFiles = relatedFiles;
    }

    public String getRelatedEdges() {
        return relatedEdges;
    }

    public void setRelatedEdges(String relatedEdges) {
        this.relatedEdges = relatedEdges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResultModel)) return false;
        ResultModel that = (ResultModel) o;
        return Objects.equals(getPattern(), that.getPattern()) && Objects.equals(getRelatedFiles(), that.getRelatedFiles()) && Objects.equals(getRelatedEdges(), that.getRelatedEdges());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPattern(), getRelatedFiles(), getRelatedEdges());
    }

    @Override
    public String toString() {
        return "ResultModel{" +
                "pattern='" + pattern + '\'' +
                ", relatedFiles='" + relatedFiles + '\'' +
                ", relatedEdges='" + relatedEdges + '\'' +
                '}';
    }
}
