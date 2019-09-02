package com.unej.demoqrscan;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;

import pl.aprilapps.easyphotopicker.EasyImage;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
//public class MainActivity extends AppCompatActivity implements View.OnClickListener, EasyImage.Callbacks {

    private ImageView imageview;
    private Button btn_scan;
    private TextView content;


    private String pathImage;

    Button scanbtn;
    TextView result;
    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        imageview = (ImageView) findViewById(R.id.imageView);
        btn_scan = (Button) findViewById(R.id.scanbtn);
        content = (TextView) findViewById(R.id.content);

        imageview.setOnClickListener(this);
        btn_scan.setOnClickListener(this);
        result = (TextView) findViewById(R.id.result);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.imageView:
                EasyImage.openGallery(MainActivity.this, 0);
                break;
            case R.id.scanbtn:
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivityForResult(intent, REQUEST_CODE);

                startActivityForResult(new Intent(getApplicationContext(), ScanActivity.class),999);

                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            if(data != null){
                final Barcode barcode = data.getParcelableExtra("barcode");
                result.post(new Runnable() {
                    @Override
                    public void run() {
                        result.setText(barcode.displayValue);
                    }
                });
            }
        }

        if (requestCode == 999 && resultCode == RESULT_OK) {
            result.setText(data.getStringExtra("message"));
        }

        super.onActivityResult(requestCode, resultCode, data);

    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        EasyImage.handleActivityResult(requestCode, resultCode, data, this, this);
//    }
//
//    @Override
//    public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
//
//    }
//
//    @Override
//    public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
//        Glide.with(MainActivity.this)
//                .load(imageFile)
//                .diskCacheStrategy(DiskCacheStrategy.ALL);
//
//        pathImage = imageFile.getAbsolutePath();
//
//        System.out.println("Gallery img => " + imageFile.getAbsolutePath());
//
//        if (pathImage.isEmpty()) {
//
//        }
//
//        scanQR(pathImage);
//    }
//
//    @Override
//    public void onCanceled(EasyImage.ImageSource source, int type) {
//
//    }
//
//    private void scanQR(String pathImage) {
//        BitmapFactory.Options bOptions = new BitmapFactory.Options();
//        Bitmap bitmap = BitmapFactory.decodeFile(pathImage, bOptions);
//        //imageview.setImageBitmap(bitmap);
//
//        BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext())
//                        .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
//                        .build();
//        if(!detector.isOperational()){
//            content.setText("Could not set up the detector!");
//            return;
//        }
//
//        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
//        SparseArray<Barcode> barcodes = detector.detect(frame);
//
//        try {
//            Barcode thisCode = barcodes.valueAt(0);
//            content.setText(thisCode.rawValue);
//        } catch (Exception e) {
//            e.printStackTrace();
//            content.setText("Format Salah!");
//        }
//
//
//    }
}
