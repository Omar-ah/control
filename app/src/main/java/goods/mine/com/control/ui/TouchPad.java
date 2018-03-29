package goods.mine.com.control.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;



public class TouchPad extends View {

    private static final String TAG = TouchPad.class.getSimpleName() ;

    public TouchPad(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (shouldPerformClick(event)) {
            super.performClick();
            Log.e(TAG , "click was detected") ;
        }
        return true;
    }

    /**
     * just as the name suggests
     */
    private static final int MAX_ACTION_MOVE_COUNT_TO_CLICK = 12 ;

    /**
     * the number of received events with {@link android.view.MotionEvent#ACTION_MOVE} action
     * that occurred in series
     */
    private int NostaticMoveActionsInRow = 0 ;

    /**
     * this boolean holds true when receiving  {@link android.view.MotionEvent#ACTION_DOWN}
     * and subsequent fixed {@link android.view.MotionEvent#ACTION_MOVE} detected by
     * {@link #getTotalXTranslation(MotionEvent)} and {@link #getTotalYTranslation(MotionEvent)}
     */
    private boolean straightFixedDown;

    /**
     * the maximum translation in either direction -x and y that we still consider the pointer to
     * be fixed
     */
    private final float MAX_FIXED_TRANSLATION  =  1.5f ;

    /**
     * this method detect clicks performed by one finger

     * @param event the series of events (one at a time) that occurred on this view
     * @return this method returns true if it received event with {@link android.view.MotionEvent#ACTION_UP}
     * and the {@link #NostaticMoveActionsInRow} was less than or equal to {@link goods.mine.com.control.ui.TouchPad#MAX_ACTION_MOVE_COUNT_TO_CLICK}
     */
    private boolean shouldPerformClick(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                straightFixedDown = true ;
                NostaticMoveActionsInRow = 0 ;
                break;
            case MotionEvent.ACTION_MOVE :
                if (straightFixedDown
                        && getTotalXTranslation(event) < MAX_FIXED_TRANSLATION
                        && getTotalYTranslation(event) < MAX_FIXED_TRANSLATION ) {
                    NostaticMoveActionsInRow++ ;
                }else {
                    NostaticMoveActionsInRow = 0 ;
                    straightFixedDown = false ;
                }
                break ;
            case MotionEvent.ACTION_UP :
                if (straightFixedDown && NostaticMoveActionsInRow < MAX_ACTION_MOVE_COUNT_TO_CLICK) {
                    return true ;
                }
                break ;
        }
        return false;
    }

    /**
     * calculate the total x translation of event with {@link android.view.MotionEvent#ACTION_MOVE}
     * this method assumes that the event parameter action is {@link android.view.MotionEvent#ACTION_MOVE}
     *
     * @param event the event we are interested in
     * @return the total translation as float if the event has a history , 101 otherwise as an abstract error message
     */
    private float getTotalXTranslation(MotionEvent event) {
        if (event.getHistorySize() != 0 ){
            return event.getX() - event.getHistoricalX(0) ;
        }
        return 0f;
    }
    /**
     * calculate the total y translation of event with {@link android.view.MotionEvent#ACTION_MOVE}
     * this method assumes that the event parameter action is {@link android.view.MotionEvent#ACTION_MOVE}
     *
     * @param event the event we are interested in
     * @return the total translation as float if the event has a history , 101 otherwise as an abstract error message
     */
    private float getTotalYTranslation(MotionEvent event) {
        if (event.getHistorySize() != 0 ){
            return event.getY() - event.getHistoricalY(0) ;
        }
        return 0f;
    }




}
