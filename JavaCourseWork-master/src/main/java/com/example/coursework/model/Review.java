package com.example.coursework.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int rating;
    private String reviewText;
    private LocalDateTime reviewDate;
    private boolean isVerified;
    @ManyToOne
    private BasicUser reviewer;
    @ManyToOne
    private Restaurant restaurant;
    @OneToOne
    private FoodOrder order;
    @Transient
    private List<String> photos;


    public Review(int id, int rating, String reviewText, BasicUser reviewer, Restaurant restaurant, FoodOrder order) {
        this.id = id;
        this.rating = rating;
        this.reviewText = reviewText;
        this.reviewer = reviewer;
        this.restaurant = restaurant;
        this.order = order;
        this.photos = new ArrayList<>();
        this.reviewDate = LocalDateTime.now();
        this.isVerified = false;
    }


    public boolean isValidRating() {
        return rating >= 1 && rating <= 5;
    }

    public String getRatingStars() {
        int maxRating = 5;
        return "★".repeat(rating) + "☆".repeat(maxRating - rating);
    }

    public String addReview() {
        return reviewer.getName() + " - " + getRatingStars();
    }

    public void addPhoto(String photoUrl) {
        if (this.photos == null) {
            this.photos = new ArrayList<>();
        }
        this.photos.add(photoUrl);
    }

    public void removePhoto(String photoUrl) {
        if (this.photos != null) {
            this.photos.remove(photoUrl);
        }
    }

    public void verify() {
        this.isVerified = true;
    }


}
