package fr.ludovic.vimont.carouselviewpager.Camera;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import fr.ludovic.vimont.carouselviewpager.BuildConfig;
import fr.ludovic.vimont.carouselviewpager.R;

/**
 * Created by SeungJun on 2018-05-03.
 */

public class ImageCropActivity extends AppCompatActivity implements View.OnClickListener {

    // 카메라 선택 플래그 값
    public static final int PICK_CAMERA = 101;
    // 앨범 선택 플래그 값
    public static final int PICK_ALBUM = 102;

    // 이미지 편집 화면에서의 카메라 선택 플래그 값
    public static final int PICK_CAMERA_FROM_CROP = 201;
    // 이미지 편집 화면에서의 앨범 선택 플래그 값
    public static final int PICK_ALBUM_FROM_CROP = 202;

    private LinearLayout mParentView;

    //사진 확대 축소 이동이 가능한 제스쳐뷰
    private CropImageView cropView;

    //카메라로 찍었을 때 나올 하단 메뉴 뷰
    private RelativeLayout mViewCamera;
    //앨범으로 선택했을 때 나올 하단 메뉴 뷰
    private RelativeLayout mViewAlbum;

    private TextView mRetryCamera;
    private TextView mUseImage;

    private TextView mSelectAlbum;
    private TextView mUserImage2;

    private int mode = 0;

    //최초 진입시 넘겨받은 이미지 url
    private Uri imageUri;
    //여기에서 생성한 이미지 url
    private Uri selectedImageUri;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crop_view);

        imageUri = (Uri) getIntent().getParcelableExtra("image");
        mode = getIntent().getIntExtra("mode", 0);

        mParentView = (LinearLayout) findViewById(R.id.parent_view);

        cropView = (CropImageView) findViewById(R.id.crop_view);

        mViewCamera = (RelativeLayout) findViewById(R.id.view_camera);
        mViewAlbum = (RelativeLayout) findViewById(R.id.view_album);

        mRetryCamera = (TextView) findViewById(R.id.retry_camera);
        mUseImage = (TextView) findViewById(R.id.crop_image);

        mSelectAlbum = (TextView) findViewById(R.id.retry_album);
        mUserImage2 = (TextView) findViewById(R.id.choice_image);

        cropView.setMinFrameSizeInDp(100);
        cropView.setInitialFrameScale(0.75f);
        cropView.setTouchPaddingInDp(16);

        mRetryCamera.setOnClickListener(this);
        mUseImage.setOnClickListener(this);
        mSelectAlbum.setOnClickListener(this);
        mUserImage2.setOnClickListener(this);

        if (mode == PICK_CAMERA) { // 카메라로 찍은 이미지를 받을 때
            mViewCamera.setVisibility(View.VISIBLE);
            mViewAlbum.setVisibility(View.GONE);

            try {
                Bitmap bitmap = null;
                ExifInterface ei = null;

                /*
                버전에 따라 file path에 접근하는 방식이 다름
                */
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, options);

                if(cropView.getWidth() > 0 || cropView.getHeight() > 0){
                    options.inSampleSize = calculateInSampleSize(options, cropView.getWidth(), cropView.getHeight());
                }

                options.inJustDecodeBounds = false;
                options.inPurgeable = true;

                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, options);

                if(bitmap != null){

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {

                        ei = new ExifInterface(imageUri.getPath());

                    }else{

                        InputStream input = getContentResolver().openInputStream(imageUri);
                        ei = new ExifInterface(input);
                    }

                    bitmap = rotate(ei, bitmap, 100);

                    cropView.setImageBitmap(bitmap);

                }else{
                    finish();
                }

            } catch (Exception e) {
                e.printStackTrace();

                finish();
            }

        } else { // 앨범에서 선택했을 때
            mViewCamera.setVisibility(View.GONE);
            mViewAlbum.setVisibility(View.VISIBLE);

            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, options);

                if(cropView.getWidth() > 0 || cropView.getHeight() > 0){
                    options.inSampleSize = calculateInSampleSize(options, cropView.getWidth(), cropView.getHeight());
                }

                options.inJustDecodeBounds = false;
                options.inPurgeable = true;

                Bitmap photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, options);

                photo = rotate(new ExifInterface(getRealPathFromURI(imageUri)), photo, 100);

                cropView.setImageBitmap(photo);

            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case PICK_CAMERA_FROM_CROP: // 카메라로 찍은 이미지를 받을 때

                mViewCamera.setVisibility(View.VISIBLE);
                mViewAlbum.setVisibility(View.GONE);

                try {

                    Bitmap bitmap = null;
                    ExifInterface ei = null;

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri), null, options);

                    if(cropView.getWidth() > 0 || cropView.getHeight() > 0){
                        options.inSampleSize = calculateInSampleSize(options, cropView.getWidth(), cropView.getHeight());
                    }

                    options.inJustDecodeBounds = false;
                    options.inPurgeable = true;

                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri), null, options);

                    if(bitmap != null){

                        /*
                         버전에 따라 file path에 접근하는 방식이 다름
                        */
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {

                            ei = new ExifInterface(selectedImageUri.getPath()); // 회전각을 구하기 위한 path 설정

                        } else {

                        /*
                        회전각을 구해야하는데 7.0 이상에서는 uri에 대한 path를 함부로 접근할 수 없어
                        해당 uri를 inputStream으로 변환 후 사용한다
                         */
                            InputStream input = getContentResolver().openInputStream(selectedImageUri);
                            ei = new ExifInterface(input);
                        }

                        // 지정해준 퀄리티에 맞게 알맞은 압축 및 회전을 실행 후 다시 셋팅
                        bitmap = rotate(ei, bitmap, 100);

                        cropView.setImageBitmap(bitmap);
                    }else{
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                    finish();
                }

                break;

            case PICK_ALBUM_FROM_CROP:  //앨범에서 선택했을 때
                mViewCamera.setVisibility(View.GONE);
                mViewAlbum.setVisibility(View.VISIBLE);

                if (data == null) {

                    finish();

                    break;

                } else {
                    // OS 버전 구분없이 intent 데이터를 받아 온 뒤
                    Uri imageUri = data.getData();

                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, options);

                        if(cropView.getWidth() > 0 || cropView.getHeight() > 0){
                            options.inSampleSize = calculateInSampleSize(options, cropView.getWidth(), cropView.getHeight());
                        }

                        options.inJustDecodeBounds = false;
                        options.inPurgeable = true;

                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, options);

                        // bitmap을 압축 및 회전 적용
                        bitmap = rotate(new ExifInterface(getRealPathFromURI(imageUri)), bitmap, 100);

                        cropView.setImageBitmap(bitmap);

                    } catch (Exception e ) {
                        e.printStackTrace();

                        finish();
                    } catch (OutOfMemoryError e){
                        e.printStackTrace();

                        finish();
                    }
                }
                break;

        }
    }

    @Override
    public void onClick(View view) {

        Intent intent = null;

        switch (view.getId()) {

            case R.id.retry_camera: // 다시찍기

                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // 임시 캐시폴더에 이미지를 저장하기 위한 작업
                File photo = new File(getExternalCacheDir(), "DeepMusic_" + System.currentTimeMillis() + ".png");

                try {
                    // 파일 생성
                    photo.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*
                버전에 따라 file uri를 접근하는 방식이 다르다.
                특히 7.0 이상에서는 권한 문제로 인해 함부로 접근이 불가하여 xml 폴더에 있는 provider에 먼저 정의 후
                접근 할 수 있도록 작업해야함
                */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    selectedImageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photo);
                } else {
                    selectedImageUri = Uri.fromFile(photo);
                }

                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, selectedImageUri);
                startActivityForResult(intent, PICK_CAMERA_FROM_CROP);
                break;

            case R.id.retry_album: // 앨범 다시 선택
                intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_ALBUM_FROM_CROP);
                break;


            case R.id.crop_image:
            case R.id.choice_image: // 이미지 자르기

                cropView.crop(selectedImageUri).execute(new CropCallback() {

                    @Override
                    public void onSuccess(Bitmap cropped) {

                        Intent intent1 = getIntent();


                        if(cropped != null){

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            cropped.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                            Bitmap converted = Bitmap.createScaledBitmap(cropped, 300, 400, true);

                            if (converted != null) {
                                intent1.putExtra("bitmap", (Bitmap)converted);
                            }
                        }

                        setResult(RESULT_OK, intent1);

                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {

                        finish();
                    }
                });

                break;

        }

    }


    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 2;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private Bitmap rotate(ExifInterface ei, Bitmap bitmap, int quality)
    {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);

        // 이미지 정보 객체에서 회전에 대한 정보를 추출
        int exifOrientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        // 해당 정보를 기반으로 int 회전각 수치를 추출
        int exifDegree = exifOrientationToDegrees(exifOrientation);

        if (exifDegree != 0 && bitmap != null)
        {
            Matrix m = new Matrix();

            // 회전각을 적용 시키고 일단은 해당 사진 크기의 절반 정도 크기로 줄인다
            m.setRotate(exifDegree, bitmap.getWidth(), bitmap.getHeight());

            try
            {
                // 회전 !
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);

                if (bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch (OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환.
                ex.printStackTrace();
            }
        }
        return bitmap;
    }

    private int exifOrientationToDegrees(int exifOrientation)
    {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    private String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try
        {
            // url에 _data 컬럼 데이터를 전체 가져온다
            cursor = getContentResolver().query(contentUri, projection, null, null, null);

            //커서를 처음으로 이동시키고
            if (cursor != null && cursor.moveToFirst())
            {
                // 지정한 컬럼 인덱스 가져온 뒤
                final int index = cursor.getColumnIndexOrThrow(column);
                // 해당하는 정보를 string으로 반환
                return cursor.getString(index);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

}
