package parseFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import util.RawJsonDeserializer;

public class Delay {
    @JsonProperty
    private String delaySync;
    private String delayType;
    private String time;
    public Delay(){}

    public String getDelaySync() {
        return delaySync;
    }

    public void setDelaySync(String delaySync) {
        this.delaySync = delaySync;
    }

    public String getDelayType() {
        return delayType;
    }

    public void setDelayType(String delayType) {
        this.delayType = delayType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Delay{" +
                "delaySync='" + delaySync + '\'' +
                ", delayType='" + delayType + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
