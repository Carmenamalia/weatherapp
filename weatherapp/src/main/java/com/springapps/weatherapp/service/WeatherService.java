package com.springapps.weatherapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.springapps.weatherapp.dto.CurrentWeatherDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    private static final String BASE_URL = "https://api.tomorrow.io/v4/weather";


    @Value("${weather.api}")
    private String apikey;

    public WeatherService(RestTemplate restTemplate, ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = mapper;
    }

    public CurrentWeatherDTO getCurrentWeather(double lat, double lon) throws JsonProcessingException {
        String url = UriComponentsBuilder
                .fromUriString(BASE_URL + "/realtime")
                .queryParam("location", lat + "," + lon)
                .queryParam("apikey", apikey)
                .toUriString();
        String response = restTemplate.getForObject(url, String.class);
        JsonNode root = objectMapper.readTree(response);
        return mapFromJsonToCurrentWeatherDTO(root);
    }

    public CurrentWeatherDTO mapFromJsonToCurrentWeatherDTO(JsonNode root) {
        Double humidity = root.path("data").path("values").path("humidity").asDouble();
        Double temperature = root.path("data").path("values").path("temperature").asDouble();
        Double feelsLikeTemperature = root.path("data").path("values").path("temperatureApparent").asDouble();
        String dateString = root.path("data").path("time").asText();
        LocalDateTime dateTime = LocalDateTime.parse(dateString);
        return new CurrentWeatherDTO(temperature, feelsLikeTemperature, humidity, dateTime);
    }

    public List<CurrentWeatherDTO> getForecastWeather(double lat, double lon, String timesteps) throws JsonProcessingException {
        String url = UriComponentsBuilder
                .fromUriString(BASE_URL + "/forecast")
                .queryParam("location", lat + "," + lon)
                .queryParam("timesteps", timesteps)
                .queryParam("apikey", apikey)
                .queryParam("units", "metric")
                .toUriString();
        String response = restTemplate.getForObject(url, String.class);
        JsonNode root = objectMapper.readTree(response);
        return mapFromJsonToForecastWeather(root);
    }
    public List<CurrentWeatherDTO> mapFromJsonToForecastWeather(JsonNode root) {
        ArrayNode dailyForecast = (ArrayNode) root.path("timelines").path("daily");
        List<CurrentWeatherDTO> result = new ArrayList<>();
        for (JsonNode jsonNode : dailyForecast) {
            result.add(mapFromForecastNodeToCurrentWeatherDTO(jsonNode));
        }
        return result;
    }

    public CurrentWeatherDTO mapFromForecastNodeToCurrentWeatherDTO(JsonNode jsonNode) {
        Double humidity = jsonNode.path("value").path("humidityAvg").asDouble();
        Double temperature = jsonNode.path("values").path("temperatureAvg").asDouble();
        Double feelsLikeTemperature = jsonNode.path("values").path("temperatureApparentAvg").asDouble();
        String dateString = jsonNode.path("time").asText();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        LocalDateTime dateTime = LocalDateTime.parse(dateString,dtf);
        return new CurrentWeatherDTO(temperature, humidity, feelsLikeTemperature, dateTime);
    }
}
