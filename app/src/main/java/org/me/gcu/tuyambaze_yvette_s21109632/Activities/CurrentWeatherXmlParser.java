package org.me.gcu.tuyambaze_yvette_s21109632.Activities;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CurrentWeatherXmlParser {
    private static final String ns = null;

    public CurrentWeather parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private CurrentWeather readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        CurrentWeather currentWeather = null;

        parser.require(XmlPullParser.START_TAG, ns, "rss");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("channel")) {
                currentWeather = readChannel(parser);
            } else {
                skip(parser);
            }
        }
        return currentWeather;
    }

    private CurrentWeather readChannel(XmlPullParser parser) throws XmlPullParserException, IOException {
        CurrentWeather currentWeather = null;

        parser.require(XmlPullParser.START_TAG, ns, "channel");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("item")) {
                currentWeather = readItem(parser);
            } else {
                skip(parser);
            }
        }
        return currentWeather;
    }

    private CurrentWeather readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String title = null;
        String description = null;
        String pubDate = null;
        String georssPoint = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "title":
                    title = readTitle(parser);
                    break;
                case "description":
                    description = readDescription(parser);
                    break;
                case "pubDate":
                    pubDate = readPubDate(parser);
                    break;
                case "georss:point":
                    georssPoint = readGeorssPoint(parser);
                    break;
                default:
                    skip(parser);
            }
        }

        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setTitle(title);
        currentWeather.setPubDate(pubDate);
        currentWeather.setGeorssPoint(georssPoint);

        if (pubDate != null) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd yyyy", Locale.US);
            try {
                Date date = inputFormat.parse(pubDate);
                currentWeather.setDayOfWeek(new SimpleDateFormat("EEEE", Locale.US).format(date));
                currentWeather.setFormattedDate(outputFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (description != null) {
            String[] descriptionParts = description.split(",");
            for (String part : descriptionParts) {
                part = part.trim();
                if (part.startsWith("Temperature:")) {
                    currentWeather.setTemperature(part.substring("Temperature:".length()).trim());
                } else if (part.startsWith("Wind Direction:")) {
                    currentWeather.setWindDirection(part.substring("Wind Direction:".length()).trim());
                } else if (part.startsWith("Wind Speed:")) {
                    currentWeather.setWindSpeed(part.substring("Wind Speed:".length()).trim());
                } else if (part.startsWith("Humidity:")) {
                    currentWeather.setHumidity(part.substring("Humidity:".length()).trim());
                } else if (part.startsWith("Pressure:")) {
                    currentWeather.setPressure(part.substring("Pressure:".length()).trim());
                } else if (part.startsWith("Visibility:")) {
                    currentWeather.setVisibility(part.substring("Visibility:".length()).trim());
                }
            }
        }

        return currentWeather;
    }

    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return description;
    }

    private String readPubDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "pubDate");
        String pubDate = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "pubDate");
        return pubDate;
    }

    private String readGeorssPoint(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "georss:point");
        String georssPoint = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "georss:point");
        return georssPoint;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
