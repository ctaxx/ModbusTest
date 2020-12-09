/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester.tasks;

import ModbusRTU.ModbusClient;
import ModbusTester.utils.FuncException;
import de.re.easymodbus.exceptions.ModbusException;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.UnsupportedLookAndFeelException;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

/**
 *
 * @author s.bikov
 */
public class DataPackageWriter extends JFrame{
    
    ModbusClient modbusClient;
    int[] targetAddresses = {570, 571};
    int [][] targetValues = {{500}, {500}};
    int initAddress = 570;
    int lastAddress = 593;
    int[] valueToWrite = {1};
    
    public DataPackageWriter() {
        super("DataPackageWriter");
        System.nanoTime();
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(DataPackageWriter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        setSize(500, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JButton initButton = new JButton("init");
        initButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                init();
            }
        });

        add(initButton);

        JButton startButton = new JButton("Start");
        startButton.addActionListener((ActionEvent e) -> {
            try {
//                for (int i = 0; i < targetAddresses.length; i++) {
                for (int i = initAddress; i <= lastAddress; i++) {    
                    Thread.sleep(450);
//                    modbusClient.WriteMultipleRegisters(targetAddresses[i], targetValues[i]);
                    modbusClient.WriteMultipleRegisters(i, valueToWrite);
                }
                System.out.println("done");
            } catch (ModbusException | SerialPortException | SerialPortTimeoutException | FuncException | InterruptedException ex) {
                Logger.getLogger(DataPackageWriter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SocketException ex) {
                Logger.getLogger(DataPackageWriter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DataPackageWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        add(startButton);

        setVisible(true);
    }
    
    public boolean init() {
        modbusClient = new ModbusClient("10.6.18.35", 502);
        boolean success = modbusClient.Available(2000);
        System.out.println("Available " + success);
        if (success) {
            try {
                modbusClient.Connect();
            } catch (IOException ex) {
                Logger.getLogger(DataPackageWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(DataPackageWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return success;
    }
    
    public static void main (String [] args){
        new DataPackageWriter();
    }
}
