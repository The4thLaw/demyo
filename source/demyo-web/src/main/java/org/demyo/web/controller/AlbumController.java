package org.demyo.web.controller;

import java.util.Set;

import org.demyo.model.Album;
import org.demyo.model.Author;
import org.demyo.model.Tag;
import org.demyo.service.IAlbumService;
import org.demyo.service.IModelServiceNG;
import org.demyo.service.ISeriesService;

import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/albums")
public class AlbumController extends AbstractModelControllerNG<Album> {
	@Autowired
	private IAlbumService service;
	@Autowired
	private ISeriesService seriesService;

	/**
	 * Default constructor.
	 */
	public AlbumController() {
		super(Album.class, "albums", "album");
	}

	@Override
	protected IModelServiceNG<Album> getService() {
		return service;
	}

	@Override
	protected void fillModelForEdition(Album entity, Model model) {
		model.addAttribute("series", seriesService.findAll());
	}

	@InitBinder
	private void initBinder(PropertyEditorRegistry binder) throws Exception {
		registerCollectionEditor(binder, Set.class, "writers", Author.class);
		registerCollectionEditor(binder, Set.class, "artists", Author.class);
		registerCollectionEditor(binder, Set.class, "colorists", Author.class);
		registerCollectionEditor(binder, Set.class, "tags", Tag.class);
	}

	//@Autowired
	//private IAuthorDao authorDao;
	/*@RequestMapping("/")
	public String list() {
		//authorDao.findAuthors();
		return "albums/list";
	}

	@RequestMapping("/ajax")
	public String listAjax(Model model) {
		setLayoutAjax(model);
		return "albums/list";
	}*/
}
