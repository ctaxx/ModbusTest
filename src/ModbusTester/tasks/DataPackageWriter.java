/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester.tasks;

import ModbusRTU.ModbusClient;
import ModbusTester.PackageItem;
import ModbusTester.parameter.TmpParam;
import ModbusTester.utils.FuncException;
import com.google.gson.JsonArray;
import de.re.easymodbus.exceptions.ModbusException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author s.bikov
 */
public class DataPackageWriter extends JFrame {

    public static final String ITEMS_PATH = "D:\\buttons.json";
    public static final String ENCODING = "Cp1251";

    ModbusClient modbusClient;
    public ArrayList<PackageItem> itemArrayList;
    int[] targetAddresses = {570, 571};
    int[][] targetValues = {{500}, {500}};
    int initAddress = 570;
    int lastAddress = 593;
    int[] valueToWrite = {0};

    public DataPackageWriter() {
        itemArrayList = readJson(ITEMS_PATH);
    }

    public boolean init() {
        modbusClient = new ModbusClient("10.6.18.35", 502);
        boolean success = modbusClient.Available(1500);
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

    public ArrayList<PackageItem> readJson(String path) {
        ArrayList<PackageItem> packageItems = new ArrayList<>();
        try {
            List<String> bytes = Files.readAllLines(Paths.get(path), Charset.forName(ENCODING));
            
            for (String s : bytes) {
                JsonObject obj = new JsonParser().parse(s).getAsJsonObject();
                System.out.println(obj.get("name"));
                PackageItem packageItem = new PackageItem(obj.get("name").getAsString());
                packageItems.add(packageItem);
                
                JsonArray registersJsonArray = obj.get("regs").getAsJsonArray();
                for (int i = 0; i < registersJsonArray.size(); i++) {
                    JsonObject registerJson = registersJsonArray.get(i).getAsJsonObject();
                    packageItem.paramArray.add(new TmpParam(registerJson.get("address").getAsInt(),
                            registerJson.get("value").getAsInt()));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataPackageWriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DataPackageWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return packageItems;
    }

    public void writeData(int[] address, int[] valueToWrite) {
        try {
            for (int i = 0; i < address.length; i++) {
                Thread.sleep(350);
                modbusClient.WriteMultipleRegisters(address[i], new int[]{valueToWrite[i]});
            }
            System.out.println("done");
        } catch (ModbusException | SerialPortException | SerialPortTimeoutException | FuncException | InterruptedException ex) {
            Logger.getLogger(DataPackageWriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(DataPackageWriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DataPackageWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void disconnect() {
        try {
            modbusClient.Disconnect();
        } catch (IOException | SerialPortException ex) {
            Logger.getLogger(DataPackageWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
