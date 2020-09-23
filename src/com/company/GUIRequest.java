package com.company;

import java.util.HashMap;

/**
 * This class is used for saving a request in GUI interface and serializing it .
 * It is like an object of RequestRepository that can be serialized which contains
 * data to full fill panel 2 and its different components .
 *
 * @author Mina Beiki
 */
public class GUIRequest implements java.io.Serializable {
    private String name = "", url = "", method = "", json = "", binaryFileAddress = "";
    private HashMap<String, String> headers, formData, urlEncoded, query;

    /**
     * Makes a new instance of GUIRequest .
     * @param name String , name of the request
     */
    public GUIRequest(String name) {
        this.name = name;
        headers = new HashMap<>();
        formData = new HashMap<>();
        urlEncoded = new HashMap<>();
        query = new HashMap<>();
    }


    public String getUrl() {
        return url;
    }

    /**
     * Gets name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets url
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets method
     * @return
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets method
     * @param method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Gets binary address
     * @return
     */
    public String getBinaryFileAddress() {
        return binaryFileAddress;
    }

    /**
     * Gets json body
     * @return
     */
    public String getJson() {
        return json;
    }

    /**
     * sets json body
     * @param json
     */
    public void setJson(String json) {
        this.json = json;
    }

    /**
     * gets headers
     * @return
     */
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    /**
     * sets headers
     * @param headers
     */
    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    /**
     * gets form data
     * @return
     */
    public HashMap<String, String> getFormData() {
        return formData;
    }

    /**
     * sets form data
     * @param formData
     */
    public void setFormData(HashMap<String, String> formData) {
        this.formData = formData;
    }

    /**
     * gets url encoded body
     * @return
     */
    public HashMap<String, String> getUrlEncoded() {
        return urlEncoded;
    }
    /**
     * sets url encoded body
     */
    public void setUrlEncoded(HashMap<String, String> urlEncoded) {
        this.urlEncoded = urlEncoded;
    }
    /**
     * gets query params
     * @return
     */
    public HashMap<String, String> getQuery() {
        return query;
    }
    /**
     * sets query params
     */
    public void setQuery(HashMap<String, String> query) {
        this.query = query;
    }
}


