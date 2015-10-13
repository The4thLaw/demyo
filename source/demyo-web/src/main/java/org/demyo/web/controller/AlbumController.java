package org.demyo.web.controller;

import java.util.Set;
import java.util.SortedSet;

import org.demyo.model.Album;
import org.demyo.model.Author;
import org.demyo.model.Image;
import org.demyo.model.Tag;
import org.demyo.model.util.AuthorComparator;
import org.demyo.model.util.IdentifyingNameComparator;
import org.demyo.service.IAlbumService;
import org.demyo.service.IAuthorService;
import org.demyo.service.IBindingService;
import org.demyo.service.IImageService;
import org.demyo.service.IModelServiceNG;
import org.demyo.service.IPublisherService;
import org.demyo.service.ISeriesService;
import org.demyo.service.ITagService;

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
	@Autowired
	private IPublisherService publisherService;
	@Autowired
	private IAuthorService authorService;
	@Autowired
	private IBindingService bindingService;
	@Autowired
	private ITagService tagService;
	@Autowired
	private IImageService imageService;

	/**
	 * Default constructor.
	 */
	public AlbumController() {
		super(Album.class, "albums", "album");
	}

	// TODO: pre-fill from last album for addition of one to a specific series

	@Override
	protected IModelServiceNG<Album> getService() {
		return service;
	}

	@Override
	protected void fillModelForEdition(Album entity, Model model) {
		model.addAttribute("series", seriesService.findAll());
		model.addAttribute("publishers", publisherService.findAll());
		model.addAttribute("authors", authorService.findAll());
		model.addAttribute("bindings", bindingService.findAll());
		model.addAttribute("tags", tagService.findAll());
		model.addAttribute("images", imageService.findAll());
	}

	@InitBinder
	private void initBinder(PropertyEditorRegistry binder) throws Exception {
		AuthorComparator authComp = new AuthorComparator();
		registerCollectionEditor(binder, SortedSet.class, "writers", Author.class, authComp);
		registerCollectionEditor(binder, SortedSet.class, "artists", Author.class, authComp);
		registerCollectionEditor(binder, SortedSet.class, "colorists", Author.class, authComp);
		registerCollectionEditor(binder, SortedSet.class, "tags", Tag.class, new IdentifyingNameComparator());
		registerCollectionEditor(binder, Set.class, "images", Image.class);
	}

	/*@RequestMapping("/ajax")
	public String listAjax(Model model) {
		setLayoutAjax(model);
		return "albums/list";
	}*/
}
