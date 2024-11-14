package com.chatbot.service;

import com.chatbot.response.ApiResponse;

public interface ChatbotService {
	
	ApiResponse getCoinDetails(String prompt) throws Exception; // This end point will return the api response, from the AI Chatbot
	
	String simpleChat(String prompt);
}
