package myapp.servlet;

import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.server.adapter.DolphinServlet;

import myapp.controller.Reception;
import myapp.service.EducationService;
import myapp.service.TranslationService;
import myapp.service.impl.EducationServiceFileBased;
import myapp.service.impl.TranslationServiceResourceBundleBased;

public class MyAppServlet extends DolphinServlet {

	@Override
	protected void registerApplicationActions(DefaultServerDolphin serverDolphin) {
        EducationService educationService = new EducationServiceFileBased();

        TranslationService translationService = new TranslationServiceResourceBundleBased();

        serverDolphin.register(new Reception(educationService, translationService));
	}
}
