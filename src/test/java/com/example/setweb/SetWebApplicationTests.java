package com.example.setweb;

import com.example.setweb.utils.DESUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;

@SpringBootTest
class SetWebApplicationTests {

	@Test
	void DESTest() {
		try {
			// 1. 生成 DES 密钥
			SecretKey secretKey = DESUtil.generateKey();

			// 2. 要加密的字符串
			String originalText = "Hello, DES!";
			System.out.println("raw plain: " + originalText);

			// 3. 加密
			String encryptedText = DESUtil.encrypt(originalText, secretKey);
			System.out.println("encryption: " + encryptedText);

			// 4. 解密
			String decryptedText = DESUtil.decrypt(encryptedText, secretKey);
			System.out.println("decryption: " + decryptedText);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
