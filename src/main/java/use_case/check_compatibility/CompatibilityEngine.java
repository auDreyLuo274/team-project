package use_case.check_compatibility;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import entity.AdopterPreferences;
import entity.Pet;

/**
 * Runs every compatibility rule on a cat. To check something new, add a rule; this class does not change.
 */
public class CompatibilityEngine {
    private final List<CompatibilityRule> rules;

    /**
     * Creates the engine.
     *
     * @param rules the rules to run, in the order their results should be shown
     */
    public CompatibilityEngine(List<CompatibilityRule> rules) {
        this.rules = List.copyOf(rules);
    }

    /**
     * Creates the engine with every rule that is registered in
     * {@code META-INF/services/use_case.check_compatibility.CompatibilityRule}. To add a rule, write a new
     * class that implements {@link CompatibilityRule} (with a public constructor that takes nothing) and add its
     * name to that file. No existing class is changed.
     *
     * @return an engine with all registered rules, in the order they are listed
     */
    public static CompatibilityEngine loadRegisteredRules() {
        final List<CompatibilityRule> found = new ArrayList<>();
        for (CompatibilityRule rule : ServiceLoader.load(CompatibilityRule.class)) {
            found.add(rule);
        }
        return new CompatibilityEngine(found);
    }

    /**
     * Checks one cat against what the adopter said about their home.
     *
     * @param pet the cat being viewed
     * @param preferences what the adopter saved
     * @return one result per rule, in rule order
     */
    public List<RuleResult> evaluate(Pet pet, AdopterPreferences preferences) {
        final List<RuleResult> results = new ArrayList<>();
        for (CompatibilityRule rule : rules) {
            results.add(rule.evaluate(pet, preferences));
        }
        return results;
    }
}
