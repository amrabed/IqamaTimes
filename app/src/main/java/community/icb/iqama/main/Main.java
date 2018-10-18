package community.icb.iqama.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import community.icb.iqama.R;
import community.icb.iqama.calendar.Settings;
import community.icb.iqama.utilities.Date;

/**
 * Main Activity
 *
 * @author AmrAbed
 */
public class Main extends FragmentActivity implements View.OnClickListener,
        DialogInterface.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        ((ViewPager) findViewById(R.id.pager)).setAdapter(new Adapter(getSupportFragmentManager()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, Settings.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        new AlertDialog.Builder(this).setTitle(R.string.support).setItems(R.array.support, this)
                .create().show();
    }

    @Override
    public void onClick(DialogInterface dialog, int i) {
        final String[] ids = {"5T6WN83XVX3BC", "N46UMV92GCUD2", "Y75AT2XMDNZ3G"};
        final Uri uri = Uri
                .parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=" + ids[i]);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    private class Adapter extends FragmentPagerAdapter {

        Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return Section.newInstance(position);
        }

        @Override
        public int getCount() {
            return 31;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Date.today().plusDays(position).toString(Date.DEFAULT_FORMAT);
        }
    }
}
