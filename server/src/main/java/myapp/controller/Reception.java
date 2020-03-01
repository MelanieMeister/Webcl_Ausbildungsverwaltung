package myapp.controller;

import myapp.service.EducationService;
import org.opendolphin.core.server.EventBus;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;

import myapp.service.TranslationService;

/**
 * At the reception all controllers check in.
 *
 */

public class Reception extends DolphinServerAction {
    private static final EventBus THE_ONE_AND_ONLY_EVENT_BUS_IN_VM = new EventBus();

    private final EducationService educationService;

    private final TranslationService translationService;

    public Reception(EducationService educationService, TranslationService translationService) {
        this.educationService = educationService;
        this.translationService = translationService;

    }

    public void registerIn(ActionRegistry registry) {
        getServerDolphin().register(new EducationController(educationService, translationService,
                                                         THE_ONE_AND_ONLY_EVENT_BUS_IN_VM));

        //always needed
        getServerDolphin().register(new ApplicationStateController(translationService));
    }
}
