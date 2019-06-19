package com.example.myexchange;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myexchange.SecondActivity;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "";
    SessionCallback callback;
    Button btLogOut;
    Button login;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("환전+여행정보");

        /**카카오톡 로그아웃 요청**/
        //한번 로그인이 성공하면 세션 정보가 남아있어서 로그인창이 뜨지 않고 바로 onSuccess()메서드를 호출하므로
        // 매번 로그아웃을 요청함
        /*
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                //로그아웃 성공 후 하고싶은 내용 코딩 ~
            }
        });*/

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);

    } //onCreate

    class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {

            UserManagement.requestMe(new MeResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        //에러로 인한 로그인 실패
                        // finish();
                    } else {
                        //redirectMainActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                }

                @Override
                public void onNotSignedUp() {
                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    //로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url등을 리턴합니다.
                    //사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.

                    Log.e("UserProfile", userProfile.toString());
                    Log.e("UserProfile", userProfile.getId() + "");
                    Log.e("UserProfile", userProfile.getProfileImagePath() + "");
                    Log.e("UserProfile", String.valueOf(userProfile.getId()) + "");
                    Log.e("UserProfile", userProfile.getNickname() + "");

                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        }

        // 세션 실패시
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
        }
    }

}
