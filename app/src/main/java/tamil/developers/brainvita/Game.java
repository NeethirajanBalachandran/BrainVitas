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
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xml.sax.Parser;

import tamil.developers.brainvita.DragDropManager.DropZoneListener;

public class Game extends Activity implements OnTouchListener {
    private AdView mAdView;
	public boolean sound = true;
	public static boolean[] aryBools = {
		false, false, false, false, false, false, 
		false, false, false, false, false, false, 
		false, false, false, false, false, false, 
		false, false, false, false, false, false,
		false, false, false, false, false, false, 
		false, false, false, false, false, false, 
		false, false, false, false, false, false, 
		false, false, false, false, false, false, 
		false, false, false, false, false, false, 
		false, false, false, false, false, false, 
		false, false, false, false, false, false, 
		false, false, false, false, false, false, 
		false, false, false, false, false, false, 
		false, false, false, false};
	private Chronometer crono;
	boolean first_click = true;
	int timeInMin = 0;
	private PopupWindow pw;
	MediaPlayer mp;
	int final_score = 0;
	int w, h;
	int row, col;
	Pattern ptn = new Pattern();
	int GAME = 0;
	String move;
	boolean completed = false;
    static SQLiteDatabase myDB;
    int[] content;
    int[] content1;
    int coinSet = 1;
    Bitmap bm_normal;
    Bitmap bm_empty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
        SharedPreferences prfs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
    	GAME = prfs.getInt("Game", 0);
        init_sound();
        init_value();
    	GetCoinImage(coinSet);
        DragDropManager.getInstance().init(this);
        Create_Layout();
        move = ptn.movement(GAME);
    }
	private void Create_Layout() {
    	row = ptn.rowCol[GAME][0];
    	col = ptn.rowCol[GAME][1];
    	content = new int[row*col];
    	content1 = new int[ptn.coinCount(GAME)];
		int pw = (int)((w * 0.95)/col);
		int ph = (w/row);
		RelativeLayout rl = findViewById(R.id.Gamelayout);
		int id = 0;
		for (int i=0;i<ptn.Patterns[GAME].length;i++){
			RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(pw, ph);
		    rlp.setMargins(((i%col)*pw),((i/col)*ph), 0, 0);
			ImageView coinImg = new ImageView(this);
			if (ptn.Patterns[GAME][i] == 1){
				coinImg.setImageBitmap(bm_empty);
				aryBools[id] = false;
				coinImg.setOnTouchListener(this);
				content1[id] = i;
				id++;
				content[i] = id;
			} else if (ptn.Patterns[GAME][i] == 2){
				coinImg.setImageBitmap(bm_normal);
				coinImg.setOnTouchListener(this);
				aryBools[id] = true;
				content1[id] = i;
				id++;
				content[i] = id;
			} else {
				coinImg.setImageResource(0);
				content[i] = 0;
			}
			coinImg.setContentDescription(id + ":" + i);
			coinImg.setId(id);
			coinImg.setLayoutParams(rlp);
		    rl.addView(coinImg);
		}
		for (int i=0; i<id; i++) {
        	DragDropManager.getInstance().addDropZone(findViewById(i+1), dropZoneListener1);
        }
		DragDropManager.getInstance().addDropZone(findViewById(R.id.container), dropZoneListener1);
	}
	private void GetCoinImage(int i){
		Drawable drawable = getResources().getDrawable(R.drawable.coinset);
		Bitmap bitmap = ((BitmapDrawable) drawable ).getBitmap();
		int ww = bitmap.getWidth()/6;
		 bm_empty = Bitmap.createBitmap(bitmap , ww * (i-1), 0, ww, bitmap.getHeight()/2);
		 bm_normal = Bitmap.createBitmap(bitmap , ww * (i-1), bitmap.getHeight()/2, ww, bitmap.getHeight()/2);
	}
	private void init_sound() {
    	SharedPreferences prfs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
    	String strSound = prfs.getString("Sound", "Off");
    	if (strSound.equals("On")){
			sound = true;
			((ImageView)findViewById(R.id.sound)).setImageResource(R.mipmap.sound_on);
    	} else {
			sound = false;
			((ImageView)findViewById(R.id.sound)).setImageResource(R.mipmap.sound_off);
		}
	}
	private void init_value() {
		SharedPreferences prfs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
    	String strW = prfs.getString("Width", "0");
    	String strH = prfs.getString("Height", "0");
    	w = Integer.parseInt("0" + strW);
    	h = Integer.parseInt("0" + strH);
		coinSet = prfs.getInt("Coin", 1);
		first_click = true;
		timeInMin = 0;
		final_score = 0;
	}
    DropZoneListener dropZoneListener1 = new DropZoneListener() {
        @Override
        public void OnDropped(View zone, Object item)
        {
        	if (!completed){
	            int str_from = Integer.parseInt("0" + item);
				if (zone.getId() == R.id.container){
	        		ImageView img = findViewById(str_from);
					img.setImageBitmap(bm_empty);
					completed = true;
					return;
	        	}
				int str_to = zone.getId();
				int str_mid = findMidNumber(str_from, str_to);
				boolean right = false;
				if (str_mid > 0) right = check_ids(str_from,str_to,str_mid);
	            if (right) {
					ImageView img = findViewById(str_from);
					img.setImageBitmap(bm_normal);
					img = findViewById(str_to);
					img.setImageBitmap(bm_empty);
					img = findViewById(str_mid);
					img.setImageBitmap(bm_normal);
					aryBools[str_from-1] = true;
					aryBools[str_to-1] = false;
					aryBools[str_mid-1] = true;
					sound(true);
					completed = true;
				}
	            else {
	            	sound(false);
	            	ImageView img = findViewById(str_from);
					img.setImageBitmap(bm_empty);
					completed = true;
	            }
				if (first_click) {
					startCrono();
					first_click = false;
				}
				if (check_is_move_there()) {
					stopCrono();
				}
        	}
        }
        @Override
        public void OnDragZoneLeft(View zone, Object item){}
        @Override
        public void OnDragZoneEntered(View zone, Object item){}
    };
	@Override
    public boolean onTouch(View v, MotionEvent event) {
    	if (!aryBools[v.getId()]) //noted changed code.
    	{
    		DragDropManager.getInstance().startDragging(v, v.getId());
    		((ImageView)v).setImageBitmap(bm_normal);
    		completed = false;
    	}
        return false;
    }
    private boolean check_is_move_there() {
		boolean rtn = true;
    	String[] moveArr = move.split("=");
		for (int i = 0 ; i < ptn.coinCount(GAME) ; i ++) {
			if (aryBools[i] == false){
				String[] new_move = moveArr[i].split(":");
				for (int j = 0 ; j < new_move.length ; j ++) {
					boolean right = check_ids(i+1, Integer.parseInt(new_move[j]),findMidNumber(i+1,Integer.parseInt(new_move[j])));
					if (right) {
						rtn = false;
						break;
					}
				}
			}
			if (!rtn) break;
		}
		return rtn;
	}
    private boolean check_ids(int from, int to,int mid) {
		boolean right_move = false;
		if (mid != 0 ) {
			if (!aryBools[from-1] && !aryBools[mid-1] && aryBools[to-1]) {
				right_move = true;
			}
		}
		return right_move;
	}
    private int findMidNumber(int from, int to) {
    	int f_num = content1[from-1];
    	int t_num = content1[to-1];
    	int diff = f_num - t_num;
    	if (diff < 0){
    		if (diff == -2){
    			return content[f_num+1];
    		} else if (diff == (-2*col)){
    			return content[f_num+col];
    		} else {
    			return -1;
    		}
    	} else {
    		if (diff == 2){
    			return content[f_num-1];
    		} else if (diff == (2*col)){
    			return content[f_num-col];
    		} else {
    			return -1;
    		}
    	}
 	}
    public void startCrono() {
		 this.crono = findViewById(R.id.calling_crono);
		 crono.setBase(SystemClock.elapsedRealtime());
		 crono.start();
		 crono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
		     @Override
		     public void onChronometerTick(Chronometer chronometer) {
	    		 TextView txt = findViewById(R.id.timer);
				 timeInMin += 1;
	    		 txt.setText((timeInMin + ""));
		     }
		});
	}
	public void stopCrono() {
		crono.stop();
		int count = 0;
		for (int i = 0 ; i < ptn.coinCount(GAME) ; i ++) {
			if (!aryBools[i]) {
				count = count + 1;
			}
		}
		int best_time = 0;
		int best_count = 0;
		Update_count();
		Cursor c1 = myDB.rawQuery("SELECT * FROM Result Where id = " + GAME, null);
		if (c1.getCount() > 0){
			c1.moveToFirst();
			if (c1.getInt(2) >= count || c1.getInt(2) == 0){
				if (c1.getInt(1) > timeInMin || c1.getInt(1) == 0){
					myDB.execSQL("UPDATE Result SET btime = " + timeInMin + " Where id = " + GAME + ";");
					best_time = timeInMin;
				} else {
					best_time = c1.getInt(1);
				}
				myDB.execSQL("UPDATE Result SET coinrem = " + count + " Where id = " + GAME + ";");
				best_count = count;
			} else {
				best_count = c1.getInt(2);
			}
		}
		c1.close();
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.finish_box, null, false);
		pw = new PopupWindow(layout, (int) (w * 0.80), (int) (h * 0.30), true);
		pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
		Button btn = layout.findViewById(R.id.menu);
		btn.setOnClickListener(menu);
		btn = layout.findViewById(R.id.next);
		btn.setOnClickListener(next);
		btn = layout.findViewById(R.id.retry);
		btn.setOnClickListener(retry);
		
		TextView txt = layout.findViewById(R.id.score);
		if (count == 1) txt.setText("Game Completed in " + timeInMin + "sec.");
		else txt.setText("Game Completed in " + timeInMin + "sec, remaining coins are " + count + ".");
		txt = layout.findViewById(R.id.best);
		c1 = myDB.rawQuery("SELECT * FROM Result Where id = " + GAME, null);
		if (c1.getCount() > 0){
			c1.moveToFirst();
			best_time = c1.getInt(1);
			best_count = c1.getInt(2);
		}
		c1.close();
		if (best_count == 1) txt.setText("Game Completed in " + best_time + "sec.");
		else txt.setText("Best: " + best_time + "sec, remaining coins are " + best_count + ".");
	}
    private void Update_count() {
    	String dbname  = this.getFilesDir().getPath() + "/" + getPackageName()+"/Brain_vita.db";
		myDB = openOrCreateDatabase(dbname , Context.MODE_PRIVATE, null);
	    myDB.setVersion(1);
	    myDB.setLocale(Locale.getDefault());
	    myDB.execSQL("CREATE TABLE IF NOT EXISTS Result (id INT(3), btime INT(4), coinrem INT(2), count INT(5));");
	    Cursor c1 = myDB.rawQuery("SELECT * FROM Result Where id = " + GAME, null);
		if (c1.getCount() > 0){
			c1.moveToFirst();
			myDB.execSQL("UPDATE Result SET count = " + (c1.getInt(3) + 1) + " Where id = " + GAME + ";");
		} else {
			myDB.execSQL("INSERT INTO Result (id, btime, coinrem, count)  VALUES (" + GAME + ",0,0,1);");
		}
		c1.close();
	}
	private OnClickListener retry = new OnClickListener() {
       public void onClick(View v) {
           pw.dismiss();
           Intent openMainList = new Intent(Game.this, Game.class);
           startActivity(openMainList);
           finish();
       }
	};
	private OnClickListener next = new OnClickListener() {
		public void onClick(View v) {
           pw.dismiss();
           SharedPreferences preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
           SharedPreferences.Editor editor = preferences.edit();
           if (GAME+1 < ptn.Patterns.length){
        	   editor.putInt("Game", GAME+1);
           } else {
        	   editor.putInt("Game", 0);
           }
           editor.apply();
           Intent openMainList = new Intent(Game.this, Game.class);
           startActivity(openMainList);
           finish();
       }
	};
	private OnClickListener menu = new OnClickListener() {
       public void onClick(View v) {
           pw.dismiss();
           Intent openMainList = new Intent(Game.this, AppMain.class);
           startActivity(openMainList);
           finish();
       }
	};
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
		if (crono != null) crono.stop();
		Intent openMainList = new Intent(Game.this, AppMain.class);
		startActivity(openMainList);
		finish();
	}
	public void sound_call(View v){
		SharedPreferences preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		if (sound){
			editor.putString("Sound", "Off");
			((ImageView) v).setImageResource(R.mipmap.sound_off);
			sound = false;
		}
		else {
			editor.putString("Sound", "On");
			((ImageView) v).setImageResource(R.mipmap.sound_on);
			sound = true;
		}
		editor.apply();
	}
	private void sound(boolean right) {
		if (sound) {
			if (right)	mp = MediaPlayer.create(this, R.raw.tone1);
			else 		mp = MediaPlayer.create(this, R.raw.tone3);
			mp.start();
		}
	}
}
