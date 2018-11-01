package com.liruya.swipelayout.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.liruya.swipelayout.R;
import com.liruya.swipelayout.SwipeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class MainFragment extends Fragment implements SwipeLayout.OnSwipeItemClickListener
{
    private static final String TAG = "MainFragment";

    private MainViewModel mViewModel;
    private SwipeLayout mSwipeLayout_CoverLeft;
    private SwipeLayout mSwipeLayout_CoverRight;
    private SwipeLayout mSwipeLayout_ScrollLeft;
    private SwipeLayout mSwipeLayout_ScrollRight;

    public static MainFragment newInstance()
    {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState )
    {
        View view =  inflater.inflate( R.layout.main_fragment, container, false );

        initView( view );
        return view;
    }

    @Override
    public void onActivityCreated( @Nullable Bundle savedInstanceState )
    {
        super.onActivityCreated( savedInstanceState );
        mViewModel = ViewModelProviders.of( this )
                                       .get( MainViewModel.class );
    }

    private void initView( View view )
    {
        mSwipeLayout_CoverLeft = view.findViewById( R.id.swipelayout_cover_left );
        mSwipeLayout_CoverRight = view.findViewById( R.id.swipelayout_cover_right );
        mSwipeLayout_ScrollLeft = view.findViewById( R.id.swipelayout_scroll_left );
        mSwipeLayout_ScrollRight = view.findViewById( R.id.swipelayout_scroll_right );
        mSwipeLayout_CoverLeft.setOnSwipeItemClickListener( this );
        mSwipeLayout_CoverRight.setOnSwipeItemClickListener( this );
        mSwipeLayout_ScrollLeft.setOnSwipeItemClickListener( this );
        mSwipeLayout_ScrollRight.setOnSwipeItemClickListener( this );
    }

    private void clickContent(int idx)
    {
        Toast.makeText( getContext(), "click content: " + idx, Toast.LENGTH_SHORT )
             .show();
    }

    private void clickAction(int idx, int actionid)
    {
        Toast.makeText( getContext(), "click action: " + idx + "  " + actionid, Toast.LENGTH_SHORT )
             .show();
    }

    @Override
    public void onContentClick()
    {
        Log.e( TAG, "onContentClick: " + getId() );
        switch ( getId() )
        {
            case R.id.swipelayout_cover_left:
                clickContent( 1 );
                break;
            case R.id.swipelayout_cover_right:
                clickContent( 2 );
                break;
            case R.id.swipelayout_scroll_left:
                clickContent( 3 );
                break;
            case R.id.swipelayout_scroll_right:
                clickContent( 4 );
                break;
        }
    }

    @Override
    public void onActionClick( int actionid )
    {
        Log.e( TAG, "onActionClick: " + actionid );
        switch ( getId() )
        {
            case R.id.swipelayout_cover_left:
                clickAction( 1, actionid );
                break;
            case R.id.swipelayout_cover_right:
                clickAction( 2, actionid );
                break;
            case R.id.swipelayout_scroll_left:
                clickAction( 3, actionid );
                break;
            case R.id.swipelayout_scroll_right:
                clickAction( 4, actionid );
                break;
        }
    }
}
