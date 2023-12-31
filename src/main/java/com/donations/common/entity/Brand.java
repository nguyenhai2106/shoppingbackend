package com.donations.common.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.ManyToAny;

import com.donations.common.constants.Constants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "brands")
public class Brand extends IdBaseEntity {
	@Column(nullable = false, length = 64, unique = true)
	private String name;
	@Column(nullable = false, length = 128)
	private String logo;

	@ManyToAny
	@JoinTable(name = "brands_categories", joinColumns = @JoinColumn(name = "brand_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
	private Set<Category> categories = new HashSet<>();

	public Brand(String name, String logo) {
		this.name = name;
		this.logo = logo;
	}

	public Brand() {

	}

	public Brand(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

	@Override
	public String toString() {
		return "Brand [id = " + id + ", name = " + name + ", categories = " + categories + "]";
	}

	@Transient
	public String getLogoPath() {
		if (id == null || logo == null) {
			return "/images/default-image.png";
		} else {
			return Constants.GCS_BASE_URI + "/brand-logos/" + this.id + "/" + this.logo;
		}
	}

}
