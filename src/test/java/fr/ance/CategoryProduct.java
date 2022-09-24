package fr.ance;


import org.apache.commons.lang3.tuple.Pair;

public class CategoryProduct {

	String productname;
	String categoryname;

	public CategoryProduct(String productname, String categoryname) {
		this.productname = productname;
		this.categoryname = categoryname;
	}

	@Override
	public String toString() {
		return "CategoryProduct{" + "productname=" + productname + ", categoryname=" + categoryname + '}';
	}

	public static CategoryProduct from(Pair<Product, Category> pair) {
		return new CategoryProduct(pair.getLeft().getName(), pair.getRight().getName());
	}

}