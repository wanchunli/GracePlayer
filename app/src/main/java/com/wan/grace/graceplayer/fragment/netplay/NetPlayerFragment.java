package com.wan.grace.graceplayer.fragment.netplay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wan.grace.graceplayer.R;
import com.wan.grace.graceplayer.base.MVPBaseFragment;

public class NetPlayerFragment extends MVPBaseFragment<NetPlayView,NetPlayPresenter>
        implements NetPlayView{

    public NetPlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NetPlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NetPlayerFragment newInstance(String param1, String param2) {
        NetPlayerFragment fragment = new NetPlayerFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    protected NetPlayPresenter createPresenter() {
        return new NetPlayPresenter(getActivity());
    }

    @Override
    protected int createViewLayoutId() {
        return R.layout.fragment_net_player;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(View rootView) {

    }
}
