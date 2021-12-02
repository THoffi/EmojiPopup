
package de.th.emoji_popup_library.Helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import java.util.Arrays;

import de.th.emoji_popup_library.R;
import de.th.emoji_popup_library.emoji.Emojicon;
import de.th.emoji_popup_library.emoji.People;

public class EmojiconGridView{
    final View rootView;
    final EmojiconsPopup mEmojiconPopup;
    private EmojiconRecents mRecents;

    @SuppressLint("InflateParams")
    EmojiconGridView(Context context, Emojicon[] emojicons, EmojiconRecents recents, EmojiconsPopup emojiconPopup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        mEmojiconPopup = emojiconPopup;
        rootView = inflater.inflate(R.layout.emojicon_grid, null);
        setRecents(recents);
        GridView gridView = rootView.findViewById(R.id.Emoji_GridView);
        Emojicon[] mData;
        if (emojicons== null) {
            mData = People.DATA;
        } else {
            mData = Arrays.asList((Object[]) emojicons).toArray(new Emojicon[emojicons.length]);
        }
        EmojiAdapter mAdapter = new EmojiAdapter(rootView.getContext(), mData);
        mAdapter.setEmojiClickListener(emojicon -> {
            if (mEmojiconPopup.onEmojiconClickedListener != null) {
                mEmojiconPopup.onEmojiconClickedListener.onEmojiconClicked(emojicon);
            }
            if (mRecents != null) {
                mRecents.addRecentEmoji(rootView.getContext(), emojicon);
            }
        });
        gridView.setAdapter(mAdapter);
    }

    private void setRecents(EmojiconRecents recents) {
        mRecents = recents;
    }

    public interface OnEmojiconClickedListener {
        void onEmojiconClicked(Emojicon emojicon);
    }

}