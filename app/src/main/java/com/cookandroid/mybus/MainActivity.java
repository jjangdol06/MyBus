package com.cookandroid.mybus;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText etBus;
    Button btSearch, btLSearch;
    TextView tv, tvID;
    String tag = "bus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etBus = (EditText) findViewById(R.id.edtBusNum);
        btSearch = (Button) findViewById(R.id.btnSearch);
        btLSearch = (Button) findViewById(R.id.btnLSearch);
        tv = (TextView) findViewById(R.id.data);
        tvID = (TextView) findViewById(R.id.tvID);

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String srvUrl = "http://ws.bus.go.kr/api/rest/busRouteInfo/getBusRouteList"; //공공데이터포털 버스 노선 url 주소_개발 정보 확인getBusRouteList
                String srvKey = "Nq%2Fww%2FKNMUR06%2Fp2Cd5qNy%2F%2F8ZYqHSMjyNlg%2Bre9%2FbHhdnk92m1Kc2%2B0LIWXxojEmFgJneihFcryruTf43cEfw%3D%3D"; //인증키
                String strSrch = etBus.getText().toString(); //찾는 버스번호 가져오기
                String strUrl = srvUrl + "?ServiceKey=" + srvKey + "&strSrch=" + strSrch;
                //ServiceKey, strSrch는 openAPI에서 정해진 이름
                new DownloadWebpageTask().execute(strUrl); //async task >> background 작업
            }
        });
        btLSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String srvUrl = "http://ws.bus.go.kr/api/rest/busRouteInfo/getRoutePath "; //공공데이터포털 버스 노선 url 주소_개발 정보 확인getBusRouteList
                String srvKey = "Nq%2Fww%2FKNMUR06%2Fp2Cd5qNy%2F%2F8ZYqHSMjyNlg%2Bre9%2FbHhdnk92m1Kc2%2B0LIWXxojEmFgJneihFcryruTf43cEfw%3D%3D"; //인증키
                String strID = tvID.getText().toString(); //찾는 버스번호 가져오기
                String strUrl = srvUrl + "?ServiceKey=" + srvKey + "&busRouteId=" + strID;
                //ServiceKey, strSrch는 openAPI에서 정해진 이름

                new DownloadWebpageTask().execute(strUrl); //async task >> background 작업
            }
        });
    }//onCreate

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return (String) downloadUrl((String) urls[0]); //urls[0]는 strUrl을 downloadUrl(do in background 에 해당)에게 전달
            } catch (IOException e) {
                return "==>다운로드 실패";
            }
        }

        protected void onPostExecute(String result) {
            Log.d(tag, result);
            tv.append(result + "\n");
            tv.append("========== 파싱 결과 ==========\n"); //파싱은 xml의 태그를 보고 분석하는 것

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();
                boolean bSet = false, bSetgpsX=false,  bSetgpsY=false;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        if (/*tag_name.equals("busRouteNm") || */tag_name.equals("busRouteId"))  //다른 api 사용시 변경되는 부분
                            bSet = true;
                        if (tag_name.equals("gpsX"))  //다른 api 사용시 변경되는 부분
                            bSetgpsX = true;
                        if (tag_name.equals("gpsY"))  //다른 api 사용시 변경되는 부분
                            bSetgpsY = true;
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (bSet) {
                            //String content = xpp.getText();
                            tvID.setText(xpp.getText());
                            //tv.append(content + "\n");
                            bSet = false; //지나갔으니까 flag를 false로 하여 그 태그를 다시 찾을 수 있도록 해준다.
                        }
                        if (bSetgpsX) {
                                //String content = xpp.getText();
                                tv.append(xpp.getText());
                                //tv.append(content + "\n");
                                bSetgpsX = false; //지나갔으니까 flag를 false로 하여 그 태그를 다시 찾을 수 있도록 해준다.
                        }
                        if (bSetgpsY) {
                            //String content = xpp.getText();
                            tv.append(xpp.getText());
                            //tv.append(content + "\n");
                            bSetgpsY = false; //지나갔으니까 flag를 false로 하여 그 태그를 다시 찾을 수 있도록 해준다.
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
                tv.setText("\n" + e.getMessage());
            }
        }

        private String downloadUrl(String myurl) throws IOException {

            HttpURLConnection conn = null;
            try {
                Log.d(tag, "downloadUrl : " + myurl);
                URL url = new URL(myurl); //String 주소를 url에 해당하는 객체로 바꿔준다.
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8")); //결과 저장
                String line = null;
                String page = "";
                while ((line = bufreader.readLine()) != null) { //다시 쓸수 있도록 문자열 객체로 일어간다.
                    page += line;
                }
                return page;
            } catch (Exception e) {
                return " ";
            } finally {
                conn.disconnect();
            }
        }
    }
}
