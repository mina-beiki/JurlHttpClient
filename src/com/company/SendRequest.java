package com.company;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Extends swing worker class and Controls sending a request with using RequestHandler
 * to generate and send a specific request with while using GUI interface .
 *
 * @author Mina Beiki
 */
public class SendRequest extends SwingWorker<ArrayList<String>, Object> {

    private JTable responseHeaderTable;
    private JTextArea rawArea, jsonArea;
    private String[] split;
    private JPanel preview;
    private RequestRepository requestRepository;
    private ArrayList<String> response;
    private Map<String, List<String>> headersMap;
    private JLabel codeLabel, statusLabel, timeLabel, imageLabel, resData;
    private int code, data;
    private String status, imageAddress;
    private long duration;
    private boolean patch;

    /**
     * Generates an instance of SendRequest by getting the needed gui parts of interface to be
     * completed after sending the request (with the response and its properties ) .
     *
     * @param split               String[] used as input for sending a request split by spacebars
     * @param responseHeaderTable JTable for headers
     * @param rawArea             text area for printing raw response
     * @param preview             preview JPanel used to show image
     * @param imageLabel          JLabel used to show an image
     * @param jsonArea            JTextArea to show json response
     * @param requestRepository   RequestRepository instance which controls over a request and its parts
     * @param code                response code
     * @param status              response status message
     * @param time                response time
     * @param resData             response content length
     * @param patch               boolean true if the method is patch
     */
    public SendRequest(String[] split, JTable responseHeaderTable, JTextArea rawArea,
                       JPanel preview, JLabel imageLabel, JTextArea jsonArea, RequestRepository requestRepository,
                       JLabel code, JLabel status, JLabel time, JLabel resData, boolean patch) {
        imageAddress = "";
        this.responseHeaderTable = responseHeaderTable;
        this.rawArea = rawArea;
        this.preview = preview;
        this.imageLabel = imageLabel;
        this.jsonArea = jsonArea;
        this.requestRepository = requestRepository;
        this.split = split;
        this.resData = resData;
        this.patch = patch;
        response = new ArrayList<>();
        codeLabel = code;
        timeLabel = time;
        statusLabel = status;

    }

    /**
     * Does the task for sending a request
     *
     * @return getResponse() output which is an arrayList of strings , which also is the response and other parts
     * that are needed
     */

    @Override
    protected ArrayList<String> doInBackground() {
        return getResponse();
    }

    /**
     * After sending a request , full fil the areas in gui interface with the
     * parts that are received ; such as raw response text area , headers table and others .
     */
    @Override
    protected void done() {
        //showing downloaded image :
        if (!(imageAddress.equals("empty"))) {
            imageAddress = "/Users/a/IdeaProjects/HTTPclient/downloads/" + imageAddress;
            imageLabel.setIcon(new ImageIcon(imageAddress));
        }
        try {
                //check if it is json :
                if (checkJson()) {
                    jsonArea.setText(get().get(0));
                }


            //printing raw response :
            rawArea.setText(get().get(0));
            //printing properties :
            codeLabel.setText(get().get(2) + "");
            statusLabel.setText(get().get(3));
            timeLabel.setText(get().get(4) + "");
            if (response.size() > 4) {
                resData.setText(get().get(5));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //printing headers :
        ArrayList<String> tableContent = new ArrayList<>();
        for (Map.Entry<String, List<String>> resHeader : headersMap.entrySet()) {
            if (resHeader.getKey() == null) {
                tableContent.add("");
            } else {
                tableContent.add(resHeader.getKey());
            }
            tableContent.add(resHeader.getValue().toString());
        }
        List<String> contentLength = headersMap.get("Content-Length");
        if (contentLength == null) {
            tableContent.add("Content-Length");
            tableContent.add("Content-Length is not present among headers .");
        } else {
            for (String cnt : contentLength) {
                tableContent.add("Content-Length");
                tableContent.add(cnt);
            }
        }
        int ctr = 0;
        try {
            for (int i = 0; i < responseHeaderTable.getRowCount(); i++) {
                for (int j = 0; j < responseHeaderTable.getColumnCount(); j++) {
                    responseHeaderTable.setValueAt(tableContent.get(ctr), i, j);
                    ctr++;
                }
            }
        } catch (IndexOutOfBoundsException ex) {
            //do nothing , because we are using gui
        }
    }

    /**
     * Does the task which is sending a request by making a request handler and giving the
     * needed input to it . The output is saved in ArrayList of strings and in order are :
     * raw response , headers string , code , status and duration time and at last the data .
     *
     * @return ArrayList of String which contains the output and necessary properties
     */
    public ArrayList<String> getResponse() {
        RequestHandler requestHandler = requestRepository.generateRequestHandler(split);
        requestHandler.doRequest();
        if (patch) {
            String patchResponse = requestHandler.getPatchResponse();
            response.add(patchResponse);
        }

        else  {
            String rawResponse = requestHandler.getRawResponse();
            response.add(rawResponse);
        }

        String headersStr = requestHandler.getResponseHeadersMap().toString();
        //put into map :
        headersMap = requestHandler.getResponseHeadersMap();
        //save properties :
        code = requestHandler.getResCode();
        status = requestHandler.getResStatus();
        duration = requestHandler.getDuration();
        data = requestHandler.getResDataAmount();

        response.add(headersStr);
        response.add(code + "");
        response.add(status);
        response.add(duration + "");
        response.add(data + "");

        imageAddress = requestHandler.getDownloadedImage();
        return response;
    }

    /**
     * Checks if the response type is json or not .
     *
     * @return true , if it is json and false , if it isn't .
     */
    public boolean checkJson() {
        //check if it is json :
        List<String> contentType = headersMap.get("Content-Type");
        if (contentType == null) {
            return false;
        }
        String type = contentType.get(0);
        return type.equals("application/json; charset=utf-8");
    }

}
