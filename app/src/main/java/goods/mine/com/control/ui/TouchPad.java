package goods.mine.com.control.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import goods.mine.com.control.processor.ClickProcessor;


public class TouchPad extends View {

    private static final String TAG = TouchPad.class.getSimpleName() ;

    ClickProcessor clickProcessor  ;

    public TouchPad(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        clickProcessor = new ClickProcessor() ;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getActionMasked() ) {
//            case MotionEvent.ACTION_POINTER_DOWN : {
//                Log.e(TAG , "ACTION_POINTER_DOWN : " + event.getPointerId(event.getActionIndex())) ;
//                break;
//            }
//            case MotionEvent.ACTION_POINTER_UP : {
//                Log.e(TAG , "ACTION_POINTER_UP : " + event.getPointerId(event.getActionIndex())) ;
//                break;
//            }
//            case MotionEvent.ACTION_DOWN : {
//                Log.e(TAG , "ACTION_DOWN : " + event.getPointerId(event.getActionIndex())) ;
//                break;
//            }
//            case MotionEvent.ACTION_UP : {
//                Log.e(TAG , "ACTION_UP : " + event.getPointerId(event.getActionIndex())) ;
//                break;
//            }
//            case MotionEvent.ACTION_MOVE: {
//                Log.e(TAG , "ACTION_MOVE : " + event.getPointerId(event.getActionIndex())) ;
//                break;
//            }
//        }
        int result ;
        if ((result = clickProcessor.getClickCount(event)) != 0 ) {
            Toast.makeText(getContext(), "result is " + result , Toast.LENGTH_SHORT).show();
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

}