//adapterHistori

package com.mekaar.presensiku;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdapterHistori extends RecyclerView.Adapter<AdapterHistori.ViewHolder> {
    private List<TimestampData> dataList;

    public AdapterHistori(List<TimestampData> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_histori, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimestampData data = dataList.get(position);

        holder.textViewLocationName.setText(data.getLocationName());
        holder.dateTextView.setText(DateUtils.formatDate(data.getCurrentDate()));
        holder.textViewAttendance.setText(DateUtils.formatTime(data.getTimeStamp()));

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewLocationName;
        TextView dateTextView;
        TextView textViewAttendance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewLocationName = itemView.findViewById(R.id.historiLocation);
            dateTextView = itemView.findViewById(R.id.historiTanggal);
            textViewAttendance = itemView.findViewById(R.id.historiWaktu);
        }
    }
}
