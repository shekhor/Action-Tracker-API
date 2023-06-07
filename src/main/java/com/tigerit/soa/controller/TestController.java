package com.tigerit.soa.controller;

import com.tigerit.soa.request.EmailRequest;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.response.StatusCode;
import com.tigerit.soa.service.email.EmailService;
import com.tigerit.soa.util.TempoStaticDataProvider;
import com.tigerit.soa.util.Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log4j2
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

	@Autowired
	private EmailService emailService;
	//demo
	private static List<String> demoList=new ArrayList<>();

	@GetMapping("/all")
	public String allAccess() {
		return "Public Content.";
	}
	
	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public String userAccess() {
		return "User Content.";
	}

	@GetMapping("/mod")
	@PreAuthorize("hasRole('MODERATOR')")
	public String moderatorAccess() {
		return "Moderator Board.";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Admin Board.";
	}

	//CREATE_DEMO
	@PostMapping("/demo/create")
	@PreAuthorize("hasAnyRole('CREATE_DEMO')")
	public String createDemo(@RequestBody String demoName){
		demoList.add(demoName);
		return "Added: "+demoName;
	}
	//UPDATE_DEMO
	@PostMapping("/demo/update")
	@PreAuthorize("hasAnyRole('UPDATE_DEMO')")
	public String updateDemo(@RequestBody String demoName){
		int index=0;
		if(demoList.contains(demoName)){
			index = demoList.indexOf(demoName);
			demoList.set(index,demoName+"-Update");
			return "Updated from"+demoName+" to "+demoName+"-Update";
		}else{
			return "Update failed , Not found";
		}

	}
	//DELETE_DEMO
	@PostMapping("/demo/delete")
	@PreAuthorize("hasAnyRole('DELETE_DEMO')")
	public String deleteDemo(@RequestBody String demoName){
		if(demoList.contains(demoName)){
			int index=demoList.indexOf(demoName);
			demoList.remove(index);
			return "Deleted "+demoName;
		}else{
			return "Update failed , Not found";
		}
	}
	//READ_DEMO
	@PostMapping("/demo/read")
	@PreAuthorize("hasAnyRole('READ_DEMO')")
	public List<String> getDemoList(){
		return demoList;
	}

	@PostMapping("/send-email")
	public ResponseEntity<ServiceResponse> sendEmail(@Valid @RequestBody EmailRequest emailRequest, BindingResult bindingResult)
	{
		ServiceResponse response;
		if(bindingResult.hasErrors())
		{
			response = Util.requestErrorHandler(bindingResult);
			return ResponseEntity.ok(response);
		}


		try
		{
			emailService.sendSimpleMail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getBody());
			log.info("email successfully sent");
			return ResponseEntity.ok(new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, "Email sent", Collections.emptyList()));

		}
		catch (Exception e)
		{
			log.error("email err:"+ e.getMessage());
		}
		return ResponseEntity.ok(new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR, StatusCode.ERROR, "Email not sent", Collections.emptyList()));

	}
}
