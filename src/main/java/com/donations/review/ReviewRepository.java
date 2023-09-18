package com.donations.review;

import org.springframework.data.jpa.repository.JpaRepository;

import com.donations.common.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

}
