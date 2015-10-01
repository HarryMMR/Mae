package org.mmaug.mae.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mmaug.mae.Config;
import org.mmaug.mae.R;
import org.mmaug.mae.adapter.EndlessRecyclerViewAdapter;
import org.mmaug.mae.adapter.PartyAdapter;
import org.mmaug.mae.base.BaseActivity;
import org.mmaug.mae.models.Party;
import org.mmaug.mae.models.PartyReturnObject;
import org.mmaug.mae.rest.RESTClient;
import org.mmaug.mae.utils.MixUtils;
import org.mmaug.mae.view.SpacesItemDecoration;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import timber.log.Timber;
import tr.xip.errorview.ErrorView;

/**
 * Created by indexer on 9/19/15.
 */

public class PartyListActivity extends BaseActivity
    implements PartyAdapter.ClickInterface, ErrorView.RetryListener {

  String TAG_TOOLBAR = "ပါတီစာရင်း";
  Integer currentPage = 1;
  int totalPageCount;

  @Bind(R.id.party_list_recycler_view) RecyclerView mPartyListRecyclerView;
  @Bind(R.id.progressBar) ProgressBar mProgressBar;
  @Bind(R.id.error_view) ErrorView mErrorView;
  PartyAdapter mPartyAdapter;
  private EndlessRecyclerViewAdapter endlessRecyclerViewAdapter;
  private List<Party> mParties = new ArrayList<>();

  @Override protected int getLayoutResource() {
    return R.layout.activity_party;
  }

  @Override protected boolean getHomeUpEnabled() {
    return true;
  }

  @Override protected boolean needToolbar() {
    return true;
  }

  @Override protected String getToolbarText() {
    return TAG_TOOLBAR;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);

    mPartyAdapter = new PartyAdapter();
    mPartyListRecyclerView.setHasFixedSize(true);
    mPartyListRecyclerView.setLayoutManager(new GridLayoutManager(PartyListActivity.this, 2));
    mPartyListRecyclerView.addItemDecoration(new SpacesItemDecoration(2));
    endlessRecyclerViewAdapter = new EndlessRecyclerViewAdapter(this, mPartyAdapter,
        new EndlessRecyclerViewAdapter.RequestToLoadMoreListener() {
          @Override public void onLoadMoreRequested() {
            fetchParties();
          }
        });
    mPartyAdapter.setOnItemClickListener(this);
    mPartyListRecyclerView.setAdapter(endlessRecyclerViewAdapter);
    ((GridLayoutManager) mPartyListRecyclerView.getLayoutManager()).setSpanSizeLookup(
        new GridLayoutManager.SpanSizeLookup() {
          @Override public int getSpanSize(int position) {
            if (endlessRecyclerViewAdapter.getItemViewType(position)
                == EndlessRecyclerViewAdapter.TYPE_PENDING) {
              return 2;
            } else {
              return 1;
            }
          }
        });

    mErrorView.setOnRetryListener(this);

    MixUtils.toggleVisibilityWithAnim(mProgressBar, true);
    MixUtils.toggleVisibilityWithAnim(mPartyListRecyclerView, false);
    MixUtils.toggleVisibilityWithAnim(mErrorView, false);

  }

  private void fetchParties() {
    Map<String, String> params = new HashMap<>();
    params.put(Config.PAGE, currentPage.toString());
    params.put(Config.PER_PAGE, "20");
    final Call<PartyReturnObject> partyCall = RESTClient.getService(this).getPartyList(params);
    partyCall.enqueue(new Callback<PartyReturnObject>() {
                        @Override public void onResponse(Response<PartyReturnObject> response) {
                          totalPageCount = response.body().getMeta().getTotal_pages();
                          if (response.body().getData().size() > 0) {
                            if (currentPage == 1) {
                              mParties = response.body().getData();
                              MixUtils.toggleVisibilityWithAnim(mPartyListRecyclerView, true);
                              MixUtils.toggleVisibilityWithAnim(mProgressBar, false);
                            } else {
                              mParties.addAll(response.body().getData());
                            }
                            mPartyAdapter.setParties(mParties);
                            endlessRecyclerViewAdapter.onDataReady(true);
                            currentPage++;
                          } else {
                            endlessRecyclerViewAdapter.onDataReady(false);
                          }
                        }

                        @Override public void onFailure(Throwable t) {
                          if (currentPage == 1) {
                            MixUtils.toggleVisibilityWithAnim(mErrorView, true);
                          }
                          Timber.e(t.getMessage());
                        }
                      }

    );
  }

  @Override public void onItemClick(View view, int position) {
    Intent intent = new Intent();
    intent.setClass(this, PartyDetailActivity.class);
    Bundle bundle = new Bundle();
    bundle.putSerializable("party", mParties.get(position));
    intent.putExtras(bundle);
    startActivity(intent);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      super.onBackPressed();
      return true;
    }
    return false;
  }

  @Override public void onRetry() {
    fetchParties();
  }
}
