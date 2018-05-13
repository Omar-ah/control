package goods.mine.com.control.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import goods.mine.com.control.processors.ClickProcessor;
import goods.mine.com.control.network.Connection;
import goods.mine.com.control.processors.DragProcessor;


public class TouchPad extends View {

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