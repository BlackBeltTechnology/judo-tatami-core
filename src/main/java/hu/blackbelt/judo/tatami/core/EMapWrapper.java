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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.emf.common.util.EMap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class EMapWrapper<K,V> implements Map<K, V> {

    @NonNull
    EMap<K, V> delegatee;

    @Override
    public int size() {
        return delegatee.size();
    }

    @Override
    public boolean isEmpty() {
        return delegatee.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegatee.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegatee.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return delegatee.get(key);
    }

    @Override
    public V put(K key, V value) {
        return delegatee.put(key, value);
    }

    @Override
    public V remove(Object key) {
        V value = delegatee.get(key);
        delegatee.remove(key);
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        delegatee.putAll(m);
    }

    @Override
    public void clear() {
        delegatee.clear();
    }

    @Override
    public Set<K> keySet() {
        return delegatee.keySet();
    }

    @Override
    public Collection<V> values() {
        return delegatee.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return delegatee.entrySet();
    }
}
