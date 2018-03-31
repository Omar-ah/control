package goods.mine.com.control.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


public class TouchPad extends View {

    private static final String TAG = TouchPad.class.getSimpleName() ;

    public TouchPad(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        snapShots = new PointersSnapShot[MAX_NUM_SUPPORTED_POINTERS] ;
        for (int i = 0 ; i<MAX_NUM_SUPPORTED_POINTERS ; i++ ) {
            snapShots[i] = new PointersSnapShot() ;
            snapShots[i].clear();
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int result = getClickPointerCount(event ) ;
        if (result == 1 ) performClick() ;
//        printSamples(event);
        if (result != 0){
            Toast.makeText(getContext() , "result " + result , Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    void printSamples(MotionEvent ev) {
        final int historySize = ev.getHistorySize();
        final int pointerCount = ev.getPointerCount();
        for (int h = 0; h < historySize; h++) {
            Log.e(TAG , String.format("At time %d:", ev.getHistoricalEventTime(h))) ;
            for (int p = 0; p < pointerCount; p++) {
                Log.e(TAG , String.format("  pointer %d: (%f,%f)",
                        ev.getPointerId(p), ev.getHistoricalX(p, h), ev.getHistoricalY(p, h))) ;
            }
        }
        Log.e(TAG , String.format("At time %d:", ev.getEventTime())) ;
        for (int p = 0; p < pointerCount; p++) {
            Log.e(TAG , String.format("  pointer %d: (%f,%f)",
                    ev.getPointerId(p), ev.getX(p), ev.getY(p)));
        }
    }

    /**
     * the max number of supported pointers in a click detection
     */
    private static final int MAX_NUM_SUPPORTED_POINTERS = 5  ;

    /**
     * holds the last updated location value i.e x  , y
     * for all pointers that were down since the {@link android.view.MotionEvent#ACTION_DOWN}
     */
    private PointersSnapShot[] snapShots  ;

    private boolean $hasId(int id) {
        for (int i= 0 ; i< MAX_NUM_SUPPORTED_POINTERS ; i++ ){
            if (snapShots[i].id == id) return true ;
        }
        return false ;
    }

    private boolean $addId(int id , float x , float  y) {
        for (int i = 0 ; i<MAX_NUM_SUPPORTED_POINTERS ; i++) {
            if (snapShots[i].id == -1){
                snapShots[i].set(id , x ,y);
                return true ;
            }
        }
        return false ;
    }

    private int getIndexFor(int id) {
        for (int i= 0 ; i< MAX_NUM_SUPPORTED_POINTERS ; i++ ){
            if (snapShots[i].id == id) return  i ;
        }
        return -1 ;
    }

    private boolean check (int id , float x , float y  ) {
        int index = getIndexFor(id) ;
        return (Math.abs(x - snapShots[index].x) < MAX_FIXED_TRANSLATION) &&
                (Math.abs(y - snapShots[index].y) < MAX_FIXED_TRANSLATION);
    }

    private void clearSnapShots() {
        for (PointersSnapShot p : snapShots ) {
            p.clear();
        }
    }

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

    /**
     * this method detects single , double , triple click events
     * @param event the series of events (one at a time) that this method detects
     * @return the count of pointers that clicked (0 -> {@link #MAX_NUM_SUPPORTED_POINTERS})
     * or 0 if no click was detected
     *
     * are all fixed && straight in series && don't exceed the number of events allowed
     */
    private int getClickPointerCount(MotionEvent event ) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                clearSnapShots();
                straightFixedDown = true ;
                nOstaticMoveActionsInRow = 0 ;
                //it's not actually zero but this pointer will be added later
                activatedPointers = 0 ;
                break ;
            case MotionEvent.ACTION_MOVE :
                if (!straightFixedDown) return 0 ;
                boolean areAllFixed = true ;
                int pointerCount = event.getPointerCount() ;
                for (int i = 0 ; i<pointerCount ; i++ ) {
                    int pointerId = event.getPointerId(i) ;
                    if ($hasId(pointerId)){
                        boolean checkRes = check(pointerId , event.getX(i) , event.getY(i));
                        areAllFixed = areAllFixed & checkRes ;
                        if (!checkRes) {
                            straightFixedDown = false ;
                        }
                    }else {
                        if ($addId(pointerId , event.getX(i) , event.getY(i))) {
                            activatedPointers++ ;
                        }else{
                            //doing this as a way of canceling the detection
                            straightFixedDown = false ;
                        }
                    }
                }
                if (areAllFixed) nOstaticMoveActionsInRow++ ;
                break ;
            case MotionEvent.ACTION_UP :
                if (straightFixedDown && nOstaticMoveActionsInRow < MAX_ACTION_MOVE_COUNT_TO_CLICK) {
                    return  activatedPointers ;
                }
                break ;

        }
        return  0 ;
    }

    /**
     * holds location information mapped by id for a specific pointer
     */
    private static class PointersSnapShot{
        float x  ;
        float y  ;
        int   id ;

        void update(float x , float y) {
            this.x= x ;
            this.y = y ;
        }

        void  set(int id , float x , float y ) {
            update(x, y ) ;
            this.id = id ;
        }
        void clear() {
            x = y = id = -1 ;
        }
    }
}