package com.example.todo.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todo.MainActivity;
import com.example.todo.databinding.ActivityMainBinding;
import com.example.todo.databinding.ActivityRegisterBinding;
import com.example.todo.model.RegisterResponse;
import com.example.todo.network.ApiRestClient;
import com.example.todo.util.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";
    private ActivityRegisterBinding binding;

    SharedPreferencesManager preferences;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activity_main.xml -> ActivityMainBinding
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.register.setOnClickListener(this);
        binding.login.setOnClickListener(this);

        preferences = new SharedPreferencesManager(this);

        binding.email.setText(preferences.getEmail());
        binding.password.setText(preferences.getPassword());
    }

    @Override
    public void onClick(View v) {
        if (v == binding.register) {
            String name = binding.name.getText().toString();
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();
            String confirmPassword = binding.confirmPassword.getText().toString();

            Log.d(TAG, "Registro");

            if (validate(name, email, password, confirmPassword) == false) {
                showMessage("Register failed");
                binding.register.setEnabled(true);
            } else {
                sendToServer(name, email, password, confirmPassword);
            }
        }
        if (v == binding.login) {
            // Finish the registration and return to the Login activity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            // Cancelar el registro
            setResult(RESULT_CANCELED, null);
            startActivity(intent);
            finish();
        }

    }

    public boolean validate(String name, String email, String password, String confirmPassword) {
        boolean valid = true;

        if (name.isEmpty() || name.length() < 3) {
            binding.name.setError("at least 3 characters");
            valid = false;
        } else {
            binding.name.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.setError("enter a valid email address");
            valid = false;
        } else {
            binding.email.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            binding.password.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            binding.password.setError(null);
        }

        if (!(confirmPassword.equals(password))) {
            binding.confirmPassword.setError("Password Do not match");
            valid = false;
        } else {
            binding.confirmPassword.setError(null);
        }

        return valid;
    }

    private void sendToServer(String name, String email, String password, String confirmPassord) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account ...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        binding.register.setEnabled(false);

        Call<RegisterResponse> call = ApiRestClient.getInstance().register(name, email, password, confirmPassord);
        //User user = new User(name, email, password);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                progressDialog.dismiss();
                //onRegisterSuccess();
                binding.register.setEnabled(true);
                if (response.isSuccessful()) {
                    RegisterResponse registerResponse = response.body();
                    if (registerResponse.getSuccess()) {
                        //Log.d("onResponse", "" + registerResponse.getData().getToken());
                        //enviar al Login para entrar después de validar el email
                        binding.register.setEnabled(true);

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("email", email);
                        resultIntent.putExtra("password", password);
                        //guardar el token en shared preferences
                        resultIntent.putExtra("token", registerResponse.getData().getToken());
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        showMessage("Error in registration " + registerResponse.getMessage());
                    }
                } else {
                    StringBuilder message = new StringBuilder();
                    message.append("Download error: " + response.code());
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
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                progressDialog.dismiss();
                String message = "Failure in the communication\n";
                if (t != null) {
                    Log.d("onFailure", t.getMessage());
                    message += t.getMessage();
                }
                showMessage(message);
                binding.register.setEnabled(true);
            }
        });
    }

    private void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
