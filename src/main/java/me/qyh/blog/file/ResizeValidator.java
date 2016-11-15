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
package me.qyh.blog.file;

/**
 * resize 校验器
 * 
 * @author Administrator
 *
 */
public interface ResizeValidator {
	/**
	 * 验证是否是一个正确的缩略图尺寸
	 * 
	 * @param resize
	 * @return
	 */
	boolean valid(Resize resize);
}
