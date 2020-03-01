package myapp.service;

import myapp.util.Language;

public interface TranslationService {
    String translate(String pmType, String attributeName,  Language language);

    String translateCommand(String command, Language language);
}
