package com.maghribpress.torrebook.ui;


import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codekidlabs.storagechooser.StorageChooser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.folioreader.Config;
import com.folioreader.Constants;
import com.folioreader.FolioReader;
import com.folioreader.model.HighLight;
import com.folioreader.model.ReadPosition;
import com.folioreader.model.ReadPositionImpl;
import com.folioreader.ui.base.OnSaveHighlight;
import com.folioreader.util.OnHighlightListener;
import com.folioreader.util.ReadPositionListener;
import com.frostwire.jlibtorrent.Priority;
import com.frostwire.jlibtorrent.TorrentHandle;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.maghribpress.torrebook.GlideApp;
import com.maghribpress.torrebook.classes.ViewTargets;
import com.masterwok.simpletorrentandroid.TorrentSession;
import com.masterwok.simpletorrentandroid.TorrentSessionOptions;
import com.masterwok.simpletorrentandroid.contracts.TorrentSessionListener;
import com.masterwok.simpletorrentandroid.models.TorrentSessionStatus;
import com.maghribpress.torrebook.BasicApp;
import com.maghribpress.torrebook.InsertBookOfflineListener;
import com.maghribpress.torrebook.OnBookInsertCompleted;
import com.maghribpress.torrebook.OnTrackingInfoReceived;
import com.maghribpress.torrebook.R;
import com.maghribpress.torrebook.TaskCompletedListener;
import com.maghribpress.torrebook.TorrentDownloadListener;
import com.maghribpress.torrebook.TrackingInfoAsync;
import com.maghribpress.torrebook.classes.ApiTokenObject;
import com.maghribpress.torrebook.classes.BookInsertExecutor;
import com.maghribpress.torrebook.classes.Functions;
import com.maghribpress.torrebook.classes.HighlightData;
import com.maghribpress.torrebook.classes.InsertBookTrackingInfo;
import com.maghribpress.torrebook.classes.InsertBooksOffline;
import com.maghribpress.torrebook.classes.ReadTrackingInfoAsyncTask;
import com.maghribpress.torrebook.classes.SharedPrefManager;
import com.maghribpress.torrebook.classes.TorrentDownloader;
import com.maghribpress.torrebook.db.AppDatabase;
import com.maghribpress.torrebook.db.entity.Author;
import com.maghribpress.torrebook.db.entity.Book;
import com.maghribpress.torrebook.db.entity.BookTracking;
import com.maghribpress.torrebook.db.entity.Tracking;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import mbanje.kurt.fabbutton.FabButton;
import mbanje.kurt.fabbutton.ProgressRingView;
import nl.siegmann.epublib.epub.EpubReader;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TaskCompletedListener,OnBookInsertCompleted, TorrentSessionListener,TorrentDownloadListener, InsertBookOfflineListener, OnTrackingInfoReceived, OnHighlightListener {
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    NavigationView navigationView;

    MainFragment mainFragment;
    SearchFragment searchFragment;
    TorrentFragment torrentFragment;
    SettingsFragement settingsFragement;

    private FolioReader mFolioReader;

    TorrentDownloadListener mTDL;
    private TorrentSession mSession;
    FabButton mProgressBar;
    TextView mTextViewDownloadStatus;
    AsyncTask mdownloader;
    NotificationManagerCompat notificationManager;
    NotificationCompat.Builder mBuilder;
    private final static String CHANNEL_ID="778";
    private final static int notificationId=778;
    FloatingActionButton fab;
    private final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE=1;
    private boolean isActivityDestroyed=false;
    Book mainBook;

    SharedPrefManager sharedPrefs;
    Toolbar toolbar;
    Button endButton;
    boolean doubleBackToExitPressedOnce= false;;
    public FolioReader getmFolioReader() {
        return mFolioReader;
    }

    public TorrentDownloadListener getTorrentDownloadListener() {
        return mTDL;
    }

    private void showCaseToolbar() {
        // sequence example
        try {
            ViewTarget navigationButtonViewTarget = ViewTargets.navigationButtonViewTarget(toolbar);
            new ShowcaseView.Builder(this)
                    .withNewStyleShowcase()
                    .hideOnTouchOutside()
                    .setTarget(navigationButtonViewTarget)
                    .setContentTitle(getString(R.string.showMainMenu))
                    .setContentText(getString(R.string.showMenuDesc))
                    .setShowcaseEventListener(new OnShowcaseEventListener() {
                        @Override
                        public void onShowcaseViewHide(ShowcaseView showcaseView) {

                        }

                        @Override
                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                            ViewTarget searchViewTarget = ViewTargets.getViewTarget(toolbar,2);
                            new ShowcaseView.Builder(MainActivity.this)
                                    .withNewStyleShowcase()
                                    .hideOnTouchOutside()
                                    .setTarget(searchViewTarget)
                                    .setShowcaseEventListener(new OnShowcaseEventListener() {
                                        @Override
                                        public void onShowcaseViewHide(ShowcaseView showcaseView) {

                                        }

                                        @Override
                                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                            ViewTarget importViewTarget = ViewTargets.getViewTarget(toolbar,2);
                                            new ShowcaseView.Builder(MainActivity.this)
                                                    .withNewStyleShowcase()
                                                    .hideOnTouchOutside()
                                                    .setTarget(importViewTarget)
                                                    .setContentTitle(getString(R.string.importBooks))
                                                    .setContentText(getString(R.string.importbooksdesc_))
                                                    .build()
                                                    .show();
                                        }

                                        @Override
                                        public void onShowcaseViewShow(ShowcaseView showcaseView) {

                                        }

                                        @Override
                                        public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                                        }
                                    })
                                    .setContentTitle(getString(R.string.searchbooks))
                                    .setContentText(getString(R.string.searchbooksdescr))
                                    .build()
                                    .show();
                        }

                        @Override
                        public void onShowcaseViewShow(ShowcaseView showcaseView) {

                        }

                        @Override
                        public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                        }
                    })
                    .build()
                    .show();
        } catch (ViewTargets.MissingViewException e) {
            e.printStackTrace();
        }
    }
        private void showcaseFAB() {
            ViewTarget target = new ViewTarget(fab);
             new ShowcaseView.Builder(this)
                    .withNewStyleShowcase()
                    .setTarget(target)
                    .setContentTitle(getString(R.string.resume_reading))
                    .setContentText(getString(R.string.resume_reading_))
                     .hideOnTouchOutside()
                    .setShowcaseEventListener(new OnShowcaseEventListener() {
                        @Override
                        public void onShowcaseViewHide(ShowcaseView showcaseView) {

                        }

                        @Override
                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                            showCaseToolbar();
                        }

                        @Override
                        public void onShowcaseViewShow(ShowcaseView showcaseView) {

                        }

                        @Override
                        public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                        }
                    })
                    .build().show();
        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                new TrackingInfoAsync(MainActivity.this,getApplication()).execute();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
         toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSupportFragmentManager().getBackStackEntryCount()>0) {
                    onBackPressed();
                }
                else {
                    drawer.openDrawer(navigationView);
                    toggle.syncState();
                }
            }
        });
        sharedPrefs = new SharedPrefManager(this);
        ApiTokenObject.getInstance().setToken(sharedPrefs.getToken());
        ApiTokenObject.getInstance().setmUsername(sharedPrefs.getName());
        ApiTokenObject.getInstance().setmEmail(sharedPrefs.getEmail());
        ApiTokenObject.getInstance().setAvatar(sharedPrefs.getAvatar());
        mFolioReader=FolioReader.get();
        if(!sharedPrefs.getDisclaimerstate()) {
            showDisclaimer();
        }
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                FragmentManager fm = getSupportFragmentManager();
                if (fm != null) {
                    int backStackCount = fm.getBackStackEntryCount();
                    if (backStackCount > 0) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    }else {
                        toggle.setDrawerIndicatorEnabled(true);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        toggle.syncState();
                    }
                }
            }
        });
        if(savedInstanceState ==null) {
            Log.d("ROTATIONTAG","savedInstanceState Is NULL");
            mainFragment = new MainFragment();
            startFragment(mainFragment,true);
        }else {
            Log.d("ROTATIONTAG","savedInstanceState Is NOT NULL");
            mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.class.getSimpleName());
            searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag(SearchFragment.class.getSimpleName());
            torrentFragment = (TorrentFragment) getSupportFragmentManager().findFragmentByTag(TorrentFragment.class.getSimpleName());
            settingsFragement = (SettingsFragement) getSupportFragmentManager().findFragmentByTag(SettingsFragement.class.getSimpleName());
        }
        /*if(torrentFragment !=null) {
            startFragment(torrentFragment,false);
        }*/
        mTDL = this;

        //seting up endButton

        endButton = new Button(this);
        endButton.setText("");
        endButton.setVisibility(View.GONE);

        View hView =  navigationView.getHeaderView(0);
        TextView usernameTextView = (TextView) hView.findViewById(R.id.txtUsername);
        ImageView userAvatarImg = (ImageView) hView.findViewById(R.id.imageView);
        usernameTextView.setText(ApiTokenObject.getInstance().getmUsername());
        GlideApp.with(this)
                .load(ApiTokenObject.getInstance().getAvatar())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.ic_launcher)
                .fitCenter()
                .into(userAvatarImg);
        //Changing App Title TypeFace
        Typeface mtypeFace = Typeface.createFromAsset(getAssets(),
                "fonts/caviardreamsbold.ttf");
        TextView tv = new TextView(getApplicationContext());
        android.support.v7.app.ActionBar.LayoutParams layoutParams = new android.support.v7.app.ActionBar.LayoutParams(android.support.v7.app.ActionBar.LayoutParams.MATCH_PARENT,
                android.support.v7.app.ActionBar.LayoutParams.MATCH_PARENT);
        //RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        //tv.setLayoutParams(lp);
        tv.setText("Torrebooks");
        tv.setTextSize(20);
        tv.setTextColor(Color.parseColor("#604D40"));
        tv.setTypeface(mtypeFace);
        tv.setGravity(Gravity.CENTER | Gravity.START);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(tv,layoutParams);
        getHighlightsAndSave();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce ) {
                super.onBackPressed();
            }else {
                if(getSupportFragmentManager().getBackStackEntryCount() ==0) {
                    toggle.syncState();
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, getString(R.string.pressagaintoexit), Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce=false;
                        }
                    }, 3000);
                }else if(getSupportFragmentManager().getBackStackEntryCount() >0) {
                    super.onBackPressed();
                }
            }
        }
    }
    public void setDrawerItemSelected(int position) {
        if(navigationView != null) {
            navigationView.getMenu().getItem(position).setChecked(true);
        }
    }
    private Bitmap generateBookCover(String title, String authors) {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View view = inflater.inflate(R.layout.book_cover_layout, null);
        Typeface mtypeFace = Typeface.createFromAsset(getAssets(),
                "fonts/halisfont.otf");
        FrameLayout bookCover = (FrameLayout) view.findViewById(R.id.boo_cover_mainlayout);
        TextView bookTitle = (TextView) view.findViewById(R.id.book_cover_title);
        TextView bookAuthor = (TextView) view.findViewById(R.id.book_cover_author);
        bookTitle.setTypeface(mtypeFace);
        bookAuthor.setTypeface(mtypeFace);
        Random rnd = new Random();
        int[][] a = {
                {11, 169, 199,255,255,255},
                {230, 136, 23,0,0,0},
                {247, 240, 213,0,0,0},
                {89, 186, 184,255,255,255},
                {241, 198, 185,0,0,0},
                {191, 220, 218,21,49,48},
        };
        int selectedRow = rnd.nextInt(5);
        int gradientFirstColor = Color.argb(255,a[selectedRow][0],a[selectedRow][1],a[selectedRow][2]);
        int gradientSecondColor = Color.argb(204,a[selectedRow][0],a[selectedRow][1],a[selectedRow][2]);
        int textColor = Color.argb(255,a[selectedRow][3],a[selectedRow][4],a[selectedRow][5]);
        bookTitle.setTextColor(textColor);
        bookAuthor.setTextColor(textColor);
        bookTitle.setText(title);
        bookAuthor.setText(authors);
        GradientDrawable gd = new GradientDrawable();
        // Set the color array to draw gradient
        gd.setColors(new int[]{
                gradientSecondColor,
                gradientFirstColor,
                gradientSecondColor
        });
        gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setSize((int)Functions.convertDpToPixel(105,MainActivity.this),(int)Functions.convertDpToPixel(162,MainActivity.this));
        bookCover.setBackground(gd);
        bookCover.setDrawingCacheEnabled(true);
        bookCover.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        bookCover.layout(0, 0, bookCover.getMeasuredWidth(), bookCover.getMeasuredHeight());
        bookCover.buildDrawingCache(true);
        return  Bitmap.createBitmap(bookCover.getDrawingCache());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        // Setting SearchView
        SearchView searchView = (SearchView) menu.findItem(R.id.search_book_view).getActionView();
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Book title or author");
        return true;
    }
    public void startFragment(Fragment fragment, boolean replace) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            if(replace) {
                transaction.replace(R.id.fragment_container, fragment,fragment.getClass().getSimpleName());
                transaction.commit();
            }else {
                transaction.add(R.id.fragment_container, fragment );
                transaction.addToBackStack(fragment.getClass().getSimpleName());
                transaction.commit();
            }
    }
    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }
    private void shareApp() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Torrebooks");
            String sAux = "\nHey, check this app its a wonderful ebook reader you can find any book there for free, literally any book \n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=" + getPackageName() + "\n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            settingsFragement = new SettingsFragement();
            startFragment(settingsFragement,true);
            return true;
        }else if(id == R.id.action_import) {
                requestWriteExtPermission();
                showImportDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main) {
            mainFragment = new MainFragment();
            startFragment(mainFragment,true);
        } else if (id == R.id.nav_download) {
            searchFragment = new SearchFragment();
            startFragment(searchFragment,true);
        } else if (id == R.id.nav_manage) {
            settingsFragement = new SettingsFragement();
            startFragment(settingsFragement,true);
        } else if (id == R.id.nav_share) {
            shareApp();
        } else if (id==R.id.nav_rateapp) {
            rateApp();
        }else if (id == R.id.nav_about) {
            setupAndShowDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onTaskComplete(Book book, Tracking tracking) {
        openBook(book,tracking);
    }
    private void openBook(Book book, Tracking tracking) {
        Config config = new Config().setThemeColorRes(R.color.colorAccent).setFont(Constants.FONT_RALEWAY).setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);
        if(tracking!=null) {
            ReadPosition readPosition = new ReadPositionImpl(tracking.getFolioBookid(), tracking.getChapterHref(),
                    tracking.isUsingId(), tracking.getValue());
            mFolioReader.setReadPosition(readPosition);
        }
        mFolioReader.setReadPositionListener(new ReadPositionListener() {

            @Override
            public void saveReadPosition(ReadPosition readPosition) {
                Tracking tracking = new Tracking();
                tracking.setBookid(book.getId());
                tracking.setBookimportid(book.getImportedid());
                tracking.setCreated_at(System.currentTimeMillis());
                try {
                    JSONObject jsonObj = new JSONObject(readPosition.toJson());
                    int currentPage = Integer.valueOf(jsonObj.getString("value"));
                    String value = jsonObj.getString("value");
                    String foliobookid = jsonObj.getString("bookId");
                    String chapterHref = jsonObj.getString("chapterHref");
                    boolean usingId = jsonObj.getBoolean("usingId");
                    tracking.setCurrentPage(currentPage);
                    tracking.setFolioBookid(foliobookid);
                    tracking.setChapterHref(chapterHref);
                    tracking.setUsingId(usingId);
                    tracking.setValue(value);
                    AppDatabase appdb = ((BasicApp) getApplicationContext()).getDatabase();
                    new InsertBookTrackingInfo(new WeakReference<Tracking>(tracking), new WeakReference<AppDatabase> (appdb)).execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        mFolioReader.setConfig(config,true);
        mFolioReader.openBook(book.getEpubPath());
    }

    @Override
    public void onInsertComplete(Book book) {
        Log.d("TRACKINGTAGS",book.getTitle());
        new ReadTrackingInfoAsyncTask(new WeakReference<MainActivity>(MainActivity.this),book,MainActivity.this).execute();
    }

    private void killSession() {
        if(mBuilder !=null && notificationManager!=null) {
            mBuilder.setContentText("Download Aborted")
                    .setProgress(0,0,false);
            notificationManager.notify(notificationId, mBuilder.build());
            notificationManager.cancel(notificationId);
        }
        mSession.stop();
        mSession.setListener(null);
        mSession=null;
    }
    private void updateProgress(String msg, float progress) {
        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mBuilder !=null && notificationManager!=null) {
                        mBuilder.setProgress(100, (int)progress, false);
                        mBuilder.setContentText("Downloading.." +  String.format("%.2f",progress) + "%");
                        notificationManager.notify(notificationId, mBuilder.build());
                    }
                    if(mProgressBar !=null && mTextViewDownloadStatus!=null) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mTextViewDownloadStatus.setVisibility(View.VISIBLE);
                        mProgressBar.setProgress(progress);
                        mTextViewDownloadStatus.setText(msg);
                    }
                }//public void run() {
            });
    }
    private void updateMsg(String msg) {
        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mProgressBar !=null && mTextViewDownloadStatus!=null) {
                        mTextViewDownloadStatus.setVisibility(View.VISIBLE);
                        mTextViewDownloadStatus.setText(msg);
                    }
                }//public void run() {
            });



    }

    //TorrentStuff

    @Override
    public void onAddTorrent(@NotNull TorrentHandle torrentHandle, @NotNull TorrentSessionStatus torrentSessionStatus) {
        updateMsg("Adding Torrent...");
        Log.d("TORRENTTAGS","onAddTorrent and Priorities Set");
    }

    @Override
    public void onBlockUploaded(@NotNull TorrentHandle torrentHandle, @NotNull TorrentSessionStatus torrentSessionStatus) {
        Log.d("TORRENTTAGS","onBlockUploaded");
    }

    @Override
    public void onMetadataFailed(@NotNull TorrentHandle torrentHandle, @NotNull TorrentSessionStatus torrentSessionStatus) {
        updateMsg("Gethering metadata failed!!");
        killSession();
        Log.d("TORRENTTAGS","onMetadataFailed");
    }

    @Override
    public void onMetadataReceived(@NotNull TorrentHandle torrentHandle, @NotNull TorrentSessionStatus torrentSessionStatus) {
        Priority[] priorities = new Priority[torrentHandle.torrentFile().numFiles()];
        for (int f=0;f<torrentHandle.torrentFile().numFiles();f++) {
            if(torrentHandle.torrentFile().files().fileName(f).endsWith(".epub")) {
                priorities[f] = Priority.SEVEN;
                Log.d("TORRENTTAGS","Priority Is 7 => " + torrentHandle.torrentFile().files().fileName(f));
            }else {
                priorities[f] = Priority.IGNORE;
                Log.d("TORRENTTAGS","Priority Is 0 => " + torrentHandle.torrentFile().files().fileName(f));
            }
        }
        torrentHandle.prioritizeFiles(priorities);
        updateMsg("metadata received");

            notificationManager = NotificationManagerCompat.from(MainActivity.this);
            mBuilder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID);
            mBuilder.setContentTitle("Book Download")
                    .setContentText("Downloading..")
                    .setOngoing(true)
                    .setOnlyAlertOnce(true)
                    .setSmallIcon(android.R.drawable.stat_sys_download)  // here is the animated icon
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), android.R.drawable.stat_sys_download))
                    .setPriority(NotificationCompat.PRIORITY_LOW);

            // Issue the initial notification with zero progress
            int PROGRESS_MAX = 100;
            int PROGRESS_CURRENT = 0;
            mBuilder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
            notificationManager.notify(notificationId, mBuilder.build());



        Log.d("TORRENTTAGS","onMetadataReceived");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isActivityDestroyed=true;
        FolioReader.clear();
        if(notificationManager!=null) {
            notificationManager.cancel(notificationId);
            mdownloader.cancel(true);
            killSession();
            mdownloader=null;
        }

    }

    @Override
    public void onPieceFinished(@NotNull TorrentHandle torrentHandle, @NotNull TorrentSessionStatus torrentSessionStatus) {
        updateProgress("Downloading..",(int)(torrentSessionStatus.getProgress()*100));
        Log.d("TORRENTTAGS", String.valueOf(torrentSessionStatus.getProgress()) + "%");
        Log.d("TORRENTTAGS","onPieceFinished");
    }

    @Override
    public void onTorrentDeleteFailed(@NotNull TorrentHandle torrentHandle, @NotNull TorrentSessionStatus torrentSessionStatus) {
        Log.d("TORRENTTAGS","onTorrentDeleteFailed");
    }

    @Override
    public void onTorrentDeleted(@NotNull TorrentHandle torrentHandle, @NotNull TorrentSessionStatus torrentSessionStatus) {
        Log.d("TORRENTTAGS","onTorrentDeleted");
    }

    @Override
    public void onTorrentError(@NotNull TorrentHandle torrentHandle, @NotNull TorrentSessionStatus torrentSessionStatus) {
        updateMsg("Torrent Error !!");
        killSession();
        Log.d("TORRENTTAGS","onTorrentError");
    }

    @Override
    public void onTorrentFinished(@NotNull TorrentHandle torrentHandle, @NotNull TorrentSessionStatus torrentSessionStatus) {
        String epubFilePath="";
        for(int i=0;i<torrentHandle.torrentFile().numFiles();i++) {
            Log.d("TORRENTTAGS",torrentHandle.torrentFile().files().filePath(i));
            if(torrentHandle.torrentFile().files().filePath(i).endsWith(".epub")) {
                epubFilePath = sharedPrefs.getSaveDirectory() + "/" + torrentHandle.torrentFile().files().filePath(i);
            }
        }
        if(epubFilePath.endsWith(".epub")) {
            updateMsg("Download completed.");
            notificationManager.cancel(notificationId);
            mainBook.setEpubPath(epubFilePath);
            //openBook(epubFilePath);
            new BookInsertExecutor(((BasicApp)MainActivity.this.getApplicationContext()).getDatabase(),mainBook,MainActivity.this).execute();
        }else {
            updateMsg("Unknown File Format!");
        }
        Log.d("TORRENTTAGS","onTorrentFinished");
        killSession();
    }
    @Override
    public void onTorrentPaused(@NotNull TorrentHandle torrentHandle, @NotNull TorrentSessionStatus torrentSessionStatus) {
        Log.d("TORRENTTAGS","onTorrentPaused");
    }

    @Override
    public void onTorrentRemoved(@NotNull TorrentHandle torrentHandle, @NotNull TorrentSessionStatus torrentSessionStatus) {
        Log.d("TORRENTTAGS","onTorrentRemoved");
    }

    @Override
    public void onTorrentResumed(@NotNull TorrentHandle torrentHandle, @NotNull TorrentSessionStatus torrentSessionStatus) {
        Log.d("TORRENTTAGS","onTorrentResumed");
    }

    @Override
    public void StartDownload(Book book, String magnet, View view) {
        mainBook=book;
        if(mProgressBar != null && mTextViewDownloadStatus != null) {
            mTextViewDownloadStatus.setVisibility(View.INVISIBLE);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        mProgressBar.showProgress(false);
                }
            });
        }
        if(mSession != null) {
            if(mSession.isRunning()) {
                if(notificationManager !=null) {
                    notificationManager.cancel(notificationId);
                }
                mSession.stop();
                mSession.setListener(null);
                mSession=null;
                mdownloader=null;
            }
        }
        if (view instanceof ProgressRingView) {
            CardView mainCard= (CardView) view.getParent().getParent().getParent();
            mProgressBar =(FabButton) mainCard.findViewById(R.id.btnDownload);
            mTextViewDownloadStatus =(TextView) mainCard.findViewById(R.id.txt_download_details);
        }else {
            mProgressBar =(FabButton) view.findViewById(R.id.btnDownload);
            mTextViewDownloadStatus =(TextView) view.findViewById(R.id.txt_download_details);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mProgressBar !=null && mTextViewDownloadStatus!=null) {
                    mTextViewDownloadStatus.setVisibility(View.VISIBLE);
                    mTextViewDownloadStatus.setText(getString(R.string.starting));
                    mProgressBar.resetIcon();
                    //mProgressBar.showShadow(false);
                    mProgressBar.showProgress(true);
                }
            }//public void run() {
        });

        TorrentSessionOptions torrentOptions = new TorrentSessionOptions(new File(sharedPrefs.getSaveDirectory()),false,false,false,false,8,0,0,200,10,88);
        mSession = new TorrentSession(torrentOptions);
        mSession.setListener(this);
        mdownloader = new TorrentDownloader(new WeakReference<Context>(this),magnet, new WeakReference<TorrentSession>(mSession)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    public void requestWriteExtPermission() {
        if(!isActivityDestroyed) {
            if (Build.VERSION.SDK_INT >= 23)
            {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                }
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Toast.makeText(MainActivity.this,getString(R.string.permission_denied),Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }

    }

    @Override
    public void onBookInserted(long bookid) {
        mainBook.setId(bookid);
    }

    @Override
    public void TrackingInfoReceived(BookTracking bookTracking) {
        if(bookTracking !=null) {
            if(bookTracking.getBook() != null) {
                File file = new File(bookTracking.getBook().getEpubPath());
                if(file.exists()) {
                    openBook(bookTracking.getBook(),bookTracking.getTracking());
                }else {
                    Toast.makeText(this,getString(R.string.book_not_found),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void getHighlightsAndSave() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<HighLight> highlightList = null;
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    highlightList = objectMapper.readValue(
                            loadAssetTextAsString("highlights/highlights_data.json"),
                            new TypeReference<List<HighlightData>>() {
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (highlightList == null) {
                    mFolioReader.saveReceivedHighLights(highlightList, new OnSaveHighlight() {
                        @Override
                        public void onFinished() {
                            //You can do anything on successful saving highlight list
                        }
                    });
                }
            }
        }).start();
    }

    private String loadAssetTextAsString(String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e("HomeActivity", "Error opening asset " + name);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("HomeActivity", "Error closing asset " + name);
                }
            }
        }
        return null;
    }
    @Override
    public void onHighlight(HighLight highlight, HighLight.HighLightAction type) {
        Toast.makeText(this,
                "highlight id = " + highlight.getUUID() + " type = " + type,
                Toast.LENGTH_SHORT).show();
    }
    private void showDisclaimer() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.disclaimer_dialog_layout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        TextView logText = (TextView) dialogView.findViewById(R.id.disclaimer_text);
        Typeface mtypeFace = Typeface.createFromAsset(getAssets(),
                "fonts/droidsans.ttf");
        logText.setTypeface(mtypeFace);
        AppCompatButton iagree = (AppCompatButton) dialogView.findViewById(R.id.iagreeBtn);
        AlertDialog b = dialogBuilder.create();
        b.setCanceledOnTouchOutside(false);
        iagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedPrefs==null) {
                    sharedPrefs = new SharedPrefManager(MainActivity.this);
                }
                sharedPrefs.setDisclaimerState(true);
                b.dismiss();
                showcaseFAB();
                /* new ShowcaseView.Builder(MainActivity.this)
                        .setTarget(new ViewTarget(fab))
                        .withNewStyleShowcase()
                        .replaceEndButton(showCaseEndButton)
                        .setContentTitle(getString(R.string.resume_reading))
                        .setContentText(getString(R.string.resume_reading_))
                        .hideOnTouchOutside()
                        .build();*/
            }
        });
        b.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(sharedPrefs==null) {
                    sharedPrefs = new SharedPrefManager(MainActivity.this);
                }
                if(!sharedPrefs.getDisclaimerstate()) {
                    MainActivity.this.finish();
                }
            }
        });
        b.show();
    }
    private void setupAndShowDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.aboutus_dialog_layout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        TextView logText = (TextView) dialogView.findViewById(R.id.logo_text);
        Typeface mtypeFace = Typeface.createFromAsset(getAssets(),
                "fonts/caviardreams.ttf");
        logText.setTypeface(mtypeFace);
        ImageView fbButton = (ImageView) dialogView.findViewById(R.id.facebook_button);
        ImageView emailButton = (ImageView)dialogView.findViewById(R.id.email_button);
        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/torrebooks/"));
                startActivity(intent);
            }
        });
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, "contact@maghribpress.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Torrebooks : android App");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCanceledOnTouchOutside(true);
        b.show();
    }
    private void showImportDialog() {
        if(!isActivityDestroyed) {
            StorageChooser.Theme theme = new StorageChooser.Theme(this);
            theme.setScheme(getResources().getIntArray(R.array.paranoid_theme));
            StorageChooser chooser = new StorageChooser.Builder()
                    .withActivity(MainActivity.this)
                    .allowCustomPath(true)
                    .setType(StorageChooser.DIRECTORY_CHOOSER)
                    .withFragmentManager(getFragmentManager())
                    .withMemoryBar(true)
                    .setTheme(theme)
                    .build();

// Show dialog whenever you want by
            chooser.show();

// get path that the user has chosen
            chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
                @Override
                public void onSelect(String path) {
                    List<Book> books = getBooksFromPath(path);
                    if(mainFragment !=null) {
                        mainFragment.importFinished(books);
                    }
                    new InsertBooksOffline(new WeakReference<List<Book>>(books),(new WeakReference<AppDatabase>(AppDatabase.getInstance(getApplicationContext()))),new WeakReference<>(MainActivity.this)).execute();
                }
            });
        }

    }
    private String getAuthorstostring(List<Author> authors) {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<authors.size();i++) {
            if(i==(authors.size()-1)) {
                sb.append(authors.get(i).getName());
            }else {
                sb.append(authors.get(i).getName() + "\n");
            }
        }
        return  sb.toString();
    }
    private List<Book> getBooksFromPath(String path) {
        List<Book> _books = new ArrayList<Book>();
        List<String> epubs = getEpubFromPath(path);
        for (String epub : epubs) {
            FileInputStream epubInputStream = null;
            try {
                epubInputStream = new FileInputStream(new File(epub));
                // Load Book from inputStream
                try {
                    nl.siegmann.epublib.domain.Book epubbook = (new EpubReader()).readEpub(epubInputStream);
                    Book _newbook = new Book();

                    _newbook.setEpubPath(epub);
                    _newbook.setTitle(epubbook.getTitle());
                    ArrayList<Author> authors = new ArrayList<Author>();
                    for (nl.siegmann.epublib.domain.Author _author : epubbook.getMetadata().getAuthors()) {
                        Author newAuthor = new Author();
                        newAuthor.setName(_author.getFirstname() + " " + _author.getLastname());
                        newAuthor.setCreated_at(System.currentTimeMillis());
                        authors.add(newAuthor);
                    }
                    _newbook.setAuthors(authors);
                    _newbook.setId(-1);
                    _newbook.setImportedid(UUID.randomUUID().toString());
                    Bitmap coverImage = null;
                    try {
                       coverImage = BitmapFactory.decodeStream(epubbook.getCoverImage()
                                .getInputStream());
                    }catch (NullPointerException ex) {
                        coverImage = generateBookCover(_newbook.getTitle(),getAuthorstostring(_newbook.getAuthors()));
                    }
                    _newbook.setCreated_at(System.currentTimeMillis());
                    _newbook.setCover(coverImage);
                    _books.add(_newbook);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return _books;
    }

    private List<String> getEpubFromPath(String path) {
        List<String> _paths = new ArrayList<String>();
        File directory = new File(path);
        // Get all files from a directory.
        File[] fList = directory.listFiles();
        if(fList != null) {
            for (File file : fList) {
                if (file.isFile()) {
                    if(file.getAbsolutePath().endsWith(".epub")) {
                        Log.d("EPUBFILE",file.getAbsolutePath());
                        _paths.add(file.getAbsolutePath());
                    }
                } else if (file.isDirectory()) {
                    _paths.addAll(getEpubFromPath(file.getAbsolutePath()));
                }
            }
        }
        Log.d("EPUBFILE",String.valueOf(_paths.size()));
        return _paths;
    }
}
