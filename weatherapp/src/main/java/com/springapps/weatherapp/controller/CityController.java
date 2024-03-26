package com.springapps.weatherapp.controller;

import com.springapps.weatherapp.model.City;
import com.springapps.weatherapp.model.User;
import com.springapps.weatherapp.service.CityService;
import com.springapps.weatherapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/city")
public class CityController {
    private UserService userService;

    private CityService cityService;

    @Autowired
    public CityController(UserService userService, CityService cityService) {
        this.userService = userService;
        this.cityService = cityService;
    }

    @PostMapping("/add/{userId}")
    public ResponseEntity<User> addFavouriteCity(@RequestBody City city, @PathVariable Long userId) {
        return ResponseEntity.ok(cityService.addFavouriteCity(city, userId));
    }
    @DeleteMapping("/delete/{userId}/{cityId}")
    public User deleteCity(@PathVariable Long cityId, @PathVariable Long userId) {
        return cityService.deleteCity(cityId,userId);
    }

    @GetMapping("/{userId}")
    public List<City> getUserFavoriteCities(@PathVariable Long userId) {
        return cityService.getUserFavoriteCities(userId);
    }
}
