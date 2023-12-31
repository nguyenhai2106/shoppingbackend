package com.donations.admin.brand;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.donations.admin.paging.PagingAndSortingHelper;
import com.donations.common.entity.Brand;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BrandService {
	public static final int BRAND_PER_PAGE = 8;
	@Autowired
	private BrandRepository repository;

	public List<Brand> listAll() {
		List<Brand> brands = repository.findAll();
		return brands;
	}

	public Brand save(Brand brand) {
		return repository.save(brand);
	}

	public void delete(Integer id) throws BrandNotFoundException {
		Long countByIt = repository.countById(id);
		if (countByIt == null || countByIt == 0) {
			throw new BrandNotFoundException("Could not find any brand with ID " + id);
		}
		repository.deleteById(id);
	}

	public Brand get(Integer id) throws BrandNotFoundException {
		try {
			return repository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new BrandNotFoundException("Could not find any brand with ID " + id);
		}
	}

	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		Brand brandByName = repository.findByName(name);

		if (isCreatingNew) {
			if (brandByName != null)
				return "Duplicated";
		} else {
			if (brandByName != null && brandByName.getId() != id) {
				return "Duplicated";
			}
		}

		return "OK";
	}

	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, BRAND_PER_PAGE, repository);
	}
}
