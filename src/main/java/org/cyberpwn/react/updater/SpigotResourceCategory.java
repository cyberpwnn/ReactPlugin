package org.cyberpwn.react.updater;

import java.util.List;

public class SpigotResourceCategory implements ResourceCategory {
	private int id = 0;
	private String name = "";
	private int resourceCount = 0;

	public int getCategoryId() {
		return id;
	}

	public void setCategoryId(int id) {
		this.id = id;
	}

	public String getCategoryName() {
		return name;
	}

	public void setCategoryName(String name) {
		this.name = name;
	}

	public List<Resource> getResources() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getResourceCount() {
		return resourceCount;
	}

	public void setResourceCount(int count) {
		this.resourceCount = count;
	}

}
