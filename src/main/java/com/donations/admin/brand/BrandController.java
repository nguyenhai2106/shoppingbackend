package com.donations.admin.brand;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.donations.admin.GoogleCloudStorageService;
import com.donations.admin.category.CategoryService;
import com.donations.admin.paging.PagingAndSortingHelper;
import com.donations.admin.paging.PagingAndSortingParam;
import com.donations.common.entity.Brand;
import com.donations.common.entity.Category;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class BrandController {
	private String defaultRedirectURL = "redirect:/brands/page/1?sortField=id&sortDir=asc";
	@Autowired
	private BrandService service;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private GoogleCloudStorageService googleService;

	@GetMapping("/brands")
	public String listAll() {
		return defaultRedirectURL;
	}

	@GetMapping("/brands/new")
	public String newBrand(Model model) {
		List<Category> categories = categoryService.listCategoriesUsedInForm();
		Brand brand = new Brand();
		model.addAttribute("brand", brand);
		model.addAttribute("pageTitle", "Create New Brand");
		model.addAttribute("categories", categories);

		return "brands/brand_form";
	}

	@PostMapping("/brands/save")
	public String saveBrand(Brand brand, @RequestParam("imageBrand") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogo(fileName);
			Brand savedBrand = service.save(brand);
			String uploadDir = "brand-logos/" + savedBrand.getId();
//			FileUploadUtil.removeDir(uploadDir);
//			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			GoogleCloudStorageService.removeFolder(uploadDir);
			GoogleCloudStorageService.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
			redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully!");
			return "redirect:/brands";
		} else {
			service.save(brand);
			redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully!");
			return defaultRedirectURL;
		}
	}

	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Brand brand = service.get(id);
			service.delete(id);
			String brandDir = "brand-logos/" + id + "/" + brand.getLogo();
			GoogleCloudStorageService.removeFolder(brandDir);
//			FileUploadUtil.removeDir(brandDir);
			redirectAttributes.addFlashAttribute("message", "The brand ID " + id + " has been deleted successfully");
		} catch (BrandNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return defaultRedirectURL;
	}

	@GetMapping("/brands/edit/{id}")
	public String editBrand(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Brand brand = service.get(id);
			List<Category> categories = categoryService.listCategoriesUsedInForm();
			model.addAttribute("brand", brand);
			model.addAttribute("categories", categories);
			model.addAttribute("pageTitle", "Edit Brand (ID " + id + ")");
			return "brands/brand_form";
		} catch (BrandNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return defaultRedirectURL;
		}
	}

	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "listBrands", moduleURL = "/brands") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum) {

		service.listByPage(pageNum, helper);

		return "brands/brands";
	}

	@GetMapping("/brands/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<Brand> listBrands = service.listAll();
		BrandCsvExporter exporter = new BrandCsvExporter();
		exporter.export(listBrands, response);
	}

}
