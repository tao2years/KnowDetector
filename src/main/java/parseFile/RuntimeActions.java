package parseFile;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RuntimeActions {
    @JsonProperty
    private String devType;
    @JsonProperty
    private String deviceId;
    @JsonProperty
    private String actionId;
    @JsonProperty
    private RuntimeParams params;
    @JsonProperty
    private String prodId;

    public RuntimeActions(){}

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

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
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
        return "RuntimeActions{" +
                "devType='" + devType + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", actionId='" + actionId + '\'' +
                ", params=" + params +
                ", prodId='" + prodId + '\'' +
                '}';
    }
}
