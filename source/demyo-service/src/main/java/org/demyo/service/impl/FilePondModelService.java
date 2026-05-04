package org.demyo.service.impl;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.demyo.common.exception.DemyoException;
import org.demyo.model.IModel;
import org.demyo.model.Image;
import org.demyo.service.IFilePondModelService;
import org.demyo.service.IImageService;
import org.demyo.service.IModelService;
import org.demyo.service.ITranslationService;

@Service
public class FilePondModelService implements IFilePondModelService {
	@Autowired
	private ITranslationService translationService;
	@Autowired
	private IImageService imageService;

	@Override
	public <M extends IModel> void recoverFromFilePond(
			long modelId,
			String mainFilePondId, String[] otherFilePondIds,
			String mainLabel, String otherLabel,
			BiConsumer<M, Image> mainSetter, BiConsumer<M, List<Image>> otherSetter,
			IModelService<M> service, Function<M, String> modelName)
			throws DemyoException {
		M model = service.getByIdForEdition(modelId);
		String baseName = modelName.apply(model);

		if (!StringUtils.isBlank(mainFilePondId)) {
			String mainBaseName = translationService.translateVargs(mainLabel, baseName);
			Image mainImage = imageService.recoverImagesFromFilePond(mainBaseName, false, mainFilePondId).get(0);
			mainSetter.accept(model, mainImage);
		}

		if (otherFilePondIds != null && otherFilePondIds.length > 0) {
			String imageBaseName = translationService.translateVargs(otherLabel, baseName);
			List<Image> otherImages = imageService.recoverImagesFromFilePond(imageBaseName, true, otherFilePondIds);
			otherSetter.accept(model, otherImages);
		}

		service.save(model);
	}
}
