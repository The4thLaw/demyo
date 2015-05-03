package org.demyo.service;

public interface ITranslationService {

	String translate(String labelId);

	String translate(String labelId, Object[] params);

}
