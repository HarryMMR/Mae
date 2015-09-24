package org.mmaug.mae.activities;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.aut.utillib.circular.animation.CircularAnimationUtils;
import org.mmaug.mae.R;
import org.mmaug.mae.base.BaseActivity;
import org.mmaug.mae.game.BoardView;
import org.mmaug.mae.utils.MixUtils;

/**
 * Created by poepoe on 24/9/15.
 */
public class VoteGameActivity extends BaseActivity implements BoardView.GameListener {

  @Bind(R.id.board) BoardView mBoardView;
  @Bind(R.id.ll_board_frame) LinearLayout mBoardFame;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);

    // set height for paper
    ViewGroup.LayoutParams params = mBoardFame.getLayoutParams();

    //top and bottom margin
    int margin = (int) MixUtils.convertDpToPixel(this, 24) * 3;
    //actionbar size to subtract too
    int actionBarSzie = (int) MixUtils.convertDpToPixel(this, 56);

    // subtract margin to fix the screen
    params.height = MixUtils.calculateHeight(getWindowManager()) - (margin + actionBarSzie);
    mBoardFame.setLayoutParams(params);

    //listener for game play
    mBoardView.setGameListener(this);
  }

  @OnClick(R.id.cardview_start_game) void startGame() {
    mBoardView.enableTouch(true);
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_vote_game;
  }

  @Override protected boolean getHomeUpEnabled() {
    return true;
  }

  @Override protected boolean needToolbar() {
    return true;
  }

  @Override protected String getToolbarText() {
    return "ခိုင်လုံမဲဖြစ်စေရန်";
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      super.onBackPressed();
      return true;
    }
    return false;
  }

  @Override public void checkValidity(BoardView.ValidityStatus status) {
    final Dialog voteResultDialog =
        new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
    View view;
    if (status == BoardView.ValidityStatus.valid) {
      view = getLayoutInflater().inflate(R.layout.dialog_valid_vote, null);
      View myTargetView = view.findViewById(R.id.circle_full);
      View mySourceView = view.findViewById(R.id.circle_empty);
      View okBtn = view.findViewById(R.id.voter_check_ok_btn);
      //text message
      TextView myTextView = (TextView) view.findViewById(R.id.tv_vote_message);
      String warning = getString(R.string.how_to_vote_placeholder_text,
          getString(R.string.correct_vote_message));
      myTextView.setText(Html.fromHtml(warning));
      //myTargetView & mySourceView are children in the CircularFrameLayout
      float finalRadius = CircularAnimationUtils.hypo(200, 200);
      ////getCenter computes from 2 view: One is touched, and one will be animated, but you can use anything for center
      //int[] center = CircularAnimationUtils.getCenter(fab, myTargetView);
      ObjectAnimator animator =
          CircularAnimationUtils.createCircularTransform(myTargetView, mySourceView, 1, 2, 0F,
              finalRadius);
      animator.setInterpolator(new AccelerateDecelerateInterpolator());
      animator.setDuration(1500);
      voteResultDialog.setContentView(view);
      voteResultDialog.show();
      animator.start();
      okBtn.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (voteResultDialog.isShowing()) {
            voteResultDialog.dismiss();
          }
        }
      });
    } else {
      view = getLayoutInflater().inflate(R.layout.dialog_invalid_vote, null);
      View okBtn = view.findViewById(R.id.voter_check_ok_btn);
      TextView myTextView = (TextView) view.findViewById(R.id.tv_vote_message);
      String warning =
          getString(R.string.incorrect_vote_message, getString(R.string.correct_vote_message));
      myTextView.setText(warning);
      okBtn.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (voteResultDialog.isShowing()) {
            voteResultDialog.dismiss();
          }
        }
      });
      voteResultDialog.setContentView(view);
      voteResultDialog.show();
    }
  }
}
