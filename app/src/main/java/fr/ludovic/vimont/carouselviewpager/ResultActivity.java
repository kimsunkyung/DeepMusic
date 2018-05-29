package fr.ludovic.vimont.carouselviewpager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ResultActivity extends AppCompatActivity {

    ImageView resultImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        resultImage = (ImageView)findViewById(R.id.resultimage);

    }

}
