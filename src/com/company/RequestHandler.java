package com.company;


import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Makes a new request and sends it with the given method , body , and other
 * settings which has been set up by user ; So it handles a request and
 * has control over it .
 * It also has the ability to print the response and headers ,
 * upload binary files , saving requests and response bodies with the
 * help of FileUtils class .
 *
 * @author Mina Beiki
 * @version 2020.06.04
 */
public class RequestHandler {

    private URL url;
    private String urlStr , method, messageBodyStr, resStatus, fileName, jsonStr, binaryAddress , rawResponse
            , patchResponse ,dateStr;
    private HashMap<String, String> formData;
    private ArrayList<ArrayList<String>> headers, jsonBody;
    private Map<String, List<String>> responseHeaders ;
    private boolean followRedirectSetup = false, printHeaders = false, hasBody = false,
            toSaveResBody = false, toSaveRequest = false, inputContainsURL = false,
            hasJsonBody = false, binaryFile = false, formUrlEnc = false, patchMethod = false , downloadedImage = false ;
    private HttpURLConnection connection;
    private int resCode, resDataAmount, resTime;
    private long duration;
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";

    /**
     * Makes a new request by getting the input line array of strings .
     * Checks all the possible errors that is not observed by user and prints
     * error (not exceptions , because we want the program to be continued . Expect
     * in emergency errors that the program should be stopped . )
     * After making a request wth checking some special circumstances , sends the
     * request and prints the response .
     *
     * @param split String[] split , Input split by space bars
     * @throws MalformedURLException Handled in Main class .
     */
    public RequestHandler(String[] split) throws MalformedURLException {
        String headersStr = "";
        messageBodyStr = null;
        method = "GET"; //default
        for (int i = 0; i < split.length; i++) {

            if (split[i].equals("-M") || split[i].equals("--method")) {
                //check if it is not the last element :
                if (!(i == split.length - 1)) {
                    method = split[i + 1];
                }
                if (method.equals("PATCH")) {
                    patchMethod = true;
                }
            }
            if (split[i].equals("-H") || split[i].equals("--headers")) {
                //check if it is not the last element :
                if (!(i == split.length - 1)) {
                    headersStr = split[i + 1];
                    headers = new ArrayList<>();
                    setHeaders(headersStr);
                }
            }
            if (split[i].equals("-d") || split[i].equals("--data")) {
                //check if it is not the last element :
                if (!(i == split.length - 1)) {
                    messageBodyStr = split[i + 1];
                    //bodyFormData = new HashMap<>();
                    //setBodyFormData(bodyFormDataStr);
                    messageBodyStr = messageBodyStr.replace("\"", "");
                    hasBody = true;
                }
            }
            if (split[i].equals("--url") || split[i].equals("--URL")) {
                //check if it is not the last element :
                if (!(i == split.length - 1)) {
                    urlStr = split[i + 1];
                    if (!(urlStr.contains("http://"))) {
                        if (!(split[i + 1].contains("http://")) || (split[i + 1].contains("https://"))) {
                            System.out.println(ANSI_RED + "WARNING : Please enter a protocol for the chosen URL . " + ANSI_RESET);
                        } else {
                            urlStr = split[i + 1];
                        }
                    }
                }
                url = new URL(urlStr);
                inputContainsURL = true;
            }
            //saving request body into a file  :
            if (split[i].equals("--output") || split[i].equals("-O")) {
                //check if it is not the last element :
                if (!(i == split.length - 1)) {
                    //if the user has given file name :
                    fileName = split[i + 1];
                    if (!(split[i + 1].contains("."))) {
                        fileName = null;
                    }
                } else {
                    fileName = null;
                }
                toSaveResBody = true;
            }
            if (split[i].equals("-f")) {
                //set follow redirect true :
                followRedirectSetup = true;
            }
            if (split[i].equals("-i")) {
                //response should contain headers :
                printHeaders = true;
            }
            if (split[i].equals("-S") || split[i].equals("--save")) {
                //request should be saved and serialized  :
                toSaveRequest = true;
            }
            if (split[i].equals("list")) {
                //all saved requests should be deserialized and the data should be shown :
                FileUtils.deserializeAllRequests();
            }
            if (split[i].equals("fire")) {
                //save the index of requests we want to send :
                ArrayList<Integer> indexes = new ArrayList<>();
                int num = split.length - 2;
                for (int j = 1; j < num + 1; j++) {
                    int index = Integer.parseInt(split[i + j]);
                    indexes.add(index);
                }
                FileUtils.fireRequests(indexes);
            }
            if (split[i].equals("-j") || split[i].equals("--json")) {
                hasJsonBody = true;
                if (!(i == split.length - 1)) {
                    jsonStr = split[i + 1];
                    //set json body into 2d array list :
                    //setJsonBody(jsonStr);
                } else {
                    System.out.println(ANSI_RED + "WARNING : You should enter json data after --json / -j . " + ANSI_RESET);
                }

            }
            if (split[i].equals("--upload")) {
                binaryFile = true;
                if (!(i == split.length - 1)) {
                    binaryAddress = split[i + 1];
                } else
                    System.out.println(ANSI_RED + "WARNING : You should enter file's absolute path after --upload . " + ANSI_RESET);
            }
            if (split[i].equals("--urlencoded")) {
                formUrlEnc = true;
                if (!(i == split.length - 1)) {
                    messageBodyStr = split[i + 1];
                } else
                    System.out.println(ANSI_RED + "WARNING : You should enter message body after --urlencoded . " + ANSI_RESET);
            }
        }
    }

    /**
     * Handles a request and does all that should be done in send a request which is sending
     * a request and printing the response . Patch method is checked separately because it is handled
     * with different library .
     */
    public void doRequest (){
        //send request and print the response :
        //if user is sending a request : (because we have possibilities like "jurl list" that user is not sending any requests )
        if (inputContainsURL) {
            //check if the method is patch or not because for patch we use a different way .
            long startTime = System.nanoTime();
            if (patchMethod) {
                sendPatch(urlStr ,messageBodyStr);
            } else {
                send();
                if (followRedirectSetup) {
                    if (toFollowRedirect()) {
                        followRedirect();
                    }
                }
                if (method.equals("GET")) {
                    if (checkPicType()) {
                        downloadPic();
                    }
                }
                printResponse();
                long endTime = System.nanoTime();
                duration = ((endTime - startTime)/1000000);
            }
        }
    }

    /**
     * Acts like doRequest method but it is only used when "jurl fire" is entered and to
     * fire requests .
     */
    public void fireRequest (){
            if (patchMethod) {
                sendPatch(urlStr ,messageBodyStr);
            } else {
                send();
                if (followRedirectSetup) {
                    if (toFollowRedirect()) {
                        followRedirect();
                    }
                }
                if (method.equals("GET")) {
                    if (checkPicType()) {
                        downloadPic();
                    }
                }
                printResponse();
            }
    }


    //for "jurl fire" :
    //because we want to send a request again .

    /**
     * This constructor that uses params is used when "jurl fire" is entered and we want
     * to send some requests again .
     *
     * @param url            URL , url of request
     * @param method         String , method of request
     * @param messageBodyStr String , request's body
     * @param headers        ArrayList<ArrayList<String>> , all headers of request
     * @param hasBody        boolean , true if it has a body and false , if it hasn't
     */
    public RequestHandler(URL url, String method, String messageBodyStr, ArrayList<ArrayList<String>> headers, boolean hasBody) {
        this.url = url;
        this.method = method;
        this.messageBodyStr = messageBodyStr;
        this.headers = headers;
        this.hasBody = hasBody;
    }

    /**
     * Gets a string of all headers and set al of them into
     * a 2d dimensional arrayList of strings .
     *
     * @param headersStr String , string of all headers
     */
    //The reason ive chosen 2d arrayList instead of hashMap is that maybe the user wants to add 2 headers
    //with same keys but with different values .
    public void setHeaders(String headersStr) {
        String[] headersSplit;
        //removes quotations :
        headersStr = headersStr.replace("\"", "");
        //split key value pairs :
        headersSplit = headersStr.split(";");
        for (String s : headersSplit) {
            int index = s.indexOf(":");
            //saving header into 2d array list :
            //first element in each internal string array list is key and second one is value .
            String header = "";
            String value = "";
            for (int i = 0; i < index; i++) {
                char newChar = s.charAt(i);
                header = header + newChar;
            }
            for (int i = index + 1; i < s.length(); i++) {
                char newChar = s.charAt(i);
                value = value + newChar;
            }
            ArrayList<String> in = new ArrayList<>();
            in.add(header);
            in.add(value);
            headers.add(in);
        }
    }


    /**
     * Makes a new connection , opens it and sends the request .
     * Check all the conditions and call the specific method for each of them , also
     * sets the method and headers .
     */
    public void send() {
        //open connection :
        try {
            connection = (HttpURLConnection) url.openConnection();

        } catch (IOException e) {
            System.out.println("Couldn't open connection . ");
            e.printStackTrace();
        }
        //set request method :
        try {
            connection.setRequestMethod(method);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        if (headers != null) {
            //set headers :
            for (ArrayList<String> array : headers) {
                connection.setRequestProperty(array.get(0), array.get(1));
            }
        }
        if (binaryFile) {
            //upload binary file
            uploadBinaryFile();
        }
        if (hasBody) {
            multiPart();
            //writeBody();
        }
        if (hasJsonBody) {
            //writeJsonBody
            writeJsonBody(jsonStr);
        }
        if (toSaveRequest) {
            FileUtils.serializeRequest(url, method, messageBodyStr, headers, hasBody);
        }
        if (formUrlEnc) {
            formUrlEncoded();
        }

    }

    /**
     * Sends patch method , it is implemented in a different method because the library used for this method
     * is different .
     */
    public void sendPatch(String urlString, String messageBodyStr) {
        HttpClient httpclient = HttpClientBuilder.create().build();

        // Prepare a request object
        HttpPatch req = new HttpPatch(urlString);

        //messageBodyStr = "\""+messageBodyStr+"\"";
        messageBodyStr=messageBodyStr.replace("\"","");
        req.setHeader("Content-Type", "application/x-www-form-urlencoded");
        req.setHeader("charset", "utf-8");
        StringEntity data = new StringEntity(messageBodyStr, "UTF-8");
        req.setEntity(data);
        printPatchResponse(httpclient , req);
    }

    /**
     * Prints the response only for patch method because is implemented with different library .
     * @param httpclient HttpClient
     * @param req HttpUriRequest
     */
    public void printPatchResponse(HttpClient httpclient , HttpUriRequest req){
        try {
            HttpResponse response = httpclient.execute(req);
            //print headers if -i is entered :
            if(printHeaders) {
                System.out.println(response.getStatusLine());
                for (Header hdr : response.getAllHeaders()) {
                    System.out.println(hdr);
                }
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())))) {
                // Read in all of the post results into a String.
                String output = "";
                Boolean keepGoing = true;
                while (keepGoing) {
                    String currentLine = br.readLine();

                    if (currentLine == null) {
                        keepGoing = false;
                    } else {
                        output += currentLine;
                    }
                }

                System.out.println( output);
                patchResponse = output ;
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the response of the request , and if -i is entered prints the headers .
     */
    public void printResponse() {
        if (printHeaders) {
            printResponseHeaders();
        }
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader in = new BufferedReader(inputStreamReader);
            StringBuffer content = new StringBuffer();
            String input;
            while ((input = in.readLine()) != null) {
                content.append(input);
            }
            System.out.println(content);
            rawResponse = content.toString() ;
            in.close();
            inputStreamReader.close();
            if (toSaveResBody) {
                FileUtils.saveResponseToFile(content.toString(), fileName);
            }
        } catch (IOException e) {
            //do nothing
        }


        //disconnect :
        connection.disconnect();

    }

    /**
     * For downloading a picture checks the content-Type , Checks if it is png/image
     * to download the image .
     *
     * @return boolean , true if the type is png/image , false if it isn't .
     */
    public boolean checkPicType() {
        Map<String, List<String>> map = connection.getHeaderFields();
        List<String> contentType = map.get("Content-Type");
        if (contentType == null) {
            return false;
        }
        String type = contentType.get(0);
        return type.equals("image/png");
    }

    /**
     * Prints all the headers (when -i is entered by the user ) .
     */
    public void printResponseHeaders() {
        Map<String, List<String>> map = connection.getHeaderFields();
        for (Map.Entry<String, List<String>> resHeader : map.entrySet()) {
            if (resHeader.getKey() == null) {
                System.out.println(resHeader.getValue());
            } else {
                System.out.println(resHeader.getKey() + " : " + resHeader.getValue());
            }
        }
        List<String> contentLength = map.get("Content-Length");
        resDataAmount = Integer.parseInt(contentLength.get(0));
        if (contentLength == null) {
            System.out.println("Content-Length is not present among headers .");
        } else {
            for (String hdr : contentLength) {
                System.out.println("Content-Length: " + hdr);
            }
        }
    }

    /**
     * Gets the response headers in Map format .
     * @return headers Map
     */
    public Map<String, List<String>> getResponseHeadersMap(){
        return connection.getHeaderFields();
    }
    //phase 3 :

    /**
     * Gets the status message of response .
     * @return String status message
     */
    public String getResStatus(){
        try {
            resStatus = connection.getResponseMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resStatus ;
    }

    /**
     * Gets the response code .
     * @return int response code
     */
    public  int getResCode(){
        try {
            resCode = connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resCode ;
    }

    /**
     * Sends messaged body in urlencoded format (When --urlencoded is entered by user ) .
     */
    public void formUrlEncoded() {
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("charset", "utf-8");
        connection.setDoOutput(true);
        String tempMsg = messageBodyStr;
        tempMsg = tempMsg.replace("\"", "");
        try {
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(tempMsg);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Sends message body in json format (When --json or -j is entered by user ) .
     *
     * @param jsonStr String , message body string entered by user
     */
    public void writeJsonBody(String jsonStr) {
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        try {
            OutputStream out = connection.getOutputStream();
            byte[] json = jsonStr.getBytes(StandardCharsets.UTF_8);
            out.write(json, 0, json.length);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends message body in multipart/form-data format . (When -d is entered it performs this method . )
     */
    public void multiPart() {
        try {
            String boundary = System.currentTimeMillis() + "";
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            BufferedOutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
            setFormDataMap(messageBodyStr);
            bufferOutFormData(formData, boundary, outputStream);

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Used for multipart/form-data , Sends both message and other fields in the body .
     *
     * @param body                 HashMap<String, String> , hashMap of body message name and values
     * @param boundary             String , boundary for multipart
     * @param bufferedOutputStream BufferedOutputStream
     * @throws IOException IOException
     */
    //Used this piece of code from TA group docs ( Edited )  :
    public static void bufferOutFormData(HashMap<String, String> body, String boundary, BufferedOutputStream bufferedOutputStream) throws IOException {
        for (String key : body.keySet()) {
            bufferedOutputStream.write(("--" + boundary + "\r\n").getBytes());
            if (key.contains("file")) {
                bufferedOutputStream.write(("Content-Disposition: form-data; filename=\"" + (new File(body.get(key))).getName() + "\"\r\nContent-Type: Auto\r\n\r\n").getBytes());
                try {
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(body.get(key))));
                    while (bis.available() > 0) {
                        bufferedOutputStream.write((byte) bis.read());
                    }
                    bufferedOutputStream.write("\r\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                bufferedOutputStream.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n").getBytes());
                bufferedOutputStream.write((body.get(key) + "\r\n").getBytes());
            }
        }
        bufferedOutputStream.write(("--" + boundary + "--\r\n").getBytes());
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }

    /**
     * When errors in range of 300 are happened , this method helps us to check
     * if we have to follow redirect or now . (This is checked when -f
     * is entered by user )
     *
     * @return boolean , true if follow redirect should be done and false , if tis the opposite .
     */
    public boolean toFollowRedirect() {
        boolean fr = false;
        try {
            resCode = connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //check if it is in range of 300  :
        if (resCode != HttpURLConnection.HTTP_OK) {
            if (resCode == HttpURLConnection.HTTP_MOVED_TEMP || resCode == HttpURLConnection.HTTP_MOVED_PERM
                    || resCode == HttpURLConnection.HTTP_SEE_OTHER) {
                fr = true;
            }
        }
        return fr;
    }

    /**
     * Does the redirecting and gets the request to the address which gives code 200 (OK) .
     */

    public void followRedirect() {
        try {
            //Make the new connection :
            String newURL = connection.getHeaderField("Location");
            //open the new connection :
            connection = (HttpURLConnection) new URL(newURL).openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * For doing a multipart/form-data body message we need to set the string of body message into
     * a hashMap which consists o pairs (name and values ) .
     *
     * @param messageBodyStr String , message body string
     */
    public void setFormDataMap(String messageBodyStr) {
        formData = new HashMap<>();
        String[] formSplit;
        String tempMsg = messageBodyStr;
        //removes quotations :
        tempMsg = tempMsg.replace("\"", "");
        //split key value pairs :
        formSplit = tempMsg.split("&");
        for (String s : formSplit) {
            int index = s.indexOf("=");
            //saving header into hash map  :
            //first element in each internal string array list is key and second one is value .
            String name = "";
            String value = "";
            for (int i = 0; i < index; i++) {
                char newChar = s.charAt(i);
                name = name + newChar;
            }
            for (int i = index + 1; i < s.length(); i++) {
                char newChar = s.charAt(i);
                value = value + newChar;
            }
            formData.put(name, value);
        }
    }

    /**
     * Uploads a binary file by having the absolute path of it which is given by user
     * and also is done when --upload is entered .
     */
    public void uploadBinaryFile() {
        try {
            File binaryFile = new File(binaryAddress);
            //connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/octet-stream");
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(connection.getOutputStream());
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(binaryFile));
            while (bis.available() > 0) {
                bufferedOutputStream.write((byte) bis.read());
            }
            bufferedOutputStream.flush();
            bufferedOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Downloads an image from an specific address and saves it into a file
     * in downloads directory .
     */
    public void downloadPic() {
        Calendar cal = Calendar.getInstance();
        Format formatter = new SimpleDateFormat("dd-MMMM-yyyy hh:mm:s");
        dateStr = formatter.format(cal.getTime());
        File file = new File("./downloads/" + dateStr + ".png");
        try {
            BufferedImage pic = ImageIO.read(url);
            ImageIO.write(pic, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        downloadedImage = true ;

    }

    /**
     * Gets the address of the downloaded image
     * @return string address of downloaded image
     */
    public String getDownloadedImage (){
        if(downloadedImage){
            return dateStr+".png";
        }
        return "empty";
    }

    /**
     * Gets the raw response
     * @return String raw response
     */
    public String getRawResponse(){
        return rawResponse;
    }

    /**
     * Gets patch response ( because it is implemented with different library )
     * @return Patch response in String
     */
    public String getPatchResponse() {
        return patchResponse;
    }

    /**
     * Gets time duration od response
     * @return long , duration
     */
    public long getDuration(){
        return duration ;
    }

    /**
     * Gets the data amount of response
     * @return int , data amount
     */
    public int getResDataAmount (){
        return resDataAmount;
    }
}
