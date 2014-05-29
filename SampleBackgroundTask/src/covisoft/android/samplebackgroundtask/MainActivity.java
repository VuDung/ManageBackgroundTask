package covisoft.android.samplebackgroundtask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import covisoft.android.managebackgroundtask.Task;
import covisoft.android.managebackgroundtask.TaskAction;
import covisoft.android.managebackgroundtask.TaskResponse;
import covisoft.android.managebackgroundtask.listener.ITaskListenter;
import covisoft.android.samplebackgroundtask.api.APIType;
import covisoft.android.samplebackgroundtask.dataparser.DataParser;
import covisoft.android.samplebackgroundtask.model.Place;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

public class MainActivity extends Activity implements ITaskListenter {

	private Task mTask;
	public ProgressBar pbPlace;
	private static boolean isRequested = false;
	private static List<Place> mListPlace;
	
	private static final String TAG = MainActivity.class.getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		pbPlace = (ProgressBar)findViewById(R.id.pbPlace);
		if(!isRequested){
			pbPlace.setVisibility(View.VISIBLE);
			mTask = new Task(this);
			getPlaceList();
		}else{
			pbPlace.setVisibility(View.GONE);
			Log.i(TAG, "LIST PLACE SIZE: " + mListPlace.size());
		}
		
	}
	
	private void getPlaceList(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("city", 30);
		params.put("industry", 1);
		mTask.loginTask(TaskAction.ActionGetPlace, APIType.LIST, params);
		pbPlace.setVisibility(View.VISIBLE);
	}

	@Override
	public void onComplete(Task task, TaskResponse data) {
		if(data.isSuccess() && data.getAction() == TaskAction.ActionGetPlace){
			DataParser parser = new DataParser(true);
			mListPlace = parser.parserPlaceList((String)data.getData());
			Log.i(TAG, "LIST PLACE SIZE: " + mListPlace.size());
			pbPlace.setVisibility(View.GONE);
			isRequested = true;
		}
	}
	

}
