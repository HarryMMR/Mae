package org.mmaug.mae.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.ArrayList;
import org.mmaug.mae.R;
import org.mmaug.mae.adapter.HowToVoteTimelineAdapter;
import org.mmaug.mae.base.BaseActivity;

/**
 * Created by poepoe on 22/9/15.
 */
public class HowToVoteActivity extends BaseActivity implements AdapterView.OnItemClickListener {

  @Bind(R.id.rv_how_to_vote) RecyclerView mRecyclerView;
  private HowToVoteTimelineAdapter adapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    mRecyclerView.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    adapter = new HowToVoteTimelineAdapter();
    adapter.setOnItemClickListener(this);
    mRecyclerView.setAdapter(adapter);
    fakeData();
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_how_to_vote;
  }

  @Override protected boolean getHomeUpEnabled() {
    return true;
  }

  @Override protected boolean needToolbar() {
    return true;
  }

  @Override protected String getToolbarText() {
    return "မဲပေးရာတွင်သိသင့်သည်များ";
  }

  private void fakeData() {
    ArrayList<HowToVoteTimelineAdapter.HTVObject> objects = new ArrayList<>();

    HowToVoteTimelineAdapter.HTVObject object = new HowToVoteTimelineAdapter.HTVObject();
    object.setTitle("မဲရုံဖွင့်ချိန် ဂရုပြုပါ");
    object.setMessage("\u23F0 " + "နံနက်၆နာရီ−ညနေ၆နာရီ");
    object.setWarning(
        "မဲရုံပိတ\u103Aခ\u103Bိန\u103A မတိုင\u103Aမီ မဲရုံသို့ သ\u103Dားရောက\u103A မဲပေးရန\u103A လိုအပ\u103Aပ\u102Bသည\u103A။");
    objects.add(object);

    object = new HowToVoteTimelineAdapter.HTVObject();
    object.setTitle("မဲလက်မှတ် ထုတ်ပါ");
    object.setMessage("နိုင်ငံသားမှတ်ပုံတင်  ယူဆောင်သွားရပါမည်။");
    object.setWarning("မဲလက်မှတ်ကို စစ်ဆေးရန် မမေ့ပါနှင့်။");
    objects.add(object);

    object = new HowToVoteTimelineAdapter.HTVObject();
    object.setTitle("ခိုင်လုံမဲ နမူနာများအား ကြိုတင်လေ့လာပါ။");
    object.setMessage(null);
    object.setDrawable(R.drawable.ic_uec);
    object.setWarning("မိမိ၏အဖိုးတန် မဲတစ်ပြားအား အလဟဿ မဖြစ်စေရန် ဤနေရာ ကိုနှိပ်၍ လေ့လာပါ။");
    objects.add(object);

    object = new HowToVoteTimelineAdapter.HTVObject();
    object.setTitle("လျှိုဝှက်မဲပေးခန်းသို့ သွားပါ။");
    object.setMessage("မဲပြားပေါ်မှာ လျှို့ဝှက်စွာ ဆန္ဒပြုနိုင်မှာပါ။");
    object.setWarning("လျှိုဝှက်စွာ ဆန္ဒပြုနိုင်ရန် လုံခြုံခြင်း ရှိ၊ မရှိကို သင်ကိုယ်တိုင် "
        + "သိရှိခံစားရပါလိမ့်မည်။");
    objects.add(object);

    object = new HowToVoteTimelineAdapter.HTVObject();
    object.setTitle("မဲသုံးကြိမ်  ပေးရမည်");
    object.setMessage(null);
    object.setDrawable(R.drawable.ic_hluttaw);
    object.setWarning("မိမိ၏ တိုင်းရင်းသားကိုယ်စားလှယ် ရှိလျှင် မဲ (၄)ကြိမ် ပေးရပါမည်။");
    objects.add(object);

    object = new HowToVoteTimelineAdapter.HTVObject();
    object.setTitle("မဲပေးပြီးလျှင်");
    object.setMessage("မဲပေးပြီးကြောင်း လက်သန်းတွင် မင်တို့ခံပါ။");
    object.setWarning("နောက်တစ်ကြိမ် ဆန္ဒမဲပေးနိုင်ခြင်း မရှိရန် တာဝန်ရှိသူက ညွှန်ကြားသည့်အတိုင်း"
        + " လက်ချောင်းတစ်ချောင်းတွင် မင်တို့ခြင်း ပြုလုပ်ရမည်။");
    objects.add(object);

    adapter.setHtvObjectList(objects);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      super.onBackPressed();
      return true;
    }
    return false;
  }

  @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    if (position == 2) startActivity(new Intent(this, VoteGameActivity.class));
  }
}
