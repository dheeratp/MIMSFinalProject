
package citytrail.pedometer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.BroadcastReceiver;
import android.os.Message;
import android.os.PowerManager.WakeLock;
import android.os.PowerManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.view.MenuInflater;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.getpebble.android.kit.PebbleKit;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.IntentFilter;

public class Pedometer extends Activity {
	private static final String TAG = "City Trail";
	private SharedPreferences mSettings;
	private PedometerSettings mPedometerSettings;
	private Utils mUtils;
	public static int coinsValue;


	private TextView mStepValueView;
//	private TextView mPaceValueView;
//	private TextView mDistanceValueView;
//	private TextView mSpeedValueView;
//	private TextView mCaloriesValueView;
//	TextView mDesiredPaceView;
	public static int mStepValue;
	public static int parentcoinsValue;
	public TextView mChildStepCountView;
	//public TextView mChildGoalValueView;
	public TextView mChildCoinView;

	//public TextView mParentGoalValueView;
	public static int parentGoalValue;
	public TextView mParentCoinView;
//	private int mPaceValue;
//	private float mDistanceValue;
//	private float mSpeedValue;
	public static int mCaloriesValue;
//	private float mDesiredPaceOrSpeed;
	private int mMaintain;
	private boolean mIsMetric;
	private float mMaintainInc;
	private boolean mQuitting = false; // Set when user selected Quit from menu,
										// can be used by onPause, onStop,
										// onDestroy

	// START : ADDED BY DHEERA FOR DATALOGGING, PHONE ACCELEROMETER AND
	// ANIMATION CHANGES
	private static final UUID KIDSENSE_APP_UUID = UUID.fromString("07d87811-510f-48f2-b723-6bcfc4db9a40");
	public static int childstepcount = 0;
	public static int stepGoal=0;
	public static int childcoinValue=0;
	public static int totalsteps = 0;
	public static int todayssteps = 0;
	private TextView textView;
	private SensorManager mSensorManager;
	private Sensor mStepCounterSensor;
	private Sensor mStepDetectorSensor;
	int notificationCount;
	private WakeLock mWakeLock = null;
	private PebbleKit.PebbleDataLogReceiver mDataLogReceiver = null;
	public static final int SCREEN_OFF_RECEIVER_DELAY = 500;
	public Button startbutton;
	// END : ADDED BY DHEERA FOR DATALOGGING, PHONE ACCELEROMETER AND ANIMATION
	// CHANGES

	//Added for storing stepcount and coin data to SharedPreferences
	SharedPreferences state;
	SharedPreferences.Editor stateEditor;
	
	
	//public static final String TTDataStore = "TTDataStore" ;
	public static final String parentStepCountKey = "parentStepCountKey"; 
    public static final String parentCoinsKey = "parentCoinsKey"; 
    public static final String childStepCountKey = "childStepCountKey"; 
    public static final String childCoinsKey = "childCoinsKey"; 
	
    //Changes for IoT
    JSONObject jsonObject;
    static String url;
    URLConnection urlConn;
    DataOutputStream printout;
    
    /**
	 * True, when service is running.
	 */
	private boolean mIsRunning;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "[ACTIVITY] onCreate");
		super.onCreate(savedInstanceState);

		mStepValue = 0;
		parentGoalValue =100;
		//mPaceValue = 0;

		setContentView(R.layout.main);

		mUtils = Utils.getInstance();
		
		
		mStepValueView = (TextView) findViewById(R.id.step_value);
		mParentCoinView = (TextView) findViewById(R.id.parent_coins);
		//mParentGoalValueView = (TextView) findViewById(R.id.parent_goal);
		mChildStepCountView = (TextView) findViewById(R.id.log_data_text_view);
		//mChildGoalValueView = (TextView) findViewById(R.id.child_goal);
		mChildCoinView = (TextView) findViewById(R.id.child_coins);
		
	
		
		state = getSharedPreferences("state", 1);
		stateEditor = state.edit();
		int val1=0, val2=0, val3=0, val4=0;
	      if (state.contains(parentStepCountKey))
	      {
	    	  Log.i("1","parentStepCountKey="+parentStepCountKey);
	    	   val1 = state.getInt(parentStepCountKey, 0);
	    	  mStepValueView.setText(String.valueOf(val1));

	      }
	      if (state.contains(parentCoinsKey))
	      {
	    	  Log.i("2","parentCoinsKey="+parentCoinsKey);
	    	 val2 =state.getInt(parentCoinsKey, 0);
	    	  mParentCoinView.setText(String.valueOf(val2));

	      }
	      if (state.contains(childStepCountKey))
	      {
	    	  Log.i("3","childStepCountKey="+childStepCountKey);

	    	   val3=state.getInt(childStepCountKey, 0);
	    	  mChildStepCountView.setText(String.valueOf(val3));

	      }
	      if (state.contains(childCoinsKey))
	      {
	    	  Log.i("4","childCoinsKey="+childCoinsKey);

	    	   val4=state.getInt(childCoinsKey, 0);
	    	  mChildCoinView.setText(String.valueOf(val4));

	      }
	      
	      Log.i("Dheera-In onCreate()..values are"," val1="+ val1+" val2="+val2+" val3="+val3+" val4="+val4);
	      
	  

	}
	
	
	

	@Override
	protected void onStart() {
		Log.i(TAG, "[ACTIVITY] onStart");
		int v1=state.getInt(parentStepCountKey, 0);
		int v2=state.getInt(parentCoinsKey, 0);
		int v3=state.getInt(childStepCountKey, 0);
		int v4=state.getInt(childCoinsKey, 0);
		Log.i("PEDOMETER ONSTART()"," v1="+v1+" v2="+v2+" v3"+v3+" v4="+v4);
		
		super.onStart();
	}

	@Override
	protected void onResume() {

		super.onResume();
		final Handler handler = new Handler();
		
		Log.i("DHEERA-", "Inside onResume()");
		// start:code for datalogging from pebble
		mDataLogReceiver = new PebbleKit.PebbleDataLogReceiver(KIDSENSE_APP_UUID) {
			@Override
			public void receiveData(android.content.Context context,
					java.util.UUID logUuid, java.lang.Long timestamp,
					java.lang.Long tag, java.lang.Long data) {
				
				Toast.makeText(Pedometer.this, "Synced with Pebble.",Toast.LENGTH_SHORT).show();
				
				if(tag==4660)
				{
				childstepcount = data.intValue();
				}
				else if(tag==4661)
				{
					stepGoal = data.intValue();
				}
				else if(tag==4662)
				{
					childcoinValue = data.intValue();
				}
				
				handler.post(new Runnable() {
					@Override
					public void run() {
						updateUi();
					}
				});
			}
		};
		
		//Display pebble values even when pebble is not currently in sync with the phone
		if(childstepcount==0)
		{
			mChildStepCountView.setText("0"+" steps");
		}
		else
		{
			mChildStepCountView.setText(childstepcount+" steps");
		}
		
		if(childcoinValue==0)
		{
			mChildCoinView.setText("0 coins");
		}
		else
		{
			mChildCoinView.setText("Coins - "+childcoinValue);
		}

		stateEditor.putInt("childStepCountKey", childstepcount);
		stateEditor.putInt("childCoinsKey", childcoinValue);
		stateEditor.commit();
		
		
		PebbleKit.registerDataLogReceiver(this, mDataLogReceiver);
		PebbleKit.requestDataLogsForApp(this, KIDSENSE_APP_UUID);

		// end: code for datalogging from pebble

		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		mPedometerSettings = new PedometerSettings(mSettings);

		mUtils.setSpeak(mSettings.getBoolean("speak", false));

		// Read from preferences if the service was running on the last onPause
		mIsRunning = mPedometerSettings.isServiceRunning();

		// Start the service if this is considered to be an application start
		// (last onPause was long ago)
		if (!mIsRunning && mPedometerSettings.isNewStart()) {
			startStepService();
			bindStepService();
		} else if (mIsRunning) {
			bindStepService();
		}

		mPedometerSettings.clearServiceRunning();

		
		
		mIsMetric = mPedometerSettings.isMetric();
		mMaintain = mPedometerSettings.getMaintainOption();
	((LinearLayout) this.findViewById(R.id.desired_pace_control)).setVisibility(mMaintain != PedometerSettings.M_NONE ? View.VISIBLE
						: View.GONE);
		if (mMaintain == PedometerSettings.M_PACE) {
			mMaintainInc = 5f;
			//mDesiredPaceOrSpeed = (float) mPedometerSettings.getDesiredPace();
		} else if (mMaintain == PedometerSettings.M_SPEED) {
			//mDesiredPaceOrSpeed = mPedometerSettings.getDesiredSpeed();
			mMaintainInc = 0.1f;
		}
		Button button1 = (Button) findViewById(R.id.button_desired_pace_lower);
		button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//mDesiredPaceOrSpeed -= mMaintainInc;
				//mDesiredPaceOrSpeed = Math.round(mDesiredPaceOrSpeed * 10) / 10f;
				displayDesiredPaceOrSpeed();
				//setDesiredPaceOrSpeed(mDesiredPaceOrSpeed);
			}
		});
		Button button2 = (Button) findViewById(R.id.button_desired_pace_raise);
		button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//mDesiredPaceOrSpeed += mMaintainInc;
				//mDesiredPaceOrSpeed = Math.round(mDesiredPaceOrSpeed * 10) / 10f;
				displayDesiredPaceOrSpeed();
				//setDesiredPaceOrSpeed(mDesiredPaceOrSpeed);
			}
		});
		if (mMaintain != PedometerSettings.M_NONE) {
			((TextView) findViewById(R.id.desired_pace_label))
					.setText(mMaintain == PedometerSettings.M_PACE ? R.string.desired_pace
							: R.string.desired_speed);
		}

		displayDesiredPaceOrSpeed();
	}

	// Added by Dheera for phone's accelerometer part and datalogging from
	// pebble
	private void updateUi() {
		System.out.print("Inside updateUi()");
		
		if(childstepcount==0)
		{
			mChildStepCountView.setText("0"+" steps");
		}
		else
		{
			mChildStepCountView.setText(childstepcount+" steps");
		}
		
		if(childcoinValue==0)
		{
			mChildCoinView.setText("0 coins");
		}
		else
		{
			mChildCoinView.setText("Coins - "+childcoinValue);
		}
		
	}


	private void displayDesiredPaceOrSpeed() {
		if (mMaintain == PedometerSettings.M_PACE) {
			//mDesiredPaceView.setText("" + (int) mDesiredPaceOrSpeed);
		} else {
			//mDesiredPaceView.setText("" + mDesiredPaceOrSpeed);
		}
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "[ACTIVITY] onPause");
		if (mIsRunning) {
		}
		if (mQuitting) {
			//mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
		} else {
			//mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);
		}
		
		super.onPause();
		stateEditor.putInt("parentStepCountKey", mStepValue);
		stateEditor.putInt("parentCoinsKey", parentcoinsValue);
		stateEditor.putInt("childStepCountKey", childstepcount);
		stateEditor.putInt("childCoinsKey", childcoinValue);
		stateEditor.commit();
		//savePaceSetting();
		Log.i("INSIDEONPAUSE", "VALUES ==> "+mStepValue+" "+parentcoinsValue+" "+childstepcount+" "+childcoinValue);

		//Dheera: Added to resolve the memory leak issue
		if (mDataLogReceiver != null) {
            unregisterReceiver(mDataLogReceiver);
            mDataLogReceiver = null;
        }
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "[ACTIVITY] onStop");
		super.onStop();
		stateEditor.putInt("parentStepCountKey", mStepValue);
		stateEditor.putInt("parentCoinsKey", parentcoinsValue);
		stateEditor.putInt("childStepCountKey", childstepcount);
		stateEditor.putInt("childCoinsKey", childcoinValue);
		stateEditor.commit();
		Log.i("INSIDEONSTOP", "VALUES ==> "+mStepValue+" "+parentcoinsValue+" "+childstepcount+" "+childcoinValue);

	}

	protected void onDestroy() {
		Log.i(TAG, "[ACTIVITY] onDestroy");
		super.onDestroy();
		
		stateEditor.putInt("parentStepCountKey", mStepValue);
		stateEditor.putInt("parentCoinsKey", parentcoinsValue);
		stateEditor.putInt("childStepCountKey", childstepcount);
		stateEditor.putInt("childCoinsKey", childcoinValue);
		stateEditor.commit();
		
		Log.i("INSIDEONDESTROYOFPEDOMETER", "VALUES ==> "+mStepValue+" "+parentcoinsValue+" "+childstepcount+" "+childcoinValue);
	}

	protected void onRestart() {
		Log.i(TAG, "[ACTIVITY] onRestart");
		super.onDestroy();
		stateEditor.putInt("parentStepCountKey", mStepValue);
		stateEditor.putInt("parentCoinsKey", parentcoinsValue);
		stateEditor.putInt("childStepCountKey", childstepcount);
		stateEditor.putInt("childCoinsKey", childcoinValue);
		stateEditor.commit();
		
		Log.i("INSIDEONRESTART", "VALUES ==> "+mStepValue+" "+parentcoinsValue+" "+childstepcount+" "+childcoinValue);

		
	}

	private void setDesiredPaceOrSpeed(float desiredPaceOrSpeed) {
		if (mService != null) {
			if (mMaintain == PedometerSettings.M_PACE) {
				mService.setDesiredPace((int) desiredPaceOrSpeed);
			} else if (mMaintain == PedometerSettings.M_SPEED) {
				mService.setDesiredSpeed(desiredPaceOrSpeed);
			}
		}
	}

	private void savePaceSetting() {
	//	mPedometerSettings.savePaceOrSpeedSetting(mMaintain,mDesiredPaceOrSpeed);
	}

	private StepService mService;

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = ((StepService.StepBinder) service).getService();

			mService.registerCallback(mCallback);
			mService.reloadSettings();

		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};

	private void startStepService() {
		if (!mIsRunning) {
			Log.i(TAG, "[SERVICE] Start");
			mIsRunning = true;
			startService(new Intent(Pedometer.this, StepService.class));
		}
	}

	private void bindStepService() {
		Log.i(TAG, "[SERVICE] Bind");
		bindService(new Intent(Pedometer.this, StepService.class), mConnection,
				Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
	}

	private void unbindStepService() {
		Log.i(TAG, "[SERVICE] Unbind");
		stateEditor.putInt("parentStepCountKey", mStepValue);
		stateEditor.putInt("parentCoinsKey", parentcoinsValue);
		stateEditor.putInt("childStepCountKey", childstepcount);
		stateEditor.putInt("childCoinsKey", childcoinValue);
		stateEditor.commit();
		unbindService(mConnection);
		
		
		Log.i("INSIDEunbind", "VALUES ==> "+mStepValue+" "+parentcoinsValue+" "+childstepcount+" "+childcoinValue);

	}

	private void stopStepService() {
		Log.i(TAG, "[SERVICE] Stop");
		if (mService != null) {
			Log.i(TAG, "[SERVICE] stopService");
			stopService(new Intent(Pedometer.this, StepService.class));
		}
		mIsRunning = false;
	}

	private void resetValues(boolean updateDisplay) {
		if (mService != null && mIsRunning) {
			mService.resetValues();
		} else {
			mStepValueView.setText("0");
			mChildStepCountView.setText("0");
			mChildCoinView.setText("0");
			mParentCoinView.setText("0");;
			//mPaceValueView.setText("0");
			//mDistanceValueView.setText("0");
			//mSpeedValueView.setText("0");
			//mCaloriesValueView.setText("0");
			
			if (updateDisplay) {
				stateEditor.putInt("steps", 0);
				stateEditor.putInt("pace", 0);
				stateEditor.putFloat("distance", 0);
				stateEditor.putFloat("speed", 0);
				stateEditor.putFloat("calories", 0);
				
				stateEditor.putInt("parentStepCountKey",0);
				stateEditor.putInt("parentCoinsKey",0);
				stateEditor.putInt("childStepCountKey",0);
				stateEditor.putInt("childCoinsKey",0);
				stateEditor.commit();
			}
		}
	}

	private static final int MENU_SETTINGS = 8;
	private static final int MENU_QUIT = 9;

	private static final int MENU_PAUSE = 1;
	private static final int MENU_RESUME = 2;
	private static final int MENU_RESET = 3;

	/* Creates the menu items */
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		
		if (mIsRunning) {
			menu.add(0, MENU_PAUSE, 0, R.string.pause)
					.setIcon(android.R.drawable.ic_media_pause)
					.setShortcut('1', 'p');
		} else {
			menu.add(0, MENU_RESUME, 0, R.string.resume)
					.setIcon(android.R.drawable.ic_media_play)
					.setShortcut('1', 'p');
		}
		menu.add(0, MENU_RESET, 0, R.string.reset)
				.setIcon(android.R.drawable.ic_menu_close_clear_cancel)
				.setShortcut('2', 'r');
		menu.add(0, MENU_SETTINGS, 0, R.string.settings)
				.setIcon(android.R.drawable.ic_menu_preferences)
				.setShortcut('8', 's')
				.setIntent(new Intent(this, Settings.class));
		menu.add(0, MENU_QUIT, 0, R.string.quit)
				.setIcon(android.R.drawable.ic_lock_power_off)
				.setShortcut('9', 'q');
		return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_PAUSE:
			unbindStepService();
			stopStepService();
			return true;
		case MENU_RESUME:
			startStepService();
			bindStepService();
			return true;
		case MENU_RESET:
			resetValues(true);
			return true;
		case MENU_QUIT:
			resetValues(false);
			//unbindStepService();
			stopStepService();
			mQuitting = true;
			finish();
			return true;
		}
		return false;
	}

	// TODO: unite all into 1 type of message
	private StepService.ICallback mCallback = new StepService.ICallback() {
		public void stepsChanged(int value) {
			
		
			mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
			
			
		}

		public void paceChanged(int value) {
			mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
		}

		public void distanceChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG,
					(int) (value * 1000), 0));
		}

		public void speedChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG,
					(int) (value * 1000), 0));
		}

		public void caloriesChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG,
					(int) (value), 0));
		}
	};

	private static final int STEPS_MSG = 1;
	private static final int PACE_MSG = 2;
	private static final int DISTANCE_MSG = 3;
	private static final int SPEED_MSG = 4;
	private static final int CALORIES_MSG = 5;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case STEPS_MSG:
				mStepValue = (int) msg.arg1;
		
				
				parentcoinsValue=mStepValue/25;
				
				Log.i("HANDLER VALUES"," parentsteps="+mStepValue+ " parentcoins="+parentcoinsValue+" childteps="+state.getInt(childStepCountKey, 0)+" childcoins="+state.getInt(childCoinsKey, 0));
				
				//Adding stepcount and coin values to SharedPreferences
				stateEditor.putInt("parentStepCountKey", mStepValue);
				stateEditor.putInt("parentCoinsKey", parentcoinsValue);
				stateEditor.putInt("childStepCountKey", state.getInt(childStepCountKey, 0));
				stateEditor.putInt("childCoinsKey",state.getInt(childCoinsKey, 0));
				stateEditor.commit();
				
				if(mStepValue>=100 )
				{
					Toast.makeText(Pedometer.this, "Garfield completed TreasureTrail 1",Toast.LENGTH_SHORT).show();

				}
				if(state.getInt(childStepCountKey, 0)>=100 )
				{
					Toast.makeText(Pedometer.this, "Odie completed TreasureTrail 1",Toast.LENGTH_SHORT).show();

				}
				if(mStepValue==parentGoalValue/4)
				{
					coinsValue++;
				}
				else if (mStepValue==parentGoalValue/2){
					
					coinsValue++;
				}
				else if (mStepValue==parentGoalValue*0.75){
					
					coinsValue++;
				}
				else if (mStepValue==parentGoalValue){
					coinsValue++;
					 AlertDialog.Builder alert = new AlertDialog.Builder(Pedometer.this);
					    alert.setTitle("TreasureTrail in Italy is complete. Celebrate with snapshot!");
					    // alert.setMessage("Message");

					    alert.setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
					        public void onClick(DialogInterface dialog, int whichButton) {
					            //Your action here
					        }
					    });

					    alert.setNegativeButton("May Be Later",
					        new DialogInterface.OnClickListener() {
					            public void onClick(DialogInterface dialog, int whichButton) {
					            }
					        });

					    alert.show();
				}
				mStepValueView.setText(mStepValue+" steps");
				mParentCoinView.setText("Coins - "+state.getInt(parentCoinsKey, 0));
				mChildStepCountView.setText(state.getInt(childStepCountKey, 0)+" steps");
				mChildCoinView.setText("Coins - "+state.getInt(childCoinsKey, 0));
				
				
				
				
				break;
			case PACE_MSG:
				//mPaceValue = msg.arg1;
				//if (mPaceValue <= 0) {
					//mPaceValueView.setText("0");
				//} else {
					//mPaceValueView.setText("" + (int) mPaceValue);
				//}
				break;
			case DISTANCE_MSG:
				//mDistanceValue = ((int) msg.arg1) / 1000f;
				//if (mDistanceValue <= 0) {
					//mDistanceValueView.setText("0");
				//} else {
					//mDistanceValueView.setText(("" + (mDistanceValue + 0.000001f)).substring(0, 5));
				//}
				break;
			case SPEED_MSG:
				//mSpeedValue = ((int) msg.arg1) / 1000f;
				//if (mSpeedValue <= 0) {
					//mSpeedValueView.setText("0");
				//} else {
					//mSpeedValueView.setText(("" + (mSpeedValue + 0.000001f)).substring(0, 4));
				//}
				break;
			case CALORIES_MSG:
				mCaloriesValue = msg.arg1;
				if (mCaloriesValue <= 0) {
					//mCaloriesValueView.setText("0");
				} else {
					//mCaloriesValueView.setText("" + (int) mCaloriesValue);
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}

	};

}