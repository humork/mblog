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
package me.qyh.blog.core.config;

import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import me.qyh.blog.core.entity.Article;
import me.qyh.blog.core.entity.Space;
import me.qyh.blog.core.entity.Tag;
import me.qyh.blog.core.util.Times;
import me.qyh.blog.core.vo.ArticleQueryParam;
import me.qyh.blog.core.vo.ArticleQueryParam.Sort;
import me.qyh.blog.template.PathTemplate;

/**
 * 链接辅助类，用来获取一些对象的访问链接
 * 
 * @author Administrator
 *
 */
@Component
public class UrlHelper implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(UrlHelper.class);
	private static final String SPACE_IN_URL = "/space/";

	@Autowired
	protected UrlConfig urlConfig;

	private Urls urls;
	private String url;

	/**
	 * 获取空间地址辅助
	 * 
	 * @param alias
	 * @return
	 */
	public SpaceUrls getUrlsBySpace(String alias) {
		return new SpaceUrls(alias);
	}

	public Urls getUrls() {
		return urls;
	}

	public String getUrl() {
		return url;
	}

	/**
	 * 链接辅助类，用来获取配置的域名，根域名，链接，空间访问链接、文章链接等等
	 * 
	 * @author Administrator
	 *
	 */
	public class Urls {

		private Urls() {
			super();
		}

		/**
		 * 获取配置的域名
		 * 
		 * @return
		 */
		public String getDomain() {
			return urlConfig.getDomain();
		}

		/**
		 * 获取根域名
		 * <p>
		 * www.abc.com => abc.com <br>
		 * abc.com => abc.com
		 * </p>
		 * 
		 * @return
		 */
		public String getRootDomain() {
			return urlConfig.getRootDomain();
		}

		/**
		 * 获取系统主页地址(schema://domain:port/contextPath)
		 * 
		 * @return
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * 获取空间的访问链接
		 * 
		 * @param space
		 *            空间(别名不能为空)
		 * @return 访问链接
		 */
		public String getUrl(Space space) {
			if (space == null) {
				return url;
			}
			return url + "/space/" + space.getAlias();
		}

		/**
		 * 得到博客访问地址
		 * 
		 * @param article
		 * @return
		 */
		public String getUrl(Article article) {
			String idOrAlias = article.getAlias() == null ? String.valueOf(article.getId()) : article.getAlias();
			return getUrl(article.getSpace()) + "/article/" + idOrAlias;
		}

		/**
		 * 获取PathTemplate的访问路径
		 * <p>
		 * <b>不会替换PathVariable中的参数</b>
		 * </p>
		 * 
		 * @param pathTemplate
		 * @return 访问路径
		 */
		public String getUrl(PathTemplate pathTemplate) {
			String relativePath = pathTemplate.getRelativePath();
			if (relativePath.isEmpty()) {
				return url;
			}
			return url + '/' + relativePath;
		}
	}

	/**
	 * 当前请求的链接辅助类
	 * 
	 * @author Administrator
	 *
	 */
	public class SpaceUrls extends Urls {

		private final Env env;

		private SpaceUrls(String alias) {
			// 空间域名
			this.env = new Env();
			env.space = alias;
			if (env.isSpaceEnv()) {
				env.url = url + SPACE_IN_URL + env.space;
			} else {
				env.url = url;
			}
		}

		public String getUnlockUrl(String unlockId) {
			return env.url + "/unlock?unlockId=" + "" + Objects.toString(unlockId, "");
		}

		public String getSpace() {
			return env.space;
		}

		public String getCurrentUrl() {
			return env.url;
		}

		/**
		 * 获取指定路径的文章分页链接辅助
		 * 
		 * @param path
		 * @return
		 */
		public ArticlesUrlHelper getArticlesUrlHelper(String path) {
			return new ArticlesUrlHelper(env.url, path);
		}

		public ArticlesUrlHelper getArticlesUrlHelper() {
			return getArticlesUrlHelper("");
		}

		private class Env {
			private String space;
			private String url;

			boolean isSpaceEnv() {
				return space != null;
			}
		}
	}

	protected final class ArticlesUrlHelper {

		private final String url;
		private final String path;

		ArticlesUrlHelper(String url, String path) {
			super();
			this.url = url;
			this.path = path;
		}

		/**
		 * 得到标签的访问链接
		 * 
		 * @param tag
		 *            标签，标签名不能为空！
		 * @return 标签访问链接
		 */
		public String getArticlesUrl(Tag tag) {
			return getArticlesUrl(tag.getName());
		}

		/**
		 * 得到标签的访问地址
		 * 
		 * @param tag
		 *            标签名，会自动过滤html标签，eg:&lt;b&gt;spring&lt;/b&gt;会被过滤为spring
		 * @return 标签访问地址
		 */
		public String getArticlesUrl(String tag) {
			ArticleQueryParam param = new ArticleQueryParam();
			param.setTag(Jsoup.clean(tag, Whitelist.none()));
			return getArticlesUrl(param, 1);
		}

		/**
		 * 根据排序获取分页链接
		 * 
		 * @param param
		 *            当前分页参数
		 * @param sortStr
		 *            排序方式 ，见{@code ArticleQueryParam.Sort}
		 * @return 分页链接
		 */
		public String getArticlesUrl(ArticleQueryParam param, String sortStr) {
			ArticleQueryParam cloned = new ArticleQueryParam(param);
			if (sortStr != null) {
				Sort sort = null;
				try {
					sort = Sort.valueOf(sortStr);
				} catch (Exception e) {
					LOGGER.debug("无效的ArticleQueryParam.Sort:" + sortStr, e);
				}
				cloned.setSort(sort);
			} else {
				cloned.setSort(null);
			}
			return getArticlesUrl(cloned, 1);
		}

		/**
		 * 获取文章分页查询链接
		 * 
		 * @param param
		 *            分页参数
		 * @param page
		 *            当前页面
		 * @return 某个页面的分页链接
		 */
		public String getArticlesUrl(ArticleQueryParam param, int page) {
			StringBuilder sb = new StringBuilder(url);
			if (!path.isEmpty()) {
				if (!path.startsWith("/")) {
					sb.append('/');
				}
				sb.append(path);
			}
			sb.append("?currentPage=").append(page);
			Date begin = param.getBegin();
			Date end = param.getEnd();
			if (begin != null && end != null) {
				sb.append("&begin=").append(Times.format(Times.toLocalDateTime(begin), "yyyy-MM-dd HH:mm:ss"));
				sb.append("&end=").append(Times.format(Times.toLocalDateTime(end), "yyyy-MM-dd HH:mm:ss"));
			}
			if (param.getFrom() != null) {
				sb.append("&from=").append(param.getFrom().name());
			}
			if (param.getStatus() != null) {
				sb.append("&status=").append(param.getStatus().name());
			}
			if (param.getQuery() != null) {
				sb.append("&query=").append(param.getQuery());
			}
			if (param.getTag() != null) {
				sb.append("&tag=").append(param.getTag());
			}
			if (param.getSort() != null) {
				sb.append("&sort=").append(param.getSort().name());
			}
			if (param.hasQuery()) {
				sb.append("&highlight=").append(param.isHighlight() ? "true" : "false");
			}
			if (!CollectionUtils.isEmpty(param.getSpaces())) {
				sb.append("&spaces=").append(param.getSpaces().stream().collect(Collectors.joining(",")));
			}
			return sb.toString();
		}

		/**
		 * 获取某个时间段内文章分页查询链接
		 * 
		 * @param begin
		 *            开始时间
		 * @param end
		 *            结束时间
		 * @return 该时间段内的分页链接
		 */
		public String getArticlesUrl(Date begin, Date end) {
			ArticleQueryParam param = new ArticleQueryParam();
			param.setBegin(begin);
			param.setEnd(end);
			return getArticlesUrl(param, 1);
		}

		/**
		 * 获取某个时间段内文章分页查询链接
		 * 
		 * @param begin
		 *            开始时间
		 * @param end
		 *            结束时间
		 * @return 该时间段内的分页链接
		 */
		public String getArticlesUrl(String begin, String end) {
			ArticleQueryParam param = new ArticleQueryParam();
			param.setBegin(Times.parseAndGetDate(begin));
			if (param.getBegin() != null) {
				param.setEnd(Times.parseAndGetDate(end));
			}
			return getArticlesUrl(param, 1);
		}
	}

	private final class UriBuilder {
		private final String scheme;
		private final int port;
		private final String contextPath;
		private final String serverName;

		UriBuilder(UrlConfig urlConfig) {
			this.port = urlConfig.getPort();
			this.scheme = urlConfig.getSchema();
			this.serverName = urlConfig.getDomain();
			this.contextPath = urlConfig.getContextPath();
		}

		private boolean isDefaultPort() {
			if ("https".equalsIgnoreCase(scheme)) {
				return 443 == port;
			}
			return "http".equalsIgnoreCase(scheme) && 80 == port;
		}

		private String toUrl() {
			StringBuilder sb = new StringBuilder();
			sb.append(scheme).append("://");
			sb.append(serverName);
			if (!isDefaultPort()) {
				sb.append(":").append(port);
			}
			sb.append(contextPath);
			String buildUrl = sb.toString();
			if (buildUrl.endsWith("/")) {
				buildUrl = buildUrl.substring(0, buildUrl.length() - 1);
			}
			return buildUrl;
		}

	}

	public UrlConfig getUrlConfig() {
		return urlConfig;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		url = new UriBuilder(urlConfig).toUrl();
		this.urls = new Urls();
	}
}
