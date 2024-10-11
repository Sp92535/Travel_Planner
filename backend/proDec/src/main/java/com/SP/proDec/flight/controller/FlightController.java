package com.SP.proDec.flight.controller;

import com.SP.proDec.flight.config.AmadeusConnect;
import com.SP.proDec.flight.dto.LocationDto;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.Location;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/flights")
public class FlightController {

    @GetMapping("/locations")
    public ResponseEntity<List<LocationDto>> locations(@RequestParam(required=true) String keyword) throws ResponseException {
        Location[] locations = AmadeusConnect.INSTANCE.location(keyword);
        return new ResponseEntity<>( Arrays.stream(locations)
                .map(location -> new LocationDto(
                        location.getName(),
                        location.getIataCode(),
                        location.getGeoCode().getLatitude(),
                        location.getGeoCode().getLatitude()
                )).collect(Collectors.toList()), HttpStatus.OK);
    }

    
}
