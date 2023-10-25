package com.devtechnexus.delivery.model;

import jakarta.persistence.*;
import lombok.Data;


import java.sql.Timestamp;

@Data
@Entity
@Table(name="delivery")

public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "product_id")
    private int productId;


    @Column(name = "quantity")
    private float quantity;

    @Column(name = "address")
    private String address;

    @Column(name = "datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp datetime;

    @Column(name = "status")
    private String status;


}
