package org.mmaug.mae.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.github.florent37.glidepalette.GlidePalette;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import org.mmaug.mae.Config;
import org.mmaug.mae.R;
import org.mmaug.mae.base.BaseActivity;
import org.mmaug.mae.models.Candidate;
import org.mmaug.mae.rest.RESTClient;
import org.mmaug.mae.view.AspectRatioImageView;
import org.mmaug.mae.view.AutofitTextView;
import org.mmaug.mae.view.CircularImageView;
import org.mmaug.mae.view.RoundCornerProgressBar;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

public class CandidateCompareActivity extends BaseActivity {
  @Bind(R.id.question_showcase) LinearLayout question_showcase;
  @Bind(R.id.motion_view) LinearLayout motion_view;
  @Bind(R.id.candidateOne_Name) TextView candidateOne;
  @Bind(R.id.candidateTwoName) TextView candidateTwo;
  @Bind(R.id.party_flag_one) AspectRatioImageView party_flag_one;
  @Bind(R.id.party_flag_two) AspectRatioImageView party_flag_two;
  @Bind(R.id.candidate_one) CircularImageView candidateOneView;
  @Bind(R.id.candidate_two) CircularImageView candidateTwoView;
  @Bind(R.id.party_edu_one) AutofitTextView candidateEduOne;
  @Bind(R.id.party_edu_two) AutofitTextView candidateEduTwo;
  @Bind(R.id.party_job_one) TextView partyJobOne;
  @Bind(R.id.party_job_two) TextView partyJobTwo;
  Candidate candidate;
  Candidate candidateCompare;
  float[] hslValues = new float[3];
  float[] darkValue = new float[3];

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);

    //GET candidate value
    setupHeader();

    Call<JsonElement> compareQuestionCall =
        RESTClient.getService().getCompareQuestion("LWMP-01-0063", "LWMP-01-0064");
    compareQuestionCall.enqueue(new Callback<JsonElement>() {
      @Override public void onResponse(Response<JsonElement> response) {
        JsonObject question = response.body().getAsJsonObject().get("questions").getAsJsonObject();

        JsonObject motion =
            response.body().getAsJsonObject().getAsJsonObject("motions").getAsJsonObject();

        Set<Map.Entry<String, JsonElement>> entriesMotions = motion.entrySet();//

        for (Map.Entry<String, JsonElement> entry : entriesMotions) {
          {
            int obtainScrollOne = entry.getValue().getAsJsonObject().get("LWMP-01-0064").getAsInt();
            int obtainScrollTwo = entry.getValue().getAsJsonObject().get("LWMP-01-0063").getAsInt();

            Float PercentageOne;
            Float PercentageTwo;
            int TotalScore =
                entry.getValue().getAsJsonObject().get("LWMP-01-0064").getAsInt() + entry.getValue()
                    .getAsJsonObject()
                    .get("LWMP-01-0063")
                    .getAsInt();
            PercentageOne = Float.valueOf((obtainScrollOne * 100 / TotalScore));
            PercentageTwo = Float.valueOf((obtainScrollTwo * 100 / TotalScore));
            View question_indicator =
                getLayoutInflater().inflate(R.layout.question_compare_layout, motion_view, false);
            TextView questionText = (TextView) question_indicator.findViewById(R.id.question_title);
            RoundCornerProgressBar roundCornerProgressBar =
                (RoundCornerProgressBar) question_indicator.findViewById(R.id.candidate1);
            RoundCornerProgressBar roundCornerProgressBarTwo =
                (RoundCornerProgressBar) question_indicator.findViewById(R.id.candidate2);
            roundCornerProgressBar.setProgress(PercentageOne);
            roundCornerProgressBar.setProgressColor(Color.HSVToColor(hslValues));
            roundCornerProgressBar.setRotation(180);
            roundCornerProgressBar.setBackgroundColor(Color.WHITE);

            roundCornerProgressBarTwo.setProgress(PercentageTwo);
            roundCornerProgressBarTwo.setProgressColor(Color.HSVToColor(darkValue));
            questionText.setText(entry.getKey());
            motion_view.addView(question_indicator);
          }
        }

        Set<Map.Entry<String, JsonElement>> entries =
            question.entrySet();//will return members of your object
        for (Map.Entry<String, JsonElement> entry : entries) {

          int obtainScrollOne = entry.getValue().getAsJsonObject().get("LWMP-01-0064").getAsInt();
          int obtainScrollTwo = entry.getValue().getAsJsonObject().get("LWMP-01-0063").getAsInt();

          Float PercentageOne;
          Float PercentageTwo;
          int TotalScore = entry.getValue().getAsJsonObject().get("LWMP-01-0064").getAsInt() + entry
              .getValue()
              .getAsJsonObject()
              .get("LWMP-01-0063")
              .getAsInt();
          PercentageOne = Float.valueOf((obtainScrollOne * 100 / TotalScore));
          PercentageTwo = Float.valueOf((obtainScrollTwo * 100 / TotalScore));
          View question_indicator =
              getLayoutInflater().inflate(R.layout.question_compare_layout, question_showcase,
                  false);
          TextView questionText = (TextView) question_indicator.findViewById(R.id.question_title);
          RoundCornerProgressBar roundCornerProgressBar =
              (RoundCornerProgressBar) question_indicator.findViewById(R.id.candidate1);
          RoundCornerProgressBar roundCornerProgressBarTwo =
              (RoundCornerProgressBar) question_indicator.findViewById(R.id.candidate2);
          roundCornerProgressBar.setProgress(PercentageOne);
          roundCornerProgressBar.setProgressColor(Color.HSVToColor(hslValues));
          roundCornerProgressBar.setRotation(180);
          roundCornerProgressBar.setBackgroundColor(Color.WHITE);

          roundCornerProgressBarTwo.setProgress(PercentageTwo);
          roundCornerProgressBarTwo.setProgressColor(Color.HSVToColor(darkValue));
          questionText.setText(entry.getKey());
          question_showcase.addView(question_indicator);
        }
      }

      @Override public void onFailure(Throwable t) {

      }
    });
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_candidate_compare;
  }

  @Override protected boolean getHomeUpEnabled() {
    return true;
  }

  @Override protected boolean needToolbar() {
    return true;
  }

  @Override protected String getToolbarText() {
    return getResources().getString(R.string.compare_candidate);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      super.onBackPressed();
      return true;
    }
    return false;
  }

  private void setupHeader() {
    candidate = (Candidate) getIntent().getSerializableExtra(Config.CANDIDATE);
    candidateCompare = (Candidate) getIntent().getSerializableExtra(Config.CANDIDATE_COMPARE);
    candidateOne.setText(candidateCompare.getName());
    candidateTwo.setText(candidate.getName());
    candidateEduOne.setText(candidateCompare.getEducation());
    candidateEduTwo.setText(candidate.getEducation());
    partyJobOne.setText(candidateCompare.getOccupation());
    partyJobTwo.setText(candidate.getOccupation());

    Glide.with(this)
        .load(candidateCompare.getParty().getPartyFlag())
        .listener(GlidePalette.with(candidateCompare.getParty().getPartyFlag())
            .use(GlidePalette.Profile.VIBRANT)
            .use(GlidePalette.Profile.VIBRANT_DARK)
            .use(GlidePalette.Profile.VIBRANT_LIGHT)
            .use(GlidePalette.Profile.MUTED)
            .use(GlidePalette.Profile.MUTED_DARK)
            .use(GlidePalette.Profile.MUTED_LIGHT)
            .intoCallBack(new GlidePalette.CallBack() {
              @Override public void onPaletteLoaded(Palette palette) {
                //specific task
                Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                hslValues = vibrantSwatch.getHsl();
              }
            }))
        .into(party_flag_one);

    Glide.with(this)
        .load(candidate.getParty().getPartyFlag())
        .listener(GlidePalette.with(candidate.getParty().getPartyFlag())
            .use(GlidePalette.Profile.VIBRANT)
            .use(GlidePalette.Profile.VIBRANT_DARK)
            .use(GlidePalette.Profile.VIBRANT_LIGHT)
            .use(GlidePalette.Profile.MUTED)
            .use(GlidePalette.Profile.MUTED_DARK)
            .use(GlidePalette.Profile.MUTED_LIGHT)
            .intoCallBack(new GlidePalette.CallBack() {
              @Override public void onPaletteLoaded(Palette palette) {
                //specific task
                Palette.Swatch darkSwatch = palette.getLightVibrantSwatch();
                darkValue = darkSwatch.getHsl();
              }
            }))
        .into(party_flag_two);

    Glide.with(this).load(candidateCompare.getPhotoUrl()).into(candidateOneView);
/*    candidateOneView.setBorderColor(Color.HSVToColor(hslValues));*/
    candidateOneView.setBorderWidth(3);
    candidateOneView.setBorderColor(R.color.accent_color);


/*    candidateTwoView.setBorderColor(Color.HSVToColor(darkValue));*/
    candidateTwoView.setBorderWidth(3);
    candidateTwoView.setBorderColor(R.color.geojson_background_color);
    Glide.with(this).load(candidate.getPhotoUrl()).into(candidateTwoView);
  }
}
