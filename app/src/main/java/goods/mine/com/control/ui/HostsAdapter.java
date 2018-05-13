package goods.mine.com.control.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.InetAddress;
import java.util.List;

import goods.mine.com.control.R;

public class HostsAdapter extends RecyclerView.Adapter<HostsAdapter.HostViewHolder> {

    List<InetAddress> addresses ;
    View.OnClickListener mListener ;

    public HostsAdapter(List<InetAddress> addresses , View.OnClickListener listener) {
        mListener = listener ;
        this.addresses = addresses ;
    }
    @NonNull
    @Override
    public HostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item , parent ,false) ;
        root.setOnClickListener(mListener);
        return new HostViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull HostViewHolder holder, int position) {
//        holder.hostAddress.setText(addresses.get(position).getHostAddress());
        holder.hostName.setText(addresses.get(position).getHostName());
        holder.progressBar.setVisibility(View.INVISIBLE) ;
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public static class HostViewHolder extends RecyclerView.ViewHolder{

        TextView hostName ;
//        TextView hostAddress ;
        ProgressBar progressBar ;
        public HostViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.connect_progress) ;
            hostName = itemView.findViewById(R.id.host_name);
//            hostAddress = itemView.findViewById(R.id.host_address) ;
        }
    }
}
