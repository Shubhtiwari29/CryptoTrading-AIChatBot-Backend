package com.chatbot.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatbot.response.ApiResponse;

@RestController
public class HomeController {
	
	@GetMapping("/")
	public ResponseEntity<ApiResponse>homeController() {
		ApiResponse response = new ApiResponse();
		response.setMessage("Welcome To Chatbot");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
