# ACL-API-client-java
Client written in Java for Globo ACL API.


# Create Client
```java
ClientAclAPI clientAclAPI = ClientAclAPI.buildHttpAPI("https://example.com", "token_123");
```
or
```java
ClientAclAPI clientAclAPI = ClientAclAPI.buildHttpAPI("https://example.com", "user_tester", "password_123");
```

# Create Access Rule

```java
Rule rule = new Rule();
rule.setAction(Rule.Action.PERMIT);
rule.setProtocol(Rule.Protocol.TCP);
rule.setDestination("10.170.0.84/32");
rule.setSource("10.170.0.80/28");


L4Option option =new L4Option();
option.setDestPortStart(231);
option.setDestPortOperation("eq");
rule.setL4Options(option);
```

## Save
```java
RuleAPI ruleAPI = clientAclAPI.getAclAPI();
Rule.RuleSaveResponse result = envAPI.save(120l, 97l, rule);
Long ruleId = result.getFirstRuleId
Long jobId = result.getJobId()

clientAclAPI.getJobAPI().run(jobId);

```
or 
```java
RuleAPI ruleAPI = clientAclAPI.getAclAPI();
envAPI.saveSync(120l, 97l, rule);
```




