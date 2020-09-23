package com.company;


import java.net.MalformedURLException;
import java.util.HashMap;

/**
 * Controls over different parts of a request and works like a repository which saves
 * different parts of a request in gui interface.
 * Consisted of 2 parts , one part for sending the request and getting the response
 * and other part for saving the data in gui for each request .
 * RequestHandler is used for logic and sending the request (which was composed
 * in phase 2 of this project ) and other fields are used for gui interface .
 * Also contains a GUIRequest which is an object for serializing and saving a request
 * in GUI interface (Different from Request class ) .
 *
 * @author Mina Beiki
 */
public class RequestRepository {
    private RequestHandler requestHandler ;
    private GUIRequest guiRequest ;
    private String name ="" ,  url="" , method ="", json="" , binaryFileAddress="" ;
    private HashMap<String ,String> headers , formData , urlEncoded , query ;

    /**
     * Makes a new instance with the given name of the request .
     * @param name String , name of the request
     */
    public RequestRepository(String name){
        this.name=name ;
        headers = new HashMap<>();
        formData = new HashMap<>();
        urlEncoded = new HashMap<>();
        query = new HashMap<>();
        guiRequest = new GUIRequest(name);
    }

    /**
     * Generates a request handler for this repository to be sent and get the
     * response .
     * @param split String[] which is the input needed to make the RequestHandler instance
     * @return RequestHandler which is generated
     */
    public RequestHandler generateRequestHandler (String[] split){
        try {
            requestHandler = new RequestHandler(split);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return requestHandler ;
    }

    /**
     * Gets url
     * @return URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets name
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets url
     */
    public void setUrl(String url) {
        this.url = url;
        guiRequest.setUrl(url);
    }

    /**
     * Sets method
     */
    public void setMethod(String method) {
        this.method = method;
        guiRequest.setMethod(method);
    }

    /**
     * Sets binary file address
     */
    public void setBinaryFileAddress(String binaryFileAddress) {
        this.binaryFileAddress = binaryFileAddress;
        guiRequest.setMethod(method);
    }

    /**
     * Gets json body
     * @return String
     */
    public String getJson() {
        return json;
    }

    /**
     * Sets json body
     * @param json String
     */
    public void setJson(String json) {
        this.json = json;
        guiRequest.setJson(json);
    }

    /**
     * Sets headers
     * @param headers Hash Map of headers
     */
    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
        guiRequest.setHeaders(headers);
    }

    /**
     * Sets form data
     * @param formData Hash Map of form data name and values
     */
    public void setFormData(HashMap<String, String> formData) {
        this.formData = formData;
        guiRequest.setFormData(formData);
    }

    /**
     * Sets form data
     * @param urlEncoded Hash Map of url encoded pairs
     */
    public void setUrlEncoded(HashMap<String, String> urlEncoded) {
        this.urlEncoded = urlEncoded;
        guiRequest.setUrlEncoded(urlEncoded);
    }

    /**
     * Sets query params
     * @param query Hash map of query params
     */
    public void setQuery(HashMap<String, String> query) {
        this.query = query;
        guiRequest.setQuery(query);
    }

    /**
     * Gets GUIRequest for this request rep
     * @return GUIRequest
     */
    public GUIRequest getGuiRequest() {
        return guiRequest;
    }
}
