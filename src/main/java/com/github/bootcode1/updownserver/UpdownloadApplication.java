package com.github.bootcode1.updownserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import com.github.bootcode1.updownserver.config.SiteProperties;
import com.github.bootcode1.updownserver.config.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({SiteProperties.class, StorageProperties.class})
public class UpdownloadApplication  extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(UpdownloadApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(UpdownloadApplication.class);
	}
	
	//hidden method를 안 쓸것이므로 제거
	//필터를 쓰면 connector에서 바로 direct로 데이터를 받아 들일 수 없음
	@Bean
	public FilterRegistrationBean registration(HiddenHttpMethodFilter filter) {
	    FilterRegistrationBean registration = new FilterRegistrationBean(filter);
	    registration.setEnabled(false);
	    return registration;
	}
	
//	@Bean
//	public RequestMappingInfoHandlerMapping  RequestMappingHandlerMapping(RequestMappingInfoHandlerMapping mappingHandlder)
//	{
//		mappingHandlder.setRemoveSemicolonContent(false);
//		return mappingHandlder;
//	}
	
	
//	@Bean
//	public FilterRegistrationBean addRegistrationBean(CharacterEncodingFilter charFilter)
//	{
//		FilterRegistrationBean registrationBean = new FilterRegistrationBean(new UploadHandleFilter());
////		registrationBean.addUrlPatterns("/upload"); // 서블릿 등록 빈 처럼 패턴을 지정해 줄 수 있다.
//		registrationBean.setOrder(Integer.MIN_VALUE);
//		Collection<ServletRegistrationBean> beans = registrationBean.getServletRegistrationBeans();
//		for (ServletRegistrationBean servletRegistrationBean : beans)
//		{
//			System.out.println(servletRegistrationBean);
//		}
//		return registrationBean;
//	}
	
	/**
	 * 스프링 boot의 multipartResolver를 교체함
	 *
	 * @param servletContext the servlet context
	 * @return the multipart resolver
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
//	@Bean(name="multipartResolver")
//	public MultipartResolver getMultipartResolver(ServletContext servletContext, MultipartConfigElement multipartConfig, StorageProperties config) throws IOException {
//		DirectMultipartUploadResolver.MULTIPART_CONFIG = multipartConfig;
//		DirectMultipartUploadResolver.CONFIG = config;
//		DirectMultipartUploadResolver resolver = new DirectMultipartUploadResolver(servletContext);
//	    resolver.setResolveLazily(true);
//	    return resolver;
//	}
}
