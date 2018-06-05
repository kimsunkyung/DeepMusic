package fr.ludovic.vimont.carouselviewpager.network;


import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

/**
 * https / http 통신을 요청하는 클래스
 * method 방식에 따라 각 method에 알맞는 파라미터를 만들어
 * 서버측으로 전송한 뒤 응답을 받는다
 * Created by SeungJun on 2017-07-07.
 */

public class HttpRequest {

    private static final String TAG = "HttpRequest";
    private static final int TIMEOUT_CONNECTION = 30000;
    private static final int TIMEOUT_READ = 30000;


    /**
     * 서버 실행 요청을 전달하는 함수
     * @param serverUrl 요청할 서버 url
     * @param api_path 요청할 api의 경로
     * @param method post / get
     * @param params 각 방식에 맞는 파라미터
     * @return 서버에서 받은 json 결과를 string으로 리턴
     * @throws Exception 주로 connection이 원활히 이루어지지 않았을 때 발생한다.
     */
    public static String callRequestServer (String serverUrl, String api_path, String method, Map<String, Object> params) throws Exception {
        return startRequest(serverUrl, api_path, method, params);
    }


    /**
     * 실질적으로 서버쪽에 요청하는 함수
     * @param serverUrl 요청할 서버 url
     * @param api_path 요청할 api의 경로
     * @param method post / get
     * @param params 각 방식에 맞는 파라미터
     * @return 서버에서 받은 json 결과를 string으로 리턴
     * @throws Exception 주로 connection이 원활히 이루어지지 않았을 때 발생한다.
     */
    public static String startRequest(String serverUrl, String api_path, String method, Map<String, Object> params) throws Exception {
        URL url = null;

        if (method.equalsIgnoreCase("get")){
            url = createUrl(serverUrl, api_path, params);
        }else {
            url = createUrl(serverUrl, api_path, null);
        }

        Log.d("Http"," url > " + url.toString());

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(TIMEOUT_CONNECTION); //연결 타임아웃
            connection.setReadTimeout(TIMEOUT_READ); // 데이터 읽어들이는 타임아웃
            connection.setRequestMethod(method); // post or get

            //원하는 프로퍼티 설정 //
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json;charset=UTF-8");
            connection.setRequestProperty("Connection", "Keep-Alive");


            if (method.equals("GET") || method.equals("DELETE")) {
                //get 방식일 경우에는 별도 파라미터 전송이 없으므로 일단 output 옵션은 끄도록 한다.
                connection.setDoOutput(false);
            } else {

                Gson gson = new Gson();
                String postJson = gson.toJson(params);

                Log.d("Http"," postJson > " + postJson);

                connection.setDoOutput(true); //post는 파라미터를 url이 아닌 별도로 전송하므로 output 옵션 활성화
                connection.setRequestProperty("Content-Length", Integer.toString(postJson.getBytes("utf-8").length));
                OutputStream os = connection.getOutputStream();
                os.write(postJson.getBytes("utf-8"));
                os.flush();
                os.close();
            }

        }catch (Exception e) {
            e.printStackTrace();
            throw new Exception("API response error : " + 0);
        }

        int responseCode = 0; //서버에서 전달받은 응답 코드를 확인한다

        try {
            responseCode = connection.getResponseCode();

        }catch (InterruptedIOException e){
            e.printStackTrace();

        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        if (responseCode != HttpURLConnection.HTTP_OK) { // 정상적인 상황이 아닐때에는 에러를 던진다.
            throw new Exception("API response error : " + responseCode);
        }

        //에러 상황 없이 연결 성공 후 응답 return 받은 이후
        //전달 받은 버퍼 리딩 작업
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuffer result = new StringBuffer();
        String buf = "";
        buf = reader.readLine();
        while(buf != null) {
            result.append(buf).append("\r\n");
            buf = reader.readLine();
        }

        String resultStr = result.toString();

        return resultStr;
    }


    /**
     * 요청할 url을 만들어주는 함수
     * @param serverUrl 요청할 서버 주소
     * @param api_path 요청할 api 경로
     * @param params 파라미터를 넘겨주면 되지만 post의 경우는 별도로 content를 생성하므로 주로 Get방식일 때에만 사용하면 됨.
     *               get이 아닐경우에는 null을 넘겨주면 됨.
     * @return 전달받은 데이터를 기반으로 만든 URL 객체를 리턴
     */
    public static URL createUrl (String serverUrl, String api_path, Map<String, Object> params) throws MalformedURLException, UnsupportedEncodingException {

        boolean isFirst = true; // ?를 붙이기 위한 구분값

        StringBuffer urlStr = new StringBuffer();
        urlStr.append(serverUrl);
        urlStr.append(api_path);

        if(params != null && params.size() > 0){
            Iterator<String> paramsKey = params.keySet().iterator();

            while (paramsKey.hasNext()){

                String key = paramsKey.next();
                Object data = params.get(key);

                if(data != null && data.toString().length() > 0){

                    if(isFirst){
                        urlStr.append(key).append("=").append(URLEncoder.encode(data.toString(), "utf-8"));
                        isFirst = false;
                    }
                    else{
                        urlStr.append("&").append(key).append("=").append(URLEncoder.encode(data.toString(), "utf-8"));
                    }

                }
            }

        }

        return new URL(urlStr.toString());
    }

}
