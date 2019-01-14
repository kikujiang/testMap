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
        data.putParcelableArrayList("list",dataList);
//        data.put
//        args.arr(ARG_DATA, param1);
//        args.putStringArrayList();
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
        Log.d(ARG_DATA, "onCreate: ``11111");
        if (getArguments() != null) {
            Log.d(ARG_DATA, "onCreate: ");
            list = getArguments().getParcelableArrayList("list");
            StateAdapter adapter = new StateAdapter(getActivity(),list);
            setListAdapter(adapter);
        }
        return inflater.inflate(R.layout.fragment_state_list, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}