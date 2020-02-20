package info.pratham.speechtotext;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

public class MyTTS {
    static TextToSpeech textToSpeech;
    static Context mContext;
    HashMap<String, String> map;

    public MyTTS(Context context) {
        super();
        mContext = context;
        try {
            textToSpeech = new TextToSpeech(mContext, new ttsInitListener());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playTTS(final String toSpeak, String Lang, float pitchTts) {
//        if (Lang.equals("hi-IN"))
//            textToSpeech.setLanguage(new Locale("hi", "IN"));
//        else if (Lang.equals("gu-IN"))
//            textToSpeech.setLanguage(new Locale("gu", "IN"));
//        else
//            textToSpeech.setLanguage(new Locale("en", "IN"));
        String[] splitArr = Lang.split("-");
        Log.d("TTS", "playTTS: "+splitArr[0] + "::::::::" +splitArr[1]);
        textToSpeech.setLanguage(new Locale(splitArr[0],splitArr[1]));
        textToSpeech.setSpeechRate(0.7f);
        textToSpeech.setPitch(pitchTts);
        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, map);
    }

}