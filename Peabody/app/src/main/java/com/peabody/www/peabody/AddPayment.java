package com.peabody.www.peabody;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AddPayment extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    List<String> paymentMethod = new ArrayList<>();
    ArrayAdapter mAdapter;
    LinearLayout linearLayout;
    EditText mAmount,mProvider,mReference;
    String method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);

        paymentMethod=getIntent().getExtras().getStringArrayList("data");
        TextView mCancel=findViewById(R.id.dismiss_ad);
        linearLayout=findViewById(R.id.linerlayout);
        mProvider=findViewById(R.id.provider);
        mReference=findViewById(R.id.reference);
        mAmount=findViewById(R.id.enter_amount_et);
        Button mAdd = findViewById(R.id.ok_ad);
        Spinner spin = findViewById(R.id.choose_method_spinner);

        spin.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the paymentMethod list
        mAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,paymentMethod);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(mAdapter);

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount=mAmount.getText().toString();
                if (!method.equals("Cash")){
                    String providerName=mProvider.getText().toString();
                    String referenceName=mReference.getText().toString();
                    if (!providerName.isEmpty() && !referenceName.isEmpty() && !amount.isEmpty()){
                        Intent mIntent = new Intent();
                        mIntent.putExtra("amount", amount);
                        mIntent.putExtra("provider", providerName);
                        mIntent.putExtra("reference", referenceName);
                        mIntent.putExtra("method", method);
                        setResult(RESULT_OK, mIntent);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), "Fields are missing !", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if (!amount.isEmpty()){
                        Intent mIntent = new Intent();
                        mIntent.putExtra("amount", amount);
                        mIntent.putExtra("provider", "NA");
                        mIntent.putExtra("reference", "NA");
                        mIntent.putExtra("method", method);
                        setResult(RESULT_OK, mIntent);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), "Fields are missing !", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        method=paymentMethod.get(i);
        if (!paymentMethod.get(i).equals("Cash")){
            linearLayout.setVisibility(View.VISIBLE);
        }else{
            linearLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
