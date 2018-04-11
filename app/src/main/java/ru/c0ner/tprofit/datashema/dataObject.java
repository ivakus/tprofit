package ru.c0ner.tprofit.datashema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class dataObject {


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("manager")
    @Expose
    private Integer manager;
    @SerializedName("manager_name")
    @Expose
    private String managerName;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("object_status")
    @Expose
    private String objectStatus;
    @SerializedName("slef_url")
    @Expose
    private String slefUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getManager() {
        return manager;
    }

    public void setManager(Integer manager) {
        this.manager = manager;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectStatus() {
        return objectStatus;
    }

    public void setObjectStatus(String objectStatus) {
        this.objectStatus = objectStatus;
    }

    public String getSlefUrl() {
        return slefUrl;
    }

    public void setSlefUrl(String slefUrl) {
        this.slefUrl = slefUrl;
    }

}