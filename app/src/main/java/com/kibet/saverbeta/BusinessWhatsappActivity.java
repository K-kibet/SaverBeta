package com.kibet.saverbeta;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;
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
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;
import com.kibet.saverbeta.fragments.BusinessImageFragment;
import com.kibet.saverbeta.fragments.BusinessVideoFragment;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BusinessWhatsappActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tabslayout) TabLayout tabLayout;
    @BindView(R.id.viewpager) ViewPager viewPager;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_activity);

        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener(v -> BusinessWhatsappActivity.super.onBackPressed());
        toolbar.setTitle("WhatsApp Business Statuses");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_other, menu);
        return true;
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


    static class PagerAdapter extends FragmentPagerAdapter {
        private final BusinessImageFragment imageFragmentBusiness;
        private final BusinessVideoFragment videoFragmentBusiness;

        public PagerAdapter(FragmentManager fm) {
            super(fm);
            imageFragmentBusiness = new BusinessImageFragment();
            videoFragmentBusiness = new BusinessVideoFragment();
        }

        @NonNull
        @Override
        public Fragment getItem(int i) {
            if (i == 0) {
                return imageFragmentBusiness;
            } else {
                return videoFragmentBusiness;
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "images";
            } else {
                return "videos";
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}