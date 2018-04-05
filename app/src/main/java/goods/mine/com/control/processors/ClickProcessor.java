package goods.mine.com.control.processors;


import android.view.MotionEvent;

public class ClickProcessor {

    /**
     * the max number of supported pointers in a click detection
     */
    private static final int MAX_NUM_SUPPORTED_POINTERS = 3  ;

    /**
     * this boolean holds true when receiving  {@link android.view.MotionEvent#ACTION_DOWN}
     * and subsequent fixed {@link android.view.MotionEvent#ACTION_MOVE}
     */
    private boolean straightFixedDown;

    /**
     * the maximum translation in either direction -x and y that we still consider the pointer to
     * be fixed
     **/
    private float MAX_FIXED_TRANSLATION  ;

    public void setMAX_FIXED_TRANSLATION (float max_fixed_translation) {
        MAX_FIXED_TRANSLATION = max_fixed_translation ;
    }

    /**
     * holds the number of pointers that were down since {@link android.view.MotionEvent#ACTION_DOWN}
     * this variable just goes up with every new pointer and cleared on action up or down
     */
    private int activatedPointers ;

    /**
     * represent when the current gesture began it is set every time we got {@link android.view.MotionEvent#ACTION_DOWN}
     */
    private long startTime  ;

    /**
     * how long the gesture is considered to be a possible click
     */
    private static final long TIME_INTERVAL = 250;


    public int getClickCount(MotionEvent event ) {
        switch (event.getActionMasked() ) {
            case MotionEvent.ACTION_DOWN :
                activatedPointers = 1 ;
                straightFixedDown = true ;
                startTime = System.currentTimeMillis() ;
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
                straightFixedDown = checkIfAllStatic(event) ;
                return 0  ;

            case MotionEvent.ACTION_UP :
                if (straightFixedDown &&
                        System.currentTimeMillis() - startTime < TIME_INTERVAL) {
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
