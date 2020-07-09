/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusRTU;

import ModbusRTU.parameter.ParserCSV;
import ModbusRTU.parameter.Parameter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
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
    ModbusTCPTester tester;

    JButton openButton, testButton, saveButton;
    JTextField ipTextField;
    JFormattedTextField formattedTextField;

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
                tester = new ModbusTCPTester();
                if (tester.init(ipTextField.getText(), 502)) {
                    tester.test(parser.getParameterArray());
                }
            }
        });
        northPanel.add(testButton);

        saveButton = new JButton("save");
        saveButton.setEnabled(false);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                saveStatus();
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

                saveButton.setEnabled(true);
                ipTextField.setText(parser.currentIP);

                tableModel.setRowCount(0);

                for (Parameter param : parser.getParameterArray()) {
                    Object obj[] = {param.name, param.address, param.readResult, param.writeResult};
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
                                    tableModel.setValueAt(tmpArray.get(i).readResult, i, 2);
                                    tableModel.setValueAt(tmpArray.get(i).writeResult, i, 3);
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
    }

}
