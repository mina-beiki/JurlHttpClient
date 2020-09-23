
package com.company;


import java.io.*;


/**
 * This class is for saving the settings in the program which needs to
 * be serialized an implements the java.io.Serializable interface .
 *
 * @author Mina Beiki
 * @version 2020.05.12
 */

public class Settings implements java.io.Serializable {
    //theme : string
    //follow redirect : boolean
    //exit operation : string
    private String theme ;
    private String exitOperation ;
    private Boolean followRedirect ;

/**
     * Makes a new settings class based on the default settings which are : light theme ,
     * null exit operation and false follow redirect ( unchecked ) .
     */

    public Settings() {
        theme="light";
        exitOperation=null;
        //at first , follow redirect is not checked so it is false : (if checked , it will be true )
        followRedirect=false;
    }


/**
     * Sets the theme of the settings .
     * @param theme String new theme
     */

    public void setTheme(String theme) {
        this.theme = theme;
    }


/**
     * Sets exit operation for program
     * @param exitOperation String new exit operation
     */

    public void setExitOperation(String exitOperation) {
        this.exitOperation = exitOperation;
    }


/**
     * Sets follow redirect
     * @param followRedirect boolean new follow redirect ( true : checked , false : unchecked)
     */

    public void setFollowRedirect(Boolean followRedirect) {
        this.followRedirect = followRedirect;
    }


/**
     * Gets the theme
     * @return String theme
     */

    public String getTheme() {
        return theme;
    }


/**
     * Gets the exit operation
     * @return String exit operation
     */

    public String getExitOperation() {
        return exitOperation;
    }


/**
     * Gets the follow redirect
     * @return Boolean follow redirect
     */

    public Boolean getFollowRedirect() {
        return followRedirect;
    }

}

