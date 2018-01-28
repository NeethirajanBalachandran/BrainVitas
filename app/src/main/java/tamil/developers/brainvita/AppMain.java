package tamil.developers.brainvita;

import java.util.Locale;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AppMain extends Activity {

	int w,h;
    static SQLiteDatabase myDB;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_main);

		SharedPreferences prfs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
    	String strW = prfs.getString("Width", "0");
    	String strH = prfs.getString("Height", "0");
    	w = Integer.parseInt("0" + strW);
    	h = Integer.parseInt("0" + strH);
    	
		ImageView img = findViewById(R.id.GameName);
		img.getLayoutParams().width = (int)(w * 0.90);
		img.getLayoutParams().height = (int)(h * 0.08);
		
		img = findViewById(R.id.play);
		img.getLayoutParams().width = (int)(w * 0.60);
		img.getLayoutParams().height = (int)(h * 0.08);
		img.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				startActivity(new Intent(AppMain.this, ListActivity.class));
				finish();
			}
		});
		img = findViewById(R.id.score);
		img.getLayoutParams().width = (int)(w * 0.60);
		img.getLayoutParams().height = (int)(h * 0.06);
		img.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				startActivity(new Intent(AppMain.this, HighScore.class));
				finish();
			}
		});
		img = findViewById(R.id.demo);
		img.getLayoutParams().width = (int)(w * 0.60);
		img.getLayoutParams().height = (int)(h * 0.08);
		img.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				startActivity(new Intent(AppMain.this, OptionsActivity.class));
				finish();
			}
		});
		img = findViewById(R.id.about);
		img.getLayoutParams().width = (int)(w * 0.60);
		img.getLayoutParams().height = (int)(h * 0.08);
		img.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				startActivity(new Intent(AppMain.this, About.class));
				finish();
			}
		});
		databaseCreation();
		sound();
		coinSet();
	}
	private void sound() {
		SharedPreferences preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		Cursor c1 = myDB.rawQuery("SELECT * FROM Options Where id = 1;", null);
		if (c1.getCount() > 0){
			c1.moveToFirst();
			int OnOrOff = c1.getInt(1);
			if (OnOrOff == 1){
				editor.putString("Sound", "On");
			} else {
				editor.putString("Sound", "Off");
			}
		} else {
			editor.putString("Sound", "On");
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
		super.onPause();
	}
	@Override
	public void onResume() {
		super.onResume();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
