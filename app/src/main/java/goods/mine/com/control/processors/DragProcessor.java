package goods.mine.com.control.processors;

import android.view.MotionEvent;

import goods.mine.com.control.network.Connection;

import static goods.mine.com.control.processors.ClickProcessor.MAX_NUM_SUPPORTED_POINTERS ;

public class DragProcessor {

//    public static final int SINGLE_FINGER_DRAG = 1001 ;
//    public static final int DOUBLE_FINGER_DRAG = 1002 ;

    private class PointerLocation {
        float x , y ;
        boolean set  ;
    }

    private PointerLocation[] pointerSnapShot = new PointerLocation[MAX_NUM_SUPPORTED_POINTERS] ;

    private void clearPointerSnapShot () {
        for (int i = 0 ; i<MAX_NUM_SUPPORTED_POINTERS; i++) {
            pointerSnapShot[i].set = false;
        }
    }

    private void add(MotionEvent event , int index) {
        int id =  event.getPointerId(index) ;
        if (id > MAX_NUM_SUPPORTED_POINTERS) return;
        pointerSnapShot[id].set = true ;
        pointerSnapShot[id].x = event.getX(index) ;
        pointerSnapShot[id].y = event.getY(index) ;
    }

    private void remove(MotionEvent event , int index) {
        int id =  event.getPointerId(index) ;
        if (id > MAX_NUM_SUPPORTED_POINTERS) return;
        pointerSnapShot[id].set = false ;
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
        if (id > MAX_NUM_SUPPORTED_POINTERS) return ;
        pointerSnapShot[id].x = x ;
        pointerSnapShot[id].y = y ;
    }

    private float getLastY(int id ) {
        if (id > MAX_NUM_SUPPORTED_POINTERS) return 0f ;
        return pointerSnapShot[id].y ;
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
        if (id > MAX_NUM_SUPPORTED_POINTERS) return 0f ;
        return pointerSnapShot[id].x ;
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