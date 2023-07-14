package com.kibet.saverbeta;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;
import com.kibet.saverbeta.fragments.GBImageFragment;
import com.kibet.saverbeta.fragments.GBVideoFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GBWhatsappActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tabslayout) TabLayout tabLayout;
    @BindView(R.id.viewpager) ViewPager viewPager;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gb_activity);

        ButterKnife.bind(this);

        toolbar.setNavigationOnClickListener(v -> GBWhatsappActivity.super.onBackPressed());

        toolbar.setTitle("GB WhatsApp Statuses");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);


        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }
    @Override
    public void onPause(){
        if(adView!=null){
            adView.pause();
        }
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy(){
        if(adView!=null){
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_other, menu);
        return true;
    }


    static class PagerAdapter extends FragmentPagerAdapter {
        private final GBImageFragment imageFragment;
        private final GBVideoFragment videoFragment;

        public PagerAdapter(FragmentManager fm) {
            super(fm);
            imageFragment = new GBImageFragment();
            videoFragment = new GBVideoFragment();
        }

        @Override
        public Fragment getItem(int i) {
            if(i == 0)
            {
                return imageFragment;
            }
            else{
                return videoFragment;
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0){
                return "images";
            }
            else{
                return "videos";
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}