package goods.mine.com.control.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import goods.mine.com.control.processors.ClickProcessor;
import goods.mine.com.control.processors.Connection;
import goods.mine.com.control.processors.DragProcessor;


public class TouchPad extends View {

    private static final String TAG = TouchPad.class.getSimpleName();
    public static final int SINGLE_TAB = 101 ;
    public static final int DOUBLE_TAB = 102 ;
    public static final int TRIPLE_TAB = 103 ;


    ClickProcessor clickProcessor;
    DragProcessor dragProcessor;

    public TouchPad(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        clickProcessor = new ClickProcessor();
        dragProcessor = new DragProcessor();
        clickProcessor.setMAX_FIXED_TRANSLATION(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15,context.getResources().getDisplayMetrics())) ;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int clicks = clickProcessor.getClickCount(event);
        if (clicks != 0) {
            Connection.postValues(100 + clicks , 0 , 0 );
        }

        dragProcessor.trackMotion(event);
        return true;
    }
}