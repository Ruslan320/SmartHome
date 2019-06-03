package com.example.smarthome.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smarthome.R;
import com.example.smarthome.activity.MainActivity;
import com.example.smarthome.pojo.Room;
import com.example.smarthome.pojo.Sensor;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder>{

    private List<Room> roomList = new ArrayList<>();
    private OnRoomClickListener onRoomClickListener;
    private OnRoomLongClickListener onRoomLongClickListener;


    public RoomAdapter(OnRoomClickListener onRoomClickListener, OnRoomLongClickListener onRoomLongClickListener) {
        this.onRoomClickListener = onRoomClickListener;
        this.onRoomLongClickListener = onRoomLongClickListener;
    }

    class RoomViewHolder extends RecyclerView.ViewHolder{
        private TextView RoomName;
        private ImageView RoomImg;
        private LinearLayout LayoutImg;
        private ImageButton btn_delete_room;
        private Timer mTimer;
        private TextView QuantitySensor;

        public RoomViewHolder(View itemView){
            super(itemView);
            RoomName = itemView.findViewById(R.id.RoomName);
            RoomImg = itemView.findViewById(R.id.RoomImg);
            btn_delete_room = itemView.findViewById(R.id.btn_delete_room);
            LayoutImg = itemView.findViewById(R.id.LayoutInCard);
            QuantitySensor = itemView.findViewById(R.id.SensorSize);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            itemView.setOnLongClickListener(v -> {
                Room room = roomList.get(getLayoutPosition());
                btn_delete_room.setVisibility(View.VISIBLE);
                btn_delete_room.setClickable(true);
                Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.wobble);
                itemView.startAnimation(animation);
                mTimer = new Timer();
                class MyTimerTask extends TimerTask {
                    @Override
                    public void run() {
                        btn_delete_room.setVisibility(View.INVISIBLE);
                        btn_delete_room.setClickable(false);
                        animation.cancel();
                    }
                }
                MyTimerTask mMyTimerTask = new MyTimerTask();
                mTimer.schedule(mMyTimerTask, 2000);

                btn_delete_room.setOnClickListener(v1 -> {


                    roomList.remove(getLayoutPosition());
                    notifyItemRemoved(getLayoutPosition());
                    CollectionReference collection = db.collection("smart_home").document(MainActivity.Element_home)
                            .collection("rooms");
                    collection.document(room.getId()).collection("sensors").document().delete();
                    collection.document(room.getId()).delete();
//                            collection
//                                    .get()
//                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                            if (task.isSuccessful()) {
//                                                int count = 0;
//                                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                                    if(document.getId().equals(room.getId())){
//
//                                                        List<Sensor> arrSen = room.GetSensorList();
//                                                        for(int i = 0; i < arrSen.size(); i++){
//                                                            collection.document(document.getId()).collection("sensors").document(arrSen.get(i).getId());
//                                                        }
//                                                        collection.document(document.getId()).collection("sensors").document().delete();
//
//                                                        collection.document(document.getId()).delete();
//
//
//                                                    }
//                                                }
//                                            } else {
//                                                Log.d("TAG_from_Holder", "Error getting documents: ", task.getException());
//                                            }
//                                        }
//                                    });
//                            Snackbar snackbar = Snackbar.make(v, "", Snackbar.LENGTH_INDEFINITE);
//                            snackbar.setAction("Отмена", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    snackbar.dismiss();
//                                }
//                            }).show();
                });
                onRoomLongClickListener.onRoomLongClick(room);
                return true;
            });
            itemView.setOnClickListener(v -> {
                Room room = roomList.get(getLayoutPosition());
                onRoomClickListener.onRoomClick(room);
            });


        }

        @SuppressLint("SetTextI18n")
        void bind(Room room){
            RoomName.setText(room.getName());
            RoomImg.setImageResource(room.getImg());

            int size = room.getSizeSensor();
            if(size%10 == 1 && size != 11){
                QuantitySensor.setText(((Integer)size).toString() + " устройство");
            }
            else if((size%10 == 2 || size%10 == 3 || size%10 == 4) && size != 12 && size != 13 && size != 14){
                QuantitySensor.setText(((Integer)size).toString() + " устройства");
            }
            else if(size == 0){
                QuantitySensor.setText("Здесь ещё не датчиков");
            }
            else {
                QuantitySensor.setText(((Integer)size).toString() + " устройств");
            }
//            List<Integer> ImgSensor = room.GetSensorListSize();
//            for(int i = 0; i < ImgSensor.size(); i++){
//                ImageView iv = new ImageView(itemView.getContext());
//                iv.setImageResource(ImgSensor.get(i));
//                LayoutImg.addView(iv, i);
//            }

//            List<Sensor> ImgSensor = room.GetSensorList();
//            Log.d("TAG", ((Integer)room.getSizeSensor()).toString());
//            for(int i = 0; i < room.getSizeSensor(); i++){
//                ImageView iv = new ImageView(itemView.getContext());
//                switch (ImgSensor.get(i).getType()){
//                    case 0:
//                        iv.setImageResource((int)R.drawable.ic_socket_on);
//                        break;
//                    case 1:
//                        iv.setImageResource((int)R.drawable.ic_lamp_on);
//                        break;
//                    default:
//                        iv.setImageResource((int)R.drawable.ic_kettler_on );
//                        break;
//                }
//                LayoutImg.addView(iv, i);
//            }
        }

    }



    public void setItems(Collection<Room> rooms){
        roomList.addAll(rooms);
        notifyDataSetChanged();
    }

    public void clearItems(){
        roomList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.room_item, viewGroup, false);
        return new RoomViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder roomViewHolder, int i) {
//        final Room room = roomList.get(i);
//
//        ViewCompat.setTransitionName(roomViewHolder.itemView, room.getId());
//
//        roomViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onRoomClickListener.onRoomClick(roomViewHolder.getAdapterPosition(), room, roomViewHolder.RoomImg);
//            }
//        });


        roomViewHolder.bind(roomList.get(i));

    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }


    public interface OnRoomClickListener {
        void onRoomClick(Room room);
    }
    public interface OnRoomLongClickListener{
        void onRoomLongClick(Room room);
    }
}
