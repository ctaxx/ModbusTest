/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester.tasks;

import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author s.bikov
 */
public class ParameterPanel extends Panel{
    
    float originValue = 0f;
    public float currentValue = 0f;
    float minValue = Float.MAX_VALUE;
    float maxValue = Float.MIN_VALUE;
    float range = 100f;
    float currErrorValue = 0f;
    float maxErrorValue = 0f;
    
    JTextField originField;
    JTextField maxField;
    JTextField currentField;
    JTextField minField;
    JTextField currErrorField;
    JTextField maxErrorField;  
    
    JButton setButton;
    
    public ParameterPanel(){
        super();
        GridLayout layout = new GridLayout(8,1);
        layout.setHgap(10);
        setLayout(layout);
        
        originField = new JTextField();
        add(originField);

        setButton = new JButton("set");
        setButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                originValue = currentValue;
                maxValue = currentValue;
                minValue = currentValue;
                currErrorValue = 0f;
                maxErrorValue = 0f;
                originField.setText(Float.toString(originValue));
            }
        });
        add(setButton);

        maxField = new JTextField();
        add(maxField);
        add(new JLabel(""));

        currentField = new JTextField();
        add(currentField);
        add(new JLabel(""));

        minField = new JTextField();
        add(minField);
        add(new JLabel(""));

        currErrorField = new JTextField();
        add(currErrorField);
        add(new JLabel("range=" + Float.toString(range)));

        maxErrorField = new JTextField();
        add(maxErrorField);
        add(new JLabel(""));

        add(new JLabel(""));
        add(new JLabel(""));
        add(new JLabel(""));
        
    }
}
