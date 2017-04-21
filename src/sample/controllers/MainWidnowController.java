package sample.controllers;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import sample.helpers.FileHelper;

import java.io.File;
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
                try {
                    File directory =
                            directoryChooser.showDialog(((Node)event.getTarget()).getScene().getWindow());
                    if(directory != null) {
                        fileNameTextField.setText(directory.getName());
                        fileHelper.processDirectory(directory);
                        saveResultsButton.setDisable(false);
                    }
                } catch(Exception e) {
                    showErrorDialog();
                    resetComponents();
                }

            }
        });
    }

    private void initializeSaveResultsButton() {
        saveResultsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                File file = showCsvFileSaveDialog(event, "wyniki.csv",
                        "Zapisz plik z wynikami studentów");

                if(file != null){
                    if(!file.getName().contains(".")) {
                        file = new File(file.getAbsolutePath() + ".csv");
                    }
                    try {
                        fileHelper.saveCsvResultFile(file);
                        File validationFile = showCsvFileSaveDialog(event, "validation.csv",
                                "Zapisz plik z wynikami walidacji");
                        fileHelper.saveCsvValidationFile(validationFile);
                        showSuccessDialog();
                        resetComponents();
                    } catch (Exception e) {
                        showErrorDialog();
                        resetComponents();
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void showSuccessDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sukces");
        alert.setContentText("Wyniki zostały wygenerowane");
        alert.showAndWait();
    }

    private void showErrorDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setContentText("Wystąpił błąd podczas przetwarzania pliku. Sprawdź poprawność formatu wczytywanych plików");
        alert.showAndWait();
    }

    private File showCsvFileSaveDialog(ActionEvent event, String initialFileName,
                                          String dialogTitle) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName(initialFileName);
        fileChooser.setTitle(dialogTitle);

        File file = fileChooser.showSaveDialog(((Node)event.getTarget()).getScene().getWindow());
        return file;
    }


    private void resetComponents() {
        fileNameTextField.setText("");
        fileNameTextField.setDisable(true);
        saveResultsButton.setDisable(true);
    }


}
