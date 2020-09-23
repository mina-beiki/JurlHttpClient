package com.company;

import java.io.*;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * This class controls all of the methods that are associated with files .
 * (Writing into a file , reading from it , serializing and ...)
 * It is used in RequestHandler and RequestRepository classes to save response bodies into file ,
 * to save requests , and also to fire requests and also to save a request in
 * GUI interface to be shown later .
 */
public class FileUtils {
    //arrayList of all saved requests :
    private static ArrayList<Request> requests = new ArrayList<>();
    private static HashMap<String, String> headers = new HashMap<>();
    private static HashMap<String, String> formData = new HashMap<>();
    private static HashMap<String, String> urlencoded = new HashMap<>();
    private static HashMap<String, String> query = new HashMap<>();
    private static String url, method, json, binaryAddress;


    /**
     * Saves the response file into a file (specified with the given format )
     * and if the format is not given , it is saved into a html file , in responses
     * directory .
     *
     * @param content String , response body
     * @param name    String , name of the file to be saved in
     */
    public static void saveResponseToFile(String content, String name) {
        Date date = new Date();
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = formatter.format(date);
        //file name :
        //if user hasn't given a name :
        String fileName;
        if (name == null) {
            fileName = " output_[" + dateStr + "].html";
        } else {
            fileName = name;
        }
        BufferedOutputStream out = null;
        File file = new File("./responses/" + fileName);

        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            byte bytes[] = content.getBytes();
            out.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    /**
     * For saving a request we need to serialize it and afterwards deserialize is needed to
     * read it and show it in "jurl list" . This method makes a request object with the help of Request Class ,
     * and serializes it .
     * A request is saved in requests directory .
     *
     * @param url            URL , url of the request
     * @param method         String , method of the request
     * @param messageBodyStr String , message body
     * @param headers        ArrayList<ArrayList<String>> , 2D arrayList of all headers
     * @param hasBody        boolean , true if it has a body and false it doesn't have a body
     */
    public static void serializeRequest(URL url, String method, String messageBodyStr, ArrayList<ArrayList<String>> headers, boolean hasBody) {
        Calendar cal = Calendar.getInstance();
        Format formatter = new SimpleDateFormat("dd-MMMM-yyyy hh:mm:s");
        String dateStr = formatter.format(cal.getTime());
        File file = new File("./requests/" + dateStr + ".txt");
        FileOutputStream outputStream = null;
        ObjectOutputStream out = null;
        try {
            outputStream = new FileOutputStream(file);
            out = new ObjectOutputStream(outputStream);
            out.writeObject(new Request(url, method, messageBodyStr, headers, hasBody));
            outputStream.close();
            out.close();
            //System.out.println("Serialized.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deserializes all saved requests and shows them in a list (when "jurl list"
     * is entered by user .
     */
    public static void deserializeAllRequests() {
        File path = new File("./requests/");
        File[] listOfFiles = path.listFiles();
        int ctr = 1;
        for (File file : listOfFiles) {
            String content = deserializeRequest(file);
            System.out.println(ctr + " . " + content);
            ctr++;
        }
    }

    /**
     * Sends the specified requests (by user) again , with the help of indexes in the list .
     * So makes a ne RequestHandler and sends it and also prints the response .
     *
     * @param indexes ArrayList<Integer> , gets an arrayList of all indexes which need to be sent again .
     */
    public static void fireRequests(ArrayList<Integer> indexes) {
        for (Integer index : indexes) {
            //by making a new request it is also sent , because the methods are implemented in the constructor .
            RequestHandler req = new RequestHandler(requests.get(index - 1).getUrl(), requests.get(index - 1).getMethod(), requests.get(index - 1).getMessageBodyStr(),
                    requests.get(index - 1).getHeaders(), requests.get(index - 1).getHasBody());
            req.fireRequest();
        }
    }

    /**
     * Deserialize a specified request and reads it from its file , and gives back the content
     * which is shown in "jurl list" in a string .
     *
     * @param file File , request file
     * @return String , data and content to be shown in "jurl list"
     */
    public static String deserializeRequest(File file) {
        String content = "";
        ArrayList<ArrayList<String>> headers;
        Request request;
        FileInputStream inputStream = null;
        ObjectInputStream in = null;
        try {
            inputStream = new FileInputStream(file);
            in = new ObjectInputStream(inputStream);
            request = (Request) in.readObject();
            requests.add(request);
            content = " url : " + request.getUrl().toString() + " | method : " + request.getMethod() + " | headers :";
            headers = request.getHeaders();
            //adding headers to content :
            if (headers != null) {
                for (ArrayList<String> array : headers) {
                    content = content.concat(" " + array.get(0) + ":" + array.get(1));
                }
            } else
                content = content.concat(" null");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return content;

    }

    /**
     * Serializes a GUIRequest which is a request in GUI interface and saves the
     * data in FileUtils fields .
     * @param req GUIRequest to be serialized and saved into file
     */
    public static void serializeGUIRequest(GUIRequest req) {
        String fileName = req.getName();
        File file = new File("./reqRepositories/" + fileName + ".txt");
        FileOutputStream outputStream = null;
        ObjectOutputStream out = null;
        try {
            outputStream = new FileOutputStream(file);
            out = new ObjectOutputStream(outputStream);
            out.writeObject(req);
            outputStream.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deserializes a GUIRequest which was serialized before and saved into reqRepositories
     * directory . After deserializing the request saves the data into FileUtils fields .
     * @param file File which is going to be deserialized
     */
    public static void deserializeGUIRequest(File file) {
        GUIRequest req;
        FileInputStream inputStream = null;
        ObjectInputStream in = null;
        try {
            inputStream = new FileInputStream(file);
            in = new ObjectInputStream(inputStream);
            req = (GUIRequest) in.readObject();
            headers = req.getHeaders();
            formData = req.getFormData();
            urlencoded = req.getUrlEncoded();
            query = req.getQuery();
            url = req.getUrl();
            method = req.getMethod();
            binaryAddress = req.getBinaryFileAddress();
            json = req.getJson();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets all String data which has been deserialized .
     * @return String ArrayList of saved data which their types are String .
     */
    public static ArrayList<String> getSavedDataStrings() {
        ArrayList<String> data = new ArrayList<>();
        data.add(FileUtils.getUrl()); //url
        data.add(FileUtils.getMethod()); //method
        data.add(FileUtils.getJson()); //json
        data.add(FileUtils.getBinaryAddress()); //binaryFile
        return data;
    }

    /**
     * Gets headers
     * @return
     */
    public static HashMap<String, String> getHeaders() {
        return headers;
    }

    /**
     * Gets form data
     * @return
     */
    public static HashMap<String, String> getFormData() {
        return formData;
    }

    /**
     * Gets url encoded
     * @return
     */
    public static HashMap<String, String> getUrlencoded() {
        return urlencoded;
    }

    /**
     * Gets query
     * @return
     */
    public static HashMap<String, String> getQuery() {
        return query;
    }

    /**
     * Gets url
     * @return
     */
    public static String getUrl() {
        return url;
    }

    /**
     * Gets method
     * @return
     */
    public static String getMethod() {
        return method;
    }

    /**
     * Gets json
     * @return
     */
    public static String getJson() {
        return json;
    }

    /**
     * Gets binary address
     * @return
     */
    public static String getBinaryAddress() {
        return binaryAddress;
    }


}
