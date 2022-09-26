package hu.blackbelt.judo.tatami.core;

/*-
 * #%L
 * Judo :: Tatami :: Core
 * %%
 * Copyright (C) 2018 - 2022 BlackBelt Technology
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PrettyPrinter {
    private static final String INDENT = "    "; //$NON-NLS-1$

    public static String prettyPrint(EObject object) {
        Set<EObject> visited = Sets.newHashSet();
        List<String> lines = prettyPrintAny(visited, object, ""); //$NON-NLS-1$
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append('\n');
        }
        return sb.toString();
    }

    private static List<String> prettyPrintAny(Set<EObject> visited, Object o, String indent) {
        List<String> lines = new ArrayList<>();
        if (o instanceof EObject) {
            EObject object = (EObject) o;
            visited.add(object);
            EClass eClass = object.eClass();
            lines.add(eClass.getName() + " [" + o.getClass().getCanonicalName() + "] {"); //$NON-NLS-1$ //$NON-NLS-2$
            lines.addAll(prettyPrintRecursive(visited, object, INDENT));
            lines.add("}"); //$NON-NLS-1$
        } else if (o instanceof Iterable) {
            lines.add("["); //$NON-NLS-1$
            for (Object obj : (Iterable<?>) o) {
                lines.addAll(prettyPrintAny(visited, obj, INDENT));
            }
            lines.add("]"); //$NON-NLS-1$
        } else {
            String line = String.valueOf(o) + ' ';
            if (o != null) {
                line += '[' + o.getClass().getCanonicalName() + ']';
            }
            lines.add(line);
        }
        return indentLines(lines, indent);
    }

    private static List<String> indentLines(List<String> lines, String indent) {
        List<String> result = new ArrayList<>();
        for (String l : lines) {
            result.add(indent + l);
        }
        return result;
    }

    private static List<String> prettyPrintRecursive(Set<EObject> visited, EObject o, String indent) {
        if (visited.contains(o)) {
            return ImmutableList.of();
        }
        EClass eClass = o.eClass();
        List<String> result = new ArrayList<>();
        for (EStructuralFeature feature : eClass.getEAllStructuralFeatures()) {
            Object value = o.eGet(feature);
            String line = feature.getName() + " = "; //$NON-NLS-1$
            List<String> list = prettyPrintAny(visited, value, INDENT);
            list.set(0, list.get(0).trim());
            result.add(line + list.get(0));
            list.remove(0);
            result.addAll(list);
        }

        return indentLines(result, indent);
    }
}
