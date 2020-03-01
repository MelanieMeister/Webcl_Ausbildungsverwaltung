package myapp;

import javafx.application.Application;


import org.opendolphin.core.server.ServerDolphin;

import myapp.controller.Reception;
import myapp.service.EducationService;
import myapp.service.TranslationService;
import myapp.service.impl.EducationServiceFileBased;
import myapp.service.impl.TranslationServiceResourceBundleBased;
import myapp.util.DefaultCombinedDolphinProvider;

/**
 * Starts a JavaFX client and controller with services as one combined, local application.
 */

public class CombinedStarter {

    public static void main(String[] args)  {
        DefaultCombinedDolphinProvider dolphinProvider = new DefaultCombinedDolphinProvider();

        registerApplicationActions(dolphinProvider.getServerDolphin());
        MyAppView.clientDolphin = dolphinProvider.getClientDolphin();

        Application.launch(MyAppView.class);
    }

    private static void registerApplicationActions(ServerDolphin serverDolphin) {
        EducationService educationService = new EducationServiceFileBased();


        TranslationService translationService = new TranslationServiceResourceBundleBased();

        serverDolphin.register(new Reception(educationService, translationService));
    }

}
