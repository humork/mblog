/*
 * Copyright 2018 qyh.me
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
package me.qyh.blog.core.vo;

import java.time.LocalDateTime;

import me.qyh.blog.core.entity.Article;

/**
 * @since 5.10
 *
 */
public class RecentlyViewdArticle extends Article {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String ip;
	private final LocalDateTime time;

	public RecentlyViewdArticle(Article source, String ip, LocalDateTime time) {
		super(source);
		this.ip = ip;
		this.time = time;

		setContent(null);
		setSummary(null);
	}

	public String getIp() {
		return ip;
	}

	public LocalDateTime getTime() {
		return time;
	}

}
