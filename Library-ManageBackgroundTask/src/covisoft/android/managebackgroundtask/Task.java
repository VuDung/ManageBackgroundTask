package covisoft.android.managebackgroundtask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import covisoft.android.managebackgroundtask.api.API;
import covisoft.android.managebackgroundtask.listener.ITaskListenter;

public class Task implements Runnable{
	
	private static int CONNECTION_TIMEOUT = 30000;
	private static int SO_TIMEOUT = 600000;
	
	private Task mTask;
	private TaskAction mAction;
	
	private ArrayList<ITaskListenter> mArrListenter;
	private Map<String, Object> mParams;
	private String mActionUri;
	
	private boolean isIncludeHost;
	private boolean isConnecting;
	private boolean isGet;
	private boolean isBitmap;
	private HttpClient mHttpClient;
	
	private Thread mThread;
	
	private static final String TAG = Task.class.getSimpleName();

	@SuppressLint("HandlerLeak") 
	final Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			for (ITaskListenter listener : mArrListenter){
				if(listener != null){
					listener.onComplete(mTask, (TaskResponse) msg.obj);
				}
			}
		}
		
	};
	
	public Task(ITaskListenter... listenters) {
		mAction = TaskAction.ActionNone;
		mArrListenter = new ArrayList<ITaskListenter>();
		if(listenters != null){
			for(ITaskListenter listener : listenters){
				mArrListenter.add(listener);
			}
		}
		
		isIncludeHost = true;
		isConnecting = false;
		isBitmap = false;
		mTask = this;
	}
	
	public void loginTask(TaskAction action, String taskType, Map<String, Object> params){
		mAction = action;
		request(taskType, params, true, true);
	}
	
	public boolean isConnecting(){
		return isConnecting;
	}
	
	private boolean request(
			String uri, 
			Map<String, Object> params, 
			boolean isIncludeHost){
		if(isConnecting){
			return false;
		}
		this.isConnecting = true;
		this.mActionUri = uri;
		this.mParams = params;
		this.isIncludeHost = isIncludeHost;
		
		mThread = new Thread(this);
		mThread.start();
		
		return true;
	}
	
	private boolean request(
			String uri, 
			Map<String, Object> params, 
			boolean isIncludeHost,
			boolean isGet){
		this.isGet = isGet;
		request(uri, params, isIncludeHost);
		return true;
	}
	
	private void processError(ResultCode errorCode){
		Message msg = mHandler.obtainMessage(0, new TaskResponse(mAction, errorCode, null));
		mHandler.sendMessage(msg);
	}

	private void dispatchResult(String result){
		if(mArrListenter == null || mAction == TaskAction.ActionNone || !isConnecting){
			return;
		}
		
		TaskAction act = mAction;
		Object resObj = result;
		TaskResponse response = null;
		switch (act) {
			case ActionNone:
				
				break;
			default:
				resObj = result;
				break;
		}
		if(resObj == null){
			response = new TaskResponse(act, ResultCode.Failed, null);
		}else{
			response = new TaskResponse(act, ResultCode.Success, resObj);
		}
		
		stop();
		
		Message msg = mHandler.obtainMessage(0, response);
		mHandler.sendMessage(msg);
	}
	
	private void dispatchResult(Bitmap result){
		if(mArrListenter == null || mAction == TaskAction.ActionNone || !isConnecting){
			return;
		}
		
		TaskAction act = mAction;
		TaskResponse response = null;
		if(result == null){
			response = new TaskResponse(act, ResultCode.Failed, null);
		}else{
			response = new TaskResponse(act, ResultCode.Success, result);
		}
		
		stop();
		Message msg = mHandler.obtainMessage(0, response);
		mHandler.sendMessage(msg);
	}

	@Override
	public void run() {
		Log.d(mAction.toString(), " RUN " + mActionUri);
		
		mHttpClient = new DefaultHttpClient();
		
		HttpParams httpParams = mHttpClient.getParams();
		
		HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		HttpConnectionParams.setTcpNoDelay(httpParams, true);
		
		try {
			String urlString = isIncludeHost ? API.HOST_URL + mActionUri : mActionUri;
			Log.d(TAG, urlString);
			HttpRequestBase request = null;
			
			if(isGet){
				request = new HttpGet();
				if(mParams != null){
					attachUriWithQuery(request, Uri.parse(urlString), mParams);
				}
			}else{
				
				request = new HttpPost();
				if(mParams != null){
					MultipartEntity reqEntity = paramsToList2(mParams);
					((HttpPost)request).setEntity(reqEntity);
				}
			}
			
			//set default headers
			HttpResponse response = mHttpClient.execute(request);
			InputStream in = null;
			
			if(response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK){
				Header[] header = response.getHeaders("Content-Encoding");
				if(header != null && header.length != 0){
					for(Header h : header){
						if(h.getName().trim().equalsIgnoreCase("gzip"));
						in = new GZIPInputStream(response.getEntity().getContent());
					}
				}
				
				if(in == null){
					in = new BufferedInputStream(response.getEntity().getContent());
				}
				if(isBitmap){
					Bitmap bm = BitmapFactory.decodeStream(in);
					dispatchResult(bm);
				}else{
					String temp = convertStreamToString(in);
					Log.d(mAction.toString(), " = " + temp);
					
					in.close();
					dispatchResult(temp);
				}
			}else if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
				processError(ResultCode.Failed);
			}else if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_SERVER_ERROR) {
				processError(ResultCode.ServerError);
			}else{
				processError(ResultCode.NetworkError);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			processError(ResultCode.NetworkError);
		} finally{
			mHttpClient.getConnectionManager().shutdown();
		}
	}
	
	public void stop(){
		//cleanUp();
		if(mHttpClient != null){
			mHttpClient.getConnectionManager().shutdown();
		}
		mAction = TaskAction.ActionNone;
		isConnecting = false;
	}
	
	private void attachUriWithQuery(HttpRequestBase request, Uri uri, Map<String, Object> params){
		try {
			if(params == null){
				request.setURI(new URI(uri.toString()));
			}else{
				Uri.Builder uriBuilder = uri.buildUpon();
				
				//Loop through our params and append them to the uri
				for(BasicNameValuePair param : paramsToList(params)){
					uriBuilder.appendQueryParameter(param.getName(), param.getValue());
				}
				uri = uriBuilder.build();
				request.setURI(new URI(uri.toString()));
				
			}
		} catch (URISyntaxException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	private static List<BasicNameValuePair> paramsToList(Map<String, Object> params){
		ArrayList<BasicNameValuePair> formList = new ArrayList<BasicNameValuePair>(params.size());
		for(String key : params.keySet()){
			Object value = params.get(key);
			if(value != null){
				formList.add(new BasicNameValuePair(key, value.toString()));
			}
		}
		return formList;
	}
	
	
	private static MultipartEntity paramsToList2(Map<String, Object> params){
		MultipartEntity reqEntity = new MultipartEntity();
		for(String key : params.keySet()){
			try {
				Object value = params.get(key);
				if(key.toUpperCase().equals("FILE")){
					reqEntity.addPart(key, (ContentBody) value);
				}else{
					Charset chars = Charset.forName("UTF-8");
					reqEntity.addPart(key, new StringBody(value.toString(), chars));
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return reqEntity;
	}
	
	private String convertStreamToString(InputStream is){
		/*
		 * To convert the InputStream to String we use the Reader.read(char[]
		 * buffer) method. We iterate until the Reader return -1 which means
		 * there's no more data to read. We use the StringWriter class to
		 * produce the string.
		 */

		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} catch (IOException e) {
				return "";
			} finally {
				try {
					is.close();
				} catch (IOException e) {

				}
			}
			return writer.toString();
		} else {
			return "";
		}
	}
}
