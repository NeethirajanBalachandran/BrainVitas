package tamil.developers.brainvita;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HighScore extends Activity {
	Pattern ptn = new Pattern();
    static SQLiteDatabase myDB;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		databasecreation();
		ListView listView = (ListView) findViewById(R.id.listView1);
		final ArrayList<Map<String, String>> list = buildData();
	    String[] from = { "text1", "text2" };
	    int[] to = {R.id.text1, R.id.text2 }; 
	    SimpleAdapter adapter = new SimpleAdapter(this, list,R.layout.level_list_layout, from, to){
            @SuppressLint("InflateParams")
			@Override
	        public View getView(int pos, View convertView, ViewGroup parent){
	            View v = convertView;
	            if(v== null){
	                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	                v=vi.inflate(R.layout.level_list_layout, null);
	            }
	            String[] str = list.get(pos).get("text2").split(":");
	            ((TextView)v.findViewById(R.id.text1)).setText(list.get(pos).get("text1"));
	            if (str[0].equals("0")) ((TextView)v.findViewById(R.id.result_time)).setText("Best: No best.");
	            else ((TextView)v.findViewById(R.id.result_time)).setText("Best: " + str[0] + "seconds for " + str[1] + " Coin remaining.");
	            ((TextView)v.findViewById(R.id.text2)).setText("Played " + str[2] + " times.");
	            if (str[1].equals("1")) ((TextView)v.findViewById(R.id.result_count)).setText("Status: " + "Completed.");
	            else if(Integer.parseInt("0" + str[2]) == 0)((TextView)v.findViewById(R.id.result_count)).setText("Status: " + "Not yet started.");
	            else ((TextView)v.findViewById(R.id.result_count)).setText("Status: " + "In-progress.");
	            return v;
	        }
		};
		listView.setAdapter(adapter);
	}
	private void databasecreation() {
		//database creation start
		String dbname  = this.getFilesDir().getPath() + "/" + getPackageName()+"/Brain_vita.db";
		myDB = openOrCreateDatabase(dbname , Context.MODE_PRIVATE, null);
	    myDB.setVersion(1);
	    myDB.setLocale(Locale.getDefault());
	    myDB.execSQL("CREATE TABLE IF NOT EXISTS Result (id INT(3), btime INT(4), coinrem INT(2), count INT(5));");
	}
	private ArrayList<Map<String, String>> buildData() {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
	    int count = ptn.Patterns.length;
	    for (int i=0; i<count; i++){
	    	Cursor c1 = myDB.rawQuery("SELECT * FROM Result Where id = " + i, null);
			if (c1.getCount() > 0){
				c1.moveToFirst();
				list.add(putData("Game" + (i+1), c1.getInt(1) + ":" + c1.getInt(2) + ":" + c1.getInt(3)));
			} else {
				list.add(putData("Game" + (i+1), "0:0:0"));
			}
			c1.close();
	    }
	    return list;
	}
	private HashMap<String, String> putData(String text1, String text2) {
	    HashMap<String, String> item = new HashMap<String, String>();
	    item.put("text1", text1);
	    item.put("text2", text2);
	    return item;
	}
	@Override
    public void onBackPressed(){
		Intent openMainList = new Intent(HighScore.this, AppMain.class);
		startActivity(openMainList);
		finish();
    }
}
