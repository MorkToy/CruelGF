package cruelgf.funself.com.cruelgf;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.util.ArrayList;
import java.util.List;

import cruelgf.funself.com.cruelfloat_sdk.FloatWindow;
import cruelgf.funself.com.cruelfloat_sdk.Screen;
import cruelgf.funself.com.cruelfloat_sdk.ViewStateListener;
import cruelgf.funself.com.cruelgf.listner.CruelSpeechListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    //语言类型，这里不要随意更改名字
    private static final String mVoiceName = "nannan";

    //sd卡写入权限
    private static final int REQUECT_CODE_SDCARD = 1001;
    //音频
    private static final int REQUECT_CODE_AUDIO = 1002;
    //读取手机状态
    private static final int REQUECT_CODE_PHONESTATE = 1003;
    //读取联系人
    private static final int REQUECT_CODE_CONTACTS = 1004;
    //读取SD卡
    private static final int REQUECT_CODE_READSDCARD = 1005;
    //悬浮窗
    private static final int REQUECT_CODE_WINDOW = 1006;

    private List<String> speechList = new ArrayList<>();

    private SpeechSynthesizer mTts;

    private ImageView gf_iv_main;

    private ImageView gf_iv_main_gif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5c25d3f7");
        requestPermission();
        initData();
        initView();
        initListener();
    }

    /**
     * 申请权限.
     */
    private void requestPermission() {
        MPermissions.requestPermissions(MainActivity.this, REQUECT_CODE_SDCARD, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE);

        MPermissions.requestPermissions(MainActivity.this, REQUECT_CODE_AUDIO, Manifest.permission.RECORD_AUDIO);
    }

    /**
     * 初始化UI.
     */
    public void initView() {
        //主封面图
        gf_iv_main = findViewById(R.id.gf_iv_main);
        //设置GIF动图
        gf_iv_main_gif = new ImageView(getApplicationContext());
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(this).load(R.drawable.main_gf).apply(options).into(gf_iv_main_gif);
        //设置开启悬浮窗
        FloatWindow
                .with(getApplicationContext())
                .setView(gf_iv_main_gif)
                .setWidth(300)                               //设置控件宽高
                .setHeight(Screen.width,0.1f)
                .setX(300)                                   //设置控件初始位置
                .setY(Screen.height,0.1f)
                .setDesktopShow(true)                        //桌面显示
                .setViewStateListener(new ViewStateListener() {
                    @Override
                    public void onPositionUpdate(int i, int i1) {

                    }

                    @Override
                    public void onShow() {

                    }

                    @Override
                    public void onHide() {

                    }

                    @Override
                    public void onDismiss() {

                    }

                    @Override
                    public void onMoveAnimStart() {

                    }

                    @Override
                    public void onMoveAnimEnd() {

                    }

                    @Override
                    public void onBackToDesktop() {

                    }
                })
                .build();

    }

    /**
     * 事件监听.
     */
    public void initListener() {
        gf_iv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rand = (int)(Math.random() * speechList.size());
                mTts.startSpeaking( speechList.get(rand), new CruelSpeechListener(MainActivity.this, mTts) );
            }
        });
    }

    /**
     * 初始化数据.
     */
    public void initData() {
        speechList.add("主人,你好坏,没想到你是这样的人");
        speechList.add("主人,再说一遍,别碰我");
        speechList.add("主人,你这个变态,别碰我");
        speechList.add("在碰我咬你");
    }

    /**
     * 媒体授权成功.
     */
    @PermissionGrant(REQUECT_CODE_AUDIO)
    public void requestAudioSuccess() {
        MPermissions.requestPermissions(MainActivity.this, REQUECT_CODE_WINDOW, Manifest.permission.SYSTEM_ALERT_WINDOW);
        //初始化
        wakeup();
    }

    @PermissionDenied(REQUECT_CODE_AUDIO)
    public void requestAudioFailed() {
        showTip("授权失败");
    }

    /**
     * 悬浮窗授权成功.
     */
    @PermissionGrant(REQUECT_CODE_WINDOW)
    public void requestWindwoSuccess() {

    }

    @PermissionDenied(REQUECT_CODE_WINDOW)
    public void requestWindowFailed() {
        showTip("悬浮窗授权失败");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 初始化合成语言.
     */
    public void wakeup() {
        //科大讯飞语言初始化//文档中心：https://doc.xfyun.cn/msc_android/
        mTts = SpeechSynthesizer.createSynthesizer(this, new CruelSpeechListener(this, mTts));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTts.destroy();
    }

    public void showTip(String content){
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }

}
