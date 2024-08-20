package com.ews.log.service.trainer;

import com.ews.log.data.LogVerifierDataRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

public class PojoGenerator {
    public static void main(String []args){
        new PojoGenerator().create(new LogVerifierDataRequest() );
    }

    public Object create(Object object){

        ObjectMapper objectMapper = new ObjectMapper();

        System.out.println(object.getClass().getSimpleName());
        PodamFactory factory = new PodamFactoryImpl();

        //RequestDto req = new RequestDto();
//		factory.manufacturePojo(RequestDto.class);
        Object req = factory.manufacturePojoWithFullData(object.getClass());
        try {
            String json = objectMapper.writeValueAsString(req);
            System.out.println(json);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return req;
    }
}
