package goods.mine.com.control.processor;


import android.view.MotionEvent;

public class ClickProcessor {

    /**
     * the max number of supported pointers in a click detection
     */
    private static final int MAX_NUM_SUPPORTED_POINTERS = 5  ;

    /**
     * just as the name suggests
     */
    private static final int MAX_ACTION_MOVE_COUNT_TO_CLICK = 50 ;

    /**
     * the number of received events with {@link android.view.MotionEvent#ACTION_MOVE} action
     * that occurred in series
     */
    private int nOstaticMoveActionsInRow = 0 ;

    /**
     * this boolean holds true when receiving  {@link android.view.MotionEvent#ACTION_DOWN}
     * and subsequent fixed {@link android.view.MotionEvent#ACTION_MOVE}
     */
    private boolean straightFixedDown;

    /**
     * the maximum translation in either direction -x and y that we still consider the pointer to
     * be fixed
     *
     * TODO : fix this value so it adapt to different screen use dp
     */
    private final float MAX_FIXED_TRANSLATION  =  15f;

    /**
     * holds the number of pointers that were down since {@link android.view.MotionEvent#ACTION_DOWN}
     * this variable just goes up with every new pointer and cleared on action up or down
     */
    private int activatedPointers ;


    public int getClickCount(MotionEvent event ) {
        switch (event.getActionMasked() ) {
            case MotionEvent.ACTION_DOWN :
                activatedPointers = 1 ;
                straightFixedDown = true ;
                nOstaticMoveActionsInRow = 0 ;
                return 0  ;

            case MotionEvent.ACTION_POINTER_DOWN :
                activatedPointers++ ;
                if (activatedPointers > MAX_NUM_SUPPORTED_POINTERS) {
                    //doing this as a way of aborting the gesture not that it is not straightFixedDown
                    straightFixedDown = false ;
                }
                return 0  ;

            case MotionEvent.ACTION_MOVE :
                if (!straightFixedDown) return 0;
                if (straightFixedDown = checkIfAllStatic(event) ) {
                    nOstaticMoveActionsInRow++ ;
                }
                return 0  ;

            case MotionEvent.ACTION_UP :
                if (straightFixedDown && nOstaticMoveActionsInRow < MAX_ACTION_MOVE_COUNT_TO_CLICK) {
                    return  activatedPointers ;
                }
                return 0  ;

            default: return 0 ;

        }
    }

    private boolean checkIfAllStatic(MotionEvent event) {
        boolean areAllFixed = true ;
        if (event.getHistorySize() != 0 ) {
            int pointerCount = event.getPointerCount() ;
            for (int i = 0 ; i<pointerCount ; i++ ) {
                areAllFixed = areAllFixed &
                        (Math.abs(event.getX(i) - event.getHistoricalX(i , 0) ) < MAX_FIXED_TRANSLATION) &
                        (Math.abs(event.getY(i) - event.getHistoricalY(i ,0) ) < MAX_FIXED_TRANSLATION) ;
            }
        }
        return areAllFixed ;
    }
}
