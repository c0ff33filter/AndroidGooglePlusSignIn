
import java.io.IOException;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;

@SuppressLint("NewApi")
public class GoogleFragment extends Fragment implements ConnectionCallbacks,
		OnConnectionFailedListener {
	
	public static final int RC_SIGN_IN = 9009;
	public static final int RC_REQUEST_AUTHORIZATION = 9010;

	private GoogleApiClient mGoogleApiClient;
	private ConnectionResult mConnectionResult;
	private String mToken;
	
	@SuppressLint("NewApi")
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(activity)
					.addApi(Plus.API).addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addScope(Plus.SCOPE_PLUS_PROFILE)
					.addScope(Plus.SCOPE_PLUS_LOGIN).build();
		}

	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		this.setRetainInstance(true);
		if (mConnectionResult == null) {
			mGoogleApiClient.connect();
		}
	}

	@SuppressLint("NewApi")
	public void onDestroy() {
		mGoogleApiClient.disconnect();
		super.onDestroy();
	}

	@SuppressLint("NewApi")
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		mConnectionResult = result;
		if (result.getErrorCode() == ConnectionResult.SIGN_IN_REQUIRED) {
			try {
				result.startResolutionForResult(this.getActivity(), RC_SIGN_IN);
			} catch (SendIntentException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int responseCode,Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			if (responseCode != Activity.RESULT_OK) {
				mGoogleApiClient.disconnect();
			}

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}else if(requestCode == RC_REQUEST_AUTHORIZATION) {
			fetchToken();
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		fetchToken();
	}

	@Override
	public void onConnectionSuspended(int cause) {
	}

	@SuppressLint("NewApi")
	public void fetchToken() {
		new AsyncTask<Void, Void, Void>() {
			
			@Override
			protected Void doInBackground(Void... params) {
				try {
					String scopes = "oauth2:"
							+ Scopes.PLUS_LOGIN
							+ " "
							+ Scopes.PROFILE;
							
					mToken = GoogleAuthUtil.getToken(getActivity(),
							Plus.AccountApi.getAccountName(mGoogleApiClient),
							scopes);
				} catch (UserRecoverableAuthException e) {
					getActivity().startActivityForResult(e.getIntent(),
					 RC_REQUEST_AUTHORIZATION);
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (GoogleAuthException e) {
					e.printStackTrace();
				}
				return null;
			}
		}.execute();
	}

}
