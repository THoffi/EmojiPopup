
package de.th.emoji_popup_library.Helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.th.emoji_popup_library.R;
import de.th.emoji_popup_library.emoji.Cars;
import de.th.emoji_popup_library.emoji.Electr;
import de.th.emoji_popup_library.emoji.Emojicon;
import de.th.emoji_popup_library.emoji.Food;
import de.th.emoji_popup_library.emoji.Nature;
import de.th.emoji_popup_library.emoji.People;
import de.th.emoji_popup_library.emoji.Sport;
import de.th.emoji_popup_library.emoji.Symbols;


public class EmojiconsPopup extends PopupWindow implements ViewPager.OnPageChangeListener, EmojiconRecents {

    private int mEmojiTabLastSelectedIndex = -1;
    private View[] mEmojiTabs;
    private EmojiconRecentsManager mRecentsManager;
    private int keyBoardHeight = 0;
    private int keyBoardOffset = 0;
    private Boolean pendingOpen = false;
    private Boolean isOpened = false;
    EmojiconGridView.OnEmojiconClickedListener onEmojiconClickedListener;
    private OnEmojiconBackspaceClickedListener onEmojiconBackspaceClickedListener;
    private OnSoftKeyboardOpenCloseListener onSoftKeyboardOpenCloseListener;
    private final View rootView;
    private final Context mContext;
    private final Activity mActivity;
    private String iconPressedColor="#495C66";
    private String tabsColor="#DCE1E2";
    private String backgroundColor="#E6EBEF";
    private ViewPager emojisPager;

    public EmojiconsPopup(View rootView, Context mContext, String iconPressedColor, String tabsColor, String backgroundColor){
        super(mContext);
        //boolean setColor = true;
        this.backgroundColor=backgroundColor;
        this.iconPressedColor=iconPressedColor;
        this.tabsColor=tabsColor;
        this.mContext = mContext;
        this.rootView = rootView;
        this.mActivity = (Activity) mContext;
        View customView = createCustomView();
        setContentView(customView);
        setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setSize(255);
        setBackgroundDrawable(null);
    }

    public EmojiconsPopup(View rootView, Context mContext){
        super(mContext);
        this.mContext = mContext;
        this.rootView = rootView;
        this.mActivity = (Activity) mContext;
        View customView = createCustomView();
        setContentView(customView);
        setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setSize(255);
        setBackgroundDrawable(null);
    }

    /**
     * Set the listener for the event of keyboard opening or closing.
     */
    public void setOnSoftKeyboardOpenCloseListener(OnSoftKeyboardOpenCloseListener listener){
        this.onSoftKeyboardOpenCloseListener = listener;
    }

    /**
     * Set the listener for the event when any of the emojicon is clicked
     */
    public void setOnEmojiconClickedListener(EmojiconGridView.OnEmojiconClickedListener listener){
        this.onEmojiconClickedListener = listener;
    }

    /**
     * Set the listener for the event when backspace on emojicon popup is clicked
     */
    public void setOnEmojiconBackspaceClickedListener(OnEmojiconBackspaceClickedListener listener){
        this.onEmojiconBackspaceClickedListener = listener;
    }

    /**
     * Use this function to show the emoji popup.
     * NOTE: Since, the soft keyboard sizes are variable on different android devices, the
     * library needs you to open the soft keyboard atleast once before calling this function.
     * If that is not possible see showAtBottomPending() function.
     *
     */
    public void showAtBottom(){
        showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }
    /**
     * Use this function when the soft keyboard has not been opened yet. This
     * will show the emoji popup after the keyboard is up next time.
     * Generally, you will be calling InputMethodManager.showSoftInput function after
     * calling this function.
     */
    public void showAtBottomPending(){
        if(isKeyBoardOpen())
            showAtBottom();
        else
            pendingOpen = true;
    }

    /**
     *
     * @return Returns true if the soft keyboard is open, false otherwise.
     */
    public Boolean isKeyBoardOpen(){
        return isOpened;
    }

    /**
     * Dismiss the popup
     */
    @Override
    public void dismiss() {
        super.dismiss();
        EmojiconRecentsManager
                .getInstance(mContext).saveRecents();
    }

    /**
     * Call this function to resize the emoji popup according to your soft keyboard size
     */
    public void setSizeForSoftKeyboard(){
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {

            // Init Fenster
            Rect rectangle = new Rect();
            Window window = mActivity.getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(rectangle);

            // get ScreenHeight
            Point screenSize = new Point();
            mActivity.getWindowManager().getDefaultDisplay().getSize(screenSize);
            int screenHeight = screenSize.y;

            // set KeyBoardHeight
            int myKeyBoardHeight = screenHeight - (rectangle.bottom - keyBoardOffset);

            // Korrigiere KeyBoardHeight
            if(myKeyBoardHeight < 0) {
                keyBoardOffset = Math.abs(myKeyBoardHeight);
            }

            if (myKeyBoardHeight > 100) {
                keyBoardHeight = myKeyBoardHeight;
                setSize(keyBoardHeight);
                if (!isOpened) {
                    if (onSoftKeyboardOpenCloseListener != null)
                        onSoftKeyboardOpenCloseListener.onKeyboardOpen(keyBoardHeight);
                }
                isOpened = true;
                if (pendingOpen) {
                    showAtBottom();
                    pendingOpen = false;
                }
            } else {
                isOpened = false;
                if (onSoftKeyboardOpenCloseListener != null)
                    onSoftKeyboardOpenCloseListener.onKeyboardClose();
            }

        });
    }

    /**
     * Manually set the popup window size
     * @param height Height of the popup
     */
    private void setSize(int height){
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(height);
    }

    private View createCustomView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.emojicons, null, false);
        emojisPager = view.findViewById(R.id.emojis_pager);
        LinearLayout tabs= view.findViewById(R.id.emojis_tab);

        emojisPager.addOnPageChangeListener(this);
        EmojiconRecents recents = this;
        PagerAdapter mEmojisAdapter = new EmojisPagerAdapter(
                Arrays.asList(
                        new EmojiconRecentsGridView(mContext, null, this),
                        new EmojiconGridView(mContext, People.DATA, recents, this),
                        new EmojiconGridView(mContext, Nature.DATA, recents, this),
                        new EmojiconGridView(mContext, Food.DATA, recents, this),
                        new EmojiconGridView(mContext, Sport.DATA, recents, this),
                        new EmojiconGridView(mContext, Cars.DATA, recents, this),
                        new EmojiconGridView(mContext, Electr.DATA, recents, this),
                        new EmojiconGridView(mContext, Symbols.DATA, recents, this)

                )
        );

        emojisPager.setAdapter(mEmojisAdapter);
        mEmojiTabs = new View[8];

        mEmojiTabs[0] = view.findViewById(R.id.emojis_tab_0_recents);
        mEmojiTabs[1] = view.findViewById(R.id.emojis_tab_1_people);
        mEmojiTabs[2] = view.findViewById(R.id.emojis_tab_2_nature);
        mEmojiTabs[3] = view.findViewById(R.id.emojis_tab_3_food);
        mEmojiTabs[4] = view.findViewById(R.id.emojis_tab_4_sport);
        mEmojiTabs[5] = view.findViewById(R.id.emojis_tab_5_cars);
        mEmojiTabs[6] = view.findViewById(R.id.emojis_tab_6_elec);
        mEmojiTabs[7] = view.findViewById(R.id.emojis_tab_7_sym);
        for (int i = 0; i < mEmojiTabs.length; i++) {
            final int position = i;
            mEmojiTabs[i].setOnClickListener(v -> emojisPager.setCurrentItem(position));
        }

        emojisPager.setBackgroundColor(Color.parseColor(backgroundColor));
        tabs.setBackgroundColor(Color.parseColor(tabsColor));
        for (View mEmojiTab : mEmojiTabs) {
            ImageButton btn = (ImageButton) mEmojiTab;
            btn.setColorFilter(Color.parseColor(iconPressedColor));
        }

        ImageButton imgBtn= view.findViewById(R.id.emojis_backspace);
        imgBtn.setColorFilter(Color.parseColor(iconPressedColor));
        imgBtn.setBackgroundColor(Color.parseColor(backgroundColor));

        view.findViewById(R.id.emojis_backspace).setOnTouchListener(new RepeatListener(v -> {
            if(onEmojiconBackspaceClickedListener != null)
                onEmojiconBackspaceClickedListener.onEmojiconBackspaceClicked(v);
        }));

        // get last selected page
        mRecentsManager = EmojiconRecentsManager.getInstance(view.getContext());
        int page = mRecentsManager.getRecentPage();
        // last page was recents, check if there are recents to use
        // if none was found, go to page 1
        if (page == 0 && mRecentsManager.size() == 0) {
            page = 1;
        }

        if (page == 0) {
            onPageSelected(page);
        }
        else {
            emojisPager.setCurrentItem(page, false);
        }
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void addRecentEmoji(Context context, Emojicon emojicon) {
        EmojiconRecentsGridView fragment = ((EmojisPagerAdapter) Objects.requireNonNull(emojisPager.getAdapter())).getRecentFragment();
        fragment.addRecentEmoji(context, emojicon);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        if (mEmojiTabLastSelectedIndex == i) {
            return;
        }
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:

                if (mEmojiTabLastSelectedIndex >= 0 && mEmojiTabLastSelectedIndex < mEmojiTabs.length) {
                    mEmojiTabs[mEmojiTabLastSelectedIndex].setSelected(false);
                }
                mEmojiTabs[i].setSelected(true);
                mEmojiTabLastSelectedIndex = i;
                mRecentsManager.setRecentPage(i);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    private static class EmojisPagerAdapter extends PagerAdapter {
        private final List<EmojiconGridView> views;
        EmojiconRecentsGridView getRecentFragment(){
            for (EmojiconGridView it : views) {
                if(it instanceof EmojiconRecentsGridView)
                    return (EmojiconRecentsGridView)it;
            }
            return null;
        }
        EmojisPagerAdapter(List<EmojiconGridView> views) {
            super();
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }


        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View v = views.get(position).rootView;
            container.addView(v, 0);
            return v;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object view) {
            container.removeView((View)view);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object key) {
            return key == view;
        }
    }

    /**
     * A class, that can be used as a TouchListener on any view (e.g. a Button).
     * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
     * click is fired immediately, next before initialInterval, and subsequent before
     * normalInterval.
     * <p/>
     * <p>Interval is scheduled before the onClick completes, so it has to run fast.
     * If it runs slow, it does not generate skipped onClicks.
     */
    static class RepeatListener implements View.OnTouchListener {

        private final Handler handler = new Handler();

        private final int initialInterval;
        private final int normalInterval;
        private final OnClickListener clickListener;

        private final Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                if (downView == null) {
                    return;
                }
                handler.removeCallbacksAndMessages(downView);
                handler.postAtTime(this, downView, SystemClock.uptimeMillis() + normalInterval);
                clickListener.onClick(downView);
            }
        };

        private View downView;

        /**
         * @param clickListener   The OnClickListener, that will be called
         */
        RepeatListener(OnClickListener clickListener) {
            if (clickListener == null)
                throw new IllegalArgumentException("null runnable");

            this.initialInterval = 500;
            this.normalInterval = 50;
            this.clickListener = clickListener;
        }

        @SuppressLint("ClickableViewAccessibility")
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downView = view;
                    handler.removeCallbacks(handlerRunnable);
                    handler.postAtTime(handlerRunnable, downView, SystemClock.uptimeMillis() + initialInterval);
                    clickListener.onClick(view);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    handler.removeCallbacksAndMessages(downView);
                    downView = null;
                    return true;
            }
            return false;
        }
    }

    public interface OnEmojiconBackspaceClickedListener {
        void onEmojiconBackspaceClicked(View v);
    }

    public interface OnSoftKeyboardOpenCloseListener{
        void onKeyboardOpen(int keyBoardHeight);
        void onKeyboardClose();
    }
}