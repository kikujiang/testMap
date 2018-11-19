package map.test.testmap.model;

import okhttp3.Response;

public interface OnInfoListener {
    public void success(Response responseMapBean);
    public void fail(Exception e);
}
