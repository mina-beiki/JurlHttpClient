package com.company;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Saves a list of all made requests in the program and makes it possible to have access
 * to them . Acts like a repository which saves all requests .
 *
 * @author Mina Beiki
 */
public class ApplicationRepository {
    private ArrayList<RequestRepository> allRequests ;

    public ApplicationRepository() {
        allRequests = new ArrayList<>();
    }

    /**
     * Adds a new request to repository .
     * @param name request name
     */
    public void addRequest(String name){
        RequestRepository req = new RequestRepository(name);
        allRequests.add(req);
    }

    /**
     * Gets a specific request in the repository by using the name of it .
     * @param name String , RequestRepository's name
     * @return RequestRepository found in app repository
     */
    public RequestRepository getRequestRepository(String name){
        for(RequestRepository req : allRequests){
            if(req.getName().equals(name)) {
               return req ;
            }
        }
        return null ;
    }

    /**
     * Saves a request in gui interface by getting its name and other panel 2 components to be serialized .
     * Makes a new instance of GUIRequest to be serialized (Each RequestRepository has a GUIRequest ) and
     * after setting its fields , serializes it .
     * @param name String , request's name
     * @param url URL , request's url
     * @param method String , request's method
     * @param json String , request's json body
     * @param binaryFileAddress String , request's binary file address
     * @param headers HashMap of headers
     * @param formData HashMap of form data pairs
     * @param urlencoded HashMap of urlEncoded pairs
     * @param query HashMap of query pairs
     */
    public void saveRequest (String name , String url , String method , String json, String binaryFileAddress , HashMap<String,String> headers ,
                             HashMap<String,String> formData , HashMap<String,String> urlencoded , HashMap<String,String> query ){
        //first set the fields :
        for(RequestRepository req : allRequests){
            if(req.getName().equals(name)) {
                req.setUrl(url);
                req.setMethod(method);
                req.setHeaders(headers);
                req.setFormData(formData);
                req.setUrlEncoded(urlencoded);
                req.setQuery(query);
                req.setJson(json);
                req.setBinaryFileAddress(binaryFileAddress);
                //then save into file :
                FileUtils.serializeGUIRequest(req.getGuiRequest());
            }
        }
    }



}
