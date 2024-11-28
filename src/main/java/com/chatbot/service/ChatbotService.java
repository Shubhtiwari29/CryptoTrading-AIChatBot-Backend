package com.chatbot.service;

import com.chatbot.response.ApiResponse;

public interface ChatbotService {


	// Below end point will return the api response, from the AI Chatbot
	// example-> used asked what's the current price of Bitcoin,this endpoint will respond with the current price of Bitcoin.
	ApiResponse getCoinDetails(String prompt) throws Exception;
	
	String simpleChat(String prompt);
}
