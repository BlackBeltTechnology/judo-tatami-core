package hu.blackbelt.judo.tatami.psm2jql;

import com.google.inject.Injector;
import hu.blackbelt.judo.meta.psm.jql.JqlDslStandaloneSetupGenerated;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.resource.XtextResourceSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

@Slf4j
public class JqlParser {

    private final Injector injector = new JqlDslStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();

    public EObject parse(final String jqlExpression) {
        if (jqlExpression == null) {
            return null;
        }

        log.info("Parsing expression: {}", jqlExpression);

        try {
            // TODO - create valid EMF URI of input stream for XText parser
            final File tmpFile = File.createTempFile("parsing-", ".jql");
            tmpFile.deleteOnExit();
            final FileOutputStream fos = new FileOutputStream(tmpFile);
            fos.write(jqlExpression.getBytes());
            fos.close();
            final URI uri = URI.createFileURI(tmpFile.getAbsolutePath());

            final XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
            final Resource jqlResource = resourceSet.getResource(uri, true);

            // get first entry of jqlResource (root expression)
            final Iterator<EObject> iterator = jqlResource.getContents().iterator();
            if (iterator.hasNext()) {
                return EcoreUtil.copy(iterator.next());
            } else {
                return null;
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to parse expression", ex);
        }
    }
}
