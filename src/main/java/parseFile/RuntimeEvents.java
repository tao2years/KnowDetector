package parseFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import util.RawJsonDeserializer;

public class RuntimeEvents {
    @JsonProperty
    private String devType;
    @JsonProperty
    private String deviceId;
    @JsonProperty
    private String eventId;
    @JsonProperty
    private RuntimeParams params;
    @JsonProperty
    private String prodId;

    public RuntimeEvents(){}

    public String getDevType() {
        return devType;
    }

    public void setDevType(String devType) {
        this.devType = devType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public RuntimeParams getParams() {
        return params;
    }

    public void setParams(RuntimeParams params) {
        this.params = params;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    @Override
    public String toString() {
        return "RuntimeEvents{" +
                "devType='" + devType + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", eventId='" + eventId + '\'' +
                ", params=" + params +
                ", prodId='" + prodId + '\'' +
                '}';
    }
}
