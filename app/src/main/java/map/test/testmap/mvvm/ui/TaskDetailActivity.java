package map.test.testmap.mvvm.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import map.test.testmap.R;
import map.test.testmap.databinding.ActivityTaskDetailBinding;

public class TaskDetailActivity extends AppCompatActivity {

    private ActivityTaskDetailBinding binding = null;
    private int checkId;
    private int pointId;
    private static final String TAG = "task_detail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkId = getIntent().getIntExtra("checkId",0);
        pointId = getIntent().getIntExtra("pointId",0);
        Log.d(TAG, "onCreate: checkId is:"+checkId+",and point id is:"+pointId);
        initial();
    }

    private void initial(){
        binding = DataBindingUtil.setContentView(this,R.layout.activity_task_detail);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TaskDetailFragment taskDetailFragment = TaskDetailFragment.newInstance(checkId);
        PointDetailFragment pointDetailFragment = PointDetailFragment.newInstance(pointId);
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_task_detail,taskDetailFragment).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_point_detail,pointDetailFragment).commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //重写ToolBar返回按钮的行为，防止重新打开父Activity重走生命周期方法
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
