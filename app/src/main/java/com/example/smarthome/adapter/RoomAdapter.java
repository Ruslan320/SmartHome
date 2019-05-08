package com.example.smarthome.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smarthome.R;
import com.example.smarthome.pojo.Room;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder>{

    private List<Room> roomList = new ArrayList<>();
    private OnRoomClickListener onRoomClickListener;

    public RoomAdapter(OnRoomClickListener onRoomClickListener) {
        this.onRoomClickListener = onRoomClickListener;
    }

    class RoomViewHolder extends RecyclerView.ViewHolder{
        private TextView RoomName;
        private ImageView RoomImg;
        private LinearLayout LayoutImg;
        private Context context;

        public RoomViewHolder(View itemView){
            super(itemView);
            RoomName = itemView.findViewById(R.id.RoomName);
            RoomImg = itemView.findViewById(R.id.RoomImg);
            LayoutImg = itemView.findViewById(R.id.LayoutInCard);



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
}
