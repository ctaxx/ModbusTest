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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.UnsupportedLookAndFeelException;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;

/**
 *
 * @author s.bikov
 */
public class DataPackageWriter extends JFrame {

    public static final String ITEMS_PATH = "D:\\buttons.json";

    ModbusClient modbusClient;
    public ArrayList<PackageItem> itemArrayList;
    int[] targetAddresses = {570, 571};
    int[][] targetValues = {{500}, {500}};
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
//            writeData(initAddress, valueToWrite);
            System.out.println("unsupported");
        });
        add(startButton);

        setVisible(true);
    }

    public DataPackageWriter(boolean n) {

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

    public void initItems() {
        itemArrayList = readJson(ITEMS_PATH);
    }

    public ArrayList<PackageItem> readJson(String path) {
        ArrayList<PackageItem> btnArr = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
            String s;
            StringBuilder bldr = new StringBuilder();
            while ((s = reader.readLine()) != null) {
                bldr.append(s).append("\n");
                JsonObject obj = new JsonParser().parse(s).getAsJsonObject();
                System.out.println(obj.get("name"));
                PackageItem packageItem = new PackageItem(obj.get("name").getAsString());
                btnArr.add(packageItem);
                JsonArray registersJsonArray = obj.get("regs").getAsJsonArray();
//                System.out.println("registers js array " + registersJsonArray.size());
                for (int i = 0; i < registersJsonArray.size(); i++) {
                    JsonObject registerJson = registersJsonArray.get(i).getAsJsonObject();
//                    System.out.println("json reg - number " + registerJson.get("address"));
                    packageItem.paramArray.add(new TmpParam(registerJson.get("address").getAsInt(),
                            registerJson.get("value").getAsInt()));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataPackageWriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DataPackageWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return btnArr;
    }

    public void writeData(int[] address, int[] valueToWrite) {
        try {
//            for (int i = 0; i < address.length; i++) {
//                Thread.sleep(450);
//                int [] tmpIntArr = new int[1];
//                tmpIntArr[0] = 0;
//                modbusClient.WriteMultipleRegisters(570, tmpIntArr);
//            }
            for (int i = initAddress; i <= lastAddress; i++) {
                Thread.sleep(450);
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
    }

    public static void main(String[] args) {
        new DataPackageWriter().readJson(ITEMS_PATH);
    }
}
