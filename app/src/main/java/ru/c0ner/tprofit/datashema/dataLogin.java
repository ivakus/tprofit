package ru.c0ner.tprofit.datashema;





import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class dataLogin {

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;


    public dataLogin (String l, String p){
        this.username = l;
        this.password = p;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}