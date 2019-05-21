package com.example.smarthome.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.smarthome.R;
import com.example.smarthome.pojo.Sensor;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SensorAdapter extends  RecyclerView.Adapter<SensorAdapter.SensorViewHolder>{

    private List<Sensor> sensorList = new ArrayList<>();
//    private final int HEADER = 0;
//    private final int ITEM = 1;
//    private SensorAdapter.OnSensorClickListener onSensorClickListener;


//    public SensorAdapter(OnSensorClickListener onSensorClickListener) {
//        this.onSensorClickListener = onSensorClickListener;
//    }

    class SensorViewHolder extends RecyclerView.ViewHolder{
        private TextView SensorName;
        private ToggleButton toggleButton;
        private ImageButton delete_btn;

        public SensorViewHolder(View itemView){
            super(itemView);
            SensorName = itemView.findViewById(R.id.sensor_name);

//
            FirebaseFirestore db = FirebaseFirestore.getInstance();
////
//            delete_btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Sensor sensor = sensorList.get(getLayoutPosition());
//
//                    sensorList.remove(getLayoutPosition());
//                    notifyItemRemoved(getLayoutPosition());

//                    db.collection("smart_home").document(MainActivity.Element_home)
//                            .collection("rooms")
//                            .document(((Integer)room.getId()).toString())
//                            .collection("sensors")
//                            .get()
//                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                    if (task.isSuccessful()) {
//                                        int count = 0;
//                                        for (QueryDocumentSnapshot document : task.getResult()) {
//                                            if(count == sensor.getId()){
//
//                                                db.collection("smart_home").document(MainActivity.Element_home)
//                                                        .collection("rooms").document(((Integer)room.getId()).toString())
//                                                        .collection("sensors").document(document.getId()).delete();
//                                            }
//                                            count++;
//                                        }
//                                    } else {
//                                        Log.d("TAG_from_Holder", "Error getting documents: ", task.getException());
//                                    }
//                                }
//                            });
//                }
//             });

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
//        switch (getItemViewType(i)){
//            case 0:
//                view =LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.socket_item, viewGroup, false);
//                break;
//            case 1:
//                view =LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lamp_item, viewGroup, false);
//                break;
//            default:
//
//                break;
//        }
        view =LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sensor_item, viewGroup, false);
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

//    @Override
//    public int getItemViewType(int pos) {
//        return pos == 0 ? HEADER : ITEM;
//    }

//    public interface OnSensorClickListener {
//        void onSensorClick(Sensor sensor);
//    }
}