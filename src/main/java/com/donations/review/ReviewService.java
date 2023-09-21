package com.donations.review;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.donations.admin.paging.PagingAndSortingHelper;
import com.donations.admin.product.ProductRepository;
import com.donations.common.entity.Review;
import com.donations.common.exception.ReviewNotFoundException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReviewService {
	public static final int REVIEWS_PER_PAGE = 5;
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private ProductRepository productRepository;

	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, REVIEWS_PER_PAGE, reviewRepository);
	}

	public Review getReview(Integer id) throws ReviewNotFoundException {
		try {
			return reviewRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new ReviewNotFoundException("Could not find any reviews with ID + " + id);
		}
	}

	public void save(Review reviewInForm) {
		Review reviewInDatabase = reviewRepository.findById(reviewInForm.getId()).get();
		reviewInDatabase.setHeadline(reviewInForm.getHeadline());
		reviewInDatabase.setComment(reviewInDatabase.getComment());
		reviewRepository.save(reviewInDatabase);
		productRepository.updateReviewCountAndAverageRating(reviewInDatabase.getProduct().getId());
	}

	public void delete(Integer id) throws ReviewNotFoundException {
		try {
			reviewRepository.deleteById(id);
		} catch (NoSuchElementException e) {
			throw new ReviewNotFoundException("Could not find any reviews with ID + " + id);
		}
	}
}
