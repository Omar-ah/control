package goods.mine.com.control.network;

import java.net.InetAddress;
import java.util.List;

public interface BroadcastCallback {
    void onBroadcastResults(List<InetAddress> upHosts) ;
}
