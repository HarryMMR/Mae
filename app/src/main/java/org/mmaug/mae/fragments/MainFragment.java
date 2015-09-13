package org.mmaug.mae.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.Calendar;
import org.mmaug.mae.R;
import org.mmaug.mae.adapter.AvatarAdapter;
import org.mmaug.mae.models.Avatar;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment
    implements com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {

  @Bind(R.id.avatars) GridView mAvatarGrid;
  @Bind(R.id.date_of_birth) TextView mDateOfBirth;
  @Bind(R.id.user_name) EditText mUserName;
  @Bind(R.id.nrc_no) EditText mNrcNo;
  @Bind(R.id.nrc_township) EditText mNrcTownShip;
  @Bind(R.id.nrc_value) EditText mNrcValue;
  @Bind(R.id.contentFragment) FrameLayout contenFragment;
  @Bind(R.id.main_fragment) RelativeLayout main_view;
  @Bind(R.id.post_fab) FloatingActionButton post_fab;
  Calendar now;
  int maxAgeforVote = 18;
  String DATE_TAG = "Datepickerdialog";
  private Avatar mSelectedAvatar = Avatar.ONE;
  private View mSelectedAvatarView;

  public MainFragment() {
  }

  private void setUpGridView() {
    mAvatarGrid.setAdapter(new AvatarAdapter(getActivity()));
    mAvatarGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mSelectedAvatarView = view;
        mSelectedAvatar = Avatar.values()[position];
      }
    });
    mAvatarGrid.setNumColumns(4);
    mAvatarGrid.setItemChecked(mSelectedAvatar.ordinal(), true);
  }

  @OnClick(R.id.post_fab) void checkVote() {

    main_view.setVisibility(View.GONE);
    post_fab.setVisibility(View.GONE);
    contenFragment.setVisibility(View.VISIBLE);
    HomeFragment homeFragment = new HomeFragment();
    FragmentManager fm = getActivity().getSupportFragmentManager();
    FragmentTransaction transaction = fm.beginTransaction();
    transaction.replace(R.id.contentFragment, homeFragment);
    transaction.commit();
   /* final String voterName = mUserName.getText().toString();
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(mNrcNo.getText().toString());
    stringBuilder.append("/");
    stringBuilder.append(mNrcTownShip.getText().toString());
    stringBuilder.append("(နိုင်)");
    stringBuilder.append(mNrcValue.getText());
    String voterNrc = stringBuilder.toString();
    Map<String, String> params = new HashMap<>();
    //params.put(Config.DATE_OF_BIRTH, "1945-06-19");
    params.put(Config.NRCNO, voterNrc);
    //params.put(Config.FATHER_NAME, "ဦးအောင်ဆန်း");
    Call<Voter> voterCall = RESTClient.getService().searchVoter(voterName, params);
    voterCall.enqueue(new Callback<Voter>() {
      @Override public void onResponse(Response<Voter> response) {
        Log.e("Response", "" + response.message());

        Voter voter = response.body();


        //TODO check null value return  Log.e("Voter", "" + response.body());
      }

      @Override public void onFailure(Throwable t) {
        t.printStackTrace();
      }
    });*/
  }

  @OnClick(R.id.date_of_birth) void DatePicker() {
    now = Calendar.getInstance();
    com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog =
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(this,
            now.get(Calendar.YEAR) - maxAgeforVote, now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH));
    datePickerDialog.show(getActivity().getFragmentManager(), DATE_TAG);
  }

  //TODO reenable
  private int calculateSpanCount() {
    int avatarSize = getResources().getDimensionPixelSize(R.dimen.size_fab);
    int avatarPadding = getResources().getDimensionPixelSize(R.dimen.spacing_micro);
    return mAvatarGrid.getWidth() / (avatarSize + avatarPadding);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_main, container, false);
    ButterKnife.bind(this, rootView);
    main_view.setVisibility(View.VISIBLE);
    post_fab.setVisibility(View.VISIBLE);
    setUpGridView();
    return rootView;
  }

  @Override
  public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year,
      int monthOfYear, int dayOfMonth) {
    now = Calendar.getInstance();
    if ((now.get(Calendar.YEAR) - year) >= 18) {
      mDateOfBirth.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
    }
  }
}
