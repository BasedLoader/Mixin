/*
 * This file is part of Mixin, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.asm.mixin.transformer;

import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.ClassWriter;

/**
 * ClassWriter which resolves common superclasses using Mixin's metadata instead
 * of calling Class.forName
 */
public class MixinClassWriter extends ClassWriter {

    private static final String JAVA_LANG_OBJECT = "java/lang/Object";

    public MixinClassWriter(int flags) {
        super(flags);
    }

    public MixinClassWriter(ClassReader classReader, int flags) {
        super(classReader, flags);
    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.ClassWriter#getCommonSuperClass(java.lang.String,
     *      java.lang.String)
     */
    @Override
    protected String getCommonSuperClass(final String type1, final String type2) {
        ClassInfo t1 = ClassInfo.forName(type1);
        ClassInfo t2 = ClassInfo.forName(type2);
        
        if (t1.hasSuperClass(t2)) {
            return type2;
        }
        if (t2.hasSuperClass(t1)) {
            return type1;
        }
        if (t1.isInterface() || t2.isInterface()) {
            return MixinClassWriter.JAVA_LANG_OBJECT;
        }
        
        do {
            t1 = t1.getSuperClass();
            if (t1 == null) {
                return MixinClassWriter.JAVA_LANG_OBJECT;
            }
        } while (!t2.hasSuperClass(t1));
        
        return t1.getName();
    }

}
