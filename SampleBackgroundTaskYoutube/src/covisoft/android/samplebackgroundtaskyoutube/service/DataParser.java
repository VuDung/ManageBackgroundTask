package covisoft.android.samplebackgroundtaskyoutube.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import covisoft.android.samplebackgroundtaskyoutube.model.Video;

public class DataParser {
	public List<Video> parseVideo(String input){
		List<Video> mListVideo = new ArrayList<Video>();
		try{
			
			JSONObject root = new JSONObject(input);
			JSONObject data = root.optJSONObject("data");
			JSONArray array = data.optJSONArray("items");
			if(array != null){
				int length = array.length();
				for(int i=0; i < length; i++){
					JSONObject obj = array.optJSONObject(i);
					String id = obj.optString("id");
					String category = obj.optString("category");
					String player;
					try {
						player = obj.getJSONObject("player").getString("mobile");
					} catch (JSONException e) {
						// TODO: handle exception
						player = obj.getJSONObject("player").getString("default");
					}
					String thumbnail;
					try {
						thumbnail = obj.getJSONObject("thumbnail").getString("sqDefault");
					} catch (JSONException e) {
						// TODO: handle exception
						thumbnail = obj.getJSONObject("thumbnail").getString("hqDefault");
					}
					String title = obj.optString("title");
					String uploader = obj.optString("uploader");
					String viewCount = obj.optString("viewCount");
					
					mListVideo.add(new Video(id, uploader, category, title, thumbnail, player, viewCount));
				}
			}
		}catch(Exception e){
			return null;
		}
		return mListVideo;
	}
}
