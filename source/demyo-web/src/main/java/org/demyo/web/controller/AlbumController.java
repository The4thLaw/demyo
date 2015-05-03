package org.demyo.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/albums")
public class AlbumController extends AbstractController {
	//@Autowired
	//private IAuthorDao authorDao;
	@RequestMapping("/")
	public String list() {
		//authorDao.findAuthors();
		return "albums/list";
	}

	@RequestMapping("/ajax")
	public String listAjax(Model model) {
		setLayoutAjax(model);
		return "albums/list";
	}
}
