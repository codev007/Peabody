package com.peabody.www.peabody;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.Window;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.peabody.www.peabody.beans.PaymentData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //-----------FILE HANDLING DECLARATION----------------------
    private static final String FILE_NAME="LastPayment.txt";
    File file;
    FileReader fileReader=null;
    FileWriter fileWriter=null;
    BufferedReader bufferedReader=null;
    BufferedWriter bufferedWriter=null;
    String response=null;

    //-----------DECLARATION----------------------
    private static final int REQUEST_CODE = 101;
    private ChipGroup chipGroupOne;
    private Button mSaveButton;
    private TextView mTotalAmount,mAddPayment;
    ArrayList<String> paymentMethod = new ArrayList<>();
    List<PaymentData> mPaymentDataList=new ArrayList<>();
    String storeJsonString;
    JSONArray jsonArray;
    int sum=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //------------INTIALIZE WITH OBJECT REFERENCE---------------------
       this.init();

       try {
           jsonArray=new JSONArray(readFile());
           getData(jsonArray);
       }catch (Exception e){
           Log.d("JSON_ARRAY",e.getMessage());
       }
        //------------ IMPLEMENT CLICK LISTENER FOR ADD PAYMENT-------------------
        mAddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (paymentMethod.size()>0){
                    Intent intent=new Intent(MainActivity.this,AddPayment.class);
                    intent.putExtra("data",paymentMethod);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });

        //------------- IMPLEMENT CLICK LISTENER FOR SAVING DETAILS TO .TXT FILE -------------------
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //-----------ASKING FOR PERMISIION READ AND WRITE-----------------
                if (isReadStoragePermissionGranted()){
                    if (isWriteStoragePermissionGranted()){
                        jsonArray=new JSONArray();
                        for (int index=0;index<mPaymentDataList.size();index++){
                            try {
                                JSONObject jsonObject=new JSONObject();
                                //  jsonObject.put("id",mPaymentDataList.get(index).getId());
                                jsonObject.put("amount",mPaymentDataList.get(index).getAmount());
                                jsonObject.put("method",mPaymentDataList.get(index).getMethod());
                                jsonObject.put("provider",mPaymentDataList.get(index).getProvider());
                                jsonObject.put("reference",mPaymentDataList.get(index).getReference());
                                jsonArray.put(jsonObject);

                            }catch (JSONException e){
                                Log.d("jsonException",e.getMessage());
                            }
                        }

                        writeFile(jsonArray.toString());
                        mSaveButton.setEnabled(false);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_CODE && data !=null) {
                String amount= data.getStringExtra("amount");
                String provider= data.getStringExtra("provider");
                String reference= data.getStringExtra("reference");
                String method= data.getStringExtra("method");
                String id= data.getStringExtra("id");
                PaymentData paymentData=new PaymentData();
                paymentData.setAmount(amount);
                paymentData.setProvider(provider);
                paymentData.setReference(reference);
                paymentData.setMethod(method);
                mPaymentDataList.add(3-paymentMethod.size(),paymentData);
                setTagOne(mPaymentDataList);
                paymentMethod.remove(method);
            }
        }

    }


    void init(){
        chipGroupOne  = findViewById(R.id.chip_group_one);
        mSaveButton=findViewById(R.id.save_btn);
        mTotalAmount=findViewById(R.id.total_amount_txt);
        mAddPayment=findViewById(R.id.add_payment_txt);
        mTotalAmount.setText(String.valueOf(sum));
        paymentMethod.add("Cash");
        paymentMethod.add("Bank Transfer");
        paymentMethod.add("Credit Card");

        //-------------CREATING A TXT FILE-----------------------
        file=new File(this.getFilesDir(),FILE_NAME);
        Log.d("CREATE_FILE_LOCATION",this.getFilesDir().getAbsolutePath());

        if (!file.exists()){
            try {
                file.createNewFile();
                fileWriter= new FileWriter(file.getAbsoluteFile());
                bufferedWriter=new BufferedWriter(fileWriter);
                bufferedWriter.write("[]");
                bufferedWriter.close();
            }catch (IOException e){
                Log.d("CREATE_FILE",e.getMessage());
            }
        }
    }

    private void setTagOne(final List<PaymentData> tagList) {
        chipGroupOne.removeAllViews();
        sum=0;
        for (int index = 0; index < tagList.size(); index++) {
                sum+=Integer.parseInt(tagList.get(index).getAmount());
                final String tagAmount = tagList.get(index).getAmount();
                final String tagName = tagList.get(index).getMethod();
                final Chip chip = new Chip(this);
                chip.setCloseIconResource(R.drawable.ic_close_black_24dp);
                chip.setCloseIconEnabled(true);
                final int finalIndex = index;

                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        paymentMethod.add(tagName);
                        try {
                            mPaymentDataList.remove(mPaymentDataList.get(finalIndex));
                            chipGroupOne.removeView(chip);
                            List<PaymentData> tempList=removeDuplicates(mPaymentDataList);
                            setTagOne(tempList);
                        }catch (IndexOutOfBoundsException e){

                        }
                    }
                });
                int paddingDp = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 10,
                        getResources().getDisplayMetrics()
                );
                chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
                chip.setText(tagName+" : "+tagAmount);
                chipGroupOne.addView(chip);

        }
        mTotalAmount.setText(String.valueOf(sum));
        mSaveButton.setEnabled(true);
    }



    public static List<PaymentData> removeDuplicates(List<PaymentData> list)
    {

        // Create a new ArrayList
        List<PaymentData> newList = new ArrayList<PaymentData>();

        // Traverse through the first list
        for (PaymentData element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        // return the new list
        return newList;
    }

    private String readFile(){
        StringBuffer output=new StringBuffer();
        try {
            fileReader=new FileReader(file.getAbsolutePath());
            bufferedReader=new BufferedReader(fileReader);
            String line="";
            while ((line=bufferedReader.readLine())!=null){
                output.append(line+"\n");
            }
            response=output.toString();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void writeFile(String payData){
        try {
            fileWriter=new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw=new BufferedWriter(fileWriter);
            bw.write(payData);
            bw.close();
        }catch (IOException e){

        }
    }

    private void getData(JSONArray jsonArray) {

        if (jsonArray.length()>0){
            for (int i=0;i<jsonArray.length();i++){
                final JSONObject obj;
                try {
                    obj=jsonArray.getJSONObject(i);
                    String amount= obj.getString("amount");
                    String provider= obj.getString("provider");
                    String reference= obj.getString("reference");
                    String method= obj.getString("method");

                    PaymentData paymentData=new PaymentData();
                    paymentData.setAmount(amount);
                    paymentData.setProvider(provider);
                    paymentData.setReference(reference);
                    paymentData.setMethod(method);
                    mPaymentDataList.add(paymentData);
                    List<PaymentData> tempList=new ArrayList<>();
                    tempList=removeDuplicates(mPaymentDataList);
                    setTagOne(tempList);
                    paymentMethod.remove(method);
                }catch (JSONException e){
                    Log.d("JSON_EXCEPTION",e.getMessage());
                }
            }
        }
        mSaveButton.setEnabled(false);
    }

    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else {
            //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else {
            //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
}
