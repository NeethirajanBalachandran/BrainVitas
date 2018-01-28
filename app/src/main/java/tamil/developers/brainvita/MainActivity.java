package tamil.developers.brainvita;

import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

public class MainActivity extends Activity {
	Handler handler;
	int w,h;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		h = metrics.heightPixels;
		w = metrics.widthPixels;
		Start();
		ImageView img = findViewById(R.id.logo);
		img.getLayoutParams().width = (int)(w * 0.70);
		SharedPreferences preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("Width", w + "");
		editor.putString("Height", h + "");
		editor.putString("Sound", "Off");
		editor.apply();
	}
	private void Start() {
		handler = new Handler();
	    startProgress();		
	}
	public void startProgress() {
	    // Do something long
	    Runnable runnable = new Runnable() {
	      @Override
	      public void run() {
	        for (int i = 0; i <= 100; i++) {
	          //final int value = i;
	          try {
	            Thread.sleep(30);
	          } catch (InterruptedException e) {
	            e.printStackTrace();
	          }
	          handler.post(new Runnable() {
	            @Override
	            public void run() {
	            }
	          });
	        }
	        Intent openMainList = new Intent(MainActivity.this, AppMain.class);
	        startActivity(openMainList);
	        finish();
	      }
	    };
	    new Thread(runnable).start();
	}
	@Override
	public void onBackPressed() {
		//don't exit
	}
}
