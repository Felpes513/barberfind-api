package com.barberfind.api.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "barbershops")
public class Barbershop {

    @Id
    @Column(length = 40)
    private String id;

    @Column(name = "owner_user_id", length = 40, nullable = false)
    private String ownerUserId;

    private String name;
    private String cnpj;

    @Column(columnDefinition = "text")
    private String description;

    private String phone;
    private String email;
    private String address;
    private String neighborhood;
    private String city;
    private String state;

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "is_headquarter", nullable = false)
    private boolean isHeadquarter = true;

    @Column(name = "parent_barbershop_id", length = 40)
    private String parentBarbershopId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ===== getters/setters =====

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(String ownerUserId) { this.ownerUserId = ownerUserId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public LocalTime getOpeningTime() { return openingTime; }
    public void setOpeningTime(LocalTime openingTime) { this.openingTime = openingTime; }

    public LocalTime getClosingTime() { return closingTime; }
    public void setClosingTime(LocalTime closingTime) { this.closingTime = closingTime; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public boolean isHeadquarter() { return isHeadquarter; }
    public void setHeadquarter(boolean headquarter) { isHeadquarter = headquarter; }

    public String getParentBarbershopId() { return parentBarbershopId; }
    public void setParentBarbershopId(String parentBarbershopId) { this.parentBarbershopId = parentBarbershopId; }
}