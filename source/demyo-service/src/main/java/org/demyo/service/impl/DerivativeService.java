package org.demyo.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.common.exception.DemyoException;
import org.demyo.dao.IDerivativeRepo;
import org.demyo.dao.IModelRepo;
import org.demyo.model.Derivative;
import org.demyo.model.Image;
import org.demyo.model.filters.DerivativeFilter;
import org.demyo.model.util.DerivativeComparator;
import org.demyo.service.IDerivativeService;
import org.demyo.service.IImageService;
import org.demyo.service.ITranslationService;

/**
 * Implements the contract defined by {@link IDerivativeService}.
 */
@Service
public class DerivativeService extends AbstractModelService<Derivative> implements IDerivativeService {
	@Autowired
	private IDerivativeRepo repo;
	@Autowired
	private IImageService imageService;
	@Autowired
	private ITranslationService translationService;

	/**
	 * Default constructor.
	 */
	public DerivativeService() {
		super(Derivative.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Derivative> findAllForIndex() {
		return findAllForIndex(null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Derivative> findAllForIndex(DerivativeFilter filter) {
		// Sort manually:
		// - The Sort + entity graph cause a CROSS JOIN that's not even outer, messing with the results
		// - We get control on one shots, which would otherwise be listed first or last
		List<Derivative> derivatives;
		if (filter == null) {
			derivatives = repo.findAll();
		} else {
			derivatives = repo.findAll(filter.toPredicate());
		}

		derivatives.sort(new DerivativeComparator());

		return derivatives;
	}

	@Override
	protected IModelRepo<Derivative> getRepo() {
		return repo;
	}

	@Override
	protected Derivative reloadIfNeeded(Derivative model) {
		if (model.getId() == null) {
			return model;
		}

		Derivative loaded = getByIdForEdition(model.getId());
		BeanUtils.copyProperties(model, loaded);
		return loaded;
	}

	@Transactional(rollbackFor = Throwable.class)
	@CacheEvict(cacheNames = "ModelLists", key = "#root.targetClass.simpleName.replaceAll('Service$', '')")
	@Override
	public void recoverFromFilePond(long derivativeId, String[] otherFilePondIds) throws DemyoException {
		Derivative derivative = getByIdForEdition(derivativeId);
		String baseName = derivative.getBaseNameForImages();

		if (otherFilePondIds != null && otherFilePondIds.length > 0) {
			String imageBaseName = translationService.translateVargs("special.filepond.Derivative.baseImageName",
					baseName);
			List<Image> images = imageService.recoverImagesFromFilePond(imageBaseName, true, otherFilePondIds);
			derivative.getImages().addAll(images);

			save(derivative); // Only save if we changed something
		}
	}

	@Override
	public long countDerivativesByFilter(DerivativeFilter filter) {
		return repo.count(filter.toPredicate());
	}
}
