package com.globo.aclapi.client.model;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by lucas.castro on 10/23/15.
 */
public class RuleTest  {

    @Before
    public void setUp() {

    }

    @Test
    public void testEquals_l4() {
        Rule rule = new Rule();
        rule.setAction(Rule.Action.PERMIT);
        rule.setProtocol(Rule.Protocol.TCP);
        rule.setSource("10.0.0.0/32");
        rule.setDestination("10.0.0.2/28");

        L4Option l4Options = new L4Option();
        rule.setL4Options(l4Options);
        l4Options.setDestPortOperation("range");
        l4Options.setDestPortStart(80);
        l4Options.setDestPortEnd(90);

        l4Options.setSrcPortOperation("eq");
        l4Options.setSrcPortStart(999);


        Rule rule2 = new Rule();
        rule2.setAction(Rule.Action.PERMIT);
        rule2.setProtocol(Rule.Protocol.TCP);
        rule2.setSource("10.0.0.0/32");
        rule2.setDestination("10.0.0.2/28");

        L4Option l4Options2 = new L4Option();
        rule2.setL4Options(l4Options2);
        l4Options2.setDestPortOperation("range");
        l4Options2.setDestPortStart(80);
        l4Options2.setDestPortEnd(90);

        l4Options2.setSrcPortOperation("eq");
        l4Options2.setSrcPortStart(999);

        assertEquals(rule, rule2);
    }


    //
    @Test
    public void testEquals_with_id() {
        Rule rule = new Rule();
        rule.setId("23321");
        rule.setAction(Rule.Action.PERMIT);
        rule.setProtocol(Rule.Protocol.TCP);
        rule.setSource("10.0.0.0/32");
        rule.setDestination("10.0.0.2/28");

        L4Option l4Options = new L4Option();
        rule.setL4Options(l4Options);
        l4Options.setDestPortOperation("range");
        l4Options.setDestPortStart(80);
        l4Options.setDestPortEnd(90);

        l4Options.setSrcPortOperation("eq");
        l4Options.setSrcPortStart(999);


        Rule rule2 = new Rule();
        rule2.setAction(Rule.Action.PERMIT);
        rule2.setProtocol(Rule.Protocol.TCP);
        rule2.setSource("10.0.0.0/32");
        rule2.setDestination("10.0.0.2/28");

        L4Option l4Options2 = new L4Option();
        rule2.setL4Options(l4Options2);
        l4Options2.setDestPortOperation("range");
        l4Options2.setDestPortStart(80);
        l4Options2.setDestPortEnd(90);

        l4Options2.setSrcPortOperation("eq");
        l4Options2.setSrcPortStart(999);

        assertEquals(rule, rule2);
    }

    @Test
    public void testEquals_icmp() {
        Rule rule = new Rule();
        rule.setAction(Rule.Action.PERMIT);
        rule.setProtocol(Rule.Protocol.TCP);
        rule.setSource("10.0.0.0/32");
        rule.setDestination("10.0.0.2/28");

        ICMPOption icmpOption = new ICMPOption(8, 10);
        rule.setIcmpOptions(icmpOption);

        Rule rule2 = new Rule();
        rule2.setAction(Rule.Action.PERMIT);
        rule2.setProtocol(Rule.Protocol.TCP);
        rule2.setSource("10.0.0.0/32");
        rule2.setDestination("10.0.0.2/28");

        ICMPOption icmpOption2 = new ICMPOption(8, 10);
        rule2.setIcmpOptions(icmpOption2);

        assertEquals(rule, rule2);
    }

    @Test
    public void testEquals_icmp_fail() {
        Rule rule = new Rule();
        rule.setAction(Rule.Action.PERMIT);
        rule.setProtocol(Rule.Protocol.TCP);
        rule.setSource("10.0.0.0/32");
        rule.setDestination("10.0.0.2/28");

        ICMPOption icmpOption = new ICMPOption(7, 10);
        rule.setIcmpOptions(icmpOption);

        Rule rule2 = new Rule();
        rule2.setAction(Rule.Action.PERMIT);
        rule2.setProtocol(Rule.Protocol.TCP);
        rule2.setSource("10.0.0.0/32");
        rule2.setDestination("10.0.0.2/28");

        ICMPOption icmpOption2 = new ICMPOption(8, 11);
        rule2.setIcmpOptions(icmpOption2);

        assertNotEquals(rule, rule2);
    }

    @Test
    public void testEquals_fail() {
        Rule rule = new Rule();
        rule.setAction(Rule.Action.PERMIT);
        rule.setProtocol(Rule.Protocol.TCP);
        rule.setSource("10.0.0.0/32");
        rule.setDestination("10.0.0.2/28");


        Rule rule2 = new Rule();
        rule2.setAction(Rule.Action.PERMIT);
        rule2.setProtocol(Rule.Protocol.UDP);
        rule2.setSource("10.0.0.0/32");
        rule2.setDestination("10.0.0.2/28");

        assertNotEquals(rule, rule2);

        rule2 = new Rule();
        rule2.setAction(Rule.Action.PERMIT);
        rule2.setProtocol(Rule.Protocol.TCP);
        rule2.setSource("10.0.0.0/32");
        rule2.setDestination("10.0.0.3/28");

        assertNotEquals(rule, rule2);


        rule2 = new Rule();
        rule2.setAction(Rule.Action.PERMIT);
        rule2.setProtocol(Rule.Protocol.TCP);
        rule2.setSource("10.0.0.0/32");
        rule2.setDestination("10.0.0.2/28");
        rule2.setIcmpOptions(new ICMPOption(8,10));

        assertNotEquals(rule, rule2);

    }

}