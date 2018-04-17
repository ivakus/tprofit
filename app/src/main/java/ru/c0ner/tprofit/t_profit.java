package ru.c0ner.tprofit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.util.LruCache;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import ru.c0ner.tprofit.Fragment.p_MainFragment;
import ru.c0ner.tprofit.Fragment.p_Object;
import ru.c0ner.tprofit.Fragment.p_Person;
import ru.c0ner.tprofit.Fragment.t_Recognise;
import ru.c0ner.tprofit.Fragment.webFragment;
import ru.c0ner.tprofit.datashema.Person;
import ru.c0ner.tprofit.datashema.dataObject;
import ru.c0ner.tprofit.datashema.dataObject_Status;

public class t_profit extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, p_Object.PObjectCallBack, p_Person.PPersonCallBack,p_MainFragment.PMainFragmentCallBack {

    public final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final int REQUEST_CODE = 100;
    FloatingActionButton mFab;
    private final int CAMERA_RESULT = 1;
    private final int PERRMITION_RESULT = 2;
    private ImageView mImageView;
    public String TOKEN;
    public String AUTH_Token = "sessionid=wc68dqzz05xnwau07syu7tdj9m2k3zvm; csrftoken=8MK2v11Mg5h3uuEmLTRAi0jXCEsdk7e9YAY5gzkbb7rPRefaPAR75n9lXcUYwwai";
    final String KEY_TOKEN = "KEY_ROKEN";
    final int CAMERA_ID = 0;
    final boolean FULL_SCREEN = true;
    private Uri mOutputFileUri;
    public p_Object mObjectFragment;
    public FragmentManager fm;
    public p_Person mPersonFragment;
    public p_MainFragment mMainFragment;
    public t_Recognise mRecogniseFragment;
    public LruCache<String, Bitmap> _memoryCache;

    public void p_Object_onItemSelect (String fagmengTAG, int position, dataObject mdataObject )
    {
        Toast.makeText(this, mdataObject.getName().toString(), Toast.LENGTH_SHORT).show();
        FragmentTransaction ft = fm.beginTransaction();
        mPersonFragment.setTitle(mdataObject.getName().toString());
        mPersonFragment.setParent_Object(mdataObject);
        mPersonFragment.setAPI_URL(getString(R.string.str_server_api_name)+"/api/object/id/"+mdataObject.getId()+"/person");
        mPersonFragment.getPerson();
        ft.replace(R.id.main_frame,mPersonFragment);
        ft.addToBackStack(p_Person.TAG);
        ft.commit();

    }

    public void p_Person_onItemSelect (String fagmengTAG, int position, Person personObject ) {

    }
    public void p_MainFragment_onItemSelect (String fagmengTAG, int position, dataObject_Status mdataObject_status ){
        if (mdataObject_status.getPerson().size() > 0) {
          //  Toast.makeText(this, mdataObject_status.getName().toString(), Toast.LENGTH_SHORT).show();
            FragmentTransaction ft = fm.beginTransaction();
            mPersonFragment.setTitle(mdataObject_status.getName().toString());
            //mPersonFragment.setParent_Object(mdataObject);
            //mPersonFragment.setAPI_URL(getString(R.string.str_server_api_name)+"/api/object/id/"+mdataObject.getId()+"/person");
            //mPersonFragment.getPerson();
            mPersonFragment.setItemList(mdataObject_status.getPerson());
            // mPersonFragment.mAdapter.notifyDataSetChanged();
            ft.replace(R.id.main_frame, mPersonFragment);
            ft.addToBackStack(p_Person.TAG);
            ft.commit();
        }
        else {

            Toast.makeText(this, "На объекте : "+mdataObject_status.getName().toString()+ ", сегодня нет персонала ", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       TOKEN = getIntent().getStringExtra(ACCESS_TOKEN);

      //  if (AUTH_Token.length() <=0) {finish();}
        setContentView(R.layout.activity_t_profit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);
        mFab.setVisibility(View.INVISIBLE);
        //mImageView = findViewById(R.id.imageView);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        fm = getSupportFragmentManager();
        mObjectFragment = new p_Object();
        mPersonFragment = new p_Person();
        mMainFragment = new p_MainFragment();
        mRecogniseFragment = new t_Recognise();
        mPersonFragment.set_context(getApplicationContext());

        // иницилизируем кешь хранилище
        if (_memoryCache == null) {
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            final int cacheSize = maxMemory / 8;
            _memoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
                }
            };
        };
        mPersonFragment.set_memoryCache(_memoryCache);

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},PERRMITION_RESULT);
        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERRMITION_RESULT);
        }


        FragmentTransaction ft =  fm.beginTransaction();
       ft.add(R.id.main_frame,mMainFragment);
       ft.commit();
    }

    public void Init (){

    }


    @Override

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.t_profit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction ft= fm.beginTransaction();
        switch (id)  {
            case  R.id.nav_camera : {
            // Handle the camera action
                mFab.setVisibility(View.VISIBLE);
                ft.replace(R.id.main_frame, mRecogniseFragment);
                ft.addToBackStack(t_Recognise.TAG);
                break;
        }
        case R.id.nav_object_fragment : {
            ft.replace(R.id.main_frame, mObjectFragment);
            ft.addToBackStack(p_Object.TAG);
            break;
        }
            case R.id.nav_main_fragment : {
                ft.replace(R.id.main_frame, mMainFragment);
                ft.addToBackStack(mMainFragment.TAG);
                break;
            }
            case R.id.nav_personal_fragment :
            {
                mPersonFragment.setTitle("Сотрудники");
                mPersonFragment.setAPI_URL("http://api.aoprofit.com/api/person/");
                mPersonFragment.getPerson();
                ft.replace(R.id.main_frame,mPersonFragment);
                ft.addToBackStack(p_Person.TAG);
                break;
            }

        }
        ft.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        int view_id = v.getId();

        if (view_id == R.id.fab) {
            // Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //startActivityForResult(cameraIntent, CAMERA_RESULT);
          TakePictureIntent();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_RESULT) {
            // Проверяем, содержит ли результат маленькую картинку
                mRecogniseFragment.setPic();
            }
    }


    private void saveFullImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(),
                "test.jpg");
        mOutputFileUri = Uri.fromFile(file);
      //  intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
        startActivityForResult(intent, CAMERA_RESULT);
    }

    private void TakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = mRecogniseFragment.createImageFile();
                //photoFile = new File(Environment.getExternalStorageDirectory(),"test.jpg");
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,"ru.c0ner.tprofit.fileprovider",photoFile);
               takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_RESULT);
            }
        }
    }

}
