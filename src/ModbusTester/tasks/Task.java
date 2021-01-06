/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester.tasks;

import ModbusRTU.ModbusClient;
import ModbusTester.device.Device;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPortException;

/**
 *
 * @author s.bikov
 */
public class Task {

    Device device;
    public ModbusClient modbusClient;

    public double progress = 0.0;
    public boolean enableDoingDeals = true;

    public void setDevice(Device device) {
        this.device = device;
    }

    public boolean init() {
        if (device.protocol.matches("Modbus TCP")) {
            return init(device.ipAddress, 502);
        }
        if (device.protocol.matches("Modbus RTU")) {
            return init("COM4");
        }
        return false;
    }

    public boolean init(String ip, int port) {
        modbusClient = new ModbusClient(ip, port);
        boolean success = modbusClient.Available(2500);
        System.out.println(success);
        if (success) {
            try {
                modbusClient.Connect();
            } catch (IOException ex) {
                Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
        }
        return success;
    }

    public boolean init(String port) {
        if ((modbusClient != null) && modbusClient.isConnected()) {
            return true;
        }
        modbusClient = new ModbusClient(port, 115200, 8);

        try {
            modbusClient.Connect();
        } catch (IOException ex) {
            Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
        }
        return modbusClient.isConnected();
    }
    
    public void disconnect() {
        try {
            modbusClient.Disconnect();
        } catch (IOException | SerialPortException ex) {
            Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
