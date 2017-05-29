package thefloowtt.giacomo.com.thefloowtt.journey;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

import thefloowtt.giacomo.com.thefloowtt.MainActivity;
import thefloowtt.giacomo.com.thefloowtt.R;
import thefloowtt.giacomo.com.thefloowtt.Settings;
import thefloowtt.giacomo.com.thefloowtt.helper.MySQLiteHelper;
import thefloowtt.giacomo.com.thefloowtt.helper.listAdapter;

public class showJourneys extends AppCompatActivity {
    private ProgressBar loading;
    private MySQLiteHelper db;
    private static final String TAG = showJourneys.class.getSimpleName();
    private ListView listJou;
    private static Context mContext;
    private List<Journey> journeys;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_journeys);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        db = new MySQLiteHelper(getApplicationContext());
        listJou = (ListView) findViewById(R.id.list);
        loading = (ProgressBar)findViewById(R.id.loadingBar);
        new getJourneys().execute("");
        listJou.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent myIntent = new Intent(showJourneys.this, displayJourney.class);
                Journey jou = journeys.get(position);
                myIntent.putExtra("journeyId", jou.getId()); //Optional parameters
                showJourneys.this.startActivity(myIntent);            }
        });
    }
    public class getJourneys extends AsyncTask<Object, Void, List<Journey> >{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }
        // This is run in a background thread
        @Override
        protected List<Journey> doInBackground(Object... objects) {
            journeys = db.getAllJourneys();
            return journeys;
        }
        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(List<Journey> jous){
            super.onPostExecute(jous);
            int count = jous.size();
            Log.i(TAG, "count = " + count);
            Log.i(TAG, "am in PostExecute now");
            listAdapter adapter = new listAdapter(getApplicationContext(),R.layout.list_row, jous);
            listJou.setAdapter(adapter);
            loading.setVisibility(View.INVISIBLE);
            // Do things like hide the progress bar or change a TextView
            adapter.notifyDataSetChanged();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
