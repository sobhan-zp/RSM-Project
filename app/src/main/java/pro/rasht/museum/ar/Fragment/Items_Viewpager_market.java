package pro.rasht.museum.ar.Fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by My Hp on 10/20/2016.
 */
public class Items_Viewpager_market extends FragmentPagerAdapter {


    public Items_Viewpager_market(FragmentManager fm) {
        super(fm);
       
    }

    @Override
    public int getCount() {
        return 5;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FragmentHome.newInstance();
            case 1:
                return FragmentGallery.newInstance();
            case 2:
                return FragmentMusic.newInstance();
            case 4:
                return FragmentProfile.newInstance();
            default:
                return FragmentHome.newInstance();
        }
    }

}
