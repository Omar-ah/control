package goods.mine.com.control.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Connection {
    private static String TAG ;
    private static ExecutorService network;
    private static DatagramPacket packet;
    private static DatagramSocket socket;
    private static byte[] msgBuffer;
    private static ByteBuffer byteBuffer;
    private static final int PORT  ;

    static {
        network = Executors.newSingleThreadScheduledExecutor();
        Executors.newSingleThreadScheduledExecutor() ;
        msgBuffer = new byte[12];
        byteBuffer = ByteBuffer.wrap(msgBuffer);
        TAG = Connection.class.getSimpleName();
        PORT = 5000 ;
        init();
    }

    private static List<Inet4Address> getBroadCastAddresses() {
        List<Inet4Address> inetAddresses = new ArrayList<>();
        try {
            for (
                    final Enumeration<NetworkInterface> interfaces =
                    NetworkInterface.getNetworkInterfaces();
                    interfaces.hasMoreElements();
                    ) {
                final NetworkInterface cur = interfaces.nextElement();

                if (cur.isLoopback()) {
                    continue;
                }

//                Log.e(TAG, "interface " + cur.getName());

                for (final InterfaceAddress addr : cur.getInterfaceAddresses()) {
                    final InetAddress inet_addr = addr.getAddress();

                    if (!(inet_addr instanceof Inet4Address)) {
                        continue;
                    }

//                    Log.e(TAG,
//                            "  address: " + inet_addr.getHostAddress() +
//                                    "/" + addr.getNetworkPrefixLength()
//                    );

                    Log.e(TAG,
                            "  broadcast address: " +
                                    addr.getBroadcast().getHostAddress()
                    );
                                        //we're safe to cast here because we know that the returned
                                        //value is Inet4Address (checked above)
                    inetAddresses.add((Inet4Address) addr.getBroadcast());

                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return inetAddresses;
    }

    public static void broadCast(final BroadcastCallback callback) {
        List<Inet4Address> broadcastAddresses = getBroadCastAddresses();
        final List<InetAddress> upHosts = new ArrayList<>() ;
        DatagramSocket bSocket = null ;
        try {
            bSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        if (bSocket != null) {
            final DatagramSocket finalBSocket = bSocket;
            for (final Inet4Address address : broadcastAddresses) {
                Log.e(TAG  , "working with address " + address.getHostAddress() );
                network.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG , "executing in background" ) ;
                        byte[] packetBytes =new byte[12];
                        ByteBuffer packetBuffer = ByteBuffer.wrap(packetBytes) ;
                        packetBuffer.clear() ;
                        packetBuffer.putInt(4000).putFloat(0f).putFloat(0f);
                        DatagramPacket bPacket = new DatagramPacket(packetBytes , packetBytes.length  , address , PORT)  ;
                        try {
                            finalBSocket.send(bPacket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            network.submit(new Runnable() {
                @Override
                public void run() {
                    byte[] packetBytes =new byte[12];
                    ByteBuffer packetBuffer = ByteBuffer.wrap(packetBytes) ;
                    DatagramPacket recPacket = new DatagramPacket(packetBytes , packetBytes.length) ;

                    try {
                        while (true) {
                            packetBuffer.clear();
                            finalBSocket.receive(recPacket) ;
                            if (packetBuffer.get() == Byte.MAX_VALUE) {
                                InetAddress current = recPacket.getAddress() ;
                                Log.e("Connection"  , "canonical name : " + current.getCanonicalHostName()
                                + "\nhost name : " + current.getHostName() + "\nhost address : " +
                                        current.getHostAddress() );
                                upHosts.add(current);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            ScheduledExecutorService closingThread = Executors.newSingleThreadScheduledExecutor() ;
            closingThread.schedule(new Runnable() {
                @Override
                public void run() {
                    finalBSocket.close();
                    Handler mainThread = new Handler(Looper.getMainLooper()) ;
                    mainThread.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onBroadcastResults(upHosts) ;
                        }
                    });
                }
            } , 1 , TimeUnit.SECONDS) ;
            closingThread.shutdown();

        }

    }

    public static void init() {
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true) ;
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    public static void connect(InetAddress address) {
        packet = new DatagramPacket(msgBuffer, msgBuffer.length, address, PORT);
    }

    public static void postValues(final int code, final float dx, final float dy) {
        network.submit(new Runnable() {
            @Override
            public void run() {
                byteBuffer.clear();
                byteBuffer.putInt(code).putFloat(dx).putFloat(dy);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
