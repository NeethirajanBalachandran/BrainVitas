package tamil.developers.brainvita;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class About extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
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
	public void onBackPressed() {
		Intent openMainList = new Intent(this, AppMain.class);
    	startActivity(openMainList);
    	finish();
	}
}
