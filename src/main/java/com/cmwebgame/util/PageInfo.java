package com.cmwebgame.util;

public class PageInfo {

	public final static String PAGE = "pagenum";

	private int totalRecords = 0;
	private int previousPage = 1;
	private int nextPage = 1;
	private int currentPage = 1;
	private int totalPages = 0;
	private int pageSize = 0;
	private String formAction = null;

	public PageInfo(int totalRecords) {
		this(totalRecords, 10);
	}

	public PageInfo(int totalRecords, int pageSize) {
		this.totalRecords = totalRecords;
		this.pageSize = pageSize;
		calculateTotalPage();
		setCurrentPage(this.currentPage);
	}

	/**
	 * @return int
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * @return int
	 */
	public int getNextPage() {
		return nextPage;
	}

	/**
	 * @return int
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @return int
	 */
	public int getPreviousPage() {
		return previousPage;
	}

	/**
	 * @return int
	 */
	public int getTotalPages() {
		return totalPages;
	}

	/**
	 * @return int
	 */
	public int getTotalRecords() {
		return totalRecords;
	}

	/**
	 * Sets the currentPage.
	 * @param currentPage The currentPage to set
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
		if(currentPage <= 1) {
			previousPage = 1;
		} else {
			previousPage = currentPage - 1;
		}
		if(currentPage >= totalPages) {
			nextPage = totalPages;
		} else {
			nextPage = currentPage + 1;
		}

	}

	/**
	 * Sets the pageSize.
	 * @param pageSize The pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		calculateTotalPage();
		setCurrentPage(this.currentPage);
	}

	private void calculateTotalPage() {
		totalPages = totalRecords / pageSize;
		if(totalRecords % pageSize != 0) {
			totalPages++;
		}
	}
	/**
	 * @return String
	 */
	public String getFormAction() {
		return formAction;
	}

	/**
	 * Sets the formAction.
	 * @param formAction The formAction to set
	 */
	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

}
