package edu.zjut.utils;


import edu.zjut.proxy.ServiceProxy;

/**
 * @author john
 * @date 2017年8月21日
 * @version v1.0
 * 
 */

public class BeanFactory {
	
	public static <T> T getBean(Class<T> clazz){
		T obj = null;
		try {
			obj = clazz.newInstance();
			if (clazz.getName().endsWith("ServiceImp")){
			    obj = ServiceProxy.getProxy(obj);
            }
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return obj;
	}

}


