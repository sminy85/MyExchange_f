package com.example.myexchange;


import android.app.Activity;
import android.content.Context;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;

public class KakaoSDKAdapter extends KakaoAdapter {

    @Override
    public ISessionConfig getSessionConfig() {
        return new ISessionConfig() {
            @Override
            public AuthType[] getAuthTypes() {
                return new AuthType[]{AuthType.KAKAO_ACCOUNT};
            }
            // ???? ???? ??? ????. ???? ?? ? ??? ?? ??? ????.

            //1.KAKAO_TALK :  kakaotalk?? login? ?? ?? ? ??.
            //2.KAKAO_STORY : kakaostory?? login? ?? ?? ? ??.
            //3.KAKAO_ACCOUNT : ?? Dialog? ?? ??? ????? ???? ?? ?? ??.
            //4.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN : ??????? ???? ???? ???? ??? ? ?? ????? ??
            //??? ?? ??? ?? ??? ??. KAKAO_TALK? ?? ????.
            //5.KAKAO_LOGIN_ALL : ?? ?????? ???? ?? ? ??.

            @Override
            public boolean isUsingWebviewTimer() {
                return false;
            }
            // SDK ???? ???? WebView?? pause? resume?? Timer? ???? CPU??? ????.
            // true ? ????? webview???? ???? ??? ?? webview? onPause? onResume ??
            // Timer? ??? ??? ??. ???? ?? ? false? ????.

            @Override
            public ApprovalType getApprovalType() {
                return ApprovalType.INDIVIDUAL;
            }

            @Override
            public boolean isSaveFormData() {
                return true;
            }
        };
    }

    @Override
    public IApplicationConfig getApplicationConfig() {
        return new IApplicationConfig() {
            @Override
            public Activity getTopActivity() {
                return GlobalApplication.getCurrentActivity();
            }

            @Override
            public Context getApplicationContext() {
                return GlobalApplication.getGlobalApplicationContext();
            }
        };
    }

}

