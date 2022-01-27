package com.blockchain.ksj.main;

import java.util.ArrayList;

import com.blockchain.ksj.core.ExBlock;
import com.google.gson.GsonBuilder;

public class ExOpenChain {

	public static ArrayList<ExBlock> blockchain = new ArrayList<ExBlock>();
	public static int difficulty = 3;
	
	public static void main(String[] arg){

//		Block genesisBlock = new Block("첫번째 블록", "0");
//		System.out.println("Block-1 : "+genesisBlock.hash);
//		
//		Block secondBlock = new Block("두번째 블록", "0");
//		System.out.println("Block-2 : "+secondBlock.hash);
//		
//		Block thirdBlock = new Block("세번째 블록", "0");
//		System.out.println("Block-3 : "+thirdBlock.hash);
		
		blockchain.add(new ExBlock("첫번째 블록", "0"));
		System.out.print("블록-1 채굴시도중....");
		blockchain.get(0).mineBlock(difficulty);
		
		blockchain.add(new ExBlock("두번째 블록",blockchain.get(blockchain.size()-1).hash));
		System.out.print("블록-2 채굴시도중....");
		blockchain.get(1).mineBlock(difficulty);
		
		blockchain.add(new ExBlock("세번째 블록",blockchain.get(blockchain.size()-1).hash));
		System.out.print("블록-3 채굴시도중....");
		blockchain.get(2).mineBlock(difficulty);
		
		System.out.println("유효성 체크 : "+ isChainValid());
		
		//블록들을 arraylist에 넣고 Json형태로 보기위해 gson임포트
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println("================블록체인 정보=================");
		System.out.println(blockchainJson);
	}
	
	public static Boolean isChainValid() {
		ExBlock currentBlock; 
		ExBlock previousBlock;

		//전체 블럭을 체크합니다.
		for(int i=1; i < blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			
			//현재 블럭의 hash가 맞는지 체크합니다.
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				System.out.println("Current Hashes not equal");			
				return false;
			}
			
			//이전 블럭의 hash값과 동일한지 체크합니다.
			if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
		}
		return true;
	}	
}
