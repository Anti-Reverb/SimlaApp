package com.example.aryanshaikh.simlaapp;

import android.Manifest;
import android.inputmethodservice.Keyboard;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    private static final String TAG = "dashboard";

    private String[] FilePathStrings;
    private String[] FileNameStrings;
    private File[] ListFile;
    File file;

    Button btnUpDirectory, btnSDCard;

    ArrayList<String> pathHistory;
    String lastDirectory;
    int count = 0;

    ArrayList<XYValues> uploadData;

    ListView lvInternalStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        lvInternalStorage= findViewById(R.id.lvInternalStorage);
        btnSDCard= findViewById(R.id.btnSDCard);
        btnUpDirectory= findViewById(R.id.btnUpDirectory);
        uploadData = new ArrayList<>();

        //need to check file permissions:
        checkFilePermission();

        lvInternalStorage.setOnClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l){
                 lastDirectory = pathHistory.get(count);
                 if(lastDirectory.equals(adapterView.getItemAtPosition(i))){
                     Log.d(TAG, "lvInternalStorage: Select a file for upload: "+lastDirectory);

                     //execute method to read the excel data:
                     readExcelData(lastDirectory);
                 }
                 else {
                     count++;
                     pathHistory.add(count, (String) adapterView.getItemAtPosition(i));
                     checkInternalStorage();
                     Log.d(TAG, "lvInternalStorage : " + pathHistory.get(count));
                 }
            }
        });
    }

    private void readExcelData(String filePath){
        Log.d(TAG, "readExcelData : reading excel file.");

        //Declare input file
        File inputFile= new File(filePath);
        try{
            InputStream inputStream = new FileInputStream(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = XSSFWorkbook.getSheetAt(0);
            int rowsCount = Sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator= workbook.getCreationHelper().getFormulaEvaluator();
            StringBuilder sb = new StringBuilder();

            //Outer loop; loops through rows
            for (int r = 1; r< rowsCount; r++){
                Row row = sheet.getRow();
                int cellsCount = row.getPhysicalNumberOfCells();
                //inner loop; loops through columns
                for(int c=0; c<cellsCount; c++){
                    String value = getCellAsString(row, c, formulaEvaluator);
                    String cellInfo = "r: "+r+",c: "+c+",v: "+value;
                    Log.d(TAG, "readExcelData : Data from row: "+cellInfo);
                    sb.append(value + ", ");
                }

                sb.append(":");

                StringBuilder sb = "x1,y1:x2,y2:x3,y3:x4,y4";
            }
            Log.d(TAG, "readExcelData : STRINGBUILDER : " + sb.toString());

            parseStringBuilder(sb);
        }
        catch (FileNotFoundException e){
            Log.e(TAG, "readExcelData : FileNotFoundException " + e.getMessage());
        }
        catch(IOException e){
            Log.e(TAG, "readExcelData : IOException "+ e.getMessage());
        }
    }

    public void parseStringBuilder(StringBuilder mStringBuilder){
        Log.d(TAG, "parseStringBuilder: Started Parsing. ");

        // splits sb into rows
        String[] rows = mStringBuilder.toString().split(":");

        //Add to ArrayList<XYValues> row by row
        for(int i = 0; i < rows.length; i++){
            //split the columns of the rows
            String[] columns = rows[i].split(",");

            //use try catch block to make sure that there are no "" that try to parse in doubles
            try{
                Double x = Double.parseDouble(columns[0]);
                Double y = Double.parseDouble(columns[1]);

                String cellInfo = "(x,y): (" + x +"," + y + " )";
                Log.d(TAG, "parseStringBuilder : data from row : " + cellInfo);

                //add uploadData to ArrayList
                uploadData.add(new XYValue(x,y));
            }
            catch(NumberFormatException e){
                Log.e(TAG, "parseStringBuilder : NumberFormatException.");

            }
        }
        printDataToLog();
    }

    private void printDataToLog(){
        Log.d(TAG, "printDataToLog : printing data to log.");

        for(int i = 0; i<uploadData.size(); i++){
            Double x = uploadData.get(i).getX();
            Double y = uploadData.get(i).getY();
            Log.d(TAG, "printing data to log : (x,y) : ("+x+","+y+")");
        }
    }

    private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator){
        String value = "";
        try{
            Cell cell = row.getCell(c);
            CellValue Cell = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()){
                case cell.CELL_TYPE_BOOLEAN:
                    value=""+cellValue.getBooleanValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    double numericValue = cellValue.getNumericValue();
                    if(HSSFDateUtil.isCellDateFormatted(cell)){
                        double date = cellValue.getNumericValue();
                        SimpleDateFormat formatter = new SimpleDateFormat(dd/mm/yy);
                        formatter.format(HSSfDateUtilgetJavaDate(date));
                    }
                    else{
                        value=""+numericValue();
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = ""+cellValue.getStringValue();
                    break;
                default:
            }
        }
        catch (NullPointerException e){
            Log.e(TAG, "getCellAsString : NullPointerException : " + e.getMessage());
        }
        return value;
    }

    private void checkInternalStorage(){
        Log.d(TAG, "checkInternalStorage : Started ");
        try{
            if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                toastMessage("no media found");
            }
            else {
                file = new File(pathHistory.get(count));
                Log.d(TAG, "checkInternalStorage : directory path : " + pathHistory.get(count));
            }

            ListFile = file.listFiles();

            //Create a String array for FilePathStrings:
            FilePathStrings = new String[ListFile.length];

            //Create a String array for FileNameStrings:
            FileNameStrings = new String[ListFile.length];

            for(int i = 0; i < ListFile.length; i++){
                //get the path of the image file
                FilePathStrings[i]=ListFile[i].getAbsolutePath();
                //get the name of the image file
                FileNameStrings[i]=ListFile[i].getName();
            }

            for(int i = 0 ; i < ListFile.length ; i++){
                Log.d("Files", "FileName: "+ListFile[i].getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FilePathStrings);
            lvInternalStorage.setAdapter(adapter);


        }
        catch (NullPointerException e){
            Log.e(TAG, "checkInternalStorage : NullPointerException " + e.getMessage());
        }
    }

    private void checkFilePermission(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");

            if(permissionCheck != 0){
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);

            }
        }else{
            Log.d("checkBTPermissions: No need to check permission, SDK version < Lollipop. ");
        }
    }

    private void toastMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT);
    }
}
