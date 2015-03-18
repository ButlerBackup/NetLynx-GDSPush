package com.nextlynxtech.gdspushnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.nextlynxtech.gdspushnotification.classes.Timeline;
import com.nextlynxtech.gdspushnotification.classes.Utils;
import com.nextlynxtech.gdspushnotification.services.UploadPhotoService;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class NewTimelineItemPhotoActivity extends ActionBarActivity {
    @InjectView(R.id.ivNewTimelineImage)
    ImageView ivNewTimelineImage;

    @InjectView(R.id.etDescription)
    EditText etDescription;

    @InjectView(R.id.tvGetLocation)
    TextView tvGetLocation;

    @InjectView(R.id.bRefreshLocation)
    Button bRefreshLocation;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    String pictureFileName = "";

    File imgFile;
    Bitmap croppedImage;
    LocationInfo currentLocation;
    boolean isResendingPhoto = false;
    String locationName = "";
    Timeline timelineResent;

    //When activity is called
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item_timeline);
        ButterKnife.inject(NewTimelineItemPhotoActivity.this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //Copy image and load after taking
        if (getIntent().hasExtra(Consts.IMAGE_CAMERA_PASS_EXTRAS)) {
            pictureFileName = getIntent().getStringExtra(Consts.IMAGE_CAMERA_PASS_EXTRAS);
            imgFile = new File(new Utils(NewTimelineItemPhotoActivity.this).createFolder(), pictureFileName);
            Log.e("FILENAME", imgFile.getAbsolutePath().toString());
            loadImageFile();
            //Get filepath from gallery and copy the exact image duplicating it
        } else if (getIntent().hasExtra(Consts.IMAGE_GALLERY_PASS_EXTRAS)) {
            String currentTime = System.currentTimeMillis() + "";
            pictureFileName = getIntent().getStringExtra(Consts.IMAGE_GALLERY_PASS_EXTRAS);
            Uri uriPath = Uri.parse(pictureFileName);
            File tempFile = new File(uriPath.getPath());
            // Toast.makeText(NewTimelineItemPhotoActivity.this, tempFile.getAbsolutePath().toString(), Toast.LENGTH_LONG).show();
            Log.e("FILENAME", uriPath.getPath());
            File destination = new File(new Utils(NewTimelineItemPhotoActivity.this).createFolder(), currentTime + ".jpg");
            try {
                FileUtils.copyFile(tempFile, destination);
                imgFile = new File(new Utils(NewTimelineItemPhotoActivity.this).createFolder(), currentTime + ".jpg");
                //tempFile.delete();
                loadImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(NewTimelineItemPhotoActivity.this, "Failed to copy file to GDSFolder", Toast.LENGTH_SHORT).show();
            }
            //Gets image from resend
        } else if (getIntent().hasExtra(Consts.TIMELINE_ITEM_SELECTED_FROM_MAINACTIVITY)) {
            timelineResent = (Timeline) getIntent().getSerializableExtra(Consts.TIMELINE_ITEM_SELECTED_FROM_MAINACTIVITY);
            pictureFileName = timelineResent.getImage();
            imgFile = new File(new Utils(NewTimelineItemPhotoActivity.this).createFolder(), pictureFileName);
            isResendingPhoto = true;
            loadImageFile();
        } else {
            finish();
        }
        //If file is not resend, gets location
        if (!isResendingPhoto) {
            tvGetLocation.setText(Consts.LOCATION_LOADING);
            LocationLibrary.forceLocationUpdate(NewTimelineItemPhotoActivity.this);
            refreshLocation(new LocationInfo(NewTimelineItemPhotoActivity.this));
            final IntentFilter lftIntentFilter = new IntentFilter(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction());
            registerReceiver(lftBroadcastReceiver, lftIntentFilter);
        } else {
            etDescription.setText(timelineResent.getMessage()); //Resend text from timeline
            //Log.e("HERE", timelineResent.getLocation().toString());
            //Toast.makeText(NewTimelineItemPhotoActivity.this, timelineResent.getLocation().toString(), Toast.LENGTH_LONG).show();
            //If there is no location previously, it will attempt to get location again
            if (timelineResent.getLocation() != null && timelineResent.getLocation().length() > 0) {
                locationName = timelineResent.getLocation();
                tvGetLocation.setText(locationName);
            } else {
                tvGetLocation.setText(Consts.LOCATION_ERROR);
            }
            //Display location
            if (timelineResent.getLocationLat() != null && timelineResent.getLocationLat().length() > 0 && timelineResent.getLocationLong() != null && timelineResent.getLocationLong().length() > 0) {
                if (currentLocation == null) {
                    currentLocation = new LocationInfo(NewTimelineItemPhotoActivity.this);
                }
                currentLocation.lastLat = Float.parseFloat(timelineResent.getLocationLat());
                currentLocation.lastLong = Float.parseFloat(timelineResent.getLocationLong());
            }
        }
    }

    //Display image file
    private void loadImageFile() {
        if (imgFile.exists()) {
            Log.e("File Size", imgFile.length() + "");
            Log.e("File Directory", imgFile.getAbsolutePath().toString());
            // Toast.makeText(NewTimelineItemPhotoActivity.this, imgFile.getAbsolutePath().toString() + "\n" + imgFile.getName().toString(), Toast.LENGTH_LONG).show();
            //croppedImage = Utils.decodeSampledBitmapFromResource(imgFile);
            croppedImage = new Utils(NewTimelineItemPhotoActivity.this).createResizeBitmap(imgFile);
            ivNewTimelineImage.setImageBitmap(croppedImage);
        } else {
            Toast.makeText(NewTimelineItemPhotoActivity.this, "No image found", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    //Get location
    @OnClick(R.id.tvGetLocation)
    public void refreshLocation() {
        Log.e("Refreshing", "Refreshing location");
        LocationLibrary.forceLocationUpdate(NewTimelineItemPhotoActivity.this);
        refreshLocation(new LocationInfo(NewTimelineItemPhotoActivity.this));
        final IntentFilter lftIntentFilter = new IntentFilter(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction());
        registerReceiver(lftBroadcastReceiver, lftIntentFilter);
    }

    //Refresh location
    @OnClick(R.id.bRefreshLocation)
    public void refresh() {
        Log.e("Refreshing", "Refreshing location");
        LocationLibrary.forceLocationUpdate(NewTimelineItemPhotoActivity.this);
        refreshLocation(new LocationInfo(NewTimelineItemPhotoActivity.this));
        final IntentFilter lftIntentFilter = new IntentFilter(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction());
        registerReceiver(lftBroadcastReceiver, lftIntentFilter);
    }

    //Gets location info
    private final BroadcastReceiver lftBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final LocationInfo locationInfo = (LocationInfo) intent.getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
            refreshLocation(locationInfo);
        }
    };

    //Gets location in string text
    private void refreshLocation(final LocationInfo locationInfo) {
        if (locationInfo.anyLocationDataReceived()) {
            //tvGetLocation.setText(locationInfo.lastLat + ", " + locationInfo.lastLong);
            currentLocation = locationInfo;
            if (locationInfo.hasLatestDataBeenBroadcast()) {
                Log.e("UPDATE", "Latest location has been broadcast");
                new getLocationPlaceName().execute();
            } else {
                tvGetLocation.setText(Consts.LOCATION_ERROR);
                // tvGetLocation.setText("Waiting for location.. (last " + LocationInfo.formatTimeAndDay(locationInfo.lastLocationUpdateTimestamp, true) + ")");
            }
        } else {
            tvGetLocation.setText(Consts.LOCATION_ERROR);
        }
    }

    //Upload button on action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_timeline_item_activity, menu);
        menu.findItem(R.id.mUpload).setIcon(new IconDrawable(this, Iconify.IconValue.md_file_upload)
                .colorRes(R.color.white)
                .actionBarSize());
        return true;
    }

    //When upload button is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mUpload) {
            //Description text limit
            if (etDescription.getText().toString().length() > 400) {
                Toast.makeText(NewTimelineItemPhotoActivity.this, "Description is more than 400 characters", Toast.LENGTH_LONG).show();
            } else { //Carry on upload
                Intent i = new Intent(NewTimelineItemPhotoActivity.this, UploadPhotoService.class);
                //Trimming of text
                if (etDescription.getText().toString() != null && etDescription.getText().toString().trim().length() > 0) {
                    i.putExtra("message", etDescription.getText().toString().trim());
                } else {
                    i.putExtra("message", "");
                }
                //Location name, lat, long attached
                i.putExtra("locationName", locationName);
                if (currentLocation != null) {
                    i.putExtra("locationLat", Float.toString(currentLocation.lastLat));
                    i.putExtra("locationLong", Float.toString(currentLocation.lastLong));
                } else {
                    i.putExtra("locationLat", "");
                    i.putExtra("locationLong", "");
                }
                i.putExtra("file", imgFile.getAbsoluteFile().toString());
                //Creates a new item on the timeline
                if (timelineResent != null && timelineResent.getSuccess() != null && timelineResent.getSuccess().equals("0")) {//ofailed,1success,2uploading
                    i.putExtra("failedResend", true);
                    i.putExtra("id", timelineResent.getId());
                }
                Toast.makeText(NewTimelineItemPhotoActivity.this, "Photo will be processed in the background. You will be notified of any changes", Toast.LENGTH_LONG).show();
                // startService(i);
                WakefulIntentService.sendWakefulWork(NewTimelineItemPhotoActivity.this, i);
                finish();
                /*mTask = null;
                mTask = new uploadImage();
                mTask.execute();*/
            }
        } else if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //Gets location in string text class
    private class getLocationPlaceName extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            NewTimelineItemPhotoActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvGetLocation.setText(locationName);
                }
            });
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //Pass in lat long to get text address
                if (currentLocation != null) {
                    Geocoder geocoder = new Geocoder(NewTimelineItemPhotoActivity.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(currentLocation.lastLat, currentLocation.lastLong, 1);
                    if (addresses != null && addresses.size() > 0) {
                        String cityName = addresses.get(0).getAddressLine(0);
                        String stateName = addresses.get(0).getAddressLine(1);
                        String countryName = addresses.get(0).getCountryName();
                        locationName = cityName + " " + stateName + " " + countryName;
                        Log.e("Location", locationName);
                    } else {
                        locationName = currentLocation.lastLat + ", " + currentLocation.lastLong;
                    }
                } else {
                    locationName = Consts.LOCATION_ERROR;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(lftBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class tempCompressImageToShow extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }
}
