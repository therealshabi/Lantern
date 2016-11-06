package testing.example.com.lantern;

/**
 * Created by shahbaz on 6/11/16.
 */

import android.Manifest;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.AlarmClock;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatHeadService extends Service implements GestureOverlayView.OnGesturePerformedListener, TextToSpeech.OnInitListener {

    private WindowManager windowManager;
    private ImageView chatHead;
    private GestureOverlayView gestureOverlayView;
//    public static int check=0;
    //   private float Amount=1;

    private GestureLibrary gestureLibrary;

    private TextToSpeech tts;


    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();

        tts = new TextToSpeech(this, this);
        tts.setLanguage(Locale.UK);
        tts.setSpeechRate(0.7f);

        gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gesture);
        if (!gestureLibrary.load()) {
            stopService(new Intent(getBaseContext(), ChatHeadService.class));
        }


        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        final WindowManager.LayoutParams paramsGesture = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                PixelFormat.TRANSLUCENT);

        paramsGesture.dimAmount = 0.7f;

        gestureOverlayView = new GestureOverlayView(getBaseContext());
        gestureOverlayView.setGestureVisible(true);
        gestureOverlayView.setEventsInterceptionEnabled(true);
        gestureOverlayView.setGestureStrokeType(1);
        gestureOverlayView.addOnGesturePerformedListener(this);

        chatHead = new ImageView(this);
        chatHead.setImageResource(R.drawable.chathead);
        chatHead.setElevation(6.0f);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        windowManager.addView(chatHead, params);
        chatHead.setClickable(true);


        chatHead.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        if ((Math.abs(initialTouchX - event.getRawX()) < 5) && (Math.abs(initialTouchY - event.getRawY()) < 5)) {
                            windowManager.addView(gestureOverlayView, paramsGesture);
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(chatHead, params);
                        return true;
                }

                return false;
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null) windowManager.removeView(chatHead);
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLibrary.recognize(gesture);
        if (predictions.size() > 0 && predictions.get(0).score > 1.0) {
            String result = predictions.get(0).name;
            if ("dialer".equalsIgnoreCase(result)) {
                speakOut("Dialer Opened");
                Intent intent = new Intent(this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (intent != null) {
                    startActivity(intent);
                }
                windowManager.removeView(gestureOverlayView);
            } else if ("clock".equalsIgnoreCase(result)) {
                speakOut("Clock Opened");
                Intent intent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (intent != null) {
                    startActivity(intent);
                }
                windowManager.removeView(gestureOverlayView);
                String currentDateAndTime = DateFormat.getDateTimeInstance().format(new Date());
                speakOut(currentDateAndTime);
            } else if ("music".equalsIgnoreCase(result)) {
                speakOut("Music Player Opened");
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.music");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (intent != null) {
                    startActivity(intent);
                }
                windowManager.removeView(gestureOverlayView);
            } else if ("camera".equalsIgnoreCase(result)) {
                speakOut("Camera Opened");
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (intent != null) {
                    startActivity(intent);
                }
                windowManager.removeView(gestureOverlayView);
            } else if ("emergency".equalsIgnoreCase(result)) {
                speakOut("Calling Your Emergency Contact");
                try {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:9711049618"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(intent);
                    windowManager.removeView(gestureOverlayView);
                } catch (ActivityNotFoundException e) {
                    Log.e("TAG", "Making A Phone CAll Error: ", e);
                }
            } else {
                speakOut("Gesture Not Recognized");
                Toast.makeText(this, "Gesture Not Recognized", Toast.LENGTH_SHORT).show();
                windowManager.removeView(gestureOverlayView);

            }
        }
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

    private void speakOut(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
