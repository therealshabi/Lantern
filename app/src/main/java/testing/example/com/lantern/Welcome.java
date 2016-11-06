
package testing.example.com.lantern;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class Welcome extends AppCompatActivity implements TextToSpeech.OnInitListener {

    Button btn;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btn = (Button) findViewById(R.id.started);
        tts = new TextToSpeech(this, this);
        tts.setLanguage(Locale.UK);
        tts.setSpeechRate(0.7f);

        /*speakOut("Lantern");
        speakOut("Enlightning your lives");*/

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getBaseContext(),ChatHeadService.class));
                speakOut("Lets get started");
            }
        });
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.UK);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut("");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOut(String s) {
        tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
    }
}
