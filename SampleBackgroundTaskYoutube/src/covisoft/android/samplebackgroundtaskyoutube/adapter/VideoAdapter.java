package covisoft.android.samplebackgroundtaskyoutube.adapter;

import java.util.List;

import com.squareup.picasso.Picasso;

import covisoft.android.samplebackgroundtaskyoutube.R;
import covisoft.android.samplebackgroundtaskyoutube.model.Video;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VideoAdapter extends BaseAdapter{

	private Context mContext;
	private List<Video> mListVideo;
	private LayoutInflater mInflater;
	
	public VideoAdapter(Context mContext) {
		super();
		this.mContext = mContext;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setListVideo(List<Video> listVideo){
		if(listVideo != null && !listVideo.isEmpty()){
			mListVideo = listVideo;
		}else{
			mListVideo = null;
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return (mListVideo != null ? mListVideo.size() : 0);
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mListVideo.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.layout_video_item, null);
			holder = new ViewHolder();
			holder.mThumbnail = (ImageView)convertView.findViewById(R.id.imgThumbnailVideo);
			holder.mTitle = (TextView)convertView.findViewById(R.id.tvTitleVideo);
			holder.mUploader = (TextView)convertView.findViewById(R.id.tvUploaderVideo);
			holder.mViewCount = (TextView)convertView.findViewById(R.id.tvViewCountVideo);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		final Video item = mListVideo.get(position);
		
		Picasso.with(mContext)
			.load(item.getThumbnail())
			.resize(70, 70)
			.placeholder(R.drawable.empty_photo)
			.error(R.drawable.empty_photo)
			.into(holder.mThumbnail);
		holder.mTitle.setText(item.getTitle());
		holder.mUploader.setText(item.getUploader());
		holder.mViewCount.setText(item.getViewCount() + " views");
		return convertView;
	}
	
	private class ViewHolder{
		private ImageView mThumbnail;
		private TextView mTitle;
		private TextView mUploader;
		private TextView mViewCount;
	}

}
