package com.forrestpangborn.ibex.sample;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG_LIST_ITEM = "list_item_tag";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final SeekBar seekbar = (SeekBar)findViewById(R.id.seek_bar);
		final TextView textview = (TextView)findViewById(android.R.id.text1);
		final ListView listview = (ListView)findViewById(android.R.id.list);
		
		seekbar.setProgress(seekbar.getMax() / 2);
		textview.setText(String.valueOf(seekbar.getProgress()));
		
		List<String> strings = new ArrayList<String>();
		strings.add("AAA");
		strings.add("BBB");
		strings.add("CCC");
		strings.add("DDD");
		strings.add("EEE");
		strings.add("FFF");
		strings.add("GGG");
		strings.add("HHH");
		strings.add("III");
		strings.add("KKK");
		strings.add("LLL");
		strings.add("MMM");
		strings.add("NNN");
		strings.add("OOO");
		strings.add("PPP");
		strings.add("QQQ");
		strings.add("RRR");
		strings.add("SSS");
		strings.add("TTT");
		strings.add("UUU");
		strings.add("VVV");
		strings.add("WWW");
		strings.add("XXX");
		strings.add("YYY");
		strings.add("000");
		strings.add("111");
		strings.add("222");
		strings.add("333");
		strings.add("444");
		strings.add("555");
		strings.add("666");
		strings.add("777");
		strings.add("888");
		strings.add("999");
		
		final BaseAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_item, strings) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null || !TAG_LIST_ITEM.equals(convertView)) {
					convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item, parent, false);
					convertView.setTag(TAG_LIST_ITEM);
				}
				
				TextView text1 = (TextView)convertView.findViewById(android.R.id.text1);
				text1.setText(getItem(position));
				
				TextView text2 = (TextView)convertView.findViewById(android.R.id.text2);
				text2.setText(String.valueOf(seekbar.getProgress()));
				return convertView;
			}
		};
		
		listview.setAdapter(adapter);
		
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
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
