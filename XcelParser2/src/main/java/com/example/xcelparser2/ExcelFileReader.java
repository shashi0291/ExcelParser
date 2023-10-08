package com.example.xcelparser2;

import com.example.xcelparser2.database.FinalResult;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class ExcelFileReader {

    private static final String FINAL_SHEET_NAME = "Final Result";

    private final File file;
    private final FileType fileType;

    public ExcelFileReader(File file, FileType fileType) {
        this.file = file;
        this.fileType = fileType;
    }

    public Map<String, String> processCertificateIdAndNameExcel() {
        if (file == null || fileType == null) return Collections.emptyMap();

        HashMap<String, String > mapCertificationIdToName = new HashMap<>();

        try {
            FileInputStream fis = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet("CertificateIdAndName");

            int certificateIdColumn = 0;
            int certificateNameColumn = 1;

            for (Row row : sheet) {
                Cell cellCertificateId = row.getCell(certificateIdColumn);
                Cell cellCertificateName = row.getCell(certificateNameColumn);
                if (!cellCertificateId.getCellStyle().getHidden() && cellCertificateName != null) {
                    System.out.println(cellCertificateId.getStringCellValue() + " " + cellCertificateName.getRichStringCellValue());
                    String key = cellCertificateId.getStringCellValue();
                    String value = cellCertificateName.getStringCellValue();
                    mapCertificationIdToName.put(key, value);
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to parse the rows");
        }
        return mapCertificationIdToName;
    }

    public Map<String, String> processCertificateExcel(Map<String, String> mapCertificateIdToName) {
        if (file == null || fileType == null) return Collections.emptyMap();

        HashMap<String, String > mapEmpIdToCertificateId = new HashMap<>();

        try {
            FileInputStream fis = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            int empIdColumn = 0;
            int certificateIdColumn = 7;

            for (Row row : sheet) {
                Cell cellEmpId = row.getCell(empIdColumn);
                Cell cellCertificateId = row.getCell(certificateIdColumn);
                if (cellEmpId != null && cellCertificateId != null) {

                    String key = cellEmpId.getStringCellValue();
                    String existingValue = mapEmpIdToCertificateId.get(key);
                    String currentValue = cellCertificateId.getStringCellValue();
                    System.out.println(key + " " + currentValue);

                    if (mapCertificateIdToName.containsKey(currentValue)) {
                        if (existingValue != null) {
                            existingValue = existingValue + cellCertificateId.getStringCellValue() + ",";
                            mapEmpIdToCertificateId.put(key, existingValue);
                        } else {
                            mapEmpIdToCertificateId.put(key, currentValue);
                        }
                    }
                }
            }

            fis.close();
            workbook.close();

        } catch (Exception e) {
            System.out.println("Failed to parse the rows");
        }

        return mapEmpIdToCertificateId;
    }

    public Map<Date, String> processSortPlanExcel() {
        if (file == null || fileType == null) return Collections.emptyMap();

        Map<Date, String> mapSortTimeToEmpId = new TreeMap<>();

        try {
            FileInputStream fis = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            int empIdColumn = 0;
            int startTimeColumn = 4;

            for (Row row : sheet) {
                Cell cellEmpId = row.getCell(empIdColumn);
                Cell cellStartTime = row.getCell(startTimeColumn);
                if (cellEmpId != null
                        && cellStartTime != null
                        && cellEmpId.getCellType() == CellType.NUMERIC
                        && cellStartTime.getCellType() == CellType.NUMERIC
                ) {
                    int empId = (int) cellEmpId.getNumericCellValue();
                    Date startTime = cellStartTime.getDateCellValue();
                    System.out.println(empId + " " + startTime);
                    String value = mapSortTimeToEmpId.get(startTime);
                    if (value != null) {
                        value = value + "," + empId;
                        mapSortTimeToEmpId.put(startTime, value);
                    } else {
                        mapSortTimeToEmpId.put(startTime, String.valueOf(empId));
                    }
                }
            }

            fis.close();
            workbook.close();

        } catch (Exception e) {
            System.out.println("Failed to parse the rows");
        }

        return mapSortTimeToEmpId;
    }

    public void updateResultInExcel(List<FinalResult> finalResultList, Map<String, String> mapCertificateIdToName) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(inputStream);

            int finalSheetIndex = getSheetIndexByName(workbook, FINAL_SHEET_NAME);
            if (finalSheetIndex != -1) workbook.removeSheetAt(finalSheetIndex);

            Sheet sheetFinalResult = workbook.createSheet(FINAL_SHEET_NAME);
            Row row = sheetFinalResult.createRow(0);
            row.createCell(0).setCellValue("Shift Start Time");
            row.createCell(1).setCellValue("Certificate Id");
            row.createCell(2).setCellValue("Certification Name");
            row.createCell(3).setCellValue("Certification Count");

            int index = 0;
            for (FinalResult finalResult : finalResultList) {

                Row newRow = sheetFinalResult.createRow(index);
                newRow.createCell(0).setCellValue(finalResult.sortStartTime.toString()); // sort start time

                for (Map.Entry<String, Integer> entry : finalResult.mapCertificateIdToCount.entrySet()) {
                    String certificateId = entry.getKey();
                    String certificateName = mapCertificateIdToName.get(certificateId);
                    newRow.createCell(1).setCellValue(certificateId); // certificate id
                    newRow.createCell(2).setCellValue(certificateName); // certification name
                    newRow.createCell(3).setCellValue(entry.getValue()); // certification count
                    index++;
                    newRow = sheetFinalResult.createRow(index);
                }
            }

            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int getSheetIndexByName(Workbook workbook, String sheetName) {
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            if (workbook.getSheetName(i).equalsIgnoreCase(sheetName)) {
                return i;
            }
        }
        return -1; // Sheet not found
    }
}


