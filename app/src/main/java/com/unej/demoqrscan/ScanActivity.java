package com.unej.demoqrscan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.IOException;

import pl.aprilapps.easyphotopicker.EasyImage;

public class ScanActivity extends AppCompatActivity implements View.OnClickListener, EasyImage.Callbacks {
    SurfaceView cameraView;
    BarcodeDetector barcode;
    CameraSource cameraSource;
    SurfaceHolder holder;

    private String pathImage;
    private TextView choose_fileqr, content;
    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        getSupportActionBar().hide();
        cameraView = (SurfaceView) findViewById(R.id.cameraView);
        cameraView.setZOrderMediaOverlay(true);
        holder = cameraView.getHolder();
        barcode = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        if(!barcode.isOperational()){
            Toast.makeText(getApplicationContext(), "Sorry, Couldn't setup the detector", Toast.LENGTH_LONG).show();
            this.finish();
        }
        cameraSource = new CameraSource.Builder(this, barcode)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(24)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1920,1024)
                .build();
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try{
                    if(ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        cameraSource.start(cameraView.getHolder());
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        barcode.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes =  detections.getDetectedItems();
                if(barcodes.size() > 0){
                    Intent intent = new Intent();
                    intent.putExtra("barcode", barcodes.valueAt(0));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });


        choose_fileqr = (TextView) findViewById(R.id.tv_choosefileqr);
        choose_fileqr.setOnClickListener(this);
        content = (TextView) findViewById(R.id.content);

    }


    @Override
    public void onClick(View v) {

        EasyImage.openGallery(ScanActivity.this, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, this);
    }

    @Override
    public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {

    }

    @Override
    public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
        Glide.with(ScanActivity.this)
                .load(imageFile)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        pathImage = imageFile.getAbsolutePath();

        System.out.println("Gallery img => " + imageFile.getAbsolutePath());

        if (pathImage.isEmpty()) {

        }

        scanQR(pathImage);
    }

    @Override
    public void onCanceled(EasyImage.ImageSource source, int type) {

    }

    private void scanQR(String pathImage) {
        BitmapFactory.Options bOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(pathImage, bOptions);
        //imageview.setImageBitmap(bitmap);

        BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                        .build();
        if(!detector.isOperational()){
            content.setText("Could not set up the detector!");
            Intent i = new Intent();
            i.putExtra("message", content.getText().toString());
            setResult(RESULT_OK, i);
            finish();
            return;
        }

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);

        try {
            Barcode thisCode = barcodes.valueAt(0);
            content.setText(thisCode.rawValue);

           if (barcodes.size() > 0) {
               Intent i = new Intent();
               i.putExtra("message",  barcodes.valueAt(0));
               setResult(RESULT_OK, i);
               finish();
           }
        } catch (Exception e) {
            e.printStackTrace();
            content.setText("Format Salah!");
            Intent i = new Intent();
            i.putExtra("message",content.getText().toString());
            setResult(RESULT_OK, i);
            finish();
        }


    }


    //private void scanQR(String pathImage) {
    //        BitmapFactory.Options bOptions = new BitmapFactory.Options();
    //        Bitmap bitmap = BitmapFactory.decodeFile(pathImage, bOptions);
    //        //imageview.setImageBitmap(bitmap);
    //
    //        BarcodeDetector detector = new BarcodeDetector.Builder(this)
    //                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
    //                .build();
    //        if(!detector.isOperational()){
    //            content.setText("Could not set up the detector!");
    //            Intent i = new Intent();
    //            i.putExtra("message",content.getText().toString());
    //            finish();
    //            return;
    //        }
    //
    //        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
    //        SparseArray<Barcode> barcodes = detector.detect(frame);
    //
    //        try {
    //            Barcode thisCode = barcodes.valueAt(0);
    //            content.setText(thisCode.rawValue);
    //
    //            showDialog("Berhasil Scan QR!");
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //            content.setText("Format Salah!");
    //            showDialog("Kesalahan Scan QR");
    //        }
    //    }
    //
    //    public void showDialog(String msg) {
    //        AlertDialog.Builder builder = new AlertDialog.Builder(this);
    //        builder.setTitle("Presensi QR From File")
    //                .setMessage(msg)
    //                .setCancelable(false)
    //                .setNegativeButton("OKE", new DialogInterface.OnClickListener() {
    //                    @Override
    //                    public void onClick(DialogInterface dialog, int which) {
    //                        dialog.dismiss();
    //                        finish();
    //                    }
    //                });
    //        AlertDialog dialog = builder.create();
    //        dialog.show();
    //    }
}
