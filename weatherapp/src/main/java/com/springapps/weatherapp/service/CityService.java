package com.springapps.weatherapp.service;

import com.springapps.weatherapp.model.City;
import com.springapps.weatherapp.model.User;
import com.springapps.weatherapp.repository.CityRepository;
import com.springapps.weatherapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CityService {
    private UserRepository userRepository;

    private CityRepository cityRepository;

    @Autowired
    public CityService(UserRepository userRepository, CityRepository cityRepository) {
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
    }

    public User addFavouriteCity(City city, Long userId) {
        User foundUser = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
        foundUser.getCities().add(city);
        city.getUsers().add(foundUser);
        return userRepository.save(foundUser);
    }

    public User deleteCity(Long cityId, Long userId) {
        User foundUser = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
        City foundCity = cityRepository.findById(cityId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "city not found"));
        foundUser.getCities().remove(foundCity);
        foundCity.getUsers().remove(foundUser);
        return userRepository.save(foundUser);
    }

    public List<City> getUserFavoriteCities(Long userId) {
        User foundUser = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
        return cityRepository.findAllByUserListContaining(foundUser);
    }
}
