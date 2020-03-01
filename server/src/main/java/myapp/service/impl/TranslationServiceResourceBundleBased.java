package myapp.service.impl;

import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import myapp.service.TranslationService;
import myapp.util.Language;


public class TranslationServiceResourceBundleBased implements TranslationService{
    private static final String RESOURCEBUNDLE_DIRECTORY = "resourcebundles/";

    @Override
    public String translate(String pmType, String attributeName, Language language) {
        return translate(getResourceBundle(pmType, language), attributeName);
    }

    @Override
    public String translateCommand(String command, Language language) {
        return command;
    }

    private String translate(ResourceBundle bundle, String propertyName) {
        String labelText;
        if (bundle != null && bundle.containsKey(propertyName)) {
           labelText = bundle.getString(propertyName);
        } else {
            labelText = Arrays.stream(propertyName.split("_"))
                  .map(this::firstLetterCaps)
                  .collect(Collectors.joining(" "));
        }
        return labelText;
    }

    private ResourceBundle getResourceBundle(String pmType, Language language) {
       String baseName  = RESOURCEBUNDLE_DIRECTORY + pmType;
        try {
            return ResourceBundle.getBundle(baseName, language.getLocale());
        } catch (MissingResourceException e) {
            return null;
        }
    }

    private String firstLetterCaps(String part) {
        String firstLetter = part.substring(0, 1).toUpperCase();
        String restLetters = part.substring(1).toLowerCase();
        return firstLetter + restLetters;
    }

}
