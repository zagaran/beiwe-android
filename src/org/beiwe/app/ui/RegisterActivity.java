package org.beiwe.app.ui;

import org.beiwe.app.DebugInterfaceActivity;
import org.beiwe.app.DeviceInfo;
import org.beiwe.app.R;
import org.beiwe.app.networking.PostRequest;
import org.beiwe.app.networking.HTTPAsync;
import org.beiwe.app.session.LoginSessionManager;
import org.beiwe.app.storage.EncryptionEngine;
import org.beiwe.app.survey.TextFieldKeyboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


/**Activity used to log a user in to the application for the first time. This activity should only be called on ONCE,
 * as once the user is logged in, data is saved on the phone.
 * @author Dor Samet */

@SuppressLint("ShowToast")
public class RegisterActivity extends Activity {

	// Private fields
	private Context appContext;
	private EditText userID;
	private EditText password;
	private EditText passwordRepeat;

	/** Users will go into this activity first to register information on the phone and on the server. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		// onCreate set variables
		appContext = getApplicationContext();
		userID = (EditText) findViewById(R.id.userID_box);
		password = (EditText) findViewById(R.id.password_box);
		passwordRepeat = (EditText) findViewById(R.id.repeat_password_box);

		TextFieldKeyboard textFieldKeyboard = new TextFieldKeyboard(appContext);
		textFieldKeyboard.makeKeyboardBehave(userID);
		textFieldKeyboard.makeKeyboardBehave(password);
		textFieldKeyboard.makeKeyboardBehave(passwordRepeat);
	}

	/**Registration sequence begins here, called when the submit button is pressed.
	 * Normally there would be interaction with the server, in order to verify the user ID as well as the phone ID.
	 * Right now it does simple checks to see that the user actually inserted a value.
	 * @param view */
	@SuppressLint("ShowToast")
	public synchronized void registrationSequence(View view) {
		String userIDStr = userID.getText().toString();
		String passwordStr = password.getText().toString();
		String passwordRepeatStr = passwordRepeat.getText().toString();

		// If the user id length is too short, alert the user
		if(userIDStr.length() == 0) {
			AlertsManager.showAlert(appContext.getResources().getString(R.string.invalid_user_id), this); }

		// If the password length is too short, alert the user
		else if (passwordStr.length() == 0) {
			AlertsManager.showAlert(appContext.getResources().getString(R.string.invalid_password), this);}

		// If the repeat password does not match the original password, alert the user
		else if ( !passwordRepeatStr.equals(passwordStr) ) {
			AlertsManager.showAlert(appContext.getResources().getString(R.string.password_mismatch), this);	}

		// TODO: Eli. make sure this doesn't fail due to password restrictions conflicting with a randomly generated password.
		// Otherwise, start the registration process against the user
		else {
			LoginSessionManager.setLoginCredentialsAndLogIn(userIDStr, EncryptionEngine.safeHash(passwordStr));
			
			Log.i("RegisterActivity", "trying \"" + LoginSessionManager.getPatientID() + "\" with password \"" + LoginSessionManager.getPassword() + "\"" );
			
			doRegister("http://beiwe.org/register_user");
		}
	}
	
	
	//Aww yeuh. 
	private void doRegister(final String url) { new HTTPAsync(url, this) {
		@Override
		protected Void doInBackground(Void... arg0) {
			parameters = PostRequest.makeParameter("bluetooth_id", DeviceInfo.getBlootoothMAC() );
			response = PostRequest.asyncRegisterHandler(parameters, url);
			return null; //hate
		}
		
		@Override
		protected void onPostExecute(Void arg) {
			if (response == 200) { 
				LoginSessionManager.setRegistered(true);
				//TODO: postproduction. Change to point at regular activity.
				activity.startActivity(new Intent(activity.getApplicationContext(), DebugInterfaceActivity.class));
				activity.finish();
			}
			super.onPostExecute(arg);
		}
	};}
}