package com.example.setweb;

import com.example.setweb.dao.Bank;
import com.example.setweb.dao.Product;
import com.example.setweb.mapper.BankMapper;
import com.example.setweb.mapper.SellerMapper;
import com.example.setweb.utils.DESUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;
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

//	@Test
//	void sqlTest() throws IOException {
//		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
//				.build(Resources.getResourceAsStream("mybatis-config.xml"));
//
//		try (SqlSession session = sqlSessionFactory.openSession()) {
//			BankMapper bankMapper = session.getMapper(BankMapper.class);
//			System.out.println(bankMapper.getAllUsers());
//
//			SellerMapper sellerMapper = session.getMapper(SellerMapper.class);
//			System.out.println(sellerMapper.getAllProducts());
//		}
//	}
//	@Autowired
//	private SqlSessionFactory sqlSessionFactory;
//
//	@Test
//	public void sqlTest() {
//		try (SqlSession session = sqlSessionFactory.openSession()) {
//			BankMapper bankMapper = session.getMapper(BankMapper.class);
//			List<Bank> users = bankMapper.getAllUsers();
//			System.out.println("Bank Users: " + users);
//
//			SellerMapper sellerMapper = session.getMapper(SellerMapper.class);
//			List<Product> products = sellerMapper.getAllProducts();
//			System.out.println("Seller Products: " + products);
//		}
//	}
}
