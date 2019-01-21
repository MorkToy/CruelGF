package cruelgf.funself.com.cruelgf.listner;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import cruelgf.funself.com.cruelgf.MainActivity;

/**
 * Created by wudi .
 * on 2019/1/21.
 */

public class CruelSpeechListener implements SynthesizerListener, InitListener {

    private static final String mVoiceName = "nannan";

    private final String TAG = "CruelSpeechListener";

    private Activity mActivity;

    private SpeechSynthesizer mTts;

    public CruelSpeechListener(Activity mActivity, SpeechSynthesizer mTts) {
        this.mActivity = mActivity;
        this.mTts = mTts;
    }

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

    private void showTip(String content){
        Toast.makeText(mActivity, content, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInit(int code) {
        Log.d(TAG, "InitListener init() code = " + code);
        if (code != ErrorCode.SUCCESS) {
            Log.e(TAG,"初始化失败,错误码："+code);
        } else {
            showTip("初始化成功");
            mTts.setParameter( SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD );
            mTts.setParameter( SpeechConstant.ENGINE_MODE, SpeechConstant.MODE_AUTO );
            mTts.setParameter( SpeechConstant.VOICE_NAME, mVoiceName );

            String strTextToSpeech = "欢迎主人";
            //开始说话
            mTts.startSpeaking( strTextToSpeech, this);
        }
    }
}
