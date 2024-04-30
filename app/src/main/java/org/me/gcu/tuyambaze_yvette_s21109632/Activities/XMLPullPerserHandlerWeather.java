package org.me.gcu.tuyambaze_yvette_s21109632.Activities;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class XMLPullPerserHandlerWeather {
    private static final String TAG_TITLE = "title";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_PUB_DATE = "pubDate";
    private static final String TAG_GEO_RSS_POINT = "georss:point";
    private static final String TAG_FORECAST_DAY = "forecastDay";
    private static final String TAG_FORECAST_TEMPERATURE = "forecastTemperature";
    private static final String TAG_FORECAST_WEATHER_TYPE = "forecastWeatherType";

    public static List<Weather> parseWeatherData(InputStream inputStream) throws XmlPullParserException, IOException {
        List<Weather> weatherItems = new ArrayList<>();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(inputStream, null);

        int eventType = parser.getEventType();
        Weather currentItem = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = null;
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    tagName = parser.getName();
                    if (TAG_TITLE.equals(tagName)) {
                        currentItem = new Weather();
                        String title = parser.nextText();
                        currentItem.setTitle(title);

                        // Extract weather condition from the title
                        String[] parts = title.split(",");
                        if (parts.length > 0) {
                            String weatherCondition = parts[0].trim();
                            currentItem.setWeatherCondition(weatherCondition);
                        }
                    } else if (TAG_DESCRIPTION.equals(tagName)) {
                        if (currentItem != null) {
                            currentItem.setDescription(parser.nextText());
                        }
                    } else if (TAG_PUB_DATE.equals(tagName)) {
                        if (currentItem != null) {
                            currentItem.setPubDate(parser.nextText());
                        }
                    } else if (TAG_GEO_RSS_POINT.equals(tagName)) {
                        if (currentItem != null) {
                            currentItem.setGeoRssPoint(parser.nextText());
                        }
                    } else if (TAG_FORECAST_DAY.equals(tagName)) {
                        if (currentItem != null) {
                            String forecastDay = parser.nextText();
                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                            SimpleDateFormat outputFormat = new SimpleDateFormat("EEE", Locale.US);
                            try {
                                Date date = inputFormat.parse(forecastDay);
                                currentItem.setForecastDay(outputFormat.format(date));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (TAG_FORECAST_TEMPERATURE.equals(tagName)) {
                        if (currentItem != null) {
                            currentItem.setForecastTemperature(parser.nextText());
                        }
                    } else if (TAG_FORECAST_WEATHER_TYPE.equals(tagName)) {
                        if (currentItem != null) {
                            currentItem.setForecastWeatherType(parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("item".equals(parser.getName())) {
                        if (currentItem != null) {
                            weatherItems.add(currentItem);
                        }
                    }
                    break;
                default:
                    // Do nothing
            }
            eventType = parser.next();
        }

        return weatherItems;
    }
}