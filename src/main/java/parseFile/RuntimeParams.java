package parseFile;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RuntimeParams {
    @JsonProperty
    private String capabilityId;
    @JsonProperty
    private String command;
    @JsonProperty
    private String value;
    public RuntimeParams(){}

    public String getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "RuntimeParams{" +
                "capabilityId='" + capabilityId + '\'' +
                ", command='" + command + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
