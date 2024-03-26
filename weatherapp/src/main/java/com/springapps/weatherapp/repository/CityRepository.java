package com.springapps.weatherapp.repository;

import com.springapps.weatherapp.model.City;
import com.springapps.weatherapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City,Long> {
    List<City> findAllByUserListContaining(User foundUser);
}
