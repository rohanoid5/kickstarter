package rohan.app.com.kickstarter.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class TaskProvider extends ContentProvider {
    private static final String TAG = TaskProvider.class.getSimpleName();

    private static final int CLEANUP_JOB_ID = 43;

    private static final int PROJECTS = 100;
    private static final int PROJECTS_WITH_ID = 101;

    private DBHelper mDbHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        // content://com.google.developer.taskmaker/tasks
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY,
                Contract.TABLE_PROJECTS,
                PROJECTS);

        // content://com.google.developer.taskmaker/tasks/id
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY,
                Contract.TABLE_PROJECTS + "/#",
                PROJECTS_WITH_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DBHelper(getContext());
        //manageCleanupJob();
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null; /* Not used */
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case PROJECTS:
                retCursor = db.query(Contract.TABLE_PROJECTS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PROJECTS_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String sel = "_id=?";
                String[] selArgs = new String[]{id};
                retCursor = db.query(Contract.TABLE_PROJECTS,
                        projection, sel, selArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri returnUri;

        long id = db.insert(Contract.TABLE_PROJECTS, null, values);
        if (id>0) {
            returnUri = ContentUris.withAppendedId(Contract.CONTENT_URI, id);
        } else
            throw new SQLException("Failed to insert a new row" + id);

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int tasksUpdated;
        int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (match) {
            case PROJECTS_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String sel = "_id=?";
                String[] selArgs = new String[]{id};
                tasksUpdated = db.update(Contract.TABLE_PROJECTS, values, sel, selArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (tasksUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        switch (sUriMatcher.match(uri)) {
            case PROJECTS:
                //Rows aren't counted with null selection
                selection = (selection == null) ? "1" : selection;
                break;
            case PROJECTS_WITH_ID:
                long id = ContentUris.parseId(uri);
                selection = String.format("%s = ?", Contract.ProjectsColumns.SERIAL_NUMBER);
                selectionArgs = new String[]{String.valueOf(id)};
                break;
            default:
                throw new IllegalArgumentException("Illegal delete URI");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count = db.delete(Contract.TABLE_PROJECTS, selection, selectionArgs);

        if (count > 0) {
            //Notify observers of the change
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    /* Initiate a periodic job to clear out completed items */
//    private void manageCleanupJob() {
//        Log.d(TAG, "Scheduling cleanup job");
//        JobScheduler jobScheduler = (JobScheduler) getContext()
//                .getSystemService(Context.JOB_SCHEDULER_SERVICE);
//
//        //Run the job approximately every hour
////        long jobInterval = 900000L;
//          long jobInterval = 3600000L;
//
//        ComponentName jobService = new ComponentName(getContext(), CleanupJobService.class);
//        JobInfo task = new JobInfo.Builder(CLEANUP_JOB_ID, jobService)
//                .setPeriodic(jobInterval)
//                .setPersisted(true)
//                .build();
//
//        if (jobScheduler.schedule(task) != JobScheduler.RESULT_SUCCESS) {
//            Log.e(TAG, "Unable to schedule cleanup job");
//        }
//    }
}
