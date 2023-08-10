package model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class ServiceNode {
    private String devType;
    private String prodId;
    private String deviceId;
    private String capabilityId;
    private String command;
    private String value;
    private String serviceId;
    private String delay;
    private static final Logger LOGGER = LogManager.getLogger();

    public ServiceNode(){}

    public ServiceNode(String devType, String prodId, String deviceId, String capabilityId, String command, String value, String serviceId, String delay) {
        this.devType = devType;
        this.prodId = prodId;
        this.deviceId = deviceId;
        this.capabilityId = capabilityId;
        this.command = command;
        this.value = value;
        this.serviceId = serviceId;
        this.delay = delay;
    }

    public String getDevType() {
        return devType;
    }

    public void setDevType(String devType) {
        this.devType = devType;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

//    public String getDServiceType(){
//        return null;
//    }

    public String getServiceWithOutValue(){
        String[] serviceArr = serviceId.split("_");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < serviceArr.length - 1; i++) {
            result.append(serviceArr[i]).append("_");
        }
        String finalString = result.toString();
        if (finalString.endsWith("_")) {
            finalString = finalString.substring(0, finalString.length() - 1);
        }
        return finalString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceNode)) return false;
        ServiceNode that = (ServiceNode) o;
        return Objects.equals(getDevType(), that.getDevType()) && Objects.equals(getProdId(), that.getProdId()) && Objects.equals(getDeviceId(), that.getDeviceId()) && Objects.equals(getCapabilityId(), that.getCapabilityId()) && Objects.equals(getCommand(), that.getCommand()) && Objects.equals(getValue(), that.getValue()) && Objects.equals(getServiceId(), that.getServiceId()) && Objects.equals(getDelay(), that.getDelay());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDevType(), getProdId(), getDeviceId(), getCapabilityId(), getCommand(), getValue(), getServiceId(), getDelay());
    }

    @Override
    public String toString() {
        return "ServiceNode{" +
                "'devType':'" + devType + '\'' +
                ", 'prodId':'" + prodId + '\'' +
                ", 'deviceId':'" + deviceId + '\'' +
                ", 'capabilityId':'" + capabilityId + '\'' +
                ", 'command':'" + command + '\'' +
                ", 'value':'" + value + '\'' +
                ", 'serviceId':'" + serviceId + '\'' +
                ", 'delay':'" + delay + '\'' +
                '}';
    }

    public static ServiceNode fromString(String input) {
        JSONObject json = JSON.parseObject(input.replace("ServiceNode", ""));
        return json.toJavaObject(ServiceNode.class);
    }
}
