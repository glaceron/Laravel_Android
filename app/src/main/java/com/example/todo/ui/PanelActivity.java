package com.example.todo.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.adapter.ClickListener;
import com.example.todo.adapter.RecyclerTouchListener;
import com.example.todo.adapter.TodoAdapter;
import com.example.todo.databinding.ActivityPanelBinding;
import com.example.todo.model.DeleteResponse;
import com.example.todo.model.GetReservasResponse;
import com.example.todo.model.LogoutResponse;
import com.example.todo.model.Reserva;
import com.example.todo.network.ApiTokenRestClient;
import com.example.todo.util.SharedPreferencesManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PanelActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int ADD_CODE = 100;
    public static final int UPDATE_CODE = 200;
    public static final int OK = 1;

    int positionClicked;
    ProgressDialog progreso;
    //ApiService apiService;
    SharedPreferencesManager preferences;
    private ActivityPanelBinding binding;
    private TodoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activity_main.xml -> ActivityMainBinding
        binding = ActivityPanelBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.floatingActionButton.setOnClickListener(this);

        preferences = new SharedPreferencesManager(this);
        //showMessage("panel: " + preferences.getToken());

        //Initialize RecyclerView
        adapter = new TodoAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        //manage click
        binding.recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, binding.recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                showMessage("Single Click on task with id: " + adapter.getAt(position).getId());
                modify(adapter.getAt(position));
                positionClicked = position;
            }

            @Override
            public void onLongClick(View view, int position) {
                showMessage("Long press on position :" + position);
                confirm(adapter.getAt(position).getId(), adapter.getAt(position).getHora_comienzo(), position);
            }
        }));

        //Destruir la instancia de Retrofit para que se cree una con el nuevo token
        ApiTokenRestClient.deleteInstance();

        downloadTasks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refresh:
                //petición al servidor para descargar de nuevo los sitios
                downloadTasks();
                break;

            case R.id.email:
                //send an email
                Intent i = new Intent(this, EmailActivity.class);
                startActivity(i);
                break;

            case R.id.exit:
                //petición al servidor para anular el token (a la ruta /api/logout)
                Call<LogoutResponse> call = ApiTokenRestClient.getInstance(preferences.getToken()).logout();
                call.enqueue(new Callback<LogoutResponse>() {
                    @Override
                    public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                        if (response.isSuccessful()) {
                            LogoutResponse logoutResponse = response.body();
                            if (logoutResponse.getSuccess()) {
                                showMessage("Logout OK");
                            } else
                                showMessage("Error in logout");
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
                    public void onFailure(Call<LogoutResponse> call, Throwable t) {
                        String message = "Failure in the communication\n";
                        if (t != null)
                            message += t.getMessage();
                        showMessage(message);

                    }
                });
                preferences.saveToken(null, null);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        if (v == binding.floatingActionButton) {
            Intent i = new Intent(this, AddActivity.class);
            startActivityForResult(i, ADD_CODE);
        }
    }

    private void downloadTasks() {
        progreso = new ProgressDialog(this);
        progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progreso.setMessage("Connecting . . .");
        progreso.setCancelable(false);
        progreso.show();

        //Call<ArrayList<Site>> call = ApiRestClient.getInstance().getSites("Bearer " + preferences.getToken());
        Call<GetReservasResponse> call = ApiTokenRestClient.getInstance(preferences.getToken()).getReservas();
        call.enqueue(new Callback<GetReservasResponse>() {
            @Override
            public void onResponse(Call<GetReservasResponse> call, Response<GetReservasResponse> response) {
                progreso.dismiss();
                if (response.isSuccessful()) {
                    GetReservasResponse getReservasResponse = response.body();
                    if (getReservasResponse.getSuccess()) {
                        adapter.setReservas(getReservasResponse.getData());
                        showMessage("Tasks downloaded ok");
                    } else {
                        showMessage("Error downloading the tasks: " + getReservasResponse.getMessage());
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
            public void onFailure(Call<GetReservasResponse> call, Throwable t) {
                progreso.dismiss();
                String message = "Failure in the communication\n";
                if (t != null)
                    message += t.getMessage();
                showMessage(message);
            }
        });
    }

    private void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Reserva reserva = new Reserva();

        if (requestCode == ADD_CODE)
            if (resultCode == OK) {
                reserva.setId(data.getIntExtra("id", 1));
                reserva.setDia(data.getIntExtra("dia",1));
                reserva.setMes(data.getIntExtra("mes",1));
                reserva.setHora_comienzo(data.getStringExtra("hora_comienzo"));
                reserva.setHora_fin(data.getStringExtra("hora_fin"));
                reserva.setCreatedAt(data.getStringExtra("createdAt"));
                adapter.add(reserva);
            }

        if (requestCode == UPDATE_CODE)
            if (resultCode == OK) {
                reserva.setId(data.getIntExtra("id", 1));
                reserva.setDia(data.getIntExtra("dia",1));
                reserva.setMes(data.getIntExtra("mes",1));
                reserva.setHora_comienzo(data.getStringExtra("hora_comienzo"));
                reserva.setHora_fin(data.getStringExtra("hora_fin"));
                reserva.setCreatedAt(data.getStringExtra("createdAt"));
                adapter.modifyAt(reserva, positionClicked);
            }
    }

    private void modify(Reserva reserva) {
        Intent i = new Intent(this, UpdateActivity.class);
        i.putExtra("reserva", reserva);
        startActivityForResult(i, UPDATE_CODE);
    }

    private void confirm(final int idTask, String description, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(description + "\nDo you want to delete?")
                .setTitle("Delete")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        connection(position);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void connection(final int position) {
        //Call<ResponseBody> call = ApiRestClient.getInstance().deleteSite("Bearer " + preferences.getToken(), adapter.getId(position));
        Call<DeleteResponse> call = ApiTokenRestClient.getInstance(preferences.getToken()).deleteReserva(adapter.getId(position));
        progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progreso.setMessage("Connecting . . .");
        progreso.setCancelable(false);
        progreso.show();
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                progreso.dismiss();
                if (response.isSuccessful()) {
                    DeleteResponse deleteResponse = response.body();
                    if (deleteResponse.getSuccess()) {
                        adapter.removeAt(position);
                        showMessage("Reserva deleted OK");
                    } else
                        showMessage("Error deleting the task");
                } else {
                    StringBuilder message = new StringBuilder();
                    message.append("Error deleting a site: " + response.code());
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
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                progreso.dismiss();
                if (t != null)
                    showMessage("Failure in the communication\n" + t.getMessage());

            }
        });
    }
}