package ru.rtksoftlabs.licensegenerator.inno;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.rtksoftlabs.licensegenerator.dao.ProtectedObjectsDataBase;

@Component
@Profile("inno")
public class ProtectedObjectsDataImpl extends ProtectedObjectsDataBase {

}
