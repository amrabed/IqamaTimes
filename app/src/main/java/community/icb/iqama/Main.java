package community.icb.iqama;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import community.icb.iqama.utilities.Date;

/**
 * Main Activity
 *
 * @author AmrAbed
 */
public class Main extends FragmentActivity
		implements View.OnClickListener, DialogInterface.OnClickListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		((ViewPager) findViewById(R.id.pager)).setAdapter(new Adapter(getSupportFragmentManager()));
	}

	@Override
	public void onClick(View v)
	{
		new AlertDialog.Builder(this).setTitle(R.string.support).setItems(R.array.support, this)
				.create().show();
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
			return 100;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			return Date.today().plusDays(position).toString(Date.DEFAULT_FORMAT);
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int i)
	{
		final String[] ids = {"5T6WN83XVX3BC", "N46UMV92GCUD2", "Y75AT2XMDNZ3G"};
		final Uri uri = Uri
				.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=" + ids[i]);
		startActivity(new Intent(Intent.ACTION_VIEW, uri));
	}

}
