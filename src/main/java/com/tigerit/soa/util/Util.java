package com.tigerit.soa.util;

import com.tigerit.soa.response.ErrorModel;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.response.ServiceResponseExtended;
import com.tigerit.soa.response.StatusCode;
import javafx.scene.control.Pagination;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/*
Fahim created at 4/9/2020
*/
public class Util {

    private static ModelMapper modelMapper = new ModelMapper();

    public static <U, V> V convertClass(U mapperObject, Class<V> targetClass) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(mapperObject, targetClass);
    }

    public static <Source, Dest> void copyProperty(Source source, Dest target) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.map(source, target);
    }

    public static <U, V> List<V> toDtoList(List<U> mapperObjects, Class<V> targetClass) {
        List<V> dtoObjects = mapperObjects
                .stream()
                .map(u -> convertClass(u, targetClass))
                .collect(Collectors.toList());

        return dtoObjects;
    }

    public static boolean isEmpty(String str)
    {
        if (str==null)
            return true;

        str=str.trim();
        if(str.isEmpty())
            return true;

        return false;

    }

    public static String toDateStringFromDate(Date date, DateFormat dateFormat) {
        if (date == null) return null;
        try {
            SimpleDateFormat fmt = new SimpleDateFormat(dateFormat.getValue());
            return fmt.format(date);
        } catch (Exception ex) {
        }
        return null;
    }

    public static String toDateStringFromLocalDate(LocalDate date, DateFormat dateFormat) {
        if (date == null) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat.toString());
            return date.format(formatter);
        } catch (Exception ex) {
            return null;
        }
    }

    public enum DateFormat {
        DD_MM_YYYY_SLASH("dd/MM/yyyy"),
        MM_DD_YYYY_SLASH("MM/dd/yyyy"),
        YYYY_MM_DD_SLASH("yyyy/MM/dd"),
        YYYY_MM_DDTHH_MM_SS_SSS_DASH("yyyy-MM-dd'T'HH:mm:ss.SSS");

        private String value;

        DateFormat(String s) {
            this.value = s;
        }

        public String getValue() {
            return value;
        }
    }

    public static <U, V> List<V> toDtoList(Iterable<U> mapperObjects, Class<V> targetClass) {
        List<V> dtoObjects = new ArrayList<>();

        mapperObjects.forEach(object -> {
            dtoObjects.add(convertClass(object, targetClass));
        });

        return dtoObjects;
    }

    public static boolean isEqualIgnoredCaseStr(String str1, String str2)
    {
        if(str1==null && str2==null)
            return  true;
        return str1.equalsIgnoreCase(str2);
    }

    public static boolean isEqual(Long v1, Long v2)
    {
        if(v1==null && v2==null)
            return  true;
        return v1 == v2;
    }

    public static Date getStartOfDay(Date date) {
        Long time = date.getTime();
        return new Date(time - time % (24 * 60 * 60 * 1000));
    }

    public static Date getEndOfDay(Date date) {
        return new Date(date.getTime() + 24 * 60 * 60 * 1000);
    }

    public static String[] toArray(List<String> strings) {
        if (strings == null || strings.size() == 0) {
            return new String[0];
        }
        return strings.stream().toArray(String[]::new);
    }

    public static ServiceResponse requestErrorHandler(BindingResult bindingResult) {
        ServiceResponse response =new ServiceResponse();
        List<ErrorModel> errorModelList = new ArrayList();

        for(FieldError fieldError : bindingResult.getFieldErrors()) {
            ErrorModel errorModel = new ErrorModel();
            errorModel.setField(fieldError.getField());
            errorModel.setMessage(fieldError.getDefaultMessage());
            errorModel.setDescription(fieldError.getObjectName());
            errorModelList.add(errorModel);
        }

        response.setErrorList(errorModelList);
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setStatusCode(StatusCode.ERROR);
        return response;
    }


    public static boolean isValidProjOperation()
    {
        //TODO: need to design this feature,
        return true;
    }

  /*  public static <U, V> ServiceResponseExtended<V> pageToServiceResponseExtended(Page<U> pageObject) {

        if (pageObject == null) {
            return null;
        }

        return new ServiceResponseExtended<>(pageObject.getContent(),pageObject.getNumber(), pageObject.getSize(),
                pageObject.getTotalElements());
    }*/

}
