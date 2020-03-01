package myapp.util;

import org.opendolphin.core.Tag;


public interface AdditionalTag {
    Tag READ_ONLY          = Tag.tagFor.get("readOnly");
    Tag VALID              = Tag.tagFor.get("valid");
    Tag VALIDATION_MESSAGE = Tag.tagFor.get("validationMessage");
}
