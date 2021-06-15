package com.example.todo.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.todo.databinding.ItemViewBinding;
import com.example.todo.model.Reserva;

import java.util.ArrayList;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.MyViewHolder> {
    private ArrayList<Reserva> reservas;

    public TodoAdapter(){
        this.reservas = new ArrayList<>();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemViewBinding binding;//Name of the item_view.xml in camel case + "Binding"

        public MyViewHolder(ItemViewBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

    @Override
    public TodoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new MyViewHolder(ItemViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // Get the data model based on position
        Reserva reserva = reservas.get(position);

        holder.binding.fecha.setText(String.valueOf(reserva.getDia()) + "/" +String.valueOf(reserva.getMes())+ "/2021");
        holder.binding.horaComienzo.setText("Hora de inicio: " + reserva.getHora_comienzo());
        holder.binding.horaFin.setText("Hora de fin: " +reserva.getHora_fin());
    }


    @Override
    public int getItemCount() {
        return reservas.size();
    }

    public int getId(int position){

        return this.reservas.get(position).getId();
    }

    public void setReservas(ArrayList<Reserva> reservas) {
        this.reservas = reservas;
        notifyDataSetChanged();
    }

    public Reserva getAt(int position){
        Reserva reserva;
        reserva = this.reservas.get(position);
        return reserva;
    }

    public void add(Reserva reserva) {
        this.reservas.add(reserva);
        notifyItemInserted(reservas.size() - 1);
        notifyItemRangeChanged(0, reservas.size() - 1);
    }

    public void modifyAt(Reserva reserva, int position) {
        this.reservas.set(position, reserva);
        notifyItemChanged(position);
    }

    public void removeAt(int position) {
        this.reservas.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, reservas.size() - 1);
    }
}
