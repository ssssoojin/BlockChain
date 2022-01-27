package com.blockchain.ksj.core;

public class ExTransactionInput {
	public String transactionOutputId;	//Reference to TransactionOutputs -> transactionId
	public ExTransactionOutput UTXO; 		//Contains the Unspent transaction output
	
	
	//TransactionOutput들 중에서 사용되지 않은 것들을 기록
	public ExTransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}
}
