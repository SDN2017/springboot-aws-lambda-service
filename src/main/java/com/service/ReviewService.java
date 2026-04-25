package com.service;

import com.model.Review;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final List<Review> reviews = new ArrayList<>();
    public void addReview(Review review) {
        reviews.add(review);
    }

    // Retrieve all reviews
    public List<Review> getAllReviews() {
        return reviews;
    }

    // Retrieve a review by id
    public Optional<Review> getReviewById(int id) {
        return reviews.stream()
                .filter(review -> review.getId() == id)
                .findFirst();
    }

    // Update review
    public boolean updateReview(int id, Review newReview) {
        return getReviewById(id).map(existingReview -> {
            reviews.remove(existingReview);
            reviews.add(newReview);
            return true;
        }).orElse(false);
    }

    // Delete a review by id
    public boolean deleteReview(int id) {
        return reviews
                .removeIf(review -> review.getId() == id);
    }
}