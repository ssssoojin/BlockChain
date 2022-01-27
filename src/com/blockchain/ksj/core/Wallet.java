package com.blockchain.ksj.core;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;

public class Wallet {
	public PrivateKey privateKey;
	public PublicKey publicKey;

	
	/*
	 * 블록체인에서 시그니쳐(서명)는 아주 중요한 두가지 인증을 하게 된다.
	 * 
	 * 1. 지갑 주인만이 지갑에서 돈을 사용할 수 있게 한다.
	 * 2. 지갑 주인이 아닌 다른 사람이 주인이 발행한 transaction에 대해서
	 * 	  데이터 수정 하는 것을 막아준다.
	 * 
	 * Private(개인)키는 데이터에 사인을 추가하는 작업이고, Public(공용)키는 데이터를 검증 하는데 사용을 한다.
	 */
	
	public void generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA",	"BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

			// Key generator 초기화 및 키생성하기
			// Initialize the key generator and generate a KeyPair
			keyGen.initialize(ecSpec, random); // 256 bytes provides an acceptable security level
			
			KeyPair keyPair = keyGen.generateKeyPair();
			
			// 공개키와 개인키 저장
			// Set the public and private keys from the keyPair
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
