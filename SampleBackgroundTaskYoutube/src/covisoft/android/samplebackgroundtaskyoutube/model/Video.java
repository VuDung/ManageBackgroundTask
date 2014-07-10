package covisoft.android.samplebackgroundtaskyoutube.model;

public class Video {
	private String id;
	private String uploader;
	private String category;
	private String title;
	private String thumbnail;
	private String player;
	private String viewCount;
	
	
	
	public Video() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Video(String id, String uploader, String category, String title,
			String thumbnail, String player, String viewCount) {
		super();
		this.id = id;
		this.uploader = uploader;
		this.category = category;
		this.title = title;
		this.thumbnail = thumbnail;
		this.player = player;
		this.viewCount = viewCount;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUploader() {
		return uploader;
	}
	public void setUploader(String uploader) {
		this.uploader = uploader;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public String getPlayer() {
		return player;
	}
	public void setPlayer(String player) {
		this.player = player;
	}
	public String getViewCount() {
		return viewCount;
	}
	public void setViewCount(String viewCount) {
		this.viewCount = viewCount;
	}
	
	

}
