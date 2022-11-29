package com.appdeb.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private Context context;

    public WeatherAdapter(Context context, ArrayList<WeatherModel> weatherModelArrayList) {
        this.context = context;
        this.weatherModelArrayList = weatherModelArrayList;
    }

    private ArrayList<WeatherModel> weatherModelArrayList;

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {

        WeatherModel model = weatherModelArrayList.get(position);
        holder.tvTemperature.setText(model.getTemperature()+ "°C");
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.imgCondition);
        holder.tvWindSpeed.setText(model.getWindSpeed()+ "Km/h");
//        holder.tvTime.setText(model.getTime());

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try{
            Date t = input.parse(model.getTime());
            holder.tvTime.setText(output.format(t));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return weatherModelArrayList.size();
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvTemperature, tvWindSpeed;
        ImageView imgCondition;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTime = itemView.findViewById(R.id.tvTime);
            tvTemperature = itemView.findViewById(R.id.tvTemperature);
            tvWindSpeed = itemView.findViewById(R.id.tvWindSpeed);
            imgCondition = itemView.findViewById(R.id.imgCondition);
        }
    }
}
