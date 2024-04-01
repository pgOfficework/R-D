package log.smtp;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.net.SmtpManager;

public class SMTP {

	public static void main(String[] args) {

		Logger log=LogManager.getLogger(SMTP.class);
		
		
		LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		File file = new File("log4j2.xml");
		context.setConfigLocation(file.toURI());
		
		log.fatal("fatal");
		log.error("error");
		log.warn("warn");
		log.info("info");
		log.debug("debug");
		log.trace("trace");

	}

}
