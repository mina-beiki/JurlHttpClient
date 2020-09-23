package com.company;

import java.net.URL;
import java.util.ArrayList;

/**
 * This class is for generating an object for request used in serializing and deserializing .
 * It is consisted of the essential fields for single request
 * and implements java.io.Serializable .
 *
 * @author Mina Beiki
 */
public class Request implements java.io.Serializable  {
    private URL url;
    private String method , messageBodyStr ;
    private ArrayList<ArrayList<String>> headers;
    private boolean hasBody ;

    /**
     * Makes a new request by using the given parameters .
     * @param url URL , url of the request
     * @param method String , method of the request
     * @param messageBodyStr String , message body
     * @param headers ArrayList<ArrayList<String>> , 2D arrayList of all headers
     * @param hasBody boolean , true if it has a body and false it doesn't have a body
     */
    public Request ( URL url , String method , String messageBodyStr ,ArrayList<ArrayList<String>> headers , boolean hasBody){
        this.url = url ;
        this.method = method ;
        this.messageBodyStr = messageBodyStr ;
        this.headers = headers ;
        this.hasBody = hasBody ;
    }

    /**
     * Gets the data and all fields of request object in string .
     * @return String , data and content of request
     */
    @Override
    public String toString() {
        return "Request{" +
                "url=" + url +
                ", method='" + method + '\'' +
                ", messageBodyStr='" + messageBodyStr + '\'' +
                ", headers=" + headers +
                ", hasBody=" + hasBody +
                '}';
    }

    /**
     * Gets the request's URL  .
     * @return URL
     */
    public URL getUrl() {
        return url;
    }
    /**
     * Gets the request's message body checker .
     * @return boolean , true if it has a body and false if it doesn't have a body .
     */
    public boolean getHasBody() {
        return hasBody;
    }
    /**
     * Gets the request's method  .
     * @return String
     */
    public String getMethod() {
        return method;
    }
    /**
     * Gets the request's messageBodyStr  .
     * @return String
     */
    public String getMessageBodyStr() {
        return messageBodyStr;
    }
    /**
     * Gets the request's headers  .
     * @return ArrayList<ArrayList<String>> , headers
     */
    public ArrayList<ArrayList<String>> getHeaders() {
        return headers;
    }
}
