package org.roly.personalaccountant.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.AsyncTaskExecutor;

@SpringBootTest(properties = "spring.thread-executor=virtual")
class ThreadConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void shouldLoadThreadConfigBeans() {
        assertThat(context.getBean(AsyncTaskExecutor.class)).isNotNull();
        assertThat(context.getBean(TomcatProtocolHandlerCustomizer.class)).isNotNull();
    }
}