package jast.editableview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import jast.org.editableview.R;

/**
 *
 * Created by ptc_02008 on 2016/11/18.
 */

public class AutoScrollLayout extends HorizontalScrollView {


    protected LinearLayout mLinearLayoutAllContent;

    protected FrameLayout mFrameLayoutContent;
    protected TextView mTextViewEditLayout;
    protected ImageView mImageViewSelection;

    private ScrollViewOnTouchEvent mScrollViewOnTouchEvent;

    private boolean translationEditMode = false;


    public AutoScrollLayout(Context context) {
        this(context, null, 0);
    }

    public AutoScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);


    }

    public AutoScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


    }

    private void initControl(Context context) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        inflater.inflate(R.layout.control_scrollable_layout, this);
        initView(context);
//        setSizeOfView();
        setEvent();

    }


    private void initView(Context context) {


        Display display = ((WindowManager) getContext().getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);


        int sizeX;
        if (size.x > 0) {
            sizeX = size.x;
        } else {
            // just for demo
            sizeX = 720;
        }

        mLinearLayoutAllContent = new LinearLayout(context);
        mLinearLayoutAllContent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));


        int valueInPixels = (int) getResources().getDimension(R.dimen.layout_padding);

        mImageViewSelection = new ImageView(context);
        mImageViewSelection.setLayoutParams(new ViewGroup.LayoutParams(sizeX / 10, LayoutParams.MATCH_PARENT));
        mImageViewSelection.setPadding(valueInPixels, valueInPixels, valueInPixels, valueInPixels);
        mImageViewSelection.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
        mImageViewSelection.setTranslationX(-mImageViewSelection.getLayoutParams().width);
//        mImageViewSelection.invalidate();
        mLinearLayoutAllContent.addView(mImageViewSelection);

        mFrameLayoutContent = new FrameLayout(context);
        mFrameLayoutContent.setLayoutParams(new ViewGroup.LayoutParams(sizeX, LayoutParams.WRAP_CONTENT));
        mFrameLayoutContent.setTranslationX(-mImageViewSelection.getLayoutParams().width);
//        mFrameLayoutContent.invalidate();
        mLinearLayoutAllContent.addView(mFrameLayoutContent);

        mTextViewEditLayout = new TextView(context);
        mTextViewEditLayout.setLayoutParams(new ViewGroup.LayoutParams(sizeX / 5, LayoutParams.MATCH_PARENT));
        mTextViewEditLayout.setText(R.string.delete);
        mTextViewEditLayout.setGravity(Gravity.CENTER);
        mTextViewEditLayout.setBackgroundResource(android.R.color.holo_red_light);
        mTextViewEditLayout.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        mTextViewEditLayout.setPadding(valueInPixels, valueInPixels, valueInPixels, valueInPixels);
        mLinearLayoutAllContent.addView(mTextViewEditLayout);

    }


    private void setEvent() {
        if (mScrollViewOnTouchEvent == null) {
            mScrollViewOnTouchEvent = new ScrollViewOnTouchEvent(this);
        }
        this.setOnTouchListener(mScrollViewOnTouchEvent);


    }


    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {


        if (getChildCount() <= 0) {
            initControl(getContext());
            super.addView(mLinearLayoutAllContent, index, mLinearLayoutAllContent.getLayoutParams());
        }
        mFrameLayoutContent.addView(child);
    }

    /**
     * 滑動的監聽者
     */
    class ScrollViewOnTouchEvent implements OnTouchListener {


        boolean openEditLayout = false;

        HorizontalScrollView scrollView;

        public ScrollViewOnTouchEvent(HorizontalScrollView scrollView) {
            this.scrollView = scrollView;
        }


        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP) {


                //是不是滑開到刪除的位置
                if (!openEditLayout) {

                    // 如果滑過螢幕3分之1就打開
                    if (scrollView.getScrollX() > 10) {
                        scrollView.post(scrollToEnd);
                    } else {
                        // 沒有就滑回去
                        scrollView.post(scrollToBegin);
                    }
                } else {
                    if (scrollView.getScrollX() < scrollView.getWidth() - 10) {
                        scrollView.post(scrollToBegin);
                    } else {
                        scrollView.post(scrollToEnd);
                    }
                }
            }


            return false;
        }


        Runnable scrollToEnd = new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(scrollView.getMaxScrollAmount(), 0);
                openEditLayout = true;
            }
        };

        Runnable scrollToBegin = new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, 0);
                openEditLayout = false;

            }
        };

        public void setOpenEditLayout(boolean openEditLayout) {
            this.openEditLayout = openEditLayout;
        }
    }


    public void openEditButton() {

        if (translationEditMode) {
            return;
        }

        float initialTranslation = -mImageViewSelection.getLayoutParams().width;
        playAnimation(mImageViewSelection, initialTranslation, 0);
        playAnimation(mFrameLayoutContent, initialTranslation, 0);


        translationEditMode = true;

    }

    public void closeEditButton() {
        if (!translationEditMode) {
            return;
        }

        float initialTranslation = -mImageViewSelection.getLayoutParams().width;
        playAnimation(mImageViewSelection, 0, initialTranslation);
        playAnimation(mFrameLayoutContent, 0, initialTranslation);

        translationEditMode = false;
    }


    private void playAnimation(View view, float startVariable, float endVariable) {

        ObjectAnimator checkIconMovement = ObjectAnimator.ofFloat(view, "translationX", startVariable, endVariable);
        checkIconMovement.setRepeatMode(ObjectAnimator.REVERSE);
        checkIconMovement.setRepeatCount(0);
        checkIconMovement.setDuration(200);
        checkIconMovement.setInterpolator(new LinearInterpolator());
        checkIconMovement.start();
    }

    /**
     * 挑整回預設狀態
     */
    public void setDefaultStatus() {
        scrollTo(0, 0);
        mScrollViewOnTouchEvent.setOpenEditLayout(false);
    }


    public void setContentZoneOnClickListener(OnClickListener mOnClickListener) {
        mLinearLayoutAllContent.setOnClickListener(mOnClickListener);
    }

    public void setEditControlZonOnClickListener(OnClickListener mOnClickListener) {
        mTextViewEditLayout.setOnClickListener(mOnClickListener);
    }

    public void closeEditMode() {
        mScrollViewOnTouchEvent.setOpenEditLayout(false);
    }

    public void setSelection(boolean selection) {
        mImageViewSelection.setSelected(selection);
    }

}
