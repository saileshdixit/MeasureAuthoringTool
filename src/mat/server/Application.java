package mat.server;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Application{
	
	private static final String algorithm = "PBEWithMD5AndDES";
	
	private static final String passwordKey = "mytestQA";
	
	@Bean
	public StandardPBEStringEncryptor getStandardEncryptor() {
		StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
		standardPBEStringEncryptor.setAlgorithm(algorithm);
		standardPBEStringEncryptor.setPassword(passwordKey);
		return standardPBEStringEncryptor;
	}
	
}
