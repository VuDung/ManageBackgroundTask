package covisoft.android.samplebackgroundtask;

import covisoft.android.managebackgroundtask.api.API;
import android.app.Application;
import android.util.Log;

public class MainApplication extends Application {
	private static MainApplication instance;
	private static final String TAG = MainApplication.class.getSimpleName();
	
	public static MainApplication getInstance(){
		if(instance == null){
			instance = new MainApplication();
		}
		return instance;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		API.HOST_URL = "http://thegioiuudai.com.vn/apps/server.php";
		Log.d(TAG, API.HOST_URL);
	}
	
	
}
