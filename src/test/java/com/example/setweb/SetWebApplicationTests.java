package com.example.setweb;

import com.example.setweb.dao.Bank;
import com.example.setweb.mapper.BankMapper;
import com.example.setweb.service.BankService;
import com.example.setweb.utils.DESUtil;
import com.example.setweb.utils.HashUtil;
import com.example.setweb.utils.RSASignature;
import jakarta.annotation.Resource;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

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
	@Resource
	private SqlSessionFactory sqlSessionFactory;

	@Test
	public void sqlTest() {
		try (SqlSession session = sqlSessionFactory.openSession()) {
			BankMapper bankMapper = session.getMapper(BankMapper.class);
			List<Bank> users = bankMapper.getAllUsers();
			System.out.println("Bank Users: " + users);
			BankService bankService = new BankService(bankMapper);
			List<Bank> bankUsers = bankService.getAllUsers();
			System.out.println(bankUsers);

		}catch (Exception e){
			System.out.println("error:" + e);
		}
	}

	@Test
	public void hashTest(){
		String hashText = HashUtil.sha1("hello world!");
		System.out.println(hashText);
	}

	@Test
	public void RSATest(){
		try {
			// 1. 生成 RSA 密钥对
			KeyPair keyPair = RSASignature.generateKeyPair();
			PrivateKey privateKey = keyPair.getPrivate();
			PublicKey publicKey = keyPair.getPublic();

			// 2. 需要签名的消息
			String message = "Hello, this is a secure message.";

			// 3. 生成签名
			String signature = RSASignature.sign(message, privateKey);
			System.out.println("Digital Signature: " + signature);

			// 4. 验证签名
			boolean isVerified = RSASignature.verify(message, signature, publicKey);
			System.out.println("Signature Verification: " + isVerified);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
