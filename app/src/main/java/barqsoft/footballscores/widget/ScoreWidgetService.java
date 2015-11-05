package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.scoresAdapter;

/**
 * Created by gishiru on 2015/11/05.
 */
public class ScoreWidgetService extends IntentService {
  private static final String LOG_TAG = ScoreWidgetService.class.getSimpleName();

  public ScoreWidgetService() {
    super("ScoreWidgetIntentService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    // Retrieve all of the widget IDs.
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
        new ComponentName(this, ScoreWidgetProvider.class));

    // Get today's score from database.
    String[] fragmentDate = new String[1];
    fragmentDate[0] = new SimpleDateFormat("yyyy-MM-dd")
        .format(new Date(System.currentTimeMillis() + ((0 - 2) * 86400000)));
    Cursor data = getContentResolver().query(
        DatabaseContract.scores_table.buildScoreWithDate(),
        null,
        null,
        fragmentDate,
        null,
        null);
    if (data == null) {
      return;
    }
    if (!data.moveToFirst()) {
      Log.e(LOG_TAG, "no data");
      data.close();
      return;
    }

    // Update each widgets.
    for (int appWidgetId : appWidgetIds) {
      RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.widget_score);

      // Add data to the remote views.
      views.setTextViewText(R.id.widget_date_textview, data.getString(scoresAdapter.COL_DATE));
      views.setTextViewText(R.id.widget_away_name, data.getString(scoresAdapter.COL_AWAY));
      views.setImageViewResource(
          R.id.widget_away_crest,
          Utilies.getTeamCrestByTeamName(data.getString(scoresAdapter.COL_AWAY)));
      views.setTextViewText(R.id.widget_home_name, data.getString(scoresAdapter.COL_HOME));
      views.setImageViewResource(
          R.id.widget_home_crest,
          Utilies.getTeamCrestByTeamName(data.getString(scoresAdapter.COL_HOME)));
      views.setTextViewText(
          R.id.widget_score_textview,
          Utilies.getScores(
              data.getInt(scoresAdapter.COL_HOME_GOALS),
              data.getInt(scoresAdapter.COL_AWAY_GOALS)));

      // Start main activity if the widget is clicked.
      PendingIntent pendingIntent =
          PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
      views.setOnClickPendingIntent(R.id.widget_score_id, pendingIntent);

      // Tell the widget manager to perform an update on the current app widget
      appWidgetManager.updateAppWidget(appWidgetId, views);
    }
  }
}
