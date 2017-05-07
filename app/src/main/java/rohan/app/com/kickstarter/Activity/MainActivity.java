package rohan.app.com.kickstarter.Activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.claudiodegio.msv.BaseMaterialSearchView;
import com.claudiodegio.msv.OnSearchViewListener;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rohan.app.com.kickstarter.Adapter.ProjectAdapter;
import rohan.app.com.kickstarter.Data.Contract;
import rohan.app.com.kickstarter.Data.TaskUpdateService;
import rohan.app.com.kickstarter.Model.Project;
import rohan.app.com.kickstarter.Network.APIService;
import rohan.app.com.kickstarter.Network.ServiceFactory;
import rohan.app.com.kickstarter.R;
import rohan.app.com.kickstarter.Util.ClickListener;
import rohan.app.com.kickstarter.Util.RecyclerTouchListener;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.R.attr.x;
import static android.R.id.text1;
import static rohan.app.com.kickstarter.R.id.cl;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ProjectAdapter.OnItemClickListener,
        OnSearchViewListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;
    ProjectAdapter mAdapter;
    private Uri uri;
    private List<Integer> xList = new ArrayList<>();
    private List<Integer> yList = new ArrayList<>();

    @Bind(R.id.sv)
    BaseMaterialSearchView mSearchView;
    @Bind(cl)
    CoordinatorLayout codLayout;
    @Bind(R.id.graph)
    CardView graph;
    @Bind(R.id.chart)
    ScatterChart chart;
    @Bind(R.id.close_button)
    AppCompatButton closeButton;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private String pattern = "%";
    private boolean closed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
        getSupportActionBar().setElevation(0);

        mAdapter = new ProjectAdapter(null, this, this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Cursor c = getContentResolver().query(Contract.CONTENT_URI, null, null, null, null);
        if((c != null ? c.getCount() : 0) == 0) {
            networkCall();
        } else {
            getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
        }
        if (c != null) {
            c.close();
        }

        mSearchView.setOnSearchViewListener(this);
        chart.getDescription().setEnabled(false);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graph.setVisibility(View.GONE);
                codLayout.setAlpha(1);
                recyclerView.setClickable(true);
            }
        });

    }

    public void networkCall() {
        final APIService apiService = ServiceFactory.provideService();
        Call<List<Project>> getAllProjects = apiService.getAllProjects();

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this, R.style.MyDialogBox);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Checking for data...");
        progressDialog.show();

        getAllProjects.enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                if (response.isSuccessful()) {
                    //Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    List<Project> res = response.body();
                    for (Project p : res) {
                        ContentValues values = new ContentValues();

                        String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
                        Date date;
                        String dateString = "";
                        long endMills;
                        try {
                            date = new SimpleDateFormat(pattern).parse(p.getEndTime());
                            dateString = new SimpleDateFormat("yyyy/MM/dd").format(date);
                            endMills = date.getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        values.put(Contract.ProjectsColumns.SERIAL_NUMBER, p.getSNo());
                        values.put(Contract.ProjectsColumns.AMT_PLEDGED, p.getAmtPledged());
                        values.put(Contract.ProjectsColumns.BLURB, p.getBlurb());
                        values.put(Contract.ProjectsColumns.BY, p.getBy());
                        values.put(Contract.ProjectsColumns.COUNTRY, p.getCountry());
                        values.put(Contract.ProjectsColumns.CURRENCY, p.getCurrency());
                        values.put(Contract.ProjectsColumns.LOCATION, p.getLocation());
                        values.put(Contract.ProjectsColumns.END_TIME, p.getEndTime());
                        values.put(Contract.ProjectsColumns.PERCENTAGE_FUNDED, p.getPercentageFunded());
                        values.put(Contract.ProjectsColumns.TITLE, p.getTitle());
                        values.put(Contract.ProjectsColumns.TYPE, p.getType());
                        values.put(Contract.ProjectsColumns.STATE, p.getState());
                        values.put(Contract.ProjectsColumns.URL, p.getUrl());
                        values.put(Contract.ProjectsColumns.NUM_BACKERS, p.getNumBackers());
                        TaskUpdateService.insertNewTask(MainActivity.this, values);
                        getContentResolver().notifyChange(Contract.CONTENT_URI, null);
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            startActivity(new Intent(MainActivity.this, MainActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        }
                    }, 1500);
                } else {
                    progressDialog.dismiss();
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Please ensure you have a Internet Connection!")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {
                progressDialog.dismiss();
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Please ensure you have a Internet Connection!")
                        .show();
                Log.e(TAG, String.valueOf(t));
            }
        });
    }

    public static boolean isDefaultSort(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String keyForSorting = context.getString(R.string.pref_sortBy_key);
        String defaultSort = context.getString(R.string.pref_sortBy_default);
        String preferredSort = sp.getString(keyForSorting, defaultSort);
        String def = context.getString(R.string.pref_sortBy_default);

        boolean userPrefersDef = false;
        if (def.equals(preferredSort)) {
            userPrefersDef = true;
        }
        return userPrefersDef;
    }

    public static boolean isDefaultFiltered(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String keyForFiltering = context.getString(R.string.pref_filterBy_key);
        String defaultFilter = context.getString(R.string.pref_filterBy_more);
        String preferredSort = sp.getString(keyForFiltering, defaultFilter);
        String def = context.getString(R.string.pref_filterBy_more);

        boolean userPrefersDef = false;
        if (def.equals(preferredSort)) {
            userPrefersDef = true;
        }
        return userPrefersDef;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mSearchView.setMenuItem(menu.findItem(R.id.action_search));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_graph) {
            getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
            List<Entry> entries = new ArrayList<Entry>();
            Integer arr[] = new Integer[xList.size()];

            YAxis yr = chart.getAxisRight();
            yr.setEnabled(false);
            yr.setDrawAxisLine(false);

            YAxis yl = chart.getAxisLeft();
            yl.setDrawLabels(true);
            yl.setDrawAxisLine(true);
            yl.setDrawGridLines(false);
            XAxis xl = chart.getXAxis();
            xl.setPosition(XAxis.XAxisPosition.BOTTOM);
            xl.setGridColor(Color.BLUE);
            xl.setDrawGridLines(false);
            xl.setTextColor(Color.BLUE);
            xl.setDrawAxisLine(true);
            xl.setDrawLabels(true);
            xl.setTextSize(10f);
            xl.setTextColor(Color.BLACK);

            for (int i = 0; i < arr.length; i++) {
                entries.add(new Entry(xList.get(i), yList.get(i)));
            }

            final ScatterDataSet dataSet1 = new ScatterDataSet(entries, "Data");
            dataSet1.setColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary300));
            dataSet1.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
//            dataSet1.setDrawCircles(false);
//            dataSet1.setLineWidth(4);
//            dataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//            dataSet1.setHighLightColor(Color.WHITE);
//            dataSet1.setDrawHorizontalHighlightIndicator(false);
//            dataSet1.setHighlightEnabled(true);
//
//            dataSet1.setDrawFilled(true);
//            dataSet1.setFillDrawable(getResources().getDrawable(R.drawable.dark_gradient));
            // lineDataSets.add(dataSet1);
            ScatterData lineData = new ScatterData(dataSet1);
            //  lineData.setXVals(Arrays.asList(xAxisLabels));
            lineData.setDrawValues(false);
            chart.setData(lineData);
            codLayout.setAlpha(0.5f);
            graph.setVisibility(View.VISIBLE);
            recyclerView.setClickable(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mTaskData = null;

            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    deliverResult(mTaskData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {

                try {
                    if (!closed) {
                        if (isDefaultSort(MainActivity.this) && isDefaultFiltered(MainActivity.this))
                            return getContentResolver().query(Contract.CONTENT_URI,
                                    null,
                                    "amt_pledged > ?",
                                    new String[]{"100000"},
                                    Contract.DEFAULT_SORT);
                        else if (!isDefaultSort(MainActivity.this) && isDefaultFiltered(MainActivity.this))
                            return getContentResolver().query(Contract.CONTENT_URI,
                                    null,
                                    "amt_pledged > ?",
                                    new String[]{"100000"},
                                    Contract.DATE_SORT);
                        else if (isDefaultSort(MainActivity.this) && !isDefaultFiltered(MainActivity.this))
                            return getContentResolver().query(Contract.CONTENT_URI,
                                    null,
                                    "amt_pledged < ?",
                                    new String[]{"100001"},
                                    Contract.DEFAULT_SORT);
                        else if (!isDefaultSort(MainActivity.this) && !isDefaultFiltered(MainActivity.this))
                            return getContentResolver().query(Contract.CONTENT_URI,
                                    null,
                                    "amt_pledged < ?",
                                    new String[]{"100001"},
                                    Contract.DATE_SORT);
                    } else {
                        return getContentResolver().query(Contract.CONTENT_URI,
                                null,
                                "title like ?",
                                new String[]{pattern},
                                Contract.DATE_SORT);
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage().toString());
                    e.printStackTrace();
                    return null;
                }
                return null;
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mAdapter.swapCursor(data);

        String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
        Date date;
        String dateString = "";
        long endMills = 1;

        try {
            while (data.moveToNext()) {
                try {
                    date = new SimpleDateFormat(pattern).parse(Contract.getColumnString(data, Contract.ProjectsColumns.END_TIME));
                    endMills = date.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                xList.add(Contract.getColumnInt(data, Contract.ProjectsColumns.NUM_BACKERS));
                yList.add(Contract.getColumnInt(data, Contract.ProjectsColumns.AMT_PLEDGED));
            }
        } finally {

        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(View v, int position) {
        int id = (int) v.getTag();
//        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
        Log.e(TAG, String.valueOf(id));
        String stringId = Integer.toString(id);
        uri = Contract.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        Intent intent = new Intent(this, ProjectDetailsActivity.class);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onSearchViewShown() {
        closed = false;
    }

    @Override
    public void onSearchViewClosed() {
        closed = true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {

        if (s.equals("")) {
            closed = false;
            getContentResolver().notifyChange(Contract.CONTENT_URI, null);
            getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
            return false;
        }

        closed = true;
        pattern = s + "%";
        getContentResolver().notifyChange(Contract.CONTENT_URI, null);
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
        return false;
    }

    @Override
    public void onQueryTextChange(String s) {

    }
}
