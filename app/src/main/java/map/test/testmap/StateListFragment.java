package map.test.testmap;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import map.test.testmap.model.State;
import map.test.testmap.model.StateAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StateListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StateListFragment extends ListFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DATA = "stateData";
    private OnTouchListener mCallback;

    private boolean isAddFooter = false;

    // TODO: Rename and change types of parameters
    private ArrayList<State> list;

    public StateListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StateListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StateListFragment newInstance(ArrayList<State> dataList) {
        StateListFragment fragment = new StateListFragment();
        Bundle data = new Bundle();
        Log.d(ARG_DATA, "newInstance: list size is:" + dataList.size());
        data.putParcelableArrayList("list",dataList);
        fragment.setArguments(data);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(ARG_DATA, "onCreateView: ``11111");
        if (getArguments() != null) {
            Log.d(ARG_DATA, "onCreate: ");
            list = getArguments().getParcelableArrayList("list");

            if(list.size() > 0){
                if(list.get(0).getStatus() != 1){
                    isAddFooter = true;
                }else{
                    isAddFooter = false;
                }
            }

            StateAdapter adapter = new StateAdapter(getActivity(),list);
            setListAdapter(adapter);
        }

        return inflater.inflate(R.layout.fragment_state_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(ARG_DATA, "onStart: ");

        if (!isAddFooter){
            isAddFooter = true;
            Button btnFooter = new Button(getActivity());
            btnFooter.setText("新增故障");
            btnFooter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.addClick();
                }
            });
            getListView().addFooterView(btnFooter);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(ARG_DATA, "onListItemClick: position is:" + position);
        State current = list.get(position);
        mCallback.setData(current);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnTouchListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TextClicked");
        }
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

    public interface OnTouchListener{
        void setData(State current);
        void addClick();
    }
}