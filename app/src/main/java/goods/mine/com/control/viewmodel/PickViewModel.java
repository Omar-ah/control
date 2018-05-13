package goods.mine.com.control.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.net.InetAddress;
import java.util.List;

import goods.mine.com.control.network.BroadcastCallback;
import goods.mine.com.control.network.Connection;

public class PickViewModel extends AndroidViewModel implements BroadcastCallback{

    MutableLiveData<List<InetAddress>> addresses ;

    public PickViewModel(@NonNull Application application) {
        super(application);
        addresses = new MutableLiveData<>() ;
        Connection.broadCast(this) ;
    }

    public LiveData<List<InetAddress>> getAddresses() {
        return addresses;
    }


    @Override
    public void onBroadcastResults(List<InetAddress> upHosts) {
        //we're calling setValue instead of postValue because we handled the threading issues in Connection class
        addresses.setValue(upHosts);
    }

    public void refreshHostsList() {
        Connection.broadCast(this) ;
    }
}
