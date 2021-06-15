package com.example.todo.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todo.databinding.ActivityAddBinding;
import com.example.todo.databinding.ActivityEmailBinding;
import com.example.todo.model.Email;
import com.example.todo.model.EmailResponse;
import com.example.todo.network.ApiTokenRestClient;
import com.example.todo.util.SharedPreferencesManager;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailActivity extends AppCompatActivity implements View.OnClickListener, Callback<EmailResponse> {
    public static final int OK = 1;
    public static final String EMAIL = "carlosmoyano@protonmail.com";

    private  ActivityEmailBinding binding;

    SharedPreferencesManager preferences;

    ProgressDialog progreso;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activity_main.xml -> ActivityMainBinding
        binding = ActivityEmailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.send.setOnClickListener(this);
        binding.cancel.setOnClickListener(this);

        preferences = new SharedPreferencesManager(this);
    }

    @Override
    public void onClick(View v) {
        hideSoftKeyboard();

        if (v == binding.send) {
            String from = binding.email.getText().toString();
            String subject = binding.subject.getText().toString();
            String message = binding.message.getText().toString();

            if (from.isEmpty() || subject.isEmpty() || message.isEmpty()) {
                showMessage("Please, fill email, subject and message");
            } else {
                Email email = new Email(EMAIL, from, subject, message);
                connection(email);
            }
        } else if (v == binding.cancel) {
            finish();
        }

    }

    private void connection(Email e) {
        progreso = new ProgressDialog(this);
        progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progreso.setMessage("Connecting . . .");
        progreso.setCancelable(false);
        progreso.show();

        Call<EmailResponse> call = ApiTokenRestClient.getInstance(preferences.getToken()).sendEmail(e);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<EmailResponse> call, Response<EmailResponse> response) {
        progreso.dismiss();
        if (response.isSuccessful()) {
            EmailResponse emailResponse = response.body();
            if (emailResponse.getSuccess()) {
                //Intent i = new Intent();
                //setResult(OK, i);
                showMessage("Email sent ok: " + emailResponse.getMessage());
                finish();
            } else {
                String message = "Email not sent";
                if (!emailResponse.getMessage().isEmpty()) {
                    message += ": " + emailResponse.getMessage();
                }
                showMessage(message);
            }
        } else {
            StringBuilder message = new StringBuilder();
            message.append("Error sending the mail: " + response.code());
            if (response.body() != null)
                message.append("\n" + response.body());
            if (response.errorBody() != null)
                try {
                    message.append("\n" + response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            showMessage(message.toString());
        }
    }

    @Override
    public void onFailure(Call<EmailResponse> call, Throwable t) {
        String message = "Failure sending the email\n";
        if (t != null)
            message += t.getMessage();
        showMessage(message);
    }

    private void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}