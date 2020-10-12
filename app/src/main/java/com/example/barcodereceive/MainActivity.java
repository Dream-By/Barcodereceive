package com.example.barcodereceive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    public static final String BARCODE_BROADCAST = "com.clover.BarcodeBroadcast";
    private final BarcodeReceiver barcodeReceiver = new BarcodeReceiver();
    private TextView mTextView;
    private CheckBox checkBox;
    private String scannedBarcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.barcode);
        checkBox = findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerBarcodeScanner();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterBarcodeScanner();
    }

    private void registerBarcodeScanner() {
        registerReceiver(barcodeReceiver, new IntentFilter(BARCODE_BROADCAST));
    }

    private void unregisterBarcodeScanner() {
        unregisterReceiver(barcodeReceiver);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        displayBarcode();
    }

    private void displayBarcode() {
        if (scannedBarcode == null)
            return;

        String barcode = scannedBarcode;
        // Clover OrderIds are sometimes encoded to fit in the barcode (Mini/Mobile Printer specifically)
        // If it is not the full OrderId, then it will be a prefix that you can filter by.
        if (checkBox.isChecked())
            barcode = BarcodeIdUtil.safeBase64toBase32(barcode);

        mTextView.setText(getString(R.string.barcode_scanned,barcode));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    private class BarcodeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BARCODE_BROADCAST)) {
                String barcode = intent.getStringExtra("Barcode");
                if (barcode != null) {
                    scannedBarcode = barcode;
                    displayBarcode();
                }
            }
        }
    }
}