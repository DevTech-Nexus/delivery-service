package com.devtechnexus.delivery.service;

import com.devtechnexus.delivery.dto.ParcelDTO;
import com.devtechnexus.delivery.model.Delivery;
import com.devtechnexus.delivery.model.GeoPoint;
import com.devtechnexus.delivery.model.Item;
import com.devtechnexus.delivery.repository.DeliveryRepository;
import com.devtechnexus.delivery.repository.ItemRepository;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Optional;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Value("${graphhopper.key}")
    private String graphhopperKey;

    @Value("${store.location}")
    private String storeLocation;


    public List<Delivery> getAll() {
        return deliveryRepository.getAll();
    }

    public Delivery getDeliveryByID(int id) {
        Optional<Delivery> delivery = deliveryRepository.findById(id);
        return delivery.orElse(null);
    }

    public Delivery createDelivery(ParcelDTO parcel) {
        Delivery delivery = new Delivery();
        delivery.setAddress(parcel.getAddress());
        delivery.setDatetime(parcel.getDatetime());
        delivery.setStatus(parcel.getStatus());
        delivery.setUserId(parcel.getUserId());
        delivery.setDatetime(parcel.getDatetime());

        deliveryRepository.save(delivery);

        for (Item item : parcel.getContents()) {
            item.setDelivery(delivery);

            itemRepository.save(item);
        }

        return delivery;
    }

    public Delivery updateDelivery(Delivery delivery) {

        return deliveryRepository.save(delivery);
    }

    public void deleteDelivery(int id) {
        Delivery delivery = deliveryRepository.findById(id).orElse(null);
        if (delivery != null) {
            List<Item> items = delivery.getItems();

            // Delete each item from the database
            itemRepository.deleteAll(items);

            // Clear the items list in the delivery entity
            delivery.getItems().clear();

            // Update the delivery to remove the items association
            deliveryRepository.save(delivery);
        }
        deliveryRepository.deleteById(id);
    }

    /**
     * uses graphhopper's routing API.
     *
     * @param address address of the destination
     * @return distance (in km) to the destination from the store location
     */
    public double getDistanceFromAPI(String address) {

        try {
            //first get locations latitude and longitude
            GeoPoint source = getLocation(storeLocation);
            GeoPoint destination = getLocation(address);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://graphhopper.com/api/1/route?point=" + source.getLat() + "," + source.getLon() + "&point=" + destination.getLat() + "," + destination.getLon() + "&profile=car&locale=de&calc_points=false&key=" + graphhopperKey)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonResponse);
                JSONArray paths = jsonObject.getJSONArray("paths");
                if (paths.length() > 0) {
                    JSONObject path = paths.getJSONObject(0);
                    return path.getDouble("distance") / 1000;
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    private GeoPoint getLocation(String address) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://graphhopper.com/api/1/geocode?q=" + address + "&locale=de&key=" + graphhopperKey)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonResponse);
                JSONArray hitsArray = jsonObject.getJSONArray("hits");
                if (hitsArray.length() > 0) {
                    JSONObject hit = hitsArray.getJSONObject(0);
                    JSONObject point = hit.getJSONObject("point");
                    double lat = point.getDouble("lat");
                    double lon = point.getDouble("lng");
                    return new GeoPoint(lat, lon);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}

