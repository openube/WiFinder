package com.jmcdale.wifinder;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.jmcdale.wifinder.R;

public class ScanResultsAdapter extends ArrayAdapter<ScanResult> {
	private final Context context;
//	private final ArrayList<ScanResult> values;
	  
	public ScanResultsAdapter(Context context, ArrayList<ScanResult> values) {
	    super(context, R.layout.list_item_scan_result, values);
	    this.context = context;
//	    this.values = values;
	  }

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.list_item_scan_result, parent, false);
	    
	    TextView lblBssid = (TextView) rowView.findViewById(R.id.dataBssid);
	    TextView lblSsid = (TextView) rowView.findViewById(R.id.dataSsid);
	    TextView lblCapabilities = (TextView) rowView.findViewById(R.id.dataCapabilities);
	    TextView lblFrequency = (TextView) rowView.findViewById(R.id.dataFrequency);
	    TextView lblLevel = (TextView) rowView.findViewById(R.id.dataLevel);
	    TextView lblTimestamp = (TextView) rowView.findViewById(R.id.dataTimestamp);
	    
//	    ScanResult s = values.get(position);
	    ScanResult s = this.getItem(position);
	    lblBssid.setText(s.BSSID);
	    lblSsid.setText(s.SSID);
	    lblCapabilities.setText(s.capabilities);
	    lblFrequency.setText("" + s.frequency);
	    lblLevel.setText("" + s.level);
	    lblTimestamp.setText("");
	    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
	    	lblTimestamp.setText(""+s.timestamp);
	    }
	    
	    return rowView;
	  }
	  
}
