
package rohan.app.com.kickstarter.Model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import rohan.app.com.kickstarter.Data.Contract;

import static rohan.app.com.kickstarter.Data.Contract.getColumnInt;
import static rohan.app.com.kickstarter.Data.Contract.getColumnLong;
import static rohan.app.com.kickstarter.Data.Contract.getColumnString;

public class Project implements Parcelable {

    public long id;
    @SerializedName("s.no")
    @Expose
    private Integer sNo;
    @SerializedName("amt.pledged")
    @Expose
    private Integer amtPledged;
    @SerializedName("blurb")
    @Expose
    private String blurb;
    @SerializedName("by")
    @Expose
    private String by;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("end.time")
    @Expose
    private String endTime;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("percentage.funded")
    @Expose
    private Integer percentageFunded;
    @SerializedName("num.backers")
    @Expose
    private String numBackers;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("url")
    @Expose
    private String url;

    protected Project(Parcel in) {
        id = in.readLong();
        blurb = in.readString();
        by = in.readString();
        country = in.readString();
        currency = in.readString();
        endTime = in.readString();
        location = in.readString();
        numBackers = in.readString();
        state = in.readString();
        title = in.readString();
        type = in.readString();
        url = in.readString();
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public long getId() {
        return id;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(blurb);
        parcel.writeString(by);
        parcel.writeString(country);
        parcel.writeString(currency);
        parcel.writeString(title);
        parcel.writeString(endTime);
        parcel.writeString(location);
        parcel.writeString(state);
        parcel.writeString(url);
        parcel.writeString(numBackers);
        parcel.writeInt(percentageFunded);
        parcel.writeInt(sNo);
        parcel.writeInt(amtPledged);
        parcel.writeString(type);
    }

    public Project(Cursor mCursor) {
        this.id = getColumnInt(mCursor, Contract.ProjectsColumns._ID);
        this.sNo = getColumnInt(mCursor, Contract.ProjectsColumns.SERIAL_NUMBER);
        this.amtPledged = getColumnInt(mCursor, Contract.ProjectsColumns.AMT_PLEDGED);
        this.blurb = getColumnString(mCursor, Contract.ProjectsColumns.BLURB);
        this.by = getColumnString(mCursor, Contract.ProjectsColumns.BY);
        this.country = getColumnString(mCursor, Contract.ProjectsColumns.COUNTRY);
        this.currency = getColumnString(mCursor, Contract.ProjectsColumns.CURRENCY);
        this.endTime = getColumnString(mCursor, Contract.ProjectsColumns.END_TIME);
        this.title = getColumnString(mCursor, Contract.ProjectsColumns.TITLE);
        this.location = getColumnString(mCursor, Contract.ProjectsColumns.LOCATION);
        this.percentageFunded = getColumnInt(mCursor, Contract.ProjectsColumns.PERCENTAGE_FUNDED);
        this.numBackers = getColumnString(mCursor, Contract.ProjectsColumns.NUM_BACKERS);
        this.state = getColumnString(mCursor, Contract.ProjectsColumns.STATE);
        this.type = getColumnString(mCursor, Contract.ProjectsColumns.TYPE);
        this.url = getColumnString(mCursor, Contract.ProjectsColumns.URL);
    }

    public Integer getSNo() {
        return sNo;
    }

    public void setSNo(Integer sNo) {
        this.sNo = sNo;
    }

    public Integer getAmtPledged() {
        return amtPledged;
    }

    public void setAmtPledged(Integer amtPledged) {
        this.amtPledged = amtPledged;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getPercentageFunded() {
        return percentageFunded;
    }

    public void setPercentageFunded(Integer percentageFunded) {
        this.percentageFunded = percentageFunded;
    }

    public String getNumBackers() {
        return numBackers;
    }

    public void setNumBackers(String numBackers) {
        this.numBackers = numBackers;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
