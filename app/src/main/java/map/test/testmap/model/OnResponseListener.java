package map.test.testmap.model;

public interface OnResponseListener {
    public void success(retrofit2.Response responseMapBean);
    public void fail(Throwable e);
}
