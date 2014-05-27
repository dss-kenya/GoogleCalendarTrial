package com.dhara.googlecalendartrial;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.Menu;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.Calendar.Events.List;
import com.google.api.services.calendar.CalendarRequest;
import com.google.api.services.calendar.CalendarRequestInitializer;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public class MainActivity extends Activity {
	private String AUTH_TOKEN_TYPE = "oauth2:https://www.googleapis.com/auth/calendar";//?key=AIzaSyCmbZhh76WL_yJ2vBRlwgJGlviMS_a1rOg";
	private GoogleCredential credential;
	private JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
	private String apiKey ="AIzaSyCl8ZaG9KMaRuZYXq8vslpxLsERwk0czHs";  //"AIzaSyCmbZhh76WL_yJ2vBRlwgJGlviMS_a1rOg";
	private AccountManager accountManager;
	private Account account;
	//private GoogleApiClient mGoogleApiClient;
	
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
		accountManager = AccountManager.get(this.getBaseContext());
		Account[] accounts = accountManager.getAccountsByType("com.google");
		account = accounts[0]; 

		Log.e("dhara","acc : " + account.name + " ");

		accountManager.getAuthToken(account, AUTH_TOKEN_TYPE, null, MainActivity.this, new AccountManagerCallback<Bundle>() {
			public void run(AccountManagerFuture<Bundle> future) {
				try {
					// If the user has authorized your application to use the tasks API
					// a token is available.
					String token = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
					// Now you can use the Tasks API...
					useCalendarAPI(token, account.name);
				} catch (OperationCanceledException e) {
					// TODO: The user has denied you access to the API, you should handle that
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, null);
	}
	
	private void setUp( final String userAccount){
		try {
			String clientId = "424045474279-tdm2pud0f32vovicoajj3hul5ot349r7.apps.googleusercontent.com";
		    String clientSecret = "pjCbZO9lwGudNtk9CMKQ7GGx";

		    // Or your redirect URL for web based applications.
		    String redirectUrl = "https://localhost/oauth2callback"; //"urn:ietf:wg:oauth:2.0:oob";
		    String scope = "https://www.googleapis.com/auth/calendar";
		    java.util.List<String> listOfScope = new ArrayList<String>();
		    listOfScope.add(scope);
		    
		    Collection<String> scopes = listOfScope;
		    
		    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();

		    // Step 1: Authorize -->
		    String authorizationUrl = new GoogleAuthorizationCodeRequestUrl(clientId, redirectUrl, scopes)
		        .build();

		    // Point or redirect your user to the authorizationUrl.
		    System.out.println("Go to the following link in your browser:");
		    System.out.println(authorizationUrl);

		    // Read the authorization code from the standard input stream.
		    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		    System.out.println("What is the authorization code?");
		    String code = in.readLine();
		    
		    Log.e("dhara","value of code: " + code);
		    // End of Step 1 <--

		    // Step 2: Exchange -->
		    final GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(httpTransport, jsonFactory,
		        clientId, clientSecret, code, redirectUrl).execute();
		    // End of Step 2 <--

		    /*GoogleAccessProtectedResource accessProtectedResource = new Google(
		        response.getAccessToken(), httpTransport, jsonFactory, clientId, clientSecret,
		        response.getRefreshToken());*/

		    credential = new GoogleCredential().setAccessToken(response.getAccessToken());
		    Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credential)
		    					.setApplicationName("GoogleCalendarTrial")
		    					.setHttpRequestInitializer(credential)
		    					.setCalendarRequestInitializer(new CalendarRequestInitializer() {
		    						@Override
		    						protected void initializeCalendarRequest(
		    								CalendarRequest<?> calendarRequest) throws IOException {
		    							super.initializeCalendarRequest(calendarRequest);
		    							ArrayMap<String, Object> customKeys = new ArrayMap<String, Object>();
		    					        customKeys.put("xoauth_requestor_id",userAccount);
		    					        calendarRequest.setUnknownKeys(customKeys);
		    					        calendarRequest.setOauthToken(response.getAccessToken());
		    							calendarRequest.setKey(apiKey);
		    						}
		    					})
		    					.build();
		    
		    List find = service.events().list("global");
			Log.e("dhara","find : " + find.size());
			
			Events events = find.execute();
			Log.e("dhara","events : " + events.size());
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void useCalendarAPI(final String accessToken, final String userAccount) {
		HttpTransport transport = AndroidHttp.newCompatibleTransport();
		Log.e("dhara","accessToken : " + accessToken);
		credential = new GoogleCredential().setAccessToken(accessToken);
		try {
			//String path = "data/data/com.dhara.googlecalendartrial" + "/private_key";
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/private_key/" + "3ceb5f543e0a13a3bd1028c5a32a89dab9397dbb-privatekey.p12";
			//java.io.File licenseFile = new java.io.File(path);
			//Log.e("dhara","licenseFile.isFile() " + licenseFile.isFile());
			//licenseFile.mkdir();
			//licenseFile.mkdirs();
			
			//if(!licenseFile.mkdirs()) {
			//	Log.e("dhara","file directory could not be created");
				
				//licenseFile.mkdirs();
				//licenseFile.mkdir();
			//}
			
			//licenseFile = new File(path,"3ceb5f543e0a13a3bd1028c5a32a89dab9397dbb-privatekey.p12");
			
			/*credential = new GoogleCredential.Builder()
							.setServiceAccountId("424045474279-dv03cnl1aslne6mpec6tlap5mg4uuei2.apps.googleusercontent.com")
							.setTransport(transport)
							.setJsonFactory(jsonFactory)
							.setServiceAccountPrivateKeyFromP12File(new File(path))
							.setServiceAccountScopes(Collections.singleton(CalendarScopes.CALENDAR))
							.build();*/
			
			credential.setAccessToken(accessToken);
			
			//.setServiceAccountPrivateKeyFromP12File( new java.io.File(path))
			
			/*credential =
			        GoogleCredential.usingOAuth2(this, Collections.singleton(CalendarScopes.CALENDAR));*/
			
			/*mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addApi(com.google.android.gms.common.api..API)
            .addScope(Drive.SCOPE_FILE)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();*/
			
			Calendar service = new Calendar.Builder(transport, jsonFactory, credential)
			.setApplicationName("GoogleCalendarTrial")
			.setHttpRequestInitializer(credential)
			.setCalendarRequestInitializer(new CalendarRequestInitializer() {
				@Override
				protected void initializeCalendarRequest(
						CalendarRequest<?> calendarRequest) throws IOException {
					super.initializeCalendarRequest(calendarRequest);
					ArrayMap<String, Object> customKeys = new ArrayMap<String, Object>();
			        customKeys.put("xoauth_requestor_id", userAccount);
			        calendarRequest.setUnknownKeys(customKeys);
			        calendarRequest.setOauthToken(accessToken);
					calendarRequest.setKey(apiKey);
				}
			})
			.build();
			
			
			List find = service.events().list(userAccount);
			Log.e("dhara","find : " + find.size());
			
			Events events = find.execute();
			Log.e("dhara","events : " + events.size());
			
			java.util.List<Event> eventList = events.getItems();
			
			if(eventList != null) {
				for(int i=0;i<eventList.size();i++) {
					Log.e("dhara","getting event desc : " + eventList.get(i).getDescription());
					Log.e("dhara","getting event id : " + eventList.get(i).getId());
					Log.e("dhara","getting event summary : " + eventList.get(i).getSummary());
					Log.e("dhara","getting event start : " + eventList.get(i).getStart());
				}
			}
			
			
		}catch(IOException e) {
			e.printStackTrace();
			
			Log.e("dhara","exception here");
			e.printStackTrace();
			accountManager.invalidateAuthToken(account.type, accessToken);
			
		}/*catch (GeneralSecurityException e) {
			e.printStackTrace();
		}*/
		
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
		
		/*service.accessKey = "AIzaSyAw1Ys2vLh152sKyfmbXUEK-aDKyhkwCFQ";
		service.setApplicationName("GoogleCalendarTrial");*/
	}
}
