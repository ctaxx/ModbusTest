/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester.tasks;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author s.bikov
 */
public class HeaderPanel extends JPanel{
    public HeaderPanel(){
        super();
        GridLayout layout = new GridLayout(8, 1, 0, 12);
    
        setLayout(layout);
        
        
        add(new JLabel("origin"));
        add(new JLabel("max"));
        add(new JLabel("current"));
        add(new JLabel("min"));
        add(new JLabel("currErr"));
        add(new JLabel("maxErr"));
        add(new JLabel(""));
    }
}
