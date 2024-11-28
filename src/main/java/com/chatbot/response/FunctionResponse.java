package com.chatbot.response;

/*This class is designed to store the output from an AI-based API (Gemini) and then be used in further processing,
 like fetching additional cryptocurrency details from a secondary API (CoinGecko).*/
public class FunctionResponse {
	
	private String currencyName;
	private String functionName;
	private String currencyData;
	public String getCurrencyName() {
		return currencyName;
	}
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getCurrencyData() {
		return currencyData;
	}
	public void setCurrencyData(String currencyData) {
		this.currencyData = currencyData;
	}
	
	
	
}
