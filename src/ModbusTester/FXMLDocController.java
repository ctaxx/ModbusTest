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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author s.bikov
 */
public class FXMLDocController implements Initializable {

    @FXML
    private Label label;

    @FXML
    private FlowPane anchorPane;

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

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        buttons = new ArrayList<>();
        VBox vBox = new VBox();
        DataPackageWriter dataPackageWriter = new DataPackageWriter(true);
        dataPackageWriter.initItems();
        for (int i = 0; i < dataPackageWriter.itemArrayList.size(); i++) {
            PackageItem item = dataPackageWriter.itemArrayList.get(i);
            Button tmpButton = new Button(item.name);
//            System.out.println("packageItem name is " + item.name);
            int [] tmpArr = item.paramArray.stream().mapToInt(e -> (int)e.address).toArray();
            int [] tmpVal = item.paramArray.stream().mapToInt(e -> (int)e.value).toArray();
            tmpButton.setOnAction(e -> {System.out.println(item.name + " clicked");
                                        dataPackageWriter.writeData(tmpArr, tmpVal);});
            buttons.add(tmpButton);
        }
        buttons.stream().forEach((b) -> {
            vBox.getChildren().add(b);
        });

        anchorPane.getChildren().add(vBox);
    }

}
