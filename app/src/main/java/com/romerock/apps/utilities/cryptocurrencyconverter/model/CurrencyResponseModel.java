package com.romerock.apps.utilities.cryptocurrencyconverter.model;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ebricko on 12/03/2018.
 */

public class CurrencyResponseModel {
    @SerializedName("uv")
    @Expose
    private UV response;

    public UV getResponse() {
        return response;
    }

    public void setResponse(UV response) {
        this.response = response;
    }

    public class UV {
        @SerializedName("success")
        private JsonElement success;
        @SerializedName("data")
        private JsonElement data;

        public JsonElement getSuccess() {
            return success;
        }

        public void setSuccess(JsonElement success) {
            this.success = success;
        }

        public JsonElement getData() {
            return data;
        }

        public void setData(JsonElement data) {
            this.data = data;
        }
    }
}
