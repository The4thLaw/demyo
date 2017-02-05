package org.demyo.web.controller;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.demyo.model.Album;
import org.demyo.model.Author;
import org.demyo.model.Image;
import org.demyo.model.ModelView;
import org.demyo.model.Tag;
import org.demyo.model.util.AuthorComparator;
import org.demyo.model.util.IdentifyingNameComparator;
import org.demyo.service.IAlbumService;
import org.demyo.service.IAuthorService;
import org.demyo.service.IBindingService;
import org.demyo.service.IImageService;
import org.demyo.service.IModelService;
import org.demyo.service.IPublisherService;
import org.demyo.service.ISeriesService;
import org.demyo.service.ITagService;

import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Controller for {@link Album} management.
 */
@Controller
@RequestMapping("/albums")
public class AlbumController extends AbstractModelController<Album> {
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

	/**
	 * Finds the Albums without Series.
	 * 
	 * @return The list of Albums.
	 */
	@JsonView(ModelView.Minimal.class)
	@RequestMapping(value = "/withoutSeries", method = RequestMethod.GET, consumes = "application/json",
			produces = "application/json")
	@ResponseBody
	public List<Album> getAlbumsWithoutSeries() {
		return service.findBySeriesId(null);
	}

	/**
	 * Adds an {@link Album}.
	 * 
	 * @param model The view model.
	 * @param seriesId The ID of the {@link org.demyo.model.Series Series} to add the album to.
	 * @return The view name.
	 */
	// params attributes allows overriding the parent /add
	@RequestMapping(value = "/add", params = { "toSeries" }, method = RequestMethod.GET)
	public String add(Model model, @RequestParam(name = "toSeries", required = false) long seriesId) {
		model.addAttribute("album", service.getAlbumTemplateForSeries(seriesId));
		fillModelForEdition(null, model);

		return "albums/add-edit";
	}

	@Override
	protected IModelService<Album> getService() {
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
		registerCollectionEditor(binder, SortedSet.class, "inkers", Author.class, authComp);
		registerCollectionEditor(binder, SortedSet.class, "translators", Author.class, authComp);
		registerCollectionEditor(binder, SortedSet.class, "tags", Tag.class, new IdentifyingNameComparator());
		registerCollectionEditor(binder, Set.class, "images", Image.class);
	}

	/*@RequestMapping("/ajax")
	public String listAjax(Model model) {
		setLayoutAjax(model);
		return "albums/list";
	}*/
}
