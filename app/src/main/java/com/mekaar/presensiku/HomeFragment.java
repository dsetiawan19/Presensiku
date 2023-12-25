package com.mekaar.presensiku;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int SCAN_REQUEST_CODE = 100;

    private String mParam1;
    private String mParam2;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbref;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    private ImageButton btnScan;
    public static TextView resultText, mNamehome;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance("https://presensiku-520ee-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dbref = firebaseDatabase.getReference("Users");

        mNamehome = view.findViewById(R.id.name_home);
        resultText = view.findViewById(R.id.result);
        btnScan = view.findViewById(R.id.btn_scanner);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScannerActivity();
            }
        });

        return view;
    }

    private void startScannerActivity() {
        Intent intent = new Intent(getActivity(), ScannerActivity.class);
        startActivityForResult(intent, SCAN_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCAN_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            String scannedValue = data.getStringExtra("scannedValue");
            long timestamp = data.getLongExtra("timestamp", 0);
            onScanResult(scannedValue, timestamp);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Scan canceled", Toast.LENGTH_SHORT).show();
            }
        }

    private void onScanResult(String scanResult, long timestamp) {
        // Perform actions based on the scanned value and timestamp

        // Format the timestamp using SimpleDateFormat
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedTimestamp = sdf.format(new Date(timestamp));

        // Display the scanned value and formatted timestamp in the fragment
        String text = formattedTimestamp;
        resultText.setText(text);

        // Save the result text and timestamp to SharedPreferences
        saveResultToSharedPreferences(text, timestamp);
    }

    private void saveResultToSharedPreferences(String text, long timestamp) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("resultText", text);
        editor.putLong("timestamp", timestamp);
        editor.apply();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Retrieve the saved result text and timestamp from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedResultText = sharedPreferences.getString("resultText", "");
        long savedTimestamp = sharedPreferences.getLong("timestamp", 0);

        // Check if the saved result text is within one day
        long currentTime = System.currentTimeMillis();
        long oneDayInMillis = 24 * 60 * 60 * 1000; // 1 day in milliseconds

        if (currentTime - savedTimestamp <= oneDayInMillis) {
            // Display the saved result text in the fragment
            resultText.setText(savedResultText);
        } else {
            // Clear the saved result text from SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("resultText");
            editor.remove("timestamp");
            editor.apply();
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dbref.child(uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.getValue(String.class);
                    // Update the UI with the user's name
                    mNamehome.setText(name);
                } else {
                    // Handle the case when the user's name doesn't exist
                    mNamehome.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the data retrieval
                Toast.makeText(getActivity(), "Failed to retrieve user's name", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
