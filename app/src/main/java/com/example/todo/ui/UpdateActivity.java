package com.example.todo.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todo.databinding.ActivityUpdateBinding;
import com.example.todo.model.AddResponse;
import com.example.todo.model.Reserva;
import com.example.todo.network.ApiTokenRestClient;
import com.example.todo.util.SharedPreferencesManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener, Callback<AddResponse> {
    public static final int OK = 1;

    private ProgressDialog progreso;
    SharedPreferencesManager preferences;

    private ActivityUpdateBinding binding;
    String horas[] = new String[15];

    private Reserva reserva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //activity_main.xml -> ActivityMainBinding
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.accept.setOnClickListener(this);
        binding.cancel.setOnClickListener(this);

        binding.numberPickerDia.setMaxValue(31);
        binding.numberPickerDia.setMinValue(1);

        binding.numberPickerMes.setMaxValue(12);
        binding.numberPickerMes.setMinValue(1);

        binding.numberPickerHoras.setMaxValue(2);
        binding.numberPickerHoras.setMinValue(1);

        for(int i = 9; i<24;i++)
        {
            horas[i-9] = i+":00";
        }

        binding.numberPickerHora.setMinValue(0);
        binding.numberPickerHora.setMaxValue(horas.length - 1);
        binding.numberPickerHora.setDisplayedValues(horas);
        binding.numberPickerHora.setValue(0);

        preferences = new SharedPreferencesManager(this);

        Intent i = getIntent();
        reserva = (Reserva) i.getSerializableExtra("reserva");
    }

    @Override
    public void onClick(View v) {

        String hora_comienzo,hora_fin;
        int dia,mes;

        Reserva reserva;

        if (v == binding.accept) {
            hideSoftKeyboard();
            dia = binding.numberPickerDia.getValue();
            mes = binding.numberPickerMes.getValue();
            int valuePicker1 = binding.numberPickerHora.getValue();
            hora_comienzo = horas[valuePicker1];

            int numeroHoras = binding.numberPickerHoras.getValue();
            hora_fin = horas[valuePicker1+numeroHoras];

            reserva = new Reserva(dia,mes,hora_comienzo,hora_fin);
            connection(reserva);

        } else if (v == binding.cancel) {
            finish();
        }
    }

    private void connection(Reserva reserva) {
        showMessage(reserva.getId() + "");
        progreso = new ProgressDialog(this);
        progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progreso.setMessage("Connecting . . .");
        progreso.setCancelable(false);
        progreso.show();

        //Call<Site> call = ApiRestClient.getInstance().createSite("Bearer " + preferences.getToken(), s);
        Call<AddResponse> call = ApiTokenRestClient.getInstance(preferences.getToken()).updateReserva(reserva, reserva.getId());
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<AddResponse> call, Response<AddResponse> response) {
        progreso.dismiss();
        if (response.isSuccessful()) {
            AddResponse addResponse = response.body();
            if (addResponse.getSuccess()) {
                Intent i = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("id", addResponse.getData().getId());
                bundle.putInt("dia", addResponse.getData().getDia());
                bundle.putInt("mes", addResponse.getData().getMes());
                bundle.putString("hora_comienzo", addResponse.getData().getHora_comienzo());
                bundle.putString("hora_fin", addResponse.getData().getHora_fin());
                bundle.putString("createdAt", addResponse.getData().getCreatedAt());
                i.putExtras(bundle);
                setResult(OK, i);
                finish();
                showMessage("Reserva updated ok: " + addResponse.getData().getUser());
            } else {
                String message = "Error updating the reserva";
                if (!addResponse.getMessage().isEmpty()) {
                    message += ": " + addResponse.getMessage();
                }
                showMessage(message);

            }
        } else {
            StringBuilder message = new StringBuilder();
            message.append("Download error: ");
            Log.e("Error:", response.errorBody().toString());
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
    public void onFailure(Call<AddResponse> call, Throwable t) {
        progreso.dismiss();
        if (t != null)
            showMessage("Failure in the communication\n" + t.getMessage());
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

