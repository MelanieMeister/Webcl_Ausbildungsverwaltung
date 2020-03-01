package myapp.controller;

import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.education.EducationAtt;
import myapp.presentationmodel.education.EducationCommands;
import myapp.service.EducationService;
import myapp.service.TranslationService;
import myapp.util.AdditionalTag;
import myapp.util.EntityController;
import org.opendolphin.core.Tag;
import org.opendolphin.core.server.EventBus;
import org.opendolphin.core.server.ServerPresentationModel;

/**
 * This is an example for an application specific controller.
 * <p>
 * Controllers may have many actions that serve a common purpose.
 * <p>
 */
class EducationController extends EntityController {

    EducationController(EducationService educationService, TranslationService translationService, EventBus eventBus) {
        super(PMDescription.EDUCATION_MASTER, PMDescription.EDUCATION,
                educationService, translationService,
                eventBus);
    }

    /**
     * initialize all pings to a method so that if the
     * user ping the Controller, the controller invoke
     * the specific method.
     */
    @Override
    public void specifyAllCommandSubscriptions() {
        super.specifyAllCommandSubscriptions();

        subscribeToCommand(EducationCommands.LOAD_ALL, this::loadAll);

        subscribeToCommand(EducationCommands.CREATE, this::createOnUserRequest);
        subscribeToCommand(EducationCommands.SAVE, this::saveOnUserRequest);
        subscribeToCommand(EducationCommands.DELETE, this::deleteOnUserRequest);

        subscribeToCommand(EducationCommands.SELECT_NEXT, this::selectNext);
        subscribeToCommand(EducationCommands.SELECT_PREVIOUS, this::selectPrevious);

        subscribeToCommand(EducationCommands.ON_PUSH, this::processEventsFromQueue);
        subscribeToCommand(EducationCommands.ON_RELEASE, this::onRelease);
    }

    @Override
    protected void registerAllProxyListeners(ServerPresentationModel proxy) {

        registerProxyListener(EducationAtt.AGE, evt -> {
            boolean valueSet = evt.getNewValue() != null && ((int) evt.getNewValue()) != 0;

            getDetailProxyPM().getAt(EducationAtt.IS_ADULT.name(), AdditionalTag.READ_ONLY).setValue(valueSet);
            getDetailProxyPM().getAt(EducationAtt.IS_ADULT.name(), Tag.MANDATORY).setValue(!valueSet);

            if (evt.getNewValue() != null) {
                getDetailProxyPM().getAt(EducationAtt.IS_ADULT.name()).setValue(((Integer) evt.getNewValue()) >= 18);
            }

        });

    }

}
