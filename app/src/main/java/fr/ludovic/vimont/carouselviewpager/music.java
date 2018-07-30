package fr.ludovic.vimont.carouselviewpager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import fr.ludovic.vimont.carouselviewpager.Camera.ImageCropActivity;
import fr.ludovic.vimont.carouselviewpager.network.task.ImageRequestTask;

import static fr.ludovic.vimont.carouselviewpager.Camera.ImageCropActivity.PICK_ALBUM;
import static fr.ludovic.vimont.carouselviewpager.Camera.ImageCropActivity.PICK_CAMERA;

public class music extends AppCompatActivity implements View.OnClickListener {

    ImageView imageButton;
    private Uri mImageCaptureUri;

    private boolean isSelectImage = false;

    private String picData = "";

    private Button selectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        imageButton = (ImageView)findViewById(R.id.imageView2);

        selectBtn = (Button) findViewById(R.id.button3);
        selectBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(!isSelectImage){
                    Toast.makeText( music.this, "이미지부터 선택하세요", Toast.LENGTH_LONG).show();
                    return;
                }

                ImageRequestTask requestTask = new ImageRequestTask(new ImageRequestTask.ImageRequestTaskResultHandler() {
                    @Override
                    public void onSuccessExampleTask(String result) {
                        //Toast.makeText(music.this, "", Toast.LENGTH_SHORT).show();

                        if(result.equals("angry\r\n")){

                            int[] img = {R.drawable.an1, R.drawable.an2, R.drawable.an3, R.drawable.an4};

                            Random random = new Random();
                            int num = random.nextInt(img.length);

                            Intent intent=new Intent(music.this,ResultActivity.class);
                            intent.putExtra("randomImg",img[num]);
                            startActivity(intent);

                        }



                        else if(result.equals("sad\r\n")){

                            int[] img = {R.drawable.sa2, R.drawable.sa3, R.drawable.sa4,R.drawable.sa5};

                            Random random = new Random();
                            int num = random.nextInt(img.length);

                            Intent intent=new Intent(music.this,ResultActivity.class);
                            intent.putExtra("randomImg",img[num]);
                            startActivity(intent);

                        }

                        else if(result.equals("happy\r\n")||result.equals("surprise\r\n")){

                            int[] img = {R.drawable.ha1, R.drawable.ha2, R.drawable.ha3, R.drawable.ha4,R.drawable.ha5};

                            Random random = new Random();
                            int num = random.nextInt(img.length);

                            Intent intent=new Intent(music.this,ResultActivity.class);
                            intent.putExtra("randomImg",img[num]);
                            startActivity(intent);

                        }
                    }

                    @Override
                    public void onFailExampleTask() {

                    }

                    @Override
                    public void onCancelExampleTask() {

                    }
                });


                requestTask.execute("http://52.79.148.129/", "upload", picData);
            }
        });



        imageButton.setOnClickListener(this);
    }

    /**
     * 카메라에서 이미지 가져오기
     */
    private void doTakePhotoAction()
    {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        File photo = new File(getExternalCacheDir(), "DeepMusic_" + System.currentTimeMillis() + ".png");

        try {
            photo.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mImageCaptureUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photo);
        } else {
            mImageCaptureUri = Uri.fromFile(photo);
        }

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

        startActivityForResult(intent, PICK_CAMERA);
    }

    /**
     * 앨범에서 이미지 가져오기
     */
    private void doTakeAlbumAction()
    {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {


        switch (requestCode) {

            case PICK_CAMERA: // 카메라에서 선택
                Intent intent = new Intent(this, ImageCropActivity.class);
                intent.putExtra("image", mImageCaptureUri);
                intent.putExtra("mode", PICK_CAMERA);
                startActivityForResult(intent, 300);

                break;

            case PICK_ALBUM: // 사진 앨범에서 선택

                if (data == null) {
                    break;

                } else {
                    Uri imageUri = data.getData();
                    Intent intent1 = new Intent(this, ImageCropActivity.class);
                    intent1.putExtra("image", imageUri);
                    intent1.putExtra("mode", PICK_ALBUM);
                    startActivityForResult(intent1, 300);
                }

                break;

            case 300:
                if(data != null){
                    Bitmap bitmap = (Bitmap) data.getParcelableExtra("bitmap");

                    if(bitmap != null){
                        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOS);

                        byte[] byteArray = byteArrayOS.toByteArray();   //바이트로 변환
                        picData = Base64.encodeToString(byteArray, 0);  //스트링으로 변환

                        imageButton.setImageBitmap(bitmap);

                        isSelectImage = true;
                    }

                }

                break;
        }

    }

    public void onClick(View v)
    {
        DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                doTakePhotoAction();
            }
        };

        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                doTakeAlbumAction();
            }
        };

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(this)
                .setTitle("업로드할 이미지 선택")
                .setPositiveButton("취소", cancelListener)
                .setNeutralButton("앨범선택", albumListener)
                .setNegativeButton("사진촬영", cameraListener)
                .show();
    }


}