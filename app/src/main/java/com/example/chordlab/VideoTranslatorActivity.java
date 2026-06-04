package com.example.chordlab;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

public class VideoTranslatorActivity extends AppCompatActivity {

    private ImageView btnBackToDashboard;
    private CardView cardUploadArea;
    private TextView tvUploadStatusText, tvSelectedFileName, tvResultsDisplay;
    private AppCompatButton btnProcessAudio;
    private ProgressBar progressBar;

    // Uri tracking context reference for your friends to pull raw byte data from
    private Uri selectedAudioUri = null;

    // Modern Android Storage Access Framework File Picker Launcher
    private final ActivityResultLauncher<String> audioPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedAudioUri = uri;
                    String fileName = getFileNameFromUri(uri);

                    // UI STATE CHANGE: Update views to show selected file name status
                    tvUploadStatusText.setText("File Ready!");
                    tvSelectedFileName.setText(fileName);

                    // Enable the action button now that a file is prepared
                    btnProcessAudio.setEnabled(true);
                    btnProcessAudio.setAlpha(1.0f);

                    Toast.makeText(this, "Audio selected successfully", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_translator);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        btnBackToDashboard = findViewById(R.id.btnBackToDashboard);
        cardUploadArea     = findViewById(R.id.cardUploadArea);
        tvUploadStatusText = findViewById(R.id.tvUploadStatusText);
        tvSelectedFileName = findViewById(R.id.tvSelectedFileName);
        btnProcessAudio    = findViewById(R.id.btnProcessAudio);
        progressBar        = findViewById(R.id.progressBar);
        tvResultsDisplay   = findViewById(R.id.tvResultsDisplay);
    }

    private void setupListeners() {
        btnBackToDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(VideoTranslatorActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        // Launches system selector filtering exclusively for standard generic audio streams
        cardUploadArea.setOnClickListener(v -> audioPickerLauncher.launch("audio/*"));

        btnProcessAudio.setOnClickListener(v -> {
            if (selectedAudioUri != null) {
                simulateProcessingState();
            }
        });
    }

    // ── SIMULATION PLACEHOLDER FOR BACKEND CODE ────────────────────────────
    private void simulateProcessingState() {
        // Toggle UI elements to processing mode
        progressBar.setVisibility(View.VISIBLE);
        btnProcessAudio.setEnabled(false);
        btnProcessAudio.setAlpha(0.5f);
        tvResultsDisplay.setText("Analyzing audio frequencies and detecting musical notes...");

        // Simulated runtime delay handler (3 seconds) representing processing latency
        progressBar.postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            btnProcessAudio.setEnabled(true);
            btnProcessAudio.setAlpha(1.0f);

            // ── HOOK POINT FOR YOUR FRIENDS ──
            // Tell your friends they can insert their main AI model execution loop or web API payload post right here!
            // They can use 'selectedAudioUri' to get the file data.

            // Setting mockup sequence results text output area display matrix
            tvResultsDisplay.setText("🎉 Processing Complete!\n\nDetected Chord Progression Sequence:\n[ C Major ] ➔ [ G Major ] ➔ [ A Minor ] ➔ [ F Major ]");
            tvResultsDisplay.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            Toast.makeText(this, "Analysis finished!", Toast.LENGTH_SHORT).show();
        }, 3000);
    }

    // Helper method to parse clean file names from a storage ContentProvider URI
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}