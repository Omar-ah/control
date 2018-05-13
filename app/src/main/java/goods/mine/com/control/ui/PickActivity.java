package goods.mine.com.control.ui;

import android.content.Intent;
import android.os.Bundle;
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
import goods.mine.com.control.network.BroadcastCallback;
import goods.mine.com.control.network.Connection;

public class PickActivity extends AppCompatActivity implements BroadcastCallback, View.OnClickListener {

    private RecyclerView mRecyclerView;
    HostsAdapter adapter ;
    private ProgressBar progressBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);

        progressBar =  findViewById(R.id.progress_bar);

        mRecyclerView = findViewById(R.id.hosts_recycler_view);

        if (savedInstanceState == null) {
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);

            mRecyclerView.setLayoutManager(mLayoutManager) ;

            Connection.broadCast(this) ;
        }else {
            progressBar.setVisibility(View.GONE) ;
            mRecyclerView.setVisibility(View.VISIBLE) ;
        }
    }

    //this call back will be called upon completion of service discovery process
    @Override
    public void onBroadcastResults(List<InetAddress> upHosts) {
        adapter = new HostsAdapter(upHosts , this) ;
        mRecyclerView.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE) ;
    }

    //this call back will be reported to recycler view items
    @Override
    public void onClick(View v) {
        int position = mRecyclerView.getChildLayoutPosition(v) ;
        if (position == RecyclerView.NO_POSITION) {
            Toast.makeText(this, "sorry, an error occured", Toast.LENGTH_SHORT).show();
            return ;
        }
        Connection.connect(adapter.addresses.get(position));
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
                refreshHostsList();
                return true ;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void refreshHostsList() {
        mRecyclerView.setVisibility(View.GONE ) ;
        progressBar.setVisibility(View.VISIBLE);
        Connection.broadCast(this) ;
    }
}
