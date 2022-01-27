package com.blockchain.ksj.core;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import com.blockchain.ksj.main.ExOpenChain;
import com.blockchain.ksj.util.ExStringUtil;

public class ExTransaction {
	public String transactionId; // this is also the hash of the transaction.
	public PublicKey sender; // senders address/public key. 계좌를 보내는 사람의 public key
	public PublicKey reciepient; // Recipients address/public key. 계좌를 받는 사람의 public key
	public float value;
	
	//암호화된 시그니쳐, 암호화된 서명을 통해 주소의 실제 주인이 트댄잭션을 통해 돈을 보내는지 확인가능, 
	//데이터변화감지(제 3자가 보내는 금액을 바꾸는것을 방지하는 역할)
	//기능 1. 실제 코인의 오너만이 그 코인을 사용할 수 있게 허락해주는 역할
	//기능 2. 새로운 블럭이 생성되기 전 이미 접수된 트랜잭션에 대해서 다른 사람들이 수정하지 못하도록 방지
	public byte[] signature; // this is to prevent anybody else from spending funds in our wallet.
	
	public ArrayList<ExTransactionInput> inputs = new ArrayList<ExTransactionInput>();
	public ArrayList<ExTransactionOutput> outputs = new ArrayList<ExTransactionOutput>();
	
	private static int sequence = 0; // a rough count of how many transactions have been generated. 
	
	// Constructor: 
	public ExTransaction(PublicKey from, PublicKey to, float value,  ArrayList<ExTransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}
	
	
	// This Calculates the transaction hash (which will be used as its Id)
	private String calulateHash() {
		sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
		return ExStringUtil.applySha256(
				ExStringUtil.getStringFromKey(sender) +
				ExStringUtil.getStringFromKey(reciepient) +
				Float.toString(value) + sequence
				);
	}
	
	//Signs all the data we dont wish to be tampered with.
		public void generateSignature(PrivateKey privateKey) {
			String data = ExStringUtil.getStringFromKey(sender) + ExStringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
			signature = ExStringUtil.applyECDSASig(privateKey,data);	
		}
		//Verifies the data we signed hasnt been tampered with
		public boolean verifySignature() {
			String data = ExStringUtil.getStringFromKey(sender) + ExStringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
			return ExStringUtil.verifyECDSASig(sender, data, signature);
		}
		
	//Returns true if new transaction could be created.	
	public boolean processTransaction() {
			
			if(verifySignature() == false) {
				System.out.println("#Transaction Signature failed to verify");
				return false;
			}
					
			//gather transaction inputs (Make sure they are unspent):
			for(ExTransactionInput i : inputs) {
				i.UTXO = ExOpenChain.UTXOs.get(i.transactionOutputId);
			}

			//check if transaction is valid:
			if(getInputsValue() < ExOpenChain.minimumTransaction) {
				System.out.println("#Transaction Inputs to small: " + getInputsValue());
				return false;
			}
			
			//generate transaction outputs:
			float leftOver = getInputsValue() - value; //get value of inputs then the left over change:
			transactionId = calulateHash();
			outputs.add(new ExTransactionOutput( this.reciepient, value,transactionId)); //send value to recipient
			outputs.add(new ExTransactionOutput( this.sender, leftOver,transactionId)); //send the left over 'change' back to sender		
					
			//add outputs to Unspent list
			for(ExTransactionOutput o : outputs) {
				ExOpenChain.UTXOs.put(o.id , o);
			}
			
			//remove transaction inputs from UTXO lists as spent:
			for(ExTransactionInput i : inputs) {
				if(i.UTXO == null) continue; //if Transaction can't be found skip it 
				ExOpenChain.UTXOs.remove(i.UTXO.id);
			}
			
			return true;
		}
		
	//returns sum of inputs(UTXOs) values
		public float getInputsValue() {
			float total = 0;
			for(ExTransactionInput i : inputs) {
				if(i.UTXO == null) continue; //if Transaction can't be found skip it 
				total += i.UTXO.value;
			}
			return total;
		}
				
	//returns sum of outputs(UTXOs) values
		public float getOutputsValue() {
			float total = 0;
			for(ExTransactionOutput o : outputs) {
				total += o.value;
			}
			return total;
		}
}
