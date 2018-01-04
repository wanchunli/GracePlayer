package com.wan.grace.graceplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.wan.grace.graceplayer.utils.CommonViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/24.
 */
public class BaseAdapter<T> extends RecyclerView.Adapter<CommonViewHolder>{
    public final static int STATUS_LOADING_MORE = 0;//加载更多
    public final static int  STATUS_EMPTY = 1;//数据为空
    public final static int STATUS_LOADING = 2;//正在加载
    public final static int STATUS_LOAD_ALL = 3;//加载完成

    public int loadingstatus = 0;//加载状态
    public final int ITEM_FOOTER = 2;
    public final int ITEM_CONTENT = 1;
    private List<T> list;
    protected Context context;
    private OnItemClickListener onItemClickListener;
    private OnCheckItemClickListener onCheckItemClickListener;
    private OnItemDeleteClickListener onItemDeleteClickListener;

    public void setLoadingstatus(int loadingstatus) {
        this.loadingstatus = loadingstatus;
    }


    public String getLoadingStatus(){
        if(loadingstatus == 1){
            return "数据为空";
        }else if(loadingstatus == 2){
            return "正在加载...";
        }else if(loadingstatus == 3){
            return "已加载全部";
        }else{
            return "加载更多";
        }
    }
    public void refreshFooterView(long totalCount){
        if (totalCount == 0) {
            setLoadingstatus(BaseAdapter.STATUS_EMPTY);
        } else {
            if (getList().size() < totalCount) {
                setLoadingstatus(BaseAdapter.STATUS_LOADING_MORE);
            } else if (getList().size() == totalCount) {
                setLoadingstatus(BaseAdapter.STATUS_LOAD_ALL);
            }
        }
    }
    public interface OnItemClickListener{
        void onItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }
    //删除
    public interface OnItemDeleteClickListener{
        void onItemDeleteClickListener(View view, int position);
    }

    public void setOnItemDeleteClickListener(OnItemDeleteClickListener onItemDeleteClickListener) {
        this.onItemDeleteClickListener = onItemDeleteClickListener;
    }

    public OnItemDeleteClickListener getOnItemDeleteClickListener() {
        return onItemDeleteClickListener;
    }


    //checkBox
    public interface OnCheckItemClickListener{
        void onCheckItemClickListener(CompoundButton view, int position);
    }

    public void setOnCheckItemClickListener(OnCheckItemClickListener onCheckItemClickListener) {
        this.onCheckItemClickListener = onCheckItemClickListener;
    }

    public OnCheckItemClickListener getOnCheckItemClickListener() {
        return onCheckItemClickListener;
    }

    public BaseAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        if(list == null){
            list = new ArrayList<>();
        }
        return list;
    }


    public View getView(ViewGroup viewGroup, int resId){
        return LayoutInflater.from(context).inflate(resId,viewGroup,false);
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(CommonViewHolder commonViewHolder, int position) {

    }

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }
}
