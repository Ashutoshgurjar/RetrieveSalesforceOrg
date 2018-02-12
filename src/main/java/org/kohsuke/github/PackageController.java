package org.kohsuke.github;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PackageController {

	@RequestMapping(value = "/retrieveOrgBackup", method = RequestMethod.GET)
	public ModelAndView retrieveOrgBackup(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		//reg.doGet(request, response);
		
		ModelAndView model = new ModelAndView("Welcome");
		
		//mav.setViewName("Register");
		//Initializing helper class and calling helper class meathod
		//OrgConnectionCreator obj = new OrgConnectionCreator();
		//obj.createConnectionRecord(request, response);

		return model;
		
	}
	@RequestMapping(value = "/retrieveOrgBackupx", method = RequestMethod.GET)
	public void retrieveOrgBackupStart(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Register reg = new Register();
		
		
		reg.startProcessing(request, response);
	}
	
}