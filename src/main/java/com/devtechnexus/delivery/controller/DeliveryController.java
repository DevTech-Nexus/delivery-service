package com.devtechnexus.delivery.controller;

import com.devtechnexus.delivery.model.Delivery;
import com.devtechnexus.delivery.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deliveries")
@CrossOrigin
public class DeliveryController {

    @Autowired
    private DeliveryService service;

    @GetMapping("/")
    public List<Delivery> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Delivery getDeliveryByID(@PathVariable int id) {
        return service.getDeliveryByID(id);
    }

    @PostMapping("/")
    public Delivery createDelivery(@RequestBody Delivery delivery) {
        return service.createDelivery(delivery);
    }

    @PutMapping("/")
    public Delivery updateDelivery(@RequestBody Delivery delivery) {
        return service.updateDelivery(delivery);
    }

    @DeleteMapping("/{id}")
    public void deleteDelivery(@PathVariable int id) {
        service.deleteDelivery(id);
    }

    /**
     *
     * @param destination
     * @return distance in km to the destination from the store
     */
    @GetMapping("/route")
    public double getDistance(@RequestParam(name="destination") String destination) {
        return service.getDistanceFromAPI(destination);
    }
}