package io.alkal.kalium.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated method with "On" will subscribe to events/messages and invoke the annotated method.
 *
 * @author Ziv Salzman
 * Created on 20-Jan-2019
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface On {
    
}
