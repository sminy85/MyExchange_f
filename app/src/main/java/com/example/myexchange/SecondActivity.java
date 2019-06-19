package com.example.myexchange;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

public class SecondActivity extends TabActivity implements OnMapReadyCallback { //TabActivity를 상속받는다.

    GoogleMap gMap;
    MapFragment mapFrag;
    GroundOverlayOptions videoMark;
    Spinner country1, country2;
    ListView listview;
    ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);
        country1=(Spinner)findViewById(R.id.country1);
        country2=(Spinner)findViewById(R.id.country2);

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


        TabHost.TabSpec tabSmypage = tabHost.newTabSpec("마이페이지").setIndicator("마이페이지");
        tabSmypage.setContent(R.id.myPage);
        tabHost.addTab(tabSmypage);

        tabHost.setCurrentTab(1); //첫화면을 결정해준다.

        //구글맵
        mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this); //mapAsync: 서버를 가져올 때 사용 (UI 가져오면서 뒤에서 MAP을 준비함)

    } //onCreate


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

}
