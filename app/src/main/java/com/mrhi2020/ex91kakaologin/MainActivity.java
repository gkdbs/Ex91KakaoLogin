package com.mrhi2020.ex91kakaologin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.sdk.auth.LoginClient;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {

    TextView tvNickname;
    TextView tvEmail;
    CircleImageView ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //키 해시값 얻어와서 Logcat 창에 출력하기 - 카카오 개발자 사이트에서 키해시값 등록해야 해서
        String keyHash= Utility.INSTANCE.getKeyHash(this);
        Log.i("KeyHash", keyHash);

        tvNickname= findViewById(R.id.tv_nickname);
        tvEmail= findViewById(R.id.tv_email);
        ivProfile= findViewById(R.id.iv);



    }

    public void clickLogin(View view) {
        //카카오 계정으로 로그인하기
        LoginClient.getInstance().loginWithKakaoAccount(this, new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                if(oAuthToken !=null ){ //로그인 정보객체가 있다면
                    Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                    //로그인한 계정 정보 얻어오기
                    UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                        @Override
                        public Unit invoke(User user, Throwable throwable) {

                            if(user != null){
                                long id= user.getId(); //카카오 회원번호

                                //필수동의 항목의 회원프로필 정보 [ 닉네임/프로필이미지 Url]
                                G.nickname= user.getKakaoAccount().getProfile().getNickname();
                                G.profileImage= user.getKakaoAccount().getProfile().getThumbnailImageUrl();

                                //선택동의 항목으로 지정한 Email
                                String email= user.getKakaoAccount().getEmail();

                                tvNickname.setText(G.nickname);
                                tvEmail.setText(email);
                                Glide.with(MainActivity.this).load(G.profileImage).into(ivProfile);

                                //다음에 접속할때 로그인 다시 하지 않으려면 SharedPreference에 로그인정보를 저장해두고 불러오도록 코드 추가...

                            }else{
                                Toast.makeText(MainActivity.this, "사용자 정보 요청 실패 : " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            return null;
                        }
                    });

                }else{
                    Toast.makeText(MainActivity.this, "로그인 실패 : " + throwable.getMessage(), Toast.LENGTH_SHORT).show();

                }
                return null;
            }
        });
    }

    public void clickLogout(View view) {
        //로그아웃 요청
        UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
            @Override
            public Unit invoke(Throwable throwable) {
                if(throwable!=null)
                    Toast.makeText(MainActivity.this, "로그아웃 실패", Toast.LENGTH_SHORT).show();
                else{
                    Toast.makeText(MainActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();

                    //로그인 회원정보 화면들 모두 초기화
                    tvNickname.setText("닉네임");
                    tvEmail.setText("이메일");
                    Glide.with(MainActivity.this).load(R.mipmap.ic_launcher).into(ivProfile);
                }
                return null;
            }
        });
    }

    public void clickNext(View view) {
        startActivity(new Intent(this, SecondActivity.class));
        finish();
    }
}