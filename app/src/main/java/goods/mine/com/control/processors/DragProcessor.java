package goods.mine.com.control.processors;

import android.view.MotionEvent;
import static goods.mine.com.control.processors.ClickProcessor.MAX_NUM_SUPPORTED_POINTERS ;

public class DragProcessor {

//    public static final int SINGLE_FINGER_DRAG = 1001 ;
//    public static final int DOUBLE_FINGER_DRAG = 1002 ;

    private class PointerLocation {
        float x , y ;
        int id ;
    }

    private PointerLocation[] pointerSnapShot = new PointerLocation[MAX_NUM_SUPPORTED_POINTERS] ;

    private void clearPointerSnapShot () {
        for (int i = 0 ; i<MAX_NUM_SUPPORTED_POINTERS; i++) {
            pointerSnapShot[i].id = -1;
        }
    }

    private void add(MotionEvent event , int index) {
        int id =  event.getPointerId(index) ;
        for (int i = 0 ; i<MAX_NUM_SUPPORTED_POINTERS; i++) {
            if (pointerSnapShot[i].id == -1) {
                pointerSnapShot[i].id = id ;
                pointerSnapShot[i].x = event.getX(index) ;
                pointerSnapShot[i].y = event.getY(index) ;
                break ;
            }
        }
    }

    private void remove(MotionEvent event , int index) {
        int id =  event.getPointerId(index) ;
        for (int i = 0 ; i<MAX_NUM_SUPPORTED_POINTERS; i++) {
            if (pointerSnapShot[i].id == id) {
                pointerSnapShot[i].id = -1 ;
                break;
            }
        }
    }

    public DragProcessor(){
        for (int i = 0 ; i<MAX_NUM_SUPPORTED_POINTERS; i++) {
            pointerSnapShot[i] = new PointerLocation() ;
        }
    }

    public void trackMotion (MotionEvent event  ) {
        switch (event.getActionMasked() ) {
            case MotionEvent.ACTION_DOWN:
                clearPointerSnapShot();
            case MotionEvent.ACTION_POINTER_DOWN :
                add(event , event.getActionIndex()) ;

                break ;

            case MotionEvent.ACTION_UP :
            case MotionEvent.ACTION_POINTER_UP:
                remove(event , event.getActionIndex());
                break ;
            case MotionEvent.ACTION_MOVE:
                int code = 1000 + event.getPointerCount() ;
                float dy = getMaxYTranslation(event) ;
                float dx = getMaxXTranslation(event) ;
                Connection.postValues(code , dx , dy) ;
                updateValues(event);
                break ;
        }
    }

    private void updateValues(MotionEvent event) {
        int count = event.getPointerCount() ;
        for (int i = 0 ; i<count ; i++ ) {
            int id = event.getPointerId(i) ;
            float x = event.getX(i) ;
            float y = event.getY(i) ;
            updateRecord(id , x , y) ;
        }
    }

    private void updateRecord(int id  ,float x , float y) {
        for (int i = 0 ; i<MAX_NUM_SUPPORTED_POINTERS ; i++ ){
            if (pointerSnapShot[i].id == id) {
                pointerSnapShot[i].x = x ;
                pointerSnapShot[i].y = y ;
                return ;
            }
        }
    }

    private float getLastY(int id ) {
        for (int i = 0 ; i<MAX_NUM_SUPPORTED_POINTERS; i++) {
            if (pointerSnapShot[i].id == id ) {
                return pointerSnapShot[i].y ;
            }
        }
        return 0f ;
    }

    private float getMaxXTranslation(MotionEvent event) {
        float res = 0f ;
        for (int i = 0 ; i<event.getPointerCount() ; i++ ){
            float r = event.getX(i) - getLastX(event.getPointerId(i)) ;
            res = Math.abs(res) > Math.abs(r) ? res : r ;
        }
        return res ;
    }

    private float getLastX(int id) {
        for (int i = 0 ; i<MAX_NUM_SUPPORTED_POINTERS; i++) {
            if (pointerSnapShot[i].id == id ) {
                return pointerSnapShot[i].x ;
            }
        }
        return 0f ;
    }

    private float getMaxYTranslation(MotionEvent event) {
        float res = 0f ;
        for (int i = 0 ; i<event.getPointerCount() ; i++ ){
            float r = event.getY(i) - getLastY(event.getPointerId(i)) ;
            res = Math.abs(res) > Math.abs(r) ? res : r ;
        }
        return res ;
    }
}