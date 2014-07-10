package covisoft.android.samplebackgroundtaskyoutube;

import covisoft.android.managebackgroundtask.api.API;
import android.app.Application;

public class MainApplication extends Application{

	private MainApplication instance;
	
	public MainApplication getInstance(){
		if(instance == null){
			instance = new MainApplication();
		}
		return instance;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		API.HOST_URL = "https://gdata.youtube.com/feeds/api/standardfeeds";
	}
	
	
}
