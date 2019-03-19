package hu.blackbelt.judo.tatami.psm2jql;

import hu.blackbelt.judo.meta.psm.jql.jqldsl.BinaryOperation;
import hu.blackbelt.judo.meta.psm.jql.jqldsl.Feature;
import hu.blackbelt.judo.meta.psm.jql.jqldsl.IntegerLiteral;
import hu.blackbelt.judo.meta.psm.jql.jqldsl.JqldslFactory;
import hu.blackbelt.judo.meta.psm.jql.jqldsl.NavigationExpression;
import hu.blackbelt.judo.meta.psm.jql.runtime.PsmJqlModelLoader;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.math.BigInteger;

public class DummyParser {

    static Resource parse(final String jqlExpression) {
        final String createdSourceModelName = "urn:jql.judo-meta-psm-jql";
        final ResourceSet jqlResourceSet = PsmJqlModelLoader.createPsmJqlResourceSet();
        final Resource jqlResource = jqlResourceSet.createResource(URI.createURI(createdSourceModelName));
        jqlResource.getContents().add(parseDemoExpression(jqlExpression));

        return jqlResource;
    }

    private static EObject parseDemoExpression(final String jqlExpression) {
        if (jqlExpression.startsWith("0 ")) {
            return parseDummyIntegerLiteral();
        } else if (jqlExpression.equals("self.quantity * self.unitPrice * (1 - self.discount)")) {
            return parseDummyExpression();
        } else {
            return parseNavigation(jqlExpression);
        }
    }

    private static EObject parseDummyIntegerLiteral() {
        final IntegerLiteral expression = JqldslFactory.eINSTANCE.createIntegerLiteral();
        expression.setValue(BigInteger.ZERO);

        return expression;
    }

    private static EObject parseDummyExpression() {
        final NavigationExpression quantityExpression = JqldslFactory.eINSTANCE.createNavigationExpression();
        quantityExpression.setBase("self");
        final Feature quantity = JqldslFactory.eINSTANCE.createFeature();
        quantity.setName("quantity");
        quantityExpression.getFeatures().add(quantity);

        final NavigationExpression unitPriceExpression = JqldslFactory.eINSTANCE.createNavigationExpression();
        unitPriceExpression.setBase("self");
        final Feature unitPrice = JqldslFactory.eINSTANCE.createFeature();
        unitPrice.setName("unitPrice");
        unitPriceExpression.getFeatures().add(unitPrice);

        final NavigationExpression discountExpression = JqldslFactory.eINSTANCE.createNavigationExpression();
        discountExpression.setBase("self");
        final Feature discount = JqldslFactory.eINSTANCE.createFeature();
        discount.setName("discount");
        discountExpression.getFeatures().add(discount);

        final IntegerLiteral one = JqldslFactory.eINSTANCE.createIntegerLiteral();
        one.setValue(BigInteger.ONE);

        final BinaryOperation rootExpression = JqldslFactory.eINSTANCE.createBinaryOperation();
        rootExpression.setOperator("*");

        final BinaryOperation priceWithoutDiscountOperand = JqldslFactory.eINSTANCE.createBinaryOperation();
        priceWithoutDiscountOperand.setOperator("*");
        priceWithoutDiscountOperand.setLeftOperand(quantityExpression);
        priceWithoutDiscountOperand.setRightOperand(unitPriceExpression);

        final BinaryOperation discountOperand = JqldslFactory.eINSTANCE.createBinaryOperation();
        discountOperand.setOperator("-");
        discountOperand.setLeftOperand(one);
        discountOperand.setRightOperand(discountExpression);

        rootExpression.setLeftOperand(priceWithoutDiscountOperand);
        rootExpression.setRightOperand(discountOperand);

        return rootExpression;
    }

    private static EObject parseNavigation(final String jqlExpression) {
        final NavigationExpression rootExpression = JqldslFactory.eINSTANCE.createNavigationExpression();

        final String[] parts = jqlExpression.replaceAll("/\\*.*\\*/", "").trim().split("\\.");
        rootExpression.setBase(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            final Feature feature = JqldslFactory.eINSTANCE.createFeature();
            feature.setName(parts[i]);
            rootExpression.getFeatures().add(feature);
        }

        return rootExpression;
    }
}
