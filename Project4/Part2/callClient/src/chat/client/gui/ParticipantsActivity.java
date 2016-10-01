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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import chat.client.agent.ChatClientInterface;
import jade.core.MicroRuntime;
import jade.util.Logger;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents;
import android.provider.ContactsContract.PhoneLookup;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This activity implement the participants interface.
 * 
 * @author Michele Izzo - Telecomitalia
 */

public class ParticipantsActivity extends ListActivity {
	private Logger logger = Logger.getJADELogger(this.getClass().getName());

	private MyReceiver myReceiver;

	private String nickname;
	private ChatClientInterface chatClientInterface;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			nickname = extras.getString("nickname");
		}

		try {
			chatClientInterface = MicroRuntime.getAgent(nickname)
					.getO2AInterface(ChatClientInterface.class);
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		myReceiver = new MyReceiver();

		IntentFilter refreshParticipantsFilter = new IntentFilter();
		refreshParticipantsFilter
				.addAction("jade.demo.chat.REFRESH_PARTICIPANTS");
		registerReceiver(myReceiver, refreshParticipantsFilter);

		setContentView(R.layout.participants);
		String[] partNames = chatClientInterface.getParticipantNames();
		ArrayList<String> contactNames = new ArrayList<String>();
		Cursor contacts = getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		int nameFieldColumn = contacts.getColumnIndex(PhoneLookup.DISPLAY_NAME);
		while (contacts.moveToNext()) {
			contactNames.add(contacts.getString(nameFieldColumn));
		}
		for (int i = 0;i < partNames.length; i ++) {
			if (contactNames.contains(partNames[i])) {
				partNames[i] += " [Y]";
			} else {
				partNames[i] += " [N]";
			}
		}
		setListAdapter(new ArrayAdapter<String>(this, R.layout.participant,
				partNames));

		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
		listView.setOnItemClickListener(listViewtListener);
	}

	private OnItemClickListener listViewtListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO: A partecipant was picked. Send a private message.
			TextView tv = (TextView) view;
			String name = (String) tv.getText();
			if(name.contains("[N]")){
				name = name.replace(" [N]", "");
				
				Intent intent = new Intent(Intents.Insert.ACTION);
				
				intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
				
				intent.putExtra(Intents.Insert.NAME, name);
				
				startActivity(intent);
			} else if (name.contains("[Y]")) {		
				
				
			/*	
				Intent newintent = new Intent();
				newintent.NotificationCompat.Builder.build()
				newintent.putExtra(Intents.Insert.NAME, name);
*/
			}
			finish();
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unregisterReceiver(myReceiver);

		logger.log(Level.INFO, "Destroy activity!");
	}

	private class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			logger.log(Level.INFO, "Received intent " + action);
			if (action.equalsIgnoreCase("jade.demo.chat.REFRESH_PARTICIPANTS")) {
				setListAdapter(new ArrayAdapter<String>(
						ParticipantsActivity.this, R.layout.participant,
						chatClientInterface.getParticipantNames()));
			}
		}
	}

}
