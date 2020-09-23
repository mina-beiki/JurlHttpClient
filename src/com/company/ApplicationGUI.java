
package com.company;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


/**
 * Makes the user interface for program and handles different events
 * happening in the program such as clicking on an specific button ,
 * exiting operations and so on .
 * Consists of 3 main parts which are called panel1 , panel2 and panel3
 * which are all located in main area panel  .
 * Each panel consists of different panels and components . Panel1 is mainly
 * for saving a list of requests or folder of requests .
 * Panel2 is for generating a new request and specifying the body message , header
 * , auth and query for it .
 * Panel3 is designed for viewing the response .
 *
 * @author Mina Beiki
 * @version 2020.05.12
 */

public class ApplicationGUI {

    //saves the request handlers and kinda has control over the logic of app :
    private ApplicationRepository appRep;
    private JFrame frame;
    private JPanel mainArea, panel1, addNewFR, formData, requests, panel2,
            searchPanel, body, auth, query, header, json, binaryData, urlPanel,
            binaryDataFields, bearerFields, urlPreviewFields, queryNameValue, addHeader, panel3, statusBar, raw, preview, jsonResponse, messageBody, responseHeader,
            queryNameValues, binaryDataFields1, binaryDataFields2, urlEncoded, queryBars , savedHeaders , savedForm , savedUrl , savedQuery;
    private JMenuItem options, exit, about, helpItem, fullScr, sideBar;
    private MenuItem showItem;
    private JMenu application;
    private JDialog optionsDialog, aboutDialog, helpDialog, newRequestDialog;
    private JComboBox semBox;
    private int frameX, frameY, lastFrameX, lastFrameY, lastX, lastY;
    private JRadioButton hideOnTray, exitClose, lightTheme, darkTheme;
    private JButton delete1, delete2, deleteQuery, applyTheme, newRequest, send, resetFile,
            save, createNewRequest, fileChooser, updateURLpreview , copyToClipboard;
    private JTextField formDataName, formDataValue, urlField, newRequestName, newHeader, newHeaderValue,
            newNameQuery, newValueQuery;
    private JLabel statusCode , statusMessage , time , dataLabel , imageLabel ;
    private JTable responseHeaderTable ;
    private JTextArea jsonText, previewArea, rawArea, jsonArea, urlPreviewField, fileAddress;
    private JSeparator sep1, sep2;
    private JTabbedPane tabs, responseTabs;
    private int ctr = 0, ctr1 = 0, ctr2 = 0, ctrEnc = 0, ctrHed = 0, ctrQuery = 0 , ctrSavedHed;
    private Settings settings;
    private JCheckBox followRedirect;
    private SerializeSettings serializeSettings;
    private DeserializeSettings deserializeSettings;
    private ArrayList<JButton> allRequests;
    private BarPanel formDataBar, urlEncodedBar, headerBar, queryBar;
    private ArrayList<BarPanel> formDataBodyList, urlEncodedBodyList, headerList, queryList;
    private String clickedRequest = "";
    private boolean patch = false ;


    /**
     * Generates a new interface and gui for program . Makes a new frame
     * for all panels and other components and apply the last modified settings .
     */

    public ApplicationGUI() {
        appRep = new ApplicationRepository();
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        //Applies the last modified settings :
        settings = new Settings();
        serializeSettings = new SerializeSettings();
        deserializeSettings = new DeserializeSettings();
        settings = deserializeSettings.deserialize();

        frame = new JFrame("My HTTP Client");
        frame.setLocationRelativeTo(null);
        frame.setPreferredSize(new Dimension(900, 550));
        //main area is a grid layout with 3 columns :
        mainArea = new JPanel();
        mainArea.setLayout(new BoxLayout(mainArea, BoxLayout.X_AXIS));
        frame.add(mainArea, BorderLayout.CENTER);
        ImageIcon logoIcon = new ImageIcon("logo.png");
        frame.setIconImage(logoIcon.getImage());
        frame.setLocation(240, 200);

        //makes a new list for requests :
        allRequests = new ArrayList<>();
    }


    /**
     * Sets up the menu bar which is located in the north of the base frame .
     * Menu bar is consisted of 3 menus : application , view and help . Each has
     * different menu items and different functionality .
     * All options which are changes in the options menu will be saved and
     * applied after reopening the program .
     */

    public void generateMenuBar() {
        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(5, 5));
        panel1.setPreferredSize(new Dimension(120, 1000));

        //adding menu bar and tabs :
        //application :
        JMenuBar menuBar = new JMenuBar();
        application = new JMenu("Application");
        application.setMnemonic('A');
        options = new JMenuItem("Options");
        exit = new JMenuItem("Exit");
        options.addActionListener(new ActionHandler());
        exit.addActionListener(new ActionHandler());
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
        options.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
        //view :
        JMenu view = new JMenu("View");
        view.setMnemonic('V');
        fullScr = new JMenuItem("Toggle Full Screen");
        sideBar = new JMenuItem("Toggle Side Bar");
        fullScr.addActionListener(new ActionHandler());
        sideBar.addActionListener(new ActionHandler());
        fullScr.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.ALT_MASK));
        sideBar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
        //help :
        JMenu help = new JMenu("Help");
        help.setMnemonic('H');
        helpItem = new JMenuItem("Help");
        about = new JMenuItem("About");
        about.addActionListener(new ActionHandler());
        helpItem.addActionListener(new ActionHandler());
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
        helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.ALT_MASK));
        //adding them :
        menuBar.add(application);
        menuBar.add(view);
        view.add(fullScr);
        view.add(sideBar);
        menuBar.add(help);
        application.add(options);
        application.add(exit);
        help.add(helpItem);
        help.add(about);
        //help and about :
        //about --> JDialog :
        Border borderIntro = BorderFactory.createLineBorder(new Color(52, 182, 238), 2);
        aboutDialog = new JDialog(frame, "About", false);
        aboutDialog.setLayout(new FlowLayout());
        aboutDialog.setMinimumSize(new Dimension(300, 270));
        aboutDialog.setLocationRelativeTo(frame);
        JTextArea aboutIntro = new JTextArea("Hi , I'm Mina . \n\n I'm a CE freshman in AUT :D .\n This project is made for my AP midterm Project.\n\n\n Name : Mina Beiki \n Student Code : 9831075 \n Email : minaabeiki@gmail.com");
        aboutIntro.setPreferredSize(new Dimension(200, 200));
        aboutIntro.setEditable(false);
        aboutIntro.setLineWrap(true);
        aboutIntro.setBorder(borderIntro);
        ImageIcon meIcon = new ImageIcon("me.JPG");
        JLabel me = new JLabel(meIcon);
        aboutDialog.add(me);
        aboutDialog.add(aboutIntro);
        //help --> JDialog :
        helpDialog = new JDialog(frame, "Help", false);
        helpDialog.setLayout(new FlowLayout());
        helpDialog.setMinimumSize(new Dimension(460, 300));
        helpDialog.setLocationRelativeTo(frame);
        ImageIcon helpIcon = new ImageIcon("help.png");
        JLabel helpLabel = new JLabel(helpIcon);
        helpDialog.add(helpLabel);

        frame.setJMenuBar(menuBar);
        //menu --> Options --> JDialog :
        optionsDialog = new JDialog(frame, "Options", false);
        optionsDialog.setLayout(new BorderLayout(5, 5));
        optionsDialog.setMinimumSize(new Dimension(250, 250));
        optionsDialog.setLocationRelativeTo(frame);
        JTabbedPane optionsTabs = new JTabbedPane();
        //general :
        JPanel general = new JPanel();
        general.setLayout(new BoxLayout(general, BoxLayout.Y_AXIS));
        JPanel theme = new JPanel();
        theme.setLayout(new BoxLayout(theme, BoxLayout.Y_AXIS));
        optionsTabs.add("General", general);
        optionsTabs.add("Theme", theme);
        optionsDialog.add(optionsTabs, BorderLayout.NORTH);

        followRedirect = new JCheckBox("Follow Redirect");
        followRedirect.addActionListener(new ActionHandler());
        JSeparator optSep = new JSeparator(SwingConstants.HORIZONTAL);
        ButtonGroup bgExit = new ButtonGroup();
        hideOnTray = new JRadioButton("Hide On System Tray");
        exitClose = new JRadioButton("Exit and close the program");
        hideOnTray.addActionListener(new ActionHandler());
        exitClose.addActionListener(new ActionHandler());
        bgExit.add(hideOnTray);
        bgExit.add(exitClose);

        general.add(followRedirect);
        general.add(optSep);
        general.add(hideOnTray);
        general.add(exitClose);


        //theme :
        ButtonGroup bgTheme = new ButtonGroup();
        lightTheme = new JRadioButton("Light Theme");
        darkTheme = new JRadioButton("Dark Theme");
        bgTheme.add(lightTheme);
        bgTheme.add(darkTheme);
        applyTheme = new JButton("Apply");
        theme.add(lightTheme);
        theme.add(darkTheme);
        theme.add(applyTheme);
        applyTheme.addActionListener(new ActionHandler());
        lightTheme.addActionListener(new ActionHandler());
        darkTheme.addActionListener(new ActionHandler());

    }


    /**
     * Generates panel 1 and saves a list of requests or folder of requests .
     * Adds buttons for adding a new request or new folder ; Also adds the name
     * of the program on top of the list .
     */

    public void generatePanel1() {
        //panel 1 : ( List of saved requests )

        searchPanel = new JPanel();
        Border border = BorderFactory.createLineBorder(new Color(36, 118, 158), 2);
        searchPanel.setLayout(new BorderLayout(5, 5));
        //label for name (my http client) :
        JLabel name = new JLabel(" Jurl HTTP Client ");
        name.setPreferredSize(new Dimension(100, 58));
        name.setBorder(border);
        name.setOpaque(true);
        name.setBackground(new Color(52, 182, 238));
        name.setForeground(Color.WHITE);
        name.setFont(new Font("Helvetica", Font.BOLD, 20));
        //Buttons for generating new folder and request :
        addNewFR = new JPanel(new GridLayout(1, 2));
        newRequest = new JButton("New Request");
        newRequest.addActionListener(new ActionHandler());
        addNewFR.add(newRequest);
        searchPanel.add(name, BorderLayout.NORTH);
        searchPanel.add(addNewFR, BorderLayout.CENTER);
        panel1.add(searchPanel, BorderLayout.NORTH);

        //adding new request or folder :
        //adding new request :
        newRequestDialog = new JDialog(frame, "New Request", true);
        newRequestDialog.setMinimumSize(new Dimension(250, 100));
        newRequestDialog.setLayout(new FlowLayout(FlowLayout.CENTER));
        newRequestDialog.setLocationRelativeTo(frame);
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nameLabel = new JLabel("Name : ");
        newRequestName = new JTextField("My Request");
        namePanel.add(nameLabel);
        namePanel.add(newRequestName);
        createNewRequest = new JButton("create");
        createNewRequest.addActionListener(new ActionHandler());
        newRequestDialog.add(namePanel);
        newRequestDialog.add(createNewRequest);

        //sample of saved requests and folders :
        requests = new JPanel();
        requests.setLayout(new BoxLayout(requests, BoxLayout.Y_AXIS));

        panel1.add(requests, BorderLayout.CENTER);
        mainArea.add(panel1);

        frame.add(mainArea);
    }


    /**
     * Generates panel2 which is designed for making a new request . Consists
     * of a part for adding the url , choosing the method and sending the request ;
     * And another part which includes tabs for body message , header , auth and query .
     * Body message has different types too which are included in different tabs .
     */

    public void generatePanel2() {

        //panel 2 : ( Generating a new request )
        panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(5, 5));
        panel2.setPreferredSize(new Dimension(320, 1000));
        urlPanel = new JPanel();
        urlPanel.setLayout(new BoxLayout(urlPanel, BoxLayout.X_AXIS));
        String[] semantics = {"GET", "DELETE", "POST", "PUT", "PATCH"};
        semBox = new JComboBox(semantics);
        urlField = new JTextField("Enter URL ...");
        urlField.setPreferredSize(new Dimension(200, 25));
        urlField.addFocusListener(new FocusHandler());
        send = new JButton("Send");
        send.addActionListener(new ActionHandler());
        urlPanel.add(semBox);
        urlPanel.add(urlField);
        urlPanel.add(send);

        panel2.add(urlPanel, BorderLayout.NORTH);

        save = new JButton("Save");
        save.addActionListener(new ActionHandler());
        panel2.add(save, BorderLayout.SOUTH);

        tabs = new JTabbedPane();
        body = new JPanel(new BorderLayout(5, 5));
        auth = new JPanel();
        query = new JPanel();
        header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        query.setLayout(new BoxLayout(query, BoxLayout.Y_AXIS));
        JScrollPane scrollFormData = new JScrollPane(body);
        scrollFormData.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


        tabs.add("Body", scrollFormData);
        tabs.add("Auth", auth);
        JScrollPane scrollQuery = new JScrollPane(query);
        scrollQuery.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tabs.add("Query", scrollQuery);
        JScrollPane scrollHeaders = new JScrollPane(header);
        scrollFormData.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tabs.add("Header", scrollHeaders);


        //Adding tabs to message body :
        JTabbedPane bodyTabs = new JTabbedPane();
        formData = new JPanel();
        urlEncoded = new JPanel();
        formData.setLayout(new BoxLayout(formData, BoxLayout.Y_AXIS));
        urlEncoded.setLayout(new BoxLayout(urlEncoded, BoxLayout.Y_AXIS));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));


        json = new JPanel();
        binaryData = new JPanel();


        bodyTabs.add("Form Data", formData);
        bodyTabs.add("UrlEncoded", urlEncoded);
        bodyTabs.add("JSON", json);
        bodyTabs.add("Binary Data", binaryData);
        body.add(bodyTabs, BorderLayout.NORTH);


        //form data :

        formDataBar = new BarPanel();
        //formDataBodyList.add(formDataBar);
        formData.add(formDataBar);
        formDataBar.getNamee().addFocusListener(new FocusHandler());
        formDataBar.getValue().addFocusListener(new FocusHandler());

        //Url Encoded :

        urlEncodedBar = new BarPanel();
        //urlEncodedBodyList.add(urlEncodedBar);
        urlEncoded.add(urlEncodedBar);
        urlEncodedBar.getNamee().addFocusListener(new FocusHandler());
        urlEncodedBar.getValue().addFocusListener(new FocusHandler());


        //JSON :

        jsonText = new JTextArea("...", 3, 26);
        jsonText.addFocusListener(new FocusHandler());
        JScrollPane scrollableText = new JScrollPane(jsonText);
        json.add(scrollableText, BorderLayout.NORTH);

        //Auth :
        bearerFields = new JPanel(new GridLayout(3, 2));
        JLabel token = new JLabel("Token");
        JLabel prefix = new JLabel("Prefix");
        JTextField tokenField = new JTextField("                          ");
        JTextField prefixField = new JTextField("                          ");
        JLabel enabledLabel = new JLabel("Enabled");
        JCheckBox enabledCheckBox = new JCheckBox();
        bearerFields.add(token);
        bearerFields.add(tokenField);
        bearerFields.add(prefix);
        bearerFields.add(prefixField);
        bearerFields.add(enabledLabel);
        bearerFields.add(enabledCheckBox);
        auth.add(bearerFields);

        //Query :

        urlPreviewFields = new JPanel(new GridLayout(3, 1));
        JLabel urlPreview = new JLabel("URL Preview:");
        urlPreviewField = new JTextArea("        ");
        urlPreviewField.setLineWrap(true);
        urlPreviewField.setEnabled(false);
        urlPreviewFields.add(urlPreview);
        urlPreviewFields.add(urlPreviewField);
        urlPreviewField.setForeground(Color.DARK_GRAY);
        updateURLpreview = new JButton("Update URL preview");
        updateURLpreview.setPreferredSize(new Dimension(300, 20));
        updateURLpreview.addActionListener(new ActionHandler());
        urlPreviewFields.add(updateURLpreview);

        urlField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                urlPreviewField.setText(urlField.getText());
            }

            public void removeUpdate(DocumentEvent e) {
                urlPreviewField.setText(urlField.getText());
            }

            public void insertUpdate(DocumentEvent e) {
                urlPreviewField.setText(urlField.getText());
            }
        });

        queryBars = new JPanel();
        queryBars.setLayout(new BoxLayout(queryBars, BoxLayout.Y_AXIS));
        query.add(urlPreviewFields);
        JSeparator sep4 = new JSeparator();
        query.add(sep4);

        queryBar = new BarPanel();
        queryBars.add(queryBar);
        queryBar.getNamee().addFocusListener(new FocusHandler());
        queryBar.getValue().addFocusListener(new FocusHandler());

        query.add(queryBars);

        //binary data :
        binaryData.setLayout(new BoxLayout(binaryData, BoxLayout.Y_AXIS));
        binaryDataFields1 = new JPanel(new GridLayout(1, 2));
        binaryDataFields2 = new JPanel(new GridLayout(1, 2));
        JLabel selectedFile = new JLabel("Selected File:");
        fileAddress = new JTextArea("No file selected");
        fileAddress.setEditable(false);
        fileAddress.setLineWrap(true);
        resetFile = new JButton("Reset File");
        resetFile.addActionListener(new ActionHandler());
        //with using file chooser :
        fileChooser = new JButton("Choose File");
        binaryDataFields1.add(selectedFile);
        binaryDataFields1.add(fileAddress);
        binaryDataFields2.add(resetFile);
        binaryDataFields2.add(fileChooser);
        binaryData.add(binaryDataFields1);
        binaryData.add(binaryDataFields2);
        fileChooser.addActionListener(new ActionHandler());


        //header panel :


        headerBar = new BarPanel();
        //headerList.add(headerBar);
        header.add(headerBar);
        headerBar.getNamee().addFocusListener(new FocusHandler());
        headerBar.getValue().addFocusListener(new FocusHandler());

        panel2.add(tabs, BorderLayout.CENTER);


        sep1 = new JSeparator(SwingConstants.VERTICAL);
        mainArea.add(sep1);
        mainArea.add(panel2);

    }


    /**
     * Generates panel 3 for viewing the response of a request and analysing it .
     * Like panel 2 consists tabs for body message and header ; And body message
     * has 3 different groups . ( raw , preview , JSON )
     * Consists of a bar on top which shows the status code , status message and
     * data amount.
     */

    public void generatePanel3() {

        //Panel 3 : (Part for viewing the response )
        panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(5, 5));
        panel3.setPreferredSize(new Dimension(270, 1000));

        statusBar = new JPanel();
        statusBar.setLayout(new FlowLayout(FlowLayout.LEFT));

        statusCode = new JLabel("Code");
        statusMessage = new JLabel("Message");
        time = new JLabel("Time");
        dataLabel = new JLabel("Data");

        Border border2 = BorderFactory.createLineBorder(Color.GRAY, 1);
        statusCode.setBorder(border2);
        //we assume it is a OK response :
        statusCode.setBackground(Color.GREEN);
        statusMessage.setBackground(Color.GREEN);
        statusMessage.setBorder(border2);
        Border border3 = BorderFactory.createLineBorder(Color.BLACK, 1);
        time.setBorder(border3);
        time.setBackground(Color.GRAY);
        dataLabel.setBorder(border3);
        dataLabel.setBackground(Color.GRAY);
        time.setOpaque(true);
        dataLabel.setOpaque(true);
        statusCode.setOpaque(true);
        statusMessage.setOpaque(true);


        statusBar.add(statusCode);
        statusBar.add(statusMessage);
        statusBar.add(time);
        statusBar.add(dataLabel);

        panel3.add(statusBar, BorderLayout.NORTH);

        responseTabs = new JTabbedPane();
        messageBody = new JPanel();
        JScrollPane messageBodyScroll = new JScrollPane(messageBody);
        messageBodyScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        messageBody.setLayout(new BorderLayout(5, 5));
        responseHeader = new JPanel();
        responseHeader.setLayout(new BorderLayout(5, 5));
        responseTabs.add("Message Body", messageBodyScroll);
        responseTabs.add("Header", responseHeader);

        JTabbedPane responseBodyTabs = new JTabbedPane();
        raw = new JPanel(new BorderLayout(5, 5));
        preview = new JPanel(new BorderLayout(5, 5));
        imageLabel = new JLabel();
        preview.add(imageLabel,BorderLayout.NORTH);


        jsonResponse = new JPanel(new BorderLayout(5, 5));
        responseBodyTabs.add("Raw", raw);
        responseBodyTabs.add("Preview", preview);
        responseBodyTabs.add("JSON", jsonResponse);
        messageBody.add(responseBodyTabs, BorderLayout.NORTH);

        //header tab :
        String[][] responseNameValues = {{"", ""},
                {"", ""}, {"", ""},{"", ""},{"", ""},{"", ""},{"", ""},
                {"", ""},{"", ""},{"", ""},{"", ""},{"", ""},{"", ""},
                {"", ""},{"", ""},{"", ""},{"", ""},{"", ""}};
        String[] column = {"Name", "Value"};

        responseHeaderTable = new JTable(responseNameValues, column);
        responseHeaderTable.getColumnModel().getColumn(0).setPreferredWidth(10);
        responseHeaderTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        JScrollPane table = new JScrollPane(responseHeaderTable);
        responseHeader.add(table, BorderLayout.CENTER);
        table.setPreferredSize(new Dimension(250, 200));
        copyToClipboard = new JButton("Copy To Clipboard");
        responseHeader.add(copyToClipboard, BorderLayout.SOUTH);
        copyToClipboard.addActionListener(new ActionHandler());


        rawArea = new JTextArea();
        rawArea.setPreferredSize(new Dimension(200, 800));
        rawArea.setLineWrap(true);
        raw.add(rawArea, BorderLayout.CENTER);
        jsonArea = new JTextArea();
        jsonArea.setLineWrap(true);
        jsonArea.setPreferredSize(new Dimension(200, 200));
        jsonResponse.add(jsonArea, BorderLayout.CENTER);


        panel3.add(responseTabs, BorderLayout.CENTER);

        sep2 = new JSeparator(SwingConstants.VERTICAL);
        mainArea.add(sep2);
        mainArea.add(panel3);


    }


    /**
     * Handler for action listener which handles action events .
     */

    private class ActionHandler implements ActionListener {


        /**
         * Invoked when an action occurs. Such as clicking a menu item
         * or a button .
         *
         * @param e action event which has happened
         */

        @Override
        public void actionPerformed(ActionEvent e) {
            String binaryFileAddress = "";
            //if click on options menu item :
            //menu bar --> options :
            if (e.getSource() == options) {
                optionsDialog.setVisible(true);
            }
            //menu bar --> About :
            if (e.getSource() == about) {
                aboutDialog.setVisible(true);
            }
            //menu bar --> Help :
            if (e.getSource() == helpItem) {
                helpDialog.setVisible(true);
            }
            //toggle ful screen :
            if (e.getSource() == fullScr) {
                frameY = frame.getHeight();
                frameX = frame.getWidth();
                //System.out.println(frameX + " " + frameY);
                if (frameX == 1440 && frameY == 807) {
                    frame.setSize(lastFrameX, lastFrameY);
                    frame.setLocation(lastX, lastY);
                } else {
                    //saves the last x and y for frame :
                    lastX = frame.getX();
                    lastY = frame.getY();
                    lastFrameX = frame.getWidth();
                    lastFrameY = frame.getHeight();
                    frame.setExtendedState(Frame.MAXIMIZED_BOTH);
                }
                frame.setVisible(true);

            }
            //toggle side bar :
            if (e.getSource() == sideBar) {
                if (panel1.isVisible()) {
                    panel1.setVisible(false);
                } else {
                    panel1.setVisible(true);
                }
            }
            //follow redirect :
            if (e.getSource() == followRedirect) {
                settings.setFollowRedirect(true);
            }
            //exit operations :
            if (e.getSource() == exit) {
                if (hideOnTray.isSelected()) {
                    settings.setExitOperation("tray");
                    SystemTray tray = SystemTray.getSystemTray();
                    ImageIcon logo = new ImageIcon("logo.png");
                    TrayIcon trayIcon = new TrayIcon(logo.getImage());
                    showItem = new MenuItem("Show");
                    PopupMenu popup = new PopupMenu();
                    popup.add(showItem);
                    showItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            frame.setVisible(true);
                        }
                    });
                    trayIcon.setPopupMenu(popup);
                    try {
                        tray.add(trayIcon);
                    } catch (AWTException e2) {
                        System.out.println("TrayIcon could not be added.");
                    }
                    frame.setVisible(false);
                    serializeSettings.serialize(settings);
                }
                if (exitClose.isSelected()) {
                    settings.setExitOperation("close");
                    serializeSettings.serialize(settings);
                    System.exit(0);
                }
            }
            if (e.getSource() == showItem) {
                frame.setVisible(true);
            }
            //theme :
            if (e.getSource() == applyTheme) {
                if (lightTheme.isSelected()) {
                    settings.setTheme("light");
                    setLightTheme();

                }
                if (darkTheme.isSelected()) {
                    settings.setTheme("dark");
                    setDarkTheme();
                }
            }

            //adding new request :
            if (e.getSource() == newRequest) {
                newRequestDialog.setVisible(true);
            }
            if (e.getSource() == createNewRequest) {
                JButton newRequest = new JButton(newRequestName.getText());
                //adds the new request to array list of all requests' j buttons :
                allRequests.add(newRequest);
                newRequest.addActionListener(new ActionHandler());
                //adds a new request repository for this new request in the app repository :
                appRep.addRequest(newRequestName.getText());
                //adds it to panel1 , in the bar for requests :
                //requests is the panel for listing requests .
                requests.add(newRequest);
                requests.revalidate();
                requests.repaint();
                frame.pack();
            }
            if (e.getSource() == fileChooser) {
                //file chooser
                JFileChooser binaryFileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                binaryFileChooser.setDialogTitle("Choose Binary File");
                int returnValue = binaryFileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = binaryFileChooser.getSelectedFile();
                    binaryFileAddress = selectedFile.getAbsolutePath();
                    fileAddress.setText(selectedFile.getAbsolutePath());
                }
            }
            for (JButton req : allRequests) {
                if (e.getSource() == req) {
                    //re new lists :
                    headerList = new ArrayList<>();
                    headerList.add(headerBar);
                    formDataBodyList = new ArrayList<>();
                    formDataBodyList.add(formDataBar);
                    queryList = new ArrayList<>();
                    queryList.add(queryBar);
                    urlEncodedBodyList = new ArrayList<>();
                    urlEncodedBodyList.add(urlEncodedBar);
                    //saves the name of the request :
                    clickedRequest = req.getText();
                    //if the request has been saved :
                    File file = new File("./reqRepositories/"+clickedRequest+".txt");
                    //if the request is saved and already exists , use the saved data to complete panel 2 :
                    if(file.exists() ) {
                        File desFile = new File("./reqRepositories/"+clickedRequest+".txt");
                        FileUtils.deserializeGUIRequest(desFile);
                        HashMap<String, String> headers = FileUtils.getHeaders();
                        System.out.println("saved headers :"+headers);
                        HashMap<String, String> formData = FileUtils.getFormData();
                        System.out.println("saved formData :"+formData);
                        HashMap<String, String> urlEncoded = FileUtils.getUrlencoded();
                        System.out.println("saved urlencoded :"+urlEncoded);
                        HashMap<String, String> query = FileUtils.getQuery();
                        System.out.println("saved query :"+query);
                        //show the data saved for the request
                        ArrayList<String> data = FileUtils.getSavedDataStrings();
                        System.out.println("string data : "+data);
                        updatePanel2(data, headers, formData, urlEncoded, query);
                    }
                }
            }

            //sending request :
            if (e.getSource() == send) {
                //url :
                String url = urlField.getText();
                //method :
                String method = semBox.getSelectedItem().toString();
                //space bars in between are important :
                //we make a input line (like phase 2 ) to exchange data between gui and repository :
                //in every request we have at least url and method :
                String formData = Input.generatePairsInput(formDataBodyList, '=', '&');
                String urlEncoded = Input.generatePairsInput(urlEncodedBodyList, '=', '&');
                String headers = Input.generatePairsInput(headerList, ':', ';');
                String query = Input.generateQueryInput(queryList);
                String json = jsonText.getText();
                binaryFileAddress = fileAddress.getText();
                if (json.equals("...")) {
                    json = "";
                }
                System.out.println("form data : " + formData);
                System.out.println("urlEncoded : " + urlEncoded);
                System.out.println("headers : " + headers);
                System.out.println("Query : " + query);
                System.out.println("Json : " + json);
                System.out.println("Binary file address : " + binaryFileAddress);
                //query is added if it is entered .
                String data = "jurl --url " + url + " --method " + method ;
                if(!(headers.equals(""))){
                    data = data.concat(" -H "+headers);
                }
                if(!(json.equals(""))){
                    data = data.concat(" -j "+json);
                }
                if(!(binaryFileAddress.equals("No file selected"))){
                    data = data.concat(" --upload "+binaryFileAddress);
                }
                if(settings.getFollowRedirect()){
                    //let's double check to make sure :
                    if(followRedirect.isSelected()) {
                        //if fr is set :
                        data = data.concat(" -f");
                    }
                }
                if(!(formData.equals(""))){
                    data = data.concat(" -d "+formData);
                }
                if(!(urlEncoded.equals(""))){
                    data = data.concat(" --urlencoded "+urlEncoded);
                }

                System.out.println("input : "+data);
                if(method.equals("PATCH")){
                    patch = true ;
                }

                // indicate that the calculation has begun
                rawArea.setText("Loading...");

                //using swing worker :
                SendRequest task = new SendRequest(data.split(" "),responseHeaderTable,rawArea,preview,imageLabel,jsonArea,
                        appRep.getRequestRepository(clickedRequest),statusCode,statusMessage,time ,dataLabel,patch);

                task.execute();

            }
            //saving a request : (data must be shown next time the request is clicked . )
            if (e.getSource() == save) {
                String url = urlField.getText();
                String method = semBox.getSelectedItem().toString();
                String json = jsonText.getText();
                if (json.equals("...")) {
                    json = "";
                }
                HashMap<String , String > formData = Input.generateHashMap(formDataBodyList);
                HashMap<String , String > headers = Input.generateHashMap(headerList);
                HashMap<String , String > query = Input.generateHashMap(queryList);
                HashMap<String , String > urlencoded = Input.generateHashMap(urlEncodedBodyList);
                //saves the data of the request :
                appRep.saveRequest(clickedRequest, url, method, json, fileAddress.getText(), headers,formData,urlencoded,query);
            }
            if (e.getSource() == updateURLpreview) {
                String query = Input.generateQueryInput(queryList);
                String urlPrv = urlField.getText();
                urlPrv = urlPrv.concat(query);
                urlPreviewField.setText(urlPrv);
                urlField.setText(urlPrv);
            }
            if (e.getSource() == resetFile) {
                fileAddress.setText("No file selected");
            }
            if(e.getSource()==copyToClipboard){
                String toCopy = "" ;
                for (int i = 0; i < responseHeaderTable.getRowCount(); i++) {
                    for (int j = 0; j < responseHeaderTable.getColumnCount(); j++) {
                        toCopy = toCopy.concat((String) responseHeaderTable.getValueAt(i,j));
                        toCopy = toCopy +":";
                    }
                    toCopy = toCopy +"\n";
                }
                StringSelection stringSelection = new StringSelection(toCopy);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
            }
        }
    }


    /**
     * Handler for Focus events such as gaining focus on a text field.Handles
     * focus events .
     */

    private class FocusHandler implements FocusListener {


        /**
         * Invoked when a component gains the keyboard focus.
         *
         * @param e Focus event which has happened
         */

        @Override
        public void focusGained(FocusEvent e) {

            Color lightColor = new Color(226, 229, 225);
            //form data --> add new name/value :

            if(e.getSource()==formDataBodyList.get(formDataBodyList.size()-1).getNamee() ||e.getSource()==formDataBodyList.get(formDataBodyList.size()-1).getValue()){
                BarPanel newBarPanel = new BarPanel();
                newBarPanel.setPreferredSize(new Dimension(100, 45));
                if (settings.getTheme().equals("light")) {
                    newBarPanel.setBackground(lightColor);
                } else {
                    newBarPanel.setBackground(Color.DARK_GRAY);
                }
                formDataBodyList.add(newBarPanel);

                newBarPanel.getDelete().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        formDataBodyList.remove(newBarPanel);
                        newBarPanel.setVisible(false);
                    }
                });

                newBarPanel.getNamee().addFocusListener(new FocusHandler());
                newBarPanel.getValue().addFocusListener(new FocusHandler());


                formData.add(newBarPanel);
                formData.revalidate();
                formData.repaint();
                frame.pack();
            }
            //UrlEncoded --> add new name/value :

            if(e.getSource()==urlEncodedBodyList.get(urlEncodedBodyList.size()-1).getNamee() ||e.getSource()==urlEncodedBodyList.get(urlEncodedBodyList.size()-1).getValue()){
                BarPanel newEncBarPanel = new BarPanel();
                newEncBarPanel.setPreferredSize(new Dimension(100, 45));
                urlEncodedBodyList.add(newEncBarPanel);
                if (settings.getTheme().equals("light")) {
                    newEncBarPanel.setBackground(lightColor);
                } else {
                    newEncBarPanel.setBackground(Color.DARK_GRAY);
                }

                newEncBarPanel.getDelete().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        urlEncodedBodyList.remove(newEncBarPanel);
                        newEncBarPanel.setVisible(false);
                    }
                });


                newEncBarPanel.getNamee().addFocusListener(new FocusHandler());
                newEncBarPanel.getValue().addFocusListener(new FocusHandler());


                urlEncoded.add(newEncBarPanel);
                urlEncoded.revalidate();
                urlEncoded.repaint();
                frame.pack();
            }
            //header --> add new header/value :

            if(e.getSource()==headerList.get(headerList.size()-1).getNamee() ||e.getSource()==headerList.get(headerList.size()-1).getValue()){
                BarPanel newHedBarPanel = new BarPanel();
                newHedBarPanel.setPreferredSize(new Dimension(100, 45));
                headerList.add(newHedBarPanel);
                if (settings.getTheme().equals("light")) {
                    newHedBarPanel.setBackground(lightColor);
                } else {
                    newHedBarPanel.setBackground(Color.DARK_GRAY);
                }

                newHedBarPanel.getDelete().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        headerList.remove(newHedBarPanel);
                        newHedBarPanel.setVisible(false);
                    }
                });

                newHedBarPanel.getNamee().addFocusListener(new FocusHandler());
                newHedBarPanel.getValue().addFocusListener(new FocusHandler());


                header.add(newHedBarPanel);
                header.revalidate();
                header.repaint();
                frame.pack();
            }
            //query --> new name and value :

            if(e.getSource()==queryList.get(queryList.size()-1).getNamee() ||e.getSource()==queryList.get(queryList.size()-1).getValue()){
                BarPanel newQueryBarPanel = new BarPanel();
                newQueryBarPanel.setPreferredSize(new Dimension(100, 45));
                queryList.add(newQueryBarPanel);
                if (settings.getTheme().equals("light")) {
                    newQueryBarPanel.setBackground(lightColor);
                } else {
                    newQueryBarPanel.setBackground(Color.DARK_GRAY);
                }

                newQueryBarPanel.getDelete().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        queryList.remove(newQueryBarPanel);
                        newQueryBarPanel.setVisible(false);
                    }
                });

                newQueryBarPanel.getNamee().addFocusListener(new FocusHandler());
                newQueryBarPanel.getValue().addFocusListener(new FocusHandler());


                query.add(newQueryBarPanel);
                query.revalidate();
                query.repaint();
                frame.pack();
            }
            //typing in json Area :
            if (e.getSource() == jsonText) {
                jsonText.setText("");
            }

        }


        /**
         * Invoked when a component loses the keyboard focus.
         *
         * @param e Focus event which has happened
         */

        @Override
        public void focusLost(FocusEvent e) {
            //nothing to do in this phase
        }
    }


    /**
     * Shows the gui interface and applies the last changed settings .
     */

    public void showGUI() {
        frame.pack();
        frame.setVisible(true);
        //set up the last saved settings :
        setLastChangedSettings();
        setLastSavedRequests();

    }

    /**
     * Shows the last saved request from reqRepositories directory every time
     * the gui interface is running .
     */
    public void setLastSavedRequests(){
        File path = new File("./reqRepositories/");
        File[] listOfFiles = path.listFiles();
        for (File file : listOfFiles) {
            String fileName = file.getName();
            fileName = fileName.replaceAll(".txt","");
            JButton request = new JButton(fileName);
            request.addActionListener(new ActionHandler());
            allRequests.add(request);
            requests.add(request);
            requests.revalidate();
            requests.repaint();
            frame.pack();
        }
    }


    /**
     * This method applies the last modified changes in the interface based on the
     * settings object which this class includes and has serialized . After deserializing
     * it , it access the last settings .
     */

    public void setLastChangedSettings() {
        if (settings.getTheme().equals("light")) {
            setLightTheme();
        }
        if (settings.getTheme().equals("dark")) {
            setDarkTheme();
        }
        if (settings.getExitOperation() == null) {
            System.out.println("null exit operation");
        }
        if (settings.getExitOperation().equals("tray")) {
            hideOnTray.setSelected(true);
        }
        if (settings.getExitOperation().equals("close")) {
            exitClose.setSelected(true);
        }
        if (settings.getFollowRedirect()) {
            followRedirect.setSelected(true);

        }
        if (!settings.getFollowRedirect()) {
            followRedirect.setSelected(false);
        }
    }


    /**
     * Sets the theme (Components and panels background color )to light theme .
     */


    public void setLightTheme() {
        Color lightColor = new Color(226, 229, 225);
        queryBars.setBackground(lightColor);
        queryBar.setBackground(lightColor);
        headerBar.setBackground(lightColor);
        mainArea.setBackground(lightColor);
        requests.setBackground(lightColor);
        panel1.setBackground(lightColor);
        searchPanel.setBackground(lightColor);
        panel2.setBackground(lightColor);
        body.setBackground(lightColor);
        auth.setBackground(lightColor);
        query.setBackground(lightColor);
        header.setBackground(lightColor);
        formData.setBackground(lightColor);
        json.setBackground(lightColor);
        binaryData.setBackground(lightColor);
        urlPanel.setBackground(lightColor);
        formDataBar.setBackground(lightColor);
        urlEncodedBar.setBackground(lightColor);
        formDataBar.setBackground(lightColor);
        jsonText.setBackground(lightColor);
        jsonText.setOpaque(true);
        binaryDataFields1.setBackground(lightColor);
        binaryDataFields2.setBackground(lightColor);
        sep1.setBackground(Color.GRAY);
        sep2.setBackground(Color.GRAY);
        tabs.setBackground(lightColor);
        addNewFR.setBackground(lightColor);
        bearerFields.setBackground(lightColor);
        urlPreviewFields.setBackground(lightColor);
        panel3.setBackground(lightColor);
        statusBar.setBackground(lightColor);
        raw.setBackground(lightColor);
        jsonResponse.setBackground(lightColor);
        preview.setBackground(lightColor);
        responseTabs.setBackground(lightColor);
        messageBody.setBackground(lightColor);
        responseHeader.setBackground(lightColor);
        rawArea.setBackground(lightColor);
        jsonArea.setBackground(lightColor);
        newRequest.setBackground(lightColor);
        urlField.setBackground(lightColor);
        send.setBackground(lightColor);
        save.setBackground(lightColor);
        //more :
        newRequestDialog.setBackground(lightColor);
        aboutDialog.setBackground(lightColor);
        optionsDialog.setBackground(lightColor);
        helpDialog.setBackground(lightColor);
    }

    /**
     * Sets the theme (Components and panels background color )to dark theme .
     */

    public void setDarkTheme() {
        queryBar.setBackground(Color.DARK_GRAY);
        queryBars.setBackground(Color.DARK_GRAY);
        formDataBar.setBackground(Color.DARK_GRAY);
        urlEncodedBar.setBackground(Color.DARK_GRAY);
        headerBar.setBackground(Color.DARK_GRAY);
        mainArea.setBackground(Color.black);
        requests.setBackground(Color.DARK_GRAY);
        panel1.setBackground(Color.DARK_GRAY);
        searchPanel.setBackground(Color.DARK_GRAY);
        panel2.setBackground(Color.DARK_GRAY);
        body.setBackground(Color.DARK_GRAY);
        auth.setBackground(Color.DARK_GRAY);
        query.setBackground(Color.DARK_GRAY);
        header.setBackground(Color.DARK_GRAY);
        formData.setBackground(Color.DARK_GRAY);
        json.setBackground(Color.DARK_GRAY);
        binaryData.setBackground(Color.DARK_GRAY);
        urlPanel.setBackground(Color.DARK_GRAY);
        formDataBar.setBackground(Color.DARK_GRAY);
        jsonText.setBackground(Color.GRAY);
        jsonText.setOpaque(true);
        binaryDataFields1.setBackground(Color.DARK_GRAY);
        binaryDataFields2.setBackground(Color.DARK_GRAY);
        sep1.setBackground(Color.black);
        sep2.setBackground(Color.black);
        tabs.setBackground(Color.DARK_GRAY);
        addNewFR.setBackground(Color.DARK_GRAY);
        bearerFields.setBackground(Color.DARK_GRAY);
        urlPreviewFields.setBackground(Color.DARK_GRAY);
        panel3.setBackground(Color.DARK_GRAY);
        statusBar.setBackground(Color.DARK_GRAY);
        raw.setBackground(Color.DARK_GRAY);
        jsonResponse.setBackground(Color.DARK_GRAY);
        preview.setBackground(Color.DARK_GRAY);
        responseTabs.setBackground(Color.DARK_GRAY);
        messageBody.setBackground(Color.DARK_GRAY);
        responseHeader.setBackground(Color.DARK_GRAY);
        rawArea.setBackground(Color.GRAY);
        jsonArea.setBackground(Color.GRAY);
        newRequest.setBackground(Color.GRAY);
        urlField.setBackground(Color.GRAY);
        send.setBackground(Color.GRAY);
        save.setBackground(Color.GRAY);
        //more :
        newRequestDialog.setBackground(Color.DARK_GRAY);
        aboutDialog.setBackground(Color.DARK_GRAY);
        optionsDialog.setBackground(Color.DARK_GRAY);
        helpDialog.setBackground(Color.DARK_GRAY);

    }

    /**
     * Updates panel 2 every time a request button is clicked to show the saved fields .
     * @param data ArrayList<String> of String data fields
     * @param headersMap HashMap of headers
     * @param formDataMap HashMap of form data pairs
     * @param urlencodedMap HashMap of urlencoded pairs
     * @param queryMap HashMap of query pairs
     */
    public void updatePanel2(ArrayList<String> data, HashMap<String , String> headersMap , HashMap<String , String> formDataMap ,HashMap<String , String>
                             urlencodedMap , HashMap<String , String> queryMap) {

        Color lightColor = new Color(226, 229, 225);
        //url :
        if (data.get(0).equals("")) {
            urlField.setText("");
        } else {
            urlField.setText(data.get(0));
        }
        //method :
        if (data.get(1).equals("")) {
            semBox.setSelectedItem("GET");
        } else {
            semBox.setSelectedItem(data.get(1));
        }
        //json :
        if (data.get(2).equals("")) {
            jsonText.setText("...");
        } else {
            jsonText.setText(data.get(2));
        }
        //binary file address :
        if (data.get(3).equals("")) {
            fileAddress.setText("No file selected");
        } else {
            fileAddress.setText(data.get(3));
        }
        //set bar panels form hashMaps :
        //HEADERS :  :
        //remove all bar panels from before :
        header.removeAll();
        header.revalidate();
        header.repaint();
        for(int i=0 ; i<headersMap.size() ; i++){
            BarPanel newHedBarPanel = new BarPanel();
            newHedBarPanel.setPreferredSize(new Dimension(100, 45));
            Object key = headersMap.keySet().toArray()[i];
            String value = headersMap.get(key);
            newHedBarPanel.getNamee().setText((String)key);
            newHedBarPanel.getValue().setText(value);
            headerList.add(newHedBarPanel);
            if (settings.getTheme().equals("light")) {
                newHedBarPanel.setBackground(lightColor);
            } else {
                newHedBarPanel.setBackground(Color.DARK_GRAY);
            }

            newHedBarPanel.getDelete().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    headerList.remove(newHedBarPanel);
                    newHedBarPanel.setVisible(false);
                }
            });

            newHedBarPanel.getValue().addFocusListener(new FocusHandler());
            newHedBarPanel.getNamee().addFocusListener(new FocusHandler());

            header.add(newHedBarPanel);
            header.revalidate();
            header.repaint();
            frame.pack();
        }
        //if empty :
        if(headersMap.isEmpty()){
            BarPanel newBarPanel = new BarPanel();
            newBarPanel.setPreferredSize(new Dimension(100, 45));
            headerList.add(newBarPanel);
            if (settings.getTheme().equals("light")) {
                newBarPanel.setBackground(lightColor);
            } else {
                newBarPanel.setBackground(Color.DARK_GRAY);
            }

            newBarPanel.getDelete().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    urlEncodedBodyList.remove(newBarPanel);
                    newBarPanel.setVisible(false);
                }
            });

            newBarPanel.getValue().addFocusListener(new FocusHandler());
            newBarPanel.getNamee().addFocusListener(new FocusHandler());
            header.add(newBarPanel);
            header.revalidate();
            header.repaint();
            frame.pack();
        }
        //FORM DATA :  :
        formData.removeAll();
        formData.revalidate();
        formData.repaint();
        for(int i=0 ; i<formDataMap.size() ; i++){
            BarPanel newBarPanel = new BarPanel();
            newBarPanel.setPreferredSize(new Dimension(100, 45));
            Object key = formDataMap.keySet().toArray()[i];
            String value = formDataMap.get(key);
            newBarPanel.getNamee().setText((String)key);
            newBarPanel.getValue().setText(value);
            formDataBodyList.add(newBarPanel);
            if (settings.getTheme().equals("light")) {
                newBarPanel.setBackground(lightColor);
            } else {
                newBarPanel.setBackground(Color.DARK_GRAY);
            }

            newBarPanel.getDelete().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    formDataBodyList.remove(newBarPanel);
                    newBarPanel.setVisible(false);
                }
            });

            newBarPanel.getValue().addFocusListener(new FocusHandler());
            newBarPanel.getNamee().addFocusListener(new FocusHandler());

            formData.add(newBarPanel);
            formData.revalidate();
            formData.repaint();
            frame.pack();
        }
        //if empty :
        if(formDataMap.isEmpty()){
            BarPanel newBarPanel = new BarPanel();
            newBarPanel.setPreferredSize(new Dimension(100, 45));
            formDataBodyList.add(newBarPanel);
            if (settings.getTheme().equals("light")) {
                newBarPanel.setBackground(lightColor);
            } else {
                newBarPanel.setBackground(Color.DARK_GRAY);
            }

            newBarPanel.getDelete().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    urlEncodedBodyList.remove(newBarPanel);
                    newBarPanel.setVisible(false);
                }
            });

            newBarPanel.getValue().addFocusListener(new FocusHandler());
            newBarPanel.getNamee().addFocusListener(new FocusHandler());
            formData.add(newBarPanel);
            formData.revalidate();
            formData.repaint();
            frame.pack();
        }
        //QUERY :
        query.removeAll();
        query.revalidate();
        query.repaint();
        for(int i=0 ; i<queryMap.size() ; i++){
            BarPanel newBarPanel = new BarPanel();
            newBarPanel.setPreferredSize(new Dimension(100, 45));
            Object key = formDataMap.keySet().toArray()[i];
            String value = formDataMap.get(key);
            newBarPanel.getNamee().setText((String)key);
            newBarPanel.getValue().setText(value);
            queryList.add(newBarPanel);
            if (settings.getTheme().equals("light")) {
                newBarPanel.setBackground(lightColor);
            } else {
                newBarPanel.setBackground(Color.DARK_GRAY);
            }

            newBarPanel.getDelete().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    queryList.remove(newBarPanel);
                    newBarPanel.setVisible(false);
                }
            });

            newBarPanel.getValue().addFocusListener(new FocusHandler());
            newBarPanel.getNamee().addFocusListener(new FocusHandler());

            query.add(newBarPanel);
            query.revalidate();
            query.repaint();
            frame.pack();
        }
        //if empty :
        if(queryMap.isEmpty()){
            BarPanel newBarPanel = new BarPanel();
            newBarPanel.setPreferredSize(new Dimension(100, 45));
            queryList.add(newBarPanel);
            if (settings.getTheme().equals("light")) {
                newBarPanel.setBackground(lightColor);
            } else {
                newBarPanel.setBackground(Color.DARK_GRAY);
            }

            newBarPanel.getDelete().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    urlEncodedBodyList.remove(newBarPanel);
                    newBarPanel.setVisible(false);
                }
            });

            newBarPanel.getValue().addFocusListener(new FocusHandler());
            newBarPanel.getNamee().addFocusListener(new FocusHandler());
            query.add(newBarPanel);
            query.revalidate();
            query.repaint();
            frame.pack();
        }
        //url encoded :
        urlEncoded.removeAll();
        urlEncoded.revalidate();
        urlEncoded.repaint();
        for(int i=0 ; i<urlencodedMap.size() ; i++){
            BarPanel newBarPanel = new BarPanel();
            newBarPanel.setPreferredSize(new Dimension(100, 45));
            Object key = urlencodedMap.keySet().toArray()[i];
            String value = urlencodedMap.get(key);
            newBarPanel.getNamee().setText((String)key);
            newBarPanel.getValue().setText(value);
            urlEncodedBodyList.add(newBarPanel);
            if (settings.getTheme().equals("light")) {
                newBarPanel.setBackground(lightColor);
            } else {
                newBarPanel.setBackground(Color.DARK_GRAY);
            }

            newBarPanel.getDelete().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    urlEncodedBodyList.remove(newBarPanel);
                    newBarPanel.setVisible(false);
                }
            });

            newBarPanel.getValue().addFocusListener(new FocusHandler());
            newBarPanel.getNamee().addFocusListener(new FocusHandler());

            urlEncoded.add(newBarPanel);
            urlEncoded.revalidate();
            urlEncoded.repaint();
            frame.pack();
        }
        if(urlencodedMap.isEmpty()){
            BarPanel newBarPanel = new BarPanel();
            newBarPanel.setPreferredSize(new Dimension(100, 45));
            urlEncodedBodyList.add(newBarPanel);
            if (settings.getTheme().equals("light")) {
                newBarPanel.setBackground(lightColor);
            } else {
                newBarPanel.setBackground(Color.DARK_GRAY);
            }

            newBarPanel.getDelete().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    urlEncodedBodyList.remove(newBarPanel);
                    newBarPanel.setVisible(false);
                }
            });

            newBarPanel.getValue().addFocusListener(new FocusHandler());
            newBarPanel.getNamee().addFocusListener(new FocusHandler());
            urlEncoded.add(newBarPanel);
            urlEncoded.revalidate();
            urlEncoded.repaint();
            frame.pack();
        }
    }
}



