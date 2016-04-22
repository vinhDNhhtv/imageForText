package vn.hhtv.imagefortext.main;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import vn.hhtv.imagefortext.R;
import vn.hhtv.imagefortext.animation.MoveAlphaAnimation;
import vn.hhtv.imagefortext.animation.MoveBottomTopAnimation;
import vn.hhtv.imagefortext.animation.MoveLeftRightAnimation;
import vn.hhtv.imagefortext.animation.MovePositionAnimation;
import vn.hhtv.imagefortext.api.RestClient;
import vn.hhtv.imagefortext.config.Constants;
import vn.hhtv.imagefortext.dialogs.SelectionFontDialog;
import vn.hhtv.imagefortext.fragments.ImageFragment;
import vn.hhtv.imagefortext.models.Image;
import vn.hhtv.imagefortext.service.FetchAddressIntentService;
import vn.hhtv.imagefortext.utils.ImageUtil;
import vn.hhtv.imagefortext.utils.KeyboardUtil;
import vn.hhtv.imagefortext.utils.ToastUtil;
import vn.hhtv.imagefortext.widget.AutoResizeEditText;
import vn.hhtv.imagefortext.widget.DrawImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

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
    private FrameLayout mContainer;
    private LinearLayout.LayoutParams mCLayoutParams;
    private LinearLayout mTextAroundLl;
    private LinearLayout mShareLl;
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
    public static String screenImage = "720/1280/";
    private ImagePageAdapter mPageAdapter;
    private RequestHandle requestHandle;

    private ImageView mGridBtn, mLetStartIv;
    private LinearLayout mFbRl, mInsRl, mTwRl, mSettingRl;
    private TextView mAddressTv;
    private LinearLayout mSearchLl;
    private EditText mContentSearchEdt;
    private View leftEdt, rightEdt;
    private int stateCrop = 0;   // 0 = Square, 1 = Horizontal rectangle, 2 = Vertical rectangle
    private int textTypeIndex = -1;
    int width = 0;
    int height = 0;
    private boolean isUserChangeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        density = getResources().getDisplayMetrics().density;
        distanceMove = density * 100;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        getPermistion();
        try {
            if (Build.VERSION.SDK_INT >= 13) {
                display.getSize(size);
                width = size.x;
                height = size.y;
            }
        } catch (NoSuchMethodError e) {
            width = display.getWidth();
            height = display.getHeight();
        }
        if (width <= 0) width = 720;
        if (height <= 0) height = 1280;
        if (width > 0 && height > 0) {
            screenResolution = width + "x" + height;
            screenImage = width + "/" + height + "/";
        }
        mHandler = new Handler();
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mContentEdt = (AutoResizeEditText) findViewById(R.id.editText);
        mRootView = (RelativeLayout) findViewById(R.id.rootView);
        mHomeRL = (RelativeLayout) findViewById(R.id.homeRL);
        mContainer = (FrameLayout) findViewById(R.id.container);
        mTextAroundLl = (LinearLayout) findViewById(R.id.textAround);
        mCLayoutParams = (LinearLayout.LayoutParams) mContainer.getLayoutParams();
        mAddressTv = (TextView) findViewById(R.id.addressTv);
        mGridBtn = (ImageView) findViewById(R.id.grid);
        mGridBtn.setOnClickListener(this);
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
        mLetStartIv = (ImageView) findViewById(R.id.letstart_iv);
        mLetStartIv.startAnimation(new MoveAlphaAnimation(mLetStartIv, null));
        mSearchLl = (LinearLayout) findViewById(R.id.search_ll);
        mShareLl = (LinearLayout) findViewById(R.id.share_ll);
        mRootView.setDrawingCacheEnabled(false);
        mContentSearchEdt = (EditText) findViewById(R.id.editText_search);
        leftEdt = findViewById(R.id.left_edt);
        rightEdt = findViewById(R.id.right_edt);
        mContentEdt.addTextChangedListener(contentChange);
        mContentEdt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                mViewPager.onTouchEvent(event);

//                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
//
//                switch (event.getAction())
//                {
//                    case MotionEvent.ACTION_MOVE:
//                        params.topMargin = (int) event.getRawY() - view.getHeight();
//                        params.leftMargin = (int) event.getRawX() - (view.getWidth() / 2);
//                        view.setLayoutParams(params);
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        params.topMargin = (int) event.getRawY() - view.getHeight();
//                        params.leftMargin = (int) event.getRawX() - (view.getWidth() / 2);
//                        view.setLayoutParams(params);
//                        break;
//
//                    case MotionEvent.ACTION_DOWN:
//                        view.setLayoutParams(params);
//                        break;
//                }

                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true;
                }
                if (mLetStartIv.getVisibility() == View.VISIBLE)
                    mLetStartIv.setVisibility(View.GONE);
                return false;
            }
        });
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("touch", "viewpg");
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    KeyboardUtil.hideSoftKeyboard(MainActivity.this);
                }
                if (mLetStartIv.getVisibility() == View.VISIBLE)
                    mLetStartIv.setVisibility(View.GONE);
                return false;
            }
        });
//        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                FragmentManager fm = getSupportFragmentManager();
//                Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
//                if (page instanceof ImageFragment) {
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
        mRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("touch", "root" + event.getAction());
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyboardUtil.hideSoftKeyboard(MainActivity.this);
                }
                if (mLetStartIv.getVisibility() == View.VISIBLE)
                    mLetStartIv.setVisibility(View.GONE);
                return false;
            }
        });
        mHomeRL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("touch", "home" + event.getAction());
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyboardUtil.hideSoftKeyboard(MainActivity.this);
                }
                if (mLetStartIv.getVisibility() == View.VISIBLE)
                    mLetStartIv.setVisibility(View.GONE);
                return false;
            }
        });
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setClipToPadding(false);
        mViewPager.setPageMargin(20);
        mViewPager.setPadding(30, 0, 30, 0);
        mViewPager.setVisibility(View.GONE);
        mContentEdt.setSelected(false);
        mContentEdt.setHeightLimit(height / 4);
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
        mViewPager.setOnPageChangeListener(mPageAdapter);
        mResultReceiver = new AddressResultReceiver(new Handler());
        mAddressRequested = false;
        updateValuesFromBundle(savedInstanceState);

        buildGoogleApiClient();
        findViewById(R.id.crop_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams lflp = leftEdt.getLayoutParams();
                ViewGroup.LayoutParams lrlp = rightEdt.getLayoutParams();
                stateCrop += 1;
                if (stateCrop == 3) stateCrop = 0;
                if (stateCrop == 2) {
                    mViewPager.setPadding(30 * 2, 30, 30 * 2, 30);
//                    mCLayoutParams.height = (int) (mViewPager.getHeight() * density * 9 / 10);
//                    mContainer.setLayoutParams(mCLayoutParams);
                    mContentEdt.setHeightLimit(height / 4);
                    lflp.width = (int) (20 * density);
                    lrlp.width = (int) (20 * density);
                } else {
                    mViewPager.setPadding(30, 0, 30, 0);
                    if (stateCrop == 1) {
//                        mCLayoutParams.height = (int) (width * density * 8 / 16);
//                        mContainer.setLayoutParams(mCLayoutParams);
                        mContentEdt.setHeightLimit(width * 9 * 3 / 16 / 4);
                    } else {
//                        mCLayoutParams.height = (int) (width * density * 9 / 10);
//                        mContainer.setLayoutParams(mCLayoutParams);
                        mContentEdt.setHeightLimit(height / 4);
                    }
                    lflp.width = (int) (10 * density);
                    lrlp.width = (int) (10 * density);
                }
//                mContentEdt.reAdjust();
                leftEdt.setLayoutParams(lflp);
                rightEdt.setLayoutParams(lrlp);
                if (mPageAdapter != null)
                    mPageAdapter.notifyDataSetChanged();
                recreateSizeDrawView();
            }
        });

        findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchLl.getVisibility() == View.VISIBLE) {
                    mSearchLl.setVisibility(View.GONE);
                } else {
                    mSearchLl.setVisibility(View.VISIBLE);
                }
            }
        });

        findViewById(R.id.edit_content_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLetStartIv.getVisibility() == View.VISIBLE)
                    mLetStartIv.setVisibility(View.GONE);
                KeyboardUtil.hideSoftKeyboard(MainActivity.this);
                String text = mContentSearchEdt.getText().toString().trim();
                if (TextUtils.isEmpty(text)) return;
                isUserChangeText = true;
                mContentEdt.setText(text);
                mContentEdt.reAdjust();
                mContent = text;
            }
        });

        findViewById(R.id.share_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShareLl.getVisibility() == View.VISIBLE) {
                    mShareLl.setVisibility(View.GONE);
                } else {
                    mShareLl.setVisibility(View.VISIBLE);
                }
            }
        });

        findViewById(R.id.fb_btn).setOnClickListener(this);
        findViewById(R.id.tw_btn).setOnClickListener(this);
        findViewById(R.id.gplus_btn).setOnClickListener(this);
        findViewById(R.id.inst_btn).setOnClickListener(this);
        findViewById(R.id.more_share_btn).setOnClickListener(this);
        findViewById(R.id.character_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = getResources().getStringArray(R.array.list_font).length;
                textTypeIndex++;
                if (textTypeIndex >= length) textTypeIndex = 0;
                String font = getResources().getStringArray(R.array.list_font)[textTypeIndex];
                Typeface type = Typeface.createFromAsset(MainActivity.this.getAssets(), "fonts/" + font);
                mContentEdt.setTypeface(type);
//                mContentEdt.setLineSpacing(0, 1);
                Paint paint = new Paint();
                paint.setTypeface(type); // if custom font use `TypeFace.createFromFile`
                paint.setTextSize(mContentEdt.getTextSize());
                Rect bounds = new Rect();
                paint.getTextBounds(mContentEdt.getText().toString(), 0, mContentEdt.getText().toString().length(), bounds);
//                mContentEdt.setHeight(mContentEdt.getLineHeight());
//                mContentEdt.setGravity(Gravity.BOTTOM);
                Log.d("Text Height", bounds.height() + "  " + mContentEdt.getLineHeight() + "  " + mContentEdt.getHeight() + "  " + font);
                mContentEdt.reAdjust();

            }
        });

        findViewById(R.id.camera_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ChooseImageDialog dialog = new ChooseImageDialog();
                dialog.setClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.take_photo_btn) {
                            dialog.dismiss();
                            dispatchTakePictureIntent();
                        } else {
                            String image = (String) v.getTag();
                            if (image != null) {
                                if (images == null) {
                                    images = new ArrayList<>();
                                }
                                images.add(0, new Image());
                                images.get(0).setSource(image);
                                images.get(0).setIsOffline(true);
                                mPageAdapter.notifyDataSetChanged();
                                mViewPager.setVisibility(View.VISIBLE);
                                mViewPager.setCurrentItem(0);
                                dialog.dismiss();
                            }
                        }
                    }
                });
                dialog.setListImage(ImageUtil.getAllShownImagesPath(MainActivity.this));
                dialog.show(getSupportFragmentManager(), "Choose Image");
            }
        });


        findViewById(R.id.search_content_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtil.hideSoftKeyboard(MainActivity.this);
                String text = mContentSearchEdt.getText().toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    images = null;
                    mPageAdapter.notifyDataSetChanged();
                    mViewPager.setAdapter(mPageAdapter);
                    mViewPager.setVisibility(View.GONE);
                    if (requestHandle != null) requestHandle.cancel(true);
                    requestHandle = RestClient.search(text, responseHandler);
                }
            }
        });
        findViewById(R.id.more_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = mPageAdapter.getCurrentItem();
                if (fragment != null && fragment instanceof ImageFragment) {
//                return;
                    OptionLayoutDialog dialog = new OptionLayoutDialog();
                    Image image = ((ImageFragment) fragment).getImageM();
                    if (image == null || TextUtils.isEmpty(image.getSource())) return;
                    String font = "";
                    if (textTypeIndex >= 0)
                        font = getResources().getStringArray(R.array.list_font)[textTypeIndex];
                    dialog.setClickListener(optionLayoutListener);
                    dialog.setData(image.getSource(), mContent, mContentEdt.getTextSize(), font, 0);
                    dialog.show(getSupportFragmentManager(), "optionlayoutdialog");
                }

            }
        });
        recreateSizeDrawView();
    }

    private int mCurrentPosition = 4;
    private View.OnClickListener optionLayoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            FrameLayout.LayoutParams lapText = (FrameLayout.LayoutParams) mContentEdt.getLayoutParams();
            mContentEdt.setBackgroundColor(Color.parseColor("#90000000"));
            mCurrentPosition = position;
            switch (position) {
                case 0: // Top Left
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    lapText.gravity = Gravity.TOP | Gravity.LEFT;
                    mContentEdt.setGravity(Gravity.LEFT);
                    break;
                case 1: // Top center
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                    lapText.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                    mContentEdt.setGravity(Gravity.CENTER);
                    break;
                case 2: // Top right
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    lapText.gravity = Gravity.TOP | Gravity.RIGHT;
                    mContentEdt.setGravity(Gravity.RIGHT);
                    break;
                case 3: // Center left
//                lapText.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    lapText.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                    mContentEdt.setGravity(Gravity.LEFT);
                    break;
                case 4: // Center center
//                lapText.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    lapText.gravity = Gravity.CENTER;
                    mContentEdt.setGravity(Gravity.CENTER);
                    break;
                case 5: // Center right
//                lapText.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    lapText.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                    mContentEdt.setGravity(Gravity.RIGHT);
                    break;
                case 6: // Bottom Left
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    lapText.gravity = Gravity.BOTTOM | Gravity.LEFT;
                    mContentEdt.setGravity(Gravity.LEFT);
                    break;
                case 7: // Bottom center
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                    lapText.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                    mContentEdt.setGravity(Gravity.CENTER);
                    break;
                case 8: // Bottom right
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    lapText.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                    mContentEdt.setGravity(Gravity.RIGHT);
                    break;
                case 9: // Bottom Left
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    lapText.gravity = Gravity.BOTTOM | Gravity.LEFT;
                    mContentEdt.setBackgroundColor(Color.parseColor("#000000"));
                    mContentEdt.setGravity(Gravity.LEFT);
                    break;
                case 10: // Bottom center
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                    lapText.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                    mContentEdt.setBackgroundColor(Color.parseColor("#000000"));
                    mContentEdt.setGravity(Gravity.CENTER);
                    break;
                case 11: // Bottom right
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    lapText.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                    mContentEdt.setBackgroundColor(Color.parseColor("#000000"));
                    mContentEdt.setGravity(Gravity.RIGHT);
                    break;
                default:
            }
            mContentEdt.setLayoutParams(lapText);
            mContentEdt.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mContentEdt.reAdjust();
                }
            }, 50);
            recreateSizeDrawView();
        }
    };

    public int getStateCrop() {
        return stateCrop;
    }

    private void recreateSizeDrawView() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        getPermistion();
        int width = 0;
        int height = 0;
        try {
            if (Build.VERSION.SDK_INT >= 13) {
                display.getSize(size);
                width = size.x;
                height = size.y;
            }
        } catch (NoSuchMethodError e) {
            width = display.getWidth();
            height = display.getHeight();
        }
        if (width <= 0) width = 720;
        if (height <= 0) height = 1280;
        if (getStateCrop() == 1) {
            height = (int) (((width * 9f) / 16f)) - (int) (20 * getResources().getDisplayMetrics().density);
        } else if (getStateCrop() == 0) {
            height = width - (int) (20 * getResources().getDisplayMetrics().density);
        } else if (getStateCrop() == 2) {
            height = (int) (((width * 16f) / 9f)) - (int) (200 * getResources().getDisplayMetrics().density);
        }
        mContentEdt.setHeightLimit(height * 2 / 5);
        mContentEdt.reAdjust();
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mTextAroundLl.getLayoutParams();
        lp.height = height;
        mTextAroundLl.setLayoutParams(lp);
        Log.d("TAG", lp.height + "");
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            String url = data.getDataString();
            if (url != null) {
                galleryAddPic(url);
                if (images == null) {
                    images = new ArrayList<Image>();
                }
                images.add(0, new Image());
                images.get(0).setSource(url);
                images.get(0).setIsOffline(true);
                mPageAdapter.notifyDataSetChanged();
                mViewPager.setVisibility(View.VISIBLE);
                mViewPager.setCurrentItem(0);
            }
//            mImageView.setImageBitmap(imageBitmap);
        }
    }

    private void galleryAddPic(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void galleryAddPic(Uri path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(path);
        this.sendBroadcast(mediaScanIntent);
    }

    private void capture(final int id) {
        mContentEdt.setCursorVisible(false);
        try {
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            mRootView.setDrawingCacheEnabled(true);
            Fragment fragment = mPageAdapter.getCurrentItem();
            if (fragment != null && fragment instanceof ImageFragment) {
//                return;
                bitmap = ((ImageFragment) fragment).getBitmap();
            }
            if (bitmap == null)
                bitmap = mRootView.getDrawingCache(true).copy(
                        Bitmap.Config.ARGB_8888, false);
            mContentEdt.setDrawingCacheEnabled(true);
            Bitmap bitmapText = mContentEdt.getDrawingCache(true).copy(Bitmap.Config.ARGB_8888, false);
            mContentEdt.setDrawingCacheEnabled(false);
            if (bitmap == null || bitmapText == null) return;
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            int bitmap1Width = bitmap.getWidth();
            getPermistion();
            int width = 0;
            int height = 0;
            try {
                if (Build.VERSION.SDK_INT >= 13) {
                    display.getSize(size);
                    width = size.x;
                    height = size.y;
                }
            } catch (NoSuchMethodError e) {
                width = display.getWidth();
                height = display.getHeight();
            }
            if (getStateCrop() != 2)
                width -= (int) (10 * density) * 2;
            else width -= (int) (20 * density) * 2;
            float scale_factor = (float) bitmap1Width / width;
            Matrix matrix = new Matrix();
            matrix.postScale(scale_factor, scale_factor);
            Matrix matrixImage = new Matrix();
            matrixImage.postScale(1 / scale_factor, 1 / scale_factor);
            Bitmap cropBitmap = Bitmap.createBitmap(bitmapText, 0, 0, bitmapText.getWidth(), bitmapText.getHeight(), new Matrix(), false);
            Bitmap cropBitmapImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrixImage, false);
            bitmap1Width = cropBitmapImage.getWidth();
            int bitmap1Height = cropBitmapImage.getHeight();
            Bitmap overlayBitmap = Bitmap.createBitmap(bitmap1Width, bitmap1Height, cropBitmapImage.getConfig());
            int bitmap2Width = cropBitmap.getWidth();
            int bitmap2Height = cropBitmap.getHeight();
            Canvas canvas = new Canvas(overlayBitmap);
            canvas.drawBitmap(cropBitmapImage, new Matrix(), null);
            int top = 0;
            int left = 0;
            switch (mCurrentPosition) {
                case 0: // Top Left
                    top = 0;
                    left = 0;
                    break;
                case 1: // Top center
                    top = 0;
                    left = bitmap1Width / 2 - bitmap2Width / 2;
                    break;
                case 2: // Top right
                    top = 0;
                    left = bitmap1Width - bitmap2Width;
                    break;
                case 3: // Center left
                    top = bitmap1Height / 2 - bitmap2Height / 2;
                    left = 0;
                    break;
                case 4: // Center center
                    top = bitmap1Height / 2 - bitmap2Height / 2;
                    left = bitmap1Width / 2 - bitmap2Width / 2;
                    break;
                case 5: // Center right
                    top = bitmap1Height / 2 - bitmap2Height / 2;
                    left = bitmap1Width - bitmap2Width;
                    break;
                case 6: // Bottom Left
                case 9:
                    left = 0;
                    top = bitmap1Height - bitmap2Height;
                    break;
                case 7: // Bottom center
                case 10:
                    top = bitmap1Height - bitmap2Height;
                    left = bitmap1Width / 2 - bitmap2Width / 2;
                    break;
                case 8: // Bottom right
                case 11:
                    top = bitmap1Height - bitmap2Height;
                    left = bitmap1Width - bitmap2Width;
                    break;
                default:
            }
            canvas.drawBitmap(cropBitmap, left, top, null);

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                overlayBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();
                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                if (!TextUtils.isEmpty(encodedImage)) {
                    encodedImage = "data:image/jpg;base64," + encodedImage;
                }
                Log.d("content", mContent);
                RestClient.uploadImage(encodedImage, mContent, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {

                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (id == 0) {
//                Intent intent = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
//                if (intent != null) {
                shareFB(overlayBitmap, mContent);
//                } else {
//                    installApp("com.facebook.katana");
//                }
            } else if (id == 1) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.twitter.android");
                if (intent == null) {
                    installApp("com.twitter.android");
                    return;
                }
                File sdcard = Environment.getExternalStorageDirectory();
                File f = new File(sdcard, "imft_tw" + System.currentTimeMillis() + " .jpg");
                FileOutputStream out = null;
                out = new FileOutputStream(f);
                overlayBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();
                mRootView.setDrawingCacheEnabled(false);
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                shareTwitter(Uri.fromFile(f));
                galleryAddPic(Uri.fromFile(f));
            } else if (id == 2 || id == 3 || id == 4) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                if (intent == null && id == 2) {
                    installApp("com.instagram.android");
                    return;
                }
                Intent intentg = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.plus");
                if (intentg == null && id == 4) {
                    installApp("com.google.android.apps.plus");
                    return;
                }
                File sdcard = Environment.getExternalStorageDirectory();
                String filename = "imft_all" + System.currentTimeMillis() + " .jpg";
                File f = new File(sdcard, filename);
                FileOutputStream out = null;
                out = new FileOutputStream(f);
                overlayBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();
                mRootView.setDrawingCacheEnabled(false);
                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{f.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {

                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri); // imageUri
                        sharingIntent.setType("image/*");
                        if (id == 2)
                            sharingIntent.setPackage("com.instagram.android");
                        if (id == 4)
                            sharingIntent.setPackage("com.google.android.apps.plus");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, mContent);
                        startActivity(Intent.createChooser(sharingIntent, "Share Image"));
                        galleryAddPic(uri);
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mContentEdt.setCursorVisible(true);
    }

    private void shareFB(Bitmap bitmap, String caption) {
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .setCaption(caption)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        FacebookSdk.sdkInitialize(getApplicationContext());
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            ShareDialog shareDialog = new ShareDialog(this);
            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
        } else {
            ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    ToastUtil.show("Share photo success");
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {
                    ToastUtil.show("Have an error, please try again");
                }
            });
        }
    }

    private void shareTwitter(Uri myImageUri) {
        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text(mContent)
                .image(myImageUri);
        builder.show();
    }

    private void checkContent() {
        KeyboardUtil.hideSoftKeyboard(this);
        mContentEdt.clearFocus();
        String text = mContentEdt.getText().toString();
        if (mContent.equals(text)) {
            return;
        }
        mContent = text;
        images = null;
        mPageAdapter.notifyDataSetChanged();
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.setVisibility(View.GONE);
        if (requestHandle != null) requestHandle.cancel(true);
        requestHandle = RestClient.search(mContent, responseHandler);
    }

    TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                Type type = new TypeToken<List<Image>>() {
                }.getType();
                images = new GsonBuilder().create().fromJson(responseString, type);
            } catch (Exception e) {
                images = new ArrayList<Image>();
            }
            mPageAdapter.notifyDataSetChanged();
//                mPageAdapter = new ImagePageAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mPageAdapter);
            mViewPager.setVisibility(View.VISIBLE);
        }
    };

    private void installApp(String packageName) {
        mContentEdt.setCursorVisible(true);
        ToastUtil.show("You must install App to share Image");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("market://details?id=" + packageName));
        startActivity(intent);
    }

    private TextWatcher contentChange = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (isUserChangeText) {
                isUserChangeText = false;
                return;
            }
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
            case 0:
            case 1:
            case 2:
                v.startAnimation(scaleClick());
                onClick(mGridBtn);
                capture(v.getId());
                break;
            case 3:
                v.startAnimation(scaleClick());
                onClick(mGridBtn);
                SelectionFontDialog dialog = SelectionFontDialog.newInstance(new SelectionFontDialog.SelectedFontListener() {
                    @Override
                    public void onSelected(int p, String font) {
                        Typeface type = Typeface.createFromAsset(MainActivity.this.getAssets(), "fonts/" + font);
                        mContentEdt.setTypeface(type);
                        Paint paint = new Paint();
                        paint.setTypeface(type); // if custom font use `TypeFace.createFromFile`
                        paint.setTextSize(mContentEdt.getTextSize());
                        Rect bounds = new Rect();
                        paint.getTextBounds("What do you thing?", 0, "What do you thing?".length(), bounds);
                        Log.d("Text Height", bounds.height() + "");
                    }
                });
                dialog.show(getSupportFragmentManager(), "SelectionFont");
                break;

            case R.id.fb_btn:
                capture(0);
                break;
            case R.id.gplus_btn:
                capture(4);
                break;
            case R.id.tw_btn:
                capture(1);
                break;
            case R.id.inst_btn:
                capture(2);
                break;
            case R.id.more_share_btn:
                capture(3);
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

    private void displayAddressOutput() {
        if (TextUtils.isEmpty(mAddressOutput)) {
            mAddressTv.setVisibility(View.GONE);
            return;
        }
        mAddressTv.setVisibility(View.VISIBLE);
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
        Log.i(TAG, "Connected");
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
//            if (mAddressRequested) {
//                startIntentService();
//            }
            fetchAddressButtonHandler(null);
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


    private class ImagePageAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {

        int currentPage = 0;

        private SparseArray<Fragment> mPageReferenceMap = new SparseArray<Fragment>();

        public ImagePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            if (images == null) return 0;
            return images.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            String text = mContentEdt != null ? mContentEdt.getText().toString() : "nature";
            Fragment fragment = ImageFragment.getInstance(position, images.get(position), text == null ? "nature" : text);
            mPageReferenceMap.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPageReferenceMap.remove(Integer.valueOf(position));
        }

        public Fragment getCurrentItem() {
            return mPageReferenceMap.get(currentPage);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currentPage = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

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
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
//                ToastUtil.show(getString(R.string.address_found));
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    private void getPermistion() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        12);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        11);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void showNotifi(int id, String content, String title) {
        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_character)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo))
                        .setContentTitle(title)
                        .setAutoCancel(false)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(content))
                        .setOngoing(true)
                        .setContentText(content);

        mNotificationManager.notify(id, mBuilder.build());
    }

    public void cancelNotifi(int id) {
        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id);
    }
}
