package com.example.smarthome.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.smarthome.R;
import com.example.smarthome.pojo.Sensor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SensorAdapter extends  RecyclerView.Adapter<SensorAdapter.SensorViewHolder>{

    private List<Sensor> sensorList = new ArrayList<>();

    class SensorViewHolder extends RecyclerView.ViewHolder{
        private TextView SensorName;
        private ToggleButton toggleButton;
        public SensorViewHolder(View itemView){
            super(itemView);
            toggleButton = (ToggleButton)itemView.findViewById(R.id.toggle_btn);
            SensorName = (TextView)itemView.findViewById(R.id.sensor_name);

        }
        public void bind(Sensor sensor){
            SensorName.setText(sensor.getName());
        }
    }

    public void setItems(Collection<Sensor> sensors){
        sensorList.addAll(sensors);
        notifyDataSetChanged();
    }

    public void clearItems(){
        sensorList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        switch (getItemViewType(i)){
            case 0:

                view =LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.socket_item, viewGroup, false);
                break;
            case 1:
                view =LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lamp_item, viewGroup, false);
                break;
            default:
                view =LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.kettle_item, viewGroup, false);
                break;
        }
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        holder.bind(sensorList.get(position));
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        Log.d("TAG", ((Integer)sensorList.get(position).getType()).toString());
        return sensorList.get(position).getType();
    }
}
