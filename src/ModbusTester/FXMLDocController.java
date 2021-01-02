/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester;

import ModbusTester.device.ParserCSV;
import ModbusTester.tasks.DataPackageWriter;
import ModbusTester.utils.FileUtils;
import static java.lang.Thread.sleep;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author s.bikov
 */
public class FXMLDocController implements Initializable {

    private static final double ITEMS_WIDTH = 180.0;
    public DataPackageWriter dataPackageWriter = new DataPackageWriter();
    private boolean locked = false;
    private boolean activeTask;

    @FXML
    private TextField ipField;

    @FXML
    private Label label;

    @FXML
    private BorderPane anchorPane;

    @FXML
    private VBox itemsVBox;
    
    @FXML
    private VBox devicesVBox;

    @FXML
    private ProgressBar taskProgress;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private StackPane activeCenterStack;

    ArrayList<Button> buttons;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        taskProgress.setProgress(0.0);
    }

    public void initialize() {

    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        if (buttons != null) {
            return;
        }
//        DoubleProperty barUpdater = new SimpleDoubleProperty(dataPackageWriter.progress);
//        System.out.println("for binding" + dataPackageWriter.progress);
//        taskProgress.progressProperty().bind(barUpdater);

        Thread progressUpdater = new Thread(() -> {
            while (true) {
//                Platform.runLater(() -> taskProgress.setProgress(dataPackageWriter.progress));
                taskProgress.setProgress(dataPackageWriter.progress);
                try {
                    sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FXMLDocController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        progressUpdater.setDaemon(true);
        progressUpdater.start();
        buttons = new ArrayList<>();
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
                taskProgress.setVisible(true);
                cancelButton.setVisible(true);
                System.out.println(item.name + " clicked");
                dataPackageWriter.init();
                
                new Thread(() -> {
                    dataPackageWriter.writeData(tmpArr, tmpVal);
                    dataPackageWriter.disconnect();
                    locked = false;
                    taskProgress.setVisible(false);
                    cancelButton.setVisible(false);
                }).start();
            }
            );
            buttons.add(tmpButton);
        }
        buttons.stream().forEach((b) -> {
            itemsVBox.getChildren().add(b);
        });

//        anchorPane.getChildren().add(itemsVBox);
    }

    @FXML
    private void handleAddDeviceAction(ActionEvent event) {
        System.out.println("adding device");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open file");
        // TODO
        String str = FileUtils.fileReader(fileChooser.showOpenDialog(anchorPane.getScene().getWindow()));
        ParserCSV parser = new ParserCSV(str);
        dataPackageWriter.setDevice(parser.getDevice());
        ipField.setText(parser.getDevice().ipAddress);
    }
    
    @FXML
    private void handleCancelAction(ActionEvent event){
        dataPackageWriter.enableWriterFlag = false;
    }

}
