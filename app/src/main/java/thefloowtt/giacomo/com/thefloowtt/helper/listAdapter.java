package thefloowtt.giacomo.com.thefloowtt.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import thefloowtt.giacomo.com.thefloowtt.R;
import thefloowtt.giacomo.com.thefloowtt.journey.Journey;

/**
 * Created by giaco on 29/05/2017.
 */

public class listAdapter extends ArrayAdapter<Journey> {
    private static final String TAG = listAdapter.class.getSimpleName();

    public listAdapter(Context context, int resource, List<Journey> values) {
        super(context, resource, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_row, parent, false);
        TextView name = (TextView) rowView.findViewById(R.id.name);
        TextView duration = (TextView) rowView.findViewById(R.id.duration);
        TextView distance = (TextView) rowView.findViewById(R.id.distance);
        TextView startTime = (TextView) rowView.findViewById(R.id.start_time);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        Journey jou = getItem(position);
        name.setText("Journey "+String.valueOf(position+1));
        int minutes = (int) ((jou.getDuration() / (1000*60)) % 60);
        duration.setText(String.valueOf(minutes)+" Min");
        DecimalFormat df = new DecimalFormat("###.##");
        distance.setText(String.valueOf( df.format(jou.getDistance() * .001))+" Km");
        String dateJou = jou.getStartDate();
        SimpleDateFormat format = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
                Locale.ENGLISH);        try {
            Date dateJouFor = format.parse(dateJou);
            startTime.setText(String.valueOf(dateJouFor.getHours())+":"+String.valueOf(dateJouFor.getMinutes()));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rowView;
    }
}
