package org.apache.flink.cep.discover;


import java.io.Closeable;

/**
 * 
 */
public interface RuleDiscoverer extends Closeable {
    void discoverRuleUpdates(RuleManager ruleManager);
}
