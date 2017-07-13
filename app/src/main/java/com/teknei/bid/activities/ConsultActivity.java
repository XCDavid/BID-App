package com.teknei.bid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.teknei.bid.R;

public class ConsultActivity extends AppCompatActivity implements View.OnClickListener{
    EditText etNumOperation;
    Button searchButton;

    String operationNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consult);

        etNumOperation = (EditText)findViewById(R.id.et_num_operation_consult);
        searchButton = (Button)findViewById(R.id.b_search_operation_consult);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.b_search_operation_consult:
                operationNumber = etNumOperation.getText().toString();
                break;
        }
    }
}
