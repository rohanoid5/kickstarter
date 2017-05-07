package rohan.app.com.kickstarter.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import rohan.app.com.kickstarter.Data.Contract;
import rohan.app.com.kickstarter.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.R.attr.description;
import static android.R.attr.priority;

public class ProjectDetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.blurb)
    TextView blurb;
    @Bind(R.id.by)
    TextView by;
    @Bind(R.id.location)
    TextView location;
    @Bind(R.id.country)
    TextView country;
    @Bind(R.id.state)
    TextView state;
    @Bind(R.id.type)
    TextView type;
    @Bind(R.id.amt_pledged)
    TextView amtPledged;
    @Bind(R.id.backed)
    TextView backed;
    @Bind(R.id.url_button)
    AppCompatButton urlButton;


    private static final String TAG = ProjectDetailsActivity.class.getSimpleName();
    private static final int ID_DETAIL_LOADER = 353;

    private Uri taskUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        taskUri = getIntent().getData();
        if (taskUri == null) throw new NullPointerException("URI for TaskDetailActivity cannot be null");

        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_DETAIL_LOADER:
                return new CursorLoader(this,
                        taskUri,
                        null,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            return;
        }

        getSupportActionBar().setTitle(Contract.getColumnString(data, Contract.ProjectsColumns.TITLE));
        blurb.setText(Contract.getColumnString(data, Contract.ProjectsColumns.BLURB));
        by.setText("By " + Contract.getColumnString(data, Contract.ProjectsColumns.BY));
        country.setText(Contract.getColumnString(data, Contract.ProjectsColumns.COUNTRY));
        location.setText(Contract.getColumnString(data, Contract.ProjectsColumns.LOCATION));
        state.setText(Contract.getColumnString(data, Contract.ProjectsColumns.STATE));
        type.setText(Contract.getColumnString(data, Contract.ProjectsColumns.TYPE));
        amtPledged.setText(Contract.getColumnString(data, Contract.ProjectsColumns.AMT_PLEDGED));
        backed.setText(Contract.getColumnString(data, Contract.ProjectsColumns.NUM_BACKERS));

        urlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = Contract.getColumnString(data, Contract.ProjectsColumns.URL);
                String baseUrl = "https://www.kickstarter.com"+url;
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(ProjectDetailsActivity.this, Uri.parse(baseUrl));
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
           super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
