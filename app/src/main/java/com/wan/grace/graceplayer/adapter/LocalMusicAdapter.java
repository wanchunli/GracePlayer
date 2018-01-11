package com.wan.grace.graceplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.wan.grace.graceplayer.R;
import com.wan.grace.graceplayer.bean.Song;
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
        if (getItemViewType(position) == FIRST_ITEM) {
            TextView tvContent = commonViewHolder.getView(R.id.tv_content);
            tvContent.setText("播放全部(共" + getList().size() + "首)");
        } else {
            List<Song> list = getList();
            Song song = list.get(position);
            LinearLayout songContentLayout = commonViewHolder.getView(R.id.song_content_layout);
            TextView tvSongName = commonViewHolder.getView(R.id.tv_songname);
            TextView tvSongArtist = commonViewHolder.getView(R.id.tv_songartist);
            tvSongName.setText(song.getDisplayName());
            tvSongArtist.setText(song.getArtist());
            songContentLayout.setOnClickListener(new View.OnClickListener() {
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
