package com.forrestpangborn.ibex.sample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG_LIST_ITEM = "list_item_tag";
	
	private boolean isSeeking;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final SeekBar seekbar = (SeekBar)findViewById(R.id.seek_bar);
		final TextView textview = (TextView)findViewById(android.R.id.text1);
		final ListView listview = (ListView)findViewById(android.R.id.list);
		
		seekbar.setProgress(seekbar.getMax() / 2);
		textview.setText(String.valueOf(seekbar.getProgress()));
		
		final BaseAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_item, getResources().getStringArray(R.array.image_urls)) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null || !TAG_LIST_ITEM.equals(convertView.getTag())) {
					convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item, parent, false);
					convertView.setTag(TAG_LIST_ITEM);
				}
				
				int progress = seekbar.getProgress();
				
				SampleImageView image = (SampleImageView)convertView.findViewById(android.R.id.icon);
				image.setPreventLoad(isSeeking);
				image.setUrl(getItem(position));
				LayoutParams params = image.getLayoutParams();
				params.width = progress;
				params.height = progress;
				image.setLayoutParams(params);
				
				TextView text2 = (TextView)convertView.findViewById(android.R.id.text2);
				text2.setText(String.valueOf(progress));
				return convertView;
			}
			
			
		};
		
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				SampleImageView imageView = (SampleImageView)view.findViewById(android.R.id.icon);
				Bitmap bmp = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
				Toast.makeText(MainActivity.this, "width, height :: " + bmp.getWidth() + ", " + bmp.getHeight(), Toast.LENGTH_LONG).show();
			}
		});
		
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				isSeeking = false;
				adapter.notifyDataSetChanged();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				isSeeking = true;
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					textview.setText(String.valueOf(progress));
					adapter.notifyDataSetChanged();
				}
			}
		});	
	}
}