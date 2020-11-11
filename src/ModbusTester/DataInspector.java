/*
 * Collecting the data from one register and save it into .CSV file
 */
package ModbusTester;

import ModbusTester.utils.FuncException;
import ModbusTester.utils.DataUtils;
import ModbusRTU.ModbusClient;
import de.re.easymodbus.exceptions.ModbusException;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UnsupportedLookAndFeelException;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

/**
 *
 * @author s.bikov
 */
public class DataInspector extends JFrame {

    ModbusClient modbusClient;

    JButton startButton;
    JButton stopButton;
    JButton setButton;
    JButton resetButton;

    JLabel responseLabel;
    JLabel errorsLabel;

    JTextField originField;
    JTextField maxField;
    JTextField currentField;
    JTextField minField;
    JTextField currErrorField;
    JTextField maxErrorField;

    public static final String EOL = System.lineSeparator();
    boolean isTesting = false;
    int[] dataArray;

    int errorCounter = 0;
    int responseCounter = 0;

    float originValue = 0f;
    float currentValue = 0f;
    float minValue = Float.MAX_VALUE;
    float maxValue =  Float.MIN_VALUE;
    float range = 1050f;
    float currErrorValue = 0f;
    float maxErrorValue = 0f;

    public static void main(String[] args) {
        new DataInspector();
    }

    public DataInspector() {
        super("DataInspector");
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(ModbusTesterMainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        setSize(300, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel northPanel = new JPanel();

        JButton initButton = new JButton("init");
        initButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                init("COM5");
                startButton.setEnabled(true);
            }
        });

        northPanel.add(initButton);

        startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isTesting = true;
                stopButton.setEnabled(true);
                startButton.setEnabled(false);
                responseCounter = 0;
                errorCounter = 0;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isTesting) {
                            try {
                           //     Thread.sleep(1);
                                dataArray = modbusClient.ReadHoldingRegisters(0xb00, 2);
                                //          System.out.println(Float.intBitsToFloat(DataUtils.dataArrayToInt(dataArray)));
                                currentValue = Float.intBitsToFloat(DataUtils.dataArrayToInt(dataArray));
                                currentField.setText(Float.toString(currentValue));
                                maxValue = Float.max(maxValue, currentValue);
                                maxField.setText(Float.toString(maxValue));
                                minValue = Float.min(minValue, currentValue);
                                minField.setText(Float.toString(minValue));

                                currErrorValue = Math.abs(currentValue - originValue) / range * 100;
                                System.out.println(String.format("%3.3f", currErrorValue));
                                currErrorField.setText(Float.toString(currErrorValue));

                                maxErrorValue = Float.max((maxValue - originValue), (originValue - minValue)) / range * 100;
                                maxErrorField.setText(Float.toString(maxErrorValue));

                                responseCounter++;

                            } catch (ModbusException | SerialPortException | SerialPortTimeoutException | IOException | FuncException ex) {
                                Logger.getLogger(DataInspector.class.getName()).log(Level.SEVERE, null, ex);
                                errorCounter++;
                     //       } catch (InterruptedException ex) {
                      //          Logger.getLogger(DataInspector.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            responseLabel.setText("rx = " + Integer.toString(responseCounter));
                            errorsLabel.setText("errs = " + Integer.toString(errorCounter));
                        }
                    }
                }).start();
            }
        });
        startButton.setEnabled(false);
        northPanel.add(startButton);

        stopButton = new JButton("stop");
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isTesting = false;
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        });
        stopButton.setEnabled(isTesting);
        northPanel.add(stopButton);

        add(northPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(7, 2));

        centerPanel.add(new JLabel("origin"));

        originField = new JTextField();
        //     originField.setText(Float.toString(originValue));
        centerPanel.add(originField);

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
        centerPanel.add(setButton);

        centerPanel.add(new JLabel("max"));
        maxField = new JTextField();
        centerPanel.add(maxField);
        centerPanel.add(new JLabel(""));

        centerPanel.add(new JLabel("current"));
        currentField = new JTextField();
        centerPanel.add(currentField);
        centerPanel.add(new JLabel(""));

        centerPanel.add(new JLabel("min"));
        minField = new JTextField();
        centerPanel.add(minField);
        centerPanel.add(new JLabel(""));

        centerPanel.add(new JLabel("currErr"));
        currErrorField = new JTextField();
        centerPanel.add(currErrorField);
        centerPanel.add(new JLabel("range=" + Float.toString(range)));

        centerPanel.add(new JLabel("maxErr"));
        maxErrorField = new JTextField();
        centerPanel.add(maxErrorField);
        centerPanel.add(new JLabel(""));

        responseLabel = new JLabel("");
        centerPanel.add(responseLabel);
        errorsLabel = new JLabel("");
        centerPanel.add(errorsLabel);
        centerPanel.add(new JLabel(""));

        add(centerPanel, BorderLayout.CENTER);
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
                Logger.getLogger(ReadWriteRegisters.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ReadWriteRegisters.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ReadWriteRegisters.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ReadWriteRegisters.class.getName()).log(Level.SEVERE, null, ex);
        }
        return modbusClient.isConnected();
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
