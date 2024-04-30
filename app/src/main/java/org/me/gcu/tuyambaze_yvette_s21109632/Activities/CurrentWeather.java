package org.me.gcu.tuyambaze_yvette_s21109632.Activities;

public class CurrentWeather {
    private String title;
    private String temperature;
    private String windDirection;
    private String windSpeed;
    private String humidity;
    private String pressure;
    private String visibility;
    private String pubDate;
    private String georssPoint;

    private String dayOfWeek;
    private String formattedDate;

    public CurrentWeather() {
    }

    public CurrentWeather(String title, String temperature, String windDirection, String windSpeed, String humidity, String pressure, String visibility, String pubDate, String georssPoint, String dayOfWeek, String formattedDate ) {
        this.title = title;
        this.temperature = temperature;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.pressure = pressure;
        this.visibility = visibility;
        this.pubDate = pubDate;
        this.georssPoint = georssPoint;
        this.dayOfWeek = dayOfWeek;
        this.formattedDate = formattedDate;
    }

    // Getters and setters


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getGeorssPoint() {
        return georssPoint;
    }

    public void setGeorssPoint(String georssPoint) {
        this.georssPoint = georssPoint;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }
}