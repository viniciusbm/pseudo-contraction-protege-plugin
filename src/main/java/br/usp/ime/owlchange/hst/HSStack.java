/*
 *    Copyright 2018-2019 OWL2DL-Change-Modularisation Developers
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package br.usp.ime.owlchange.hst;

import java.util.ArrayDeque;
import java.util.function.Supplier;

public class HSStack<ItemType> extends ArrayDeque<ItemType>
        implements HSDeque<ItemType> {

    private static final long serialVersionUID = -5689151895836125208L;

    @Override
    public void addNew(ItemType element) {
        addFirst(element);
    }

    @Override
    public ItemType removeNext() {
        return removeFirst();
    }

    @Override
    public Supplier<? extends HSDeque<ItemType>> supplier() {
        return HSStack::new;
    }
}
