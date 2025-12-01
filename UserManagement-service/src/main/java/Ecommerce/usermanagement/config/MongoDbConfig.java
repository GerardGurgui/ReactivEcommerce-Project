package Ecommerce.usermanagement.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;


import javax.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
public class MongoDbConfig {

    private final ApplicationContext applicationContext;

    @PostConstruct
    public void init() {

        MappingMongoConverter converter =
                applicationContext.getBean(MappingMongoConverter.class);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
    }
}
