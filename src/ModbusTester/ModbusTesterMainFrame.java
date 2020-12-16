/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester;

import ModbusTester.utils.FileUtils;
import ModbusTester.device.ParserCSV;
import ModbusTester.parameter.Parameter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author s.bikov
 */
public class ModbusTesterMainFrame extends JFrame implements ActionListener {

    //   ModbusClient modbusClient;
    JTable resultTable;
    DefaultTableModel tableModel;
    ParserCSV parser;
    ReadWriteRegisters tester;

    JButton openButton, testButton, saveButton;
    JTextField ipTextField;
    JFormattedTextField formattedTextField;
//    JComboBox serialTcpList;
//    public static final String[] SERIAL_TCP_ITEMS = {"TCP", "serial"};

    public static final String EXTENSION = ".html";

    public static void main(String[] args) {
        new ModbusTesterMainFrame();
    }

    public ModbusTesterMainFrame() {
        super("modbus tester");

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

        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout());

//        serialTcpList = new JComboBox(SERIAL_TCP_ITEMS);
//        serialTcpList.addActionListener(this);
//        northPanel.add(serialTcpList);
        openButton = new JButton("open");
        openButton.addActionListener(this);
        northPanel.add(openButton);

        ipTextField = new JTextField();
        ipTextField.setColumns(9);
        northPanel.add(ipTextField);

        try {
            MaskFormatter mf = new MaskFormatter("##.#.##.##");
            formattedTextField = new JFormattedTextField(mf);
        } catch (ParseException ex) {
            Logger.getLogger(ModbusTesterMainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        testButton = new JButton("test");
        testButton.setEnabled(false);
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean enabled = false;
                if (tester == null) {
                    tester = new ReadWriteRegisters();
                }
                if (parser.protocol.matches("Modbus TCP")) {
                    enabled = tester.init(ipTextField.getText(), 502);
                }

                if (parser.protocol.matches("Modbus RTU")) {
                    enabled = tester.init("COM4");
                    System.out.println("enabled = " + enabled);
                }

                if (enabled) {
                    tester.test(parser.getParameterArray());
                }
                saveButton.setEnabled(true);
            }
        });
        northPanel.add(testButton);

        saveButton = new JButton("save");
        saveButton.setEnabled(false);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileNameExtensionFilter filter = new FileNameExtensionFilter("html files (*.html)", EXTENSION);
                JFileChooser fc = new JFileChooser();
                fc.setFileFilter(filter);
                if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = new File(fc.getSelectedFile() + EXTENSION);
                    try (FileWriter fw = new FileWriter(file)) {
                        fw.write(tester.resultString.toString());
                    } catch (IOException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            }
        });
        northPanel.add(saveButton);
        add(northPanel, BorderLayout.NORTH);
        String[] colNames = {"parameter", "address", "readResult", "writeResult"};
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(colNames);
        resultTable = new JTable(tableModel);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);
        setVisible(true);
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
                parser = new ParserCSV(str);
                testButton.setEnabled(true);

//                saveButton.setEnabled(true);
                ipTextField.setText(parser.currentIP);

                tableModel.setRowCount(0);

                for (Parameter param : parser.getParameterArray()) {
                    Object obj[] = {param.name, param.address, "stub", "stub"};
                    tableModel.addRow(obj);
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Parameter> tmpArray;
                        while (true) {
                            try {
                                tmpArray = parser.getParameterArray();
                                for (int i = 0; i < tmpArray.size(); i++) {
                                    tableModel.setValueAt("stub", i, 2);
                                    tableModel.setValueAt("stub", i, 3);
                                }
                                Thread.sleep(200);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(ModbusTesterMainFrame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }).start();
            }

        }
//        if (e.getSource() == serialTcpList) {
//            if (serialTcpList.getSelectedIndex() == 0);
//        }
    }

}
