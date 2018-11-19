package map.test.testmap.model;

public interface IMapBiz {

    public void getALLInfo(OnInfoListener listener);

    public void addPoint(Point point,OnInfoListener listener);

    public void editPoint(Point point,OnInfoListener listener);
}
