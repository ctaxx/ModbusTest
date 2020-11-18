/*
 * Collecting the data from one register and save it into .CSV file
 */
package ModbusTester.tasks;

import ModbusTester.utils.FuncException;
import ModbusRTU.ModbusClient;
import de.re.easymodbus.datatypes.RegisterOrder;
import de.re.easymodbus.exceptions.ModbusException;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.UnsupportedLookAndFeelException;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

/**
 *
 * @author s.bikov
 */
public class DataCollector extends JFrame {

    ModbusClient modbusClient;
    public static final String EOL = System.lineSeparator();

    public static void main(String[] args) {
        new DataCollector();
    }

    public DataCollector() {
        super("DataCollector");
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(DataCollector.class.getName()).log(Level.SEVERE, null, ex);
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
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = new File("D:\\wrk\\result.csv");
                try (FileWriter writer = new FileWriter(file);) {
                    for (int i = 0; i < 1000; i++) {
                        Thread.sleep(450);
                        int[] dataArray = modbusClient.ReadHoldingRegisters(0, 2);
                        System.out.println(ModbusClient.ConvertRegistersToFloat(dataArray, RegisterOrder.LowHigh));
                        String f = Float.toString(ModbusClient.ConvertRegistersToFloat(dataArray, RegisterOrder.LowHigh));
                        
                        writer.write(f.replace(".", ","));
                        writer.append(";").append(EOL);
                    }
                    System.out.println("done");
                } catch (ModbusException | SerialPortException | SerialPortTimeoutException | FuncException ex) {
                    Logger.getLogger(DataCollector.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SocketException ex) {
                    Logger.getLogger(DataCollector.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(DataCollector.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DataCollector.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        add(startButton);

        setVisible(true);
    }

    public boolean init() {
        modbusClient = new ModbusClient("10.6.18.17", 503);
        boolean success = modbusClient.Available(2000);
        System.out.println("Available " + success);
        if (success) {
            try {
                modbusClient.Connect();
            } catch (IOException ex) {
                Logger.getLogger(DataCollector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(DataCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return success;
    }

    protected int dataArrayToInt(int[] dataArray) {
        int res = 0;
        for (int i = 0; i < dataArray.length; i++) {
            res |= (dataArray[i] << (i * 16));
        }
        return res;
    }

    protected long collectDataToLong(int[] dataArray) {
        return Integer.toUnsignedLong(dataArrayToInt(dataArray));
    }

    public String getValueString(int[] dataArray) {
        if (dataArray != null) {
            return Long.toString(collectDataToLong(dataArray));
        } else {
            return "-";
        }
    }
}
