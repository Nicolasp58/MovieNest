package com.example.demo.config;

import com.example.demo.util.CurrencyFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring6.ISpringTemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

import java.util.Collections;
import java.util.Set;

@Configuration
public class ThymeleafConfig {

    private final CurrencyFormatter currencyFormatter;
    
    public ThymeleafConfig(CurrencyFormatter currencyFormatter) {
        this.currencyFormatter = currencyFormatter;
    }

    @Bean
    public ISpringTemplateEngine templateEngine(ITemplateResolver templateResolver) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.addDialect(new Java8TimeDialect());
        engine.addDialect(new CurrencyDialect(currencyFormatter));
        engine.setTemplateResolver(templateResolver);
        return engine;
    }
    
    public class CurrencyDialect extends AbstractDialect implements IExpressionObjectDialect {
        
        private final CurrencyFormatter currencyFormatter;
        
        public CurrencyDialect(CurrencyFormatter currencyFormatter) {
            super("currencyDialect");
            this.currencyFormatter = currencyFormatter;
        }
        
        @Override
        public IExpressionObjectFactory getExpressionObjectFactory() {
            return new IExpressionObjectFactory() {
                @Override
                public Set<String> getAllExpressionObjectNames() {
                    return Collections.singleton("currencyUtil");
                }
                
                @Override
                public Object buildObject(IExpressionContext context, String expressionObjectName) {
                    return currencyFormatter;
                }
                
                @Override
                public boolean isCacheable(String expressionObjectName) {
                    return true;
                }
            };
        }
    }
}