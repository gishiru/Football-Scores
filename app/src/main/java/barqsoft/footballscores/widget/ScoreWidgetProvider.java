package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import barqsoft.footballscores.service.myFetchService;

/**
 * Created by gishiru on 2015/11/03.
 */
public class ScoreWidgetProvider extends AppWidgetProvider {
  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);

    if (myFetchService.ACTION_DATA_UPDATED.equals(intent.getAction())) {
      context.startService(new Intent(context, myFetchService.class));
    }
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    super.onUpdate(context, appWidgetManager, appWidgetIds);

    context.startService(new Intent(context, ScoreWidgetService.class));
  }
}
