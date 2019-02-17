/**
 * 
 */
package com.boot.tableau.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author 870909
 *
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/tableau")
public class TableauRestAPI {

	private static final Logger log = LoggerFactory.getLogger(TableauRestAPI.class);

	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${signoutUrl}")
	private String signoutUrl; 
	
	@Value("${siginUrl}")
	private String siginUrl; 

	@Value("${dashboardKeyUrl}")
	private String dashboardKeyUrl; 
	
	@Value("${getSitesUrl}")
	private String getSitesUrl; 
	
	@Value("${target_site}")
	private String target_site; 
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping("/login")
	public ResponseEntity<?> login(@RequestParam("user") String username, @RequestParam("password") String password) {

		try {
			String authKey = null;
			String loginrequest = "<tsRequest>" + "<credentials name=\"" + username + "\"" + " password=\"" + password
					+ "\" >" + "<site contentUrl=\""+ target_site + "\"/>" + "</credentials>" + "</tsRequest>";
			ResponseEntity<Credentials> response = restTemplate.postForEntity(siginUrl, loginrequest, Credentials.class);
			HttpStatus status = response.getStatusCode();
			Credentials loginrestCall = response.getBody();
			log.info("Login API Response --> " + loginrestCall);

			if (status.equals(HttpStatus.OK)) {
				authKey = loginrestCall.getCredentials().getToken();

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

				MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
				map.add("username", username);
				map.add("target_site", target_site);

				HttpEntity<MultiValueMap<String, String>> keyrequest = new HttpEntity<>(map, headers);
				ResponseEntity<String> keyresponse = restTemplate.postForEntity(dashboardKeyUrl, keyrequest,
						String.class);
				HttpStatus keystatus = keyresponse.getStatusCode();
				String keyrestCall = keyresponse.getBody();
				log.info("DashBoard Key API Response --> " + keyrestCall);
				SigninResponse signinResponse = new SigninResponse(authKey, keyrestCall);
				if (keystatus.equals(HttpStatus.OK)) {
					return new ResponseEntity<Object>(signinResponse, HttpStatus.OK);
				} else {
					return new ResponseEntity<Error>(new Error("Unable to get the Dashboard Key"),
							HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<Error>(new Error("Login Failed"), HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			return new ResponseEntity<Error>(new Error("Exception when in Login and Getting the dashboard key"),
					HttpStatus.BAD_REQUEST);
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping("/refresh")
	public ResponseEntity<?> refresh(@RequestParam("authKey") String authKey, @RequestParam("user") String username) {

		try {

			HttpHeaders getSitesheaders = new HttpHeaders();
			getSitesheaders.setContentType(MediaType.APPLICATION_JSON);
			getSitesheaders.set("x-tableau-auth", authKey);

			MultiValueMap<String, String> getSitesmap = new LinkedMultiValueMap<>();

			HttpEntity<MultiValueMap<String, String>> getSitesrequest = new HttpEntity<>(getSitesmap, getSitesheaders);
			ResponseEntity<String> getSitesresponse = restTemplate.exchange(getSitesUrl, HttpMethod.GET, getSitesrequest,
					String.class);
			HttpStatus getSitesstatus = getSitesresponse.getStatusCode();

			if (getSitesstatus.equals(HttpStatus.OK)) {

				log.info("Get Sites API Call is Success ");

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

				MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
				map.add("username", username);
				map.add("target_site", target_site);

				HttpEntity<MultiValueMap<String, String>> keyrequest = new HttpEntity<>(map, headers);
				ResponseEntity<String> keyresponse = restTemplate.postForEntity(dashboardKeyUrl, keyrequest,
						String.class);
				HttpStatus keystatus = keyresponse.getStatusCode();
				String keyrestCall = keyresponse.getBody();
				log.info("DashBoard Key API Response --> " + keyrestCall);
				SigninResponse signinResponse = new SigninResponse(authKey, keyrestCall);
				if (keystatus.equals(HttpStatus.OK)) {
					return new ResponseEntity<Object>(signinResponse, HttpStatus.OK);
				} else {
					return new ResponseEntity<Error>(new Error("Unable to get the Dashboard Key"),
							HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<Error>(new Error("Invalid authentication key provided"),
						HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			return new ResponseEntity<Error>(
					new Error("Exception when in Validating the Login and Getting the dashboard key"),
					HttpStatus.BAD_REQUEST);
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping("/logout")
	public ResponseEntity<?> logout(@RequestParam("authKey") String authKey) {

		try {

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("x-tableau-auth", authKey);

			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			HttpEntity<MultiValueMap<String, String>> signoutrequest = new HttpEntity<>(map, headers);
			ResponseEntity<String> signoutresponse = restTemplate.postForEntity(signoutUrl, signoutrequest,
					String.class);
			String signoutrestCall = signoutresponse.getBody();
			log.info("Signout API Response --> " + signoutrestCall);

		} catch (Exception e) {
			return new ResponseEntity<Error>(new Error("Unable Signout the user"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Object>("Singout Successful", HttpStatus.OK);
	}

}
