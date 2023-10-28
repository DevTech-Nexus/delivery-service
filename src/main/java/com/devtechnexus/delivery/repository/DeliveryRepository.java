package com.devtechnexus.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.devtechnexus.delivery.model.Delivery;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {


    @Query("SELECT d FROM Delivery d")
    public List<Delivery> getAll();
}
