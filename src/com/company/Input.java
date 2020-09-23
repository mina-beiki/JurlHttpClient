package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Controls the input and sends it to RequestHandler class to make a new request .
 * Overall , this class gets one line of input and changes it into an array of strings
 * to check if there are any errors and to check every command user have made .
 * Also handles when you want specific input in gui interface by getting a arrayList of
 * panels and gives a hashMap or string which is the input needed .
 *
 * @author Mina Beiki
 * @version 2020.06.04
 */
public class Input {
    private static String[] split;
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";

    /**
     * Static method which gets the input , without the need for making the new instance of
     * this class .
     * Some initial and essential errors in input are checked here and others are checked
     * in RequestHandler class .
     */
    public static void getInput() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        split = input.split(" ");
        //checking exceptions and errors in input :
        if (!(split[0].equals("jurl"))) {
            System.out.println(ANSI_RED + "WARNING : First input word should be jurl . " + ANSI_RESET);
        }
        if (split[split.length - 1].equals("-M") || split[split.length - 1].equals("--method")) {
            System.out.println(ANSI_RED + "WARNING : You should specify method type after entering -M or --method . " + ANSI_RESET);
        }
        int temp = 0, flag = 0;
        for (int i = 0; i < split.length; i++) {
            if (split[i].equals("-M") || split[i].equals("--method")) {
                temp = i;
                flag = 1;
            }
        }
        if (flag == 1) {
            if (!(temp == split.length - 1)) {
                if (!(split[temp + 1].equals("GET") || split[temp + 1].equals("POST") ||
                        split[temp + 1].equals("DELETE") || split[temp + 1].equals("PATCH") ||
                        split[temp + 1].equals("HEAD") || split[temp + 1].equals("PUT") || split[temp + 1].equals("TRACE")
                        || split[temp + 1].equals("OPTIONS"))) {
                    System.out.println(ANSI_RED + "WARNING : You should specify method type after entering -M or --method . " + ANSI_RESET);
                    System.out.println(ANSI_RED + "Examples : GET , PUT , PATCH , DELETE , ... . " + ANSI_RESET);

                }
            }
        }
        //--help :
        for (String s : split) {
            if (s.equals("--help")) {
                printHelpMenu();
            }
        }
        // TODO check all the possibilities that make an wrong input
    }

    /**
     * Gets the split version of input (by space bars) .
     *
     * @return String[] , input array of strings
     */
    public static String[] getSplit() {
        return split;
    }

    /**
     * Checks if the user has chosen to exit the program or not.
     * Notice : by entering "jurl exit" , program will be stopped .
     *
     * @return boolean , true if program wants to exit and false if it's the opposite .
     */
    public static boolean checkExit() {
        if(split[0].equals("jurl") && split.length==1){
            System.out.println(ANSI_RED + "WARNING : You should enter more data to send a request , See --help . "+ANSI_RESET);
        }
        else if (split[1].equals("exit"))
            //exit :
            return true;
        //continue sending requests :
        return false;
    }

    /**
     * Prints the help menu (short introductions of all commands used in program )
     * by entering --help .
     */
    public static void printHelpMenu() {
        System.out.println("Notice : All commands start with \"jurl\" .");
        System.out.println("--url / --URL  : Enter the url (rather with protocol ) after it . ");
        System.out.println("--method / -M : Enter the chosen method after it . (Examples : GET , POST , PUT , DELETE , PATCH .) Default is GET .");
        System.out.println("--headers / -H : Enter headers after it .(Example : \"key1:value1;key2:key2:value2\") ");
        System.out.println("--data / -d : Enter multipart/form-data after it .(Example :\"firstName=mina&lastName=beiki\"");
        System.out.println("--urlencoded : Sends data in urlencoded format in body message . ");
        System.out.println("--output / -O : Saves the response body .");
        System.out.println("-f : Turns on the follow redirect . ");
        System.out.println("--save / -S : Saves the request .");
        System.out.println("list : prints the list of saved requests . ");
        System.out.println("fire : sends one or more requests from list . (Example : jurl fire 1 2 )");
        System.out.println("-i : Shows the headers in the response . ");
        System.out.println("--json / -j : Sends json message body . (Example : \"{firstName:mina,lastName:beiki}\" ");
        System.out.println("--upload : Gets an absolute path after it and uploads a file to the chosen URL . ");
        System.out.println("exit : exits the program ");

    }

    /**
     * Gets an arrayList of BarPanels and gives the inputs that are entered by user in them.
     * It should be noticed that only the noticed which have their status check box selected
     * will be contained in the output .
     * @param barList ArrayList of BarPanels
     * @param between character which will be between to strings in the output
     * @param separator character which will separate pairs in the output
     * @return String
     */
    //used in phase 2 :
    /*When this generators return "" it means it doesn't have anything (empty string , not "") . */
    //used for bodyMessage and headers  :
    public static String generatePairsInput(ArrayList<BarPanel> barList , char between , char separator) {
        ArrayList<ArrayList<String>> temp = new ArrayList<>();
        String content = "\"";
        int ctr = 0 ;
        for (BarPanel barPanel : barList) {
            if(barPanel.getStatus().isSelected()) {
                String name = barPanel.getNamee().getText();
                String value = barPanel.getValue().getText();
                //if they are not empty :
                if (!(name.equals("")) && !(value.equals(""))) {
                    ArrayList<String> array = new ArrayList<>();
                    array.add(name);
                    array.add(value);
                    temp.add(array);
                }
            }
        }
        for(ArrayList<String> array : temp){
            content = content.concat(array.get(0)+between+array.get(1));
            ctr ++ ;
            if(ctr != temp.size()){
                content = content+separator;
            }
        }
        content = content.concat("\"");
        if(content.equals("\"\""))
            content="";
        return content;
    }

    /**
     * Gets an arrayList of BarPanels and gives the inputs that are entered by user in them.
     * It should be noticed that only the noticed which have their status check box selected
     * will be contained in the output .
     * @param barList ArrayList of BarPanels
     * @return String
     */
    public static String generateQueryInput (ArrayList<BarPanel> barList){
        ArrayList<ArrayList<String>> temp = new ArrayList<>();
        String content = "?";
        int ctr = 0 ;
        for (BarPanel barPanel : barList) {
            if(barPanel.getStatus().isSelected()) {
                String name = barPanel.getNamee().getText();
                String value = barPanel.getValue().getText();
                //if they are not empty :
                if (!(name.equals("")) && !(value.equals(""))) {
                    ArrayList<String> array = new ArrayList<>();
                    array.add(name);
                    array.add(value);
                    temp.add(array);
                }
            }
        }
        for(ArrayList<String> array : temp){
            content = content.concat(array.get(0)+"="+array.get(1));
            ctr ++ ;
            if(ctr != temp.size()){
                content = content+"&";
            }
        }
        if(content.equals("?"))
            content = "";
        return content;
    }

    /**
     * Used for when we want to save a request and serialize so all the data entered by
     * user should be shown the next time .
     * For saving , we take an ArrayList of BarPanels and generate the entered name and values
     * into a hashMap .
     * @param barList ArrayList of BarPanels
     * @return HashMap
     */
    public static HashMap<String , String> generateHashMap (ArrayList<BarPanel> barList){
        HashMap<String , String > map = new HashMap<>();
        for (BarPanel barPanel : barList) {
            String name = barPanel.getNamee().getText();
            String value = barPanel.getValue().getText();
            //if they are not empty :
            if (!(name.equals("")) && !(value.equals(""))) {
                map.put(name,value);
            }
        }
        return map;
    }





}
