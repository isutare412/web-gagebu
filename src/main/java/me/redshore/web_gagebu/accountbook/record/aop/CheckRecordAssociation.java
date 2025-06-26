package me.redshore.web_gagebu.accountbook.record.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckRecordAssociation {

    String accountBookIdKey();

    String recordIdKey();

}
