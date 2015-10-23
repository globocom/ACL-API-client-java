package com.globo.aclapi.client.api;

import com.globo.aclapi.client.AbstractAPI;
import com.globo.aclapi.client.MockGloboACL;
import com.globo.aclapi.client.TestUtil;
import com.globo.aclapi.client.model.ICMPOption;
import com.globo.aclapi.client.model.L4Option;
import com.globo.aclapi.client.model.Rule;
import java.io.IOException;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

public class RuleAPITest extends TestCase {

    MockGloboACL globoAcl;
    private RuleAPI ruleAPI;


    @Before
    public void setUp(){
        this.globoAcl = new MockGloboACL("token_123");
        this.ruleAPI = this.globoAcl.getAclAPI();
    }

    @Test
    public void testListByEnv()  {
        String result = TestUtil.getSample("rule_list_by_env.json");
        this.globoAcl.registerFakeRequest(MockGloboACL.HttpMethod.GET, "/api/ipv4/acl/" + 123l, result);

        List<Rule> rules = this.ruleAPI.listByEnv(123l);
        assertEquals(4, rules.size());
    }


    @Test
    public void testListACLEnv() throws IOException {
        String result = TestUtil.getSample("rule_list_by_env.json");

        Rule.RuleResponse response = AbstractAPI.parse(result, Rule.RuleResponse.class);

        List<Rule> rules = response.getRules();
        assertEquals(4, rules.size());

        //rule 0
        Rule rule = rules.get(0);
        assertEquals("1", rule.getId());
        assertEquals(Rule.Action.PERMIT, rule.getAction());
        assertEquals(Rule.Protocol.TCP, rule.getProtocol());
        assertEquals("0.0.0.0/0", rule.getSource());
        assertEquals("10.70.1.80/32", rule.getDestination());

        L4Option l4Options = rule.getL4Options();
        assertEquals((Integer)636, l4Options.getDestPortStart());
        assertEquals("eq", l4Options.getDestPortOperation());
        assertNull(l4Options.getSrcPortOperation());
        assertNull(l4Options.getSrcPortStart());

        //rule 1
        rule = rules.get(1);
        assertEquals("2", rule.getId());
        assertEquals(Rule.Action.PERMIT, rule.getAction());
        assertEquals(Rule.Protocol.UDP, rule.getProtocol());
        assertEquals("200.0.0.0/0", rule.getSource());
        assertEquals("224.0.0.102/32", rule.getDestination());

        l4Options = rule.getL4Options();
        assertEquals((Integer)1985, l4Options.getDestPortStart());
        assertEquals("eq", l4Options.getDestPortOperation());
        assertEquals((Integer)1111, l4Options.getSrcPortStart());
        assertEquals("eq", l4Options.getSrcPortOperation());


        //rule 2
        rule = rules.get(2);
        assertEquals("3", rule.getId());
        assertEquals(Rule.Action.PERMIT, rule.getAction());
        assertEquals(Rule.Protocol.IP, rule.getProtocol());
        assertEquals("0.0.0.0/0", rule.getSource());
        assertEquals("10.10.5.0/24", rule.getDestination());

    }

    @Test
    public void testSaveByEnv() {
        String result = TestUtil.getSample("rule_saveByEnv_response.json");
        this.globoAcl.registerFakeRequest(MockGloboACL.HttpMethod.PUT, "/api/ipv4/acl/" + 123l, result);

        Rule rule = new Rule();

        Rule.RuleSaveResponse response = this.ruleAPI.save(123l, rule);

        assertEquals((Long)1242l, response.getFirstRuleId());
        assertEquals((Long)373l, response.getJobId());

    }

    @Test
    public void testSaveByEnvAndNumVlan() {
        String result = TestUtil.getSample("rule_saveByEnvNumVlan_response.json");
        this.globoAcl.registerFakeRequest(MockGloboACL.HttpMethod.PUT, "/api/ipv4/acl/" + 123l + "/" + 97, result);

        Rule rule = new Rule();

        Rule.RuleSaveResponse response = this.ruleAPI.save(123l, 97l, rule);

        assertEquals((Long)1241l, response.getFirstRuleId());
        assertEquals((Long)372l, response.getJobId());
    }

    @Test
    public void testListByEnvAndNumVlan() {
        String result = TestUtil.getSample("rule_listByEnvAndNumVlan_ok_response.json");
        this.globoAcl.registerFakeRequest(MockGloboACL.HttpMethod.GET, "/api/ipv4/acl/" + 123l + "/" + 97, result);

        List<Rule> list = this.ruleAPI.listByEnvAndNumVlan(123l, 97l);

        assertEquals(3, list.size());

        Rule rule = list.get(0);
        assertEquals(Rule.Protocol.TCP, rule.getProtocol());
        assertEquals("10.170.0.80/28", rule.getSource());
        assertEquals("10.170.0.86/32", rule.getDestination());


        rule = list.get(1);
        assertEquals(Rule.Protocol.UDP, rule.getProtocol());
        assertEquals("10.170.0.80/28", rule.getSource());
        assertEquals("10.170.0.83/32", rule.getDestination());

        rule = list.get(2);
        assertEquals(Rule.Protocol.ICMP, rule.getProtocol());
        assertEquals("10.170.0.80/28", rule.getSource());
        assertEquals("10.170.0.83/32", rule.getDestination());
        ICMPOption icmpOptions = rule.getIcmpOptions();
        assertEquals((Integer)8, icmpOptions.getType());
        assertEquals((Integer)3, icmpOptions.getCode());
    }


}