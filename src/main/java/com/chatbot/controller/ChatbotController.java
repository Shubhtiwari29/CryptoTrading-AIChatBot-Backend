package com.chatbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatbot.dto.PromptBody;
import com.chatbot.response.ApiResponse;
import com.chatbot.service.ChatbotService;

@RestController
@RequestMapping("/ai/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatbotController {

	@Autowired
	private ChatbotService chatBotService;

	@PostMapping
	public ResponseEntity<ApiResponse> getCoinDetailsHHandler(@RequestBody PromptBody prompt) throws Exception {

		ApiResponse response = chatBotService.getCoinDetails(prompt.getPrompt());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/simple")
	public ResponseEntity<String> simpleChatHandler(@RequestBody PromptBody prompt) throws Exception {

		String response = chatBotService.simpleChat(prompt.getPrompt());

//		ApiResponse response = new ApiResponse();
//		response.setMessage(prompt.getPrompt());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
