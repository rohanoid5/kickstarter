package rohan.app.com.kickstarter.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import rohan.app.com.kickstarter.Activity.MainActivity;
import rohan.app.com.kickstarter.Data.Contract;
import rohan.app.com.kickstarter.Model.Project;
import rohan.app.com.kickstarter.R;

/**
 * Created by rohan on 06-05-2017.
 */

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectHolder> {

    private static final String TAG = ProjectAdapter.class.getSimpleName();
    private Context mContext;
    private Cursor mCursor;
    private OnItemClickListener mOnItemClickListener;
    private String curSymbol = "$";

    /* Callback for list item click events */
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public ProjectAdapter(Cursor cursor, Context mContext, OnItemClickListener mOnItemClickListener) {
        mCursor = cursor;
        this.mContext = mContext;
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public ProjectAdapter.ProjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_projects, parent, false);

        return new ProjectHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProjectAdapter.ProjectHolder holder, int position) {
        if(!mCursor.moveToPosition(position))
            return;

        int idIndex = mCursor.getColumnIndex(Contract.ProjectsColumns._ID);
        final int id = mCursor.getInt(idIndex);
        holder.itemView.setTag(id);

//        Log.e(TAG, Contract.getColumnString(mCursor, Contract.ProjectsColumns.TITLE));
        String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
        Date date;
        String dateString = "";
        long endMills;
        try {
            date = new SimpleDateFormat(pattern).parse(Contract.getColumnString(mCursor, Contract.ProjectsColumns.END_TIME));
            dateString = new SimpleDateFormat("yyyy/MM/dd").format(date);
            endMills = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int numBackers = Integer.parseInt(Contract.getColumnString(mCursor, Contract.ProjectsColumns.NUM_BACKERS));
        int amtPledge = Integer.parseInt(Contract.getColumnString(mCursor, Contract.ProjectsColumns.AMT_PLEDGED));
        DecimalFormat formatter = new DecimalFormat("#,###");

        if (Contract.getColumnString(mCursor, Contract.ProjectsColumns.CURRENCY).equals("eur"))
            curSymbol = "€";
        else if (Contract.getColumnString(mCursor, Contract.ProjectsColumns.CURRENCY).equals("gbp"))
            curSymbol = "£";
        else
            curSymbol = "$";

        holder.title.setText(Contract.getColumnString(mCursor, Contract.ProjectsColumns.TITLE));
        holder.amtPledge.setText(curSymbol + formatter.format(amtPledge));
        holder.backers.setText(formatter.format(numBackers));
        holder.endDate.setText(dateString);
        holder.fundPercentage.setText(Contract.getColumnString(mCursor, Contract.ProjectsColumns.PERCENTAGE_FUNDED) + "%");
        holder.progressBar.setProgress(Contract.getColumnInt(mCursor, Contract.ProjectsColumns.PERCENTAGE_FUNDED));
    }

    @Override
    public int getItemCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    /**
     * Retrieve a {@link Project} for the data at the given position.
     *
     * @param position Adapter item position.
     *
     * @return A new {@link Project} filled with the position's attributes.
     */
    public Project getItem(int position) {
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Invalid item position requested");
        }
        return new Project(mCursor);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    public void swapCursor(Cursor cursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public class ProjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.amt_pledge)
        TextView amtPledge;
        @Bind(R.id.end_date)
        TextView endDate;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.backers)
        TextView backers;
        @Bind(R.id.perc_bar)
        ProgressBar progressBar;
        @Bind(R.id.fund_percentage)
        TextView fundPercentage;

        public ProjectHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            postItemClick(this);
        }
    }

    private void postItemClick(ProjectHolder holder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(holder.itemView, holder.getAdapterPosition());
        }
    }
}
