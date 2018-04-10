package info.pratham.speechtotext;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.MediaRecorder;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity implements
        RecognitionListener {

    private ImageButton btnSpeak, btnHear, btnNextSentence;
    private TextView textToReadView, printVoiceText, tv_mic;
    private SpeechRecognizer speech = null;
    private Spinner language_sp, type_sp;
    public MyTTS ttspeech;
    LinearLayout ll_Hear;
    myUser myuser;
    Intent intent;
    MyDBHelper myDBHelper;
    String tex = "";
    JSONArray readingData2, actualReadingData;
    static String languageSpinner, typeSpinner, engLang = "en-IN", hinLang = "gu-IN";
    private String recName, recName2, LOG_TAG = "VoiceRecognitionActivity", uName;
    static String RecordedSpeech = "", selectedLanguage = "en-IN", selectedType = "words", mySentence = "";
    boolean voiceStart = false, stopped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        uName = getIntent().getStringExtra("uName");
        readingData2 = FormActivity.readingData;
        ttspeech = new MyTTS(this);
        myuser = new myUser();
        myDBHelper = new MyDBHelper(this);

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);

        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnHear = (ImageButton) findViewById(R.id.btnHear);
        btnNextSentence = (ImageButton) findViewById(R.id.btnNextSentence);
        language_sp = (Spinner) findViewById(R.id.language_spinner);
        type_sp = (Spinner) findViewById(R.id.type_spinner);
        printVoiceText = (TextView) findViewById(R.id.spokenText);
        textToReadView = (TextView) findViewById(R.id.textToRead);
        ll_Hear = (LinearLayout) findViewById(R.id.ll_hear);
        tv_mic = (TextView) findViewById(R.id.tv_mic);
        textToReadView.setMovementMethod(new ScrollingMovementMethod());
        printVoiceText.setMovementMethod(new ScrollingMovementMethod());


        type_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String datasTyp = parent.getItemAtPosition(position).toString();
                if(datasTyp.equalsIgnoreCase("words"))
                    typeSpinner = "words";
                else
                    typeSpinner = "sentences";

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
        languages.add("Bangali");
        languages.add("Kannada");
        languages.add("Tamil");
        languages.add("Telugu");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language_sp.setAdapter(dataAdapter);


        language_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                languageSpinner = parent.getItemAtPosition(position).toString();

                if (languageSpinner.equalsIgnoreCase("Hindi")) {
                    selectedLanguage = "hi-IN";
                    ll_Hear.setVisibility(View.VISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("English")) {
                    selectedLanguage = "en-IN";
                    ll_Hear.setVisibility(View.VISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("Marathi")) {
                    selectedLanguage = "mr-IN";
                    ll_Hear.setVisibility(View.INVISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("Gujarati")) {
                    selectedLanguage = "gu-IN";
                    ll_Hear.setVisibility(View.INVISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("Bangali")) {
                    selectedLanguage = "bn-IN";
                    ll_Hear.setVisibility(View.INVISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("Kannada")) {
                    selectedLanguage = "kn-IN";
                    ll_Hear.setVisibility(View.INVISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("Tamil")) {
                    selectedLanguage = "ta-IN";
                    ll_Hear.setVisibility(View.INVISIBLE);
                } else if (languageSpinner.equalsIgnoreCase("Telugu")) {
                    selectedLanguage = "te-IN";
                    ll_Hear.setVisibility(View.INVISIBLE);
                }

                intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

                getReadingData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        List<String> types = new ArrayList<String>();
        types.add("Words");
        types.add("Sentences");

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
                    voiceStart = true;
                    RecordedSpeech = "";
                    recName="NA";
                    startSpeechInput();
                } else {
                    stopSpeechInput();
                    voiceStart = false;
                    tv_mic.setText("Speak");
                    try {
                        stopped = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    stopSpeechInput();
                }
            }
        });

        btnHear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaySpokenText();
            }
        });
    }

    private void stopSpeechInput() {
        speech.stopListening();
    }

    private void startSpeechInput() {
        speech.startListening(intent);
    }


    private void PlaySpokenText() {
        String writtenText = String.valueOf(printVoiceText.getText());
        Log.d("lang", "PlaySpokenText: " + selectedLanguage);
        ttspeech.playTTS(writtenText, selectedLanguage, 1);
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void getNextSentence() {

        BackupDatabase.backup(MainActivity.this);
        try {
            int randomNum = ThreadLocalRandom.current().nextInt(0, actualReadingData.length());
            mySentence = actualReadingData.getJSONObject(randomNum).getString("data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        textToReadView.setText(mySentence);
        printVoiceText.setText(" ");
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
    }

    @Override
    public void onResults(Bundle results) {

        System.out.println(LOG_TAG + " onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        tex = "";
        String tempResultArray = "";
        for (int i = 0; i < matches.size(); i++) {
            tempResultArray += " - " + matches.get(i);
            Log.d("res:::", "onResults: " + matches.get(i));
        }
        for (int i = 0; i < matches.size(); i++) {
            if (mySentence.equalsIgnoreCase(matches.get(i))) {
                tex = matches.get(i) + " ";
                break;
            } else
                tex = matches.get(0) + " ";

        }
        RecordedSpeech += tex;

/*        Toast.makeText(this, "" + tempResultArray, Toast.LENGTH_LONG).show();*/

        voiceStart = false;
        tv_mic.setText("Speak");

        myuser.ReordId = recName;
        myuser.UserName = uName;

        myuser.OriginalText = String.valueOf(textToReadView.getText());
        myuser.VoiceText = tex;

        myDBHelper.AddSttText(myuser);

        BackupDatabase.backup(MainActivity.this);

        try {
            if (!stopped) {
                stopped = true;
            }
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