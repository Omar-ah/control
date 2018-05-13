package goods.mine.com.control.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.List;

import goods.mine.com.control.R;
import goods.mine.com.control.network.Connection;
import goods.mine.com.control.viewmodel.PickViewModel;

public class PickActivity extends AppCompatActivity implements View.OnClickListener , Observer<List<InetAddress>>{

    private RecyclerView mRecyclerView;
    private HostsAdapter adapter ;
    private ProgressBar progressBar ;
    private PickViewModel mPickViewModel ;

    private View lastClickedItem ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);

        progressBar =  findViewById(R.id.progress_bar);
        mRecyclerView = findViewById(R.id.hosts_recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager) ;

        mPickViewModel = ViewModelProviders.of(this).get(PickViewModel.class) ;
        mPickViewModel.getAddresses().observe(this , this) ;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lastClickedItem != null) {
            lastClickedItem.findViewById(R.id.connect_progress).setVisibility(View.INVISIBLE) ;
        }
    }

    //this call back will be reported to recycler view items
    @Override
    public void onClick(View v) {
        lastClickedItem = v ;
        int position = mRecyclerView.getChildLayoutPosition(v) ;
        if (position == RecyclerView.NO_POSITION) {
            Toast.makeText(this, "sorry, an error occured", Toast.LENGTH_SHORT).show();
            return ;
        }
        v.findViewById(R.id.connect_progress).setVisibility(View.VISIBLE);
        Connection.connect(adapter.addresses.get(position));
//        v.findViewById(R.id.connect_progress).setVisibility(View.GONE);
        startActivity(new Intent(PickActivity.this , ControlActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pick_activity_options ,menu) ;
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_menu_item :
                requestToRefreshHostsList();
                return true ;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void requestToRefreshHostsList() {
        mRecyclerView.setVisibility(View.GONE ) ;
        progressBar.setVisibility(View.VISIBLE);
        mPickViewModel.refreshHostsList();
    }

    @Override
    public void onChanged(@Nullable List<InetAddress> upHosts) {
        adapter = new HostsAdapter(upHosts , this) ;
        mRecyclerView.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE) ;
    }
}
