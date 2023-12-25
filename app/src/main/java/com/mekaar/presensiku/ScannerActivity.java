package com.mekaar.presensiku;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.zxing.Result;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 201;
    private ZXingScannerView scannerView;
    private DatabaseReference dbref;
    FirebaseAuth mAuth;
    private FusedLocationProviderClient fusedLocationClient;

    // Define your predefined location here
    private static final double PREDEFINED_LAT = 106.79586742534607;
    private static final double PREDEFINED_LON = -6.202931020715152;

    // Define the radius of the circular area for scanning (in meters)
    private static final double SCAN_RADIUS = 100.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        dbref = FirebaseDatabase.getInstance("https://presensiku-520ee-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("History");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Check location before starting the camera
            checkLocationAndStartCamera();
        }
    }

    private void checkLocationAndStartCamera() {
        // Check and request location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Check if the user's location matches the predefined location
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    double userLatitude = location.getLatitude();
                    double userLongitude = location.getLongitude();

                    // Calculate the distance between the user's location and the predefined location
                    double distance = calculateDistance(userLatitude, userLongitude, PREDEFINED_LAT, PREDEFINED_LON);

                    // Check if the distance is within the specified scanning radius
                    if (distance <= SCAN_RADIUS) {
                        // User's location is within the allowed radius, start camera
                        startCamera();
                    } else {
                        // User's location is outside the allowed radius
                        // Display an error message or take appropriate action
                        Toast.makeText(this, "Anda Diluar Area Presensi", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }
    }

    private void startCamera() {
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        // Handle the scanned QR code result
        String scanResult = result.getText();

        // Parse the scanned value as JSON
        try {
            JsonObject json = JsonParser.parseString(scanResult).getAsJsonObject();

            // Extract the values from the JSON object
            String locationName = json.get("locationName").getAsString();
            String entityName = json.get("entityName").getAsString();
            String locationDesc = json.get("locationDesc").getAsString();
            // Create a TimestampData object
            FirebaseUser currentUser = mAuth.getInstance().getCurrentUser();
            String uid = currentUser != null ? currentUser.getUid() : "";
            long currentDate = System.currentTimeMillis();
            long timeStamp = System.currentTimeMillis();

            // Create a TimestampData object with the extracted values, UID, and timestamp
            TimestampData timestampData = new TimestampData(locationName, entityName, locationDesc, uid, timeStamp, currentDate);

            // Perform actions based on the extracted values
            if (locationName.equals("PNM-KP") && entityName.equals("PNM") && locationDesc.equals("Menara PNM")) {
                // The scanned QR code matches the allowed QR code
                // Perform the desired action
                Toast.makeText(this, "Presensi Berhasil!", Toast.LENGTH_SHORT).show();

                // Save the scan result to Firebase
                saveScanResultToFirebase(timestampData);
            } else {
                // The scanned QR code does not match the allowed QR code
                // Show an error message or take appropriate action
                Toast.makeText(this, "Invalid QR code", Toast.LENGTH_SHORT).show();
            }

            // Create an Intent to pass the scanned result to another fragment or activity
            Intent intent = new Intent();
            intent.putExtra("scanResult", scanResult);
            intent.putExtra("timestamp", timeStamp);

            // Set the result code and data
            setResult(RESULT_OK, intent);

            // Finish the activity to return to the previous fragment or activity
            finish();
        } catch (JsonParseException e) {
            // Failed to parse the scanned value as JSON
            // Show an error message or take appropriate action
            Toast.makeText(this, "QR Code Salah!", Toast.LENGTH_SHORT).show();

            // Stop the camera processing
            if (scannerView != null) {
                scannerView.stopCamera();
                finish();
            }
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in kilometers
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // Convert to meters

        return distance;
    }

    private void saveScanResultToFirebase(TimestampData timestampData) {
        String key = dbref.push().getKey();
        dbref.child(key).setValue(timestampData);

    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                Toast.makeText(this, "Kamera tidak di izinkan!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}

    