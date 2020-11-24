/*
 * Collecting the data from one register and save it into .CSV file
 */
package ModbusTester.tasks;

import ModbusTester.utils.FuncException;
import ModbusRTU.ModbusClient;
import ModbusTester.device.ParserCSV;
import ModbusTester.parameter.Parameter;
import ModbusTester.utils.FileUtils;
import de.re.easymodbus.datatypes.RegisterOrder;
import de.re.easymodbus.exceptions.ModbusException;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

/**
 *
 * @author s.bikov
 */
public class DataInspector extends JFrame implements ActionListener {

    ModbusClient modbusClient;

    JButton openButton;
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

    JPanel centerPanel;

    public static final String EOL = System.lineSeparator();

    ArrayList<Parameter> parameterArray;
    ArrayList<ParameterPanel> panelArray = new ArrayList();

    boolean isTesting = false;
    int[] dataArray;

    int errorCounter = 0;
    int responseCounter = 0;

    float originValue = 0f;
    float currentValue = 0f;
    float minValue = Float.MAX_VALUE;
    float maxValue = Float.MIN_VALUE;
    float range = 100f;
    float currErrorValue = 0f;
    float maxErrorValue = 0f;

    public static void main(String[] args) {
        //  new DataInspector();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DataInspector frame = new DataInspector();
                frame.setDefaultCloseOperation(DataInspector.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }

    public DataInspector() {
        super("DataInspector");
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(DataInspector.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        setSize(500, 280);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel northPanel = new JPanel();

        openButton = new JButton("open");
        openButton.addActionListener(this);
        northPanel.add(openButton);

        JButton initButton = new JButton("init");
        initButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                init("COM4");
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
                                ParameterPanel panel;
                                Parameter parameter;
                                for (int i = 0; i < parameterArray.size(); i++) {
                                    parameter = parameterArray.get(i);
                                    panel = panelArray.get(i);
                                    //     Thread.sleep(1);
                                    dataArray = modbusClient.ReadHoldingRegisters(parameter.address, parameter.numOfRegs);
                                    //          System.out.println(Float.intBitsToFloat(DataUtils.dataArrayToInt(dataArray)));
                                    //           currentValue = Float.intBitsToFloat(DataUtils.dataArrayToInt(dataArray));
                                    panel.currentValue = ModbusClient.ConvertRegistersToFloat(dataArray, parameter.registerOrder);
                                    panel.currentField.setText(Float.toString(panel.currentValue));
                                    panel.maxValue = Float.max(panel.maxValue, panel.currentValue);
                                    panel.maxField.setText(Float.toString(panel.maxValue));
                                    panel.minValue = Float.min(panel.minValue, panel.currentValue);
                                    panel.minField.setText(Float.toString(panel.minValue));

                                    panel.currErrorValue = Math.abs(panel.currentValue - panel.originValue) / panel.range * 100;
                           //         System.out.println(String.format("%3.3f", panel.currentValue));
                                    panel.currErrorField.setText(String.format("%3.3f", panel.currErrorValue));

                                    panel.maxErrorValue = Float.max((panel.maxValue - panel.originValue), (panel.originValue - panelArray.get(i).minValue)) / panelArray.get(i).range * 100;
                                    panel.maxErrorField.setText(String.format("%3.3f", panel.maxErrorValue));

                                }

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

        centerPanel = new JPanel();

        JPanel header = new HeaderPanel();

        centerPanel.add(header);

        add(centerPanel, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel();
        responseLabel = new JLabel("");
        southPanel.add(responseLabel);
        errorsLabel = new JLabel("");
        southPanel.add(errorsLabel);
        add(southPanel, BorderLayout.SOUTH);
        
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
                Logger.getLogger(DataInspector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(DataInspector.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(DataInspector.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(DataInspector.class.getName()).log(Level.SEVERE, null, ex);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openButton) {
            FileFilter filter = new FileNameExtensionFilter("csv", "csv");
            JFileChooser fileDialog = new JFileChooser("D:/");
            fileDialog.setFileFilter(filter);
            int ret = fileDialog.showDialog(this, "Open");

            if (ret == JFileChooser.APPROVE_OPTION) {
                String str = FileUtils.fileReader(fileDialog.getSelectedFile());
                ParserCSV parser = new ParserCSV(str);
                parameterArray = parser.getParameterArray();
                for (int i = 0; i < parameterArray.size(); i++) {
                    panelArray.add(new ParameterPanel());
                }
                for (int i = 0; i < panelArray.size(); i++) {
                    centerPanel.add(panelArray.get(i));
                }

            }
        }
    }
}
