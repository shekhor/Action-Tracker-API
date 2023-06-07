package com.tigerit.soa.loginsecurity.util;

import com.fasterxml.jackson.databind.ObjectMapper;
//import org.modelmapper.ModelMapper;
//import org.modelmapper.convention.MatchingStrategies;
import com.tigerit.soa.loginsecurity.models.PaginationParam;
import com.tigerit.soa.loginsecurity.models.response.RestApiResponse;
import com.tigerit.soa.loginsecurity.models.response.ajax.ErrorDetails;
import com.tigerit.soa.loginsecurity.models.response.ajax.SuccessDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
Fahim created at 4/9/2020
*/
public class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class);

    /*//for only temporary redis duplicate use
    public static List<String> redisTemplate=new ArrayList<>();
    public static void printBlackList(){
        int size=redisTemplate.size();
        for(int i=0;i<size;i++){
            logger.debug("Current content{}:"+redisTemplate.get(i),i);
        }
    }*/
/*
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
*/

    //for login as nid2
    public static List<String> methodAccessListToUniqueRoles(List<String> methodAccessList) {
        List<String> accessNameList = new ArrayList<>();
        for (String access : methodAccessList) {
            //String accessName = access.getAccessName();
            //accessNameList.add(accessName);
            accessNameList.add(access);
        }
        logger.debug("Size of accessNamelist : {}", accessNameList.size());
        Set<String> accessNameSet = new HashSet<>(accessNameList);
        logger.debug("Size fo accessNameset : {}", accessNameSet.size());
        return new ArrayList<>(accessNameSet);
    }

    public static <T> RestApiResponse<T> buildSuccessRestResponse(HttpStatus httpStatus, T klass) {
        return new RestApiResponse<>(httpStatus, new SuccessDetails<>(klass));
    }
    public static <T> RestApiResponse<T> buildErrorRestResponse(HttpStatus httpStatus, String filed, String message) {
        if (filed != null) {
            return new RestApiResponse<>(httpStatus, new ErrorDetails(filed, message));
        } else {
            return new RestApiResponse<>(httpStatus, new ErrorDetails(message));
        }
    }
    public static void createCustomResponse(HttpServletResponse response, RestApiResponse apiResponse) throws IOException {
        response.setContentType("application/json");
        response.setStatus(apiResponse.getStatus().value());
        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, apiResponse);
        out.flush();
    }

    public static PageRequest parsePaginationParam(PaginationParam param) {
        if (param != null) {
            int start;
            int length;
            Sort.Direction direction;
            String properties;

            if (param.getPageNo() < 0) {
                start = 0;
            } else {
                start = param.getPageNo();
            }

            if (param.getLength() <= 0) {
                length = 10;
            } else {
                length = param.getLength();
            }

            if (param.getSortDirection() != null && !param.getSortDirection().isEmpty()
                    && param.getSortDirection().equalsIgnoreCase(Sort.Direction.DESC.name())) {
                direction = Sort.Direction.DESC;
            } else {
                direction = Sort.Direction.ASC;
            }

            if (param.getSortProperties() != null && !param.getSortProperties().isEmpty()) {
                properties = param.getSortProperties();
            } else {
                properties = "id";
            }
            logger.debug("PaginationParam : {}, {}, {}, {}", start, length, direction, properties);

            return PageRequest.of(start, length, Sort.by(direction, properties));
        }
        return null;
    }
}
