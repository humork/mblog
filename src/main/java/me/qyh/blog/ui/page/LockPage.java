package me.qyh.blog.ui.page;

import me.qyh.blog.entity.Space;

public class LockPage extends Page {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String lockType;

	public String getLockType() {
		return lockType;
	}

	public void setLockType(String lockType) {
		this.lockType = lockType;
	}

	@Override
	public String getTemplateName() {
		Space space = getSpace();
		return PREFIX + "LockPage:" + (space == null ? lockType : space.getAlias() + "-" + lockType);
	}

	@Override
	public final PageType getType() {
		return PageType.LOCK;
	}

	public LockPage() {
	}

	public LockPage(Space space) {
		super(space);
	}

	public LockPage(Space space, String lockType) {
		super(space);
		this.lockType = lockType;
	}
	
	public Page toExportPage() {
		LockPage page = new LockPage();
		page.setTpl(getTpl());
		page.setType(PageType.LOCK);
		page.setLockType(lockType);
		return page;
	}

}