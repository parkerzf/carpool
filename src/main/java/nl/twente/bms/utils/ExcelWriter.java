package nl.twente.bms.utils;

import nl.twente.bms.model.ModelStats;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.*;

public class ExcelWriter {

    public static void writeOutput(String filename, String algorithmName, int iterations) {
        String[] header = {"algorithm name", "unit taxi cost", "iterations", "CPU time", "total cost", "taxi cost",
                "self-driving cost", "covered distance"};
        Object[] instance = {algorithmName, Utils.TAXI_EUR_PER_KM, iterations,
                ModelStats.runTime, ModelStats.totalCost, ModelStats.taxiCostTotal,
                ModelStats.totalCost - ModelStats.taxiCostTotal, ModelStats.distanceSavingTotal
        };

        try {

            FileInputStream inputStream = new FileInputStream(new File(filename));
            Workbook workbook = WorkbookFactory.create(inputStream);


            int rowNum;
            Sheet sheet = workbook.getSheet("Algo Output");

            if(sheet == null){
                sheet = workbook.createSheet("Algo Output");
                Row row = sheet.createRow(0);
                int colNum = 0;
                for (String field : header) {
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(field);
                }
                rowNum = 1;
            }
            else{
                rowNum = sheet.getLastRowNum() + 1;
            }

            Row row = sheet.createRow(rowNum);

            int colNum = 0;
            for (Object field : instance) {
                Cell cell = row.createCell(colNum++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if(field instanceof Double) {
                    cell.setCellValue((Double) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }

            inputStream.close();

            FileOutputStream out = new FileOutputStream(filename);
            workbook.write(out);
            workbook.close();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}