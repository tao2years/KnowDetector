package model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import parseFile.App;

import java.util.Objects;

public class AppInfo {
    private static final Logger LOGGER = LogManager.getLogger();
    String appId;
    String fileName;
    String relation;

    public AppInfo() {}

    public AppInfo(String appId, String fileName) {
        this.appId = appId;
        this.fileName = fileName;
    }

    public AppInfo(String relation) {
        this.relation = relation;
    }

    public AppInfo(String appId, String fileName, String relation) {
        this.appId = appId;
        this.fileName = fileName;
        this.relation = relation;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppInfo)) return false;
        AppInfo appInfo = (AppInfo) o;
        return Objects.equals(getAppId(), appInfo.getAppId()) && Objects.equals(getFileName(), appInfo.getFileName()) && Objects.equals(getRelation(), appInfo.getRelation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAppId(), getFileName(), getRelation());
    }

    @Override
    public String toString() {
        return "EdgeInfo{" +
                "'relation':'" + relation + '\'' +
                ", 'appId':'" + appId + '\'' +
                ", 'fileName':'" + fileName + '\'' +
                '}';
    }

    public static AppInfo fromString(String input) {
        if (input!=null && input.contains("EdgeInfo")){
            JSONObject json = JSON.parseObject(input.replace("EdgeInfo", ""));
            return json.toJavaObject(AppInfo.class);
        } else {
            return new AppInfo(input);
        }
    }
}
