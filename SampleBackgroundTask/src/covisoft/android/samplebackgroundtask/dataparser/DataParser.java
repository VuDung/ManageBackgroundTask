package covisoft.android.samplebackgroundtask.dataparser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import covisoft.android.samplebackgroundtask.model.Place;

public class DataParser {

	public DataParser(boolean isJson) {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public List<Place> parserPlaceList(String jsonString){
		try {
			JSONArray jArray = new JSONArray(jsonString);
            List<Place> places = new ArrayList<Place>();
            for (int i= 0; i< jArray.length(); i++){
            	JSONObject jObj = jArray.getJSONObject(i);
            	String id = jObj.getString("id");
				String name = jObj.getString("name");
				String address = jObj.getString("address");
				String phoneNumber = jObj.getString("phoneNumber");
				String promotionPercentage ;
				if(null != jObj.getString("promotionPercentage")
					    || jObj.getString("promotionPercentage").trim().length() != 0){
					promotionPercentage = jObj.getString("promotionPercentage");
				}else{
					promotionPercentage = "0";
				}
				String image = jObj.getString("image");
				String expiryDate = jObj.getString("expiryDate");
				String category = jObj.getString("category");
				String district = jObj.getString("district");
				String features = jObj.getString("features");
				String lat = jObj.getString("lat");
				String lng = jObj.getString("lng");
				String conditions = jObj.getString("conditions");
				String description = jObj.getString("description");
				places.add(new Place(id, name, address, phoneNumber, promotionPercentage, image, expiryDate, category, district,features,lat,lng,conditions,description));
            }
            return places;
	      
	        
        }catch(JSONException e){
        	e.printStackTrace();
        	return null;
        }
	}

}
