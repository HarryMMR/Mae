package org.mmaug.mae.activities;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import org.mmaug.mae.Config;
import org.mmaug.mae.R;
import org.mmaug.mae.models.Candidate;
import org.mmaug.mae.view.ZoomAspectRatioImageView;

public class CandidateDetailActivity extends AppCompatActivity {

  @Bind(R.id.backdrop) ZoomAspectRatioImageView partyImage;
  @Bind(R.id.candidate_avatar) ImageView candidateImage;
  @Bind(R.id.candidate_name) TextView candidateName;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
  @Bind(R.id.appbar) AppBarLayout mAppBarLayout;
  @Bind(R.id.candidate_detail_constituency_pl) TextView mCandidateConstituency;
  @Bind(R.id.candidate_detail_dob) TextView mCandidateDateOfBirth;
  @Bind(R.id.candidate_detail_education) TextView mCandidateEducation;
  @Bind(R.id.candidate_detail_mother) TextView mCandidateMother;
  @Bind(R.id.candidate_detail_father) TextView mCandidateFather;
  @Bind(R.id.candidate_detail_occupation)TextView mCandidateOccupation;
  @Bind(R.id.candidate_detail_race) TextView mCandidateRace;
  @Bind(R.id.candidate_detail_religion)TextView mCandidateReligion;
  @Bind(R.id.candidate_detail_party_flag) ImageView mCandidatePartyFlag;
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_candidate_detail);
    ButterKnife.bind(this);

    //actionbar
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    assert actionBar != null;
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle("");

    Candidate candidate = (Candidate) getIntent().getSerializableExtra(Config.CANDIDATE);
    Glide.with(this).load(candidate.getParty().getPartyFlag()).
        bitmapTransform(new BlurTransformation(getApplicationContext(), 8, 1)).into(partyImage);
    candidateName.setText(candidate.getName());

    //Glide.with(this)
    //    .load(candidate.getPhotoUrl())
    //    .asBitmap()
    //    .into(new BitmapImageViewTarget(candidateImage) {
    //      @Override protected void setResource(Bitmap resource) {
    //        // Do bitmap magic here
    //        super.setResource(resource);
    //        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
    //          public void onGenerated(Palette p) {
    //            // Use generated instance
    //            Palette.Swatch vibrantSwatch = p.getVibrantSwatch();
    //            float[] vibrant = vibrantSwatch.getHsl();
    //            Log.e("Color", "code" + Color.HSVToColor(vibrant));
    //            candidateName.setTextColor(Color.HSVToColor(vibrant));
    //          }
    //        });
    //      }
    //    });
    Glide.with(this)
        .load(candidate.getPhotoUrl())
        .bitmapTransform(new CropCircleTransformation(this))
        .into(candidateImage);

    Glide.with(this)
        .load(candidate.getParty().getPartyFlag())
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(mCandidatePartyFlag);

    mCandidateConstituency.setText(candidate.getConstituency().getName());
    mCandidateDateOfBirth.setText(String.valueOf(candidate.getBirthdate()));
    mCandidateEducation.setText(candidate.getEducation());
    mCandidateFather.setText(candidate.getFather().getName());
    mCandidateMother.setText(candidate.getMother().getName());
    mCandidateOccupation.setText(candidate.getOccupation());
    mCandidateRace.setText(candidate.getEthnicity());
    mCandidateReligion.setText(candidate.getReligion());
  }
}
