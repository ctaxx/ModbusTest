/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester;

import ModbusTester.device.Device;
import ModbusTester.device.ParserCSV;
import ModbusTester.parameter.Parameter;
import ModbusTester.tasks.DataPackageWriter;
import ModbusTester.tasks.Task;
import ModbusTester.utils.FileUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import static java.lang.Thread.sleep;
import javafx.geometry.Orientation;
import javafx.scene.layout.TilePane;

/**
 * FXML Controller class
 *
 * @author s.bikov
 */
public class FXMLDocController implements Initializable {

    private static final double ITEMS_WIDTH = 180.0;
    private static final double ITEMS_GAPS = 2.0;

    FileChooser fileChooser = new FileChooser();

    public DataPackageWriter dataPackageWriter = new DataPackageWriter();
    public ReadWriteRegisters readWriteRegisters = new ReadWriteRegisters();

    Task activeTask;

    TilePane itemsVBox;
//    Double progress = 0.0;

    @FXML
    TableView readWriteRegsTable;

    @FXML
    Button readWriteRegsTestButton;

    @FXML
    Button readWriteRegsSaveButton;

    Device activeDevice;

    private boolean locked = false;

    @FXML
    private TextField ipField;

    @FXML
    private Label label;

    @FXML
    private BorderPane anchorPane;

    @FXML
    private VBox devicesVBox;

    @FXML
    private ProgressBar taskProgress;

    @FXML
    private Button cancelButton;

    @FXML
    private StackPane activeCenterStack;

    @FXML
    private VBox menuVBox;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        startProgressBarUpdater();
        taskProgress.setProgress(0.0);
    }

    public void initialize() {

    }

    @FXML
    private void handleReadWriteRegsAction(ActionEvent event) {
        if (activeDevice != null && readWriteRegsTable.getColumns().isEmpty()) {
//                TODO check before adding items

            ObservableList<Parameter> pList = FXCollections.observableArrayList(activeDevice.parametersArray);
            readWriteRegsTable.setItems(pList);

            TableColumn<Parameter, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableColumn<Parameter, Integer> addressCol = new TableColumn<>("Address");
            addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

            readWriteRegsTable.getColumns().addAll(nameCol, addressCol);
        }
        setNodeToFront("readWriteRegsPane");
        activeTask = readWriteRegisters;
    }

    @FXML
    private void handleReadWriteRegsTestButtonAction(ActionEvent event) {
        if (activeDevice != null) {
            showProgress();
            readWriteRegsTestButton.setDisable(true);
            readWriteRegisters.init();

            new Thread(() -> {
                readWriteRegisters.test(activeDevice.parametersArray);
                readWriteRegisters.disconnect();
                readWriteRegsTestButton.setDisable(false);
                hideProgress();
                readWriteRegsSaveButton.setDisable(false);
            }).start();

        }
        System.out.println("going to test");
    }

    @FXML
    private void handleReadWriteRegsSaveButtonAction(ActionEvent event) {
        fileChooser.setTitle("Save result file");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("HTML", "*.html")
        );
        File dest = fileChooser.showSaveDialog(anchorPane.getScene().getWindow());
        if (dest == null) {
            return;
        }
        try (FileWriter fw = new FileWriter(dest + ".hmtl")) {
            fw.write(readWriteRegisters.resultString.toString());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (itemsVBox == null) {
            setTopStackItemToUnvisible();

            itemsVBox = new TilePane(Orientation.VERTICAL);
            itemsVBox.setVgap(ITEMS_GAPS);
            itemsVBox.setHgap(ITEMS_GAPS);
            itemsVBox.setId("itemsVBox");
            itemsVBox.setPrefWidth(ITEMS_WIDTH);

            for (int i = 0; i < dataPackageWriter.itemArrayList.size(); i++) {
                PackageItem item = dataPackageWriter.itemArrayList.get(i);
                Button tmpButton = new Button(item.name);
                tmpButton.setMinWidth(ITEMS_WIDTH);
                int[] tmpArr = item.paramArray.stream().mapToInt(e -> (int) e.address).toArray();
                long[] tmpVal = item.paramArray.stream().mapToLong(e -> (long) e.value).toArray();
                tmpButton.setOnAction(e -> {
                    if (locked) {
                        return;
                    }
                    locked = true;
                    activeTask = dataPackageWriter;
                    showProgress();

                    dataPackageWriter.init();

                    new Thread(() -> {
                        dataPackageWriter.writeData(tmpArr, tmpVal);
                        dataPackageWriter.disconnect();
                        locked = false;
                        hideProgress();
                    }).start();
                }
                );
                itemsVBox.getChildren().add(tmpButton);
            }
            activeCenterStack.getChildren().add(itemsVBox);

        } else {
            setNodeToFront("itemsVBox");
        }
    }

    @FXML
    private void handleAddDeviceAction(ActionEvent event) {
        fileChooser.setTitle("Open device file");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV", "*.csv")
        );
        File devFile = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());
        if (devFile == null) {
            return;
        }

        ParserCSV parser = new ParserCSV(FileUtils.fileReader(devFile));

        activeDevice = parser.getDevice();
//        TODO avoid hardcoding
        readWriteRegisters.setDevice(activeDevice);
        dataPackageWriter.setDevice(activeDevice);
        ipField.setText(parser.getDevice().ipAddress);
    }

    @FXML
    private void handleCancelAction(ActionEvent event) {
        activeTask.enableDoingDeals = false;
    }

    @FXML
    private void handleSideMenuButtonAction(ActionEvent event) {
        if (menuVBox.isVisible()) {
            menuVBox.setMinWidth(0.0);
            menuVBox.setVisible(false);
        } else {
            menuVBox.setMinWidth(70.0);
            menuVBox.setVisible(true);
        }
    }

    private void setNodeToFront(String id) {
//        TODO divide states visible and onFront
        ObservableList<Node> children = activeCenterStack.getChildren();
        if (children.size() > 0) {
            Node topChild = children.get(children.size() - 1);
            if (id.equals(topChild.getId()) && topChild.isVisible()) {
//                System.out.println("Returned: top equals applicant");
                return;
            }

            Node applicantChild = children
                    .stream()
                    .filter(f -> id.equals(f.getId()))
                    .findFirst().orElse(null);

            if (applicantChild != null) {
                topChild.setVisible(false);
                topChild.toBack();

                applicantChild.toFront();
                applicantChild.setVisible(true);
            }
        }
    }

    private void setTopStackItemToUnvisible() {
        ObservableList<Node> children = activeCenterStack.getChildren();
        if (children.size() > 0) {
            Node topChild = children.get(children.size() - 1);
            topChild.setVisible(false);
            topChild.toBack();
        }
    }

//    TODO pause thread
    private void startProgressBarUpdater() {
        Thread progressUpdater = new Thread(() -> {
            while (true) {
//                Platform.runLater(() -> taskProgress.setProgress(dataPackageWriter.progress));
                if (activeTask != null) {
                    taskProgress.setProgress(activeTask.progress);
                }
                try {
                    sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FXMLDocController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        progressUpdater.setDaemon(true);
        progressUpdater.start();
    }

    private void showProgress() {
        taskProgress.setVisible(true);
        cancelButton.setVisible(true);
    }

    private void hideProgress() {
        taskProgress.setVisible(false);
        cancelButton.setVisible(false);
    }
}
