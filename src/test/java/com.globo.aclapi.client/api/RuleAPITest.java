package com.globo.aclapi.client.api;

import com.globo.aclapi.client.AbstractAPI;
import com.globo.aclapi.client.TestUtil;
import com.globo.aclapi.client.model.L4Option;
import com.globo.aclapi.client.model.Rule;
import java.util.List;
import junit.framework.TestCase;

public class RuleAPITest extends TestCase {


    public void testListACLEnv() throws Exception {
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
}