package com.chatbot.service;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;
import com.chatbot.dto.CoinDto;
import com.chatbot.response.ApiResponse;
import com.chatbot.response.FunctionResponse;

@Service
public class ChatbotServiceImpl implements ChatbotService {

	String GEMINI_API_KEY = "AIzaSyBAGktncjfKE_bqBSsz9whx8pz3xKo_GeE";

	private double convertToDouble(Object value) // It will convert our field to double.
	{
		if (value instanceof Integer) {
			return ((Integer) value).doubleValue();
		} else if (value instanceof Long) {
			return ((Long) value).doubleValue();
		} else if (value instanceof Double) {
			return (Double) value;
		} else
			throw new IllegalArgumentException("unsupported type" + value.getClass().getName());
	}
	//This method extracts the currency name from the user's input prompt.
	public CoinDto fetchCurrenyData(String currencyName) throws Exception {

		/*This method fetches cryptocurrency data from the CoinGecko API and stores it in a Map
		so you can extract the necessary information and set it in the CoinDto object.*/

		String url = "https://api.coingecko.com/api/v3/coins/"+currencyName;
		RestTemplate restTemplate = new RestTemplate(); //TO MAKE HTTP API REQUEST.

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class);
		Map<String, Object> responseBody = responseEntity.getBody();

		if (responseBody != null) {
			Map<String, Object> image = (Map<String, Object>) responseBody.get("image");
			Map<String, Object> marketData = (Map<String, Object>) responseBody.get("market_data");

			CoinDto coinDto = new CoinDto();
			coinDto.setId((String) responseBody.get("id"));
			coinDto.setName((String) responseBody.get("name"));
			coinDto.setSymbol((String) responseBody.get("symbol"));
			coinDto.setImage((String) image.get("large"));

//            MARKET DATA
			coinDto.setCurrentPrice(
					convertToDouble(((Map<String, Object>) marketData.get("current_price")).get("usd")));
			coinDto.setMarketCap(convertToDouble(((Map<String, Object>) marketData.get("market_cap")).get("usd")));
			coinDto.setMarketCapRank(convertToDouble((marketData.get("market_cap_rank"))));
			coinDto.setTotalVolume(convertToDouble(((Map<String, Object>) marketData.get("total_volume")).get("usd")));
			coinDto.setHigh24h(convertToDouble(((Map<String, Object>) marketData.get("high_24h")).get("usd")));
			coinDto.setLow24h(convertToDouble(((Map<String, Object>) marketData.get("low_24h")).get("usd")));

			coinDto.setPriceChange24h(convertToDouble((marketData.get("price_change_24h"))));
			coinDto.setPriceChangePercentage24h(convertToDouble((marketData.get("price_change_percentage_24h"))));
			coinDto.setMarketCapChange24h(convertToDouble((marketData.get("market_cap_change_24h"))));
			coinDto.setMarketCapChangePercentage24h(
					convertToDouble((marketData.get("market_cap_change_percentage_24h"))));
			coinDto.setCirculatingSupply(convertToDouble((marketData.get("circulating_supply"))));
			coinDto.setTotalSupply(convertToDouble((marketData.get("total_supply"))));

			return coinDto;
		}
		throw new Exception("coin not found");
	}

	public FunctionResponse getFunctionResponse(String prompt) {
		String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key="
				+ GEMINI_API_KEY;

		// Create JSON request body using method chaining
		JSONObject requestBodyJson = new JSONObject()
				.put("contents",
						new JSONArray().put(new JSONObject().put("parts",
								new JSONArray().put(new JSONObject().put("text", prompt)))))
				.put("tools", new JSONArray().put(new JSONObject().put("functionDeclarations",
						new JSONArray().put(new JSONObject().put("name", "getCoinDetails")
								.put("description", "Get the coin details from given currency object").put("parameters",
										new JSONObject().put("type", "OBJECT").put("properties", new JSONObject()
												.put("currencyName",
														new JSONObject().put("type", "STRING").put("description",
																"The currency name, " + "id, symbol."))
												.put("currencyData", new JSONObject().put("type", "STRING")
														.put("description", "Currency Data id, " + "symbol, " + "name, "
																+ "image, " + "current_price, " + "market_cap, "
																+ "market_cap_rank, " + "fully_diluted_valuation, "
																+ "total_volume, high_24h, "
																+ "low_24h, price_change_24h, "
																+ "price_change_percentage_24h, "
																+ "market_cap_change_24h, "
																+ "market_cap_change_percentage_24h, "
																+ "circulating_supply, " + "total_supply, "
																+ "max_supply, " + "ath, " + "ath_change_percentage, "
																+ "ath_date, " + "atl, " + "atl_change_percentage, "
																+ "atl_date, last_updated.")))
												.put("required",
														new JSONArray().put("currencyName").put("currencyData")))))));

		// Create HTTP headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Create the HTTP entity with headers and request body
		HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJson.toString(), headers);

		// Make the POST request
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL, requestEntity, String.class);

		String responseBody = response.getBody();

		JSONObject jsonObject = new JSONObject(responseBody);

		// Extract the first candidate
		JSONArray candidates = jsonObject.getJSONArray("candidates");
		JSONObject firstCandidate = candidates.getJSONObject(0);

		// Extract the function call details
		JSONObject content = firstCandidate.getJSONObject("content");
		JSONArray parts = content.getJSONArray("parts");
		JSONObject firstPart = parts.getJSONObject(0);
		JSONObject functionCall = firstPart.getJSONObject("functionCall");

		String functionName = functionCall.getString("name");
		JSONObject args = functionCall.getJSONObject("args");
		String currencyName = args.getString("currencyName");
		String currencyData = args.getString("currencyData");

		// Print or use the extracted values
		System.out.println("Function Name: " + functionName);
		System.out.println("Currency Name: " + currencyName);
		System.out.println("Currency Data: " + currencyData);

		FunctionResponse res = new FunctionResponse();
		res.setFunctionName(functionName);
		res.setCurrencyName(currencyName);
		res.setCurrencyData(currencyData);

		return res;
	}


	/*BELOW METHOD Takes a user prompt
	Sends this data to Gemini API to generate a user-friendly response.
	The generated response is then fetched from the CoinGecko API using the fetched currency name.
	The fetched data is then returned as an ApiResponse object.
	Returns the AI-generated response as an ApiResponse object.*/
	@Override
	public ApiResponse getCoinDetails(String prompt) throws Exception {
		FunctionResponse res = getFunctionResponse(prompt);
		CoinDto apiResponse = fetchCurrenyData(res.getCurrencyName().toLowerCase());
		

		String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key="
				+ GEMINI_API_KEY; //This sets the Gemini API URL using the API key GEMINI_API_KEY.
		                         // This URL is used to send a POST request to generate AI content based on the cryptocurrency data.
		                        //The Gemini API returns a generated response (e.g., "Bitcoin is the largest cryptocurrency by market cap..."),
								// which is extracted and returned as ApiResponse.
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Create JSON body using method chaining
		String body = new JSONObject()
				.put("contents",
						new JSONArray()
								.put(new JSONObject().put("role", "user").put("parts",
										new JSONArray().put(new JSONObject().put("text", prompt))))
								.put(new JSONObject().put("role", "model").put("parts",
										new JSONArray().put(new JSONObject().put("functionCall",
												new JSONObject().put("name", "getCoinDetails").put("args",
														new JSONObject().put("currencyName", res.getCurrencyName())
																.put("currencyData", res.getCurrencyData()))))))
								.put(new JSONObject().put("role", "function").put("parts",
										new JSONArray().put(new JSONObject().put("functionResponse",
												new JSONObject().put("name", "getCoinDetails").put("response",
														new JSONObject().put("name", "getCoinDetails").put("content",
																apiResponse)))))))
				.put("tools",
						new JSONArray().put(new JSONObject().put("functionDeclarations",
								new JSONArray().put(new JSONObject().put("name", "getCoinDetails")
										.put("description", "Get crypto currency data from given currency object.")
										.put("parameters",
												new JSONObject().put("type", "OBJECT")
														.put("properties", new JSONObject()
																.put("currencyName",
																		new JSONObject()
																				.put("type", "STRING").put(
																						"description",
																						"The currency Name, " + "id, "
																								+ "symbol."))
																.put("currencyData",
																		new JSONObject().put("type", "STRING").put(
																				"description",
																				"The currency data id, "
																						+ "symbol, current price, "
																						+ "image, " 
																						+ "market cap rank"
																						+ "market cap extra...")))
														.put("required", new JSONArray()
																.put("currencyName")
																.put("currencyData"))))

						))).toString();

		HttpEntity<String> request = new HttpEntity<>(body, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL, request, String.class);

		String responseBody = response.getBody();

		System.out.println("------ " + responseBody);

		JSONObject jsonObject = new JSONObject(responseBody);

		// Extract the first candidate
		JSONArray candidates = jsonObject.getJSONArray("candidates");
		JSONObject firstCandidate = candidates.getJSONObject(0);

		// Extract the text
		JSONObject content = firstCandidate.getJSONObject("content");
		JSONArray parts = content.getJSONArray("parts");
		JSONObject firstPart = parts.getJSONObject(0);
		String text = firstPart.getString("text");

		ApiResponse answer = new ApiResponse();

		answer.setMessage(text);
		
		return answer;
	}

/* SIMPLE CHAT
 	Takes a user prompt as input.
	Sends the prompt to the Gemini API.
	Returns the response from the API as a string.
	This method is useful for general conversational interactions with the AI model,
 	allowing it to generate responses to simple queries or chat inputs.*/

	@Override
	public String simpleChat(String prompt) {

		String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent?key="
				+ GEMINI_API_KEY;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String requestBody = new JSONObject()
				.put("contents", new JSONArray()
						.put(new JSONObject().put("parts", new JSONArray().put(new JSONObject().put("text", prompt)))))
				.toString();

		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL, requestEntity, String.class);

		return response.getBody();
	}

}



/*NOTES ->
* RestTemplate is a class provided by Spring Framework to simplify making HTTP requests to RESTful web services.
* Here, new RestTemplate() creates an instance of RestTemplate that can be used to send HTTP requests (like GET, POST, PUT, etc.)
* and receive responses.
* It's responsible for managing the underlying HTTP connection, handling response codes, and parsing the response body.
*
* HttpHeaders is a class used to represent the HTTP headers in a request.
* In this case, an empty HttpHeaders object is created. If you needed to add any custom headers (e.g., API keys, content type, etc.),
* you would set them here.
*
* HttpEntity is a helper class that represents an HTTP request or response entity, including headers and the body.
Here, it's wrapping the headers object created earlier.
Since no body is provided (HttpEntity<String> with no body), this represents an HTTP request with only headers.
* Map.class tells Spring to convert the JSON response into a Map<String, Object> where:
The keys are the JSON field names.
The values are the corresponding JSON values.
*responseEntity.getBody() retrieves the body of the HTTP response, which contains the actual data returned by the API.
This body is returned as a Map<String, Object>, where you can access the different fields of the JSON response using their keys. */