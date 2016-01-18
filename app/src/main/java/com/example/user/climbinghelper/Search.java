package com.example.user.climbinghelper;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.renderscript.Element;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by user on 2015-12-30.
 */
public class Search extends ActionBarActivity{
        TextView textview;
        EditText editText;
        Document doc = null;
        private String key="D9%2BEupTMLVNVGCAVyrIODMgBtnnBNGT%2BUP6J6JHCfeKQeVjX%2Fm%2B29Uicpg%2Fn2j7rLSAfSEAKU5htKfxtLuBwNw%3D%3D";
        String query;
        Button button;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.search);
            ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

            if(cm.getActiveNetworkInfo()!=null){
                textview = (TextView) findViewById(R.id.textView);
                editText=(EditText)findViewById(R.id.editText);
                button=(Button)findViewById(R.id.search);

                button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        GetXMLTask task = new GetXMLTask();
                        String str= editText.getText().toString();
                        try{
                            query= URLEncoder.encode(str, "UTF-8");
                        }catch (UnsupportedEncodingException e1){
                            e1.printStackTrace();
                        }
                        task.execute("http://openapi.forest.go.kr/openapi/service/cultureInfoService/gdTrailInfoOpenAPI?ServiceKey=" + key + "&searchMtNm=" + query + "&numOfRows=999&pageSize=999&pageNo=1&startPage=1");
                    }
                });

            }else{
                Log.d("Test", "네트워크연결 안됨");
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("네트워크 에러");
                alertDialog.setMessage("네트워크 연결이 되지 않았습니다.\n확인을 누르면 설정창으로 이동합니다.");
                alertDialog.setButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent setting=new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                        setting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(setting);
                    }
                });
                alertDialog.show();
            }
        }

        private class GetXMLTask extends AsyncTask<String, Void, Document> {
            @Override
            protected Document doInBackground(String... urls) {
                URL url;
                try {
                    url = new URL(urls[0]);
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder(); //XML문서 빌더 객체를 생성
                    doc = db.parse(new InputSource(url.openStream())); //XML문서를 파싱한다.
                    doc.getDocumentElement().normalize();

                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
                }
                return doc;
            }

            @Override
            protected void onPostExecute(Document doc) {

                String s = "";
                String resultP1=""; String resultP2=""; String resultBR1=""; String resultlt="";
                String result="";

                int i=0;
                //data태그가 있는 노드를 찾아서 리스트 형태로 만들어서 반환
                NodeList nodeList = doc.getElementsByTagName("item");
                //data 태그를 가지는 노드를 찾음, 계층적인 노드 구조를 반환
                for (i = 0; i < nodeList.getLength(); i++) {
                    //날씨 데이터를 추출
                    s += "<산정보> \n\n";
                    Node node = nodeList.item(i); //data엘리먼트 노드
                    org.w3c.dom.Element fstElmnt = (org.w3c.dom.Element) node;

                    NodeList nameList = fstElmnt.getElementsByTagName("mntnm");
                    org.w3c.dom.Element nameElement = (org.w3c.dom.Element) nameList.item(0);
                    nameList = nameElement.getChildNodes();

                    s += "* 이름 = " + ((Node) nameList.item(0)).getNodeValue() + "\n\n";

                    NodeList detailList = fstElmnt.getElementsByTagName("aeatreason");
                    s += "* 설명 = " + detailList.item(0).getChildNodes().item(0).getNodeValue() + "\n\n";

                    NodeList courseList = fstElmnt.getElementsByTagName("etccourse");
                    s += "* 경로 = " + courseList.item(0).getChildNodes().item(0).getNodeValue() + "\n\n";

                    NodeList transportList = fstElmnt.getElementsByTagName("transport");
                    s += "* 오시는길 = " + transportList.item(0).getChildNodes().item(0).getNodeValue() + "\n\n";

                    NodeList tourList = fstElmnt.getElementsByTagName("tourisminf");
                    s += "* 기타정보 = " + tourList.item(0).getChildNodes().item(0).getNodeValue() + "\n\n";
                }
                s+= i +"개가 검색되었습니다.\n";
                resultP1=s.replace("<P>", " ");
                resultP2=resultP1.replace("</P>", " ");
                resultBR1=resultP2.replace("<BR>", "\n");
                resultlt=resultBR1.replace("&lt;", " ");
                result=resultlt.replace("&gt;"," ");
                textview.setText(result);

                super.onPostExecute(doc);
            }

        }//end inner class - GetXMLTask

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml

        //noinspection SimplifiableIfStatement
        switch(item.getItemId()){
            case R.id.info:
                Intent intent1=new Intent(this, Search.class);
                startActivity(intent1);
                return true;

            case R.id.login:
                Intent intent2=new Intent(this, Login.class);
                startActivity(intent2);
                return true;

            case R.id.recode:
                Intent intent3=new Intent(this, Recode.class);
                startActivity(intent3);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }
}