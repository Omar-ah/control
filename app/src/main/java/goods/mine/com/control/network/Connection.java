package goods.mine.com.control.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Connection {
    private static Executor network ;
    private static DatagramPacket packet ;
    private static DatagramSocket socket ;
    private static byte[] msgBuffer;
    private static ByteBuffer byteBuffer ;

    static {
        network = Executors.newSingleThreadExecutor() ;
        msgBuffer = new byte[12];
        byteBuffer = ByteBuffer.wrap(msgBuffer) ;
        network.execute(new Runnable() {
            @Override
            public void run() {
                retryConnection() ;
            }
        });
    }

    private static boolean retryConnection() {
        try {
            socket = new DatagramSocket();
            packet = new DatagramPacket(msgBuffer, msgBuffer.length , Inet4Address.getByName("192.168.1.101" ), 5000)  ;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false ;
        } catch (SocketException e) {
            e.printStackTrace();
            return false ;
        }
        return true ;
    }

    public static void postValues(final int code ,  final float dx, final float dy) {

        network.execute(new Runnable() {
            @Override
            public void run() {
//                if (socket == null || packet == null)
//                    if (!retryConnection()){
//                        Log.e("error" ,  "couldn't establish connection") ;
//                        return ;
//                    }
                byteBuffer.clear();
                byteBuffer.putInt(code).putFloat(dx).putFloat(dy) ;
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
