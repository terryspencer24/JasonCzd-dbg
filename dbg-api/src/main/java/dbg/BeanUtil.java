package dbg;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * Simple utility class to get a bean from non-Spring managed class.
 */
@Service
public class BeanUtil implements ApplicationContextAware {

	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	public static <T> T getBean(Class<T> beanClass) {
		try {
			return context == null ? beanClass.newInstance() : context.getBean(beanClass);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}