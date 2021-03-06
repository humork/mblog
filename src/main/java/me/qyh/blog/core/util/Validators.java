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
package me.qyh.blog.core.util;

import java.util.regex.Pattern;

/**
 * 校验辅助
 * 
 * @author Administrator
 *
 */
public final class Validators {
	private static final Pattern LETTER_NUM_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");
	private static final Pattern LETTER_PATTERN = Pattern.compile("^[A-Za-z]+$");
	private static final Pattern LETTER_NUM_CHINESE_PATTERN = Pattern.compile("^[A-Za-z0-9\u4E00-\u9FA5]+$");

	private static final char[] NUMS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	/**
	 * private
	 */
	private Validators() {
		super();
	}

	/**
	 * 判断字符串是否为null或空
	 * 
	 * @param str
	 *            字符串
	 * @param trim
	 *            是否trim
	 * @return 如果为null|空 返回true,否则返回false
	 */
	public static boolean isEmptyOrNull(String str, boolean trim) {
		return str == null || (trim ? str.trim().isEmpty() : str.isEmpty());
	}

	/**
	 * is alpha
	 * 
	 * <pre>
	 * Validators.isAlpha(null)   = false
	 * Validators.isAlpha("")     = false
	 * Validators.isAlpha("  ")   = false
	 * Validators.isAlpha("abc")  = true
	 * Validators.isAlpha("ab2c") = false
	 * Validators.isAlpha("ab-c") = false
	 * </pre>
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isAlpha(String str) {
		return !isEmptyOrNull(str, false) && str.chars().allMatch(i -> Character.isLetter(i));
	}

	/**
	 * 判断两个对象是否是相同的类型，如果两个对象任意一个为null，那么返回false
	 * 
	 * @param a
	 *            对象a
	 * @param b
	 *            对象b
	 * @return
	 */
	public static boolean baseEquals(Object a, Object b) {
		if (a == null) {
			return false;
		}
		if (b == null) {
			return false;
		}
		return a == b || a.getClass() == b.getClass();
	}

	/**
	 * 判断字符串是否由字母或者数字组成
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isLetterOrNum(String str) {
		return str != null && LETTER_NUM_PATTERN.matcher(str).find();
	}

	/**
	 * 判断字符串是否由字母组成
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isLetter(String str) {
		return str != null && LETTER_PATTERN.matcher(str).find();
	}

	/**
	 * 判断字符串是否只由字母，数字和中文组成
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isLetterOrNumOrChinese(String str) {
		return str != null && LETTER_NUM_CHINESE_PATTERN.matcher(str).find();
	}

	/**
	 * 判断字符是否时数字
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isNum(char ch) {
		for (char num : NUMS) {
			if (num == ch) {
				return true;
			}
		}
		return false;
	}
}
