package fr.ludovic.vimont.carouselviewpager.network.task;

import android.os.AsyncTask;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import fr.ludovic.vimont.carouselviewpager.network.HttpRequest;

/**
 * Created by SeungJun on 2017-07-07.
 */

public class ImageRequestTask extends AsyncTask<String, Integer, String> {

    private ImageRequestTaskResultHandler handler;


    public interface ImageRequestTaskResultHandler{
        public void onSuccessExampleTask(String result);
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
    protected String doInBackground(String... strings) {

        String url = strings[0];
        String path = strings[1];

        Map<String, Object> params = new HashMap<String, Object>();

        if(strings[2] != null){
            params.put("img", strings[2]);
        }

        params.put("key", String.valueOf(System.currentTimeMillis()));

        String result  = null;


        HttpRequest request = new HttpRequest();

        try {
            String str = request.callRequestServer(url, path,  "POST", params);

            Log.d("http", "str > " + str);

           result = str;




        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(result != null){
            handler.onSuccessExampleTask(result);
        }

    }
}
