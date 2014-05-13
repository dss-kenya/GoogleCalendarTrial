package com.example.googlecalendartrial;

import java.io.IOException;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarRequest;
import com.google.api.services.calendar.CalendarRequestInitializer;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	private String AUTH_TOKEN_TYPE = "oauth2:https://www.googleapis.com/auth/calendar";
	private GoogleCredential credential = new GoogleCredential();
	private JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
	private String apiKey = "AIzaSyAw1Ys2vLh152sKyfmbXUEK-aDKyhkwCFQ";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getAccounts();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void getAccounts() {
		AccountManager accountManager = AccountManager.get(this.getBaseContext());
		Account[] accounts = accountManager.getAccountsByType("com.google");
		Account account = accounts[0]; 

		Log.e("dhara","acc : " + account.name + " ");

		accountManager.getAuthToken(account, AUTH_TOKEN_TYPE, null, MainActivity.this, new AccountManagerCallback<Bundle>() {
			public void run(AccountManagerFuture<Bundle> future) {
				try {
					// If the user has authorized your application to use the tasks API
					// a token is available.
					String token = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
					// Now you can use the Tasks API...
					useCalendarAPI(token);
				} catch (OperationCanceledException e) {
					// TODO: The user has denied you access to the API, you should handle that
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, null);
	}

	private void useCalendarAPI(String accessToken) {
		HttpTransport transport = AndroidHttp.newCompatibleTransport();

		// Setting up the Tasks API Service
		/*
		HttpRequestInitializer requestInitializer = new HttpRequestInitializer() {
			public void initialize(HttpRequest request) throws IOException {
				request.getHeaders().setAuthorization(HttpHeaders..getGoogleLoginValue(accessToken));
			}
		};*/
		
		/*service = com.google.api.services.tasks.Tasks.builder(transport, jsonFactory)
		        .setApplicationName("Google-TasksAndroidSample/1.0")
		        .setHttpRequestInitializer(credential)
		        .setJsonHttpRequestInitializer(new GoogleKeyInitializer(ClientCredentials.KEY))
		        .build();*/
		
		Calendar service = new Calendar.Builder(transport, jsonFactory, credential)
			.setApplicationName("GoogleCalendarTrial/1.0")
			.setHttpRequestInitializer(credential)
			.setCalendarRequestInitializer(new CalendarRequestInitializer() {
				@Override
				protected void initializeCalendarRequest(
						CalendarRequest<?> calendarRequest) throws IOException {
					super.initializeCalendarRequest(calendarRequest);
					calendarRequest.setKey(apiKey);
				}
			})
			.build();
		
		/*service.accessKey = "AIzaSyAw1Ys2vLh152sKyfmbXUEK-aDKyhkwCFQ";
		service.setApplicationName("GoogleCalendarTrial");*/
	}
}
