package info.pratham.speechtotext;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static info.pratham.speechtotext.FormActivity.getCurrentDateTime;

public class MainActivity extends BaseActivity implements
        RecognitionListener {

    private ImageButton btnSpeak, btnHearAnswer, btnNextSentence, btnHearQuestion;
    private FrameLayout ans_framelayout;
    private TextView textToReadView, printVoiceText, tv_mic;
    private SpeechRecognizer speech = null;
    private Spinner language_sp, type_sp;
    public MyTTS ttspeech;
    SttData sttData;
    Intent intent;
    int randomNum1, randomNum2;
    MyDBHelper myDBHelper;
    String tex = "";
    JSONArray readingData2, actualReadingData;
    static String languageSpinner, typeSpinner, engLang = "en-IN", hinLang = "gu-IN";
    public String recName, recName2, LOG_TAG = "VoiceRecognitionActivity", uName, uID, numInText;
    static String RecordedSpeech = "", selectedLanguage = "en-IN", selectedType = "words", mySentence = "";
    boolean voiceStart = false, stopped = false, numFlag = false, breakFlg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uName = getIntent().getStringExtra("uName");
        uID = getIntent().getStringExtra("uId");
        readingData2 = FormActivity.readingData;
        ttspeech = new MyTTS(this);
        sttData = new SttData();
        myDBHelper = new MyDBHelper(this);

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);

        ans_framelayout = (FrameLayout) findViewById(R.id.ans_framelayout);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnHearAnswer = (ImageButton) findViewById(R.id.btnHearAnswer);
        btnHearQuestion = (ImageButton) findViewById(R.id.btnHearQuestion);
        btnNextSentence = (ImageButton) findViewById(R.id.btnNextSentence);
        language_sp = (Spinner) findViewById(R.id.language_spinner);
        type_sp = (Spinner) findViewById(R.id.type_spinner);
        printVoiceText = (TextView) findViewById(R.id.spokenText);
        textToReadView = (TextView) findViewById(R.id.textToRead);
        tv_mic = (TextView) findViewById(R.id.tv_mic);
        textToReadView.setMovementMethod(new ScrollingMovementMethod());
        printVoiceText.setMovementMethod(new ScrollingMovementMethod());


        type_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String datasTyp = parent.getItemAtPosition(position).toString();
                numFlag = false;
                if (datasTyp.equalsIgnoreCase("words")) {
                    typeSpinner = "words";
                    textToReadView.setTextSize(55f);
                    printVoiceText.setTextSize(45f);
                } else if (datasTyp.equalsIgnoreCase("sentences")) {
                    typeSpinner = "sentences";
                    textToReadView.setTextSize(35f);
                    printVoiceText.setTextSize(30f);
                } else {
                    typeSpinner = "numberList";
                    numFlag = true;
                    textToReadView.setTextSize(75f);
                    printVoiceText.setTextSize(65f);
                }
                getReadingData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Spinner Drop down elements
        List<String> languages = new ArrayList<String>();
        languages.add("Hindi");
        languages.add("English");
        languages.add("Marathi");
        languages.add("Gujarati");
        languages.add("Bengali");
        languages.add("Kannada");
        languages.add("Tamil");
        languages.add("Telugu");
        languages.add("English - Eritrea");
        languages.add("English - Ghana");
        languages.add("English - Kenya");
        languages.add("English - Madagascar");
        languages.add("English - Nigeria");
        languages.add("English - Tanzania");
        languages.add("English - Uganda");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language_sp.setAdapter(dataAdapter);


        language_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                languageSpinner = parent.getItemAtPosition(position).toString();
                if (languageSpinner.equalsIgnoreCase("Hindi")) {
                    selectedLanguage = "hi-IN";
                    btnHearAnswer.setVisibility(View.VISIBLE);
                    btnHearQuestion.setVisibility(View.VISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("English")) {
                    selectedLanguage = "en-IN";
                    btnHearAnswer.setVisibility(View.VISIBLE);
                    btnHearQuestion.setVisibility(View.VISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("English - Eritrea")) {
                    selectedLanguage = "en-ER";
                    btnHearAnswer.setVisibility(View.VISIBLE);
                    btnHearQuestion.setVisibility(View.VISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("English - Ghana")) {
                    selectedLanguage = "en-GH";
                    btnHearAnswer.setVisibility(View.VISIBLE);
                    btnHearQuestion.setVisibility(View.VISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("English - Kenya")) {
                    selectedLanguage = "en-KE";
                    btnHearAnswer.setVisibility(View.VISIBLE);
                    btnHearQuestion.setVisibility(View.VISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("English - Madagascar")) {
                    selectedLanguage = "en-MG";
                    btnHearAnswer.setVisibility(View.VISIBLE);
                    btnHearQuestion.setVisibility(View.VISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("English - Nigeria")) {
                    selectedLanguage = "en-NG";
                    btnHearAnswer.setVisibility(View.VISIBLE);
                    btnHearQuestion.setVisibility(View.VISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("English - Tanzania")) {
                    selectedLanguage = "en-TZ";
                    btnHearAnswer.setVisibility(View.VISIBLE);
                    btnHearQuestion.setVisibility(View.VISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("English - Uganda")) {
                    selectedLanguage = "en-UG";
                    btnHearAnswer.setVisibility(View.VISIBLE);
                    btnHearQuestion.setVisibility(View.VISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("Marathi")) {
                    selectedLanguage = "mr-IN";
                    btnHearAnswer.setVisibility(View.INVISIBLE);
                    btnHearQuestion.setVisibility(View.INVISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("Gujarati")) {
                    selectedLanguage = "gu-IN";
                    btnHearAnswer.setVisibility(View.INVISIBLE);
                    btnHearQuestion.setVisibility(View.INVISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("Bengali")) {
                    selectedLanguage = "bn-IN";
                    btnHearAnswer.setVisibility(View.INVISIBLE);
                    btnHearQuestion.setVisibility(View.INVISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("Kannada")) {
                    selectedLanguage = "kn-IN";
                    btnHearAnswer.setVisibility(View.INVISIBLE);
                    btnHearQuestion.setVisibility(View.INVISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("Tamil")) {
                    selectedLanguage = "ta-IN";
                    btnHearAnswer.setVisibility(View.INVISIBLE);
                    btnHearQuestion.setVisibility(View.INVISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("Telugu")) {
                    selectedLanguage = "te-IN";
                    btnHearAnswer.setVisibility(View.INVISIBLE);
                    btnHearQuestion.setVisibility(View.INVISIBLE);
                }

                intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

                getReadingData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }

        });

        List<String> types = new ArrayList<String>();
        types.add("Words");
        types.add("Sentences");
        types.add("Numbers");

        ArrayAdapter<String> dataTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types);
        dataTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_sp.setAdapter(dataTypeAdapter);

        btnNextSentence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNextSentence();
            }
        });

        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!voiceStart) {
                    tv_mic.setText("Stop");
                    btnSpeak.setImageResource(R.drawable.iv_stop);
                    voiceStart = true;
                    RecordedSpeech = "";
                    recName = "NA";
                    printVoiceText.setText("");
                    ans_framelayout.setBackgroundResource(R.drawable.trans_black_stroke);
                    startSpeechInput();
                } else {
                    stopSpeechInput();
                    voiceStart = false;
                    tv_mic.setText("Speak");
                    btnSpeak.setImageResource(R.drawable.ic_mic);
                    try {
                        stopped = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    stopSpeechInput();
                }
            }
        });

        btnHearQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String questionText = String.valueOf(textToReadView.getText());
                PlaySpokenText(questionText);
            }
        });

        btnHearAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answerText = String.valueOf(printVoiceText.getText());
                PlaySpokenText(answerText);
            }
        });
    }

    private void stopSpeechInput() {
        speech.stopListening();
    }

    private void startSpeechInput() {
        speech.startListening(intent);
    }


    public void getReadingData() {
        try {
            for (int i = 0; i < readingData2.length(); i++) {
                String lang = readingData2.getJSONObject(i).getString("language");
                if (lang.equalsIgnoreCase(languageSpinner)) {
                    actualReadingData = readingData2.getJSONObject(i).getJSONArray("" + typeSpinner);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        getNextSentence();
    }

    private void PlaySpokenText(String speechText) {
        Log.d("lang", "PlaySpokenText: " + selectedLanguage + "          speechText: " + speechText);
        ttspeech.playTTS(speechText, selectedLanguage, 1);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void getNextSentence() {

        BackupDatabase.backup(MainActivity.this);
        try {
            if (numFlag) {
                randomNum1 = ThreadLocalRandom.current().nextInt(0, actualReadingData.length());
                randomNum2 = ThreadLocalRandom.current().nextInt(0, actualReadingData.length());
                if (randomNum1 != 0) {
                    numInText = "" + randomNum1 + randomNum2;
                    mySentence = actualReadingData.getJSONObject(randomNum1).getString("data") + actualReadingData.getJSONObject(randomNum2).getString("data");
                } else {
                    numInText = "" + randomNum2;
                    mySentence = actualReadingData.getJSONObject(randomNum2).getString("data");
                }
            } else {
                int randomNum = ThreadLocalRandom.current().nextInt(0, actualReadingData.length());
                mySentence = actualReadingData.getJSONObject(randomNum).getString("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        textToReadView.setText(mySentence);
        printVoiceText.setText("");
        ans_framelayout.setBackgroundResource(R.drawable.trans_black_stroke);
    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
        voiceStart = false;
        tv_mic.setText("Speak");
        btnSpeak.setImageResource(R.drawable.ic_mic);
        try {
            if (!stopped) {
                stopped = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(int error) {
        try {
            if (!stopped) {
                stopped = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        voiceStart = false;
        tv_mic.setText("Speak");
        btnSpeak.setImageResource(R.drawable.ic_mic);
    }

    @Override
    public void onResults(Bundle results) {
        breakFlg = false;
        System.out.println(LOG_TAG + " onResults");
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        tex = "";
        String tempResultArray = "";
        for (int i = 0; i < matches.size(); i++) {
            tempResultArray += " - " + matches.get(i);
            Log.d("res:::", i + ") Results: " + matches.get(i));
        }

        Log.d("res:::", "\n\nRandom Nos: " + randomNum1 + ", " + randomNum2);

        try {
            tex = matches.get(0) + "";
            Log.d("res:::", "\n\n tex : " + tex);
            ans_framelayout.setBackgroundResource(R.drawable.wrong_ans_bg);
            String randomNoStr1 = "" + randomNum1, randomNoStr2 = "" + randomNum2;

            for (int i = 0; i < matches.size(); i++) {
                if (numFlag) {
                    String resNum = matches.get(i);
                    Log.d("res:::", "\n\nresNum: " + resNum);
                    if (numInText.equalsIgnoreCase("" + matches.get(i))) {
                        tex = actualReadingData.getJSONObject(randomNum1).getString("data")
                                + actualReadingData.getJSONObject(randomNum2).getString("data");
                        breakFlg = true;
                        ans_framelayout.setBackgroundResource(R.drawable.correct_ans_bg);
                        break;
                    }
                } else {
                    if (mySentence.equalsIgnoreCase(matches.get(i))) {
                        tex = matches.get(i) + " ";
                        ans_framelayout.setBackgroundResource(R.drawable.correct_ans_bg);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!breakFlg) {
            breakFlg = false;
            for (int j = 0; j < matches.size(); j++) {
                try {
                    int tempNo = Integer.parseInt(matches.get(j));
                    String tempNostr = "" + tempNo;
                    Log.d("ressss", "Not True Num: " + tempNo);
                    if (tempNo < 100) {
                        try {
                            if (tempNo < 10) {
                                tex = actualReadingData.getJSONObject(Integer.parseInt(String.valueOf(matches.get(j).charAt(0)))).getString("data");
                            } else {
                                tex = actualReadingData.getJSONObject(Integer.parseInt(String.valueOf(matches.get(j).charAt(0)))).getString("data") +
                                        actualReadingData.getJSONObject(Integer.parseInt(String.valueOf(matches.get(j).charAt(1)))).getString("data");
                            }
                            break;
                        } catch (Exception e) {
                            Log.d("Exception", "inCatch: This is in the first catch");
                            //tex = matches.get(j) + " ";
                        }
                    } else {
                        int numlen = tempNostr.length();
                        tex = "";
                        while (tempNo > 0) {
                            int digit = tempNo % 10;
                            tempNo = tempNo / 10;
                            tex = actualReadingData.getJSONObject(digit).getString("data") + "" + tex;
                        }
                    }
                } catch (Exception e) {
                    Log.d("Exception", "inCatch: This is in the second catch");
                }
            }
        }

        RecordedSpeech += tex;
        voiceStart = false;

        tv_mic.setText("Speak");
        btnSpeak.setImageResource(R.drawable.ic_mic);
        sttData.ReordId = recName;
        sttData.UserID = uID;
        sttData.OriginalText = String.valueOf(textToReadView.getText());
        sttData.VoiceText = tex;
        sttData.DateTime = "" + getCurrentDateTime();
        myDBHelper.AddSttText(sttData);

        BackupDatabase.backup(MainActivity.this);

        try {
            if (!stopped)
                stopped = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        printVoiceText.setText(tex);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        final ArrayList<String> partialData = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        if (partialData != null) {
            for (String partial : partialData) {
                if (partial.replaceAll("\\s", "").isEmpty()) {
                    Log.d("SPEECH_TEST", "onPartialResults: I'm an empty String?");
                }
            }
        }
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }

}

/*
                    if (randomNum1 == 0) {
                        if ( numInText.equalsIgnoreCase("0"+matches.get(i)) ) {
                            tex = actualReadingData.getJSONObject(randomNum1).getString("data")
                                    + actualReadingData.getJSONObject(randomNum2).getString("data");
                            break;
                        }
                    } else {}
*/
