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
package me.qyh.blog.pageparam;

import me.qyh.blog.entity.Space;

public class UserPageQueryParam extends PageQueryParam {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private Space space;

	public Space getSpace() {
		return space;
	}

	public void setSpace(Space space) {
		this.space = space;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
