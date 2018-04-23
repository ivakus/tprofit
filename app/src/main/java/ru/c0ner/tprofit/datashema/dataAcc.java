package ru.c0ner.tprofit.datashema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class dataAcc {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("url")
    @Expose
    private String url;

    public Integer getId_predict() {
        return id_predict;
    }


    public void setId_predict(Integer id_predict) {
        this.id_predict = id_predict;
    }

    @SerializedName("id_predict")
    @Expose
    private Integer id_predict;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getToken() {

        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
