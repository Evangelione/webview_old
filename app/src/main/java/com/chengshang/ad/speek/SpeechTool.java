package com.chengshang.ad.speek;

import android.content.Context;
import android.util.Log;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.chengshang.ad.speek.control.InitConfig;
import com.chengshang.ad.speek.control.MySyntherizer;
import com.chengshang.ad.speek.listener.MessageListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * APP名： AdvertisingProject
 * 包名：com.chengshang.advertisingproject.asrwakeup
 * 作者：jiang-pc
 * 版本：
 * 创建日期：2019/7/22
 * 描述：语音合成
 * 修订历史：
 */
public class SpeechTool {
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线；TtsMode.OFFLINE 纯离线
    private TtsMode ttsMode = TtsMode.ONLINE;
    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat为离线男声模型；
    // assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat为离线女声模型
    //    protected String offlineVoice = OfflineResource.VOICE_DUYY;

    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================
    private static final String TAG = "====SpeechTool";
    private String appId = "17623229";
    private String appKey = "r7lZqaAjaDi8ZkeMiZkXfk1R";
    private String secretKey = "LvW01jxYmhZy6kKWw6TGDpKgXm07FH6G";
    private Context mContext;
    private static SpeechTool mInstance;
    public MySyntherizer synthesizer;
    private InitConfig initConfig;

    private int mWake_flag; //0 播报完成后不需要语音识别 1 播报完成后需要语音识别  2小由消失
    private String mString;//语音播报的文字

    // ================选择TtsMode.ONLINE  不需要设置以下参数; 选择TtsMode.MIX 需要设置下面2个离线资源文件的路径
    private static final String TEMP_DIR = "/sdcard/baiduTTS"; // 重要！请手动将assets目录下的3个dat 文件复制到该目录

    // 请确保该PATH下有这个文件
    private static final String TEXT_FILENAME = TEMP_DIR + "/"
            + "bd_etts_text.dat";

    // 请确保该PATH下有这个文件 ，m15是离线男声
    private static final String MODEL_FILENAME =
            TEMP_DIR + "/" + "bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat";

    public SpeechTool(Context context) {
        mContext = context;
        Log.i(TAG, "合成控制器实例完成");
        SpeechSynthesizerListener listener = new MessageListener() {
            /**
             * 播放正常结束，每句播放正常结束都会回调，如果过程中出错，则回调onError,不再回调此接口
             *
             * @param utteranceId
             */
            @Override
            public void onSpeechFinish(String utteranceId) {
                super.onSpeechFinish(utteranceId);
                Log.i(TAG, "这里播放完成了");
                stop();
            }

            @Override
            public void onSpeechStart(String utteranceId) {
                super.onSpeechStart(utteranceId);
                Log.i(TAG, "这里播放开始了");
            }

            @Override
            public void onSynthesizeStart(String utteranceId) {
                super.onSynthesizeStart(utteranceId);
            }

            /**
             * 语音流 16K采样率 16bits编码 单声道 。
             *
             * @param utteranceId
             * @param bytes       二进制语音 ，注意可能有空data的情况，可以忽略
             * @param progress    如合成“百度语音问题”这6个字， progress肯定是从0开始，到6结束。 但progress无法和合成到第几个字对应。
             */
//            @Override
            public void onSynthesizeDataArrived(String utteranceId, byte[] bytes, int progress) {
                super.onSynthesizeDataArrived(utteranceId, bytes, progress);
                Log.i(TAG, "合成进度回调, progress：" + progress + ";序列号:" + utteranceId);
            }


            //合成结束回调
            @Override
            public void onSynthesizeFinish(String utteranceId) {
                super.onSynthesizeFinish(utteranceId);
            }

            @Override
            public void onError(String utteranceId, SpeechError speechError) {
                super.onError(utteranceId, speechError);
            }
        };
        // 1. 获取实例
        Map<String, String> params = getParams();
        initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);
        Log.i(TAG, "合成控制器参数设置完成");
    }

    public synchronized static SpeechTool getInstance(Context context, int wake_flag) {
        if (mInstance == null) {
            mInstance = new SpeechTool(context);
        }
        mInstance.mWake_flag = wake_flag;
        return mInstance;
    }

    public void initSpeech(String str) {
        if (synthesizer != null) {
            synthesizer.release();
            synthesizer = null;
        }
        mString = str;
        try {
            synthesizer = new MySyntherizer(mContext, initConfig, null);
            speak(str);
        } catch (Exception e) {
            Log.i("=====", "实例化失败");
        }
    }


    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return
     */
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
//        params.put(SpeechSynthesizer.PARAM_SPEAKER, "103");
        // 设置合成的音量，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "15");
        // 设置合成的语速，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "6");

        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI);
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
        //        OfflineResource offlineResource = createOfflineResource(offlineVoice);
        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
        params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
        params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);
//        params.put(SpeechSynthesizer.PARAM_AUTH_SN,"81a5dbfa-6a0eb201-00bc-00b9-20ae8");
        return params;
    }

//    protected OfflineResource createOfflineResource(String voiceType) {
//        OfflineResource offlineResource = null;
//        try {
//            offlineResource = new OfflineResource(mContext, voiceType);
//        } catch (IOException e) {
//            // IO 错误自行处理
//            e.printStackTrace();
//            print("【error】:copy files from assets failed." + e.getMessage());
//        }
//        return offlineResource;
//    }

    private void print(String message) {
        Log.i(TAG, message);
    }

    /**
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     */
    private void speak(String text) {
        synthesizer.speak(text);
    }


    private void stop() {
        print("停止合成引擎 按钮已经点击");
        synthesizer.stop();
    }


    /**
     * 检查 TEXT_FILENAME, MODEL_FILENAME 这2个文件是否存在，不存在请自行从assets目录里手动复制
     *
     * @return
     */
    private boolean checkOfflineResources() {
        String[] filenames = {TEXT_FILENAME, MODEL_FILENAME};
        for (String path : filenames) {
            File f = new File(path);
            if (!f.canRead()) {
                print("[ERROR] 文件不存在或者不可读取，请从assets目录复制同名文件到：" + path);
                print("[ERROR] 初始化失败！！！");
                return false;
            }
        }
        return true;
    }
}
