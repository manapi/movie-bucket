package ift2905.moviebucket;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;

/**
 * Created by Amélie on 2017-04-22.
 */

public class SearchPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 4;

    public SearchPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        /*switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return null; //FirstFragment.newInstance(0, "Page # 1");
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return null; //FirstFragment.newInstance(1, "Page # 2");
            case 2: // Fragment # 1 - This will show SecondFragment
                return null; //SecondFragment.newInstance(2, "Page # 3");
            case 3: // Fragment # 2 - this will whow the ThirdFragment
                return null; //ThirdFragment.newInstance(3, "Page # 4");
            default:
                return null;
        }*/
        return new Fragment();
    }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

}
