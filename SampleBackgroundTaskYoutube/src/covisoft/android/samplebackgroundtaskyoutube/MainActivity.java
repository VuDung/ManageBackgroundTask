package covisoft.android.samplebackgroundtaskyoutube;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.costum.android.widget.LoadMoreListView;
import com.costum.android.widget.LoadMoreListView.OnLoadMoreListener;

import covisoft.android.managebackgroundtask.Task;
import covisoft.android.managebackgroundtask.TaskAction;
import covisoft.android.managebackgroundtask.TaskResponse;
import covisoft.android.managebackgroundtask.listener.ITaskListenter;
import covisoft.android.samplebackgroundtaskyoutube.adapter.VideoAdapter;
import covisoft.android.samplebackgroundtaskyoutube.model.Video;
import covisoft.android.samplebackgroundtaskyoutube.service.DataParser;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

public class MainActivity extends Activity implements ITaskListenter, OnLoadMoreListener{

	public static int START_INDEX = 1;
	public static int MAX_RESULT = 15;
	
	private LoadMoreListView mListVideoView;
	private List<Video> mListVideo = new ArrayList<Video>();
	private List<Video> mListVideoTemp;
	private VideoAdapter mAdapter;
	private ProgressBar mProgress;
	
	private boolean isLoading = true;
	
	private static String TAG = MainActivity.class.getSimpleName();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
        
        mListVideoView = (LoadMoreListView)findViewById(R.id.lvVideo);
        mProgress = (ProgressBar)findViewById(R.id.pbLoadVideo);
        mAdapter = new VideoAdapter(this);
        
        mListVideoView.setAdapter(mAdapter);
        
        mListVideoView.setOnLoadMoreListener(this);
        
        showProgress(isLoading);
        
        callTask();
        
      
    }
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(mListVideo == null){
			callTask();
		}
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		START_INDEX = 1;
	}

	private void showProgress(boolean loading){
		mProgress.setVisibility(loading ? View.VISIBLE : View.GONE);
	}

	private void callTask(){
    	Task task = new Task(this);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("max-results", MAX_RESULT);
        params.put("start-index", START_INDEX);
        task.loginTask(TaskAction.ActionGetVideo, APIType.POPULAR, params, true);
    }

	@Override
	public void onComplete(Task task, TaskResponse data) {
		// TODO Auto-generated method stub
		if(data.isSuccess() && data.getAction() == TaskAction.ActionGetVideo){
			mListVideoView.onLoadMoreComplete();
			isLoading = false;
			showProgress(isLoading);
			
			String resultString = (String)data.getData();
			Log.e(TAG, "OnComplete: " + data.getAction() + " " + resultString);
			
			
			DataParser parser = new DataParser();
			mListVideoTemp = parser.parseVideo(resultString);
			if(mListVideoTemp.size() != 0){
				mListVideo.addAll(mListVideoTemp);
			}else{
				mListVideoView.getmFooterView().setVisibility(View.GONE);
			}			
			Log.e(TAG, "OnComplete: " + mListVideo.size());
			
			mAdapter.setListVideo(mListVideo);
			
			START_INDEX += 15;
		}
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		callTask();
	}



}
