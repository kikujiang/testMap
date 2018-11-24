package map.test.testmap;

import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import map.test.testmap.model.OnInfoListener;
import map.test.testmap.utils.OkHttpClientManager;
import okhttp3.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private static final String TAG = "test";

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    String url = "";
    Map<String,String> data = null;
    OnInfoListener listener;

    @Before
    public void init(){
        url = "http://10.206.2.29:8088/SystemTestPlatform/platform/mobileTest/index.do";
        data = new HashMap<>();
        data.put("act","getResUrl");
    }

    @Test
    public void test(){
        System.out.print("test: ");
        assertEquals(4, 2 + 2);
        OkHttpClientManager.getInstance().post(url, data, new OnInfoListener() {
            @Override
            public void success(Response responseMapBean) {
                System.out.print("success: "+responseMapBean.toString());
                assertNotNull(responseMapBean);
            }

            @Override
            public void fail(Exception e) {
                System.out.print("fail: "+e.getMessage());
            }
        });
    }

    @After
    public void end(){

    }
}