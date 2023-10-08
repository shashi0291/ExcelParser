package com.example.xcelparser2;

import com.example.xcelparser2.database.FinalResult;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.*;

public class HelloController {

    private Map<String, String> mapCertificateIdToName = new HashMap<>();
    private Map<String, String> mapEmpIdToCertificateId = new HashMap<>();
    private Map<Date, String> mapShiftTimeToEmpId = new TreeMap<>();

    @FXML
    private Label welcomeText;

    @FXML
    protected void onSelectCertificateIdAndNameButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Excel File");
        java.io.File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            // Process the selected file
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            ExcelFileReader excelFileReader = new ExcelFileReader(selectedFile, FileType.CERTIFICATION_ID_AND_NAME);
            mapCertificateIdToName = excelFileReader.processCertificateIdAndNameExcel();
        }
    }

    @FXML
    protected void onSelectCertificateButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Excel File");
        java.io.File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            // Process the selected file
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            ExcelFileReader excelFileReader = new ExcelFileReader(selectedFile, FileType.CERTIFICATE);
            mapEmpIdToCertificateId = excelFileReader.processCertificateExcel(mapCertificateIdToName);
        } else {
            System.out.println("File selection canceled");
        }
    }

    @FXML
    protected void onSelectShiftButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Excel File");
        java.io.File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            // Process the selected file
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            ExcelFileReader excelFileReader = new ExcelFileReader(selectedFile, FileType.SORT_PLAN);
            mapShiftTimeToEmpId = excelFileReader.processSortPlanExcel();
        } else {
            System.out.println("File selection canceled");
        }
    }

    @FXML
    protected void onProcessButtonClick() {

        List<FinalResult> finalResult = processResult();
        System.out.println(finalResult);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Excel File");
        java.io.File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            // Process the selected file
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            ExcelFileReader excelFileReader = new ExcelFileReader(selectedFile, FileType.SORT_PLAN);
            excelFileReader.updateResultInExcel(finalResult, mapCertificateIdToName);
        } else {
            System.out.println("File selection canceled");
        }
    }

    private List<FinalResult> processResult() {
        List<FinalResult> finalResultList = new ArrayList<>();

        for (Map.Entry<Date, String> entry : mapShiftTimeToEmpId.entrySet()) {

            Date sortStartTime = entry.getKey();
            String[] employeeIdList = entry.getValue().split(",");

            FinalResult result = new FinalResult();
            result.sortStartTime = sortStartTime;

            TreeMap<String, Integer> mapCertificateIdToCount = new TreeMap<>();
            for (String certificateId : mapCertificateIdToName.keySet()) {

                for (String employeeId: employeeIdList) {
                    String csvCertificateId = mapEmpIdToCertificateId.get(employeeId);

                    if (csvCertificateId != null) {
                        if (csvCertificateId.contains(certificateId)) {
                            int count = mapCertificateIdToCount.getOrDefault(certificateId, 0) + 1;
                            mapCertificateIdToCount.put(
                                    certificateId,
                                    count
                            );
                        } else {
                            mapCertificateIdToCount.put(
                                    certificateId,
                                    0
                            );
                        }
                    }
                }
                result.mapCertificateIdToCount = mapCertificateIdToCount;
            }

            finalResultList.add(result);
        }
        return finalResultList;
    }
}