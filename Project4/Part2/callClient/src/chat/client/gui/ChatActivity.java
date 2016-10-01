/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
 *****************************************************************/

package chat.client.gui;

import java.util.logging.Level;

import jade.core.MicroRuntime;
import jade.util.Logger;
import jade.wrapper.ControllerException;
import jade.wrapper.O2AException;
import jade.wrapper.StaleProxyException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import chat.client.agent.ChatClientInterface;
import chat.client.agent.ChatClientAgent;
import chat.client.agent.ChatClientAgent.ChatSpeaker;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


/**
 * This activity implement the chat interface.
 * 
 * @author Michele Izzo - Telecomitalia
 */

public class ChatActivity extends Activity {
	private Logger logger = Logger.getJADELogger(this.getClass().getName());

	static final int PARTICIPANTS_REQUEST = 0;

	private MyReceiver myReceiver;

	private String nickname;
	private ChatClientInterface chatClientInterface;
	TelephonyManager tm;
	boolean email = false;
	String myNum;
	
	int quota = 0;

	private LocationManager locationManager;
	private String provider;
	NotificationCompat.Builder mBuilder;
	NotificationManager mNotificationManager;
	
	PhoneCallListener listener;
	boolean ring;
	boolean callReceived;
	boolean missedCall;
	
	String callerNum; 
	String calleeNum;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mNotificationManager =
				   (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		myNum = tm.getLine1Number();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			nickname = extras.getString("nickname");
		}
		provider = LocationManager.GPS_PROVIDER;
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager.getLastKnownLocation(provider);

		locationManager.requestLocationUpdates(provider, 0, 0,
				new LocationListener() {
					@Override
					public void onLocationChanged(Location location) {
						// if (location != null)
						// updateWithNewLocation(location);
					}

					@Override
					public void onStatusChanged(String provider, int status,
							Bundle extras) {

					}

					@Override
					public void onProviderEnabled(String provider) {

					}

					@Override
					public void onProviderDisabled(String provider) {

					}
				});

		try {
			chatClientInterface = MicroRuntime.getAgent(nickname)
					.getO2AInterface(ChatClientInterface.class);
		} catch (StaleProxyException e) {
			showAlertDialog(getString(R.string.msg_interface_exc), true);
		} catch (ControllerException e) {
			showAlertDialog(getString(R.string.msg_controller_exc), true);
		}

		myReceiver = new MyReceiver();

		IntentFilter refreshChatFilter = new IntentFilter();
		refreshChatFilter.addAction("jade.demo.chat.REFRESH_CHAT");
		registerReceiver(myReceiver, refreshChatFilter);

		IntentFilter clearChatFilter = new IntentFilter();
		clearChatFilter.addAction("jade.demo.chat.CLEAR_CHAT");
		registerReceiver(myReceiver, clearChatFilter);


		setContentView(R.layout.chat);

		Button button = (Button) findViewById(R.id.button_send);
		button.setOnClickListener(buttonSendListener);
		
		TelephonyManager mgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

		listener = new PhoneCallListener();
		mgr.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		

	}
	
	private class PhoneCallListener extends PhoneStateListener {

		boolean ring = false;
		boolean callReceived = false;
		boolean missedCall = false;

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			
			callerNum = incomingNumber; 
			calleeNum = tm.getLine1Number();
			
			if (TelephonyManager.CALL_STATE_RINGING == state) {
				// phone ringing
				ring = true;
			}

			if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
				// active
				callReceived = true;
			}

			// When the call ends launch the main activity again
			if (TelephonyManager.CALL_STATE_IDLE == state) {
		    	if (ring == true && callReceived == false) {
			           missedCall = true;
			           quota++;
			    }
		    	if (quota >= 2 && myNum.equals(calleeNum)) {
		    		try {
						chatClientInterface.handleSpoken("Please email me at " + nickname + "@ncsu.edu instead of calling.\n");
						quota = 0;
					} catch (O2AException e) {
						showAlertDialog(e.getMessage(), false);
					}
		    	}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unregisterReceiver(myReceiver);

		logger.log(Level.INFO, "Destroy activity!");
	}

	private OnClickListener buttonSendListener = new OnClickListener() {
		public void onClick(View v) {
			final EditText messageField = (EditText) findViewById(R.id.edit_message);
			String message = messageField.getText().toString();
			if (message != null && !message.equals("")) {
				try {
					chatClientInterface.handleSpoken(message);
					messageField.setText("");
				} catch (O2AException e) {
					showAlertDialog(e.getMessage(), false);
				}
			}

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.chat_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_participants:
			Intent showParticipants = new Intent(ChatActivity.this,
					ParticipantsActivity.class);
			showParticipants.putExtra("nickname", nickname);
			startActivityForResult(showParticipants, PARTICIPANTS_REQUEST);
			return true;
		case R.id.menu_clear:
			/*
			 * Intent broadcast = new Intent();
			 * broadcast.setAction("jade.demo.chat.CLEAR_CHAT");
			 * logger.info("Sending broadcast " + broadcast.getAction());
			 * sendBroadcast(broadcast);
			 */
			final TextView chatField = (TextView) findViewById(R.id.chatTextView);
			chatField.setText("");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PARTICIPANTS_REQUEST) {
			if (resultCode == RESULT_OK) {
				// TODO: A partecipant was picked. Send a private message.
			}
		}
	}
	
	private void sendNotification(String title, String myContext, String sender) {
		mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setSmallIcon(R.drawable.icon);
		mBuilder.setContentTitle(title);
		mBuilder.setContentText(myContext + sender);
			// notificationID allows you to update the notification later on.
		mNotificationManager.notify(Constants.NOTIFICATION_ID, mBuilder.build());
		

	}

	private class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			//Messaging
			String action = intent.getAction();
			logger.log(Level.INFO, "Received intent " + action);
			if (action.equalsIgnoreCase("jade.demo.chat.REFRESH_CHAT")) {
				final TextView chatField = (TextView) findViewById(R.id.chatTextView);
				String additionalInfo = "";
				
				Location location = locationManager
						.getLastKnownLocation(provider);
				if (intent.getExtras().getString("sentence") != null) {
					chatField.append(intent.getExtras().getString("sentence"));
				}
				if (intent.getExtras().getString("chat") != null) {
					if (location != null) {
						additionalInfo += " @Latitude: "
								+ location.getLatitude() + "\n  Longitude "
								+ location.getLongitude() + "\n";

						if (getDistance(location.getLatitude(),
								location.getLongitude(), 35.77198, -78.67385) <= 50)
							additionalInfo += "This is EBII @ Centennial NCSU\n";
						if (getDistance(location.getLatitude(),
								location.getLongitude(), 38.90719, -77.03687) <= 50)
							additionalInfo += "This is Washington DC\n";
						if (getDistance(location.getLatitude(),
								location.getLongitude(), 48.85661, 2.35222) <= 50)
							additionalInfo += "This is Paris\n";
					} else {
						additionalInfo += "Location not found\n";
					}
					
					
					//Obtaining the sender's name
					int loc = intent.getExtras().getString("chat").indexOf(":");
					String sender = intent.getExtras().getString("chat").substring(0, loc);
					
					if (listener.missedCall && !nickname.equals(sender) &&  (intent.getExtras().getString("chat").contains("urgent") ||  intent.getExtras().getString("chat").contains("URGENT"))) {
						String title = "Missed urgent call!";
						String message = "Received urgent call from: ";
						sendNotification(title, message, sender);
						
					} 

					chatField.append(intent.getExtras().getString("chat")
								+ additionalInfo);

				}
				scrollDown();
			}
			if (action.equalsIgnoreCase("jade.demo.chat.CLEAR_CHAT")) {
				final TextView chatField = (TextView) findViewById(R.id.chatTextView);
				chatField.setText("");
			}
		}
	}

	
	private void scrollDown() {
		final ScrollView scroller = (ScrollView) findViewById(R.id.scroller);
		final TextView chatField = (TextView) findViewById(R.id.chatTextView);
		scroller.smoothScrollTo(0, chatField.getBottom());
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		final TextView chatField = (TextView) findViewById(R.id.chatTextView);
		savedInstanceState.putString("chatField", chatField.getText()
				.toString());
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		final TextView chatField = (TextView) findViewById(R.id.chatTextView);
		chatField.setText(savedInstanceState.getString("chatField"));
	}

	private void showAlertDialog(String message, final boolean fatal) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
		builder.setMessage(message).setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						if (fatal)
							finish();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private static double getDistance(double lat1, double lon1, double lat2,
			double lon2) {
		final double Radius = 6371 * 1E3; // Earth's mean radius
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return Radius * c;
	}
}