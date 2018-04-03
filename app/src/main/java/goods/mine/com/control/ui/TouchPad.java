package goods.mine.com.control.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import goods.mine.com.control.processors.ClickProcessor;


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
        int clicks = clickProcessor.getClickCount(event) ;
        if (clicks != 0 ) {
            Toast.makeText(getContext(), "clicks : " + clicks, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

}