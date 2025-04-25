package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.BookType;
import org.demyo.service.impl.IBookTypeService;


/**
 * Controller handling the API calls for {@link BookType}s.
 */
@RestController
@RequestMapping("/api/bookTypes")
public class BookTypeAPIController extends AbstractModelAPIController<BookType> {
	private final IBookTypeService service;

	/**
	 * Creates the controller.
	 *
	 * @param service The service to manage the entries.
	 */
	@Autowired
	public BookTypeAPIController(IBookTypeService service) {
		super(service);
		this.service = service;
	}

	/**
	 * Checks if book type management has been explicitly enabled.
	 *
	 * @return <code>true</code> if it's enabled.
	 */
	@GetMapping("management/enabled")
	public boolean isManagementEnabled() {
		return service.isManagementEnabled();
	}

	/**
	 * Enables book type management.
	 */
	@PostMapping("management/enable")
	public void enableManagement() {
		service.enableManagement();
	}

}
