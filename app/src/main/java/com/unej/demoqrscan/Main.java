package com.unej.demoqrscan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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

public class Main extends AppCompatActivity implements View.OnClickListener, EasyImage.Callbacks {

    private Button file_button;
    private ImageView image_camera;
    private TextView tv_result;
    private CoordinatorLayout coordinatorLayout;

    private String pathImage;

    public static final int REQUEST_CODE = 1;
    public static final int REQUEST_CODE_FILE = 7458;
    public static final int PERMISSION_REQUEST = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        file_button = (Button) findViewById(R.id.btn_file);
        image_camera = (ImageView) findViewById(R.id.image_camera);
        tv_result = (TextView) findViewById(R.id.tv_result);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatLayout);

        file_button.setOnClickListener(this);
        image_camera.setOnClickListener(this);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        }
    }

    @Override
    public void onClick(View v) {
        int id  = v.getId();

        switch (id) {
            case R.id.btn_file:
                EasyImage.openGallery(this, REQUEST_CODE_FILE);
                break;
            case R.id.image_camera:
                startActivityForResult(new Intent(this, CameraActivity.class), REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("RequsetCode Gallery => " + requestCode);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            if(data != null){
                final Barcode barcode = data.getParcelableExtra("barcode");
                tv_result.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_result.setText(barcode.displayValue);
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Berhasil!", Snackbar.LENGTH_INDEFINITE)
                                .setAction("OKE", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });

                        snackbar.show();
                    }
                });
            }
        }

        if(requestCode == REQUEST_CODE_FILE && resultCode == RESULT_OK){
            EasyImage.handleActivityResult(requestCode, resultCode, data, this, this);
        } else {
            System.out.println("FAIL!");
        }

    }

    @Override
    public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {

    }

    @Override
    public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
        Glide.with(this)
                .load(imageFile)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        pathImage = imageFile.getAbsolutePath();
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
            tv_result.setText("Could not set up the detector!");
            return;
        }

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);

        try {
            Barcode thisCode = barcodes.valueAt(0);
            tv_result.setText(thisCode.rawValue);

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Berhasil!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OKE", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
            tv_result.setText("Format Salah!");
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Kesalahan saat membaca QRCode", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OKE", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });;
            snackbar.show();
        }

    }
}
