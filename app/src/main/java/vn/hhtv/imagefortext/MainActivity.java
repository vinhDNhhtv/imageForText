package vn.hhtv.imagefortext;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import vn.hhtv.imagefortext.animation.MoveBottomTopAnimation;
import vn.hhtv.imagefortext.animation.MoveLeftRightAnimation;
import vn.hhtv.imagefortext.animation.MovePositionAnimation;
import vn.hhtv.imagefortext.api.RestClient;
import vn.hhtv.imagefortext.config.ApiConfig;
import vn.hhtv.imagefortext.config.Constants;
import vn.hhtv.imagefortext.dialogs.SelectionFontDialog;
import vn.hhtv.imagefortext.fragments.ImageFragment;
import vn.hhtv.imagefortext.models.Image;
import vn.hhtv.imagefortext.service.FetchAddressIntentService;
import vn.hhtv.imagefortext.utils.ToastUtil;
import vn.hhtv.imagefortext.widget.AutoResizeEditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    protected static final String TAG = "main-activity";

    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     * The user requests an address by pressing the Fetch Address button. This may happen
     * before GoogleApiClient connects. This activity uses this boolean to keep track of the
     * user's intent. If the value is true, the activity tries to fetch the address as soon as
     * GoogleApiClient connects.
     */
    protected boolean mAddressRequested;

    /**
     * The formatted location address.
     */
    protected String mAddressOutput;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;

    private AutoResizeEditText mContentEdt;
    private ViewPager mViewPager;
    private Handler mHandler;
    private RelativeLayout mRootView, mHomeRL;
    private String mContent = "";
    private Bitmap bitmap;
    public static int timeMove = 200;
    public static float distanceMove = 430f;
    public static float density = 1f;
    private int timeDelay = 2 * 1000; // 1second
    private Runnable runCheck = new Runnable() {
        @Override
        public void run() {
            checkContent();
//            mHandler.postDelayed(this, timeDelay);
        }
    };
    private List<Image> images;
    public static String screenResolution = "720x1280";
    private ImagePageAdapter mPageAdapter;

    private ImageView mGridBtn, mFbBtn, mInsBtn, mTwBtn, mSettingBtn;
    private LinearLayout mFbRl, mInsRl, mTwRl, mSettingRl;
    private TextView mAddressTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        density = getResources().getDisplayMetrics().density;
        distanceMove = density * 100;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        int width = 0;
        int height = 0;
        try {
            if(Build.VERSION.SDK_INT >= 13) {
                display.getSize(size);
                width = size.x;
                height = size.y;
            }
        } catch (NoSuchMethodError e) {
            width = display.getWidth();
            height = display.getHeight();
        }
        if(width > 0 && height > 0)
        screenResolution = width + "x" + height;
        mHandler = new Handler();
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mContentEdt = (AutoResizeEditText) findViewById(R.id.editText);
        mRootView = (RelativeLayout) findViewById(R.id.rootView);
        mHomeRL = (RelativeLayout) findViewById(R.id.homeRL);
        mAddressTv = (TextView) findViewById(R.id.addressTv);
        Picasso.with(this).load("file:///android_asset/bg/bg.jpg").into(new Target(){

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mHomeRL.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(final Drawable errorDrawable) {
                Log.d("TAG", "FAILED");
            }

            @Override
            public void onPrepareLoad(final Drawable placeHolderDrawable) {
                Log.d("TAG", "Prepare Load");
            }
        });
        mGridBtn = (ImageView) findViewById(R.id.grid);
        mGridBtn.setOnClickListener(this);
        mFbBtn = (ImageView) findViewById(R.id.fb);
        mInsBtn = (ImageView) findViewById(R.id.inta);
        mTwBtn = (ImageView) findViewById(R.id.tw);
        mSettingBtn = (ImageView) findViewById(R.id.setting);
        mFbRl = (LinearLayout) findViewById(R.id.fb_rl);
        mFbRl.setOnClickListener(this);
        mInsRl = (LinearLayout) findViewById(R.id.inta_rl);
        mInsRl.setOnClickListener(this);
        mTwRl = (LinearLayout) findViewById(R.id.tw_rl);
        mTwRl.setOnClickListener(this);
        mSettingRl = (LinearLayout) findViewById(R.id.setting_rl);
        mSettingRl.setOnClickListener(this);
        mRootView.setDrawingCacheEnabled(false);
        mContentEdt.addTextChangedListener(contentChange);
        mContentEdt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mViewPager.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_MOVE)
                    return true;
                return false;
            }
        });
        mContentEdt.setHeightLimit(500);
        mViewPager.setOffscreenPageLimit(4);
        mContentEdt.setSelected(false);
        if (Build.VERSION.SDK_INT > 10)
            mContentEdt.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public void onDestroyActionMode(ActionMode mode) {
                }

                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }
            });
        mPageAdapter = new ImagePageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPageAdapter);
        mResultReceiver = new AddressResultReceiver(new Handler());
        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        updateValuesFromBundle(savedInstanceState);

        buildGoogleApiClient();
    }

    private void capture() {
        mContentEdt.setCursorVisible(false);
        try {
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            mRootView.setDrawingCacheEnabled(true);
            File sdcard = Environment.getExternalStorageDirectory();
            File f = new File(sdcard, "temp.jpg");
            FileOutputStream out = null;
            out = new FileOutputStream(f);
            mRootView.setDrawingCacheEnabled(true);
            bitmap = mRootView.getDrawingCache(true).copy(
                    Bitmap.Config.ARGB_8888, false);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
            mRootView.setDrawingCacheEnabled(false);
            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f)); // imageUri
            sharingIntent.setType("image/jpg");

            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f)); // imageUri
            startActivity(Intent.createChooser(sharingIntent, "Share Image"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mContentEdt.setCursorVisible(true);
    }

    private void checkContent() {
        String text = mContentEdt.getText().toString();
        if (mContent.equals(text)) {
            return;
        }
        mContent = text;
        images = null;
        mPageAdapter.notifyDataSetChanged();
        mViewPager.setAdapter(mPageAdapter);
        RestClient.search(mContent, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try{
                    Type type = new TypeToken<List<Image>>() {
                    }.getType();
                    images = new GsonBuilder().create().fromJson(responseString,type);
                }catch (Exception e){
                    images = new ArrayList<Image>();
                }
                mPageAdapter.notifyDataSetChanged();
                mViewPager.setAdapter(mPageAdapter);
            }
        });
    }

    private TextWatcher contentChange = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mHandler.removeCallbacks(runCheck);
            if (count > 0)
                mHandler.postDelayed(runCheck, timeDelay);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private boolean isExpand = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.grid:
                mFbRl.startAnimation(new MoveLeftRightAnimation(mFbRl, isExpand, null));
                mSettingRl.startAnimation(new MoveBottomTopAnimation(mSettingRl, isExpand, null));
                mInsRl.startAnimation(new MovePositionAnimation(mInsRl, 6f, isExpand, null));
                mTwRl.startAnimation(new MovePositionAnimation(mTwRl, 3f, isExpand, null));
                isExpand = !isExpand;
                break;
            case R.id.fb_rl:
                v.startAnimation(scaleClick());
                onClick(mGridBtn);
                capture();
                break;
            case R.id.tw_rl:
                v.startAnimation(scaleClick());
                onClick(mGridBtn);
                break;
            case R.id.inta_rl:
                v.startAnimation(scaleClick());
                onClick(mGridBtn);
                break;
            case R.id.setting_rl:
                v.startAnimation(scaleClick());
                onClick(mGridBtn);
                SelectionFontDialog dialog = SelectionFontDialog.newInstance(new SelectionFontDialog.SelectedFontListener() {
                    @Override
                    public void onSelected(int p, String font) {
                        Typeface type = Typeface.createFromAsset(MainActivity.this.getAssets(), "fonts/" + font);
                        mContentEdt.setTypeface(type);
                    }
                });
                dialog.show(getSupportFragmentManager(), "SelectionFont");
                break;

        }
    }

    private static Animation moveLeftToRight(boolean reverse) {
        AnimationSet animationSet = new AnimationSet(true);
        Animation moveLefttoRight = new TranslateAnimation(reverse ? -distanceMove : 0, reverse ? 0 : -distanceMove, 0, 0);
        moveLefttoRight.setDuration(timeMove);
        moveLefttoRight.setFillAfter(true);
        Animation aSc = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        if (reverse) {
            animationSet.addAnimation(new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        }
        aSc.setDuration(timeMove);
        aSc.setFillAfter(true);
        animationSet.addAnimation(aSc);
        animationSet.addAnimation(moveLefttoRight);
        Animation alp = new AlphaAnimation(reverse ? 1.0f : 0.0f, reverse ? 0.0f : 1.0f);
        alp.setDuration(timeMove);
        alp.setFillAfter(true);
        animationSet.addAnimation(alp);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    private static Animation scaleClick() {
        AnimationSet animationSet = new AnimationSet(true);
        Animation aSc = new ScaleAnimation(1.0f, 2.0f, 1.0f, 2.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        aSc.setDuration(timeMove);
        aSc.setFillAfter(false);
        animationSet.addAnimation(aSc);
        animationSet.setFillAfter(false);
        return animationSet;
    }

    private static Animation moveBottomToTop(boolean reverse) {
        AnimationSet animationSet = new AnimationSet(true);
        Animation moveLefttoRight = new TranslateAnimation(0, 0, reverse ? -distanceMove : 0, reverse ? 0 : -distanceMove);
        moveLefttoRight.setDuration(timeMove);
        moveLefttoRight.setFillAfter(false);
        Animation aSc = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        if (reverse) {
            animationSet.addAnimation(new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        }
        aSc.setDuration(timeMove);
        aSc.setFillAfter(true);
        animationSet.addAnimation(aSc);
        animationSet.addAnimation(moveLefttoRight);
        Animation alp = new AlphaAnimation(reverse ? 1.0f : 0.0f, reverse ? 0.0f : 1.0f);
        alp.setDuration(timeMove);
        alp.setFillAfter(true);
        animationSet.addAnimation(alp);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    private static Animation moveToPosition(float p, boolean reverse) {
        AnimationSet animationSet = new AnimationSet(true);
        Animation moveLefttoRight = new TranslateAnimation(0f, -(float) (distanceMove * Math.cos(Math.PI / p)), 0f, -(float) (distanceMove * Math.sin(Math.PI / p)));
        if (reverse) {
            moveLefttoRight = new TranslateAnimation(-(float) (distanceMove * Math.cos(Math.PI / p)), 0f, -(float) (distanceMove * Math.sin(Math.PI / p)), 0f);
        }
        moveLefttoRight.setDuration(timeMove);
        moveLefttoRight.setFillAfter(true);
        Animation aSc = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        if (reverse) {
            animationSet.addAnimation(new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        }
        aSc.setDuration(timeMove);
        aSc.setFillAfter(true);
        animationSet.addAnimation(moveLefttoRight);
        animationSet.addAnimation(aSc);
        Animation alp = new AlphaAnimation(reverse ? 1.0f : 0.0f, reverse ? 0.0f : 1.0f);
        alp.setDuration(timeMove);
        alp.setFillAfter(true);
        animationSet.addAnimation(alp);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    /**
     * Updates fields based on data stored in the bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }
        }
    }

    private void displayAddressOutput(){
        mAddressTv.setText(mAddressOutput);
    }

    /**
     * Builds a GoogleApiClient. Uses {@code #addApi} to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Runs when user clicks the Fetch Address button. Starts the service to fetch the address if
     * GoogleApiClient is connected.
     */
    public void fetchAddressButtonHandler(View view) {
        // We only start the service to fetch the address if GoogleApiClient is connected.
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
        }
        // If GoogleApiClient isn't connected, we process the user's request by setting
        // mAddressRequested to true. Later, when GoogleApiClient connects, we launch the service to
        // fetch the address. As far as the user is concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                return;
            }
            // It is possible that the user presses the button to get the address before the
            // GoogleApiClient object successfully connects. In such a case, mAddressRequested
            // is set to true, but no attempt is made to fetch the address (see
            // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
            // user has requested an address, since we now have a connection to GoogleApiClient.
            if (mAddressRequested) {
                startIntentService();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }


    private class ImagePageAdapter extends FragmentStatePagerAdapter {

        public ImagePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            if(images == null) return 0;
            return images.size();
        }


        @Override
        public Fragment getItem(int position) {
            return ImageFragment.getInstance(position, images.get(position));
        }
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                ToastUtil.show(getString(R.string.address_found));
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
        }
    }
}
