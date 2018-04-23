package ru.c0ner.tprofit.datashema;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class dataRecogniseResponse {

    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("id_predict")
    @Expose
    private Integer idPredict;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("id_current_object")
    @Expose
    private Integer idCurrentObject;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public Integer getIdPredict() {
        return idPredict;
    }

    public void setIdPredict(Integer idPredict) {
        this.idPredict = idPredict;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getIdCurrentObject() {
        return idCurrentObject;
    }

    public void setIdCurrentObject(Integer idCurrentObject) {
        this.idCurrentObject = idCurrentObject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
