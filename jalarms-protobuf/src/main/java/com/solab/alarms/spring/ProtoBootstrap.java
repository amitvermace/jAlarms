package com.solab.alarms.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/** This is just a bootstrap class for an app which accepts protobuf connections,
 * configured via Spring. */
public class ProtoBootstrap {

	public static void main(String[] args) {
		String cname = args.length > 0 ? args[0] : "jalarms-protobuf.xml";
		ClassPathXmlApplicationContext ctxt = new ClassPathXmlApplicationContext(cname);
		ctxt.registerShutdownHook();
		((Runnable)ctxt.getBean("main")).run();
	}

}
