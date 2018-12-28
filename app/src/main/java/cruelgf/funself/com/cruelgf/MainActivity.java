package cruelgf.funself.com.cruelgf;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    //语言类型
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

    private SpeechSynthesizer mTts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5c25d3f7");
        requestPermission();

    }

    private void requestPermission() {
        MPermissions.requestPermissions(MainActivity.this, REQUECT_CODE_SDCARD, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        MPermissions.requestPermissions(MainActivity.this, REQUECT_CODE_AUDIO, Manifest.permission.RECORD_AUDIO);
        MPermissions.requestPermissions(MainActivity.this, REQUECT_CODE_PHONESTATE, Manifest.permission.READ_PHONE_STATE);
        MPermissions.requestPermissions(MainActivity.this, REQUECT_CODE_CONTACTS, Manifest.permission.READ_CONTACTS);
        MPermissions.requestPermissions(MainActivity.this, REQUECT_CODE_READSDCARD, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    @PermissionGrant(REQUECT_CODE_AUDIO)
    public void requestAudioSuccess() {
        //初始化
        wakeup();
    }

    @PermissionDenied(REQUECT_CODE_AUDIO)
    public void requestAudioFailed() {
        showTip("授权失败");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void wakeup() {
        //科大讯飞语言初始化//文档中心：https://doc.xfyun.cn/msc_android/
        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
    }

    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Log.e(TAG,"初始化失败,错误码："+code);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
                showTip("初始化成功");
                mTts.setParameter( SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD );
                mTts.setParameter( SpeechConstant.ENGINE_MODE, SpeechConstant.MODE_AUTO );
                mTts.setParameter( SpeechConstant.VOICE_NAME, mVoiceName );

                final String strTextToSpeech = "主淫,你来了";
                mTts.startSpeaking( strTextToSpeech, mTtsListener );
            }
        }
    };

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            showTip("开始播放");
        }

        @Override
        public void onSpeakPaused() {
            showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            showTip("继续播放");
        }

        @Override
        public void onCompleted(SpeechError speechError) {
            showTip("播放完毕");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {

        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度

        }



        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}

            if (SpeechEvent.EVENT_TTS_BUFFER == eventType) {
                byte[] buf = obj.getByteArray(SpeechEvent.KEY_EVENT_TTS_BUFFER);
                Log.e("MscSpeechLog", "buf is =" + buf);
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTts.destroy();
    }

    public void showTip(String content){
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }

}