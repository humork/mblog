/*
 * Copyright 2016 qyh.me
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.qyh.blog.service;

import me.qyh.blog.entity.Tag;
import me.qyh.blog.exception.LogicException;
import me.qyh.blog.pageparam.PageResult;
import me.qyh.blog.pageparam.TagQueryParam;

/**
 * 
 * @author Administrator
 *
 */
public interface TagService {

	/**
	 * 分页查询标签
	 * 
	 * 
	 * @param param
	 *            查询参数
	 * @return 标签该分页对象
	 */
	PageResult<Tag> queryTag(TagQueryParam param);

	/**
	 * 更新标签
	 * 
	 * @param tag
	 *            待更新的标签
	 * @param merge
	 *            是否合并已经存在的标签
	 * @throws LogicException
	 *             更新过程中发生逻辑异常
	 */
	void updateTag(Tag tag, boolean merge) throws LogicException;

	/**
	 * 删除标签
	 * 
	 * @param id
	 *            标签id
	 * @throws LogicException
	 *             删除过程中发生逻辑异常
	 */
	void deleteTag(Integer id) throws LogicException;

}
