package ru.c0ner.tprofit.datashema;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class dataLoginResponse {
        @SerializedName("token")
        @Expose
        private Object token;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("success")
        @Expose
        private Integer success;

        public Object getToken() {
            return token;
        }

        public void setToken(Object token) {
            this.token = token;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Integer getSuccess() {
            return success;
        }

        public void setSuccess(Integer success) {
            this.success = success;
        }

    }


