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
package me.qyh.blog.template.render.data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import me.qyh.blog.comment.entity.Comment;
import me.qyh.blog.comment.service.CommentService;
import me.qyh.blog.core.exception.LogicException;

public class LastCommentsDataTagProcessor extends DataTagProcessor<List<Comment>> {

	private static final Integer DEFAULT_LIMIT = 10;
	private static final int MAX_LIMIT = 50;

	@Autowired
	private CommentService commentService;

	public LastCommentsDataTagProcessor(String name, String dataName) {
		super(name, dataName);
	}

	@Override
	protected List<Comment> query(Attributes attributes) throws LogicException {
		String type = attributes.get("moduleType");
		if (type == null) {
			return new ArrayList<>();
		}
		return commentService.queryLastComments(type, getLimit(attributes), attributes.getBoolean("queryAdmin", false));
	}

	private int getLimit(Attributes attributes) {
		int limit = attributes.getInteger("limit", 0);
		if (limit <= 0) {
			limit = DEFAULT_LIMIT;
		}
		if (limit > MAX_LIMIT) {
			limit = MAX_LIMIT;
		}
		return limit;
	}

}
