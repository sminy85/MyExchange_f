package com.example.myexchange;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.zip.Inflater;

import jxl.Sheet;
import jxl.Workbook;

public class SecondActivity extends TabActivity implements OnMapReadyCallback { //TabActivity를 상속받는다.

    GoogleMap gMap;
    MapFragment mapFrag;
    GroundOverlayOptions videoMark;

    ListView listview;
    ListViewAdapter adapter;
    String tag = "country_alert";
    EditText search_country;
    Button search_country_button;
    TextView single_result;
    Spinner countrylist1, countrylist2;
    WebView webView;

    private NotesDbAdapter dbAdapter;

    private static final String TAG = "NotesDbAdapter";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);

        TabHost tabHost = getTabHost();

        //TabSpec은 LinearLayout의 갯수만큼 필요하다.
        TabHost.TabSpec tabSmap = tabHost.newTabSpec("지도").setIndicator("지도");
        tabSmap.setContent(R.id.map);
        tabHost.addTab(tabSmap);

        TabHost.TabSpec tabSexchangerate = tabHost.newTabSpec("환율").setIndicator("환율");
        tabSexchangerate.setContent(R.id.exchange);
        tabHost.addTab(tabSexchangerate);

        TabHost.TabSpec tabScommunity = tabHost.newTabSpec("커뮤니티").setIndicator("커뮤니티");
        tabScommunity.setContent(R.id.webcafe);
        tabHost.addTab(tabScommunity);
        listview = (ListView)findViewById(R.id.listview);
        String names[]={"미국||USA","유럽||EUROP","베트남||VIETNAM","아시아||ASIA" };
        int icons[]={R.drawable.usa, R.drawable.europe, R.drawable.vietnam, R.drawable.asia};
        adapter = new ListViewAdapter();

        for(int i =0;i<4;i++){
            adapter.addItem(this.getResources().getDrawable(icons[i]),names[i]);
        }

        listview.setAdapter(adapter);
        webView=(WebView)findViewById(R.id.webView);
        webView.setWebViewClient(new CookWebViewClient());

        WebSettings webSet=webView.getSettings();
        webSet.setBuiltInZoomControls(true);

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                CountryItem item=(CountryItem)parent.getItemAtPosition(position);
                String strText=item.getName();

                if(strText.equals("미국||USA")){
                    Uri uri= Uri.parse("https://cafe.naver.com/nyctourdesign");
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(intent);
                }
                else if(strText.equals("유럽||EUROP")){
                    Uri uri= Uri.parse("https://cafe.naver.com/firenze");
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(intent);
                }
                else if(strText.equals("베트남||VIETNAM")){
                    Uri uri= Uri.parse("https://cafe.naver.com/minecraftpe");
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(intent);
                }
                else if(strText.equals("아시아||ASIA")){
                    Uri uri= Uri.parse("https://cafe.naver.com/yabamcafe2");
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(intent);
                }


                /*
                else if(strText.equals("유럽||EUROP"))
                    webView.loadUrl("https://cafe.naver.com/firenze");
                else if(strText.equals("베트남||VIETNAM"))
                    webView.loadUrl("https://cafe.naver.com/minecraftpe");
                else if(strText.equals("아시아||ASIA"))
                    webView.loadUrl("https://cafe.naver.com/yabamcafe2");
                    */
                return false;
            }
        });


        TabHost.TabSpec tabSalert = tabHost.newTabSpec("여행 경보").setIndicator("여행 경보");
        tabSalert.setContent(R.id.Alert);
        tabHost.addTab(tabSalert);

        tabHost.setCurrentTab(1); //첫화면을 결정해준다.

        //구글맵
        mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this); //mapAsync: 서버를 가져올 때 사용 (UI 가져오면서 뒤에서 MAP을 준비함)

//listview=(ListView)findViewById(R.id.listView);
        search_country=(EditText)findViewById(R.id.search_country);
        search_country_button=(Button)findViewById(R.id.search_country_button);
        single_result=(TextView)findViewById(R.id.alert_result);

        search_country_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String srvUrl = " http://apis.data.go.kr/1262000/TravelWarningService/getTravelWarningList";
                String srvKey = "Nq%2Fww%2FKNMUR06%2Fp2Cd5qNy%2F%2F8ZYqHSMjyNlg%2Bre9%2FbHhdnk92m1Kc2%2B0LIWXxojEmFgJneihFcryruTf43cEfw%3D%3D";
                String strSrch = search_country.getText().toString();
                String strUrl = srvUrl + "?ServiceKey="+srvKey+"&countryName="+strSrch;

                new DownloadWebpageTask().execute(strUrl);
            }
        });



        //국가 선택에 대한 Spinner(드롭다운):자체 xml 파일 생성해줌(country_dropdown)






        Log.d(TAG, "DatabaseTest :: onCreate()");
        this.dbAdapter = new NotesDbAdapter(this);

        copyExcelDataToDatabase();

        Button bt = (Button) findViewById(R.id.addButton);
        bt.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String title = "러키";
                String body = "해피";

                dbAdapter.open();
                dbAdapter.createNote(title, body);
                dbAdapter.close();

                TextView tv = (TextView) findViewById(R.id.message11);
                tv.setText(title + ", " + body + "를 추가하였습니다.");
            }
        });

        Button bt1 = (Button) findViewById(R.id.loadButton);
        bt1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                copyExcelDataToDatabase();
                dbAdapter.open();
                Cursor result = dbAdapter.fetchAllNotes();
                result.moveToFirst();
                String resultStr = "";
                while (!result.isAfterLast()) {
                    String title = result.getString(1);
                    String body = result.getString(2);
                    resultStr += title + ", " + body + "\n";
                    result.moveToNext();
                }

                TextView tv = (TextView) findViewById(R.id.message11);
                tv.setText(resultStr);
                result.close();
                dbAdapter.close();
            }
        });

    } //onCreate

    class CookWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }
    }

    public class ListViewAdapter extends BaseAdapter {
        // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
        private ArrayList<CountryItem> listViewItemList = new ArrayList<CountryItem>() ;

        // ListViewAdapter의 생성자
        public ListViewAdapter() {

        }

        // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
        @Override
        public int getCount() {
            return listViewItemList.size() ;
        }

        // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            // "listview_item" Layout을 inflate하여 convertView 참조 획득.
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_item, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView) ;
            TextView titleTextView = (TextView) convertView.findViewById(R.id.textViewX) ;

            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            CountryItem listViewItem = listViewItemList.get(position);

            // 아이템 내 각 위젯에 데이터 반영
            iconImageView.setImageDrawable(listViewItem.getIcon());
            titleTextView.setText(listViewItem.getName());

            return convertView;
        }

        // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
        @Override
        public long getItemId(int position) {
            return position ;
        }

        // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
        @Override
        public Object getItem(int position) {
            return listViewItemList.get(position) ;
        }

        // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
        public void addItem(Drawable icon, String title) {
            CountryItem item = new CountryItem();

            item.setIcon(icon);
            item.setName(title);

            listViewItemList.add(item);
        }
    }




    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return (String)downloadUrl((String)urls[0]);
            } catch (IOException e) {
                return "==>다운로드 실패";
            }
        }

        protected void onPostExecute(String result) {
            Log.d(tag, result);
            //tv.append(result + "\n");
            //tv.append("========== 파싱 결과 ==========\n");

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();
                boolean a=false, aP=false, aN=false, c=false, cN=false, l=false, lP=false, lN=false;
                single_result.setText("");
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if(eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if(eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        if (tag_name.equals("attention"))
                            a = true;
                        if (tag_name.equals("attentionPartial"))
                            aP= true;
                        if (tag_name.equals("attentionNote"))
                            aN = true;
                        if (tag_name.equals("control"))
                            c = true;
                        if (tag_name.equals("controlNote"))
                            cN = true;
                        if (tag_name.equals("limita"))
                            l = true;
                        if (tag_name.equals("limitaPartial"))
                            lP= true;
                        if (tag_name.equals("limitaNote"))
                            lN = true;
                    } else if(eventType == XmlPullParser.TEXT) {
                        if (a) {
                            //String content = xpp.getText();
                            single_result.setText(xpp.getText());
                            a = false;
                        }if (aP) {
                            //String content = xpp.getText();
                            single_result.append(xpp.getText()+"\n");
                            aP = false;
                        }
                        if (aN) {
                            //String content = xpp.getText();
                            single_result.append(xpp.getText()+"\n");
                            aN = false;
                        }
                        if (c) {
                            //String content = xpp.getText();
                            single_result.append(xpp.getText()+"\n");
                            c = false;
                        }if (cN) {
                            //String content = xpp.getText();
                            single_result.append(xpp.getText()+"\n");
                            cN = false;
                        }if (l) {
                            //String content = xpp.getText();
                            single_result.append(xpp.getText()+"\n");
                            l = false;
                        }
                        if (l) {
                            //String content = xpp.getText();
                            single_result.append(xpp.getText()+"\n");
                            lN = false;
                        }
                        if (lP) {
                            //String content = xpp.getText();
                            single_result.append(xpp.getText()+"\n");
                            lP = false;
                        }
                    } else if(eventType == XmlPullParser.END_TAG) {
                        ;
                    }
                    eventType = xpp.next();
                } // while
            } catch (Exception e) {
                //tv.setText("\n"+e.getMessage());
            }
            if(single_result.getText().length() == 0){//빈값이 넘어올때의 처리
                single_result.setText("발령된 여행경보가 없습니다.");
            }
        }

        private String downloadUrl(String myurl) throws IOException {

            HttpURLConnection conn = null;
            try {
                Log.d(tag, "downloadUrl : "+  myurl);
                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));
                String line = null;
                String page = "";
                while((line = bufreader.readLine()) != null) {
                    page += line;
                }

                return page;
            } catch(Exception e){
                return " ";
            }
            finally {
                conn.disconnect();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) { //MAP이 준비되면 Async가 자동으로 부름
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.651683,127.016171), 15)); //xml에서 초기값을 설정한 것과 동일한 효과
        gMap.getUiSettings().setZoomControlsEnabled(true);

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) { //클릭하는 위도, 경도 값을 전달받는다.
                videoMark = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.map_icon)).position(latLng,100f,100f);
                gMap.addGroundOverlay(videoMark);
                View dialogView=(View)View.inflate(SecondActivity.this, R.layout.map_dialog, null);
                AlertDialog.Builder dlg=new AlertDialog.Builder(SecondActivity.this);
                dlg.setTitle("나라 정보");
                dlg.setIcon(R.drawable.ic_beach_access_black_24dp);
                dlg.setView(dialogView); //이미 앞에 view가 있는데 또 view를 show 하게 된다.
                /*
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tvName.setText(dlgEdtName.getText().toString());
                        tvEmail.setText(dlgEdtEmail.getText().toString());
                    }
                });*/
                dlg.setNegativeButton("돌아가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();//dialog를 좀더 안전하게 종료 dialoginterface는 메소드에서 전닫받은 변수
                    }
                });
                dlg.show(); //두번 눌렀을 때 오류가 난다. removeview를 통해서 다시 view를 만들어야된다.
            }
        });
    }

    public class NotesDbAdapter {

        public static final String KEY_TITLE = "title";
        public static final String KEY_BODY = "body";
        public static final String KEY_ROWID = "_id";
        private static final String TAG = "NotesDbAdapter";

        private DatabaseHelper mDbHelper;
        private SQLiteDatabase mDb;

        /**
         *
         * Database creation sql statement
         */

        private static final String DATABASE_CREATE = "create table notes (_id integer primary key autoincrement, "
                + "title text not null, body text not null);";

        private static final String DATABASE_NAME = "data";
        private static final String DATABASE_TABLE = "notes";
        private static final int DATABASE_VERSION = 2;
        private final Context mCtx;

        private class DatabaseHelper extends SQLiteOpenHelper {

            DatabaseHelper(Context context) {
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(DATABASE_CREATE);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                        + ", which will destroy all old data");
                db.execSQL("DROP TABLE IF EXISTS notes");
                onCreate(db);
            }
        }

        public NotesDbAdapter(Context ctx) {
            this.mCtx = ctx;
        }

        public NotesDbAdapter open() throws SQLException {
            mDbHelper = new DatabaseHelper(mCtx);
            mDb = mDbHelper.getWritableDatabase();
            return this;
        }

        public void close() {
            mDbHelper.close();
        }

        public long createNote(String title, String body) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_TITLE, title);
            initialValues.put(KEY_BODY, body);
            return mDb.insert(DATABASE_TABLE, null, initialValues);
        }

        public boolean deleteNote(long rowId) {
            Log.i("Delete called", "value__" + rowId);
            return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
        }

        public Cursor fetchAllNotes() {
            return mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_TITLE, KEY_BODY }, null, null, null, null, null);
        }

        public Cursor fetchNote(long rowId) throws SQLException {

            Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROWID, KEY_TITLE, KEY_BODY }, KEY_ROWID
                    + "=" + rowId, null, null, null, null, null);
            if (mCursor != null) {
                mCursor.moveToFirst();
            }
            return mCursor;
        }

        public boolean updateNote(long rowId, String title, String body) {
            ContentValues args = new ContentValues();
            args.put(KEY_TITLE, title);
            args.put(KEY_BODY, body);
            return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
        }

    }

    private void copyExcelDataToDatabase() {
        Log.w("ExcelToDatabase", "copyExcelDataToDatabase()");

        Workbook workbook = null;
        Sheet sheet = null;

        try {
            InputStream is = getBaseContext().getResources().getAssets().open("exchangeData.xlsx");
            workbook = Workbook.getWorkbook(is);

            if (workbook != null) {
                sheet = workbook.getSheet(0);

                if (sheet != null) {

                    int nMaxColumn = 3;
                    int nRowStartIndex = 0;
                    int nRowEndIndex = sheet.getColumn(nMaxColumn - 1).length - 1;
                    int nColumnStartIndex = 0;
                    int nColumnEndIndex = sheet.getRow(3).length - 1;

                    dbAdapter.open();
                    for (int nRow = nRowStartIndex; nRow <= nRowEndIndex; nRow++) {
                        String title = sheet.getCell(nColumnStartIndex+1, nRow).getContents();
                        String body = sheet.getCell(nColumnStartIndex + 2, nRow).getContents();
                        dbAdapter.createNote(title, body);
                    }
                    dbAdapter.close();
                } else {
                    System.out.println("Sheet is null!!");
                }
            } else {
                System.out.println("WorkBook is null!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
    }


}
