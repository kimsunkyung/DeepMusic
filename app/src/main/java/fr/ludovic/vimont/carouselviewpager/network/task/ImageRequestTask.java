package fr.ludovic.vimont.carouselviewpager.network.task;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import fr.ludovic.vimont.carouselviewpager.network.HttpRequest;
import fr.ludovic.vimont.carouselviewpager.network.result.ImageResultList;

/**
 * Created by SeungJun on 2017-07-07.
 */

public class ImageRequestTask extends AsyncTask<String, Integer, ImageResultList> {

    private ImageRequestTaskResultHandler handler;


    public interface ImageRequestTaskResultHandler{
        public void onSuccessExampleTask(ImageResultList result);
        public void onFailExampleTask();
        public void onCancelExampleTask();
    }



    public ImageRequestTask(ImageRequestTaskResultHandler handler){
        this.handler = handler;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ImageResultList doInBackground(String... strings) {

        String url = strings[0];
        String path = strings[1];

        Map<String, Object> params = new HashMap<String, Object>();

        if(strings[2] != null){
            params.put("photo", strings[2]);
        }


        ImageResultList result  = null;


        HttpRequest request = new HttpRequest();

        try {
            String str = request.callRequestServer(url, path,  "GET", null);

            Log.d("http", "str > " + str);


            Gson gson = new GsonBuilder().create();
            result = gson.fromJson(str, ImageResultList.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(ImageResultList taskResult) {
        super.onPostExecute(taskResult);

        if(taskResult != null){
            handler.onSuccessExampleTask(taskResult);
        }

    }
}
