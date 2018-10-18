package community.icb.iqama.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import community.icb.iqama.R;
import community.icb.iqama.common.Prayers;
import community.icb.iqama.utilities.Date;

/**
 * Content Fragment
 *
 * @author AmrAbed
 */
public class Section extends Fragment {
    private static final String SECTION_NUMBER = "position";

    private int sectionNumber;

    static Section newInstance(int sectionNumber) {
        final Section section = new Section();
        final Bundle args = new Bundle();
        args.putInt(SECTION_NUMBER, sectionNumber);
        section.setArguments(args);
        return section;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sectionNumber = getArguments().getInt(SECTION_NUMBER);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.section, container, false);
        final RecyclerView listView = view.findViewById(R.id.times);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(new Adapter());
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final DateTime date = Date.today().plusDays(sectionNumber);
            final Prayers prayers = new Prayers(getContext(), date);


            if (prayers.isNextPrayer(position)) {
                final int highlight = ContextCompat.getColor(getContext(), R.color.highlight);
                holder.time.setTextColor(highlight);
                holder.arabicLabel.setTextColor(highlight);
                holder.englishLabel.setTextColor(highlight);
            }

            holder.arabicLabel.setText(prayers.getArabicName(position));
            holder.englishLabel.setText(prayers.getEnglishName(position));
            holder.time.setText(prayers.getTime(position));
        }

        @Override
        public int getItemCount() {
            return 5;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView englishLabel, arabicLabel, time;

            ViewHolder(View view) {
                super(view);
                englishLabel = view.findViewById(R.id.label);
                arabicLabel = view.findViewById(R.id.label_ar);
                time = view.findViewById(R.id.iqama);
            }
        }
    }
}
