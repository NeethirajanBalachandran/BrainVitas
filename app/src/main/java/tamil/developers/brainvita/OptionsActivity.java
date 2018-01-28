package tamil.developers.brainvita;

import java.util.Locale;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OptionsActivity extends Activity {

	private AdView mAdView;
    static SQLiteDatabase myDB;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);

		mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
        TextView tx = findViewById(R.id.lblSound);
        tx.setText(R.string.lblSound);
		databaseCreation();
		sound();
		coinSet();
		setPrevNext();
	}
	private void setPrevNext() {
		final ImageView prev = findViewById(R.id.back);
		final ImageView next = findViewById(R.id.next);
		prev.setImageBitmap(GetPrevNextImage(1));
		next.setImageBitmap(GetPrevNextImage(3));
		prev.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					prev.setImageBitmap(GetPrevNextImage(2));
					break;
				case MotionEvent.ACTION_UP:
					prev.setImageBitmap(GetPrevNextImage(1));
					back();
					break;
				default:
					break;
				}
				return true;
			}
		});
		next.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					next.setImageBitmap(GetPrevNextImage(4));
					break;
				case MotionEvent.ACTION_UP:
					next.setImageBitmap(GetPrevNextImage(3));
					next();
					break;
				default:
					break;
				}
				return true;
			}
		});
		SharedPreferences preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
		int coin = preferences.getInt("Coin", 1);
		String strW = preferences.getString("Width", "0");
		int ww = Integer.parseInt("0" + strW);
		ImageView img =findViewById(R.id.coin);
		img.setImageBitmap(GetCoinSetImage(coin));
		img.getLayoutParams().width = (int) (ww * 0.30);
		img.getLayoutParams().height = (int) (ww * 0.60);
	}
	private void back() {
		SharedPreferences preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		int coin = preferences.getInt("Coin", 1);
		if (coin > 1){
			ImageView img =findViewById(R.id.coin);
			img.setImageBitmap(GetCoinSetImage(coin-1));
			editor.putInt("Coin", coin-1);
			editor.apply();
			myDB.execSQL("UPDATE Options Set value = " + (coin - 1) + " Where id = 2;");
		}
	}
	private void next() {
		SharedPreferences preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		int coin = preferences.getInt("Coin", 1);
		if (coin < 6){
			ImageView img =findViewById(R.id.coin);
			img.setImageBitmap(GetCoinSetImage(coin+1));
			editor.putInt("Coin", coin+1);
			editor.apply();
			myDB.execSQL("UPDATE Options Set value = " + (coin + 1) + " Where id = 2;");
		}
	}
	private Bitmap GetPrevNextImage(int i){
		Drawable drawable = getResources().getDrawable(R.drawable.prev_next);
		Bitmap bitmap = ((BitmapDrawable) drawable ).getBitmap();
		if (i == 1) bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth()/2, bitmap.getHeight()/2);
		if (i == 2) bitmap = Bitmap.createBitmap(bitmap , 0, bitmap.getWidth()/2, bitmap.getWidth()/2, bitmap.getHeight()/2);
		if (i == 3) bitmap = Bitmap.createBitmap(bitmap , bitmap.getWidth()/2, 0, bitmap.getWidth()/2, bitmap.getHeight()/2);
		if (i == 4) bitmap = Bitmap.createBitmap(bitmap , bitmap.getWidth()/2, bitmap.getWidth()/2, bitmap.getWidth()/2, bitmap.getHeight()/2);
		return bitmap;
	}
	private Bitmap GetCoinSetImage(int i){
		Drawable drawable = getResources().getDrawable(R.drawable.coinset);
		Bitmap bitmap = ((BitmapDrawable) drawable ).getBitmap();
		int ww = bitmap.getWidth()/6;
		bitmap = Bitmap.createBitmap(bitmap , ww * (i-1), 0, ww, bitmap.getHeight());
		return bitmap;
	}
	private void sound() {
		final Button btn = findViewById(R.id.soundonoff);
		SharedPreferences preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
		final SharedPreferences.Editor editor = preferences.edit();
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (btn.getText().equals("On")){
					btn.setText(R.string.off);
					editor.putString("Sound", "Off");
					myDB.execSQL("UPDATE Options Set value = 2 Where id = 2;");
				} else {
					btn.setText(R.string.on);
					editor.putString("Sound", "On");
					myDB.execSQL("UPDATE Options Set value = 1 Where id = 2;");
				}
				editor.apply();
			}
		});
		Cursor c1 = myDB.rawQuery("SELECT * FROM Options Where id = 1;", null);
		if (c1.getCount() > 0){
			c1.moveToFirst();
			int OnOrOff = c1.getInt(1);
			if (OnOrOff == 1){
				editor.putString("Sound", "On");
				btn.setText(R.string.on);
			} else {
				editor.putString("Sound", "Off");
				btn.setText(R.string.off);
			}
		} else {
			editor.putString("Sound", "On");
			btn.setText(R.string.on);
			myDB.execSQL("INSERT INTO Options (id, value)  VALUES (1,1);");
		}
		editor.apply();
		c1.close();
	}
	private void coinSet() {
		SharedPreferences preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		Cursor c1 = myDB.rawQuery("SELECT * FROM Options Where id = 2;", null);
		if (c1.getCount() > 0){
			c1.moveToFirst();
			editor.putInt("Coin", c1.getInt(1));
		} else {
			editor.putInt("Coin", 1);
			myDB.execSQL("INSERT INTO Options (id, value)  VALUES (2,1);");
		}
		editor.apply();
		c1.close();
	}
	private void databaseCreation() {
		//database creation start
		String dbName  = this.getFilesDir().getPath() + "/" + getPackageName()+"/Brain_vita.db";
		myDB = openOrCreateDatabase(dbName , Context.MODE_PRIVATE, null);
	    myDB.setVersion(1);
	    myDB.setLocale(Locale.getDefault());
	    myDB.execSQL("CREATE TABLE IF NOT EXISTS Options (id INT(2), value INT(4));");
	}
	@Override
	public void onPause() {
		if (mAdView != null) {
			mAdView.pause();
		}
		super.onPause();
	}
	@Override
	public void onResume() {
		super.onResume();
		if (mAdView != null) {
			mAdView.resume();
		}
	}
	@Override
	public void onDestroy() {
		if (mAdView != null) {
			mAdView.destroy();
		}
		super.onDestroy();
	}
    @Override
	public void onBackPressed() {
		Intent openMainList = new Intent(OptionsActivity.this, AppMain.class);
        startActivity(openMainList);
        finish();
	}
}
