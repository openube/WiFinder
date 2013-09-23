package com.jmcdale.wifinder;

import java.util.ArrayList;
import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jmcdale.wifinder.R;

public class MainActivity extends Activity {
	
	Button btnScan;
	Button btnTrain;
	TextView lblTrainingStatus;
	AutoCompleteTextView trainLocation;
	
	WifiManager wifi;
	WifiReceiver wifiReceiver;
	WiFiDataSQLiteHelper sqlHelper;
	ListView lvScanResults;
	ArrayList<ScanResult> wifiList = new ArrayList<ScanResult>();
	ScanResultsAdapter resultAdapter;
	boolean isTraining = false;
	
	ProgressDialog loading;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sqlHelper = new WiFiDataSQLiteHelper(this);
		
		wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		wifiReceiver = new WifiReceiver();
		this.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		
		startScan();
		
		btnScan = (Button) findViewById(R.id.btnScan);
		btnScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startScan();
			}
		});
		
		resultAdapter = new ScanResultsAdapter(this, wifiList);
		resultAdapter.setNotifyOnChange(true);
		
		lvScanResults = (ListView) findViewById(R.id.lvScanResults);
		lvScanResults.setAdapter(resultAdapter);
		
		btnTrain = (Button) findViewById(R.id.btnTrain);
		btnTrain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isTraining){
					startTraining();
				}else{
					stopTraining();
				}
			}
		});
		
		lblTrainingStatus = (TextView) findViewById(R.id.lblTrainingStatus);
		trainLocation = (AutoCompleteTextView) findViewById(R.id.inputTrainLocation);
		loadAutoCompleter();
	}//end onCreate

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	protected void onPause() {
		if(wifiReceiver != null){
			this.unregisterReceiver(wifiReceiver);
		}
        super.onPause();
    }

    protected void onResume() {
        if(wifiReceiver != null){
        	this.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        }
        super.onResume();
    }
	
	private void startScan(){
		loading = ProgressDialog.show(this,"","Scanning...",true);
		wifi.startScan();
	}
	
	private void loadAutoCompleter(){
		trainLocation.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, sqlHelper.getLocations()));
	}
	
	private void startTraining(){
		isTraining = true;
		lblTrainingStatus.setText("Training: ON");
		trainLocation.setVisibility(View.VISIBLE);
	}
	
	private void stopTraining(){
		isTraining = false;
		lblTrainingStatus.setText("Training: OFF");
		trainLocation.setVisibility(View.INVISIBLE);
	}
	
	private void train(){
		//get location that we are training
		String locationName = trainLocation.getText().toString();
		sqlHelper.insertTrainingSet(locationName, wifiList);
	}
	
	private void locateMe(){
		//TODO
	}
	
	class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
        	wifiList = new ArrayList<ScanResult>(wifi.getScanResults());
            resultAdapter.clear();
            resultAdapter.addAll(wifiList);
            
            if(isTraining){
            	train();
            }
            
            locateMe();
            
            loading.cancel();
        }
    }
}
