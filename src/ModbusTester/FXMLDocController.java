/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester;

import ModbusTester.tasks.DataPackageWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author s.bikov
 */
public class FXMLDocController implements Initializable {

    private static final double ITEMS_WIDTH = 180.0;

    @FXML
    private Label label;

    @FXML
    private VBox anchorPane;

    @FXML
    private VBox itemsVBox;

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
    }

    public void initialize() {

    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        if (buttons != null) {
            return;
        }
        buttons = new ArrayList<>();
//        VBox itemsVBox = new VBox();
        itemsVBox.setPrefWidth(ITEMS_WIDTH);
        DataPackageWriter dataPackageWriter = new DataPackageWriter();

        for (int i = 0; i < dataPackageWriter.itemArrayList.size(); i++) {
            PackageItem item = dataPackageWriter.itemArrayList.get(i);
            Button tmpButton = new Button(item.name);
            tmpButton.setMinWidth(ITEMS_WIDTH);
//            System.out.println("packageItem name is " + item.name);
            int[] tmpArr = item.paramArray.stream().mapToInt(e -> (int) e.address).toArray();
            long[] tmpVal = item.paramArray.stream().mapToLong(e -> (long) e.value).toArray();
            tmpButton.setOnAction(e -> {
                System.out.println(item.name + " clicked");
                dataPackageWriter.init();
                dataPackageWriter.writeData(tmpArr, tmpVal);
                dataPackageWriter.disconnect();
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
    }
}
