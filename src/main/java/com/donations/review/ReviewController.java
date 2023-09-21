package com.donations.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.donations.admin.paging.PagingAndSortingHelper;
import com.donations.admin.paging.PagingAndSortingParam;
import com.donations.common.entity.Review;
import com.donations.common.exception.ReviewNotFoundException;

@Controller
public class ReviewController {
	private String defaultRedirectURL = "redirect:/reviews/page/1?sortField=reviewTime&sortDir=desc";

	@Autowired
	private ReviewService reviewService;

	@GetMapping("/reviews")
	public String listFirstPage(Model model) {
		return defaultRedirectURL;
	}

	@GetMapping("/reviews/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "listReviews", moduleURL = "/reviews") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum) {
		reviewService.listByPage(pageNum, helper);
		return "reviews/review";
	}

	@GetMapping("/reviews/detail/{id}")
	public String viewReviewDetail(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Review review = reviewService.getReview(id);
			model.addAttribute("review", review);
			return "reviews/review_detail_modal";
		} catch (ReviewNotFoundException ex) {
			redirectAttributes.addAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}

	@GetMapping("/reviews/edit/{id}")
	public String editReview(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Review review = reviewService.getReview(id);
			model.addAttribute("review", review);
			model.addAttribute("pageTitle", String.format("Edit Review (ID: %d)", id));
			return "reviews/review_form";
		} catch (ReviewNotFoundException exception) {
			redirectAttributes.addAttribute("message", exception.getMessage());
			return defaultRedirectURL;
		}
	}

	@PostMapping("/reviews/save")
	public String saveReview(Review reviewInForm, RedirectAttributes redirectAttributes) {
		reviewService.save(reviewInForm);
		redirectAttributes.addFlashAttribute("message",
				"The review ID " + reviewInForm.getId() + " has bean updated successfully!");
		return defaultRedirectURL;
	}

	@GetMapping("/reviews/delete/{id}")
	public String deleteReview(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			reviewService.delete(id);
			redirectAttributes.addAttribute("Message", "The review ID " + id + " has been deleted successfully");
		} catch (ReviewNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return defaultRedirectURL;
	}
}
