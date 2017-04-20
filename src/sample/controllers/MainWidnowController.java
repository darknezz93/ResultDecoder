package sample.controllers;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import sample.helpers.FileHelper;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class MainWidnowController {

    @FXML
    private TextField fileNameTextField;

    @FXML
    private Button loadFileButton;

    @FXML
    private Button saveResultsButton;

    private FileHelper fileHelper;


    @FXML
    public void initialize() throws MalformedURLException {
        resetComponents();
        initializeLoadFileButton();
        initializeSaveResultsButton();
    }

    private void initializeLoadFileButton() {
        loadFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fileHelper = new FileHelper();
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File directory =
                        directoryChooser.showDialog(((Node)event.getTarget()).getScene().getWindow());
                if(directory != null) {
                    fileNameTextField.setText(directory.getName());
                    fileHelper.processDirectory(directory);
                    saveResultsButton.setDisable(false);
                }
            }
        });
    }

    private void initializeSaveResultsButton() {
        saveResultsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();

                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
                fileChooser.getExtensionFilters().add(extFilter);
                fileChooser.setInitialFileName("wyniki.csv");

                File file = fileChooser.showSaveDialog(((Node)event.getTarget()).getScene().getWindow());
                if(file != null){
                    if(!file.getName().contains(".")) {
                        file = new File(file.getAbsolutePath() + ".csv");
                    }
                    try {
                        fileHelper.saveCsvFile(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void resetComponents() {
        fileNameTextField.setDisable(true);
        saveResultsButton.setDisable(true);
    }


}
