//package com.tigerit.soa.loginsecurity.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.LocaleResolver;
//import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
//import org.springframework.web.servlet.i18n.SessionLocaleResolver;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Locale;
//
///**
// *
// */
//@Configuration
//public class ActrLocaleResolver extends AcceptHeaderLocaleResolver {
//    private List<Locale> LOCALES = Arrays.asList(
//            new Locale("en"),
//            new Locale("bn"));
//
//    @Override
//    public Locale resolveLocale(HttpServletRequest request) {
//        String headerLang = request.getHeader("Accept-Language");
//        return headerLang == null || headerLang.isEmpty()
//                ? Locale.getDefault()
//                : Locale.lookup(Locale.LanguageRange.parse(headerLang), LOCALES);
//    }
//
//    @Bean
//    public LocaleResolver localeResolver() {
//        SessionLocaleResolver localResolver = new SessionLocaleResolver();
//        localResolver.setDefaultLocale(Locale.US);
//        //localResolver.setDefaultLocale(new Locale("en"));
//        return localResolver;
//    }
//}
