package com.liruya.swipelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

public class SwipeLayout extends RelativeLayout
{
    private final String TAG = "SwipeLayout";

    private final int SWIPE_MODE_DISALED = 0;
    private final int SWIPE_MODE_COVER = 1;
    private final int SWIPE_MODE_SCROLL = 2;
    private final int SWIPE_DIRECTION_LEFT = 0;
    private final int SWIPE_DIRECTION_RIGHT = 1;

    private int mSwipeMode;
    private int mSwipeDirection;
    private @LayoutRes int mContentLayoutResID;
    private @LayoutRes int mActionLayoutResID;

    private View mContentView;
    private View mActionView;

    private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    private int mTouchX;
    private boolean mMoving;
    private boolean mOpened;
    private boolean mClickAction;

    public SwipeLayout( @NonNull Context context )
    {
        this( context, null );
    }

    public SwipeLayout( @NonNull Context context, AttributeSet attrs )
    {
        this( context, attrs, 0 );
    }

    public SwipeLayout( @NonNull Context context, AttributeSet attrs, int defStyleAttr )
    {
        super( context, attrs, defStyleAttr );

        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.SwipeLayout );
        mSwipeMode = a.getInt( R.styleable.SwipeLayout_swipeMode, SWIPE_MODE_DISALED );
        mSwipeDirection = a.getInt( R.styleable.SwipeLayout_swipeDirection, SWIPE_DIRECTION_LEFT );
        mContentLayoutResID = a.getResourceId( R.styleable.SwipeLayout_contentLayout, R.layout.default_content );
        mActionLayoutResID = a.getResourceId( R.styleable.SwipeLayout_actionLayout, R.layout.default_action );
        a.recycle();

        initView( context );
    }

//    @Override
//    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
//    {
//        super.onMeasure( widthMeasureSpec, heightMeasureSpec );
//        for ( int i = 0; i < getChildCount(); i++ )
//        {
//            Log.e( TAG, "onMeasure: " + i + "   " + getChildAt( i ).getMeasuredWidth() + "  " + getChildAt( i ).getMeasuredHeight() );
//        }
//    }

    private void initView( @NonNull Context context )
    {
        LayoutInflater inflater = LayoutInflater.from( context );

        //加载contentView
        mContentView = inflater.inflate( mContentLayoutResID, this, false );
        //如果contentView为空,视图异常 退出
        if ( mContentView == null )
        {
            return;
        }
        //触摸事件需要使能点击
        mContentView.setClickable( true );
        LayoutParams lpc = new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
        lpc.addRule( ALIGN_PARENT_LEFT );
        lpc.addRule( ALIGN_PARENT_RIGHT );
        lpc.topMargin = 0;
        lpc.bottomMargin = 0;
        mContentView.setLayoutParams( lpc );

        //Swipe模式为Cover或Scroll 加载actionView 否则不加载actionView
        if ( mSwipeMode == SWIPE_MODE_COVER || mSwipeMode == SWIPE_MODE_SCROLL )
        {
            mActionView = inflater.inflate( mActionLayoutResID, this, false );
        }
        //如果actionView为空,Swipe模式Disabled 只加载contentView
        if ( mActionView == null )
        {
            addView( mContentView );
        }
        else
        {
            //触摸事件需要使能点击
            mActionView.setClickable( true );
            LayoutParams lpa = new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );

            //如果用户未设置id,则设置为默认id
            if ( mContentView.getId() == View.NO_ID )
            {
                mContentView.setId( R.id.default_content_id );
            }
            if ( mActionView.getId() == View.NO_ID )
            {
                mActionView.setId( R.id.default_action_id );
            }
            //Cover模式 contentView 覆盖在 actionView上方
            if ( mSwipeMode == SWIPE_MODE_COVER )
            {
                lpa.addRule( mSwipeDirection == SWIPE_DIRECTION_RIGHT ? ALIGN_PARENT_LEFT : ALIGN_PARENT_RIGHT );
                lpa.addRule( ALIGN_TOP, mContentView.getId() );
                lpa.addRule( ALIGN_BOTTOM, mContentView.getId() );
                lpa.setMargins( 0, 0, 0, 0 );
                mActionView.setLayoutParams( lpa );
                //先添加actionView 后添加contentView
                addView( mActionView );
                addView( mContentView );
            }   //Scoll模式  actionView toRightOf/toLeftOf contetnView,左右滚动时显示actionView
            else if ( mSwipeMode == SWIPE_MODE_SCROLL )
            {
                lpa.addRule( mSwipeDirection == SWIPE_DIRECTION_RIGHT ? LEFT_OF : RIGHT_OF, mContentView.getId() );
                lpa.addRule( ALIGN_TOP, mContentView.getId() );
                lpa.addRule( ALIGN_BOTTOM, mContentView.getId() );
                lpa.setMargins( 0, 0, 0, 0 );
                mActionView.setLayoutParams( lpa );
                //先添加contentView 后添加actionView
                addView( mContentView );
                addView( mActionView );
            }
        }
    }

    public int getContentViewWidth()
    {
        return mContentView == null ? 0 : mContentView.getWidth();
    }

    public int getActionViewWidth()
    {
        return mActionView == null ? 0 : mActionView.getWidth();
    }

    @Override
    public boolean onInterceptTouchEvent( MotionEvent ev )
    {
        if ( mActionView == null || mContentView == null )
        {
            mClickAction = false;
            mMoving = false;
            mOpened = false;
            return super.onInterceptTouchEvent( ev );
        }
        if ( mSwipeMode == SWIPE_MODE_COVER )
        {
            return interceptTouchEventOfCover( ev );
        }
        else if ( mSwipeMode == SWIPE_MODE_SCROLL )
        {
            return interceptTouchEventOfScroll( ev );
        }
        return super.onInterceptTouchEvent( ev );
    }

    @Override
    public boolean onTouchEvent( MotionEvent event )
    {
        if ( mSwipeMode == SWIPE_MODE_COVER )
        {
            return touchEventOfCover( event );
        }
        else if ( mSwipeMode == SWIPE_MODE_SCROLL )
        {
            return touchEventOfScroll( event );
        }
        return super.onTouchEvent( event );
    }

    private boolean interceptTouchEventOfCover( MotionEvent event )
    {
        switch ( event.getAction() )
        {
            case MotionEvent.ACTION_DOWN:
                mTouchX = (int) event.getX();
                mClickAction = false;
                mMoving = false;
                if ( mOpened )
                {
                    if ( mSwipeDirection == SWIPE_DIRECTION_RIGHT )
                    {
                        if ( mTouchX > getWidth() - getActionViewWidth() )
                        {
                            mMoving = true;
                        }
                        else
                        {
                            mClickAction = true;
                            return false;
                        }
                    }
                    else //if ( mSwipeDirection == SWIPE_DIRECTION_LEFT )
                    {
                        if ( mTouchX < getWidth() - getActionViewWidth() )
                        {
                            mMoving = true;
                        }
                        else
                        {
                            mClickAction = true;
                            return false;
                        }
                    }
                }
                else
                {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if ( mClickAction )         //向下分发,不处理onTouchEvent
                {
                    return false;
                }
                //触摸坐标越界 向下分发,不处理onTouchEvent
                if ( event.getY() < 0 || event.getY() > getHeight() || event.getX() < 0 || event.getX() > getWidth() )
                {
                    return false;
                }
                //如果contentView未打开,且不在滑动状态,手势左滑速度必须大于100进入滑动状态
                if ( !mMoving && !mOpened )
                {
                    mVelocityTracker.addMovement( event );
                    mVelocityTracker.computeCurrentVelocity( 1000 );
                    if ( mSwipeDirection == SWIPE_DIRECTION_RIGHT )
                    {
                        if ( mVelocityTracker.getXVelocity() > 100 )
                        {
                            mMoving = true;
                        }
                    }
                    else //if ( mSwipeDirection == SWIPE_DIRECTION_LEFT )
                    {
                        if ( mVelocityTracker.getXVelocity() < -100 )
                        {
                            mMoving = true;
                        }
                    }
                }
                if ( !mMoving )
                {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.clear();
                if ( mClickAction || !mMoving )
                {
                    return false;
                }
                break;
        }
        return true;
    }

    private boolean touchEventOfCover( MotionEvent event )
    {
        switch ( event.getAction() )
        {
            case MotionEvent.ACTION_MOVE:
                int dx = (int) ( event.getX() - mTouchX );
                if ( !mOpened )
                {
                    if ( mSwipeDirection == SWIPE_DIRECTION_RIGHT )
                    {
                        if ( dx < 0 )
                        {
                            dx = 0;
                        }
                        else if ( dx > getActionViewWidth() )
                        {
                            dx = getActionViewWidth();
                        }
                    }
                    else //if ( mSwipeDirection == SWIPE_DIRECTION_LEFT )
                    {
                        if ( dx > 0 )
                        {
                            dx = 0;
                        }
                        else if ( dx < 0 - getActionViewWidth() )
                        {
                            dx = 0 - getActionViewWidth();
                        }
                    }
                    mContentView.setTranslationX( dx );
                }
                else
                {
                    if ( mSwipeDirection == SWIPE_DIRECTION_RIGHT )
                    {
                        if ( dx > 0 )
                        {
                            dx = getActionViewWidth();
                        }
                        else if ( dx < 0 - getActionViewWidth() )
                        {
                            dx = 0;
                        }
                        else
                        {
                            dx = getActionViewWidth() + dx;
                        }
                    }
                    else //if ( mSwipeDirection == SWIPE_DIRECTION_LEFT )
                    {
                        if ( dx < 0 )
                        {
                            dx = 0 - getActionViewWidth();
                        }
                        else if ( dx > getActionViewWidth() )
                        {
                            dx = 0;
                        }
                        else
                        {
                            dx -= getActionViewWidth();
                        }
                    }
                    mContentView.setTranslationX( dx );
                }
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.clear();
                if ( mMoving )
                {
                    if ( mSwipeDirection == SWIPE_DIRECTION_RIGHT )
                    {
                        mOpened = mContentView.getTranslationX() > getActionViewWidth()/2;
                        mContentView.setTranslationX( mOpened ? getActionViewWidth() : 0 );
                    }
                    else //if ( mSwipeDirection == SWIPE_DIRECTION_LEFT )
                    {
                        mOpened = mContentView.getTranslationX() < (0 - getActionViewWidth())/2;
                        mContentView.setTranslationX( mOpened ? 0 - getActionViewWidth() : 0 );
                    }
                    mMoving = false;
                }
                break;
        }
        return true;
    }

    private boolean interceptTouchEventOfScroll( MotionEvent event )
    {
        switch ( event.getAction() )
        {
            case MotionEvent.ACTION_DOWN:
                mTouchX = (int) event.getX();
                mClickAction = false;
                mMoving = false;
                if ( mOpened )
                {
                    if ( mSwipeDirection == SWIPE_DIRECTION_RIGHT )
                    {
                        if ( mTouchX > getWidth() - getActionViewWidth() )
                        {
                            mMoving = true;
                        }
                        else
                        {
                            mClickAction = true;
                            return false;
                        }
                    }
                    else //if ( mSwipeDirection == SWIPE_DIRECTION_LEFT )
                    {
                        if ( mTouchX < getWidth() - getActionViewWidth() )
                        {
                            mMoving = true;
                        }
                        else
                        {
                            mClickAction = true;
                            return false;
                        }
                    }
                }
                else
                {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }

    private boolean touchEventOfScroll( MotionEvent event )
    {
        switch ( event.getAction() )
        {
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }
}
