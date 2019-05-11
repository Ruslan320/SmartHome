package com.example.smarthome.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.R;
import com.example.smarthome.activity.MainActivity;
import com.example.smarthome.pojo.Room;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.firebase.ui.auth.AuthUI.TAG;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

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

        public RoomViewHolder(View itemView){
            super(itemView);
            RoomName = itemView.findViewById(R.id.RoomName);
            RoomImg = itemView.findViewById(R.id.RoomImg);
            LayoutImg = itemView.findViewById(R.id.LayoutInCard);
            btn_delete_room = itemView.findViewById(R.id.btn_delete_room);
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
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

                    btn_delete_room.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id;
                            for(int i = room.getId()+1; i < roomList.size(); i++){
                                id = roomList.get(i).getId();
                                roomList.get(i).setId(id-1);
                            }
                            roomList.remove(room.getId());
                            notifyItemRemoved(room.getId());

                            db.collection("smart_home").document(MainActivity.Element_home)
                                    .collection("rooms")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                int count = 0;
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if(count == room.getId()){

                                                        db.collection("smart_home").document(MainActivity.Element_home)
                                                                .collection("rooms").document(document.getId()).delete();
                                                    }
                                                    count++;
                                                }
                                            } else {
                                                Log.d("TAG_from_Holder", "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
//                            Snackbar snackbar = Snackbar.make(v, "", Snackbar.LENGTH_INDEFINITE);
//                            snackbar.setAction("Отмена", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    snackbar.dismiss();
//                                }
//                            }).show();
                        }
                    });
                    onRoomLongClickListener.onRoomLongClick(room);
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Room room = roomList.get(getLayoutPosition());
                    onRoomClickListener.onRoomClick(room);
                }
            });


        }

        public void bind(Room room){
            RoomName.setText(room.getName());
            RoomImg.setImageResource(room.getImg());

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
