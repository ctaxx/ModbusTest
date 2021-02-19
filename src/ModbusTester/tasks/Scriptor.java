/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester.tasks;

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
public class Scriptor extends Task {

    public static final String ITEMS_PATH = "D:\\script.json";
    public static final String ENCODING = "Cp1251";

    public ArrayList<PackageItem> itemArrayList;
    int[] valueToWrite = {0};

    public Scriptor() {
        itemArrayList = readJson(ITEMS_PATH);
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
                            registerJson.get("value").getAsLong()));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Scriptor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Scriptor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return packageItems;
    }

    public void writeData(int[] address, long[] valueToWrite) {
        double pauseSec = 5.;
        int numOfIterations = 1000;
        double progressStep = 2.0 / (address.length * numOfIterations);
        enableDoingDeals = true;
        try {
            for (int j = 0; j < numOfIterations / address.length; j++) {
                for (int i = 0; i < address.length; i++) {
                    if (!enableDoingDeals) {
                        return;
                    }
                    Thread.sleep((int)(pauseSec * 1000));
                    System.out.println(address[i] + "/" + Long.toHexString(valueToWrite[i]));
//                modbusClient.WriteMultipleRegisters(address[i], DataUtils.ConvertLongToRegisters(valueToWrite[i]));
                    modbusClient.WriteMultipleRegisters(address[i], device.getParameterByAddress(address[i]).prepareRegisterData(valueToWrite[i]));
                    progress = progress + progressStep;
                    System.out.println(progress + "%");
                    System.out.println(j * 2);
                }

            }
            System.out.println("done");
            progress = 0.0;
        } catch (ModbusException | SerialPortException | SerialPortTimeoutException | FuncException | InterruptedException ex) {
            Logger.getLogger(Scriptor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(Scriptor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Scriptor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
