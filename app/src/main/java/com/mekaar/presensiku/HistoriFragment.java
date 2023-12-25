package com.mekaar.presensiku;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoriFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoriFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    AdapterHistori adapterHistori;
    List<TimestampData> dataList;
    DatabaseReference dbref;
    private TextView emptyDataTextView;

    public HistoriFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoriFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoriFragment newInstance(String param1, String param2) {
        HistoriFragment fragment = new HistoriFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_histori, container, false);
        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.histori_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        emptyDataTextView = view.findViewById(R.id.emptyDataTextView);

        dataList = new ArrayList<>();
        adapterHistori = new AdapterHistori(dataList);
        recyclerView.setAdapter(adapterHistori);

        dbref = FirebaseDatabase.getInstance("https://presensiku-520ee-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("History");

        retrieveDataFromFirebase();

        return view;
    }

    private void retrieveDataFromFirebase() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get the current user's ID
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataList.clear();

                long currentTime = System.currentTimeMillis();
                long twoWeeksAgo = currentTime - (14L * 24L * 60L * 60L * 1000L); // Two weeks ago in milliseconds

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TimestampData data = snapshot.getValue(TimestampData.class);
                    if (data.getUid().equals(currentUserId) && data.getCurrentDate() >= twoWeeksAgo) {
                        String formattedDate = dateFormat.format(new Date(data.getCurrentDate()));
                        data.setFormattedDate(formattedDate);
                        dataList.add(data);
                    }
                }

                adapterHistori.notifyDataSetChanged();
                updateEmptyDataView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyDataView() {
        if (dataList.isEmpty()) {
            emptyDataTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyDataTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}