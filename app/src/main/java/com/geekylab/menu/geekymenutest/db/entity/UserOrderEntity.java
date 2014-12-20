package com.geekylab.menu.geekymenutest.db.entity;

import java.io.Serializable;

/**
 * Created by johna on 20/12/14.
 * Kodokux System
 */
public class UserOrderEntity implements Serializable {
    private String id;
    private String store_id;
    private String table;
    private String order_token;
    private String order_number;
    private String customer;
    private double total_price;
    private int status;
    private int created_at;

    public String getId() {
        return id;
    }

    public UserOrderEntity setId(String id) {
        this.id = id;
        return this;
    }

    public String getStoreId() {
        return store_id;
    }

    public UserOrderEntity setStoreId(String store_id) {
        this.store_id = store_id;
        return this;
    }

    public String getTable() {
        return table;
    }

    public UserOrderEntity setTable(String table) {
        this.table = table;
        return this;
    }

    public String getOrderToken() {
        return order_token;
    }

    public UserOrderEntity setOrderToken(String order_token) {
        this.order_token = order_token;
        return this;
    }

    public String getOrderNumber() {
        return order_number;
    }

    public UserOrderEntity setOrderNumber(String order_number) {
        this.order_number = order_number;
        return this;
    }

    public String getCustomer() {
        return customer;
    }

    public UserOrderEntity setCustomer(String customer) {
        this.customer = customer;
        return this;
    }

    public double getTotalPrice() {
        return total_price;
    }

    public UserOrderEntity setTotalPrice(double total_price) {
        this.total_price = total_price;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public UserOrderEntity setStatus(int status) {
        this.status = status;
        return this;
    }

    public int getCreatedAt() {
        return created_at;
    }

    public UserOrderEntity setCreatedAt(int created_at) {
        this.created_at = created_at;
        return this;
    }
}
