/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.spreadtrum.iit.zpayapp.network.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


/**
 * For a given BLE device, this Activity provides the user interface to connect,
 * display data, and display GATT services and characteristics supported by the
 * device. The Activity communicates with {@code BluetoothService}, which in
 * turn interacts with the Bluetooth LE API.
 */
@SuppressLint("NewApi")
public class DeviceControlActivity extends Activity {
	private final static String TAG = "BLE";//DeviceControlActivity.class.getSimpleName();

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	private TextView mConnectionState;
	private TextView mDataField;
	private String mDeviceName;
	private String mDeviceAddress;
	private ExpandableListView mGattServicesList;
	private BluetoothService mBluetoothService;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private boolean mConnected = false;
	private BluetoothGattCharacteristic mNotifyCharacteristic;

	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";

	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothService = ((BluetoothService.LocalBinder) service)
					.getService();
			if (!mBluetoothService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			LogUtil.debug(TAG,"START CONNECTING... -BY LONG");
			mBluetoothService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothService = null;
		}
	};

	// Handles various events fired by the Service.
	// ACTION_GATT_CONNECTED: connected to a GATT server.
	// ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
	// ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
	// ACTION_DATA_AVAILABLE: received data from the device. This can be a
	// result of read
	// or notification operations.
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			System.out.println("action = " + action);
			Toast.makeText(DeviceControlActivity.this,"收到广播："+action,Toast.LENGTH_LONG).show();
			if (BluetoothService.ACTION_GATT_CONNECTED.equals(action)) {
				mConnected = true;
				updateConnectionState(R.string.connected);
				//invalidateOptionsMenu();
			} else if (BluetoothService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				mConnected = false;
				updateConnectionState(R.string.disconnected);
				//invalidateOptionsMenu();
				clearUI();
			} else if (BluetoothService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
				// Show all the supported services and characteristics on the
				// user interface.
				LogUtil.info(TAG,"display Gatt services");
				displayGattServices(mBluetoothService
						.getSupportedGattServices());
			} else if (BluetoothService.ACTION_DATA_AVAILABLE.equals(action)) {
				displayData(intent
						.getStringExtra(BluetoothService.EXTRA_DATA));
			}
		}
	};

	// If a given GATT characteristic is selected, check for supported features.
	// This sample
	// demonstrates 'Read' and 'Notify' features. See
	// http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for
	// the complete
	// list of supported characteristic features.
	private final ExpandableListView.OnChildClickListener servicesListClickListner = new ExpandableListView.OnChildClickListener() {
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			LogUtil.info(TAG,"groupPosition is:"+groupPosition+",childPosition is:"+childPosition);

			if (mGattCharacteristics != null) {
				final BluetoothGattCharacteristic characteristic = mGattCharacteristics
						.get(groupPosition).get(childPosition);
				final int charaProp = characteristic.getProperties();
				//System.out.println("charaProp = " + charaProp + ",UUID = " + characteristic.getUuid().toString());
				LogUtil.info(TAG,"charaProp = " + charaProp + ",UUID = " + characteristic.getUuid().toString());
				Random r = new Random();
				LogUtil.debug(TAG,"Position: "+groupPosition+","+childPosition+",property:"+charaProp);

				if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
						// If there is an active notification on a
						// characteristic, clear
						// it first so it doesn't update the data field on the
						// user interface.
						LogUtil.info(TAG,"PROPERTY_READ");
						if (mNotifyCharacteristic != null) {
							mBluetoothService.setCharacteristicNotification(
									mNotifyCharacteristic, false);
							mNotifyCharacteristic = null;
						}
						mBluetoothService.readCharacteristic(characteristic);
					}

				if(characteristic.getUuid().toString().equals("0003cdd1-0000-1000-8000-00805f9b0131")){
					//BluetoothGattCharacteristic readCharacteristic;
					//mBluetoothService.readCharacteristic(readCharacteristic);
					int properties = characteristic.getProperties();
					LogUtil.info(TAG,"property is:"+properties);	//PROPERTY_NOTIFY
					mNotifyCharacteristic = characteristic;
					mBluetoothService.setCharacteristicNotification(
							characteristic, true);
					Toast.makeText(DeviceControlActivity.this,"Notify",Toast.LENGTH_SHORT).show();
				}

				if(characteristic.getUuid().toString().equals("0003cdd2-0000-1000-8000-00805f9b0131")){
					int properties = characteristic.getProperties();
					LogUtil.info(TAG,"property is:"+properties);	//PROPERTY_WRITE_NO_RESPONSE

					String data = "123456";//ASCII码
					byte[] bytes = data.getBytes();
					characteristic.setValue(bytes);
					mBluetoothService.wirteCharacteristic(characteristic);
					LogUtil.debug(TAG,data);

					byte[] input = new byte[5];
					input[0]=0x21;
					input[1]=0x00;
					input[2]=0x02;
					input[3]=(byte)0xa5;
					input[4]=0x5a;
					characteristic.setValue(input);
					mBluetoothService.wirteCharacteristic(characteristic);
				}

//				if (characteristic.getUuid().toString()
//						.equals("0000fff2-0000-1000-8000-00805f9b34fb")) {
//						int time= 0;
//						while((time=r.nextInt(9))<=0){
//
//						}
//
//						String data = time+","+"1,,,,,";
//						characteristic.setValue(data.getBytes());
//						mBluetoothService.wirteCharacteristic(characteristic);
//				}
//				if (characteristic.getUuid().toString()
//						.equals("0000fff1-0000-1000-8000-00805f9b34fb")) {
//					int R = r.nextInt(255);
//					int G = r.nextInt(255);
//					int B = r.nextInt(255);
//					int BB = r.nextInt(100);
//					String data = R + "," + G + "," + B + "," + BB;
//					while (data.length() < 18) {
//						data += ",";
//					}
//					System.out.println(data);
//					characteristic.setValue(data.getBytes());
//					mBluetoothService.wirteCharacteristic(characteristic);
//				}
//				if (characteristic.getUuid().toString()
//						.equals("0000fff3-0000-1000-8000-00805f9b34fb")) {
//					int R = r.nextInt(255);
//					int G = r.nextInt(255);
//					int B = r.nextInt(255);
//					int BB = r.nextInt(100);
//					String data = R + "," + G + "," + B + "," + BB;
//					while (data.length() < 18) {
//						data += ",";
//					}
//					System.out.println("RT");
//					characteristic.setValue("RT".getBytes());
//					mBluetoothService.wirteCharacteristic(characteristic);
//				}
//				if (characteristic.getUuid().toString()
//						.equals("0000fff5-0000-1000-8000-00805f9b34fb")) {
//					characteristic.setValue("S".getBytes());
//					mBluetoothService.wirteCharacteristic(characteristic);
//					System.out.println("send S");
//				} else {
//
//					if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
//						// If there is an active notification on a
//						// characteristic, clear
//						// it first so it doesn't update the data field on the
//						// user interface.
//						LogUtil.info(TAG,"PROPERTY_READ");
//						if (mNotifyCharacteristic != null) {
//							mBluetoothService.setCharacteristicNotification(
//									mNotifyCharacteristic, false);
//							mNotifyCharacteristic = null;
//						}
//						mBluetoothService.readCharacteristic(characteristic);
//
//					}
//				}
//				if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
//					//LogUtil.info(TAG,"PROPERTY_NOTIFY for orgin");
//					if (characteristic.getUuid().toString().equals("0000fff6-0000-1000-8000-00805f9b34fb")||characteristic.getUuid().toString().equals("0000fff4-0000-1000-8000-00805f9b34fb")) {
//						System.out.println("enable notification");
//						mNotifyCharacteristic = characteristic;
//						mBluetoothService.setCharacteristicNotification(
//								characteristic, true);
//
//					}
//				}

				return true;
			}
			return false;
		}
	};

	private void clearUI() {
		mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
		mDataField.setText(R.string.no_data);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gatt_services_characteristics);

		final Intent intent = getIntent();
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

		// Sets up UI references.
		((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
		mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
		mGattServicesList.setOnChildClickListener(servicesListClickListner);
		mConnectionState = (TextView) findViewById(R.id.connection_state);
		mDataField = (TextView) findViewById(R.id.data_value);

		//getActionBar().setTitle(mDeviceName);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		Intent gattServiceIntent = new Intent(this, BluetoothService.class);
		boolean bll = bindService(gattServiceIntent, mServiceConnection,
				BIND_AUTO_CREATE);
		if (bll) {
			System.out.println("---------------");
		} else {
			System.out.println("===============");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.debug(TAG,"onResume");
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (mBluetoothService != null) {
			final boolean result = mBluetoothService.connect(mDeviceAddress);
			Log.d(TAG, "Connect request result=" + result);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mServiceConnection);
		mBluetoothService = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.gatt_services, menu);
		if (mConnected) {
			menu.findItem(R.id.menu_connect).setVisible(false);
			menu.findItem(R.id.menu_disconnect).setVisible(true);
		} else {
			menu.findItem(R.id.menu_connect).setVisible(true);
			menu.findItem(R.id.menu_disconnect).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_connect:
			mBluetoothService.connect(mDeviceAddress);
			return true;
		case R.id.menu_disconnect:
			mBluetoothService.disconnect();
			return true;
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateConnectionState(final int resourceId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mConnectionState.setText(resourceId);
			}
		});
	}

	private void displayData(String data) {
		if (data != null) {
			mDataField.setText(data);
		}
	}

	// Demonstrates how to iterate through the supported GATT
	// Services/Characteristics.
	// In this sample, we populate the data structure that is bound to the
	// ExpandableListView
	// on the UI.
	private void displayGattServices(List<BluetoothGattService> gattServices) {
		if (gattServices == null)
			return;
		String uuid = null;
		String unknownServiceString = getResources().getString(
				R.string.unknown_service);
		String unknownCharaString = getResources().getString(
				R.string.unknown_characteristic);
		ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
		ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
		mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {
			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			uuid = gattService.getUuid().toString();
			currentServiceData.put(LIST_NAME,
					SampleGattAttributes.lookup(uuid, unknownServiceString));
			currentServiceData.put(LIST_UUID, uuid);
			gattServiceData.add(currentServiceData);

			ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
			List<BluetoothGattCharacteristic> gattCharacteristics = gattService
					.getCharacteristics();
			ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

			// Loops through available Characteristics.
			for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				charas.add(gattCharacteristic);
				HashMap<String, String> currentCharaData = new HashMap<String, String>();
				uuid = gattCharacteristic.getUuid().toString();
				currentCharaData.put(LIST_NAME,
						SampleGattAttributes.lookup(uuid, unknownCharaString));
				currentCharaData.put(LIST_UUID, uuid);
				gattCharacteristicGroupData.add(currentCharaData);
			}
			mGattCharacteristics.add(charas);
			gattCharacteristicData.add(gattCharacteristicGroupData);
		}

		SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
				this, gattServiceData,
				android.R.layout.simple_expandable_list_item_2, new String[] {
						LIST_NAME, LIST_UUID }, new int[] { android.R.id.text1,
						android.R.id.text2 }, gattCharacteristicData,
				android.R.layout.simple_expandable_list_item_2, new String[] {
						LIST_NAME, LIST_UUID }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		mGattServicesList.setAdapter(gattServiceAdapter);
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

	public static String bytesToHexString(byte[] bytes) {
		String result = "";
		for (int i = 0; i < bytes.length; i++) {
			String hexString = Integer.toHexString(bytes[i] & 0xFF);
			if (hexString.length() == 1) {
				hexString = '0' + hexString;
			}
			result += hexString.toUpperCase();
		}
		return result;
	}
}
