package hu.blackbelt.judo.tatami.psm2jql;

import com.google.inject.Injector;
import hu.blackbelt.judo.meta.psm.jql.JqlDslStandaloneSetupGenerated;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResourceSet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@Slf4j
public class JqlParser {

    public static final String JQLSCRIPT_CONTENT_TYPE = "jqlscript";
    private static Injector injectorInstance = null;  // = new JqlDslStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();

    private static Injector injector() {
        if (injectorInstance == null) {
            injectorInstance =  new JqlDslStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
            Resource.Factory.Registry.INSTANCE.getContentTypeToFactoryMap().put(JQLSCRIPT_CONTENT_TYPE, (IResourceFactory) injectorInstance.getInstance(IResourceFactory.class));

        }
        return injectorInstance;
    }


    public EObject parse(final String jqlExpression) {
        if (jqlExpression == null) {
            return null;
        }

        if (log.isDebugEnabled()) {
            log.info("Parsing expression: {}", jqlExpression);
        }

        try {
            Resource jqlResource = injector().getInstance(XtextResourceSet.class).createResource(URI.createURI(jqlExpression), JQLSCRIPT_CONTENT_TYPE);
            InputStream in = new ByteArrayInputStream(jqlExpression.getBytes("UTF-8"));
            jqlResource.load(in, injector().getInstance(XtextResourceSet.class).getLoadOptions());

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
