package com.mycompany.xmljson_converter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.stage.FileChooser;

public class SceneController implements Initializable {

    private boolean selectedBtn = false;
    @FXML private Button clearXmlBtn, clearJsonBtn, uploadBtn, copyBtn, downloadBtn, Xml_JSonBtn, JSON_XMLBtn;
    @FXML private ComboBox<String> spaceCombo;
    @FXML private TextArea xmlInput_Output, jsonOutput_Input;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // 2. Clear Buttons
        clearXmlBtn.setOnAction(e -> xmlInput_Output.clear());
        clearJsonBtn.setOnAction(e -> jsonOutput_Input.clear());

        // 3. UPLOAD FUNCTION
        uploadBtn.setOnAction(event -> handleUpload());

        // 4. XML to JSON
        Xml_JSonBtn.setOnAction(event -> {
            selectedBtn = false; // Logic: Mode is now XML -> JSON
            String input = xmlInput_Output.getText();
            if (input.isEmpty()) {
                showWindowStatus("Input Required", "Please enter XML before converting.", AlertType.WARNING);
                return;
            }
            try {
                
                    jsonOutput_Input.setText(Transformer.convertWithJackson(input));
            } catch (Exception e) {
                showWindowStatus("Conversion Error", "The XML is malformed: " + e.getMessage(), AlertType.ERROR);
            }
        });

        // 5. JSON to XML
        JSON_XMLBtn.setOnAction(event -> {
            selectedBtn = true; // Logic: Mode is now JSON -> XML
            String json = jsonOutput_Input.getText();
            if (json.isEmpty()) {
                showWindowStatus("Input Required", "Please enter JSON before converting.", AlertType.WARNING);
                return;
            }
            try {
                    xmlInput_Output.setText(Transformer.convert(jsonOutput_Input.getText(), "root"));
                } catch (Exception e) {
                showWindowStatus("Conversion Error", "The JSON is malformed: " + e.getMessage(), AlertType.ERROR);
            }
        });

        // 6. Copy to Clipboard
        copyBtn.setOnAction(event -> {
            if (!selectedBtn) {
                if (!jsonOutput_Input.getText().isEmpty()) {
                    jsonOutput_Input.selectAll();
                    jsonOutput_Input.copy();
                } else {
                    showWindowStatus("Nothing to Copy", "The JSON output area is empty.", AlertType.WARNING);
                }
            } else {
                if (!xmlInput_Output.getText().isEmpty()) {
                    xmlInput_Output.selectAll();
                    xmlInput_Output.copy();
                } else {
                    showWindowStatus("Nothing to Copy", "The XML output area is empty.", AlertType.WARNING);
                }
            }
        });

        // 7. Download
        downloadBtn.setOnAction(event -> handleDownload());
    }

    private void handleUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Supported Files", "*.xml", "*.json"),
            new FileChooser.ExtensionFilter("XML Files", "*.xml"),
            new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );

        File selectedFile = fileChooser.showOpenDialog(uploadBtn.getScene().getWindow());
        if (selectedFile != null) {
            try {
                String fileName = selectedFile.getName().toLowerCase();
                String content = Files.readString(selectedFile.toPath());

                if (fileName.endsWith(".xml")) {
                    xmlInput_Output.setText(content);
                } else if (fileName.endsWith(".json")) {
                    jsonOutput_Input.setText(content);
                }
            } catch (IOException ex) {
                showWindowStatus("Load Error", "Error reading file: " + ex.getMessage(), AlertType.ERROR);
            }
        }
    }

    private void handleDownload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Converted File");

        String dataToSave;
        String defaultFileName;

        if (!selectedBtn) {
            dataToSave = jsonOutput_Input.getText();
            defaultFileName = "converted_data.json";
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        } else {
            dataToSave = xmlInput_Output.getText();
            defaultFileName = "converted_data.xml";
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        }

        // Prevent opening dialog if there is nothing to save
        if (dataToSave.isEmpty()) {
            showWindowStatus("Save Error", "No converted data available to save!", AlertType.ERROR);
            return; 
        }

        fileChooser.setInitialFileName(defaultFileName);
        File file = fileChooser.showSaveDialog(downloadBtn.getScene().getWindow());

        if (file != null) {
            try {
                Files.writeString(file.toPath(), dataToSave);
            } catch (IOException e) {
                showWindowStatus("Save Failed", "Error saving file: " + e.getMessage(), AlertType.ERROR);
            }
        }
    }

    private void showWindowStatus(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    
}