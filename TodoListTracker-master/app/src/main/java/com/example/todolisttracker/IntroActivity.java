package com.example.todolisttracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    EditText editTextName, editTextPhone;
    Button buttonContinue;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        mAuth = FirebaseAuth.getInstance();

        EditText editTextName = findViewById(R.id.editTextName);
        EditText editTextPhone = findViewById(R.id.editTextPhone);
        Button buttonContinue = findViewById(R.id.buttonContinue);

        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);

        if (prefs.contains("name") && prefs.contains("phone")) {
            startActivity(new Intent(IntroActivity.this, MainActivity.class));
            finish();
            return;
        }

        buttonContinue.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();

            if (!name.isEmpty() && !phone.isEmpty()) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("name", name);
                editor.putString("phone", phone);
                editor.apply();

                Toast.makeText(this, "Welcome " + name, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(IntroActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
