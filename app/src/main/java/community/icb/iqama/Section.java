package community.icb.iqama;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import community.icb.iqama.utilities.Date;
import community.icb.iqama.utilities.IqamaTimes;

/**
 * Content Fragment
 *
 * @author AmrAbed
 */
public class Section extends Fragment
{
    private static final String SECTION_NUMBER = "position";

    private int sectionNumber;

    public static Section newInstance(int sectionNumber)
    {
        final Section section = new Section();
        final Bundle args = new Bundle();
        args.putInt(SECTION_NUMBER, sectionNumber);
        section.setArguments(args);
        return section;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            sectionNumber = getArguments().getInt(SECTION_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.section, container, false);
        final RecyclerView listView = (RecyclerView) view.findViewById(R.id.times);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(new Adapter());
        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>
    {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position)
        {
            holder.englishLabel.setText(getResources().getStringArray(R.array.prayers_en)[position]);
            holder.arabicLabel.setText(getResources().getStringArray(R.array.prayers_ar)[position]);

            final String[] iqamaTimes = IqamaTimes.get(Date.today().plusDays(sectionNumber));
            holder.time.setText(iqamaTimes[position]);
        }

        @Override
        public int getItemCount()
        {
            return 5;
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView englishLabel, arabicLabel, time;

            public ViewHolder(View view)
            {
                super(view);
                englishLabel = (TextView) view.findViewById(R.id.label);
                arabicLabel = (TextView) view.findViewById(R.id.label_ar);
                time = (TextView) view.findViewById(R.id.iqama);
            }
        }
    }
}
