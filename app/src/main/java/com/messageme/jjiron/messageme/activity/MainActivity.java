package com.messageme.jjiron.messageme.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.login.LoginManager;
import com.messageme.jjiron.messageme.firebase.FirebaseManager;
import com.messageme.jjiron.messageme.R;
import com.messageme.jjiron.messageme.service.FirebaseSyncService;


public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";
    private static final int ALL_PERMISSIONS_REQUEST_ID = 4;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        checkAllPermissions();
    }


	private final View.OnClickListener logoutButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			LoginManager.getInstance().logOut();
			// TODO also logout of firebase
		}
	};

    private void checkAllPermissions() {
        requestPermissions(new String[]{
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECEIVE_SMS,
        }, ALL_PERMISSIONS_REQUEST_ID);
    }

    private void onAllPermissionsGranted() {
        Log.d(TAG, "onAllPermissionsGranted");
        Intent startIntent = new Intent(MainActivity.this, FirebaseSyncService.class);
        startIntent.setAction(FirebaseSyncService.START_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(startIntent);
        } else {
            startService(startIntent);
        }
    }

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
		// Make sure it's our original READ_CONTACTS request
		if (requestCode == ALL_PERMISSIONS_REQUEST_ID) {
		    if (allSame(grantResults, PackageManager.PERMISSION_GRANTED)) {
                onAllPermissionsGranted();
            } else {
		        Log.d(TAG, "permissions not granted " + grantResults.length);
            }
        } else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

    private static boolean allSame(int[] arr, int val) {
        for (int i : arr) {
            if (val != i) {
                return false;
            }
        }
        return true;
    }
}