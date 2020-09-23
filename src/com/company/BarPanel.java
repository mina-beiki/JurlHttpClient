package com.company;

import javax.swing.*;
import java.awt.*;

/**
 * This is a component in GUI interface panel 2 which is repeated many times .
 * It is shaped as a bar and contains a pair text fields to enter inputs for name and values
 * (or key and values ) .
 * Also has a JButton for deletion and JCheckBox which specifies the status of the
 * bar ( that it should be used or not )
 *
 * @author Mina Beiki
 *
 */
public class BarPanel extends JPanel {

    private JTextField name , value;
    private JCheckBox status ;
    private Icon trashIcon ;
    private JButton delete ;
    private JLabel nameLabel ;
    private JLabel valueLabel ;

    /**
     * Generates a new BarPanel  .
     */
    public BarPanel(){
        nameLabel = new JLabel("Name :");
        valueLabel = new JLabel("Value :");
        name = new JTextField("");
        name.setPreferredSize(new Dimension(80, 30));
        value = new JTextField("");
        value.setPreferredSize(new Dimension(80, 30));
        status = new JCheckBox("Status");
        status.setSelected(true);
        trashIcon = new ImageIcon("icon.png");
        delete = new JButton(trashIcon);
        delete.setPreferredSize(new Dimension(20, 20));

        //add :
        this.add(nameLabel);
        this.add(name);
        this.add(valueLabel);
        this.add(value);
        this.add(status);
        this.add(status);
        this.add(delete);

    }

    /**
     * Gets the name text field .
     * @return JTextField
     */
    public JTextField getNamee() {
        return name;
    }
    /**
     * Gets the value text field .
     * @return JTextField
     */
    public JTextField getValue() {
        return value;
    }
    /**
     * Gets the status checkBox .
     * @return JCheckBox
     */
    public JCheckBox getStatus() {
        return status;
    }
    /**
     * Gets the delete button .
     * @return JButton
     */
    public JButton getDelete() {
        return delete;
    }


}
