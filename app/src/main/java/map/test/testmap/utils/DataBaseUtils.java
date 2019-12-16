package map.test.testmap.utils;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.List;

import map.test.testmap.Constants;
import map.test.testmap.model.Line;
import map.test.testmap.model.Notice;
import map.test.testmap.model.Point;

public class DataBaseUtils {

    private static final String TAG = "DataBaseUtils";
    private static volatile DataBaseUtils manager = null;
    private static SQLiteDatabase database;

    private DataBaseUtils(){
        database = LitePal.getDatabase();
    }

    public static DataBaseUtils getInstance(){
        if (manager == null){
            synchronized (DataBaseUtils.class){
                if (manager == null){
                    manager = new DataBaseUtils();
                }
            }
        }
        return manager;
    }

    public void insertMessage(Notice item){
        item.save();
    }

    public void updateMessage(Notice item){
        item.updateAll("messageId = ?", item.getId()+"");
    }

    public void deleteMessage(Notice item){
        LitePal.deleteAll(Notice.class,"messageId = ?", item.getId()+"");
    }

    public List<Notice> findAllMessage(){
        return LitePal.order(" messageId desc").find(Notice.class);
    }

    public List<Notice> findAllUserMessage(){
        return LitePal.where("notice_user_id = ? or notice_user_id = ?",0+"", Constants.userId+"").order(" messageId desc").find(Notice.class);
    }

    public Notice findNewMessage(){
        return LitePal.order(" messageId desc").findFirst(Notice.class);
    }

    public void insertPoint(Point point){
        point.save();
    }

    public void updatePoint(Point point){
        point.updateAll("pointId = ?", point.getId()+"");
    }

    public Point findPoint(int id){
        return LitePal.where("pointId = ?",id+"").findFirst(Point.class);
    }

    public List<Point> findPointInMap(String[] idList){
        return LitePal.find();
    }

    public List<Point> findAllPoint(){
        return LitePal.order(" pointId desc").find(Point.class);
    }

    public void insertLine(Line line){
        line.save();
    }

    public void updateLine(Line line){
        line.updateAll("lineId = ?", line.getId()+"");
    }

    public Line findLine(int id){
        return LitePal.where("lineId = ?",id+"").findFirst(Line.class);
    }
}
