package org.demyo.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController extends AbstractController {
	@RequestMapping("/")
	public String viewHome() {
		return "core/home";
	}
}
