package community.icb.iqama;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import community.icb.iqama.utilities.Date;

/**
 * Main Activity
 *
 * @author AmrAbed
 */
public class Main extends FragmentActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        ((ViewPager) findViewById(R.id.pager)).setAdapter(new Adapter(getSupportFragmentManager()));

    }

    private class Adapter extends FragmentPagerAdapter
    {

        public Adapter(FragmentManager manager)
        {
            super(manager);
        }

        @Override
        public Fragment getItem(int position)
        {
            return Section.newInstance(position);
        }

        @Override
        public int getCount()
        {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch(position)
            {
                case 0:
                    return Date.today().toString(Date.DEFAULT_FORMAT);
                case 1:
                    return Date.tomorrow().toString(Date.DEFAULT_FORMAT);
            }
            return super.getPageTitle(position);
        }
    }

}
