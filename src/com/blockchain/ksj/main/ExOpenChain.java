package com.blockchain.ksj.main;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

import com.blockchain.ksj.core.ExBlock;
import com.blockchain.ksj.core.ExTransaction;
import com.blockchain.ksj.core.ExTransactionInput;
import com.blockchain.ksj.core.ExTransactionOutput;
import com.blockchain.ksj.core.ExWallet;
import com.blockchain.ksj.util.ExStringUtil;

public class ExOpenChain {

	public static ArrayList<ExBlock> blockchain = new ArrayList<ExBlock>();

	//사용되지 않은 트랜잭션의 추가 컬랙션
	public static HashMap<String, ExTransactionOutput> UTXOs = new HashMap<String, ExTransactionOutput>(); //list of all unspent transactions.
	public static int difficulty = 3;
	public static float minimumTransaction = 0.1f;
	public static ExWallet walletA;
	public static ExWallet walletB;
	public static ExTransaction genesisTransaction;
	
	public static void main(String[] arg){

//		Block genesisBlock = new Block("첫번째 블록", "0");
//		System.out.println("Block-1 : "+genesisBlock.hash);
//		
//		Block secondBlock = new Block("두번째 블록", "0");
//		System.out.println("Block-2 : "+secondBlock.hash);
//		
//		Block thirdBlock = new Block("세번째 블록", "0");
//		System.out.println("Block-3 : "+thirdBlock.hash);
		
//		blockchain.add(new ExBlock("첫번째 블록", "0"));
//		System.out.print("블록-1 채굴시도중....");
//		blockchain.get(0).mineBlock(difficulty);
//		
//		blockchain.add(new ExBlock("두번째 블록",blockchain.get(blockchain.size()-1).hash));
//		System.out.print("블록-2 채굴시도중....");
//		blockchain.get(1).mineBlock(difficulty);
//		
//		blockchain.add(new ExBlock("세번째 블록",blockchain.get(blockchain.size()-1).hash));
//		System.out.print("블록-3 채굴시도중....");
//		blockchain.get(2).mineBlock(difficulty);
//		
//		System.out.println("유효성 체크 : "+ isChainValid());
		
//		//블록들을 arraylist에 넣고 Json형태로 보기위해 gson임포트
//		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
//		System.out.println("================블록체인 정보=================");
//		System.out.println(blockchainJson);
		
//		//Setup Bouncey castle as a Security Provider
//		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); 
//		
//		//Create the new wallets
//		walletA = new ExWallet();
//		walletB = new ExWallet();
//		
//		walletA.generateKeyPair();
//		walletB.generateKeyPair();
//		
//		//Test public and private keys
//		System.out.println("Private and public keys:");
//		System.out.println("A-Private keys:"+ExStringUtil.getStringFromKey(walletA.privateKey));
//		System.out.println("A-public keys:"+ExStringUtil.getStringFromKey(walletA.publicKey));
//		
//		//테스트를 위한 Transaction생성(WalletA -> walletB : 100)  
//		ExTransaction transaction = new ExTransaction(walletA.publicKey, walletB.publicKey, 100, null);
//		//생성된 Transaction에 서명합니다.
//		transaction.generateSignature(walletA.privateKey);
//		
//		//서명한 Transaction을 검증합니다.
//		System.out.println("Is this Transaction Verify? " + transaction.verifiySignature());
		//add our blocks to the blockchain ArrayList:
				Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncey castle as a Security Provider
				
				//Create wallets:
				walletA = new ExWallet();
				walletB = new ExWallet();		
				ExWallet coinbase = new ExWallet();
				
				walletA.generateKeyPair();
				walletB.generateKeyPair();
				coinbase.generateKeyPair();
				
				//create genesis transaction, which sends 100 NoobCoin to walletA: 
				genesisTransaction = new ExTransaction(coinbase.publicKey, walletA.publicKey, 100f, null);
				genesisTransaction.generateSignature(coinbase.privateKey);	 //manually sign the genesis transaction	
				genesisTransaction.transactionId = "0"; //manually set the transaction id
				genesisTransaction.outputs.add(new ExTransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
				UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.
				
				System.out.println("Creating and Mining Genesis block... ");
				ExBlock genesis = new ExBlock("0");
				genesis.addTransaction(genesisTransaction);
				addBlock(genesis);
				
				//testing
				ExBlock block1 = new ExBlock(genesis.hash);
				System.out.println("\nWalletA's balance is: " + walletA.getBalance());
				System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
				block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
				addBlock(block1);
				System.out.println("\nWalletA's balance is: " + walletA.getBalance());
				System.out.println("WalletB's balance is: " + walletB.getBalance());
				
				ExBlock block2 = new ExBlock(block1.hash);
				System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
				block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
				addBlock(block2);
				System.out.println("\nWalletA's balance is: " + walletA.getBalance());
				System.out.println("WalletB's balance is: " + walletB.getBalance());
				
				ExBlock block3 = new ExBlock(block2.hash);
				System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
				block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
				System.out.println("\nWalletA's balance is: " + walletA.getBalance());
				System.out.println("WalletB's balance is: " + walletB.getBalance());
				
				isChainValid();
				
			}

	
	public static Boolean isChainValid() {
		ExBlock currentBlock; 
		ExBlock previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		HashMap<String,ExTransactionOutput> tempUTXOs = new HashMap<String,ExTransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		

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
			//check if hash is solved
			if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
				System.out.println("#This block hasn't been mined");
				return false;
			}
	
	
			//loop thru blockchains transactions:
			ExTransactionOutput tempOutput;
			for(int t=0; t <currentBlock.transactions.size(); t++) {
				ExTransaction currentTransaction = currentBlock.transactions.get(t);
				
				if(!currentTransaction.verifySignature()) {
					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
					return false; 
				}
				if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
					return false; 
				}
				
				for(ExTransactionInput input: currentTransaction.inputs) {	
					tempOutput = tempUTXOs.get(input.transactionOutputId);
					
					if(tempOutput == null) {
						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}
					
					if(input.UTXO.value != tempOutput.value) {
						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}
					
					tempUTXOs.remove(input.transactionOutputId);
				}
				
				for(ExTransactionOutput output: currentTransaction.outputs) {
					tempUTXOs.put(output.id, output);
				}
				
				if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
					return false;
				}
				if( currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}
				
			}
		}
		
		System.out.println("Blockchain is valid");
		return true;
	}
	
		
	public static void addBlock(ExBlock newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}


}
