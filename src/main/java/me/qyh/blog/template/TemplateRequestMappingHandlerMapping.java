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
package me.qyh.blog.template;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo.BuilderConfiguration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import me.qyh.blog.core.exception.SystemException;
import me.qyh.blog.core.util.FileUtils;
import me.qyh.blog.template.TemplateMapping.TemplateMatch;
import me.qyh.blog.web.Webs;
import me.qyh.blog.web.interceptor.AppInterceptor;
import me.qyh.blog.web.security.IPGetter;
import me.qyh.blog.web.view.TemplateView;

public class TemplateRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

	private static final Method method;

	private final List<TemplateInterceptor> templateInterceptors = new ArrayList<>();

	/**
	 * 用于预览的IP
	 * <p>
	 * 成功设置预览界面后设置这个值<br>
	 * 当用户注销后|删除预览页面时 将此值置为null<br>
	 * 非线程安全
	 * </p>
	 * 
	 * @since 5.10
	 * 
	 */
	public static String PREVIEW_IP = null;

	static {
		try {
			method = TemplateHandler.class.getMethod("handlerTemplate");
		} catch (Exception e) {
			throw new SystemException(e.getMessage(), e);
		}
	}

	@Autowired
	private TemplateMapping templateMapping;
	@Autowired(required = false)
	private IPGetter ipGetter;

	private RequestMappingInfo.BuilderConfiguration config;

	@Override
	protected HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
		String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);

		String ip = ipGetter.getIp(request);
		request.setAttribute(Webs.IP_ATTR_NAME, ip);

		if (Webs.errorRequest(request) || "GET".equals(request.getMethod())) {
			Optional<TemplateMatch> matchOptional;

			if (isPreview(request)) {
				matchOptional = templateMapping.getPreviewTemplateMapping()
						.getBestHighestPriorityTemplateMatch(lookupPath);
			} else {
				matchOptional = templateMapping.getBestHighestPriorityTemplateMatch(lookupPath);
			}

			if (matchOptional.isPresent()) {
				TemplateMatch match = matchOptional.get();
				setUriTemplateVariables(match, lookupPath, request);
				return new HandlerMethod(new TemplateHandler(match.getTemplateName()), method);
			}

		}
		return super.getHandlerInternal(request);
	}

	@Override
	protected HandlerMethod handleNoMatch(Set<RequestMappingInfo> infos, String lookupPath, HttpServletRequest request)
			throws ServletException {
		if ("GET".equals(request.getMethod())) {
			Optional<TemplateMatch> matchOptional;

			if (isPreview(request)) {
				matchOptional = templateMapping.getPreviewTemplateMapping()
						.getBestPathVariableTemplateMatch(lookupPath);
			} else {
				matchOptional = templateMapping.getBestPathVariableTemplateMatch(lookupPath);
			}

			if (matchOptional.isPresent()) {
				TemplateMatch match = matchOptional.get();
				setUriTemplateVariables(match, lookupPath, request);
				return new HandlerMethod(new TemplateHandler(match.getTemplateName()), method);
			}
		}

		return super.handleNoMatch(infos, lookupPath, request);
	}

	@Override
	protected HandlerExecutionChain getHandlerExecutionChain(Object handler, HttpServletRequest request) {
		HandlerExecutionChain chain = super.getHandlerExecutionChain(handler, request);
		if (!templateInterceptors.isEmpty()) {
			getTemplateHandler(handler).ifPresent(th -> {
				String templateName = th.templateName;
				for (TemplateInterceptor templateInterceptor : templateInterceptors) {
					if (templateInterceptor.match(templateName, request)) {
						chain.addInterceptor(templateInterceptor);
					}
				}
			});
		}
		return chain;
	}

	/**
	 * 注册一个新的Mapping
	 * 
	 * @param builder
	 *            RequestMappingInfo.Builder
	 * @param handler
	 * @param method
	 */
	public void registerMapping(RequestMappingInfo.Builder builder, Object handler, Method method) {
		super.registerMapping(builder.options(config).build(), handler, method);
	}

	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();

		try {
			Field configField = this.getClass().getSuperclass().getDeclaredField("config");
			configField.setAccessible(true);
			this.config = (BuilderConfiguration) configField.get(this);
		} catch (Exception e) {
			throw new SystemException(e.getMessage(), e);
		}

		templateInterceptors.addAll(BeanFactoryUtils
				.beansOfTypeIncludingAncestors(getApplicationContext(), TemplateInterceptor.class, true, false)
				.values());

		if (ipGetter == null) {
			ipGetter = new IPGetter();
		}
	}

	private final class TemplateHandler {
		private final String templateName;

		public TemplateHandler(String templateName) {
			super();
			this.templateName = templateName;
		}

		@SuppressWarnings("unused")
		public TemplateView handlerTemplate() {
			return new TemplateView(templateName);
		}
	}

	private Optional<TemplateHandler> getTemplateHandler(Object handler) {
		if (handler instanceof HandlerMethod) {
			Object bean = ((HandlerMethod) handler).getBean();
			return bean instanceof TemplateHandler ? Optional.of((TemplateHandler) bean) : Optional.empty();
		}
		return Optional.empty();
	}

	private boolean isPreview(HttpServletRequest request) {
		return PREVIEW_IP != null && PREVIEW_IP.equals(Webs.getIP(request));
	}

	private void setUriTemplateVariables(TemplateMatch match, String lookupPath, HttpServletRequest request) {
		String pattern = match.getPattern();
		Map<String, String> pathVariables = getUrlPathHelper().decodePathVariables(request,
				getPathMatcher().extractUriTemplateVariables(pattern, FileUtils.cleanPath(lookupPath)));
		request.setAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE, pathVariables);
	}

	@Override
	protected void extendInterceptors(List<Object> interceptors) {
		interceptors.add(getApplicationContext().getBean(AppInterceptor.class));
	}

}
