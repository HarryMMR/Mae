package org.mmaug.mae.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.ArrayList;
import org.mmaug.mae.Config;
import org.mmaug.mae.R;
import org.mmaug.mae.base.BaseAdapter;
import org.mmaug.mae.utils.DataUtils;
import org.mmaug.mae.utils.FontCache;
import org.mmaug.mae.utils.MMTextUtils;
import org.mmaug.mae.utils.UserPrefUtils;

/**
 * Created by poepoe on 16/9/15.
 */
public class TownshipAdapter extends BaseAdapter<BaseAdapter.BaseViewHolder> {
  Context mContext;
  private ArrayList<DataUtils.Township> townships;

  private Typeface typeface;
  private boolean isUni;
  private MMTextUtils mmTextUtils;

  public TownshipAdapter(ArrayList<DataUtils.Township> townships) {
    this.townships = townships;
  }

  public void setTownships(ArrayList<DataUtils.Township> townships) {
    this.townships = townships;
    notifyDataSetChanged();
  }

  @Override public BaseAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    mContext = parent.getContext();

    //get typeface
    typeface = FontCache.getTypefaceLight(mContext);
    //check user has choose unicode or not
    isUni = UserPrefUtils.getInstance(mContext).getTextPref().equals(Config.UNICODE);
    mmTextUtils = MMTextUtils.getInstance(mContext);

    View itemView = inflater.inflate(R.layout.item_township, parent, false);
    return new ViewHolder(itemView, this);
  }

  @Override public void onBindViewHolder(BaseViewHolder holder, int position) {
    String townshipName = townships.get(position).getTowhshipNameBurmese();
    ViewHolder myHolder = ((ViewHolder) holder);
    myHolder.mText.setText(townshipName);

    if (isUni) {//if uni set typeface
      myHolder.mText.setTypeface(typeface);
    } else {//force to show uni
      mmTextUtils.prepareSingleView(myHolder.mText);
    }
  }

  @Override public int getItemCount() {
    return townships.size();
  }

  class ViewHolder extends BaseAdapter.BaseViewHolder {
    @Bind(R.id.tv_township) TextView mText;

    public ViewHolder(View itemView, TownshipAdapter adapter) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      itemView.setOnClickListener(this);
      mAdapter = adapter;
    }
  }
}