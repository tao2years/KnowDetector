package parseFile;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import util.RawJsonDeserializer;

public class RuntimeCondition {
    private String condId;
    private String physical;
    @JsonDeserialize(using = RawJsonDeserializer.class)
    private String params;
    public RuntimeCondition(){}

    public String getCondId() {
        return condId;
    }

    public void setCondId(String condId) {
        this.condId = condId;
    }

    public String getPhysical() {
        return physical;
    }

    public void setPhysical(String physical) {
        this.physical = physical;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "RuntimeCondition{" +
                "condId='" + condId + '\'' +
                ", physical='" + physical + '\'' +
                ", params='" + params + '\'' +
                '}';
    }
}
