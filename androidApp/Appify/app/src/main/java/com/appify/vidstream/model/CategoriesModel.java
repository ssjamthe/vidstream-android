package com.appify.vidstream.model;

public class CategoriesModel {
	
	private String catTitle;
	private String catImage;
	private String catID;

	public CategoriesModel() {}

	public CategoriesModel(String catTitle, String catImage, String catID) {
		super();
		this.catTitle = catTitle;
		this.catImage = catImage;
		this.catID = catID;
	}

	public String getCatTitle() {
		return catTitle;
	}

	public void setCatTitle(String catTitle) {
		this.catTitle = catTitle;
	}

	public String getCatImage() {
		return catImage;
	}

	public void setCatImage(String catImage) {
		this.catImage = catImage;
	}
	
	public String getCateID()
	{
		return catID;
	}
	
	public void setCatID(String catID)
	{
		this.catID = catID;
	}
}
