package community.icb.iqama;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

/**
 * Main Activity
 *
 * @author AmrAbed
 */
public class Main extends Activity implements DateHandler.Listener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        new DateHandler(this, (TextView) findViewById(R.id.date), this);
    }

    @Override
    public void onDateChanged(DateTime newDate)
    {
        // Show Iqama Times
        final RecyclerView listView = (RecyclerView) findViewById(R.id.times);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(new Adapter(newDate));
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>
    {
        private final String[] iqamaTimes;

        private Adapter(DateTime date)
        {
            iqamaTimes = IqamaTimes.get(date);
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position)
        {
            holder.label.setText(getResources().getStringArray(R.array.prayers)[position]);
            holder.label2.setText(getResources().getStringArray(R.array.prayers_ar)[position]);
            holder.time.setText(iqamaTimes[position]);
        }

        @Override
        public int getItemCount()
        {
            return 5;
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView label, label2, time;

            public ViewHolder(View view)
            {
                super(view);
                label = (TextView) view.findViewById(R.id.label);
                label2 = (TextView) view.findViewById(R.id.label_ar);
                time = (TextView) view.findViewById(R.id.iqama);
            }
        }
    }
}
