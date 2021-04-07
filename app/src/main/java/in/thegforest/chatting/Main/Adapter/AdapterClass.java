package in.thegforest.chatting.Main.Adapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentPagerAdapter;


import java.util.ArrayList;
public class AdapterClass extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments;
    private ArrayList<String> title;
    public AdapterClass(@NonNull FragmentManager fm) {
        super(fm);
        this.fragments=new ArrayList<>();
        this.title=new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title.get(position);
    }
    public void addFragment(Fragment fragment,String titles){
        fragments.add(fragment);
        title.add(titles);
    }
}