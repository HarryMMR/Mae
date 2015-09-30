package org.mmaug.mae.game;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import org.mmaug.mae.Config;
import org.mmaug.mae.R;
import org.mmaug.mae.utils.FontCache;
import org.mmaug.mae.utils.MixUtils;
import org.mmaug.mae.utils.UserPrefUtils;

/**
 * Created by poepoe on 24/9/15.
 */
public class BoardView extends View {

  private Context mContext;
  private Resources res;
  private Canvas canvas;
  private Paint background = new Paint();
  private Paint gridPaint = new Paint();
  private TextPaint textPaint = new TextPaint();
  private Paint partyFlagPaint = new Paint();
  private Bitmap stamp;
  private int stampX, stampY;
  private boolean alredyDrawn = false;
  private boolean isUnicode;
  private ArrayList<Rect> rects; //List of rectangles where the touch of the user needs to be
  // checked
  private int margin;
  private int padding;
  private int marginSmall; //boundaries of table
  private boolean touchMode; //control touch mode by button
  private GameListener listener;
  private String[] candidateName;
  private int[] color = new int[3];
  private Typeface typefacelight;

  int normalTextSize;

  public BoardView(Context context, AttributeSet attrs) {
    super(context, attrs);

    mContext = context;
    res = getResources();

    candidateName = res.getStringArray(R.array.candidate_name);
    color[0] = res.getColor(R.color.red);
    color[1] = res.getColor(R.color.accent_color);
    color[2] = res.getColor(R.color.geojson_background_color);

    margin = (int) MixUtils.convertDpToPixel(context, 16);
    marginSmall = (int) MixUtils.convertDpToPixel(context, 8);
    padding = (int) MixUtils.convertDpToPixel(context, 4);

    typefacelight = FontCache.getTypefaceLight(mContext);
    isUnicode = UserPrefUtils.getInstance(mContext).getTextPref().equals(Config.UNICODE);

    normalTextSize = (int) MixUtils.convertDpToPixel(context, 10);
    //background rect paint
    background.setColor(res.getColor(R.color.board_background));

    //cell rect paint
    gridPaint.setColor(res.getColor(R.color.grey));
    gridPaint.setStyle(Paint.Style.STROKE);
    gridPaint.setStrokeWidth(4);

    //text paint
    textPaint.setColor(res.getColor(R.color.grey));
    textPaint.setAntiAlias(true);
    textPaint.setStyle(Paint.Style.FILL);
    textPaint.setTypeface(typefacelight);


    //flag paint
    partyFlagPaint.setAntiAlias(true);
    partyFlagPaint.setStyle(Paint.Style.FILL);
  }

  public void enableTouch(boolean touchMode) {
    this.touchMode = touchMode;
    setFocusable(touchMode);
    setFocusableInTouchMode(touchMode);
  }

  public void setGameListener(GameListener listener) {
    this.listener = listener;
  }

  //height of the row
  private int getBigCellHeight(int y) {
    return y / 3;
  }

  // first column width for candidate name
  private int getBigCellWith(int x) {
    int ratio = x / 4;
    return ratio * 2;
  }

  //small column for party flag and stamp
  private int getSmallCellWidth(int x) {
    return x / 4;
  }

  //draw grid table
  private void drawBoard() {
    //list of rectangles drawn on canvas, we will use their boundary to check the stamp is valid
    // or not
    rects = new ArrayList<>();
    //align left for the top text
    textPaint.setTextAlign(Paint.Align.LEFT);
    //draw bg
    canvas.drawRect(0, 0, getWidth(), getHeight(), background);
    //draw boundary
    canvas.drawRect(0, 0, getWidth(), getHeight(), gridPaint);

    //set height and width of table
    int x = canvas.getWidth() - (margin * 2);
    int y = getSmallCellWidth(x) * 3;//square cell

    int textAreaHeight = y + (margin * 3); // table area and margin are subtracted from canvas
    // height to write text

    int marginTop = getHeight() - textAreaHeight; // Y coordinate point where table will be drawn

    int titleHeight = marginTop / 4;
    int paraHeight = 3 * titleHeight;
    int paraMargin;

    if (stamp == null) {
      stamp = getStamp(getSmallCellWidth(x), getBigCellHeight(y), R.drawable.ic_stamp);
    }

    int signatureTextSize;
    if (paraHeight < 130) {
      signatureTextSize = marginSmall;
    } else if (paraHeight < 350) {
      signatureTextSize = normalTextSize;
    } else {
      signatureTextSize = marginSmall + padding;
    }
    drawTitle(marginSmall, marginTop);
    //this is the top point of the rectangle and it will need to be recalculated
    //when rows are added
    int top = 0;
    for (int i = 0; i < 3; i++) {
      switch (i) {
        case 0:
          top = marginTop;
          break;
        case 1:
          //first row's height plus spacing between rows
          top = marginTop + getBigCellHeight(y) + padding;
          break;
        case 2:
          //first and second row's height plus spacing between rows
          top = marginTop + (getBigCellHeight(y) * 2) + (padding * 2);
          break;
      }
      addRow(top, x, y, i);
    }

    //align left for the signature text
    textPaint.setTextSize(signatureTextSize);
    textPaint.setTextAlign(Paint.Align.RIGHT);
    int signatureTop = top + getBigCellHeight(y) + margin + padding;
    canvas.drawText(res.getString(R.string.signature_line_1), canvas.getWidth() - margin,
        signatureTop, textPaint);
    canvas.drawText(res.getString(R.string.signature_line_2), canvas.getWidth() - margin,
        signatureTop + margin, textPaint);

    if (alredyDrawn) {
      drawStamp(stampX - (stamp.getWidth() / 2), stampY - (stamp.getHeight() / 2));
    }
  }

  public void reset() {
    alredyDrawn = false;
    invalidate();
  }

  @Override protected void onDraw(Canvas canvas) {
    this.canvas = canvas;
    drawBoard();
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    if (touchMode) {
      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          alredyDrawn = true;
          enableTouch(false);
          stampX = (int) event.getX();
          stampY = (int) event.getY();
          if (checkWithinBounds(stampX, stampY)) {
            listener.checkValidity(ValidityStatus.valid);
          } else {
            listener.checkValidity(ValidityStatus.invalid);
          }
          invalidate();
          return true;
        default:
          return super.onTouchEvent(event);
      }
    }
    return super.onTouchEvent(event);
  }

  private void addRow(int topStart, int x, int y, int i) {
    int left, right, top, bottom;
    top = topStart;
    bottom = top + getBigCellHeight(y);

    for (int j = 0; j < 3; j++) {
      if (j == 0) {
        left = margin;
        right = left + getBigCellWith(x);
      } else if (j == 1) {
        left = margin + getBigCellWith(x);
        right = left + getSmallCellWidth(x);
      } else {
        left = margin + getBigCellWith(x) + getSmallCellWidth(x);
        right = left + getSmallCellWidth(x);
      }

      Rect rect = new Rect(left, top, right, bottom);
      rects.add(rect);

      canvas.drawRect(rect, gridPaint);
      if (j == 1) {
        partyFlagPaint.setColor(color[i]);
        canvas.drawRect(left + marginSmall, top + margin, right - marginSmall, bottom - margin,
            partyFlagPaint);
      }
      if (j == 0) {
        canvas.drawText(candidateName[i], (rect.width() / 2) - (candidateName[i].length() / 2),
            top + rect.height() / 2, textPaint);
      }
    }
  }

  private boolean checkWithinBounds(int x, int y) {
    int left = x - (stamp.getWidth() / 2);
    int top = y - (stamp.getHeight() / 2);
    int right = x + (stamp.getWidth() / 2);
    int bottom = y + (stamp.getHeight() / 2);
    int percent = 4 * (stamp.getWidth() / 10);
    Rect[] rows = {
        new Rect(rects.get(0).left, rects.get(0).top, rects.get(2).right, rects.get(0).bottom),
        new Rect(rects.get(3).left, rects.get(3).top, rects.get(5).right, rects.get(3).bottom),
        new Rect(rects.get(6).left, rects.get(6).top, rects.get(8).right, rects.get(6).bottom)
    };

    //if the stamp is exactly inside a rect, the it is valid
    for (Rect rect : rects) {
      if (rect.left < left && rect.top < top && rect.right > right && rect.bottom > bottom) {
        return true;
      }
    }

    //if the stamp is inside the row of particular candidate, it is valid
    for (Rect row : rows) {
      if (row.left < left && row.top < top && row.right > right && row.bottom > bottom) return true;
    }

    if (rows[0].contains(left, bottom) && rows[0].contains(right, bottom) && !rows[0].contains(left,
        top) && !rows[0].contains(right, top)) {
      //if the bottom of stamp is partially inside row 0, it is valid
      return true;
    } else if (rows[0].contains(left, bottom) && !rows[0].contains(right, top) && !rows[0].contains(
        left, top) && !rows[0].contains(right, bottom)) {
      //if the left bottom corner of stamp is partially inside row 0, it is valid
      return true;
    } else if (rows[0].contains(right, bottom) && !rows[0].contains(left, top) && !rows[0].contains(
        left, bottom) && !rows[0].contains(right, top)) {
      //if the right bottom corner of stamp is partially inside row 0, it is valid
      return true;
    }

    if (rows[2].contains(left, top) && rows[2].contains(right, top) && !rows[2].contains(left,
        bottom) && !rows[2].contains(right, bottom)) {
      //if the top of the stamp is partially inside row 2, it is valid
      return true;
    } else if (rows[2].contains(left, top) && !rows[2].contains(right, top) && !rows[2].contains(
        left, bottom) &&
        !rows[2].contains(right, bottom)) {
      //if the top left corner of the stamp is partially inside row 2, it is valid
      return true;
    } else if (rows[2].contains(right, top) && !rows[2].contains(left, top) && !rows[2].contains(
        left, bottom) && !rows[2].contains(right, bottom)) {
      //if the top right corner of the stamp is partially inside row 2, it is valid
      return true;
    }

    //check if right and left of the stamp is partially inside row
    for (Rect row : rows) {
      if (row.contains(right, top) && row.contains(right, bottom) && !row.contains(left, top) &&
          !row.contains(left, bottom)) {
        return true;
      } else if (row.contains(left, top)
          && row.contains(left, bottom)
          && !row.contains(right, top)
          && !row.contains(right, bottom)) {
        return true;
      }
    }

    // check percentage if the stamp is at intersection of Rect
    for (Rect rect : rects) {
      if (rect.contains(left, top) && rect.contains(right, top) && rect.bottom > (bottom - percent)
          || (rect.contains(right, top) && rect.contains(right, bottom) && rects.get(0).left < (left
          - percent))
          || (rect.contains(left, top) && rect.contains(left, bottom) && rect.right > (right
          - percent))
          || (rect.contains(left, bottom) && rect.contains(right, bottom) && rect.top < (top
          - percent))) {
        return true;
      }
    }

    // check percentage if the stamp is at intersection of row
    for (Rect rect : rows) {
      if (rect.contains(left, top) && rect.contains(right, top) && rect.bottom > (bottom - percent)
          || (rect.contains(right, top) && rect.contains(right, bottom) && rects.get(0).left < (left
          - percent))
          || (rect.contains(left, top) && rect.contains(left, bottom) && rect.right > (right
          - percent))
          || (rect.contains(left, bottom) && rect.contains(right, bottom) && rect.top < (top
          - percent))) {
        return true;
      }
    }
    return false;
  }

  public enum ValidityStatus {
    valid, invalid
  }

  public interface GameListener {
    void checkValidity(ValidityStatus status);
  }

  private Bitmap getStamp(int width, int height, int icon) {
    Drawable drawable = res.getDrawable(icon);
    assert drawable != null;
    Bitmap b = ((BitmapDrawable) drawable).getBitmap();
    return Bitmap.createScaledBitmap(b, width - margin, height - margin, false);
  }

  private Bitmap getTitleBitmap(int height) {
    Drawable drawable = res.getDrawable(R.drawable.example_info);
    assert drawable != null;
    height = height - margin;
    Bitmap b = ((BitmapDrawable) drawable).getBitmap();
    return Bitmap.createScaledBitmap(b, (int) (height * 3.25), height, false);
  }


  private void drawStamp(int x, int y) {
    canvas.drawBitmap(stamp, x, y, null);
  }

  private void drawTitle(int y, int height) {
    canvas.drawBitmap(getTitleBitmap(height), margin, y, null);
  }
}
