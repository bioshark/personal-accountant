# Feature Ideas

## Bugs
- Creating new month, it starts on start date + 1. Which is not correct.
- Adding income on the first day of the series, which is tomorrow, resulted in the error:
- 2026-06-25T20:10:24.190+02:00 ERROR 136649 --- [personal-accountant] [mcat-handler-15] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed: org.thymeleaf.exceptions.TemplateProcessingException: Exception evaluating SpringEL expression: "expense.expenseName" (template: "detail" - line 6, col 10)] with root cause

org.springframework.expression.spel.SpelEvaluationException: EL1007E: Property or field 'expenseName' cannot be found on null
at org.springframework.expression.spel.ast.PropertyOrFieldReference.readProperty(PropertyOrFieldReference.java:225) ~[spring-expression-6.2.7.jar!/:6.2.7]
at org.springframework.expression.spel.ast.PropertyOrFieldReference.getValueInternal(PropertyOrFieldReference.java:112) ~[spring-expression-6.2.7.jar!/:6.2.7]
at org.springframework.expression.spel.ast.PropertyOrFieldReference$AccessorValueRef.getValue(PropertyOrFieldReference.java:376) ~[spring-expression-6.2.7.jar!/:6.2.7]
at org.springframework.expression.spel.ast.CompoundExpression.getValueInternal(CompoundExpression.java:97) ~[spring-expression-6.2.7.jar!/:6.2.7]
at org.springframework.expression.spel.ast.SpelNodeImpl.getValue(SpelNodeImpl.java:116) ~[spring-expression-6.2.7.jar!/:6.2.7]
at org.springframework.expression.spel.standard.SpelExpression.getValue(SpelExpression.java:338) ~[spring-expression-6.2.7.jar!/:6.2.7]
at org.thymeleaf.spring6.expression.SPELVariableExpressionEvaluator.evaluate(SPELVariableExpressionEvaluator.java:265) ~[thymeleaf-spring6-3.1.3.RELEASE.jar!/:3.1.3.RELEASE]
at org.thymeleaf.standard.expression.VariableExpression.executeVariableExpression(VariableExpression.java:166) ~[thymeleaf-3.1.3.RELEASE.jar!/:3.1.3.RELEASE]
at org.thymeleaf.standard.expression.SimpleExpression.executeSimple(SimpleExpression.java:66) ~[thymeleaf-3.1.3.RELEASE.jar!/:3.1.3.RELEASE]
at org.thymeleaf.standard.expression.Expression.execute(Expression.java:109) ~[thymeleaf-3.1.3.RELEASE.jar!/:3.1.3.RELEASE]
at org.thymeleaf.standard.expression.Expression.execute(Expression.java:138) ~[thymeleaf-3.1.3.RELEASE.jar!/:3.1.3.RELEASE]
at org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor.doProcess(AbstractStandardExpressionAttributeTagProcessor.java:144) ~[thymeleaf-3.1.3.RELEASE.jar!/:3.1.3.RELEASE]
at org.thymeleaf.processor.element.AbstractAttributeTagProcessor.doProcess(AbstractAttributeTagProcessor.java:74) ~[thymeleaf-3.1.3.RELEASE.jar!/:3.1.3.RELEASE]
at org.thymeleaf.processor.element.AbstractElementTagProcessor.process(AbstractElementTagProcessor.java:95) ~[thymeleaf-3.1.3.RELEASE.jar!/:3.1.3.RELEASE]
at org.thymeleaf.util.ProcessorConfigurationUtils$ElementTagProcessorWrapper.process(ProcessorConfigurationUtils.java:633) ~[thymeleaf-3.1.3.RELEASE.jar!/:3.1.3.RELEASE]
at org.thymeleaf.engine.ProcessorTemplateHandler.handleOpenElement(ProcessorTemplateHandler.java:1314) ~[thymeleaf-3.1.3.RELEASE.jar!/:3.1.3.RELEASE]
at org.thymeleaf.engine.OpenElementTag.beHandled(OpenElementTag.java:205) ~[thymeleaf-3.1.3.RELEASE.jar!/:3.1.3.RELEASE]
at org.thymeleaf.engine.TemplateModel.process(TemplateModel.java:136) ~[thymeleaf-3.1.3.RELEASE.jar!/:3.1.3.RELEASE]
at org.thymeleaf.engine.TemplateManager.parseAndProcess(TemplateManager.java:592) ~[thymeleaf-3.1.3.RELEASE.jar!/:3.1.3.RELEASE]
at org.thymeleaf.TemplateEngine.process(TemplateEngine.java:1103) ~[thymeleaf-3.1.3.RELEASE.jar!/:3.1.3.RELEASE]
at org.thymeleaf.TemplateEngine.process(TemplateEngine.java:1077) ~[thymeleaf-3.1.3.RELEASE.jar!/:3.1.3.RELEASE]
at org.thymeleaf.spring6.view.ThymeleafView.renderFragment(ThymeleafView.java:372) ~[thymeleaf-spring6-3.1.3.RELEASE.jar!/:3.1.3.RELEASE]
at org.thymeleaf.spring6.view.ThymeleafView.render(ThymeleafView.java:192) ~[thymeleaf-spring6-3.1.3.RELEASE.jar!/:3.1.3.RELEASE]
at org.springframework.web.servlet.DispatcherServlet.render(DispatcherServlet.java:1437) ~[spring-webmvc-6.2.7.jar!/:6.2.7]
at org.springframework.web.servlet.DispatcherServlet.processDispatchResult(DispatcherServlet.java:1168) ~[spring-webmvc-6.2.7.jar!/:6.2.7]
at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1106) ~[spring-webmvc-6.2.7.jar!/:6.2.7]
at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979) ~[spring-webmvc-6.2.7.jar!/:6.2.7]
at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014) ~[spring-webmvc-6.2.7.jar!/:6.2.7]
at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:903) ~[spring-webmvc-6.2.7.jar!/:6.2.7]
at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:564) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885) ~[spring-webmvc-6.2.7.jar!/:6.2.7]
at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51) ~[tomcat-embed-websocket-10.1.41.jar!/:na]
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) ~[spring-web-6.2.7.jar!/:6.2.7]
at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.2.7.jar!/:6.2.7]
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93) ~[spring-web-6.2.7.jar!/:6.2.7]
at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.2.7.jar!/:6.2.7]
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201) ~[spring-web-6.2.7.jar!/:6.2.7]
at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.2.7.jar!/:6.2.7]
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:116) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:398) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:903) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1740) ~[tomcat-embed-core-10.1.41.jar!/:na]
at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52) ~[tomcat-embed-core-10.1.41.jar!/:na]
at java.base/java.lang.VirtualThread.run(VirtualThread.java:460) ~[na:na]

## General stuff

## Month
- Edit start/end
- what happens with the days "deleted" if the end is shortened?
- 

## Income

- When adding new income, auto-fill the date with the first day of the period

## Recurring Transactions

- Auto-generate fixed payments each month (rent, subscriptions, utilities)
- Define once, applied to each new expense period

## Category Budgets

- Set monthly limits per category (e.g. max €200 on food)
- Progress bars or warnings when approaching limit

## Multi-Month Views & Charts

- Spending trends over time by category
- Income growth visualization
- Month-over-month comparison

## Bank Import

- CSV/OFX file import to avoid manual entry
- Column mapping configuration

## Tags & Notes

- Flexible labeling beyond fixed categories
- Free-text notes on payments

## Reports & Export

- PDF/CSV export for tax or personal review
- Monthly/yearly summary reports

## Search

- Find a specific payment across all months
- Filter by description, category, amount range, date range

## DB Management (UI)

- Load/switch database files from the main page
- Backup/restore functionality
- Export full data as JSON

## Notes

#Release

./mvnw release:prepare release:perform

This will:

1. Prompt for the release version (e.g. 0.0.1) and next dev version (e.g. 0.0.2-SNAPSHOT)
2. Remove -SNAPSHOT, commit, create git tag v0.0.1
3. Bump to next snapshot, commit
4. Push both commits and the tag to origin

If you want to skip the prompts and set versions non-interactively:

./mvnw release:prepare -DreleaseVersion=1.0.0 -DdevelopmentVersion=1.0.1-SNAPSHOT