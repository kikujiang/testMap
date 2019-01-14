package map.test.testmap.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class State implements Parcelable {

    private String operator;
    private String checkTime;
    private int checkState;
    private String remark;
    private List<String> imageList;


    public State(String o,String t,int s,String r,List<String> images){
        this.operator = o;
        this.checkTime = t;
        this.checkState = s;
        this.remark = r;
        this.imageList = images;
    }

    protected State(Parcel in) {
        operator = in.readString();
        checkTime = in.readString();
        checkState = in.readInt();
        remark = in.readString();
        imageList = in.readArrayList(null);
    }

    public static final Creator<State> CREATOR = new Creator<State>() {
        @Override
        public State createFromParcel(Parcel in) {
            return new State(in);
        }

        @Override
        public State[] newArray(int size) {
            return new State[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(operator);
        dest.writeString(checkTime);
        dest.writeInt(checkState);
        dest.writeString(remark);
        dest.writeList(imageList);
    }

    public String getOperator() {
        return operator;
    }

    public String getCheckTime() {
        return checkTime;
    }

    public int getCheckState() {
        return checkState;
    }

    public String getRemark() {
        return remark;
    }

    public List<String> getImageList() {
        return imageList;
    }
}
