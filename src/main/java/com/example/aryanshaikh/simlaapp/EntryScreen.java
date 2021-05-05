package com.example.aryanshaikh.simlaapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EntryScreen extends AppCompatActivity implements View.OnClickListener {

    EditText et1, p1;
    TextView t1;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_screen);
        et1=(EditText)findViewById(R.id.editText);
        p1=(EditText)findViewById(R.id.editText2);
        t1=(TextView)findViewById(R.id.textView);
        b1=(Button)findViewById(R.id.button);
        et1.setOnClickListener(this);
        p1.setOnClickListener(this);
        b1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(et1.getText().toString().equals("ABC") && p1.getText().toString().equals("123")) {         //toString() method converts the input text to string important!!!
            t1.setText("Login Success");
            Intent i = new Intent(this, Dashboard.class);
            startActivity(i);

        }

        else
            t1.setText("Invalid username or password");
    }
}
