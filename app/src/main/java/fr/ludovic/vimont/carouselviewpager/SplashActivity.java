package fr.ludovic.vimont.carouselviewpager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);

        if(CheckPermission(this)){

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }


    private Boolean CheckPermission(Activity act) {

        Boolean Permission = false;

        String[] PermissionCheckType = new String[3];
        PermissionCheckType[0] = Manifest.permission.READ_EXTERNAL_STORAGE;
        PermissionCheckType[1] = Manifest.permission.CAMERA;
        PermissionCheckType[2] = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        // 권한이 없는 경우
        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(act, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


            // 최초 요청 및 다시보지않기는 fasle
            // 사용자가 거절한 경우 true
            if (ActivityCompat.shouldShowRequestPermissionRationale(act, Manifest.permission.CAMERA) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(act, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(act, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(act, PermissionCheckType, 0);
            } else {
                ActivityCompat.requestPermissions(act, PermissionCheckType, 0);
            }


        } else {
            // 사용 권한이 있음을 확인한 경우
            Permission = true;
        }

        return Permission;
    }


    @SuppressLint("Override")
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                // 권한이 없는 경우
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, permissions, 0);

                }else{
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    startActivity(new Intent(this, MainActivity.class));
                    finish();

                }
        }
    }
}
