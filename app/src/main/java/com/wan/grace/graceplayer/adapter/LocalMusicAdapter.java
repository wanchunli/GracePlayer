package com.wan.grace.graceplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.allen.library.SuperTextView;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.wan.grace.graceplayer.R;
import com.wan.grace.graceplayer.bean.Song;
import com.wan.grace.graceplayer.info.MusicInfo;
import com.wan.grace.graceplayer.utils.CommonViewHolder;

import java.util.List;

/**
 * Created by 开发部 on 2018/1/3.
 */

public class LocalMusicAdapter extends BaseAdapter<Song> {

    final static int FIRST_ITEM = 0;
    final static int ITEM = 1;

    public LocalMusicAdapter(Context context, List<Song> list) {
        super(context, list);
    }

    public CommonViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        if (viewtype == FIRST_ITEM) {
            return new CommonViewHolder(getView(viewGroup, R.layout.localmusic_top_item));
        } else {
            return new CommonViewHolder(getView(viewGroup, R.layout.localmusic_item));
        }
    }

    @Override
    public void onBindViewHolder(CommonViewHolder commonViewHolder, final int position) {
        super.onBindViewHolder(commonViewHolder, position);
        List<Song> list = getList();
        Song song = list.get(position);
        if (getItemViewType(position) == FIRST_ITEM) {
            SuperTextView superTextView = commonViewHolder.getView(R.id.super_texttopview);
            superTextView.getLeftIconIV().setImageDrawable(
                    context.getResources().getDrawable(R.drawable.all_song));
            superTextView.setLeftString("播放全部(共" + list.size() + "首)");
        } else {
            SuperTextView superTextView = commonViewHolder.getView(R.id.super_textview);
//            Picasso.with(context).load(musicInfo.albumData)
//                    .placeholder(R.drawable.grace_logo)
//                    .into(superTextView.getLeftIconIV());
            superTextView.setLeftTopString(song.getDisplayName());
            superTextView.setLeftBottomString(song.getArtist());
            superTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getOnItemClickListener() != null) {
                        getOnItemClickListener().onItemClickListener(v, position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == FIRST_ITEM ? FIRST_ITEM : ITEM;
    }

    @Override
    public int getItemCount() {
        return getList().size() + 1;
    }
}
