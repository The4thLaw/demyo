package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.Reader;
import org.demyo.service.IReaderService;

/**
 * Controller handling the API calls for {@link Reader}s.
 */
@RestController
@RequestMapping("/api/readers")
public class ReaderAPIController extends AbstractModelAPIController<Reader> {
	private final IReaderService service;

	/**
	 * Creates the controller.
	 * 
	 * @param service The service to manage the entries.
	 */
	@Autowired
	public ReaderAPIController(IReaderService service) {
		super(service);
		this.service = service;
	}

	/**
	 * Automatically selects a Reader if possible.
	 * 
	 * @return The Reader, or <code>null</code> if one cannot be automatically selected.
	 */
	@GetMapping("autoSelect")
	public Reader autoSelect() {
		return this.service.getUniqueReader();
	}
}
