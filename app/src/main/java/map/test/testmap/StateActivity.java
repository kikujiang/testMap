package map.test.testmap;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import map.test.testmap.model.State;

public class StateActivity extends AppCompatActivity {

    private Fragment detailFragment;
    private Fragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);
        init();
    }

    private void init() {
        initData();
        FragmentManager manager = getSupportFragmentManager();
        detailFragment = manager.findFragmentById(R.id.fragment_detail);
        listFragment = manager.findFragmentById(R.id.fragment_list);

        if(detailFragment == null){
            detailFragment = Detail.newInstance("");
        }

        if(listFragment == null){
            listFragment = StateListFragment.newInstance(stateList);
        }
    }

    private ArrayList<State> stateList;
    private void initData(){
        stateList = new ArrayList<>();
        for(int i=0;i<6;i++){
            State state = new State("hehe","2019:01:14",i,"haha"+i,null);
            stateList.add(state);
        }
    }
}
