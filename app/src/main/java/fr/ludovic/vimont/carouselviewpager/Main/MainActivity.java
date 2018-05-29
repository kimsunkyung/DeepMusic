package fr.ludovic.vimont.carouselviewpager.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;

import java.util.ArrayList;

import fr.ludovic.vimont.carouselviewpager.R;
import fr.ludovic.vimont.carouselviewpager.music;

public class MainActivity extends AppCompatActivity {
    private CarouselViewPager carousel;
    ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButton =(ImageButton)findViewById(R.id.music);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),music.class);
                startActivity(intent);
            }
        });

        carousel = (CarouselViewPager) findViewById(R.id.carousel);
        ArrayList<Entity> entities = buildData();
        CarouselAdapter carouselAdapter = new CarouselAdapter(this, carousel, getSupportFragmentManager(), entities);

        carousel.setAdapter(carouselAdapter);
        carousel.addOnPageChangeListener(carouselAdapter);
        carousel.setOffscreenPageLimit(entities.size());
        carousel.setClipToPadding(false);

        carousel.setScrollDurationFactor(1.5f);
        carousel.setPageWidth(0.55f);
        carousel.settPaddingBetweenItem(16);
        carousel.setAlpha(0.0f);
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            carousel.startAnimation(false, new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    carousel.setAlpha(1.0f);
                }

                @Override
                public void onAnimationEnd(Animation animation) { }

                @Override
                public void onAnimationRepeat(Animation animation) { }
            });
        }
    }

    private ArrayList<Entity> buildData() {
        ArrayList<Entity> entities = new ArrayList<>();

        entities.add(new Entity(R.drawable.bluemoon, "Blue Moon", getString(R.string.song1)));
        entities.add(new Entity(R.drawable.beautiful, "아름다운 밤이야", getString(R.string.song2)));
        entities.add(new Entity(R.drawable.energetic, "에너제틱", getString(R.string.song3)));
        entities.add(new Entity(R.drawable.mamamo, "넌 is 뭔들", getString(R.string.song4)));
        entities.add(new Entity(R.drawable.ikon, "사랑을 했다", getString(R.string.song5)));


        return entities;
    }


}
